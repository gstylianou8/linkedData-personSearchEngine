// The class used to crawl the web -- The crawler
// Also responsible to create the database and create the index

//import of the required libraries
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.sql.Connection;
import java.sql.DriverManager;

public class Spider {

	// the query used to extract people
	private String pSQuery = "prefix dbpedia:<http://dbpedia.org/resource/> "
			+ "prefix foaf:<http://xmlns.com/foaf/0.1/> "
			+ "prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "select ?person "
			+ "where {<%s> ?p ?person.?person rdf:type foaf:Person} LIMIT 100";

	// the query used to extract the resources
	private String rSQuery = "prefix dbpedia:<http://dbpedia.org/resource/> "
			+ "select ?resource " + "where {<%s> ?p ?resource}";

	// a private instance of the QueryProcessor class, used to execute the queries
	private QueryProcessor qp = new QueryProcessor();

	// stores people found during crawling
	private HashSet<String> people = new HashSet<String>();
	// stores resources found during crawling
	private Queue<String> resources = new ConcurrentLinkedQueue<String>();
	// stores resources which have already processed in order to avoid duplicates
	private HashSet<String> checkedR = new HashSet<String>();
	IndexWriter writer;
	StandardAnalyzer anal;
	Directory dir;

	// the path used to create the index
	// ADJUST THIS BASED ON YOUR MACHINE
	String indexPath = "";
	int maxPeople = 100;

	//for database creation

	String driver = "com.mysql.jdbc.Driver";
	String url = "jdbc:mysql://localhost/";

	// SET YOUR USERNAME AND THE PASSWORD
	String username = "";
	String password = "";

	Statement stmt = null;
	Connection conn;
	String sql;
	String urlD;
	// the name of the database
	String databaseName = "test";

	// a method used to create a database in MySQL
	public void createDatabase() {
		try {
			urlD = "jdbc:mysql://localhost/" +databaseName;
			int flag = 0;
			Class.forName(driver);
			// connect to MySQL using your url, username and password
			conn = DriverManager.getConnection(url, username, password);

			// test if the database has already been created
			ResultSet resultSet = conn.getMetaData().getCatalogs();
			while (resultSet.next()) {
				String databaseName = resultSet.getString(1);
				if (databaseName.equals("test"))
					flag = 1;
			}
			resultSet.close();

			if (flag == 0) {
				stmt = conn.createStatement();
				// the query needed to create the database is executed
				sql = "CREATE DATABASE " + databaseName;
				stmt.executeUpdate(sql);
				System.out.println("Database created successfully...");
				conn = DriverManager.getConnection(urlD, username, password);
				stmt = conn.createStatement();
				// the query needed to create a table in the database
				sql = "CREATE TABLE PEOPLE " +
						"(name VARCHAR(255)) ";
				stmt.executeUpdate(sql);
				System.out.println("Table created successfully...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	// end of the method
	}

	// a method used to get the last bit of a URI
	public String getLastBitFromUrl (String url) {
		return url.replaceFirst(".*/([^/?]+).*", "$1");
	}

	// a method used to add a record into the MySQL database
	public void addPerson(String person) {
		// a simple query to add to the database the person specified as a parameter
		sql = "INSERT INTO PEOPLE " +
				"VALUES (" + "\"" + person + " \")";
		try {
			conn = DriverManager.getConnection(urlD, username, password);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	// end of the method
	}

	// a method used to create the index
	public void createIndex() {
		try {
			// an index is created using the specified directory
			dir = FSDirectory.open(Paths.get(indexPath));
			// an StandardAnalyzer is created
			anal = new StandardAnalyzer();
			// the indexWriter is created
			IndexWriterConfig iwc = new IndexWriterConfig(anal);
			writer = new IndexWriter(dir, iwc);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
	// end of the method
	}

	// a method used for the closing of the IndexWriter
	public void closeWriter() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	// end of the method
	}

	// a method used to extract people from the URI specified as an argument
	private List<String> extractPeople(String dbpr) {
		try {
			System.out.print("Looking for people in " + dbpr);
			// the query is executed and the results are stored into a list of strings
			List<String> peeps = qp.runQuery(String.format(pSQuery, dbpr), "person");
			System.out.println(" found " + peeps.size());
			return peeps;
		} catch (Exception e) {
			System.out.println(" couldn't find anything - probably not a resource");
			return Arrays.asList();
		}
	// end of the method
	}

	// a method used to extract resources from the URI specified as an argument
	private List<String> extractResources(String dbpr) {
		try {
			System.out.print("Looking for resources in " + dbpr);
			// the query is executed and the results are stored into a list of strings
			List<String> res = qp.runQuery(String.format(rSQuery, dbpr),"resource");
			System.out.println(" found " + res.size());
			return res;
		} catch (Exception e) {
			System.out.println(" couldn't find anything - probably not a resource");
			return Arrays.asList();
		}
	// end of the method
	}

	// the "core" method of the class
	public void walk(String dbpr) {
		// initially the seed page is used to extract people and resources
		people.addAll(extractPeople(dbpr));
		resources.addAll(extractResources(dbpr));

		// the loop is executed as long as there are resources available and the amount of people found does not exceed the maximum number of people
		while (!resources.isEmpty() && people.size() < maxPeople) {
			// each time a new resource is processed
			String res = resources.poll();
			if (!checkedR.contains(res)) {
				people.addAll(extractPeople(res));
				resources.addAll(extractResources(res));
				// all the resources that have already been processed are stored to avoid the processing of a resource twice
				checkedR.add(res);
			}
		}

		int i = 1;

		// the appropriate methods are called to create a MySQL database and an index.
		createDatabase();
		createIndex();

		// this loop goes through all the people found in the crawling part
		for (String p : people) {
			System.out.println(i + ". " + p);
			String name[] = getLastBitFromUrl(p).split("_");
			String text = "";
			try {
				for (int k=0; k<name.length;k++)
					text = text + name[k] + " ";
				// a new document is created for each person
				Document doc = new Document();
				// the parts of the name and the URI of each person are stored in the document
				doc.add(new TextField("name", text, Field.Store.YES));
				doc.add(new TextField("link", p, Field.Store.YES));
				// the writer adds the document to the index directory
				writer.addDocument(doc);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// each person is added to the database
			addPerson(p);
			i++;
		}
		// closing of the writer
		closeWriter();

		/*
		 * Note there may be some overflow of the 100 first found people. For
		 * example if there are 99 people in the set the next resource will be
		 * queried if that resource then returns 3 people the set will finish
		 * containing 102 people.
		 */

	// end of the walk method
	}

	// the main method of the crawler
	public static void main(String[] args) {
		// a new instance of the class is created using a seed page
		new Spider().walk("http://dbpedia.org/resource/Leeds_University");
	// end of the main method
	}

// end of the class
}