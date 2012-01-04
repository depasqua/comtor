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
import java.text.*;

/**
 * The BadWords class is a tool to check for offensive words in comments.
 *
 * @author Peter DePasquale
 */
public final class OffensiveWords implements ComtorDoclet {
	// The analysis report (property list) returned by this doclet
	private Properties prop = new Properties();

	// A hash set that will hold the list of 'bad' (offensive) words found in comments
	private HashSet<String> badWordsList = new HashSet<String>();
	
	// A counter for the classes, used in the properties list
	private int classID = 0;
	
	// Formatter for the report
	private DecimalFormat formatter = new DecimalFormat("##0000.000");
	
	// Counter for the number of offensive words
	private int numBadWords = 0;

	/**
	 * Examine each class and its methods. Calculate the length of each method's comments.
	 *
	 * @param rootDoc the root of the documentation tree provided by the JavaDoc parser
	 * @return Properties list
	 */
	public Properties analyze(RootDoc rootDoc) {
		prop.setProperty("title", "Offensive Words");

		// Load the bad words list into a hashset
		Util.loadDataList("badwords.txt", badWordsList, true, this.getClass().getName());
		
		// Capture the starting time, just prior to the start of the analysis
		long startTime = new Date().getTime();

		// Find bad words in the RootDoc's packages
		for (PackageDoc packageDoc : rootDoc.specifiedPackages()) {
			prop.setProperty(formatter.format(classID), "Package: " + packageDoc.name());
			parseComment(packageDoc.commentText(), 0.000f);
			classID++;
		}
		
		// Parse the comments for each class found in the root doc
		for (ClassDoc classMember : rootDoc.classes()) {
			prop.setProperty(formatter.format(classID), "Class: " + classMember.qualifiedName());
			parseClass(classMember);
			classID++;
			
			// Parse the comments for any/all inner classes of any class
			for (ClassDoc innerClass : classMember.innerClasses()) {
				prop.setProperty(formatter.format(classID), "Class: " + classMember.qualifiedName());
				parseClass(innerClass);
				classID++;
			}
		}

		// Capture the ending time, just after the termination of the analysis
		long endTime = new Date().getTime();

		prop.setProperty("score", "" + getGrade());
		prop.setProperty("start time", Long.toString(startTime));
		prop.setProperty("end time", Long.toString(endTime));
		prop.setProperty("execution time", Long.toString(endTime - startTime));
		prop.setProperty("metric1", "Number of offensive words found: " + numBadWords);
		return prop;
	}
	
	/**
	 * Parses all possible Javadoc comments from the provided class.
	 * 
	 * @pararm classMember a reference to the class (as a ClassDoc object) to parse
	 */
	private void parseClass(ClassDoc classMember) {
		float memberNum = 0.000f;
		parseComment(classMember.commentText(), memberNum);
		
		// Parse the comment for each constructor
		for (ExecutableMemberDoc memberDoc : classMember.constructors()) {
			memberNum += 0.001f;
			prop.setProperty(formatter.format(classID+memberNum), "\tConstructor: " + memberDoc.qualifiedName());
			parseComment(memberDoc.commentText(), memberNum);

			// Parse the parameter comments
			for (ParamTag param : memberDoc.paramTags()) {
				memberNum += 0.001f;
				parseComment(param.parameterComment(), memberNum);
			}
			
			// Parse the parameter comments
			for (ParamTag param : memberDoc.typeParamTags()) {
				memberNum += 0.001f;
				parseComment(param.parameterComment(), memberNum);
			}
			
			// Parse the throws comments
			for (ThrowsTag tag : memberDoc.throwsTags()) {
				memberNum += 0.001f;
				parseComment(tag.exceptionComment(), memberNum);
			}
		}
		
		// Parse the comment for each method
		for (ExecutableMemberDoc memberDoc : classMember.methods()) {
			memberNum += 0.001f;
			prop.setProperty(formatter.format(classID+memberNum), "\tMethod: " + memberDoc.qualifiedName());
			parseComment(memberDoc.commentText(), memberNum);
			
			// Parse the parameter comments
			for (ParamTag param : memberDoc.paramTags()) {
//				memberNum += 0.001f;
				parseComment(param.parameterComment(), memberNum);
			}
			
			// Parse the parameter comments
			for (ParamTag param : memberDoc.typeParamTags()) {
//				memberNum += 0.001f;
				parseComment(param.parameterComment(), memberNum);
			}
			
			// Parse the throws comments
			for (ThrowsTag tag : memberDoc.throwsTags()) {
//				memberNum += 0.001f;
				parseComment(tag.exceptionComment(), memberNum);
			}
			
			// Needs to also fetch @return tag and extract comment (not trivial, see API)
		}
		
		// Parse the comment for each field
		for (FieldDoc field : classMember.fields()) {
			memberNum += 0.001f;
			prop.setProperty(formatter.format(classID+memberNum), "\tField: " + field.qualifiedName());
			parseComment(field.commentText(), memberNum);
		}
		
		// Parse the comment for each enum
		for (FieldDoc field : classMember.enumConstants()) {
			memberNum += 0.001f;
			prop.setProperty(formatter.format(classID+memberNum), "\tEnum: " + field.qualifiedName());
			parseComment(field.commentText(), memberNum);
		}
	}

	/**
	 * Parses the words (tokens) out of comments, removes punctuation, and adds each word to
	 * the word list.
	 *
	 * @param comment The comment string which we wish to parse
	 */
	private void parseComment(String comment, float memberID) {
		String fmtID = formatter.format(((float) classID) + memberID);
		DecimalFormat shortFormatter = new DecimalFormat("000");
		int itemID = 0;

		// Replace parenthesis, brackets, dashes, and periods with spaces
		comment = comment.replaceAll("[\\p{Punct}&&[^']]"," ");

		// Create Scanner to process the comment string
		Scanner scan = new Scanner(comment);
   		while (scan.hasNext()) {
			String word = scan.next().toLowerCase();
			if (badWordsList.contains(word)) {
				String wordID = fmtID + '.' + shortFormatter.format(itemID++);
				numBadWords++;
				prop.setProperty(wordID, word);
			}
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
		if (numBadWords != 0)
			return 100.0f;
		else
			return 0.0f;
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