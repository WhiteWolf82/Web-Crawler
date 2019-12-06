import java.io.*;
import java.util.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;
 
public class LuceneIndex
{
	//the maximum total number + 1
	private static int maxTotalNum = 53746;
	
	//store error IDs
	private static ArrayList<Integer> errorID = new ArrayList<Integer>();
	
	//read file to get the document
	private static Document getDocument(int docID) throws IOException
	{
		String fileName = "D:\\Java\\test\\data\\" + docID + "" + ".txt";
		File file = new File(fileName);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String tmpString = null;
		
		String curID = null;
		String curTitle = null;
		String curAuthor = null;
		String curYear = null;
		String curUrl = null;
		String curBookTitle = null;
		String curAddress = null;
		String curJournal = null;
		String curPublisher = null;
		String curAbstract = null;
		
		//read the file line by line
		tmpString = reader.readLine();
		while (tmpString != null)
		{
			if (tmpString.substring(0, 2).equals("ID"))
			{
				if (!tmpString.substring(4, tmpString.length()).equals("null"))
					curID = tmpString.substring(4, tmpString.length());
				tmpString = reader.readLine();
			}
			else if (tmpString.substring(0, 3).equals("url"))
			{
				if (!tmpString.substring(5, tmpString.length()).equals("null"))
					curUrl = tmpString.substring(5, tmpString.length());
				tmpString = reader.readLine();
			}
			else if (tmpString.substring(0, 4).equals("year"))
			{
				if (!tmpString.substring(6, tmpString.length()).equals("null"))
					curYear = tmpString.substring(6, tmpString.length());
				tmpString = reader.readLine();
			}
			//come to the end, and there is no abstract
			else if (tmpString.substring(0, 4).equals("null"))
			{
				break;
			}
			else if (tmpString.substring(0, 5).equals("title"))
			{
				if (!tmpString.substring(7, tmpString.length()).equals("null"))
					curTitle = tmpString.substring(7, tmpString.length());
				tmpString = reader.readLine();
			}
			else if (tmpString.substring(0, 6).equals("author"))
			{
				if (!tmpString.substring(8, tmpString.length()).equals("null"))
					curAuthor = tmpString.substring(8, tmpString.length());
				tmpString = reader.readLine();
			}
			else if (tmpString.substring(0, 7).equals("address"))
			{
				if (!tmpString.substring(9, tmpString.length()).equals("null"))
					curAddress = tmpString.substring(9, tmpString.length());
				tmpString = reader.readLine();
			}
			else if (tmpString.substring(0, 7).equals("journal"))
			{
				if (!tmpString.substring(9, tmpString.length()).equals("null"))
					curJournal = tmpString.substring(9, tmpString.length());
				tmpString = reader.readLine();
			}
			else if (tmpString.substring(0, 8).equals("Abstract"))
			{
				curAbstract = tmpString.substring(9, tmpString.length());
				//while there are more lines of Abstract
				while ((tmpString = reader.readLine()) != null)
				{
					curAbstract += tmpString;
				}
				//come to the end of file
				break;
			}
			else if (tmpString.substring(0, 9).equals("booktitle"))
			{
				if (!tmpString.substring(11, tmpString.length()).equals("null"))
					curBookTitle = tmpString.substring(11, tmpString.length());
				tmpString = reader.readLine();
			}
			else if (tmpString.substring(0, 9).equals("publisher"))
			{
				if (!tmpString.substring(11, tmpString.length()).equals("null"))
					curPublisher = tmpString.substring(11, tmpString.length());
				tmpString = reader.readLine();
			}
			else
				continue;
		}
		
		reader.close();
		
		//create a new document class
		Document doc = new Document();
		Field idField = null;
		Field titleField = null;
		Field authorField = null;
		Field yearField = null;
		Field urlField = null;
		Field btField = null;
		Field addrField = null;
		Field jourField = null;
		Field pubField = null;
		Field absField = null;
		
		//if not null, store the field into the document
		if (curID != null)
		{
			idField = new StringField("ID", curID, Store.YES);
			doc.add(idField);
		}
		if (curTitle != null)
		{
			titleField = new TextField("title", curTitle, Store.YES);
			doc.add(titleField);
		}
		if (curAuthor != null)
		{
			authorField = new TextField("author", curAuthor, Store.YES);
			doc.add(authorField);
		}
		if (curYear != null)
		{
			yearField = new StringField("year", curYear, Store.YES);
			doc.add(yearField);
		}
		if (curUrl != null)
		{
			urlField = new StringField("url", curUrl, Store.YES);
			doc.add(urlField);
		}
		if (curBookTitle != null)
		{
			btField = new TextField("booktitle", curBookTitle, Store.YES);
			doc.add(btField);
		}
		if (curAddress != null)
		{
			addrField = new TextField("address", curAddress, Store.YES);
			doc.add(addrField);
		}
		if (curJournal != null)
		{
			jourField = new TextField("journal", curJournal, Store.YES);
			doc.add(jourField);
		}
		if (curPublisher != null)
		{
			pubField = new TextField("publisher", curPublisher, Store.YES);
			doc.add(pubField);
		}
		if (curAbstract != null)
		{
			absField = new TextField("Abstract", curAbstract, Store.YES);
			doc.add(absField);
		}

		return doc;
	}
	
	//create index
	private static void createIndex(String filePath)
	{
		File file = new File(filePath);
		IndexWriter iwr = null;
		Document doc= null;
		
		try
		{
			Directory dir = FSDirectory.open(file);
			Analyzer analyzer = new IKAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_0, analyzer);
			iwr = new IndexWriter(dir, config);
			//create index for each article
			for (int i = 1; i <= maxTotalNum - 1; i++)
			{
				try
				{
					doc = getDocument(i);
					iwr.addDocument(doc);
					System.out.println("Index " + i);
				} 
				catch (IOException e)
				{
					System.out.println("IOException happens in ID " + i);
					if (!errorID.contains(i))
					{
						errorID.add(i);
						continue;
					}
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			iwr.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		createIndex("D:\\Java\\LuceneIndex");
		
		//print the error IDs to let the developer handle them
		if (errorID.size() != 0)
		{
			System.out.println("Unfinished IDs are: ");
			for (int i = 0; i < errorID.size(); i++)
			{
				System.out.println(errorID.get(i));
			}
		}
		else 
		{
			System.out.println("All finished.");
		}
	}
}
