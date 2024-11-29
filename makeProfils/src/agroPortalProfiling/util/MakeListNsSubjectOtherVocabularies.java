package agroPortalProfiling.util;

import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class MakeListNsSubjectOtherVocabularies {

    // Création d'une liste avec les trois noms de domaine du sujet,
    // du predicat et de l'objet des triplets du graphe.
    public static ArrayList<UriAndNumber> makeList(Model model) {

        String prefix = AgroPortalProfilingConf.queryPrefix;

        ArrayList<UriAndNumber> ListResources = new ArrayList<>();

        // SPARQL Query pour extraire les namespaces des sujets, prédicats et objets
        String sparqlQuery = prefix +
        " SELECT ?namespace (COUNT(?namespace) AS ?count) WHERE { " +
        "   { " +
        "     SELECT ?namespace WHERE { " +
        "       ?s ?p ?o . " +
        "       FILTER(isIRI(?s)) " +
        "       BIND(REPLACE(STR(?s), '([/#][^/#]*)$', '') AS ?namespaceS) " +
        "       BIND(CONCAT(?namespaceS, SUBSTR(STR(?s), STRLEN(?namespaceS) + 1, 1)) AS ?namespaceS1) " +
        "       BIND(IF(REGEX(STR(?s), '^http://purl.obolibrary.org/obo/[A-Za-z]+_\\\\d+$'), " +
        "           CONCAT('http://purl.obolibrary.org/obo', " + //
		"           LCASE(SUBSTR(STR(?s), 31, STRLEN(STRBEFORE(SUBSTR(STR(?s), 31), '_')))),'.owl'),  " +
        "           ?namespaceS1) AS ?namespace) " +
        "     } " +
        "   } " +
        "   FILTER(STR(?namespace) NOT IN ('http://www.w3.org/1999/02/22-rdf-syntax-ns#', " + 
        "    'http://www.w3.org/2000/01/rdf-schema#',       " +
        "    'http://www.w3.org/2002/07/owl#',              " +
        "    'http://www.w3.org/2004/02/skos/core#',        " +
        "    'http://www.w3.org/2008/05/skos-xl#',          " +
        "    'http://w3id.org/nkos/nkostype#classification_schema', " + // Classification scheme
        "    'http://purl.org/dc/elements/1.1/', " + // Dublin core (DC)
        "    'http://purl.org/dc/terms/', " + // Dublin core (DCTERMS)
        "    'http://omv.ontoware.org/2005/05/ontology#', " + // Ontology Metadata Vocabulary (OMV)
        "    'http://www.isibang.ac.in/ns/mod#', " + // Metadata for Ontology Description and Publication (MOD 1)
        "    'https://w3id.org/mod', " + // Metadata for Ontology Description and Publication (MOD 2)
        "    'http://kannel.open.ac.uk/ontology#', " + // Descriptive Ontology of Ontology Relations (DOOR)
        "    'http://purl.org/vocommons/voaf#', " + // Vocabulary of a Friend (VOAF)
        "    'http://rdfs.org/ns/void#', " + // Vocabulary of Interlinked Datasets (VOID)
        "    'http://biomodels.net/vocab/idot.rdf#', " + // Identifiers.org (IDOT)
        "    'http://purl.org/vocab/vann/', " + // Vocabulary for annotating vocabulary descriptions (VANN)
        "    'http://www.w3.org/ns/dcat#', " + // Data Catalog Vocabulary (DCAT)
        "    'http://www.w3.org/ns/adms#', " + // Asset Description Metadata Schema (ADMS)
        "    'http://schema.org/', " + // Schema.org (SCHEMA)
        "    'http://xmlns.com/foaf/0.1/', " + // Friend of a Friend Vocabulary (FOAF)
        "    'http://usefulinc.com/ns/doap#', " + // Description of a Project (DOAP)
        "    'http://creativecommons.org/ns#', " + // Creative Commons Rights Expression Language (CC)
        "    'http://www.w3.org/ns/prov#', " + // Provenance Ontology (PROV)
        "    'http://purl.org/pav/', " + // Provenance, Authoring and Versioning (PAV)
        "    'http://www.geneontology.org/formats/oboInOwl#', " + // OboInOwl Mappings (OBOINOWL)
        "    'http://www.w3.org/ns/sparql-service-description#', " + // SPARQL 1.1 Service Description (SD)
        "    'http://w3id.org/nkos#', " + // Networked Knowledge Organization Systems Dublin Core Application Profile (NKOS)
        "    'http://purl.obolibrary.org/obo/iao.owl', " +
        "    'http://purl.obolibrary.org/obo/ro.owl' " +
        "   ))" +
        " } " +
        " GROUP BY ?namespace " +
        " ORDER BY DESC(?count) ";

        // Exécuter la requête SPARQL
        Query query2 = QueryFactory.create(sparqlQuery);
        QueryExecution qe2 = QueryExecutionFactory.create(query2, model);
        ResultSet result2 = qe2.execSelect();

        // Extraction des namespaces
        while (result2.hasNext()) {
            QuerySolution querySolution = result2.next();
            String namespace = querySolution.getLiteral("namespace").getString();
			Integer count = querySolution.getLiteral("count").getInt();
            
            ListResources.add(new UriAndNumber(namespace, count));
            // System.out.println("Namespace: " + namespace + ", Count: " + count);
        }
        // ListResources.sort((o1, o2) -> Integer.compare(o2.getNumber(), o1.getNumber()));
        return ListResources;
    }
}