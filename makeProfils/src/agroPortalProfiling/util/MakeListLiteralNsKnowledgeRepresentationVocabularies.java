package agroPortalProfiling.util;

import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class MakeListLiteralNsKnowledgeRepresentationVocabularies {

    // Création d'une liste avec les trois noms de domaine du sujet,
    // du predicat et de l'objet des triplets du graphe.
    public static ArrayList<UriAndStringAndNumber> makeList(Model model) {

        String prefix = AgroPortalProfilingConf.queryPrefix;

        ArrayList<UriAndStringAndNumber> ListResources = new ArrayList<>();

        // SPARQL Query pour extraire les namespaces des sujets, prédicats et objets
        String sparqlQuery = prefix +
        " SELECT ?namespace ?abrevNamespace (COUNT(?namespaceFound) AS ?count) ?order WHERE { " +
        "  VALUES (?namespace ?abrevNamespace ?order) { " +
        "    ('http://www.w3.org/1999/02/22-rdf-syntax-ns#' 'RDF' 1) " +
        "    ('http://www.w3.org/2000/01/rdf-schema#' 'RDFS' 2) " +
        "    ('http://www.w3.org/2002/07/owl#' 'OWL' 3) " +
        "    ('http://www.w3.org/2004/02/skos/core#' 'SKOS' 4) " +
        "    ('http://www.w3.org/2008/05/skos-xl#' 'SKOS-XL' 5) " +
        "    ('http://www.geneontology.org/formats/oboInOwl#' 'OBOINOWL' 6) " +
        "    ('http://purl.obolibrary.org/obo/ro.owl' 'RO' 7) " +
        "  } " +
        "  OPTIONAL { " +
        "    { " +
        "      SELECT ?namespaceFound WHERE { " +
        "        ?s ?p ?o . " +
        "        FILTER(isIRI(?s)) " +
        "        FILTER(isLiteral(?o)) " +  // Ne traiter que les triplets dont les objets sont des littéraux
        "        BIND(REPLACE(STR(?s), '([/#][^/#]*)$', '') AS ?namespaceS) " +
        "        BIND(CONCAT(?namespaceS, SUBSTR(STR(?s), STRLEN(?namespaceS) + 1, 1)) AS ?namespaceS1) " +
        "        BIND(IF(REGEX(STR(?s), '^http://purl.obolibrary.org/obo/[A-Za-z]+_\\\\d+$'), " +
        "            CONCAT('http://purl.obolibrary.org/obo', " +
        "            LCASE(SUBSTR(STR(?s), 31, STRLEN(STRBEFORE(SUBSTR(STR(?s), 31), '_')))),'.owl'), " +
        "            ?namespaceS1) AS ?namespaceFound) " +
        "      } " +
        "    } UNION { " +
        "      SELECT ?namespaceFound WHERE { " +
        "        ?s ?p ?o . " +
        "        FILTER(isLiteral(?o)) " + // Ne traiter que les triplets dont les objets sont des littéraux
        "        BIND(REPLACE(STR(?p), '([/#][^/#]*)$', '') AS ?namespaceP) " +
        "        BIND(CONCAT(?namespaceP, SUBSTR(STR(?p), STRLEN(?namespaceP) + 1, 1)) AS ?namespaceP1) " +
        "        BIND(IF(REGEX(STR(?p), '^http://purl.obolibrary.org/obo/[A-Za-z]+_\\\\d+$'), " +
        "            CONCAT('http://purl.obolibrary.org/obo', " +
        "            LCASE(SUBSTR(STR(?p), 31, STRLEN(STRBEFORE(SUBSTR(STR(?p), 31), '_')))),'.owl'), " +
        "            ?namespaceP1) AS ?namespaceFound) " +
        "      } " +
        "    } " +
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