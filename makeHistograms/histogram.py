import os
import json
import pandas as pd
import matplotlib.pyplot as plt
import re
import numpy as np  # Import de numpy pour cos et sin
from matplotlib.ticker import FixedLocator
from matplotlib.colors import hex2color  # Importez depuis matplotlib.colors

# Dictionnaire de couleurs pour chaque catégorie
category_colors = {'Subject': 'yellow', 'Predicate': 'fuchsia', 'Object': 'orange'}

# Fonction pour traiter les chaînes
def process_string(s):
    if re.match(r"^\d{4}-", s):
        s = s[5:]  # Supprime le préfixe NNNN-
    return s.upper()  # Convertit en majuscules

# Chemin vers le dossier "results"
results_dir = r'D:\var\www\profilingForAgroportal\results'

        #############################################################
        # Récupération des acronymes pour les ontologies d'AgroPortal
        #############################################################

# Chemin vers le fichier JSON
file_path = results_dir + "\listUriAndId.json"

# Charger le fichier JSON
with open(file_path, "r", encoding="utf-8") as f:
    data = json.load(f)

# Afficher le contenu
# for item in data:
#     print(f"URI: {item['uri']}, STR: {item['str']}")
    
# Exemple de traitement : Grouper par URI
from collections import defaultdict

uri_groups = defaultdict(list)
for item in data:
    uri_groups[item['uri']].append(item['str'])

# # Afficher les groupes
# for uri, strs in uri_groups.items():
#     print(f"URI: {uri}")
#     print(f"Associations STR: {strs}")
#     print("-" * 20)
        #############################################################

# Dictionnaire pour mapper le nom de fichier à une catégorie
category_mapping = {
    'listNsSubjectKnowledgeRepresentationVocabularies.json': 'Subject',
    'listNsPredicateKnowledgeRepresentationVocabularies.json': 'Predicate',
    'listNsObjectKnowledgeRepresentationVocabularies.json': 'Object',
    'listNsSubjectReusedVocabularies.json': 'Subject',
    'listNsPredicateReusedVocabularies.json': 'Predicate',
    'listNsObjectReusedVocabularies.json': 'Object',
    'listNsSubjectOtherVocabularies.json': 'Subject',
    'listNsPredicateOtherVocabularies.json': 'Predicate',
    'listNsObjectOtherVocabularies.json': 'Object'
}

# Ordre de la légende souhaité
legend_order = ['Subject', 'Predicate', 'Object']

# Boucle à travers chaque sous-répertoire dans "results"
for subdir in os.listdir(results_dir):
    subdir_path = os.path.join(results_dir, subdir)
    
    # Vérifie si c'est un dossier
    if os.path.isdir(subdir_path):
        
        ###############################################
        # Histogramme pour les vocabulaires structuraux
        ###############################################
        
        json_files_structural = [
            'listNsSubjectKnowledgeRepresentationVocabularies.json',
            'listNsPredicateKnowledgeRepresentationVocabularies.json',
            'listNsObjectKnowledgeRepresentationVocabularies.json'
        ]

        all_data_structural = []
        
        for json_file_structural in json_files_structural:
            json_file_path = os.path.join(subdir_path, json_file_structural)
            if os.path.exists(json_file_path):
                with open(json_file_path, 'r', encoding='utf-8') as file:
                    data = json.load(file)
                    for entry in data:
                        entry['category'] = category_mapping[json_file_structural]
                    all_data_structural.extend(data)

        df_structural = pd.DataFrame(all_data_structural)
        vocab_order = df_structural['str'].unique()
        
        pivot_df = df_structural.pivot_table(index='str', columns='category', values='number', fill_value=0)
        
        # Ajouter des colonnes manquantes avec des zéros pour les catégories absentes
        for category in legend_order:
            if category not in pivot_df.columns:
                pivot_df[category] = 0
                
        pivot_df = pivot_df.reindex(vocab_order)

        fig_structural, ax = plt.subplots()
        
        pivot_df[legend_order].plot(
            kind='bar', 
            stacked=True, 
            ax=ax, 
            color=[category_colors[cat] for cat in legend_order]
        )
        
        ax.set_title(f"Use of structural vocabularies for the {process_string(subdir)} vocabulary")
        ax.set_xlabel("Vocabulary")
        ax.set_ylabel("Usage (number)")
        ax.tick_params(axis='y', which='both', left=False)
        plt.xticks(rotation=0)
        
        handles, labels = ax.get_legend_handles_labels()
        ordered_handles = [handles[labels.index(cat)] for cat in legend_order if cat in labels]
        ax.legend(ordered_handles, legend_order, title="")

        output_path = os.path.join(subdir_path, f"histogram_structural_{subdir}.png")
        plt.savefig(output_path)
        plt.close(fig_structural)
        
        ##################################################
        # Histogramme pour les vocabulaires de métadonnées
        ##################################################
        
        json_files_reused = [
            'listNsSubjectReusedVocabularies.json',
            'listNsPredicateReusedVocabularies.json',
            'listNsObjectReusedVocabularies.json'
        ]

        all_data_reused = []

        for json_file_reused in json_files_reused:
            json_file_path = os.path.join(subdir_path, json_file_reused)
            if os.path.exists(json_file_path):
                with open(json_file_path, 'r', encoding='utf-8') as file:
                    data = json.load(file)
                    for entry in data:
                        entry['category'] = category_mapping[json_file_reused]
                    all_data_reused.extend(data)

        df_reused = pd.DataFrame(all_data_reused)
        
        total_number = df_reused['number'].sum()
        seuil_1_pct = total_number * 0.01
        df_reused_filtered = df_reused[df_reused['number'] >= seuil_1_pct]
        vocab_order = df_reused_filtered['str'].unique()

        pivot_df = df_reused_filtered.pivot_table(index='str', columns='category', values='number', fill_value=0)
        
        for category in legend_order:
            if category not in pivot_df.columns:
                pivot_df[category] = 0

        pivot_df = pivot_df.reindex(vocab_order)

        fig_reused, ax = plt.subplots()

        pivot_df[legend_order].plot(
            kind='barh', 
            stacked=True, 
            ax=ax, 
            color=[category_colors[cat] for cat in legend_order]
        )

        ax.set_title(f"Use of metadata vocabularies for the {process_string(subdir)} vocabulary")
        ax.set_ylabel("Vocabulary")
        ax.set_xlabel("Usage (number)")
        #plt.subplots_adjust(left=0.3)
        plt.xticks(rotation=0)

        handles, labels = ax.get_legend_handles_labels()
        ordered_handles = [handles[labels.index(cat)] for cat in legend_order if cat in labels]
        ax.legend(ordered_handles, legend_order, title="")

        output_path = os.path.join(subdir_path, f"histogram_metadata_{subdir}.png")
        plt.savefig(output_path)
        plt.close(fig_reused)
        
        ##################################################
        # Histogramme pour les vocabulaires sémantiques
        ##################################################

        json_files_other = [
            'listNsSubjectOtherVocabularies.json',
            'listNsPredicateOtherVocabularies.json',
            'listNsObjectOtherVocabularies.json'
        ]

        all_data_other = []

        # Chargement des données JSON et ajout de la catégorie appropriée
        for json_file_other in json_files_other:
            json_file_path = os.path.join(subdir_path, json_file_other)
            if os.path.exists(json_file_path):
                with open(json_file_path, 'r', encoding='utf-8') as file:
                    data = json.load(file)
                    for entry in data:
                        entry['category'] = category_mapping[json_file_other]
                    all_data_other.extend(data)

        # Création du DataFrame
        df_other = pd.DataFrame(all_data_other)

        ####################
        # Pour les 1%
        ####################
        
        # Calculer la somme des `number` pour chaque `uri` pour déterminer la répartition globale
        uri_totals = df_other.groupby('uri')['number'].sum()

        # Calculer la répartition en S, P, O pour chaque `uri`
        category_breakdown = df_other.pivot_table(index='uri', columns='category', values='number', aggfunc='sum', fill_value=0)

        # Calcul du total général et du seuil de 1%
        total_usage = category_breakdown.sum().sum()
        seuil_1_pct = total_usage * 0.01

        # Filtrer les URI dont la somme est inférieure au seuil de 1%
        category_breakdown['total'] = category_breakdown.sum(axis=1)
        category_breakdown_filtered = category_breakdown[category_breakdown['total'] >= seuil_1_pct].drop(columns='total')

        ####################

        # Ajouter les colonnes manquantes si elles n'existent pas dans le DataFrame
        for col in ['Subject', 'Predicate', 'Object']:
            if col not in category_breakdown_filtered.columns:
                category_breakdown_filtered[col] = 0

        # Réorganiser les colonnes pour l'ordre souhaité
        category_breakdown_filtered = category_breakdown_filtered[['Subject', 'Predicate', 'Object']]

        # Conversion en tableau numpy
        vals = category_breakdown_filtered.to_numpy()

        #print("df_other : ", df_other) 
        #print("vals:\n", vals)

        # Générer les valeurs et les labels
        values = category_breakdown_filtered.sum(axis=1)
        labels = list(category_breakdown_filtered.index)

        #print("values : ", values) 
        #print("labels : ", labels) 


        fig, ax = plt.subplots(subplot_kw=dict(projection="polar"), figsize=(12, 10))

        size = 0.33
        # Normalize vals to 2 pi
        valsnorm = vals/np.sum(vals)*2*np.pi
        # Obtain the ordinates of the bar edges
        valsleft = np.cumsum(np.append(0, valsnorm.flatten()[:-1])).reshape(vals.shape)

        ####################
        # Pour les couleurs
        ####################

        # Fonction pour convertir les couleurs hexadécimales en RGB
        def hex_to_rgb(hex_color):
            return hex2color(hex_color)

        # Passage en RGB
        category_colors_rgb = [hex_to_rgb(color) for color in category_colors.values()]

        # Palette initiale pour le camembert principal
        pie_chart_colors = list(plt.cm.tab20b_r.colors[:len(values)])

        # Filtrage des couleurs utilisées pour ne pas les répéter
        filtered_pie_colors = [color for color in pie_chart_colors if color not in category_colors_rgb]

        # Compléter la palette si nécessaire
        if len(filtered_pie_colors) < len(values):
            additional_colors = list(plt.cm.tab20c_r.colors[:len(values) - len(filtered_pie_colors)])
            filtered_pie_colors.extend(additional_colors)

        ####################

        ###########################
        # Mise en place des cercles
        ###########################

        # Anneau interne pour la distribution des uri (nouveau positionnement interne)  
        bars = ax.bar(
            x=valsleft[:, 0],
            width=valsnorm.sum(axis=1),
            bottom=1 - 2 * size,  # Position interne
            height=size * 8,
            color=filtered_pie_colors,
            edgecolor='w',
            linewidth=1,
            align="edge"
        )

        # Ajouter les pourcentages et étiquettes pour chaque segment de l'anneau interne
        total_sum = vals.sum()  # Somme totale pour calculer les pourcentages
        
        for i, bar in enumerate(bars):
            angle = valsleft[i, 0] + valsnorm.sum(axis=1)[i] / 2
            angle_deg = np.degrees(angle)
            alignment = angle_deg >= 90 and angle_deg <= 270
            rotation = angle_deg + (180 if alignment else 0)
            flip = angle_deg >= 90 and angle_deg <= 270
            haValue = 'left' if flip else 'right'

            # Récupérer les acronymes correspondants
            uri = labels[i]
            associated_strs = uri_groups.get(uri, [])
            
            # Déterminer le texte à afficher
            if len(associated_strs) == 1:
                selected_str = associated_strs[0]
            else:
                processed_str = process_string(subdir)
                selected_str = processed_str if processed_str in associated_strs else uri
            
            # Calculer le pourcentage
            percentage = (values.iloc[i] / total_sum) * 100
            label_pct = f"{percentage:.1f}%"
            label_text = f"{selected_str}  {label_pct}"
            label_text = label_text.replace("https:", "").replace("http:", "")

            # Afficher le pourcentage dans l'anneau interne
            offsetPct = 0.20  # Décalage pour rapprocher les % du centre
            if (len(label_text) > 20):
                haValue = 'center' 
            ax.text(angle, 1 + size * 5 + offsetPct, label_text, ha=haValue, va='center', color="black", fontsize=8,
                    rotation=rotation, rotation_mode="anchor")

        ####################

        # Anneau externe pour la distribution SPO
        bars_spo = ax.bar(
            x=valsleft.flatten(),
            width=valsnorm.flatten(),
            bottom=1 + size*6,  # Position externe
            height=size * 2,
            color=[category_colors[cat] for cat in legend_order] * len(vals),
            edgecolor='w',
            linewidth=1,
            align="edge"
        )

        # Ajouter les étiquettes pour chaque segment de l'anneau SPO
        for i, bar_spo in enumerate(bars_spo):
            angle = valsleft.flatten()[i] + valsnorm.flatten()[i] / 2
            angle_deg = np.degrees(angle)
            alignment = angle_deg >= 90 and angle_deg <= 270
            rotation = angle_deg + (180 if alignment else 0)
            
            label_spo = f"\n{vals.flatten()[i]}"

            if (valsnorm.flatten()[i] / valsnorm.flatten().sum()) * 100 > 0.1:
                # Afficher l'étiquette sur l'anneau SPO
                offsetSpo = 0.30  # Décalage pour éloigner les étiquettes du centre pour SPO
                ax.text(angle, 1 + size * 9 + offsetSpo, label_spo, ha='center', va='top', color="black", fontsize=8,
                        rotation=rotation, rotation_mode="anchor")        

        ####################

        ###########################
        # Lègende et titre
        ###########################

        # Création de la légende pour toute la figure
        spo_labels = list(category_colors.keys())
        fig.legend(
            handles=[plt.Line2D([0], [0], color=color, lw=4) for color in category_colors.values()],
            labels=spo_labels,
            loc="lower left",
            bbox_to_anchor=(0.05, 0.05),
            title="Distribution by vocabulary",
            fontsize="small",
            title_fontsize="medium"
        )

        # Ajouter le titre
        ax.set_title(f"Use of semantic vocabularies for the {process_string(subdir)} vocabulary", pad=40)
        ax.set_axis_off()

        #############
        # Sauvegarde
        #############

        output_path = os.path.join(subdir_path, f"histogram_semantic_{subdir}.png")
        plt.savefig(output_path)
        plt.close()

         