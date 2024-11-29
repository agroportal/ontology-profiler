package agroPortalProfiling.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.jena.rdf.model.Model;

public class MakeListModelPrefixNameSpace {
    
    // Création d'une liste des espaces de noms et des préfixes dans un modèle
    public static ArrayList<UriAndString> makeList(Model model) {

        Set<UriAndString> uniqueResources = new HashSet<>();

        // Récupère la carte des préfixes et des espaces de noms
        Map<String, String> prefixMap = model.getNsPrefixMap();

        // Crée une liste des préfixes et espaces de noms
        for (Map.Entry<String, String> entry : prefixMap.entrySet()) {
            String prefix = entry.getKey();
            String namespace = entry.getValue();
            uniqueResources.add(new UriAndString(namespace, prefix));
        }

        // Convertit le Set en ArrayList pour trier les éléments
        ArrayList<UriAndString> listResources = new ArrayList<>(uniqueResources);

        // Trie la liste en ordre croissant par prefix
        listResources.sort((uri1, uri2) -> uri1.getStr().compareTo(uri2.getStr()));

        return listResources;
    }
}
