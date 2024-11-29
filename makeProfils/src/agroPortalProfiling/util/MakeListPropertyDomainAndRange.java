package agroPortalProfiling.util;

import java.io.IOException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.ArrayList;

public class MakeListPropertyDomainAndRange {
	
	// Extraction des domaines et ranges déclarés des prédicats  
	public static ArrayList<UriAndX2UriAndUriListAndUriListAndUriList> makeList(Model model) throws JsonParseException, JsonMappingException, IOException {

        String prefix = AgroPortalProfilingConf.queryPrefix;
		ArrayList<UriAndX2UriAndUriListAndUriListAndUriList> listPropertyDomainAndRange = new ArrayList<UriAndX2UriAndUriListAndUriListAndUriList>();
        
        // Define SPARQL query to retrieve properties with their domain and range
        Query query = QueryFactory.create(prefix + 
            " SELECT ?property ?domain ?range ?domainOneOf ?domainUnionOf ?domainIntersectionOf " +
            "        ?rangeOneOf ?rangeUnionOf ?rangeIntersectionOf WHERE { " +
            "  { " +
            "    SELECT DISTINCT ?property WHERE { " +
            "      { " +
            "        ?property rdfs:domain ?domain . " +
            "      } " +
            "      UNION " +
            "      { " +
            "        ?property rdfs:range ?range . " +
            "      } " +
            "    } " +
            "  } " +
            "  OPTIONAL { " +
            "    ?property rdfs:domain ?domain . " +
            "    OPTIONAL { " +
            "      ?domain owl:oneOf ?domainOneOf . " +
            "    } " +
            "    OPTIONAL { " +
            "      ?domain owl:unionOf ?domainUnionOf . " +
            "    } " +
            "    OPTIONAL { " +
            "      ?domain owl:intersectionOf ?domainIntersectionOf . " +
            "    } " +
            "  } " +
            "  OPTIONAL { " +
            "    ?property rdfs:range ?range . " +
            "    OPTIONAL { " +
            "      ?range owl:oneOf ?rangeOneOf . " +
            "    } " +
            "    OPTIONAL { " +
            "      ?range owl:unionOf ?rangeUnionOf . " +
            "    } " +
            "    OPTIONAL { " +
            "      ?range owl:intersectionOf ?rangeIntersectionOf . " +
            "    } " +
            "  } " +
            "} "
        );
	

        QueryExecution qe = QueryExecutionFactory.create(query, model);		
		ResultSet result = qe.execSelect();

        while (result.hasNext()) {
            QuerySolution querySolution = result.nextSolution();
            RDFNode property = querySolution.get("property");
            RDFNode domain = querySolution.get("domain");
            RDFNode range = querySolution.get("range");
            RDFNode domainOneOf = querySolution.get("domainOneOf");
            RDFNode domainUnionOf = querySolution.get("domainUnionOf");
            RDFNode domainIntersectionOf = querySolution.get("domainIntersectionOf");
            RDFNode rangeOneOf = querySolution.get("rangeOneOf");
            RDFNode rangeUnionOf = querySolution.get("rangeUnionOf");
            RDFNode rangeIntersectionOf = querySolution.get("rangeIntersectionOf");
        
            UriAndX2UriAndUriListAndUriListAndUriList propertyDomainAndRange = new UriAndX2UriAndUriListAndUriListAndUriList();
            propertyDomainAndRange.setUri(new Uri(property.toString()));
        
            // Process domain
            UriAndUriListAndUriListAndUriList domainInfo = processDomainOrRange(domain, domainOneOf, domainUnionOf, domainIntersectionOf);
            propertyDomainAndRange.setDomain(domainInfo);
        
            // Process range
            UriAndUriListAndUriListAndUriList rangeInfo = processDomainOrRange(range, rangeOneOf, rangeUnionOf, rangeIntersectionOf);
            propertyDomainAndRange.setRange(rangeInfo);
        
            listPropertyDomainAndRange.add(propertyDomainAndRange);
        }
        

        return listPropertyDomainAndRange;
	}

    private static UriAndUriListAndUriListAndUriList processDomainOrRange(RDFNode mainNode, RDFNode oneOfNode, RDFNode unionOfNode, RDFNode intersectionOfNode) {
        UriAndUriListAndUriListAndUriList info = new UriAndUriListAndUriListAndUriList();

        // Set main node URI, default to an empty URI if null
        info.setUri(new Uri(mainNode != null ? mainNode.toString() : ""));

        // Set optional fields, defaulting to empty lists if null
        info.setOneOf(oneOfNode != null ? new UriList(oneOfNode.as(RDFList.class)) : new UriList());
        info.setUnionOf(unionOfNode != null ? new UriList(unionOfNode.as(RDFList.class)) : new UriList());
        info.setIntersectionOf(intersectionOfNode != null ? new UriList(intersectionOfNode.as(RDFList.class)) : new UriList());

        return info;
    }
}
	

    