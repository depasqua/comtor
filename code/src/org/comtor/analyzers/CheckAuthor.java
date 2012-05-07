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
 * The CheckAuthor class is a tool to validate that each class contains an apprpriate
 * '@author' tag in the class-level comment block.
 *
 * @author Peter DePasquale
 */
public class CheckAuthor implements ComtorDoclet
{
	private Properties prop = new Properties();
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
		// A counter for the classes, used in the properties list
		int classID = 0;
		DecimalFormat formatter = new DecimalFormat("##0000.000");	
		Tag [] tags = new Tag[0];
		float msgIndex = 0.0f;
		
		prop.setProperty("title", "Check Author");
		prop.setProperty(formatter.format(-1), "Preamble notes go here.");

		// Capture the starting time, just prior to the start of the analysis
		long startTime = new Date().getTime();

		for (ClassDoc classdoc : rootDoc.classes()) {
		        msgIndex = 0.0f;
			// Format the ID number of this class, for the report
			prop.setProperty(formatter.format(classID), 
					 "Class: " + classdoc.qualifiedName());
			tags = classdoc.tags("@author");

			if (tags.length > 0)
			{
			    // Search for at least one occurence of the specified tag
			    // that also contains non-blank corresponding text
			    for (int index = 0; index < tags.length; index++)
			    {
				if (tags[index].text().equals(""))
				{
				    prop.setProperty(formatter.format(classID+msgIndex), 
						     "empty @author tag found in class.");
				    msgIndex += 0.001;
				}
			    }
			}else{
			    //report no @author tags
			    prop.setProperty(formatter.format(classID+msgIndex), 
					     "no @author tags found in class.");
			    msgIndex += 0.001;
			}

			classID++;
		}
		
		// Capture the ending time, just after the termination of the analysis
		long endTime = new Date().getTime();

		prop.setProperty("metric1", "A total of " + classID + " class(es) were processed.");
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
}
