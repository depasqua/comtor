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

/**
 * The CheckAuthor class is a tool to validate that each class contains an apprpriate
 * '@author' tag in the class-level comment block.
 *
 * @author Peter DePasquale
 */
public class CheckAuthor implements ComtorDoclet {
	// JSON analysis report from this module's execution
	ModuleReport report = report = new ModuleReport ("Check Author", "This module validates the presence of " +
			"the @author tag in each class. Additionally, this module checks for non-blank @author tags (missing " +
			"author name).");

	private HashMap<String, Float> gradeBreakdown = new HashMap<String, Float>();

	/**
	 * Constructor for this doclet. By default, it sets the total number of possible "points" to 5.
	 * This values is used when "scoring" student submissions in the web-based client. The "points"
	 * value can be modified via the web interface which in turn calls the setGradingBreakdown
	 * method.
	 */
	public CheckAuthor() {
		// Set default values for grade breakdown
		gradeBreakdown.put("TotalPoints", new Float(5.0));
	}

	/**
	 * Examine each class, and determines if an '@author' tag is
	 * present for the class.
	 *
	 * @param rootDoc  the root of the documentation tree
	 * @return Properties list containing the result set
	 */
	public Properties analyze(RootDoc rootDoc) {

		// A counter for the various metrics and reporting variables
		int numClasses = 0;
		boolean classError = false;
		int classesWithErrors = 0;

		int foundTags = 0;
		int missingAuthorTagCount = 0;
		int emptyAuthorTagCount = 0;
		
		report.appendToAmble("preamble", "References to line numbers in this report are generally to " +
			"the subsequent non-comment Java statement that follows the commment/comment block under analysis.");

		// Capture the starting time, just prior to the start of the analysis
		long startTime = new Date().getTime();

		for (ClassDoc classdoc : rootDoc.classes()) {
			numClasses++;
			classError = false;
			report.addClass(classdoc.qualifiedName());

			// Obtain the array of @author tags for this class
			Tag [] tags = classdoc.tags("@author");

			if (tags.length > 0) {
			    // Search for at least one occurence of the specified tag
			    // that also contains non-blank corresponding text
			    for (int index = 0; index < tags.length; index++) {
					if (tags[index].text().equals("")) {
						report.appendClassMessage("An empty @author tag was found in the comment block " +
							"preceeding line " + tags[index].position().line());
						emptyAuthorTagCount++;
						classError = true;
					} else
						report.appendClassMessage("@author tag found: " + tags[index].text());
			    }
			    foundTags++;
			} else {
				// Report that no @author tags were found for this class
				report.appendClassMessage("No @author tags found.");
				classError = true;
			    missingAuthorTagCount++;
			}
			if (classError != false)
				classesWithErrors++;
		}
		
		// Capture the ending time, just after the termination of the analysis
		long endTime = new Date().getTime();

		// Add the report results / metrics
		report.appendLongToObject("information", "classes processed", numClasses);
		report.appendLongToObject("information", "classes with errors", classesWithErrors);
		report.appendLongToObject("information", "tags found", foundTags);
		report.appendLongToObject("information", "tags errors", emptyAuthorTagCount);

		report.addMetric(numClasses + " class(es) were processed.");
		report.addMetric(missingAuthorTagCount + " class(es) are missing @author tags.");
		report.addMetric(emptyAuthorTagCount +  " @author tags are empty (missing names following tag)");
		String longURL = "http://chart.apis.google.com/chart?chs=650x350&cht=p&" +
			"chco=FF0000|0000FF&chds=a&chd=t:" + (numClasses-classesWithErrors) + "," + classesWithErrors + 
			"&chl=" + (numClasses-classesWithErrors) + "|" + classesWithErrors + 
			"&chxs=0,000000,14&chxt=x&chdls=000000,14&chdl=%23+Classes+Without+Errors|%23+Classes+With+Errors" +
			"&chtt=COMTOR+Check+Author Module:+Class+Error+Chart&chts=000000,18";
		report.addChart("Class Error Chart", BitlyServices.shortenUrl(longURL));

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
		return 1.0f;
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