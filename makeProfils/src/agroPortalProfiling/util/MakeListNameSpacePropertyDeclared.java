package agroPortalProfiling.util;

import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class MakeListNameSpacePropertyDeclared {
	
	// Création liste des types des propriétés les plus utilisées.
	public static ArrayList<UriAndUriAndNumber> makeList(Model model) {
		
		String prefix = AgroPortalProfilingConf.queryPrefix;

		ArrayList<UriAndUriAndNumber> ListResources = new ArrayList<UriAndUriAndNumber>();
	
		Query query = QueryFactory.create(prefix + 
			"SELECT ?propertyNameSpace ?effectiveType (COUNT(?propertyNameSpace) AS ?count) WHERE { " +
            " ?p rdf:type ?type ." +
            " ?type (rdfs:subClassOf)* ?effectiveType ." +
            " FILTER (?effectiveType IN (rdf:Property, owl:ObjectProperty, owl:DatatypeProperty, owl:AnnotationProperty, " +
			"                            owl:FunctionalProperty, owl:InverseFunctionalProperty, owl:SymmetricProperty, " +
			" 							 owl:AsymmetricProperty, owl:TransitiveProperty, owl:IrreflexiveProperty, " + 
			"                            owl:ReflexiveProperty )) " +
			" BIND(REPLACE(STR(?p), '([/#][^/#]*)$', '') AS ?namespaceP) " +
        	" BIND(CONCAT(?namespaceP, SUBSTR(STR(?p), STRLEN(?namespaceP) + 1, 1)) AS ?namespaceP1) " +
        	" BIND(IF(REGEX(STR(?p), '^http://purl.obolibrary.org/obo/[A-Za-z]+_\\\\d+$'), " +
        	" 	CONCAT('http://purl.obolibrary.org/obo', " + 
			"   LCASE(SUBSTR(STR(?p), 31, STRLEN(STRBEFORE(SUBSTR(STR(?p), 31), '_')))),'.owl'),  " +
        	"   ?namespaceP1) AS ?propertyNameSpace) " +
            " } " +
			" GROUP BY ?propertyNameSpace ?effectiveType " +
            " ORDER BY DESC(?count) "
		);
		
 		QueryExecution qe = QueryExecutionFactory.create(query, model);		
		ResultSet result = qe.execSelect();
		if (result.hasNext()) {
			while( result.hasNext() ) {
				QuerySolution querySolution = result.next() ;
				String propertyNameSpace = querySolution.getLiteral("propertyNameSpace").getString();
			 	String effectiveType = querySolution.getResource("effectiveType").toString();
				Integer count = querySolution.getLiteral("count").getInt();
				ListResources.add(new UriAndUriAndNumber(propertyNameSpace, effectiveType, count));
			}
		}
		
		return ListResources;
	}
}