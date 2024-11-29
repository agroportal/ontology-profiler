package agroPortalProfiling.util;

import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class MakeListPredicatNoRdfRdfsOwlUsageCount {
	
	// Création d'une liste des propriétés et de leur usage dans un triplet
	public static ArrayList<UriAndNumber> makeList(Model model) {
		
		String prefix = AgroPortalProfilingConf.queryPrefix;

		ArrayList<UriAndNumber> ListResources = new ArrayList<UriAndNumber>();
		
		// Requête SPARQL avec un filtre pour exclure les propriétés dont le domaine est le namespace RDF syntaxe
		Query query = QueryFactory.create(prefix + 
		"SELECT ?predicat (COUNT(?predicat) AS ?predicatUsage) " +
		" WHERE { " +
			" ?s ?predicat ?o ." +
			// Exclure les propriétés dont le domaine est http://www.w3.org/1999/02/22-rdf-syntax-ns#
			" FILTER(!STRSTARTS(STR(?predicat), STR(rdf:)))" +
			// Exclure les propriétés dont le domaine est http://www.w3.org/2000/01/rdf-schema#
			" FILTER(!STRSTARTS(STR(?predicat), STR(rdfs:)))" +
			// Exclure les propriétés dont le domaine est http://www.w3.org/2002/07/owl#
			" FILTER(!STRSTARTS(STR(?predicat), STR(owl:)))" +
		" } GROUP BY ?predicat ORDER BY DESC (?predicatUsage) " 
		);			
		
		QueryExecution qe = QueryExecutionFactory.create(query, model);		
		ResultSet result = qe.execSelect();
		
		if (result.hasNext()) {
			while( result.hasNext() ) {
				QuerySolution querySolution = result.next() ;
				ListResources.add(new UriAndNumber(querySolution.getResource("predicat").toString(), querySolution.getLiteral("predicatUsage").getInt())) ;
			}
		}
		qe.close();
		return ListResources;
	}
}
