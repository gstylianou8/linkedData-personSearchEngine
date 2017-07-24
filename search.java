// A class used to search the index directory

// import the required libraries
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;


public class search {
    // the main method of the class
    public static void main(String[] args) {
        // the index directory
        // ADJUST THIS BASED ON YOUR MACHINE
        String indexPath = "";
        // save the argument specified by the user
        String querystr = args[0]+'*';

        try {

            // create an instance of the IndexSearcher class, create a StandardAnalyzer and create a query to search the index directory
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            IndexReader directoryReader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(directoryReader);
            Analyzer anal = new StandardAnalyzer();
            Query q = new QueryParser("name", anal).parse(querystr);

            // save the top 200 results
            TopDocs results = searcher.search(q, 200);
            ScoreDoc[] hits = results.scoreDocs;

            // print the URI of each person found to match the specified argument
            System.out.println("Found " + hits.length + " hits.");
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". " + "\t" + d.get("link"));
            }

        } catch (Exception ex) {
            System.out.println("An exception was raised");
            ex.printStackTrace();
        }
    // end of the main method
    }
// end of the class
} 