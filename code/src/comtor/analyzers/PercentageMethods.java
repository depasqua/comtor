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
import java.util.*;
import java.text.*;

/**
 * The PercentageMethods class is a tool to measure the percentage of methods in a class 
 * that are documented with a JavaDoc header.
 *
 * @author Joe Brigandi
 * @author Peter DePasquale
 */
public class PercentageMethods implements ComtorDoclet
{
	private Properties prop = new Properties();
	private HashMap<String, Float> gradeBreakdown = new HashMap<String, Float>();
	private HashMap<String, Integer> possibleScore = new HashMap<String, Integer>();
	private HashMap<String, Integer> points = new HashMap<String, Integer>();

	/**
	 * Constructor
	 */
	public PercentageMethods() {
		// Set default values for grade breakdown
		gradeBreakdown.put("Percent", new Float(5.0));

		// Set default values for scores
		possibleScore.put("Percent", new Integer(0));
		points.put("Percent", new Integer(0));
	}

	/**
	 * Examine each class, obtain each method. See if rawComment text has
	 * a length more than zero. If so, count it in the total frequency of
	 * commented methods for that class. Obtain percentage of commented
	 * methods per class.
	 *
	 * @param rootDoc  the root of the documentation tree
	 * @return some boolean value
	 */
	public Properties analyze(RootDoc rootDoc) {
		prop.setProperty("title", "Percentage Methods");
		
		ClassDoc[] classes = rootDoc.classes();
		MethodDoc[] methods = new MethodDoc[0];
		
		int classId = 0;
		for (ClassDoc classdoc : classes) {
			int methodsCommented = 0;
			double percentCommented = 0.0;
			DecimalFormat formatter = new DecimalFormat("000");
			String classID = formatter.format(classId); // ID number of this class, for the report
			
			prop.setProperty("" + classID, "Class: " + classdoc.name());
			methods = classdoc.methods();
			
			for (MethodDoc methodoc : methods)
				if (methodoc.getRawCommentText().length() > 0)
					methodsCommented++;

			if  (methods.length != 0) {
				// Set the points in the map
				possibleScore.put("Percent", new Integer(possibleScore.get("Percent") +
						methods.length));
				points.put("Percent", new Integer(points.get("Percent") + methodsCommented));
				// Calculate the percentage of commented methods.
				percentCommented = ((double) methodsCommented) / methods.length;

				// Store percentCommented in the property list
				if (methodsCommented == 0)
					prop.setProperty(classID + ".000", Math.round(percentCommented*100)
						+ "% (" + methodsCommented + "/" + methods.length
						+ ") of the methods are commented.");
				else
					prop.setProperty(classID + ".000", Math.round(percentCommented*100)
						+ "% (" + methodsCommented + "/" + methods.length
						+ ") of the methods are commented.");
			}
			else //if there are no methods...
				prop.setProperty(classID + ".000", "This class has no methods commented with JavaDoc.");

			classId++;
		}
		prop.setProperty("score", "" + getGrade());

		//Return the property list.
		return prop;
	}

	/*
	 * Sets the grading breakdown for the doclet.
	 *
	 * @param section Name of the section to set the max grade for
	 * @param maxGrade Maximum grade for the section
	 */
	public void setGradingBreakdown(String section, float maxGrade)
	{
		gradeBreakdown.put(section, new Float(maxGrade));
	}

	/**
	 * Returns the grade for the doclet.
	 *
	 * @return the overall grade for the doclet, as a float
	 */
	public float getGrade() {
		int possible;
		float percent;

		possible = possibleScore.get("Percent");
		percent = (possible == 0) ? (float) 1.0 : (float) points.get("Percent") / possible;
		return percent * gradeBreakdown.get("Percent");
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
