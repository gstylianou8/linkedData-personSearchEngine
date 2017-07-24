// A class used by the crawler to execute queries in order to find resources and people on the Semantic Web

//import of the required libraries
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import java.util.ArrayList;
import java.util.List;

// the QueryProcessor class
public class QueryProcessor {

    // the runQuery method without parameters
    public void runQuery() {
        
        String query = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";

        // this the endpoint of the SPARQL server
        String sparqlEndpointURL =  "http://dbpedia.org/sparql";

        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpointURL,query);

        ResultSet results = qexec.execSelect() ;
        for ( ; results.hasNext() ; )
        {
            // get the next solution from the list
           QuerySolution soln = results.nextSolution();

            // get the variable value from the list
           String Celeb = soln.get("?Concept").toString();

            // output the Celeb
           	System.out.println(Celeb);

        }
        qexec.close();
    // end of the runQuery method
    }

    // the runQuery method which takes two strings arguments as parameters and returns a list of strings.
    // the first string parameter represents the query and the second one represents the name of the variable
    public List<String> runQuery(String query, String variableName) {
        List<String> variables = new ArrayList<String>();
        // this the endpoint of the SPARQL server
        String sparqlEndpointURL = "http://dbpedia.org/sparql";

        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpointURL, query);
        ResultSet results = qexec.execSelect();
        for (; results.hasNext(); ) {
            // get the next solution from the list
            QuerySolution soln = results.nextSolution();

            // get the variable value from the list
            String variable = soln.get("?" + variableName).toString();
            variables.add(variable);

        }
        qexec.close();

        // return the idenified variables
        return variables;
    // end of the runQuery method
    }
// end of the class
}