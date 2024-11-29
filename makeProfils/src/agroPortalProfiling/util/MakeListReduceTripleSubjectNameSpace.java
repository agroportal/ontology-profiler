package agroPortalProfiling.util;

import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class MakeListReduceTripleSubjectNameSpace {

    // Création d'une liste avec les trois noms de domaine du sujet,
    // du predicat et de l'objet des triplets du graphe.
    public static ArrayList<UriAndNumber> makeList(Model model) {

        String prefix = AgroPortalProfilingConf.queryPrefix;

        ArrayList<UriAndNumber> ListResources = new ArrayList<>();

        // SPARQL Query pour extraire les namespaces des sujets, prédicats et objets
        String sparqlQuery = prefix +
    "SELECT ?namespace ?count WHERE { " +
    "  { " +
    "    SELECT ?namespace (COUNT(?namespace) AS ?count) WHERE { " +
    "      ?s ?p ?o . " +
    "      FILTER(isIRI(?s)) " +
    "      FILTER(STR(?o) != 'http://www.w3.org/2002/07/owl#Ontology') " +
    "      FILTER(STR(?p) != 'http://www.w3.org/2002/07/owl#imports') " +
    "      BIND(REPLACE(STR(?s), '([/#][^/#]*)$', '') AS ?namespaceS) " +
    "      BIND(CONCAT(?namespaceS, SUBSTR(STR(?s), STRLEN(?namespaceS) + 1, 1)) AS ?namespaceS1) " +
    "      BIND(IF(REGEX(STR(?s), '^http://purl.obolibrary.org/obo/[A-Za-z]+_\\\\d+$'), " +
    "           CONCAT('http://purl.obolibrary.org/obo', " + 
    "           LCASE(SUBSTR(STR(?s), 31, STRLEN(STRBEFORE(SUBSTR(STR(?s), 31), '_')))), '.owl'),  " +
    "           ?namespaceS1) AS ?namespace) " +
    "    } " +
    "    GROUP BY ?namespace " +
    "    ORDER BY DESC(?count) " +
    "  } " +
    "  { " +
    "    SELECT (SUM(?c) AS ?totalCount) WHERE { " +
    "      ?s1 ?p1 ?o1 . " +
    "      FILTER(isIRI(?s1)) " +
    "      FILTER(STR(?o1) != 'http://www.w3.org/2002/07/owl#Ontology') " +
    "      FILTER(STR(?p1) != 'http://www.w3.org/2002/07/owl#imports') " +
    "      BIND(1 AS ?c) " +  // BIND une constante pour le comptage
    "    } " +
    "  } " +
    "  FILTER(?count >= (?totalCount * 0.01)) " +  // Appliquer le filtre des 1 %
    "} " +
    "ORDER BY DESC(?count)";

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
