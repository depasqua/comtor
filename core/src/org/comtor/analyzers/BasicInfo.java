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

import org.comtor.reporting.*;
import com.sun.javadoc.*;
import java.util.*;

/**
 * The BasicInfo analyzer provides basic information about what Javadoc finds. This module is used
 * as a pedagogical tool for those learning to develop for COMTOR as well as a simple debugging
 * aid for those in need of knowing what is going on inside of COMTOR (consider this a dump
 * of the most relevant parts of the RootDoc).
 *
 * @author Peter DePasquale
 */
public class BasicInfo implements ComtorDoclet {
	// JSON analysis report from this module's execution
	private ModuleReport report = new ModuleReport ("Basic Info", "This module reports basic " +
		"informational metrics.");

	// Counters for the packages, classes, and members
	private int numConstructors = 0;
	private int numMethods = 0;
	private int numParameters = 0;
	private int numFields = 0;
	private HashMap<String, Integer> typeMap = new HashMap<String, Integer>();

	/**
	 * The analyze method in each analysis module performs the analysis and
	 * operates on the parsed source code (parsed by Javadoc program) as a 
	 * com.sun.javadoc.RootDoc object. The method returns a property list with 
	 * the grading results and the report.
	 */
	public Properties analyze(RootDoc rootDoc) {
		int numPackages = 0;
		int numClasses = 0;
		
		// Capture the starting time, just prior to the start of the analysis
		long startTime = new Date().getTime();

		// Fetch the list of packages
		PackageDoc[] packages = rootDoc.specifiedPackages();
		if (packages != null) {
			numPackages = packages.length;
			for (PackageDoc packageDoc : packages)
				report.addItem(ReportItem.PACKAGE, packageDoc.name());
		}

		// Fetch the list of classes
		ClassDoc[] classes = rootDoc.classes();
		if (classes != null) {
			numClasses = classes.length;

			// Iterate through the list of classes submitted to Javadoc
			// Obtain and process the basic info about each class
			for (ClassDoc classDoc : classes) {
				report.addItem(ReportItem.CLASS, classDoc.qualifiedName());
				report.addLocation(ReportItem.CLASS, classDoc.position());
				processClass(classDoc);
			}
		}

		// Capture the ending time, just after the termination of the analysis
		long endTime = new Date().getTime();

		report.appendLongToObject("information", "packages identified", numPackages);
		report.appendLongToObject("information", "classes identified", numClasses);
		report.appendLongToObject("information", "constructors identified", numConstructors);
		report.appendLongToObject("information", "methods identified", numMethods);
		report.appendLongToObject("information", "fields identified", numFields);
		report.appendLongToObject("information", "parameters identified", numParameters);

		report.addMetric(numPackages + " package(s) were identified.");
		report.addMetric(numClasses + " class(es) were identified.");
		report.addMetric(numConstructors + " constructor(s) were identified.");
		report.addMetric(numMethods + " method(s) were identified.");
		report.addMetric(numFields + " field(s) were identified.");
		report.addMetric(numParameters + " parameter(s) were identified.");

		report.addTimingString("start time", Long.toString(startTime));
		report.addTimingString("end time", Long.toString(endTime));
		report.addTimingString("execution time", Long.toString(endTime - startTime));

		report.addMapToResults(typeMap);

		return null;
	}
	
	/**
	 * Processes a class
	 * 
	 * @param classItem a reference to the class to analyze/process
	 */
	private void processClass(ClassDoc classItem) {		
		// Fetch the list of constructors, methods, and fields
		ConstructorDoc[] constructors = classItem.constructors();
		numConstructors += constructors.length;
		for (ConstructorDoc constr : constructors) {
			if (!Util.isNullaryConstructor(constr)) {
				report.addItem(ReportItem.CONSTRUCTOR, constr.name() + constr.flatSignature());
				report.addLocation(ReportItem.CONSTRUCTOR, constr.position());
				processExecutable(constr);
			}
		}
		
		MethodDoc[] methods = classItem.methods();
		numMethods += methods.length;
		for (MethodDoc method : methods) {
			if (method.position() != null) {
				report.addItem(ReportItem.METHOD, method.name() + method.flatSignature());
				report.addLocation(ReportItem.METHOD, method.position());
				processExecutable(method);
			}
		}
		
		FieldDoc[] fields = classItem.fields();
		numFields += fields.length;
		for (FieldDoc field : fields) {
			if (field.position() != null) {
				report.addItem(ReportItem.FIELD, field.name());
				report.addLocation(ReportItem.FIELD, field.position());
				addToTypeMap(field.type().qualifiedTypeName());
			}
		}
	}


	/**
	 * Adds the specified type to the type map for histogram analysis.
	 *
	 * @param typeName the fully qualified name of the type to add to the map.
	 */
	private void addToTypeMap(String typeName) {
		if (!typeMap.containsKey(typeName))
			typeMap.put(typeName, 1);
		else 
			typeMap.put(typeName, ((Integer)typeMap.get(typeName)+1));	
	}


	/**
	 * Processes methods and constructors
	 *
	 * @param member a reference to a method or class doc
	 * @return a properties list for the analyzer report
	 */
	private void processExecutable(ExecutableMemberDoc member) {

		// Report on the exceptions declared to be thrown by this executable member
		Type[] types = member.thrownExceptionTypes();
		if (types.length > 0) {
			for (Type type : types)
				report.addItem(ReportItem.THROWS, type.qualifiedTypeName());
		}

		// Report on the formal parameters for this executable member		
		Parameter[] params = member.parameters();
		numParameters += params.length;
		if (params.length > 0) {
			for (Parameter param : params)
				report.addItem(ReportItem.PARAMETER, param.type().qualifiedTypeName());
		}

		//Report on the return type for this executable member
		if (member.isMethod()) {
			Type returnType = ((MethodDoc) member).returnType();
			report.addItem(ReportItem.RETURNS, returnType.qualifiedTypeName());
		}
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

	/**
	 * Returns the string representation of this module's report (JSON format)
	 *
	 * @return a string value containing the JSON report
	 */
	public String getJSONReport() {
		return report.toString();
	}
}
