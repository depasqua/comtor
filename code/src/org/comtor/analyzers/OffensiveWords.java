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

import com.sun.javadoc.*;
import org.comtor.reporting.*;
import java.util.*;
import java.text.*;

/**
 * The BadWords class is a tool to check for offensive words in comments.
 *
 * @author Peter DePasquale
 */
public final class OffensiveWords implements ComtorDoclet {
	// JSON analysis report from this module's execution
	ModuleReport report = new ModuleReport ("Offensive Words", "This module identifies \"offensive\" English words " +
		"used in comments at the class, method, and field level. Such usage is considered to be unprofessional.");

	// Hash sets that hold the list of 'bad' (offensive) words found in comments and the unique words processed
	private HashSet<String> badWordsList = new HashSet<String>();
	private HashSet<String> uniqueWordList = new HashSet<String>();
	
	// Counters for the packages, classes, and members
	private int numPackages = 0;
	private int numClasses = 0;
	private int numConstructors = 0;
	private int numMethods = 0;
	private int numParameters = 0;
	private int numFields = 0;
	
	// Counter for the number of offensive words
	private int numBadWords = 0;
	private int totalNumWords = 0;

	/**
	 * Examine each class and its methods. Calculate the length of each method's comments.
	 *
	 * @param rootDoc the root of the documentation tree provided by the JavaDoc parser
	 * @return Properties list
	 */
	public Properties analyze(RootDoc rootDoc) {

		// Load the bad words list into a hashset
		Util.loadDataList("badwords.txt", badWordsList, true, this.getClass().getName());
		
		// Capture the starting time, just prior to the start of the analysis
		long startTime = new Date().getTime();

		// Find bad words in the RootDoc's packages
		for (PackageDoc packageDoc : rootDoc.specifiedPackages()) {
			numPackages++;
			report.addItem(ReportItem.PACKAGE, packageDoc.name());
			parseComment(packageDoc.commentText(), packageDoc.position());
		}
		
		// Parse the comments for each class found in the root doc
		for (ClassDoc classMember : rootDoc.classes()) {
			numClasses++;
			report.addItem(ReportItem.CLASS, classMember.qualifiedName());
			parseClass(classMember);
			
			// Note: There is no need to process the inner classes, they are provided
			// via the .classes() call above.
		}

		// Capture the ending time, just after the termination of the analysis
		long endTime = new Date().getTime();

		report.appendLongToObject("information", "packages processed", numPackages);
		report.appendLongToObject("information", "classes processed", numClasses);
		report.appendLongToObject("information", "constructors processed", numConstructors);
		report.appendLongToObject("information", "methods processed", numMethods);
		report.appendLongToObject("information", "parameters processed", numParameters);
		report.appendLongToObject("information", "fields processed", numFields);
		report.appendLongToObject("information", "total number of words processed", totalNumWords);
		report.addMetric(numPackages + " package(s) were processed.");
		report.addMetric(numClasses + " class(es) were processed.");
		report.addMetric(numConstructors + " constructor(s) were processed.");
		report.addMetric(numMethods + " method(s) were processed.");
		report.addMetric(numParameters + " parameter(s) were processed.");
		report.addMetric(numFields + " field(s) were processed.");
		report.addMetric(totalNumWords + " words were processed (including duplicates).");
		report.addMetric(uniqueWordList.size() + " unique words were processed (excludes duplicates).");
		report.addMetric(numBadWords + " 'offensive' words were identified (including duplicates).");
		report.appendToAmble("preamble", "The @return tag is currently not processed.");

		report.addTimingString("start time", Long.toString(startTime));
		report.addTimingString("end time", Long.toString(endTime));
		report.addTimingString("execution time", Long.toString(endTime - startTime));

		return null;
	}
	
	/**
	 * Parses all possible Javadoc comments from the provided class.
	 * 
	 * @pararm classMember a reference to the class (as a ClassDoc object) to parse
	 */
	private void parseClass(ClassDoc classMember) {
		parseComment(classMember.commentText(), classMember.position());
		
		// Parse the comment for each constructor
		for (ConstructorDoc memberDoc : classMember.constructors()) {
			// Do not process synthetic (compiler-produced) constructors
			if (memberDoc.position() != null) {
				report.addItem(ReportItem.CONSTRUCTOR, memberDoc.name() + memberDoc.flatSignature());
				parseComment(memberDoc.commentText(), memberDoc.position());
				numConstructors++;

				// Parse the parameter's comments (@param tag)
				for (ParamTag param : memberDoc.paramTags()) {
					report.addItem(ReportItem.PARAMETER, param.parameterName());
					parseComment(param.parameterComment(), param.position());
					numParameters++;
				}

				// Parse the throws comments (@throws tag)
				for (ThrowsTag tag : memberDoc.throwsTags()) {
					report.addItem(ReportItem.THROWS, tag.exceptionName());
					parseComment(tag.exceptionComment(), tag.position());
				}
			} else
				System.err.println("null position found: " + memberDoc.name() + memberDoc.flatSignature());
		}
		
		// Parse the comment for each method
		for (ExecutableMemberDoc memberDoc : classMember.methods()) {
			report.addItem(ReportItem.METHOD, memberDoc.name() + memberDoc.flatSignature());
			parseComment(memberDoc.commentText(), memberDoc.position());
			numMethods++;
			
			// Parse the parameter's comments (@param tag)
			for (ParamTag param : memberDoc.paramTags()) {
				report.addItem(ReportItem.PARAMETER, param.parameterName());
				parseComment(param.parameterComment(), param.position());
				numParameters++;
			}
			
			// Parse the throws comments (@throws tag)
			for (ThrowsTag tag : memberDoc.throwsTags()) {
				report.addItem(ReportItem.THROWS, tag.exceptionName());
				parseComment(tag.exceptionComment(), tag.position());
			}
			
			// Needs to also fetch @return tag and extract comment (not trivial, see API)
		}
		
		// Parse the comment for each field
		for (FieldDoc field : classMember.fields()) {
			report.addItem(ReportItem.FIELD, field.name());
			parseComment(field.commentText(), field.position());
			numFields++;
		}
		
		// Parse the comment for each enum
		for (FieldDoc field : classMember.enumConstants()) {
			report.addItem(ReportItem.FIELD, field.name());
			parseComment(field.commentText(), field.position());
			numFields++;
		}
	}

	/**
	 * Parses the words (tokens) out of comments, removes punctuation, and adds each word to
	 * the word list.
	 *
	 * @param comment The comment string which we wish to parse
	 */
	private void parseComment(String comment, SourcePosition position) {
		// Replace parenthesis, brackets, dashes, and periods with spaces
		comment = comment.replaceAll("[\\p{Punct}&&[^']]"," ");

		// Create Scanner to process the comment string
		Scanner scan = new Scanner(comment);
		while (scan.hasNext()) {
			totalNumWords++;
			String word = scan.next().toLowerCase();
			uniqueWordList.add(word);
			if (badWordsList.contains(word)) {
				numBadWords++;
				report.appendMessage(ReportItem.LASTITEM, "'" + word + "' considered an offensive word " +
					"on/near line " + position.line());
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

	/**
	 * Returns the string representation of this module's report (JSON format)
	 *
	 * @return a string value containing the JSON report
	 */
	public String getJSONReport() {
		return report.toString();
	}
}