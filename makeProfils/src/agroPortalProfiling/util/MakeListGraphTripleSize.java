package agroPortalProfiling.util;

import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class MakeListGraphTripleSize {
	
	// Création d'une liste pour extraire le nombre de triplets du graphe RDF
	public static ArrayList<UriAndNumber> makeList(Model model) {
    
		String prefix = AgroPortalProfilingConf.queryPrefix;
	
		ArrayList<UriAndNumber> ListResources = new ArrayList<UriAndNumber>();

		 // SPARQL Query 
		 String sparqlQuery = prefix +
		 	" SELECT (COUNT(*) AS ?count) WHERE { " +
            "       ?s ?p ?o . " +
			" } " ;
 
		 // Exécuter la requête SPARQL
		 Query query = QueryFactory.create(sparqlQuery);
		 QueryExecution qe = QueryExecutionFactory.create(query, model);
		 ResultSet result = qe.execSelect();
 
		 // Extraction du compte
		 while (result.hasNext()) {
			 QuerySolution querySolution = result.next();
			
			 Integer count = querySolution.getLiteral("count").getInt();
			 
			 ListResources.add(new UriAndNumber("Graph size", count));
		 }
		 // ListResources.sort((o1, o2) -> Integer.compare(o2.getNumber(), o1.getNumber()));
		 return ListResources;
	}

}