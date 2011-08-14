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

import com.sun.javadoc.*;
import java.util.*;

/**
 * The BadWords class is a tool to check for offensive words in comments.
 *
 * @author Peter DePasquale
 */
public final class OffensiveWords implements ComtorDoclet {
	// The analysis report (property list) returned by this doclet
	private Properties prop = new Properties();

	private HashSet<String> badWordsList = new HashSet<String>();
	
	/**
	 * Examine each class and its methods. Calculate the length of each method's comments.
	 *
	 * @param rootDoc the root of the documentation tree provided by the JavaDoc parser
	 * @return Properties list
	 */
	public Properties analyze(RootDoc rootDoc) {
		prop.setProperty("title", "Spell Checker");

		// Load the bad words list into a hashset
		Util.loadDataList("badwords.txt", badWordsList);
		
		// Find bad words in the RootDoc's comments
		parseComment(rootDoc.commentText());

		// Find bad words in the RootDoc's packages
		for (PackageDoc packageDoc : rootDoc.specifiedPackages())
			parseComment(packageDoc.commentText());
		
		// Parse the comments for each class found in the root doc
		for (ClassDoc classMember : rootDoc.classes()) {
			parseClass(classMember);
			
			// Parse the comments for any/all inner classes of any class
			for (ClassDoc innerClass : classMember.innerClasses())
				parseClass(innerClass);
		}

		return prop;
	}
	
	/**
	 * Parses all possible Javadoc comments from the provided class.
	 * 
	 * @pararm classMember a reference to the class (as a ClassDoc object) to parse
	 */
	private void parseClass(ClassDoc classMember) {
		parseComment(classMember.commentText());
		
		// Parse the comment for each constructor
		for (ExecutableMemberDoc memberDoc : classMember.constructors()) {
			parseComment(memberDoc.commentText());

			// Parse the parameter comments
			for (ParamTag param : memberDoc.paramTags()) {
				parseComment(param.parameterComment());
			}
			
			// Parse the parameter comments
			for (ParamTag param : memberDoc.typeParamTags()) {
				parseComment(param.parameterComment());
			}
			
			// Parse the throws comments
			for (ThrowsTag tag : memberDoc.throwsTags()) {
				parseComment(tag.exceptionComment());
			}
		}
		
		// Parse the comment for each method
		for (ExecutableMemberDoc memberDoc : classMember.methods()) {
			parseComment(memberDoc.commentText());
			
			// Parse the parameter comments
			for (ParamTag param : memberDoc.paramTags()) {
				parseComment(param.parameterComment());
			}
			
			// Parse the parameter comments
			for (ParamTag param : memberDoc.typeParamTags()) {
				parseComment(param.parameterComment());
			}
			
			// Parse the throws comments
			for (ThrowsTag tag : memberDoc.throwsTags()) {
				parseComment(tag.exceptionComment());
			}
			
			// Needs to also fetch @return tag and extract comment (not trivial, see API)
		}
		
		// Parse the comment for each field
		for (FieldDoc field : classMember.fields()) {
			parseComment(field.commentText());
		}
		
		// Parse the comment for each enum
		for (FieldDoc field : classMember.enumConstants()) {
			parseComment(field.commentText());
		}
	}

	/**
	 * Parses the words (tokens) out of comments, removes punctuation, and adds each word to
	 * the word list.
	 *
	 * @param comment The comment string which we wish to parse
	 */
	private void parseComment(String comment) {
		// Replace parenthesis, brackets, dashes, and periods with spaces
		comment = comment.replaceAll("[\\p{Punct}&&[^']]"," ");

		// Create Scanner to process the comment string
		Scanner scan = new Scanner(comment);
   		while (scan.hasNext()) {
			String word = scan.next().toLowerCase();
			if (badWordsList.contains(word))
				System.out.println("Bad word: " + word);
		}
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
		return -1.0f;
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
		return "BadWords";
	}
}