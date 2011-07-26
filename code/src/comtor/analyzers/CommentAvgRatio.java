/** *  Comment Mentor: A Comment Quality Assessment Tool *  Copyright (C) 2011 The College of New Jersey * *  This program is free software: you can redistribute it and/or modify *  it under the terms of the GNU General Public License as published by *  the Free Software Foundation, either version 3 of the License, or *  (at your option) any later version. * *  This program is distributed in the hope that it will be useful, *  but WITHOUT ANY WARRANTY; without even the implied warranty of *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *  GNU General Public License for more details. * *  You should have received a copy of the GNU General Public License *  along with this program.  If not, see <http://www.gnu.org/licenses/>. */package comtor.analyzers;import comtor.*;import com.sun.javadoc.*;import java.util.*;import java.text.*;/** * The CommentAvgRatio class is a tool to measure the average length of comments for all methods * of a class. * * @author Joe Brigandi * @author Peter DePasquale */public final class CommentAvgRatio implements ComtorDoclet{	private Properties prop = new Properties();	private long commentLength = 0;	private float maxScore = 5;	private int n_methods = 0, n_words = 0;	// Default minimum average words per comment.	private float min_avg_words = 10;	/**	 * Examine each class and its methods. Calculate the length of each method's comments.	 *	 * @param rootDoc  the root of the documentation tree	 * @return Properties list	 */	public Properties analyze(RootDoc rootDoc) {		prop.setProperty("title", "Comment Average Ratio");		ClassDoc[] classes = rootDoc.classes();		int index = 0;		DecimalFormat formatter = new DecimalFormat("000");				for (ClassDoc classDoc : classes) {			String classID = formatter.format(index);			prop.setProperty("" + classID, "Class: " + classDoc.name());			int total = 0;			MethodDoc[] methods = classDoc.methods();			int methodIndex = 0;			for (MethodDoc methodDoc : methods) {				String methodID = formatter.format(methodIndex);				Scanner scan = new Scanner(methodDoc.getRawCommentText());				// Number of words in the method's documentation.				int count = 0;				// Scan through the list of methods, increment count for each word in the comments.				while(scan.hasNext()) {					scan.next();					count++;				}				// Total length (in # of words) of the comments.				commentLength = count;				// Store length of comments for the method in the property list.				if (commentLength == 0) {						prop.setProperty(classID + "." + methodID, "The length of comments for " + 						"the method " + methodDoc.name() + " is " + commentLength + " words.");				} else {					prop.setProperty(classID + "." + methodID, "The length of comments for " +						"the method " + methodDoc.name() + " is " + commentLength + " words.");				}				total += commentLength;				methodIndex++;			}						String methodLength = formatter.format(methods.length);						if (methods.length == 0)				prop.setProperty(classID + "." + methodLength, "There are no methods in the class " +					classDoc.name() + ".");			else {				// Set the points in the map				n_methods += methods.length;				n_words += total;				// Calculate the average length of comments and store it in the property list.				int average = total / methods.length;				if (average == 0)					prop.setProperty(classID + "." + methodLength, 						"Average length of comments: " + average + " words.");				else					prop.setProperty(classID + "." + methodLength, 						"Average length of comments: " + average + " words.");			}			index++;		}			prop.setProperty("score", "" + getGrade());		return prop;	}	/**	  * Sets the grading breakdown for the doclet.	  *	  * @param section Name of the section to set the max grade for	  * @param maxGrade Maximum grade for the section	  */	public void setGradingBreakdown(String section, float maxGrade) {		if (section.equals("Average"))			maxScore = maxGrade;	}	/**	 * Returns the grade for the doclet.	 *	 * @return a float value representing the ratio of correctly spelt words	 * from comments to the total number of works in the comments.	 * @return 	 */	public float getGrade() {		float avgWords = (n_methods == 0) ? (float) min_avg_words : (float) n_words/n_methods;		if (avgWords > min_avg_words - 0.001)			return maxScore;		else			return (float) 0.0;	}	/**	 * Sets a parameter used for doclet grading.	 *	 * @param param Name of the grading parameter	 * @param value Value of the parameter	 */	public void setGradingParameter(String param, String value) {		// Minimum average number of words		if (param.equals("min_ratio")) {			min_avg_words = Float.parseFloat(value);		}	}	/**	 * Sets the configuration properties loaded from the config file	 *	 * @param props Properties list	 */	public void setConfigProperties(Properties props) {		// Not needed for this analyzer	}	/**	 * Returns a string representation of this object	 *	 * @return the string name of this analyzer	 */	public String toString() {		return "CommentAvgRatio";	}}