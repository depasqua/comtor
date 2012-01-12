/**
 *  Comment Mentor: A Comment Quality Assessment Tool
 *  Copyright (C) 2011 The College of New Jersey
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.comtor.analyzers;

import org.comtor.drivers.*;
import com.sun.javadoc.*;
import java.io.*;

import java.util.*;
import java.text.*;

/**
 * The ReadingLevel class is a tool to calculate the Flesh-Kinkade reading level in comments.
 * Flesch-Kincaid Grade Level = 
 *        (0.39* average sentence length in words) + (11.8 * average # syllables per word) - 15.59
 *
 * @author Autumn Breese
 * @author Peter DePasquale
 * @link http://en.wikipedia.org/wiki/Flesch-Kincaid_readability_test
 */
 public class ReadingLevel implements ComtorDoclet
{
	private Properties prop = new Properties();

	// A counter for the classes, used in the properties list
	private int classID = 0;

	/**
	 * Examine each class, its methods, fields, etc. Calculate the length of each item's comments.
	 *
	 * @param rootDoc the root of the documentation tree provided by the Javadoc parser
	 * @return Properties list containing the result set
	 */
	public Properties analyze(RootDoc rootDoc) {
		prop.setProperty("title", "Reading Level");
		DecimalFormat fmt = new DecimalFormat("##0000.000");
		DecimalFormat reportFmt = new DecimalFormat("#####0.000");

		// Initialize variable for reading comments
		String allComments = "";

		// Capture the starting time, just prior to the start of the analysis
		long startTime = new Date().getTime();

		// Extract the list of classes to analyze, and process them.
		ClassDoc[] classes = rootDoc.classes();
		for (ClassDoc classdoc : classes) {
   			classID++;
			prop.setProperty(fmt.format(classID), "Class: " + classdoc.qualifiedName());
			String classComments = processClass(classdoc);
			allComments += classComments;

			// Calculate the F-K score for all comments in this class
			int numWords = countWords(classComments);
			int numSentences = countSentences(classComments);
			int numSyllables = countSyllables(classComments);
			prop.setProperty(fmt.format(classID+.001), "Number of sentences: " + numSentences);
			prop.setProperty(fmt.format(classID+.002), "Number of words: " + numWords);
			prop.setProperty(fmt.format(classID+.003), "Number of syllables: " + numSyllables);
			
			// Check for uncommented results
			double result = 0.0d;
			if (numWords != 0 && numSentences != 0)
				result = calcFKScore((double) numWords / numSentences, (double) numSyllables / numWords);

			prop.setProperty(fmt.format(classID+.004), "Flesch-Kincaid reading level score: " + 
				reportFmt.format(result));
		}

		// Calculate the F-K score for all comments
		int numWords = countWords(allComments);
		int numSentences = countSentences(allComments);
		int numSyllables = countSyllables(allComments);

		// Capture the ending time, just after the termination of the analysis
		long endTime = new Date().getTime();

		prop.setProperty("metric1", "Overall total number of sentences: " + numSentences);
		prop.setProperty("metric2", "Overall total number of words: " + numWords);
		prop.setProperty("metric3", "Overall total number of syllables: " + numSyllables);
		// Check for uncommented results
		double result = 0.0d;
		if (numWords != 0 && numSentences != 0)
			result = calcFKScore((double) numWords / numSentences, (double) numSyllables / numWords);

		prop.setProperty("metric4", "Flesch-Kincaid reading level score: " + 
			reportFmt.format(result));
		prop.setProperty("score", "" + getGrade());
		prop.setProperty("start time", Long.toString(startTime));
		prop.setProperty("end time", Long.toString(endTime));
		prop.setProperty("execution time", Long.toString(endTime - startTime));
		return prop;
	}

	/**
	 * Calculates the Flesch-Kincaid readability score based on the two parameters.
	 *
	 * @param avgSentLng the average length of the sentences (in number of words) for the passage
	 *        in question.
	 * @param avgSyllPerWord the average number of syllables per word for the passage in question.
	 * @return the calculated F-K score
	 */
	public double calcFKScore(double avgSentLng, double avgSyllPerWord) {
		return (0.39 * avgSentLng) + (11.8 * avgSyllPerWord) - 15.59;
	}

	/**
 	 * Processes a single class
	 *
	 * @param class Class to proces
	 * @return String String to which all comments will be written/saved
	 **/
	private String processClass(ClassDoc classDoc) {
		String allComments = "";

		// Process the class' comment
		allComments = parseComment(classDoc.commentText());
		for (Tag tagComment : classDoc.tags())
			allComments += parseComment(tagComment.text());
		
		// Process each constructor and its tag comments
		for (ConstructorDoc constructor : classDoc.constructors()) {
			allComments += parseComment(constructor.commentText());
			
			for (Tag tagComment : constructor.tags())
				allComments += parseComment(tagComment.text());
		}
			
		// Process each fields
		for (FieldDoc field : classDoc.fields())
			allComments += parseComment(field.commentText());

		// Process each method and its tag comments
		for (MethodDoc method : classDoc.methods()) {
			allComments += parseComment(method.commentText());

			for (Tag tagComment : method.tags())
				allComments += parseComment(tagComment.text());
		}

		return allComments;
  	}

	/**
	 * Parses words out of comments and removes punctuation
	 *
	 * @param comment Comment to parse
	 * @return String a string containing all comments are added after processing
	 **/
	private String parseComment(String comment) {
		// Replace parenthesis, brackets, dashes, and periods with spaces
		comment = comment.replaceAll("[()<>-]|\\+"," ");

	
	// Creates sentences out of comments to be analyzed.
		if (comment.length() > 0) {
			char temp = comment.charAt(comment.length() - 1);
			if (temp != '.' && temp != '?' && temp != '!')
				comment += ". ";
		}

		return comment;
	}
	
	/**
	 * Counts the whitespace characters to return an estimate of the number of words.
	 * 
	 * @param source A string of characters
	 * @return the number of words in the input string
	 */
	public static int countWords(String source) {
		int wordCount = 0;
		Scanner scan = new Scanner(source);
		while (scan.hasNext()) {
			wordCount++;
			scan.next();
		}
		return wordCount;
	}
	
	/**
	 * Counts and returns the number of syllables in the given string by counting the number of
	 * vowels.
	 *
	 * @param target the source string which will have its syllables counted
	 * @return the integer value of the number of syllables present in the source string
	 * @see http://www.howmanysyllables.com/howtocountsyllables.html
	 */
	public static int countSyllables(String target) {
		int vowelCount = 0;
		
		// Count the number of standard vowels in the target string
		for (int index = 0; index < target.length(); index++) {
			if (target.charAt(index)=='a' || target.charAt(index)=='e' ||
					target.charAt(index)=='i' || target.charAt(index)=='o' ||
					target.charAt(index)=='u');
				vowelCount++;
		}

		return vowelCount;
	}

	/**
	 * This method returns an estimate of the number of sentences found in the source
	 * string by counting the number of '.','?', and '!' characters.
	 * 
	 * @param source A string of characters
	 * @return the estimated number of sentences
	 */
	public static int countSentences(String source) {
		int numSentences = 0;
		for (int index = 0; index < source.length(); index++)
			if (source.charAt(index) == '.' || source.charAt(index) == '?' ||
					source.charAt(index) == '!')
				numSentences++;
		return numSentences;
	}

	/**
	* Sets the grading breakdown for the doclet.
	*
	* @param section Name of the section to set the max grade for
	* @param maxGrade Maximum grade for the section
	*/
	public void setGradingBreakdown(String section, float maxGrade) {
		// Not needed for this analyzer
	}

	/**
	 * Returns the grade for the doclet.
	 *
	 * @return a float value representing the ratio of correctly spelt words
	 * from comments to the total number of works in the comments.
	 */
	public float getGrade() {
		return 0;
	}

	/**
	 * Sets a parameter used for doclet grading. Through this method, the instructor should be
	 * able to set specific valid words (perhaps the name of their institution, instructor name, etc.
	 * for inclusion in the list.
	 *
	 * @param param Name of the grading parameter
	 * @param value Value of the parameter
	 */
	public void setGradingParameter(String param, String value) {
		// Not needed for this analyzer
	}

	/**
	 * Sets the configuration properties loaded from the config file
	 *
	 * @param props Properties list
	 */
	public void setConfigProperties(Properties props) {
		// Not needed for this analyzer
	}

	/**
	 * Returns a string representation of this object
	 *
	 * @return the string name of this analyzer
	 */
	public String toString() {
		return "Reading Level";
	}
}