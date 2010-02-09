package comtor.analyzers;

import comtor.*;
import com.sun.javadoc.*;
import java.util.regex.*;
import java.util.*;
import java.text.*;
import java.io.*;

public class ReadingLevel implements ComtorDoclet
{
	private Properties prop = new Properties();

	public Properties analyze(RootDoc root)
	{
	    prop.setProperty("title", "Reading Level"); //doclet title
	    try
	    {
	
		String dir = System.getProperty("user.dir");
		System.out.println("User directory: " + dir);
		BufferedReader fileNames =  new BufferedReader(new FileReader(dir+"/source.txt"));
		// Initializing variables for reading files
		String line;
		int next;
		String allFiles = "";
		// Read in all files
		while((line = fileNames.readLine()) != null)
		{
			BufferedReader br = new BufferedReader(new FileReader(line));
			next = br.read();
			while(next != -1)
			{
				allFiles = allFiles + (char)next;
				next = br.read();
			}
		}
		// Create a pattern to match comments and run matches
		Pattern ml = Pattern.compile("//.*|(s?)/[*][^([*]/)]*[*]/");
		Matcher match = ml.matcher(allFiles);
		// Pull out all comments
		String matched = "";
		char temp;
		String allComments = "";
	    	while(match.find())
		{
			matched = match.group();
			// Clean up comments found.
			// Multiline, trim off '*/' at the end of the comment
			if(matched.charAt(1)=='*') 
				matched = matched.substring(0,matched.length()-2);

			// Trim off '//' or '/*' from front of comment
			matched = matched.substring(2);
			// Eliminate trailing and leading whitespace
			matched = matched.trim();

			/* Makes comment a complete sentence for the reading algorithm;
			   later sentence detection algorithms will be used.
			   This is a hack for now. */	
			if(matched.length() > 0)
			{
				temp = matched.charAt(matched.length()-1);
				if(temp == '.' || temp == '?' || temp == '!'); // Do nothing
				else	
				matched = matched + ". ";
				// Removes any newlines and tabs from the string

				matched = matched.replace('\n',' ');
				matched = matched.replace('\t','\0');

				// Comment added to allComments to be analyzed for reading level.  
				allComments +=matched;
			}
		}
		// 1) Count sentences
		// 2) Count words
		// 3) Count syllables
		int numSentences = countSentences(allComments);
		int numWords = countWords(allComments);
		int numSyllables = countSyllables(allComments);
		//Flesch-Kincaid Grade Level = (0.39* Average Sentence Length)+(11.8*Average Syllables per Word)-15.59
		double avgSentenceLength = (double)numWords/numSentences;
		double avgSyllablesPerWord = (double)numSyllables/numWords;
		double FleschKincaid = (0.39*avgSentenceLength)+(11.8*avgSyllablesPerWord)-15.59;
		System.out.println("AverageSentenceLength: " + avgSentenceLength);	
		System.out.println("AverageSyllablesPerWord: " + avgSyllablesPerWord);
		System.out.println("Total sentences: " + numSentences);
		System.out.println("Total words: " + numWords);
		System.out.println("Total syllables: " + numSyllables);
		System.out.println("Flesch-Kincaid Grade Level = " + FleschKincaid);
	
		DecimalFormat fmt = new DecimalFormat("#0.00");

		prop.setProperty("000.000.000", "AverageSentanceLength: " + fmt.format(avgSentenceLength));
		prop.setProperty("000.000.001", "AverageSyllablesPerWord: " + fmt.format(avgSyllablesPerWord));
		prop.setProperty("000.000.002", "Total sentences: " + fmt.format(numSentences));
		prop.setProperty("000.000.003", "Total words: " + fmt.format(numWords));
		prop.setProperty("000.000.004", "Total syllables: " + fmt.format(numSyllables));
		prop.setProperty("000.000.005", "Flesch-Kincaid Grade Level = " + fmt.format(FleschKincaid));
		prop.setProperty("score", "" + getGrade());
	    }
	    catch(Exception e)
	    {
		System.out.println(e);
	    }

	    return prop;	
	}

	// At present it simply counts the number of '.','?', and '!' characters to estimate number of sentences.
	public static int countWords(String text)
	{
		int count = 0;
		Pattern word = Pattern.compile("[ ]+");  
	        Matcher match = word.matcher(text);
		while(match.find())		
			count++;
		return count;
	}

	// Counts syllables by counting the number of vowels.
	public static int countSyllables(String text)
	{
		int count = 0;
		char c;		
		for(int i = 0; i < text.length(); i++)
		{
			c = text.charAt(i);
			if(c=='a' || c=='e' || c=='i' || c=='o' || c=='u')
				count++;
		}
		return count;
	}

	// At present it simply counts the number of '.','?', and '!' characters to estimate number of sentences.
	public static int countSentences(String text)
	{
		int count = 0;
		char c;		
		for(int i = 0; i < text.length(); i++)
		{
			c = text.charAt(i);
			if(c == '.' || c == '?' || c == '!')
				count++;
		}

		return count;
	}

	public void setGradingBreakdown(String section, float maxGrade)
	{

	}

	public float getGrade()
	{
		return 0;
	}

	public void setGradingParameter(String param, String value)
	{

	}

	public void setConfigProperties(Properties props)
	{

	}	

}
