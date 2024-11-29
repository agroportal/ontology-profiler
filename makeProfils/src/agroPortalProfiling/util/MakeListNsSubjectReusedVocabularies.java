package agroPortalProfiling.util;

import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class MakeListNsSubjectReusedVocabularies {

    // Création d'une liste avec les trois noms de domaine du sujet,
    // du predicat et de l'objet des triplets du graphe.
    public static ArrayList<UriAndStringAndNumber> makeList(Model model) {

        String prefix = AgroPortalProfilingConf.queryPrefix;

        ArrayList<UriAndStringAndNumber> ListResources = new ArrayList<>();

        // SPARQL Query pour extraire les namespaces des sujets, prédicats et objets
        String sparqlQuery = prefix +
        " SELECT ?namespace ?abrevNamespace (COUNT(?namespaceFound) AS ?count) ?order WHERE { " +
        "  VALUES (?namespace ?abrevNamespace ?order) { " +
        "    ('http://w3id.org/nkos/nkostype#classification_schema' 'Classification scheme' 1) " + // Classification scheme
        "    ('http://purl.org/dc/elements/1.1/' 'DC' 2) " + // Dublin core (DC)
        "    ('http://purl.org/dc/terms/' 'DCTERMS' 3) " + // Dublin core (DCTERMS)
        "    ('http://omv.ontoware.org/2005/05/ontology#' 'OMV' 4) " + // Ontology Metadata Vocabulary (OMV)
        "    ('http://www.isibang.ac.in/ns/mod#' 'MOD 1' 5) " + // Metadata for Ontology Description and Publication (MOD 1)
        "    ('https://w3id.org/mod' 'MOD 2' 6) " + // Metadata for Ontology Description and Publication (MOD 2)
        "    ('http://kannel.open.ac.uk/ontology#' 'DOOR' 7) " + // Descriptive Ontology of Ontology Relations (DOOR)
        "    ('http://purl.org/vocommons/voaf#' 'VOAF' 8) " + // Vocabulary of a Friend (VOAF)
        "    ('http://rdfs.org/ns/void#' 'VOID' 9) " + // Vocabulary of Interlinked Datasets (VOID)
        "    ('http://biomodels.net/vocab/idot.rdf#' 'IDOT' 10) " + // Identifiers.org (IDOT)
        "    ('http://purl.org/vocab/vann/' 'VANN' 11) " + // Vocabulary for annotating vocabulary descriptions (VANN)
        "    ('http://www.w3.org/ns/dcat#' 'DCAT' 12) " + // Data Catalog Vocabulary (DCAT)
        "    ('http://www.w3.org/ns/adms#' 'ADMS' 13) " + // Asset Description Metadata Schema (ADMS)
        "    ('http://schema.org/' 'SCHEMA' 14) " + // Schema.org (SCHEMA)
        "    ('http://xmlns.com/foaf/0.1/' 'FOAF' 15) " + // Friend of a Friend Vocabulary (FOAF)
        "    ('http://usefulinc.com/ns/doap#' 'DOAP' 16) " + // Description of a Project (DOAP)
        "    ('http://creativecommons.org/ns#' 'CC' 17) " + // Creative Commons Rights Expression Language (CC)
        "    ('http://www.w3.org/ns/prov#' 'PROV' 18) " + // Provenance Ontology (PROV)
        "    ('http://purl.org/pav/' 'PAV' 19) " + // Provenance, Authoring and Versioning (PAV)
        "    ('http://www.w3.org/ns/sparql-service-description#' 'SD' 20) " + // SPARQL 1.1 Service Description (SD)
        "    ('http://w3id.org/nkos#' 'NKOS' 21) " + // Networked Knowledge Organization Systems Dublin Core Application Profile (NKOS)
        "    ('http://purl.obolibrary.org/obo/iao.owl' 'IAO' 22) " + // The Information Artifact Ontology (IAO)
        "  } " +
        "  OPTIONAL { " +
        "    { " +
        "      SELECT ?namespaceFound WHERE { " +
        "        ?s ?p ?o . " +
        "        FILTER(isIRI(?s)) " +
        "        BIND(REPLACE(STR(?s), '([/#][^/#]*)$', '') AS ?namespaceS) " +
        "        BIND(CONCAT(?namespaceS, SUBSTR(STR(?s), STRLEN(?namespaceS) + 1, 1)) AS ?namespaceS1) " +
        "        BIND(IF(REGEX(STR(?s), '^http://purl.obolibrary.org/obo/[A-Za-z]+_\\\\d+$'), " +
        "            CONCAT('http://purl.obolibrary.org/obo', " +
        "            LCASE(SUBSTR(STR(?s), 31, STRLEN(STRBEFORE(SUBSTR(STR(?s), 31), '_')))),'.owl'), " +
        "            ?namespaceS1) AS ?namespaceFound) " +
        "      } " +
        "    }  " +
        "    FILTER(STR(?namespaceFound) = STR(?namespace)) " +
        "  } " +
        "} " +
        " GROUP BY ?namespace ?abrevNamespace ?order " +
        " ORDER BY ?order";


        // Exécuter la requête SPARQL
        Query query2 = QueryFactory.create(sparqlQuery);
        QueryExecution qe2 = QueryExecutionFactory.create(query2, model);
        ResultSet result2 = qe2.execSelect();

        // Extraction des namespaces
        while (result2.hasNext()) {
            QuerySolution querySolution = result2.next();
            String namespace = querySolution.getLiteral("namespace").getString();
	        String abrevNamespace = querySolution.getLiteral("abrevNamespace").getString();
			Integer count = querySolution.getLiteral("count").getInt();
            
            ListResources.add(new UriAndStringAndNumber(namespace, abrevNamespace, count));
        }

        return ListResources;
    }
}