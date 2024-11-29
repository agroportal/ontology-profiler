package agroPortalProfiling.util;

import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class MakeListTriplePredicatNameSpaceNoRdfRdfsOwlUsageCount {
	
	// Création d'une liste des propriétés et de leur usage dans un triplet
	public static ArrayList<UriAndNumber> makeList(Model model) {
		
		String prefix = AgroPortalProfilingConf.queryPrefix;

		ArrayList<UriAndNumber> ListResources = new ArrayList<UriAndNumber>();
		
		// Requête SPARQL avec un filtre pour exclure les propriétés dont le domaine est le namespace RDF syntaxe
		Query query = QueryFactory.create(prefix + 
        " SELECT ?namespace (COUNT(?namespace) AS ?count) WHERE { " +
        "    	?s ?p ?o . " +
		// Exclure les propriétés dont le domaine est http://www.w3.org/1999/02/22-rdf-syntax-ns#
		" 		FILTER(!STRSTARTS(STR(?p), STR(rdf:)))" +
		// Exclure les propriétés dont le domaine est http://www.w3.org/2000/01/rdf-schema#
		" 		FILTER(!STRSTARTS(STR(?p), STR(rdfs:)))" +
		// Exclure les propriétés dont le domaine est http://www.w3.org/2002/07/owl#
		" 		FILTER(!STRSTARTS(STR(?p), STR(owl:)))" +
        "       FILTER(STR(?o) != STR(owl:Ontology))" +
		"       FILTER(STR(?p) != STR(owl:imports))" +
        "       BIND(REPLACE(STR(?p), '([/#][^/#]*)$', '') AS ?namespaceP) " +
        "       BIND(CONCAT(?namespaceP, SUBSTR(STR(?p), STRLEN(?namespaceP) + 1, 1)) AS ?namespaceP1) " +
        "       BIND(IF(REGEX(STR(?p), '^http://purl.obolibrary.org/obo/[A-Za-z]+_\\\\d+$'), " +
        "           CONCAT('http://purl.obolibrary.org/obo', " + //
		"           LCASE(SUBSTR(STR(?p), 31, STRLEN(STRBEFORE(SUBSTR(STR(?p), 31), '_')))),'.owl'),  " +
        "           ?namespaceP1) AS ?namespace) " +
        " } " +
        " GROUP BY ?namespace " +
        " ORDER BY DESC(?count) "
		);		

		QueryExecution qe = QueryExecutionFactory.create(query, model);		
		ResultSet result = qe.execSelect();
		
		if (result.hasNext()) {
			while( result.hasNext() ) {
				QuerySolution querySolution = result.next() ;
				String namespace = querySolution.getLiteral("namespace").getString();
				Integer count = querySolution.getLiteral("count").getInt();
            	ListResources.add(new UriAndNumber(namespace, count));
			}
		}
		qe.close();
		return ListResources;
	}
}
