package agroPortalProfiling.util;

import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class MakeListTripleObjectNameSpace {

    // Création d'une liste avec les trois noms de domaine du sujet,
    // du predicat et de l'objet des triplets du graphe.
    public static ArrayList<UriAndNumber> makeList(Model model) {

        String prefix = AgroPortalProfilingConf.queryPrefix;

        ArrayList<UriAndNumber> ListResources = new ArrayList<>();

        // SPARQL Query pour extraire les namespaces des sujets, prédicats et objets
        String sparqlQuery = prefix +
        " SELECT ?namespace (COUNT(?namespace) AS ?count) WHERE { " +
        "       ?s ?p ?o . " +
        "       FILTER(isIRI(?o)) " +
		"       FILTER(STR(?o) != STR(owl:Ontology))" +
		"       FILTER(STR(?p) != STR(owl:imports))" +
        "       BIND(REPLACE(STR(?o), '([/#][^/#]*)$', '') AS ?namespaceO) " +
        "       BIND(CONCAT(?namespaceO, SUBSTR(STR(?o), STRLEN(?namespaceO) + 1, 1)) AS ?namespaceO1) " +
        "       BIND(IF(REGEX(STR(?o), '^http://purl.obolibrary.org/obo/[A-Za-z]+_\\\\d+$'), " +
        "           CONCAT('http://purl.obolibrary.org/obo', " + //
		"           LCASE(SUBSTR(STR(?o), 31, STRLEN(STRBEFORE(SUBSTR(STR(?o), 31), '_')))),'.owl'),  " +
        "           ?namespaceO1) AS ?namespace) " +
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