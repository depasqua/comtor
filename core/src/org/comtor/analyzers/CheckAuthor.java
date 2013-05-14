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

import org.comtor.util.*;
import org.comtor.drivers.*;
import org.comtor.reporting.*;

import com.sun.javadoc.*;

import java.util.*;
import java.text.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * The CheckAuthor class is a tool to validate that each class contains an apprpriate
 * '@author' tag in the class-level comment block.
 *
 * @author Peter DePasquale
 */
public class CheckAuthor implements ComtorDoclet {
	private static Logger logger = LogManager.getLogger(CheckAuthor.class.getName());
	private HashMap<String, Float> gradeBreakdown = new HashMap<String, Float>();
	private int numClasses = 0;
	private int classesWithErrors = 0;
	private	ModuleReport report = null;

	/**
	 * Examine each class, and determines if an '@author' tag is
	 * present for the class.
	 *
	 * @param rootDoc  the root of the documentation tree
	 * @return Properties list containing the result set
	 */
	public Properties analyze(RootDoc rootDoc) {
		logger.entry();

		// JSON analysis report from this module's execution
		String description = "This module validates the presence of the @author tag in each class. " +
			"Additionally, this module checks for non-blank @author tags (missing names following the tag).";
		report = new ModuleReport("Check Author", Util.stringWrapAfter(description, 80));

		// A counter for the various metrics and reporting variables
		boolean classError = false;

		int missingAuthorTagCount = 0;
		int emptyAuthorTagCount = 0;
		int foundTags = 0;
		
		report.appendToAmble("preamble", "The @author tag is widely used to show who has contributed source code to a class.");
		report.appendToAmble("preamble", "Multiple @author tags are permitted in the class-block of comments.");
		report.appendToAmble("preamble", "References to line numbers in this report are generally to the subsequent non-comment " +
			"Java statement that follows the commment/comment block under analysis.");
		report.appendToAmble("preamble", "The score in this module is calculated by determining the percentage of classes processed " +
			"that contain no errors (contain @author tags with strings following the tag).");

		// Capture the starting time, just prior to the start of the analysis
		long startTime = new Date().getTime();

		for (ClassDoc classdoc : rootDoc.classes()) {
			logger.trace("Processing class '" + classdoc.qualifiedName() + "'");
			numClasses++;
			classError = false;
			report.addItem(ReportItem.CLASS, classdoc.qualifiedName());

			// Obtain the array of @author tags for this class
			Tag [] tags = classdoc.tags("@author");

			if (tags.length > 0) {
			    // Search for at least one occurence of the specified tag
			    // that also contains non-blank corresponding text
			    for (int index = 0; index < tags.length; index++) {
					if (tags[index].text().equals("")) {
						report.appendMessage(ReportItem.CLASS, "An empty @author tag was found in the comment block " +
							"preceeding line " + tags[index].position().line() + ".");
						emptyAuthorTagCount++;
						classError = true;
					} else {
						report.appendMessage(ReportItem.CLASS_SUMMARY, "@author tag found: " + tags[index].text());
						report.appendStringToClass(report.getCurrentClass(), "noProblemMessage", "1 or more @author tags were found.");
					}
			    }
			    foundTags++;
			} else {
				// Report that no @author tags were found for this class
				report.appendMessage(ReportItem.CLASS, "No @author tags were found.");
				classError = true;
			    missingAuthorTagCount++;
			}
			if (classError != false)
				classesWithErrors++;

			report.setFieldsAnalyzed(false);
			report.setParamsAnalyzed(false);
			report.setThrowsAnalyzed(false);
			report.setReturnsAnalyzed(false);
		}
		
		// Capture the ending time, just after the termination of the analysis
		long endTime = new Date().getTime();

		// Add the report results / metrics
		report.appendLongToObject("information", "classes processed", numClasses);
		report.appendLongToObject("information", "classes with errors", classesWithErrors);
		report.appendLongToObject("information", "tags found", foundTags);
		report.appendLongToObject("information", "tags errors", emptyAuthorTagCount);

		report.addMetric(numClasses + " class(es) were processed.");
		report.addMetric(classesWithErrors + " class(es) contained errors related to the use of the @author tag.");
		report.addMetric(missingAuthorTagCount + " class(es) are missing @author tags.");
		report.addMetric(emptyAuthorTagCount +  " @author tag(s) are empty (missing names following tag)");
		report.addMetric(foundTags + " @author tag(s) were located with text following the tags.");

		if (missingAuthorTagCount > 0)
			report.appendToAmble("postamble", Util.stringWrapAfter(missingAuthorTagCount + " @author tag(s) are missing from class-level documentation.", 80));

		if (emptyAuthorTagCount > 0)
			report.appendToAmble("postamble", Util.stringWrapAfter(emptyAuthorTagCount + " @author tag(s) are empty and have no author specified.", 80));

		if (emptyAuthorTagCount + missingAuthorTagCount > 0)
			report.appendToAmble("postamble", Util.stringWrapAfter("Consider adding @author tags to the comment block proceeding each class header. If you " +
				"are receiving false results, please be sure you are using the Javadoc commenting style (i.e. /**  **/).", 80));

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
		return (numClasses - classesWithErrors) / (float) numClasses;
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
		return "CheckAuthor";
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