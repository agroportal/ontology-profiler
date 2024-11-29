package agroPortalProfiling.util;

import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class MakeListUriOntology {
	
	// Création d'une liste des liens dans un triplet
	public static ArrayList<Uri> makeList(Model model) {
    
		String prefix = AgroPortalProfilingConf.queryPrefix;
	
		ArrayList<Uri> ListResources = new ArrayList<Uri>();

		 // SPARQL Query pour extraire les namespaces des sujets, prédicats et objets
		 String sparqlQuery = prefix +
		 	" SELECT (?s AS ?uriOntology) WHERE { " +
            " 	?s ?p ?o . " +
			"	FILTER(STR(?o) = STR(owl:Ontology))" +
			" } " ;
 
		 // Exécuter la requête SPARQL
		 Query query = QueryFactory.create(sparqlQuery);
		 QueryExecution qe = QueryExecutionFactory.create(query, model);
		 ResultSet result = qe.execSelect();
 
		 // Extraction des namespaces
		 while (result.hasNext()) {
			 QuerySolution querySolution = result.next();
			
			 String ontologyURI = querySolution.getResource("uriOntology").toString();
			 
			 ListResources.add(new Uri(ontologyURI));
		 }
		 ListResources.sort((o1, o2) -> o2.getUri().toString().compareTo(o1.getUri().toString()));
		 return ListResources;
	}

}