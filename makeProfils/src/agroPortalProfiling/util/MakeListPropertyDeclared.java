package agroPortalProfiling.util;

import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class MakeListPropertyDeclared {
	
	// Création liste des types des propriétés les plus utilisées.
	public static ArrayList<UriAndUri> makeList(Model model) {
		
		String prefix = AgroPortalProfilingConf.queryPrefix;

		ArrayList<UriAndUri> ListResources = new ArrayList<UriAndUri>();
	
		Query query = QueryFactory.create(prefix + 
			"SELECT ?property ?effectiveType WHERE { " +
            " ?property rdf:type ?type ." +
            " ?type (rdfs:subClassOf)* ?effectiveType ." +
            " FILTER (?effectiveType IN (rdf:Property, owl:ObjectProperty, owl:DatatypeProperty, owl:AnnotationProperty, " +
			"                            owl:FunctionalProperty, owl:InverseFunctionalProperty, owl:SymmetricProperty, " +
			" 							 owl:AsymmetricProperty, owl:TransitiveProperty, owl:IrreflexiveProperty, " + 
			"                            owl:ReflexiveProperty )) " +
            " } ORDER BY ?property "+
			" LIMIT 5000 "
		);
		
 		QueryExecution qe = QueryExecutionFactory.create(query, model);		
		ResultSet result = qe.execSelect();
		if (result.hasNext()) {
			while( result.hasNext() ) {
				QuerySolution querySolution = result.next() ;
				ListResources.add(new UriAndUri(querySolution.getResource("property").toString(), querySolution.getResource("effectiveType").toString()));
			}
		}
		
		return ListResources;
	}
}