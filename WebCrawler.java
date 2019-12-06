import java.io.*;
import java.util.*;
import java.net.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class WebCrawler
{	
	//the maximum total number + 1
	static int maxTotalNum = 53746;
	
	//download the pdf file by the url
	private static void downloadPdf(String urlStr, String savePath) throws IOException
	{
		URL url = new URL(urlStr);
		//open a new connection
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		//get the input stream
		InputStream input = conn.getInputStream();
		
		//get the byte array from the input stream
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int len = 0;
		while ((len = input.read(buffer)) != -1)
		{
			bos.write(buffer, 0, len);
		}
		byte[] data = bos.toByteArray();
		bos.close();
		input.close();
		
		//write data to file
		File file = new File(savePath);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data);
		fos.close();
		
		System.out.println("Downloaded: " + savePath);
	}
	
	//get the name of the pdf file by the url
	private static String getFileName(String url)
	{
		int index;
		for (index = url.length() - 1; index >= 0; index--)
		{
			if (url.charAt(index) == '/')
				break;
		}
		return url.substring(index + 1);
	}
	
	public static void main(String[] args) throws IOException
	{
		File bibFile = new File("D:\\Java\\test\\anthology.bib");
		if (!bibFile.exists())
		{
			System.out.println("File does not exist!");
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(bibFile));
		String tmpString = null;
		int curNum = 1;
		
		//arrays to store different fields
		String[] types = new String[maxTotalNum];
		String[] titles = new String[maxTotalNum];
		String[] authors = new String[maxTotalNum];
		String[] years = new String[maxTotalNum];
		String[] urls = new String[maxTotalNum];
		String[] booktitles = new String[maxTotalNum];
		String[] addresses = new String[maxTotalNum];
		String[] journals = new String[maxTotalNum];
		String[] publishers = new String[maxTotalNum];
		
		//initialize arrays
		for (int i = 0; i < maxTotalNum; i++)
		{
			types[i] = null;
			titles[i] = null;
			authors[i] = null;
			years[i] = null;
			urls[i] = null;
			booktitles[i] = null;
			addresses[i] = null;
			journals[i] = null;
			publishers[i] = null;
		}
		
		String curTitle = null;
		String curAuthor = null;
		String curYear = null;
		String curUrl = null;
		String curBookTitle = null;
		String curAddress = null;
		String curJournal = null;
		String curPublisher = null;
		
		//read data from the bib file
		while ((tmpString = reader.readLine()) != null)
		{
			//System.out.println(tmpString);
			//@inproceedings type
			if ((tmpString.substring(0, 14)).equals("@inproceedings"))
				types[curNum] = "@inproceedings";
			//@proceedings type
			else if ((tmpString.substring(0, 12)).equals("@proceedings"))
				types[curNum] = "@proceedings";
			//@article type
			else if ((tmpString.substring(0, 8)).equals("@article"))
				types[curNum] = "@article";
			else
				continue;
			
			tmpString = reader.readLine();
			
			//deal with each data block
			while (!tmpString.equals("}"))
			{
				//System.out.println(tmpString);
				if ((tmpString.substring(4, 9)).equals("title"))
				{
					curTitle = tmpString.substring(13, tmpString.length() - 2);
					titles[curNum] = curTitle;
					tmpString = reader.readLine();
				}
				else if ((tmpString.substring(4, 10)).equals("author"))
				{
					curAuthor = tmpString.substring(14, tmpString.length());
					tmpString = reader.readLine();
					//while there are more authors
					while (tmpString.charAt(4) == ' ')
					{
						curAuthor += tmpString.substring(5, tmpString.length());
						tmpString = reader.readLine();
					}
					curAuthor = curAuthor.substring(0, curAuthor.length() - 2);
					authors[curNum] = curAuthor;
				}
				else if ((tmpString.substring(4, 8)).equals("year"))
				{
					curYear = tmpString.substring(12, tmpString.length() - 2);
					years[curNum] = curYear;
					tmpString = reader.readLine();
				}
				else if ((tmpString.substring(4, 7)).equals("url"))
				{
					curUrl = tmpString.substring(11, tmpString.length() - 2);
					urls[curNum] = curUrl;
					tmpString = reader.readLine();
				}
				else if ((tmpString.substring(4, 13)).equals("booktitle"))
				{
					curBookTitle = tmpString.substring(17, tmpString.length() - 2);
					booktitles[curNum] = curBookTitle;
					tmpString = reader.readLine();
				}
				else if ((tmpString.substring(4, 11)).equals("address"))
				{
					curAddress = tmpString.substring(15, tmpString.length() - 2);
					addresses[curNum] = curAddress;
					tmpString = reader.readLine();
				}
				else if ((tmpString.substring(4, 13)).equals("publisher"))
				{
					curPublisher = tmpString.substring(17, tmpString.length() - 2);
					publishers[curNum] = curPublisher;
					tmpString = reader.readLine();
				}
				else if ((tmpString.substring(4, 11)).equals("journal"))
				{
					curJournal = tmpString.substring(15, tmpString.length() - 2);
					journals[curNum] = curJournal;
					tmpString = reader.readLine();
				}
				else
				{
					tmpString = reader.readLine();
				}
			}
			
			System.out.println("Read " + curNum);
			curNum++;
		}
		
		reader.close();
		
		String folderName = "D:\\Java\\test\\data";
		File writeFolder = new File(folderName);	//article data folder
		if (!writeFolder.exists())
			writeFolder.mkdir();
		File pdfFolder = new File("D:\\Java\\test\\pdf");	//pdf file folder
		if (!pdfFolder.exists())
			pdfFolder.mkdir();
		
		//store IDs of articles whose url is not correct or unable to access
		ArrayList<Integer> errorID = new ArrayList<Integer>();
		//store error IDs in a file named "errorID"
		File errorIDFile = new File("D:\\Java\\test\\errorID");
		
		loop1:
		for (int i = 1; i <= maxTotalNum - 1; i++)
		{
			//System.out.println(urls[i]);
			String articleAbstract = null;
			Document doc = null;
			if (urls[i] != null)
			{
				try
				{
					doc = Jsoup.connect(urls[i]).get();
				}
				catch(IOException e)
				{
					System.out.println("IOException happens in ID " + i);
					//write and store the error ID
					if (!errorID.contains(i))
					{
						errorID.add(i);
						FileWriter errorWriter = new FileWriter(errorIDFile, true);
						errorWriter.write(i + "" + "\n");
						errorWriter.close();
					}
					//parse next url first
					continue loop1;
				}
				
				//get abstract
				Elements absElements = doc.select("div.card-body.acl-abstract");
				for (Element element : absElements)
				{
					articleAbstract = element.text();
					break;	//there is only one abstract for each article
				}
				
				//download pdf
				if (i >= 32501 && i <= 33000)
				{
					Elements hrefElements = doc.select("a[href]");
					loop2:
					for (Element element : hrefElements)
					{
						if (element.html().indexOf("pdf") != -1)
						{
							String pdfPath = pdfFolder + "\\" + getFileName(urls[i]) + ".pdf";
							try
							{
								downloadPdf(element.html(), pdfPath);
							}
							catch(IOException e)
							{
								System.out.println("IOException happens in downloading pdf " + i);
								//write and store the error ID
								if (!errorID.contains(i))
								{
									errorID.add(i);
									FileWriter errorWriter = new FileWriter(errorIDFile, true);
									errorWriter.write(i + "" + "\n");
									errorWriter.close();
								}
								//parse next url first
								continue loop1;
							} 
						}
					}
				}
			}
			//write the data into a file named "i.txt"
			String txtName = folderName + "\\" + i + "" + ".txt";
			File txtFile = new File(txtName);
			FileWriter writer = new FileWriter(txtFile);
			writer.write("ID: " + i + "" + "\n");
			writer.write("title: " + titles[i] + "\n");
			writer.write("author: " + authors[i] + "\n");
			writer.write("year: " + years[i] + "\n");
			writer.write("url: " + urls[i] + "\n");
			writer.write("booktitle: " + booktitles[i] + "\n");
			writer.write("address: " + addresses[i] + "\n");
			writer.write("journal: " + journals[i] + "\n");
			writer.write("publisher: " + publishers[i] + "\n");
			writer.write(articleAbstract + "\n");
			writer.close();
			System.out.println("Write " + i);
		}
		
		//print the error IDs to let the developer handle them
		if (errorID.size() != 0)
		{
			System.out.println("Unfinished IDs are: ");
			for (int j = 0; j < errorID.size(); j++)
			{
				System.out.println(errorID.get(j));
			}
		}
		else 
		{
			System.out.println("All finished.");
		}
			
	}
}
