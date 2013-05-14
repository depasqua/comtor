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

import com.sun.javadoc.*;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.comtor.reporting.*;
import org.comtor.analyzers.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * The ComtorStandAlone class is a tool to run COMTOR doclets (analysis modules)
 * and to pass a vector of property lists to the COMTOR report generator. This class is 
 * used by the Comtor class once execution has started.  Essentially, it is this class that
 * is inserted into Javadoc as a Doclet to supercede the Javadoc output functionality.
 *
 * @author Joe Brigandi
 * @author Stephen Sigwart
 * @author Peter DePasquale
 */
public class ComtorStandAlone extends Doclet {

	// Stores the current operational mode
	private static Mode currentMode = Mode.CLI;
	private static File tempDir = null;
	private static Logger logger = LogManager.getLogger(ComtorStandAlone.class.getName());

	/**
	* Accepts a rootDoc object (the parsed Java code tree) and calls the 
	* analysis modules in parallel.
	*
	* @param	rootDoc the root of the documentation tree
	* @return	boolean true if a successful execution, false otherwise
	*/
	public static boolean start(RootDoc rootDoc) {
		logger.entry();

		// Look through options to get config file and assignmentId
		int assignmentId = -1;
		String configFile = null;
		String options[][] = rootDoc.options();
		boolean done = false;
		try {
			Vector<String> jsonReportVector = new Vector<String>();

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
					logger.debug("Adding thread for: " + docletName);

				} catch (ClassNotFoundException e) {
					logger.error("Class not found: " + e);

				} catch (ExceptionInInitializerError eiie) {
					logger.error(eiie);

				} catch (LinkageError le) {
					logger.error(le);

				} catch (InstantiationException inste) {
					logger.error(inste);

				} catch (IllegalAccessException iae) {
					logger.error(iae);
				}
			}

			// Wait for all threads to complete
			for (int i = 0; i < threads.size(); i++) {
				DocletThread docThrd = threads.get(i);
				docThrd.join();
				logger.debug("Thread ended for: " + docThrd.toString());
				if (docThrd.getJSONReport() != null)
					// Fetch the JSON report for this analyzer
					jsonReportVector.addElement(docThrd.getJSONReport());
			}

			logger.debug("Commencing text report creation.");
			(new TextReporter()).generateReportFile(jsonReportVector);

			logger.debug("Commencing HTML report creation.");
			(new HTMLReporter()).generateReportFile(jsonReportVector);

		} catch (Exception e) {
			// Exceptions from above.  This should be less catch-all and integrated above better.
			logger.error(e);
		}

		return logger.exit(true);
	}
	
	/**
	 * Returns the current mode of operation
	 *
	 * @return the current mode of operation as an enum value
	 */
	public static Mode getMode() {
		logger.entry();
		return logger.exit(currentMode);
	}
	
	/**
	 * Sets the current mode of operation
	 * 
	 * @param newMode a enum value of the current mode
	 * @see org.comtor.analyzers.Mode
	 */
	public static void setMode(Mode newMode) {
		logger.entry();
		currentMode = newMode;
		logger.exit();
	}

	public static File getTempDir () {
		logger.entry();
		return logger.exit(tempDir);
	}

	public static void setTempDir(File dir) {
		logger.entry();
		tempDir = dir;
		logger.exit();
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
		 * @param doclet The doclet (analysis module) that will process the root document.
		 */
		public void setAnalyzer(ComtorDoclet doclet) {
			this.doclet = doclet;
		}

		 /**
		 * Runs the doclet on provided root document.
		 */
		public void run() {
			// Call the analyze method to perform the analysis
			list = doclet.analyze(rootDoc);
		}

		/**
		 * Returns the list of properties.
		 *
		 * @return Returns the property list created by doclet
		 */
		public Properties getProperties() {
			return list;
		}

		/**
		 * Returns the analyzer's report as a JSON stirng
		 *
		 * @return the analyzer's analysis report
		 */
		public String getJSONReport() {
			return doclet.getJSONReport();
		}

		/** 
		 * Returns the string representation of this analyzer modules' name.
		 *
		 * @return the string name of the analyzer
		 */
		public String toString() {
			return doclet.toString();
		}
	}
}