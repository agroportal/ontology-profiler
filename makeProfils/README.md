# AgroPortalProfiling for AgroPortal

AgroPortalProfiling est une application qui à partir d'un fichier de sérialisation pour un graphe RDF fournie
 des listes au format JSON. Ces listes fournissent principalement des métriques sur l'utilisation au sein du graphe traité d'autres vocabulaires. Ces autres vocabulaires sont classés en trois groupes 

- 1 Les vocabulaires structuraux

    RDF (Resource Description Framework) : RDF Syntax
    RDFS (RDF Schema) : RDFS
    OWL (Web Ontology Language) : OWL
    SKOS (Simple Knowledge Organization System) : SKOS
    SKOS-XL (SKOS eXtension for Labels) : SKOS-XL
    OBOINOWL (Open Biological and Biomedical Ontologies in OWL) : OBOINOWL
    RO (Relations Ontology) : RO

- 2 Les vocabulaires de métadonnées

    Classification Scheme : NKOS Classification Scheme
    Dublin Core (DC) : DC
    Dublin Core Terms (DCTERMS) : DCTERMS
    Ontology Metadata Vocabulary (OMV) : OMV
    Metadata for Ontology Description and Publication (MOD 1 & 2) : MOD 1, MOD 2
    Descriptive Ontology of Ontology Relations (DOOR) : DOOR
    Vocabulary of a Friend (VOAF) : VOAF
    Data Catalog Vocabulary (DCAT) : DCAT

- 3 Les autres vocaburalaires

    Ce groupe englobe tous les autres vocabulaires non mentionnés dans les deux premiers cas.

# Fonctionnement de l'application

Les listes sont générées par des requêtes SPARQL, qui extraient les noms de domaine pour le sujet, le prédicat et l'objet à partir des triplets du graphe. Les listes traitées incluent les éléments suivants :
Listes générées :

    Liste du nombre de triplets du graphe :
        Nom : listGraphTripleSize
        Type : ArrayList<UriAndNumber>
        Fonction : Compte le nombre total de triplets dans le graphe.

    Liste des namespaces des propriétés déclarées :
        Nom : listNameSpacePropertyDeclared
        Type : ArrayList<UriAndUriAndNumber>
        Fonction : Liste les namespaces des propriétés déclarées dans le graphe.

    Liste des namespaces des classes déclarées :
        Nom : listNameSpaceClassDeclared
        Type : ArrayList<UriAndUriAndNumber>
        Fonction : Liste les namespaces des classes déclarées dans le graphe.

    URI de l'ontologie :
        Nom : listUriOntology
        Type : ArrayList<Uri>
        Fonction : Liste l'URI de l'ontologie utilisée (FILTER(STR(?o) = STR(owl:Ontology))).

    Liste des préfixes des espaces de noms :
        Nom : listModelPrefixNameSpace
        Type : ArrayList<UriAndString>
        Fonction : Liste les préfixes des namespaces (extraction par méthode JENA).

    URI des imports de l'ontologie :
        Nom : listUriImport
        Type : ArrayList<Uri>
        Fonction : Liste les imports d'ontologie (FILTER(STR(?p) = STR(owl:imports))).

    Liste des noms de domaine du sujet, du prédicat et de l'objet :
        Nom : listGraphNameSpace
        Type : ArrayList<UriAndNumber>
        Fonction : Extrait les noms de domaine des triplets du graphe.

    Liste des sujets :
        Nom : listTripleSubjectNameSpace
        Type : ArrayList<UriAndNumber>
        Fonction : Liste les noms de domaine des sujets dans les triplets.

    Liste des objets :
        Nom : listTripleObjectNameSpace
        Type : ArrayList<UriAndNumber>
        Fonction : Liste les noms de domaine des objets.

    Liste des prédicats :
        Nom : listTriplePredicatNameSpace
        Type : ArrayList<UriAndNumber>
        Fonction : Liste les noms de domaine des prédicats.

    Liens entre namespace sujets et namespace objets :
        Nom : listLinksSubjectObject
        Type : ArrayList<UriAndUriAndNumber>
        Fonction : Liste les liens entre les sujets et les objets.

    Liens avec l'ontologie par défaut :
        Nom : listLinksWithDefaultOntology
        Type : ArrayList<UriAndUriAndNumber>
        Fonction : Liste les liens où le domaine du sujet ou de l'objet appartient à l'ontologie traitée.

    Liens avec le prédicat "rdf:type" :
        Nom : listNameSpaceWithRdfTypePredicat
        Type : ArrayList<UriAndUriAndNumber>
        Fonction : Liste les liens utilisant le prédicat "rdf:type".

    Liens avec le prédicat "rdfs:subClassOf"
        Nom : listNameSpaceWithRdfsSubClassOfPredicat
        Type : ArrayList<UriAndUriAndNumber>
        Fonction : Liste les liens utilisant le prédicat "rdfs:subClassOf".

    Liste des namespaces dans les triplets :
        Nom : listTripleSubjectPredicateObjectNameSpace
        Type : ArrayList<UriAndUriAndUriAndNumber>
        Fonction : Liste complète des namespaces des sujets, prédicats et objets.

    Liste des namespaces dans les triplets avec le namespace l'ontologie par défaut :
        Nom : listTripleSubjectPredicateObjectNameSpaceWithDefaultOntology
        Type : ArrayList<UriAndUriAndUriAndNumber>
        Fonction : Liens triples où le domaine est l'ontologie par défaut.

    Liens avec des types littéraux :
        Nom : listTripleSubjectAndPredicateNameSpaceAndDataType
        Type : ArrayList<UriAndUriAndUriAndNumber>
        Fonction : Liens triples pour les objets littéraux.

    ++++ Pour les trois groupes ++++

    Liste des namespaces pour les vocabulaire de représentation des connaissances :
        Nom : listNsKnowledgeRepresentationVocabularies
        Type : ArrayList<UriAndStringAndNumber>
        Fonction : Liste des vocabulaires de représentation des connaissances.

    Liste des namespaces pour les vocabulaire réutilisés :
        Nom : listNsReusedVocabularies
        Type : ArrayList<UriAndStringAndNumber>
        Fonction : Liste des vocabulaires réutilisés.

    Liste des namespaces pour les autres vocabulaires :
        Nom : listNsOtherVocabularies
        Type : ArrayList<UriAndNumber>
        Fonction : Liste des autres vocabulaires.

    ++++ Pour les trois groupes en détail ++++    

    Liste des namespaces des sujets pour les vocabulaires de représentation des connaissances :
        Nom : listNsSubjectKnowledgeRepresentationVocabularies
        Type : ArrayList<UriAndStringAndNumber>
        Fonction : Liste des sujets pour les vocabulaires de représentation des connaissances.

    Liste des namespaces des sujets pour les vocabulaires réutilisés :
        Nom : listNsSubjectReusedVocabularies
        Type : ArrayList<UriAndStringAndNumber>
        Fonction : Liste des sujets pour les vocabulaires réutilisés.

    Liste des namespaces des sujets pour les autres vocabulaires :
        Nom : listNsSubjectOtherVocabularies
        Type : ArrayList<UriAndNumber>
        Fonction : Liste des sujets pour les autres vocabulaires.

    ++++++++    

    Liste des namespaces des predicats pour les vocabulaires de représentation des connaissances :
        Nom : listNsPredicateKnowledgeRepresentationVocabularies
        Type : ArrayList<UriAndStringAndNumber>
        Fonction : Liste des predicats pour les vocabulaires de représentation des connaissances.

    Liste des namespaces des predicats pour les vocabulaires réutilisés :
        Nom : listNsPredicateReusedVocabularies
        Type : ArrayList<UriAndStringAndNumber>
        Fonction : Liste des predicats pour les vocabulaires réutilisés.

    Liste des namespaces des predicats pour les autres vocabulaires :
        Nom : listNsPredicateOtherVocabularies
        Type : ArrayList<UriAndNumber>
        Fonction : Liste des predicats pour les autres vocabulaires.

    ++++++++        

    Liste des namespaces des objets pour les vocabulaires de représentation des connaissances :
        Nom : listNsObjectKnowledgeRepresentationVocabularies
        Type : ArrayList<UriAndStringAndNumber>
        Fonction : Liste des objets pour les vocabulaires de représentation des connaissances.

    Liste des namespaces des objets pour les vocabulaires réutilisés :
        Nom : listNsObjectReusedVocabularies
        Type : ArrayList<UriAndStringAndNumber>
        Fonction : Liste des objets pour les vocabulaires réutilisés.

    Liste des namespaces des objets pour les autres vocabulaires :
        Nom : listNsObjectOtherVocabularies
        Type : ArrayList<UriAndNumber>
        Fonction : Liste des objets pour les autres vocabulaires.

    ++++++++       

    Un exemple des fichiers en sortie est visible avec le fichier profilingForAgroportal.zip