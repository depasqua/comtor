/***************************************************************************
  *  Comment Mentor: A Comment Quality Assessment Tool
  *  Copyright (C) 2005 Michael E. Locasto
  *
  *  This program is free software; you can redistribute it and/or modify
  *  it under the terms of the GNU General Public License as published by
  *  the Free Software Foundation; either version 2 of the License, or
  *  (at your option) any later version.
  *
  *  This program is distributed in the hope that it will be useful, but
  *  WITHOUT ANY WARRANTY; without even the implied warranty of
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  *  General Public License for more details.
  *
  *  You should have received a copy of the GNU General Public License
  *  along with this program; if not, write to the:
  *  Free Software Foundation, Inc.
  *  59 Temple Place, Suite 330
  *  Boston, MA  02111-1307  USA
  **************************************************************************/
  
package comtor.analyzers;

import comtor.*;
import com.sun.javadoc.*;
import java.util.*;
import java.text.*;

/**
 * The <code>PercentageMethods</code> class is a tool to measure a the
 * percentage of methods in a class that are documented with a JavaDoc header.
 *
 * @author Joe Brigandi
 */
public class PercentageMethods implements ComtorDoclet
{
	Properties prop = new Properties();
	HashMap<String, Float> gradeBreakdown = new HashMap<String, Float>();
	HashMap<String, Integer> possibleScore = new HashMap<String, Integer>();
	HashMap<String, Integer> points = new HashMap<String, Integer>();

	/**
	 * Constructor
	 */
	public PercentageMethods()
	{
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
	public Properties analyze(RootDoc rootDoc)
	{
		//Doclet Title.
		prop.setProperty("title", "Percentage Methods");
		
		//Number of methods commented.
		int methodsCommented = 0;
		//Percentage of methods commented.
		double percentCommented = 0.0;
		ClassDoc[] classes = rootDoc.classes();
		MethodDoc[] methods = new MethodDoc[0];
		for(int i=0; i < classes.length; i++)
		{
			String classID = numberFormat(i);
			//Store class name in property list.
			prop.setProperty("" + classID, "Class: " + classes[i].name());
			methods = classes[i].methods();
			for(int j=0;j<methods.length;j++)
			{
				//If comment is present, increment number of methods commented.
				if(methods[j].getRawCommentText().length() > 0)
					methodsCommented++;
			}

			if(0 != methods.length)
			{
				//Set the points in the map
				possibleScore.put("Percent", new Integer(possibleScore.get("Percent") + methods.length));
				points.put("Percent", new Integer(points.get("Percent") + methodsCommented));
				//Calculate the percentage of commented methods.
				percentCommented = (double)((1.0*methodsCommented)/methods.length);

				//Store percentCommented in the property list
				prop.setProperty("" + classID + ".000", Math.round(percentCommented*100) 
					+ " percent (" + methodsCommented + "/" + methods.length + ") of class " 
					+ classes[i].name() + "s methods are commented.");
			}
			else //if there are no methods...
				prop.setProperty("" + classID + ".000", "Class: " + classes[i].name() 
					+ " has no JavaDoc\'d methods.");

			methodsCommented = 0;
			percentCommented = 0.0;
		}

		prop.setProperty("score", "" + getGrade());

		//Return the property list.
		return prop;
	}

	//Used to convert an integer value to 3 digits (ex. 7 --> 007)
	private String numberFormat(int value)
	{
		String newValue;
		if(value<10)
			newValue = "00" + value;
		else if(value<100)
			newValue = "0" + value;
		else
			newValue = "" + value;

		return newValue;
	}

	/*************************************************************************
	 * Sets the grading breakdown for the doclet.
	 *
	 * @param section Name of the section to set the max grade for
	 * @param maxGrade Maximum grade for the section
	 *************************************************************************/
	public void setGradingBreakdown(String section, float maxGrade)
	{
		gradeBreakdown.put(section, new Float(maxGrade));
	}

	/*************************************************************************
	 * Returns the grade for the doclet.
	 *************************************************************************/
	public float getGrade()
	{
		int score, possible;
		float max, total = (float)0.0, percent;

		String sections[] = {"Percent"};

		for (int i = 0; i < sections.length; i++)
		{
			possible = possibleScore.get(sections[i]);
			score = points.get(sections[i]);
			max = gradeBreakdown.get(sections[i]);

			percent = (possible == 0) ? (float)1.0 : (float)score/possible;
			total += percent * max;
		}

		return total;
	}

	/*************************************************************************
	 * Sets a parameter used for doclet grading.
	 *
	 * @param param Name of the grading parameter
	 * @param value Value of the parameter
	 *************************************************************************/
	public void setGradingParameter(String param, String value)
	{
		//Not needed.
	}

	/*************************************************************************
	 * Sets the configuration properties loaded from the config file
	 *
	 * @param props Properties list
	 *************************************************************************/
	public void setConfigProperties(Properties props)
	{
		//Not needed.
	}
	
}
