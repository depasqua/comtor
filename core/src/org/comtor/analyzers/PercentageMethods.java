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
import org.comtor.reporting.*;
import com.sun.javadoc.*;
import java.util.*;
import java.text.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * The PercentageMethods class is a tool to measure the percentage of methods in a class 
 * that are documented with a Javadoc header.
 *
 * @author Joe Brigandi
 * @author Peter DePasquale
 */
public class PercentageMethods implements ComtorDoclet {
	private HashMap<String, Float> gradeBreakdown = new HashMap<String, Float>();
	
	private static Logger logger = LogManager.getLogger(SpellCheck.class.getName());

	// Counters for the classes, constructors, methods, etc. used in the report
	private int numClasses = 0;
	private int numMethods = 0;
	private int numConstructors = 0;
	private int numCommentedMethods = 0;
	private int numCommentedConstructors = 0;

	// JSON analysis report from this module's execution
	private ModuleReport report = null;

	/**
	 * Constructor for this doclet. By default, it sets the total number of possible "points" to 5.
	 * This values is used when "scoring" student submissions in the web-based client. The "points"
	 * value can be modified via the web interface which in turn calls the setGradingBreakdown
	 * method.
	 */
	public PercentageMethods() {
		// Set default values for grade breakdown
		gradeBreakdown.put("TotalPoints", new Float(100.0));
	}

	/**
	 * Examine each class, and obtains each method / constructor. Determines if rawComment text has
	 * a length more than zero. If so, count it in the total frequency of commented methods for
	 * that class. Obtain percentage of commented methods per class.
	 *
	 * @param rootDoc  the root of the documentation tree
	 * @return Properties list containing the result set
	 */
	public Properties analyze(RootDoc rootDoc) {
		logger.entry();
		String description = "This module calculates the percentage of constructors and method that have associated comments. " +
		"The reported score is the overall percentage of methods and constructors which are commented. The length and/or content " +
		"of the comment is not considered. Other types of comments (class-level, field-level) are not considered in this analysis.";
		report = new ModuleReport ("Percentage Methods", Util.stringWrapAfter(description, 80));
		report.appendToAmble("preamble", "References to line numbers in this report are generally to " +
			"the subsequent non-comment Java statement that follows the commment/comment block under analysis.");
		report.appendToAmble("preamble", "The score in this module is calculated by determining the overall percentage of commented methods " +
			"and constructors across all methods and constructors identified.");

		// Capture the starting time, just prior to the start of the analysis
		long startTime = new Date().getTime();

		for (ClassDoc classdoc : rootDoc.classes()) {
			logger.trace("Processing class '" + classdoc.qualifiedName() + "'");
			int localNumMethods = 0;
			int localNumConstructors = 0;
			int localNumCommentedMethods = 0;
			int localNumCommentedConstructors = 0;
			
			report.addItem(ReportItem.CLASS, classdoc.qualifiedName());
			numClasses++;

			// Count the number of commented constructors
			for (ConstructorDoc constr : classdoc.constructors()) {
				if (!Util.isNullaryConstructor(constr)) {
					numConstructors++;
					localNumConstructors++;
					report.addItem(ReportItem.CONSTRUCTOR, constr.qualifiedName() + constr.signature());
					if (constr.getRawCommentText().length() > 0)
						localNumCommentedConstructors++;
					else
						report.appendMessage(ReportItem.LASTITEM, " " + constr.qualifiedName() + constr.signature() +
							" is not commented at/near line " + constr.position().line() + ".");
				}
			}
				
			// Count the number of commented methods
			ExecutableMemberDoc [] members = classdoc.methods();
			numMethods += members.length;
			localNumMethods = members.length;
			for (ExecutableMemberDoc method : members) {
				report.addItem(ReportItem.METHOD, method.qualifiedName() + method.signature());
				if (method.getRawCommentText().length() > 0)
					localNumCommentedMethods++;
				else
					report.appendMessage(ReportItem.LASTITEM, " " + method.qualifiedName() + method.signature() +
						" is not commented at/near line " + method.position().line() + ".");
			}
			
			// Generate the report results
			if (numMethods + numConstructors != 0) {
				// Update the running totals for the data observed in this class
				numCommentedMethods += localNumCommentedMethods;
				numCommentedConstructors += localNumCommentedConstructors;
				ReportItem destination;

				destination = (localNumConstructors > 0 && localNumConstructors != localNumCommentedConstructors)?ReportItem.CLASS:ReportItem.CLASS_SUMMARY;
				report.appendMessage(destination, localNumCommentedConstructors + " of the " + localNumConstructors + 
					" constructors (" + Math.round(((double) localNumCommentedConstructors) / localNumConstructors * 100) + 
					"%) are commented.");

				// Store percentCommented in the report
				destination = (localNumMethods > 0 && localNumMethods != localNumCommentedMethods)?ReportItem.CLASS:ReportItem.CLASS_SUMMARY;
				report.appendMessage(destination, localNumCommentedMethods + " of the " + localNumMethods + 
					" methods (" + Math.round(((double) localNumCommentedMethods) / localNumMethods * 100) + 
					"%) are commented.");
			}

			int numProblemsThisClass = (localNumMethods - localNumCommentedMethods) + (localNumConstructors - localNumCommentedConstructors);
			report.appendStringToClass(report.getCurrentClass(), "numProblemsDetected", Integer.toString(numProblemsThisClass));
			if (numProblemsThisClass == 0)
				report.appendStringToClass(report.getCurrentClass(), "noProblemMessage", "No uncommented constructors or methods " +
					"were found in processing this class.");

			report.setFieldsAnalyzed(false);
			report.setParamsAnalyzed(false);
			report.setThrowsAnalyzed(false);
			report.setReturnsAnalyzed(false);
		}

		double totalPercentMethods = (((double) numCommentedMethods) / numMethods);
		double totalPercentConstructors = (((double) numCommentedConstructors) / numConstructors);

		// Capture the ending time, just after the termination of the analysis
		long endTime = new Date().getTime();

		report.appendLongToObject("information", "classes processed", numClasses);
		report.appendLongToObject("information", "constructors processed", numConstructors);
		report.appendLongToObject("information", "constructors commented", numCommentedConstructors);
		report.appendLongToObject("information", "methods processed", numMethods);
		report.appendLongToObject("information", "methods commented", numCommentedMethods);
		report.addMetric(numClasses + " class(es) were processed.");
		
		report.addMetric(numMethods + " method(s) were processed.");
		report.addMetric(numCommentedMethods + " method(s) were commented.");
		report.addMetric(Math.round(((double) numCommentedMethods) / numMethods * 100) +
			"% method(s) were commented across all classes.");

		report.addMetric(numConstructors + " constructors(s) were processed.");
		report.addMetric(numCommentedConstructors + " constructors(s) were commented.");
		report.addMetric(Math.round(((double) numCommentedConstructors) / numConstructors * 100) +
			"% constructors(s) were commented across all classes.");

		int numMissing = (numMethods - numCommentedMethods) + (numConstructors - numCommentedConstructors);
		if (numMissing > 0)
			report.appendToAmble("postamble", Util.stringWrapAfter(numMissing + " location(s) (which are methods or constructors) in your source code are missing " +
				"Javadoc comments. Consider commenting all of your constructors and methods to reduce this amount. Additionally, if you are receiving false " +
				"results, please be sure you are using the Javadoc commenting style (i.e. /**  **/).", 80));

		NumberFormat formatter = NumberFormat.getPercentInstance();
		report.addScore(formatter.format(getGrade()));

		report.addTimingString("start time", Long.toString(startTime));
		report.addTimingString("end time", Long.toString(endTime));
		report.addTimingString("execution time", Long.toString(endTime - startTime));

		logger.exit();
		return null;
	}

	/*
	 * Sets the grading breakdown for the doclet.
	 *
	 * @param section Name of the section to set the max grade for
	 * @param maxGrade Maximum grade for the section
	 */
	public void setGradingBreakdown(String section, float maxGrade) {
		gradeBreakdown.put(section, new Float(maxGrade));
	}

	/**
	 * Returns the grade for the doclet.
	 *
	 * @return the overall grade for the doclet, as a float
	 */
	public float getGrade() {
		float percent = 0;
		
		if ((numMethods + numConstructors) == 0)
			percent = 1.0f;

		else {
			float totalNum = (float) (numMethods + numConstructors);
			float totalCommented = (float) (numCommentedMethods + numCommentedConstructors);
			percent = totalCommented / totalNum;
		}

		return percent;
	}

	/**
	 * Sets a parameter used for doclet grading.
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
		return "PercentageMethods";
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
