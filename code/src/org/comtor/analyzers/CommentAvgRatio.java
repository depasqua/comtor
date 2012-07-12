/** *  Comment Mentor: A Comment Quality Assessment Tool *  Copyright (C) 2011 The College of New Jersey * *  This program is free software: you can redistribute it and/or modify *  it under the terms of the GNU General Public License as published by *  the Free Software Foundation, either version 3 of the License, or *  (at your option) any later version. * *  This program is distributed in the hope that it will be useful, *  but WITHOUT ANY WARRANTY; without even the implied warranty of *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *  GNU General Public License for more details. * *  You should have received a copy of the GNU General Public License *  along with this program.  If not, see <http://www.gnu.org/licenses/>. */package org.comtor.analyzers;import org.comtor.reporting.*;import com.sun.javadoc.*;import java.util.*;/** * The CommentAvgRatio class is a tool to measure the average length (defined by the number of * words) of comments for all methods and constructors of a class. The "count" of words here, is * number of words the Javadoc comments that (usually) precede the tags (and their comments).  * * @author Joe Brigandi * @author Peter DePasquale */public final class CommentAvgRatio implements ComtorDoclet {	// Set the initial report object needed for execution. 	private ModuleReport report = new ModuleReport ("Comment Average Ratio",			"This module analyzes the length (in terms of the number of words) of class, constructor, " +			"and method comments.");	// Variables related to scoring	private float minAvgNumWords = 10;	private float maxScore = 5;	// Local variables for metrics information	private int totalNumComments = 0;	private int totalNumWords = 0;	private int totalNumConstructors = 0;	private int totalNumMethods = 0;	private int totalNumFields = 0;	private int numZeroComments = 0;	/**	 * Examine each class and its methods. Calculate the length of each method's comments.	 *	 * @param rootDoc  the root of the documentation tree	 * @return Properties list	 */	public Properties analyze(RootDoc rootDoc) {		// Capture the starting time, just prior to the start of the analysis		long startTime = new Date().getTime();		int numClasses = rootDoc.classes().length;		for (ClassDoc classDoc : rootDoc.classes()) {			int numWords = 0;			int numMembers = 0;			report.addItem(ReportItem.CLASS, classDoc.qualifiedName());			report.addLocation(ReportItem.CLASS, classDoc.position());						// Analyze the class			int wordCount = (new StringTokenizer(classDoc.commentText())).countTokens();			if (wordCount != 0) {				report.appendMessage(ReportItem.LASTITEM, "There were " + wordCount +						" words in the class comments.");				numMembers++;				numWords += wordCount;				totalNumWords += wordCount;			} else				numZeroComments++;						// Analyze the class constructors			for (ConstructorDoc constr : classDoc.constructors()) {				if (!Util.isNullaryConstructor(constr)) {					report.addItem(ReportItem.CONSTRUCTOR, constr.name() + constr.flatSignature());					report.addLocation(ReportItem.CONSTRUCTOR, constr.position());					totalNumConstructors++;					String paramList = Util.getParamTypeList(constr);					wordCount = (new StringTokenizer(constr.commentText())).countTokens();					String messageStr = "The length of comments is " + wordCount + " words";					numMembers++;					numWords += wordCount;					totalNumWords += wordCount;					if (wordCount == 0) {						numZeroComments++;						messageStr += " (on/near line " + constr.position().line() + ").";					} else						messageStr += ".";					report.appendMessage(ReportItem.LASTITEM, messageStr);				}			}						// Analyze the class methods			for (MethodDoc method : classDoc.methods()) {				report.addItem(ReportItem.METHOD, method.name() + method.flatSignature());				report.addLocation(ReportItem.METHOD, method.position());				String paramList = Util.getParamTypeList(method);				wordCount = (new StringTokenizer(method.commentText())).countTokens();				String messageStr = "The length of comments is " + wordCount + " words";				numMembers++;				numWords += wordCount;				totalNumWords += wordCount;				totalNumMethods++;				if (wordCount == 0) {					numZeroComments++;					messageStr += " (on/near line " + method.position().line() + ").";				} else					messageStr += ".";				report.appendMessage(ReportItem.LASTITEM, messageStr);			}			// Analyze the class' fields			for (FieldDoc field : classDoc.fields()) {				report.addItem(ReportItem.FIELD, field.name());				report.addLocation(ReportItem.FIELD, field.position());				wordCount = (new StringTokenizer(field.commentText())).countTokens();				String messageStr = "The length of comments is " + wordCount + " words";				numMembers++;				numWords += wordCount;				totalNumWords += wordCount;				totalNumFields++;				if (wordCount == 0) {					numZeroComments++;					messageStr += " (on/near line " + field.position().line() + ").";				} else					messageStr += ".";				report.appendMessage(ReportItem.LASTITEM, messageStr);			} 			// Generate the report results for the class	 			if (numMembers == 0)				report.appendMessage(ReportItem.CLASS, "There are no class, constructor, " +					"or method comments in the class " + classDoc.qualifiedName() + ".");					 			else				// Calculate the average length of comments and store it in the property list.				report.appendMessage(ReportItem.CLASS, "The average length of all comments " + 					"in the class (including all members) " + classDoc.qualifiedName() + " is " +					numWords / numMembers + " words.");			totalNumComments += numMembers;		}		// Capture the ending time, just after the termination of the analysis		long endTime = new Date().getTime();		report.addMetric("A total of " + numClasses + " classes were processed.");		report.addMetric("A total of " + totalNumConstructors + " constructors were processed.");		report.addMetric("A total of " + totalNumMethods + " methods were processed.");		report.addMetric("A total of " + totalNumFields + " fields were processed.");		report.addMetric("A total of " + totalNumWords + " words were processed.");		report.addMetric("A total of " + totalNumComments + " class, method, and " + 			"constructor comments were processed.");		report.addMetric("The overall average number of words per comment is " + 			totalNumWords / totalNumComments + ".");		if (numZeroComments > 0)			report.appendToAmble("postamble", numZeroComments + " location(s) in your source code are missing " +				"comments. Consider commenting all classes, method, constructors and fields, to " +				"reduce this amount.");		report.addTimingString("start time", Long.toString(startTime));		report.addTimingString("end time", Long.toString(endTime));		report.addTimingString("execution time", Long.toString(endTime - startTime));		return null;	}	/**	  * Sets the grading breakdown for the doclet.	  *	  * @param section Name of the section to set the max grade for	  * @param maxGrade Maximum grade for the section	  */	public void setGradingBreakdown(String section, float maxGrade) {		if (section.equals("Average"))			maxScore = maxGrade;	}	/**	 * Returns the grade for the doclet.	 *	 * @return a float value representing the ratio of correctly spelt words	 * from comments to the total number of works in the comments.	 * @return 	 */	public float getGrade() {		float avgWords = 0;				if (totalNumComments == 0) 			avgWords = (float) minAvgNumWords;		else			avgWords = (float) totalNumWords / totalNumComments;		if (avgWords > minAvgNumWords - 0.001)			return maxScore;		else			return (float) 0.0;	}	/**	 * Sets a parameter used for doclet grading.	 *	 * @param param Name of the grading parameter	 * @param value Value of the parameter	 */	public void setGradingParameter(String param, String value) {		// Minimum average number of words		if (param.equals("min_ratio"))			minAvgNumWords = Float.parseFloat(value);	}	/**	 * Sets the configuration properties loaded from the config file	 *	 * @param props Properties list	 */	public void setConfigProperties(Properties props) {		// Not needed for this analyzer	}	/**	 * Returns a string representation of this object	 *	 * @return the string name of this analyzer	 */	public String toString() {		return "CommentAvgRatio";	}	/**	 * Returns the string representation of this module's report (JSON format)	 *	 * @return a string value containing the JSON report	 */	public String getJSONReport() {		return report.toString();	}}