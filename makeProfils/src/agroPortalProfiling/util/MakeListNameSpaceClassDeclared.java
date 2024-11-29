package agroPortalProfiling.util;

import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class MakeListNameSpaceClassDeclared {
	
	// Création liste des types des propriétés les plus utilisées.
	public static ArrayList<UriAndUriAndNumber> makeList(Model model) {
		
		String prefix = AgroPortalProfilingConf.queryPrefix;

		ArrayList<UriAndUriAndNumber> ListResources = new ArrayList<UriAndUriAndNumber>();
	
		Query query = QueryFactory.create(prefix + 
			"SELECT ?classNameSpace ?effectiveType (COUNT(?classNameSpace) AS ?count) WHERE { " +
            " ?class rdf:type ?type ." +
            " ?type (rdfs:subClassOf)* ?effectiveType ." +
			" FILTER(isIRI(?class)) " +
            " FILTER (?effectiveType IN (rdfs:Class, owl:Class)) " +
			" BIND(REPLACE(STR(?class), '([/#][^/#]*)$', '') AS ?namespaceC) " +
        	" BIND(CONCAT(?namespaceC, SUBSTR(STR(?class), STRLEN(?namespaceC) + 1, 1)) AS ?namespaceC1) " +
        	" BIND(IF(REGEX(STR(?class), '^http://purl.obolibrary.org/obo/[A-Za-z]+_\\\\d+$'), " +
        	" 	CONCAT('http://purl.obolibrary.org/obo', " + 
			"   LCASE(SUBSTR(STR(?class), 31, STRLEN(STRBEFORE(SUBSTR(STR(?class), 31), '_')))),'.owl'),  " +
        	"   ?namespaceC1) AS ?classNameSpace) " +
            " } " +
			" GROUP BY ?classNameSpace ?effectiveType " +
            " ORDER BY DESC(?count) "
		);
		
 		QueryExecution qe = QueryExecutionFactory.create(query, model);		
		ResultSet result = qe.execSelect();
		if (result.hasNext()) {
			while( result.hasNext() ) {
				QuerySolution querySolution = result.next() ;
				String classNameSpace = querySolution.getLiteral("classNameSpace").getString();
			 	String effectiveType = querySolution.getResource("effectiveType").toString();
				Integer count = querySolution.getLiteral("count").getInt();
				ListResources.add(new UriAndUriAndNumber(classNameSpace, effectiveType, count));
			}
		}
		
		return ListResources;
	}
}