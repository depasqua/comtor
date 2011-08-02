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
package comtor;

import comtor.analyzers.*;
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
		boolean debug = true;
		
		try {			
			Vector<Properties> resultsVector = new Vector<Properties>(); 
			Vector<DocletThread> threads = new Vector<DocletThread>();
			Scanner scan = new Scanner(new File("Doclets.txt"));

			while (scan.hasNext()) {
				// Store doclet as docletName
				String docletName = scan.nextLine();
	
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
				
			// The following line is for debugging only. Please consult debug.txt for details.
			// (Some of which are old, but may still be helpful - PJD 7/12/11), which can be found
			// in the designdoc/tutorials directory.
			ComtorDebugger.generateDebugFile(resultsVector);

			scan.close();
		}

		// Exceptions from above.  This should be less catch-all and integrated above better.
		catch (InterruptedException e) {
			System.out.println("InterruptedException");
			System.out.println(e.toString());
		} catch (InstantiationException ie) {
			System.out.println("InstantiationException");
			System.out.println(ie.toString());
		} catch (IllegalAccessException iae) {
			System.out.println("IllegalAccessException");
			System.out.println(iae.toString());
		} catch (IOException ioe) {
			System.out.println("IOException");
			System.out.println(ioe.toString());
		} catch (Exception e) {
			System.out.println("Other Exception");
			System.out.println(e.toString());
		}
		return true;
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
}