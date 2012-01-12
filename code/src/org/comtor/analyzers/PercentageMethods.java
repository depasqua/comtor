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
import java.util.*;
import java.text.*;

/**
 * The PercentageMethods class is a tool to measure the percentage of methods in a class 
 * that are documented with a Javadoc header.
 *
 * @author Joe Brigandi
 * @author Peter DePasquale
 */
public class PercentageMethods implements ComtorDoclet
{
	private Properties prop = new Properties();
	private HashMap<String, Float> gradeBreakdown = new HashMap<String, Float>();
	private int totalMethodsCommented = 0;
	private int totalNumMethods = 0;

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
		// A counter for the classes, used in the properties list
		int classID = 0;
		DecimalFormat formatter = new DecimalFormat("##0000.000");
			
		prop.setProperty("title", "Percentage Methods");
		prop.setProperty(formatter.format(-1), "Note that if a class contains no user-defined " +
			"constructors, the compiler includes a no-argument, uncommented default constructor. " +
			"Javadoc, and thus COMTOR, has no way to eliminate this constructor from this " +
			"analysis.");

		// Capture the starting time, just prior to the start of the analysis
		long startTime = new Date().getTime();

		for (ClassDoc classdoc : rootDoc.classes()) {
			int methodsCommented = 0;
			int numMethods = 0;
			double percentCommented = 0.0;
			double propID = classID;
			
			// Format the ID number of this class, for the report
			prop.setProperty(formatter.format(classID), "Class: " + classdoc.qualifiedName());

			// Count the number of commented constructors
			ExecutableMemberDoc[] members = classdoc.constructors();
			numMethods += members.length;
			for (ExecutableMemberDoc docs : members)
				if (docs.getRawCommentText().length() > 0)
					methodsCommented++;
				else {
					propID += 0.001;
					prop.setProperty(formatter.format(propID)+".000", "Constructor " + 
						docs.qualifiedName() + '(' + Util.getParamTypeList(docs) + 
						") is not commented. (See note above.)");
				}
				
			// Count the number of commented methods
			members = classdoc.methods();
			numMethods = members.length;
			for (ExecutableMemberDoc docs : members)
				if (docs.getRawCommentText().length() > 0)
					methodsCommented++;
				else {
					propID += 0.001;
					prop.setProperty(formatter.format(propID)+".000", "Method " +
						docs.qualifiedName() + '(' + Util.getParamTypeList(docs) +
						") is not commented.");
				}
			
			propID += 0.001;
			// Generate the report results
			if  (numMethods != 0) {
				// Update the running totals for the data observed in this class
				totalMethodsCommented += methodsCommented;
				totalNumMethods += numMethods;
				
				// Calculate the percentage of commented methods.
				percentCommented = ((double) methodsCommented) / numMethods;

				// Store percentCommented in the property list
				prop.setProperty(formatter.format(propID)+".000", methodsCommented + " of the " + 
					numMethods + " constructors / methods (" + Math.round(percentCommented * 100) + 
					"%) in " + classdoc.qualifiedName() + " are commented.");
			}
			else {
				// No methods / constructors present.
				prop.setProperty(formatter.format(propID), classdoc.qualifiedName() + 
					" has no methods or constructors.");
			}
			classID++;
		}
		double totalPercent = (((double) totalMethodsCommented) / totalNumMethods);
		NumberFormat percentFormatter = NumberFormat.getPercentInstance();
		
		// Capture the ending time, just after the termination of the analysis
		long endTime = new Date().getTime();

		prop.setProperty("metric1", "A total of " + classID + " class(es) were processed.");
		prop.setProperty("metric2", "A total of " + totalMethodsCommented + " of the " + 
			totalNumMethods + " methods (" + percentFormatter.format(totalPercent) + ") " +
			"present were commented.");
		prop.setProperty("score", "" + getGrade());
		prop.setProperty("start time", Long.toString(startTime));
		prop.setProperty("end time", Long.toString(endTime));
		prop.setProperty("execution time", Long.toString(endTime - startTime));

		// Return the property list (report)
		return prop;
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
		
		if (totalNumMethods == 0)
			percent = 1.0f;
		else
			percent = ((float) totalMethodsCommented) / totalNumMethods;
			
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
}
