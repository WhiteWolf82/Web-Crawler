import java.util.*;
import java.io.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class SearchEngine
{
	//the maximum total number + 1
	public static int maxTotalNum = 53746;
	
	//the search function
	public static void search(String filePath, String type, String searchStr)
	{
		Scanner in = new Scanner(System.in);
		File file = new File(filePath);
		try
		{
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(file)));
			Analyzer analyzer = new IKAnalyzer();
			QueryParser parser = new QueryParser(Version.LUCENE_4_10_0, type, analyzer);
			Query query = parser.parse(searchStr);
			
			System.out.println("1. Sort by relevlance.");
			System.out.println("2. Sort by ID.");
			int sortType = in.nextInt();
			Sort sort = null;
			TopDocs hits = null;
			if (sortType == 1)		//sort by relevlance
			{
				sort = new Sort();
				hits = searcher.search(query, maxTotalNum - 1, sort);
			}
			else if (sortType == 2)		//sort by ID
			{
				sort = new Sort(new SortField("ID", SortField.Type.INT));
				hits = searcher.search(query, maxTotalNum - 1, sort);
			}
			else 
			{
				System.out.println("Invalid input");
				return;
			}
			
			System.out.println("The searching result(s) are: ");
			System.out.println();
			//traverse to print the searching results
			//only print ID and title for simplicity
			for (ScoreDoc doc : hits.scoreDocs)
			{
				Document d = searcher.doc(doc.doc);
				System.out.print(d.get("ID") + ". ");
				System.out.println(d.get("title"));
				System.out.println();
			}
			System.out.println("Returned " + hits.totalHits + " result(s).");
			System.out.println();
		}
		catch(IOException | ParseException e)
		{
			e.printStackTrace();
		}
		
		//in.close();
	}
	
	public static void main(String[] args)
	{
		String filePath = "D:\\Java\\LuceneIndex";
		Scanner input = new Scanner(System.in);
		
		String ID = null;
		String title = null;
		String author = null;
		String year = null;
		String url = null;
		String booktitle = null;
		String address = null;
		String journal = null;
		String publisher = null;
		String abstractString = null;
		
		char type = ' ';
		
		while (true)
		{
			System.out.println("Choose the field that you want to search for.");
			System.out.println("1. Search by ID.");
			System.out.println("2. Search by title.");
			System.out.println("3. Search by author.");
			System.out.println("4. Search by year.");
			System.out.println("5. Search by url.");
			System.out.println("6. Search by booktitle.");
			System.out.println("7. Search by address.");
			System.out.println("8. Search by journal.");
			System.out.println("9. Search by publisher.");
			System.out.println("0. Search by Abstract.");
			System.out.println("#. Exit.");
			
			type = input.next().charAt(0);
			
			switch(type)
			{
			case '1':
				System.out.println("Enter the searching ID.");
				ID = input.next();
				search(filePath, "ID", ID);
				break;
			case '2':
				System.out.println("Enter the searching key word.");
				title = input.next();
				search(filePath, "title", title);
				break;
			case '3':
				System.out.println("Enter the searching key word.");
				author = input.next();
				search(filePath, "author", author);
				break;
			case '4':
				System.out.println("Enter the searching year.");
				year = input.next();
				search(filePath, "year", year);
				break;
			case '5':
				System.out.println("Enter the searching url.");
				url = input.next();
				search(filePath, "url", url);
				break;
			case '6':
				System.out.println("Enter the searching key word.");
				booktitle = input.next();
				search(filePath, "booktitle", booktitle);
				break;
			case '7':
				System.out.println("Enter the searching key word.");
				address = input.next();
				search(filePath, "address", address);
				break;
			case '8':
				System.out.println("Enter the searching key word.");
				journal = input.next();
				search(filePath, "journal", journal);
				break;
			case '9':
				System.out.println("Enter the searching key word.");
				publisher = input.next();
				search(filePath, "publisher", publisher);
				break;
			case '0':
				System.out.println("Enter the searching key word.");
				abstractString = input.next();
				search(filePath, "Abstract", abstractString);
				break;
			case '#':
				System.out.println("Bye.");
				input.close();
				System.exit(0);
			default:
				System.out.println("Invalid input!");
				break;
			}
		}
	}
}
