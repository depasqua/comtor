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

/**
 * The BasicInfo analyzer provides basic information about what Javadoc finds. This module is used
 * as a pedagogical tool for those learning to develop for COMTOR as well as a simple debugging
 * aid for those in need of knowing what is going on inside of COMTOR (consider this a dump
 * of the most relevant parts of the RootDoc).
 *
 * @author Peter DePasquale
 */
public class BasicInfo implements ComtorDoclet {
	/**
	 * The analyze method in each analysis module performs the analysis and
	 * operates on the parsed source code (parsed by Javadoc program) as a 
	 * com.sun.javadoc.RootDoc object. The method returns a property list with 
	 * the grading results and the report.
	 */
	public Properties analyze(RootDoc root) {
		Properties prop = new Properties();
		if (root == null)
			return null;
		
		// Iterate through the list of classes submitted to Javadoc
		for (ClassDoc classItem : root.classes()) {
			Properties result = processClass(classItem);
			if (result != null)
				prop.add(result);
			
			// List all inner classes
			for (ClassDoc innerclass : classItem.innerClasses()) {
				result = processClass(innerclass);
				if (result != null)
				prop.add(result);
			}
		}
		
		return prop;
	}
	
	/**
	 * Processes a class
	 * 
	 * @param classItem a reference to the class to analyze/process
	 * @return a properties list for the analyzer report
	 */
	private Properties processClass(ClassDoc classItem) {
		System.out.println("Class: " + classItem.qualifiedName());
		
		// List all constructors
		for (ConstructorDoc constrs : classItem.constructors()) {
			System.out.println("\tConstructor: " + constrs.qualifiedName());
			processExecutable(constrs);
		}
		
		// List all methods
		for (MethodDoc method : classItem.methods()) {
			System.out.println("\tMethod: " + method.qualifiedName());
			processExecutable(method);
		}
		
		// List all fields
		for (FieldDoc field : classItem.fields()) {
			System.out.println("\tField: " + field.qualifiedName());
			System.out.println("\t\ttype: " + field.type());
			System.out.println("\t\tcomment text: " + field.commentText());
			System.out.println("\t\tmodifiers: " + field.modifiers());
			System.out.println();
		}
		return null;
	}
	
	/**
	 * Processes methods and constructors
	 *
	 * @param member a reference to a method or class doc
	 * @return a properties list for the analyzer report
	 */
	private Properties processExecutable(ExecutableMemberDoc member) {
		System.out.println("\t\tcomment text: " + member.commentText());
		System.out.println("\t\tmodifiers: " + member.modifiers());
		System.out.print("\t\tthrows: ");
		Type[] types = member.thrownExceptionTypes();
		if (types.length > 0) {
			System.out.println();
			for (int index = 0; index < types.length; index++)
				System.out.println("\t\t\t" + types[index].qualifiedTypeName());
			System.out.println();
		} else
			System.out.println("none.");

		System.out.print("\t\tparameters: ");
		Parameter[] params = member.parameters();
		if (params.length > 0) {
			System.out.println();
			for (int index = 0; index < params.length; index++)
				System.out.println("\t\t\t" + params[index].typeName() + '\t' + params[index].name());
			System.out.println();
		} else {
			System.out.println("none.");
			System.out.println();
		}
		return null;
	}
	
	/*
	 * Sets the grading breakdown for the doclet.
	 *
	 * @param section Name of the section to set the max grade for
	 * @param maxGrade Maximum grade for the section
	 */
	public void setGradingBreakdown(String section, float maxGrade)
	{
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
