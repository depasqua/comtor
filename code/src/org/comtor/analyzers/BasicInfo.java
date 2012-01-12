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
 * The BasicInfo analyzer provides basic information about what Javadoc finds. This module is used
 * as a pedagogical tool for those learning to develop for COMTOR as well as a simple debugging
 * aid for those in need of knowing what is going on inside of COMTOR (consider this a dump
 * of the most relevant parts of the RootDoc).
 *
 * @author Peter DePasquale
 */
public class BasicInfo implements ComtorDoclet {
	// A counter for the classes, used in the properties list
	private int classID = 0;
	
	// Various counters
	private int constructorCounter = 0;
	private int methodCounter = 0;
	private int fieldCounter = 0;

	// The analysis report (property list) returned by this doclet
	private Properties prop = new Properties();

	// A formatter for the report
	DecimalFormat formatter = new DecimalFormat("##0000.000");
	float memberID = 0.000f;
	
	/**
	 * The analyze method in each analysis module performs the analysis and
	 * operates on the parsed source code (parsed by Javadoc program) as a 
	 * com.sun.javadoc.RootDoc object. The method returns a property list with 
	 * the grading results and the report.
	 */
	public Properties analyze(RootDoc rootDoc) {
		prop.setProperty("title", "Basic Info");
		if (rootDoc == null)
			return null;
		
		// Capture the starting time, just prior to the start of the analysis
		long startTime = new Date().getTime();

		// Iterate through the list of classes submitted to Javadoc
		// Obtain and process the basic info about each class
		for (ClassDoc classDoc : rootDoc.classes()) {
   			classID++;
			processClass(classDoc);
   		}		
	
		// Capture the ending time, just after the termination of the analysis
		long endTime = new Date().getTime();

		// Add the counters metrics to the report
		prop.setProperty("metric1", "A total of " + classID + " class(es) were processed.");
		prop.setProperty("metric2", "A total of " + constructorCounter + " constructor(s) were processed.");
		prop.setProperty("metric3", "A total of " + methodCounter + " method(s) were processed.");
		prop.setProperty("metric4", "A total of " + fieldCounter + " field(s) were processed.");
		prop.setProperty("start time", Long.toString(startTime));
		prop.setProperty("end time", Long.toString(endTime));
		prop.setProperty("execution time", Long.toString(endTime - startTime));
		
		// Return the report
		return prop;
	}
	
	/**
	 * Processes a class
	 * 
	 * @param classItem a reference to the class to analyze/process
	 */
	private void processClass(ClassDoc classItem) {		
		prop.setProperty(formatter.format(classID), "Class: " + classItem.qualifiedName());
		memberID = 0.000f;
		
		// List all constructors
		for (ConstructorDoc constrs : classItem.constructors()) {
			constructorCounter++;
			memberID += 0.001f;
			prop.setProperty(formatter.format(classID+memberID), "\tConstructor: " + constrs.name());
			processExecutable(constrs);
		}
		
		// List all methods
		for (MethodDoc method : classItem.methods()) {
			methodCounter++;
			memberID += 0.001f;
			prop.setProperty(formatter.format(classID+memberID), "\tMethod: " + method.name());
			processExecutable(method);
		}
		
		// List all fields
		DecimalFormat smallFmt = new DecimalFormat(".000");
 		for (FieldDoc field : classItem.fields()) {
 			fieldCounter++;
			memberID += 0.001f;
			prop.setProperty(formatter.format(classID+memberID), "\tField: " + field.name());
			prop.setProperty(formatter.format(classID+memberID)+smallFmt.format(.000f), "\t\ttype: " + field.type());
			prop.setProperty(formatter.format(classID+memberID)+smallFmt.format(.001f), "\t\tmodifiers: " + field.modifiers());
 		}
	}
	
	/**
	 * Processes methods and constructors
	 *
	 * @param member a reference to a method or class doc
	 * @return a properties list for the analyzer report
	 */
	private Properties processExecutable(ExecutableMemberDoc member) {
		DecimalFormat smallFmt = new DecimalFormat(".000");
		float itemID = 0.000f;
		prop.setProperty(formatter.format(classID+memberID)+smallFmt.format(itemID), "\t\tmodifiers: " + member.modifiers());

		// Report on the exceptions declared to be thrown by this executable member
		Type[] types = member.thrownExceptionTypes();
		if (types.length > 0) {
			itemID += 0.001f;
			prop.setProperty(formatter.format(classID+memberID)+smallFmt.format(itemID), "\t\tthrows: ");
			for (int index = 0; index < types.length; index++) {
				itemID += 0.001f;
				prop.setProperty(formatter.format(classID+memberID)+smallFmt.format(itemID), "\t\t\t" + 
					types[index].qualifiedTypeName());
			}
		} else {
			itemID += 0.001f;
			prop.setProperty(formatter.format(classID+memberID)+smallFmt.format(itemID), "\t\tthrows: none");
		}

		// Report on the formal parameters for this executable member		
		Parameter[] params = member.parameters();
		if (params.length > 0) {
			itemID += 0.001f;
			prop.setProperty(formatter.format(classID+memberID)+smallFmt.format(itemID), "\t\tparameters:");
			for (int index = 0; index < params.length; index++) {
				itemID += 0.001f;
				prop.setProperty(formatter.format(classID+memberID)+smallFmt.format(itemID), "\t\t\t" +
					params[index].typeName() + '\t' + params[index].name());
			}
		} else {
			itemID += 0.001f;
			prop.setProperty(formatter.format(classID+memberID)+smallFmt.format(itemID), "\t\tparameters: none");
		}

		// Report on the return type for this executable member
		// TO BE COMPLETED
		
		return null;
	}
	
	/*
	 * Sets the grading breakdown for the doclet.
	 *
	 * @param section Name of the section to set the max grade for
	 * @param maxGrade Maximum grade for the section
	 */
	public void setGradingBreakdown(String section, float maxGrade) {
		// Not needed for this analyzer
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
	 * Returns the grade for the doclet.
	 *
	 * @return the overall grade for the doclet, as a float
	 */
	public float getGrade() {
		return 100.0f;
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
		return "BasicInfo";
	}
}
