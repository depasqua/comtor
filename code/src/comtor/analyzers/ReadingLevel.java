/***************************************************************************
  *  Comment Mentor: A Comment Quality Assessment Tool
  *  Copyright (C) 2005 Michael E. Locasto
  *
  *  This program is free software; you can redistribute it and/or modify
  *  it under the terms of the GNU General Public License as published by
  *  the Free Software Foundation; either version 2 of the License, or
  *  (at your option) any later version.
  *
  *  This program is distributed in the hope that it will be useful, but
  *  WITHOUT ANY WARRANTY; without even the implied warranty of
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  *  General Public License for more details.
  *
  *  You should have received a copy of the GNU General Public License
  *  along with this program; if not, write to the:
  *  Free Software Foundation, Inc.
  *  59 Temple Place, Suite 330
  *  Boston, MA  02111-1307  USA
  **************************************************************************/

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
	
	//Constructor.
	public ReadingLevel(){
	};

	public Properties analyze(RootDoc rootDoc)
	{
	  	//Doclet Title
	  	prop.setProperty("title", "Reading Level");
	
		// Initializing variables for reading files
		String line;
		int next;
		String allFiles = "";
		String allComments = "";
		
		//Pulling out all of the classes to analyze, and processing them.
		ClassDoc[] classes = rootDoc.classes();
    		for(int i = 0; i < classes.length; i++)
      			allComments += processClass(classes[i]);
			
		// 1) Count sentences
		// 2) Count words
		// 3) Count syllables
		int numSentences = countSentences(allComments);
		int numWords = countWords(allComments);
		int numSyllables = countSyllables(allComments);
		//Flesch-Kincaid Grade Level = 
			//(0.39* Average Sentence Length)+(11.8*Average Syllables per Word)-15.59
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

		prop.setProperty("000.000.000", "Average sentence length: " 
			+ fmt.format(avgSentenceLength));
		prop.setProperty("000.000.001", "Average syllables per word: " 
			+ fmt.format(avgSyllablesPerWord));
		prop.setProperty("000.000.002", "Total number of sentences: " 
			+ fmt.format(numSentences));
		prop.setProperty("000.000.003", "Total number of words: " 
			+ fmt.format(numWords));
		prop.setProperty("000.000.004", "Total number of syllables: " 
			+ fmt.format(numSyllables));
		prop.setProperty("000.000.005", "Flesch-Kincaid Grade Level: " 
			+ fmt.format(FleschKincaid));
		prop.setProperty("score", "" + getGrade());

	    return prop;	
	}
	
 	/*************************************************************************
 	 * Processes a single class
 	 *
 	 * @param class Class to process
 	 * @return String String to which all comments will be written/saved
 	 *************************************************************************/
	private String processClass(ClassDoc classDoc)
	{
		String allComments = "";

		// Add class comment
		allComments = parseComment(classDoc.commentText());

		// Get all fields
		FieldDoc[] fields = classDoc.fields();
		for(int h = 0; h < fields.length; h++)
	    	{
      			// Add field comment
      			allComments += parseComment(fields[h].commentText());
    		}

    		// Get inner classes
    		ClassDoc[] inner_classes = classDoc.innerClasses();
    		for(int h = 0; h < inner_classes.length; h++)
      			processClass(inner_classes[h]);

    		// Get comments for tags
    		Tag[] tags = classDoc.tags();
    		for (int h = 0; h < tags.length; h++)
      			allComments += parseComment(tags[h].text());

    		// Get comments for each method
    		MethodDoc[] methods = classDoc.methods();
    		for(int j = 0; j < methods.length; j++)
    		{
      			// Get comments for tags
      			tags = methods[j].tags();
      			for (int h = 0; h < tags.length; h++)
        			allComments += parseComment(tags[h].text());

      			// Get method comments
      			allComments += parseComment(methods[j].commentText());
    		}
    
    		return allComments;
  	}

	/*************************************************************************
	 * Parses words out of comments and removes punctuation
	 *
	 * @param comment Comment to parse
	 * @return String String to which all comments are added after processing
	 *************************************************************************/
 	private String parseComment(String comment)
	{
		char temp;
    		// Replace parenthesis, brackets, dashes, and periods with spaces
    		comment = comment.replaceAll("[()<>-]|\\+"," ");

		//Creates sentences out of comments to be analyzed.
		if (comment.length() > 0){
			temp = comment.charAt(comment.length() - 1);
    			if (temp == '.' || temp == '?' || temp == '!');
   			else
    				comment += '.';
    		}

		// Comment added to allComments to be analyzed for reading level.  
		return comment;
  	}

	// At present it simply counts the number of '.','?', and '!' 
	//characters to estimate number of sentences.
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

	//At present it simply counts the number of '.','?', and '!' 
	//characters to estimate number of sentences.
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
		//Not needed.
	}

	public float getGrade()
	{
		return 0;
	}

	public void setGradingParameter(String param, String value)
	{
		//Not needed.
	}

	public void setConfigProperties(Properties props)
	{
		//Not needed.
	}	

}
