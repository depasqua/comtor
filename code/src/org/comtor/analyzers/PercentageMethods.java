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

/**
 * The PercentageMethods class is a tool to measure the percentage of methods in a class 
 * that are documented with a Javadoc header.
 *
 * @author Joe Brigandi
 * @author Peter DePasquale
 */
public class PercentageMethods implements ComtorDoclet {
	private HashMap<String, Float> gradeBreakdown = new HashMap<String, Float>();

	// Counters for the classes, constructors, methods, etc. used in the report
	private int numClasses = 0;
	private int numMethods = 0;
	private int numConstructors = 0;
	private int numCommentedMethods = 0;
	private int numCommentedConstructors = 0;

	// JSON analysis report from this module's execution
	private ModuleReport report = report = new ModuleReport ("Percentage Methods", "This module calculates the " + 
		"percentage of methods that have associated comments. ");

	/**
	 * Constructor for this doclet. By default, it sets the total number of possible "points" to 5.
	 * This values is used when "scoring" student submissions in the web-based client. The "points"
	 * value can be modified via the web interface which in turn calls the setGradingBreakdown
	 * method.
	 */
	public PercentageMethods() {
		// Set default values for grade breakdown
		gradeBreakdown.put("TotalPoints", new Float(5.0));
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
			
		report.appendToAmble("preamble", "If a class contains no user-defined constructors, the compiler includes " +
			"a no-argument, uncommented default (nullary) constructor. Javadoc, and thus COMTOR, has no way to " +
			"identify this type of constructor during analysis.");

		// Capture the starting time, just prior to the start of the analysis
		long startTime = new Date().getTime();

		for (ClassDoc classdoc : rootDoc.classes()) {
			int localNumMethods = 0;
			int localNumConstructors = 0;
			int localNumCommentedMethods = 0;
			int localNumCommentedConstructors = 0;
			
			report.addItem(ReportItem.CLASS, classdoc.qualifiedName());
			numClasses++;

			// Count the number of commented constructors
			ExecutableMemberDoc[] members = classdoc.constructors();
			numConstructors += members.length;
			localNumConstructors = members.length;
			for (ExecutableMemberDoc docs : members) {
				report.addItem(ReportItem.CONSTRUCTOR, docs.name() + docs.flatSignature());
				if (docs.getRawCommentText().length() > 0)
					localNumCommentedConstructors++;
				else
					report.appendMessage(ReportItem.LASTITEM, docs.name() + docs.flatSignature() +
						" is not commented at/near line " + docs.position().line() + ". (See note.)");
			}
				
			// Count the number of commented methods
			members = classdoc.methods();
			numMethods += members.length;
			localNumMethods = members.length;
			for (ExecutableMemberDoc docs : members) {
				report.addItem(ReportItem.METHOD, docs.name() + docs.flatSignature());
				if (docs.getRawCommentText().length() > 0)
					localNumCommentedMethods++;
				else
					report.appendMessage(ReportItem.LASTITEM, docs.name() + docs.flatSignature() +
						" is not commented at/near line " + docs.position().line() + ". (See note.)");
			}
			
			// Generate the report results
			if (numMethods + numConstructors != 0) {
				// Update the running totals for the data observed in this class
				numCommentedMethods += localNumCommentedMethods;
				numCommentedConstructors += localNumCommentedConstructors;
				
				// Store percentCommented in the property list

				if (localNumMethods > 0)
					report.appendMessage(ReportItem.CLASS, localNumCommentedMethods + " of the " + localNumMethods + 
						" methods (" + Math.round(((double) localNumCommentedMethods) / localNumMethods * 100) + 
						"%) are commented.");

				if (localNumConstructors > 0)
					report.appendMessage(ReportItem.CLASS, localNumCommentedConstructors + " of the " + localNumConstructors + 
						" constructors (" + Math.round(((double) localNumCommentedConstructors) / localNumConstructors * 100) + 
						"%) are commented.");
			}
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

		// prop.setProperty("score", "" + getGrade());

		report.addTimingString("start time", Long.toString(startTime));
		report.addTimingString("end time", Long.toString(endTime));
		report.addTimingString("execution time", Long.toString(endTime - startTime));

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
		
		if (numMethods == 0)
			percent = 1.0f;
		else
			percent = ((float) numCommentedMethods) / numMethods;
			
		return percent * gradeBreakdown.get("TotalPoints");
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
