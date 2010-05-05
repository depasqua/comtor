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
 * The <code>CommentAvgRatio</code> class is a tool to measure the
 * average length of comments for methods in a class.
 *
 * @author Joe Brigandi
 */
public final class CommentAvgRatio implements ComtorDoclet
{
	Properties prop = new Properties();
	long commentLength = 0;
	float maxScore = 5;
	int n_methods = 0, n_words = 0;
	//Default minimum average words per comment.
	float min_avg_words = 10;
	
	/**
	 * Constructor
	 */
	public CommentAvgRatio()
	{
		//Not needed.
	}

	/**
	 * Examine each class and its methods. Calculate the length of each method's comments.
	 *
	 * @param rootDoc  the root of the documentation tree
	 * @return Properties list
	 */
	public Properties analyze(RootDoc rootDoc)
	{
		//Doclet title.
		prop.setProperty("title", "Comment Average Ratio");

		ClassDoc[] classes = rootDoc.classes();

		for(int i=0; i < classes.length; i++)
		{
			String classID = numberFormat(i);
			//Store class name.
			prop.setProperty("" + classID, "Class: " + classes[i].name());
			int total=0;

			MethodDoc[] methods = new MethodDoc[0];
			methods = classes[i].methods();
			for(int j=0; j < methods.length; j++)
			{
				String methodID = numberFormat(j);
				//Scanner.
				Scanner scan = new Scanner(methods[j].getRawCommentText());
				//Number of words in the method's documentation.
				int count=0;

				//Scan through the list of methods, increment count for each word in the comments.
				while(scan.hasNext())
				{
					scan.next();
					count++;
				}
				
				//Total length of the comments.
				commentLength = count;

			        //Store length of comments for the method in the property list.
			        if (commentLength == 0) {
			        	prop.setProperty(classID + "." + methodID, "The length of comments for the method " 
					+ methods[j].name() + " is " + commentLength + " words.");
				}
				else {
					prop.setProperty(classID + "." + methodID, "The length of comments for the method " 
						+ methods[j].name() + " is " + commentLength + " words.");
				}
				total+=commentLength;
			}

			//Format number to 3 digits (see method below)
			String methodLength = numberFormat(methods.length);
			
			//If there are no methods...
			if(methods.length==0)
				prop.setProperty(i + "." + methodLength, "There are no methods in the class " 
					+ classes[i].name() + ".");
			else
			{
				//Set the points in the map
				n_methods += methods.length;
				n_words += total;

				//Calculate the average length of comments and store it in the property list.
				int average = total/methods.length;
				
				if (average == 0) {
					prop.setProperty(classID + "." + methodLength, 
					"Average length of comments: " + average + " words.");
				}
				else {
					prop.setProperty(classID + "." + methodLength, 
						"Average length of comments: " + average + " words.");
				}
			}
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
		if (section.equals("Average"))
			maxScore = maxGrade;
	}

	/*************************************************************************
	 * Returns the grade for the doclet.
	 *************************************************************************/
	public float getGrade()
	{
		float avgWords = (n_methods == 0) ? (float)min_avg_words : (float)n_words/n_methods;

		if (avgWords > min_avg_words - 0.001)
			return maxScore;

		return (float)0.0;
	}

	/*************************************************************************
	 * Sets a parameter used for doclet grading.
	 *
	 * @param param Name of the grading parameter
	 * @param value Value of the parameter
	 *************************************************************************/
	public void setGradingParameter(String param, String value)
	{
		//Minimum average number of words
		if (param.equals("min_ratio"))
		{
			//Parse value into a float
			min_avg_words = Float.parseFloat(value);
		}
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
