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
package org.comtor.drivers;

import org.comtor.analyzers.*;
import com.sun.javadoc.*;
import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;

/**
 * The ComtorDriver class is a tool to run COMTOR doclets (analysis modules)
 * and to pass a vector of property lists to the COMTOR report generator.
 *
 * @author Joe Brigandi
 * @author Stephen Sigwart
 * @author Peter DePasquale
 */
public class ComtorStandAlone extends Doclet {

	// Stores the current operational mode
	private static Mode currentMode = Mode.CLI;
	
	/**
	* Accepts a property list from the called doclets and puts them
	* in a vector. It then passes the vector to the report generator.
	*
	* @param	rootDoc the root of the documentation tree
	* @return	boolean true if a successful execution, false otherwise
	*/
	public static boolean start(RootDoc rootDoc) {

		// Look through options to get config file and assignmentId
		int assignmentId = -1;
		String configFile = null;
		String options[][] = rootDoc.options();
		boolean done = false;
		
		try {			
			Vector<Properties> resultsVector = new Vector<Properties>(); 
			Vector<DocletThread> threads = new Vector<DocletThread>();
			Properties docletSet = Comtor.getDocletList();

			for (Object elem : docletSet.values()) {
				String docletName = (String) elem;
	
				try {
					// Create class for doclet
					Class docletClass = Class.forName(docletName);
	
					// Create new instance of the class and start thread
					ComtorDoclet comtorDoclet = (ComtorDoclet) docletClass.newInstance();
					DocletThread docThrd = new DocletThread();
					docThrd.setRootDoc(rootDoc);					
					docThrd.setAnalyzer(comtorDoclet);
					docThrd.start();
					threads.add(docThrd);
				} catch (ClassNotFoundException e) {
					System.out.println("Class not found: " + e);
				} catch (ExceptionInInitializerError e) {
					System.out.println(e);
				} catch (LinkageError e) {
					System.out.println(e);
				}
			}

			// Wait for all threads to complete
			for (int i = 0; i < threads.size(); i++) {
				DocletThread docThrd = threads.get(i);
				docThrd.join();
	
				if (docThrd.getProperties() != null)
					// Add the resulting property list to the vector
					resultsVector.addElement(docThrd.getProperties());
			}
				
			generateOutputFile(resultsVector);
		}

		// Exceptions from above.  This should be less catch-all and integrated above better.
		catch (InterruptedException e) {
			System.out.println(e.toString());
		} catch (InstantiationException ie) {
			System.out.println(ie.toString());
		} catch (IllegalAccessException iae) {
			System.out.println(iae.toString());
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return true;
	}

	private static void generateOutputFile(Vector<Properties> resultsVector) {
	
		// Set the path for the location of the report output file.
		String path = Comtor.getCodeDir() + System.getProperty("file.separator") +
			"comtorReport.txt";
		try {

			// Check to see the output file currently exists, if so, delete the old file to
			// replace it with a new one. We should improve this to use a date/time stamp in
			// the file name so that deletion is redundant.
			File outputFile = new File(path);
			if (outputFile.exists())
				outputFile.delete();

			// Create a new output report file, and prepare it for writing
			PrintWriter outFilePW = new PrintWriter(new BufferedWriter(new FileWriter(path)));

			// Write the header of the debug file
			outFilePW.println("COMTOR Execution Report");
			outFilePW.println((new java.util.Date()).toString());
			outFilePW.println("==========================================");
			
			// For each vector element, each of which is a properties list from the reports created
 			for (Properties results : resultsVector) {
				// Fetch the keys, add them to an array, sort and then print results
				Enumeration keyList = results.keys();
				String keyArray[] = new String[results.size()];
				int index = 0;
				for (Enumeration e = keyList; e.hasMoreElements(); ) {
					String key = (String) e.nextElement();
					keyArray[index++] = key;
				}
				Arrays.sort(keyArray);
				
				for (String key : keyArray)
					outFilePW.println(key + '\t' + results.getProperty(key));

				outFilePW.println("------------------------------------------------------");
			}
			outFilePW.close();
		}
		catch (IOException e) {
			System.out.println(e);
			System.out.println("Path: " + path);
		}
	}

	/**
	 * The DocletThread class is used to run each doclet as a thread
	 *
	 * @author Stephen Sigwart
	 */
	private static class DocletThread extends Thread {
		private Properties list = null;
		private ComtorDoclet doclet;
		private PreparedStatement docletParametersPrepStmt;
		private PreparedStatement docletSectionsPrepStmt;
		private RootDoc rootDoc;
		
		/**
		 * Sets the root document with which the doclet will work
		 *
		 * @param rootDoc A reference to the RootDoc
		 */
		public void setRootDoc(RootDoc rootDoc) {
			this.rootDoc = rootDoc;
		}
        
		/**
		 * Sets the doclet that will process the root document
		 *
		 * @param docletSectionsPrepStmt Prepared statement used to get doclet
		 *                               grading section info
		 * @param docletParametersPrepStmt Prepared statement used to get doclet
		 *                                 parameters for grading
		 */
		public void setDocletSectionsPrepStatements(PreparedStatement docletSectionsPrepStmt,
				PreparedStatement docletParametersPrepStmt) {
			this.docletSectionsPrepStmt   = docletSectionsPrepStmt;
			this.docletParametersPrepStmt = docletParametersPrepStmt;
		}

		/**
		 * Sets the doclet that will process the root document
		 *
		 * @param doclet Doclet that will process the root document
		 */
		public void setAnalyzer(ComtorDoclet doclet) {
			this.doclet = doclet;
		}

		 /**
		 * Runs the doclet on the root document
		 */
		public void run() {
			// Call the analyze method to perform the analysis
			list = doclet.analyze(rootDoc);
		}

		/**
		 * Returns the list of properties.
		 *
		 * @return Property list created by doclet
		 */
		public Properties getProperties() {
			return list;
		}
	}
	
	/**
	 * Returns the current mode of operation
	 *
	 * @return the current mode of operation as an enum value
	 */
	public static Mode getMode() {
		return currentMode;
	}
	
	/**
	 * Sets the current mode of operation
	 * 
	 * @param newMode a enum value of the current mode
	 * @see org.comtor.analyzers.Mode
	 */
	public static void setMode(Mode newMode) {
		currentMode = newMode;
	}
}