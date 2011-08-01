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
package comtor.analyzers;

import comtor.*;
import com.sun.javadoc.*;
import java.util.regex.*;
import java.io.*;

import java.util.*;
import java.text.*;

public class ReadingLevel implements ComtorDoclet
{
	private Properties prop = new Properties();

	public Properties analyze(RootDoc rootDoc) {
		prop.setProperty("title", "Reading Level");

		// Initialize variable for reading comments
		String allComments = "";

		// Extract the list of classes to analyze, and process them.
		ClassDoc[] classes = rootDoc.classes();
		for (ClassDoc classdoc : classes)
			allComments += processClass(classdoc);

		// Count the number of sentences, words, and syllables
		int numWords = countWords(allComments);
		int numSentences = countSentences(allComments);
		int numSyllables = countSyllables(allComments);

		// See http://en.wikipedia.org/wiki/Flesch-Kincaid_readability_test
		// Flesch-Kincaid Grade Level =
		//		(0.39* Average Sentence Length) + (11.8 * Average Syllables / Word) - 15.59
		double avgSentenceLength = (double) numWords / numSentences;
		double avgSyllablesPerWord = (double) numSyllables / numWords;
		double fkScore = (0.39 * avgSentenceLength) + (11.8 * avgSyllablesPerWord) - 15.59;

		DecimalFormat fmt = new DecimalFormat("#0.00");
		prop.setProperty("000.000.000", "Average sentence length: " + fmt.format(avgSentenceLength));
		prop.setProperty("000.000.001", "Average syllables per word: " + fmt.format(avgSyllablesPerWord));
		prop.setProperty("000.000.002", "Total number of sentences: " + fmt.format(numSentences));
		prop.setProperty("000.000.003", "Total number of words: " + fmt.format(numWords));
		prop.setProperty("000.000.004", "Total number of syllables: " + fmt.format(numSyllables));
		prop.setProperty("000.000.005", "Flesch-Kincaid Grade Level: " + fmt.format(fkScore));
		prop.setProperty("score", "" + getGrade());
		return prop;
	}
	
	/**
 	 * Processes a single class
	 *
	 * @param class Class to proces
	 * @return String String to which all comments will be written/saved
	 **/
	private String processClass(ClassDoc classDoc) {
		String allComments = "";

		// Add class comment
		allComments = parseComment(classDoc.commentText());

		// Get all fields
		FieldDoc[] fields = classDoc.fields();
		for (FieldDoc field : fields)
			// Add field comment
			allComments += parseComment(field.commentText());

		// Get inner classes
		ClassDoc[] innerClasses = classDoc.innerClasses();
		for (ClassDoc innerClass : innerClasses)
			processClass(innerClass);

		// Get comments for tags
		Tag[] tags = classDoc.tags();
		for (Tag tag : tags)
			allComments += parseComment(tag.text());

		// Get comments for each method
		MethodDoc[] methods = classDoc.methods();
		for (MethodDoc method : methods) {
			// Get comments for tags
			tags = method.tags();
			for (Tag tag : tags)
				allComments += parseComment(tag.text());

			// Get method comments
			allComments += parseComment(method.commentText());
		}
		return allComments;
  	}

	/**
	 * Parses words out of comments and removes punctuation
	 *
	 * @param comment Comment to parse
	 * @return String String to which all comments are added after processing
	 **/
	private String parseComment(String comment) {
		char temp;
		// Replace parenthesis, brackets, dashes, and periods with spaces
		comment = comment.replaceAll("[()<>-]|\\+"," ");

	
	// Creates sentences out of comments to be analyzed.
		if (comment.length() > 0) {
			temp = comment.charAt(comment.length() - 1);
			if (temp == '.' || temp == '?' || temp == '!')
				;
			else
				comment += '.';
		}


		// Comment added to allComments to be analyzed for reading level.
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
		Matcher match = Pattern.compile("[ ]+").matcher(source);
		while (match.find())
			wordCount++;
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
			char targetChar = target.charAt(index);
			if (targetChar=='a' || targetChar=='e' || targetChar=='i' ||
					targetChar=='o' || targetChar=='u')
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