/**
 *  Comment Mentor: A Comment Quality Assessment Tool
 *  Copyright (C) 2010 The College of New Jersey
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
public class ComtorDriver {

	/**
	* Check for doclet-added options. Returns the number of arguments you must
	* specify on the command line for the given option. For example, "-d docs"
	* would return 2.
	*
	* @param	option	Command line argument
	* @return	The number of arguments on the command line for an option
	* 			including the option name itself. Zero return means option
	* 			not known. Negative value means error occurred.
	*/
	public static int optionLength(String option) {
		if (option.equals("--config-file")) {
			return 2;
		}

		if (option.equals("--assignment-id")) {
			return 2;
		}

		return 0;
	}

	/**
	* Required function
	*
	* @param	options the array of command line arguments
	* @param	reporter XXXX
	* @return 	The number of arguments on the command line for an option
	* 			including the option name itself. Zero return means option
	* 			not known. Negative value means error occurred.
	*/
	public static boolean validOptions(String[][] options, 
		DocErrorReporter reporter) {
		return true;
	}

	/**
	* Accepts a property list from the called doclets and puts them
	* in a vector. It then passes the  vector to the report generator.
	*
	* @param	rootDoc the root of the documentation tree
	* @return	boolean value
	*/
	public static boolean start(RootDoc rootDoc) {

		// Look through options to get config file and assignmentId
        	int    assignmentId = -1;
        	String configFile   = null;
        	String options[][]  = rootDoc.options();

        	for (int i = 0; ((assignmentId == -1) || (configFile == null)) &&
        			(i < options.length); i++) {
            		if (options[i][0].equals("--config-file")) {
                		System.out.println("Config file: " + options[i][1]);
                		configFile = options[i][1];
            		} else if (options[i][0].equals("--assignment-id")) {
                		System.out.println("Assignment Id: " + options[i][1]);
                		assignmentId = Integer.parseInt(options[i][1]);
            		}
        	}

        	if ((configFile == null) || (assignmentId == -1)) {
            		System.err.println("Missing either config file or assignment id.");
            		return true;
        	}

        	try {

            	// Get database connection so that we can get doclet grading
            	// settings and parameters
            	Connection db = ComtorDatabase.getConnection(configFile);

		if (db == null) {
			return true;
		}

		//Prepare statement to get grading information
		PreparedStatement docletSectionsPrepStmt =
			db.prepareStatement(
			"SELECT sectionName, maxGrade FROM docletGradeSections " +
			"dGS LEFT JOIN assignmentGradeBreakdownsView aGBW ON " +
			"dGS.docletGradeSectionId = aGBW.docletGradeSectionId " +
			" WHERE docletId IN (SELECT docletId FROM doclets WHERE" +
			"javaName=?) AND assignmentId=?");
		docletSectionsPrepStmt.setLong(2, assignmentId);

		// Prepare statement to get grading parameters
		PreparedStatement docletParametersPrepStmt =
			db.prepareStatement(
			"SELECT parameterName, param FROM docletGradeParameters " +
			"dGP LEFT JOIN assignmentGradeParametersView aGPW ON " +
			"dGP.docletGradeParameterId = aGPW.docletGradeParameterId" +
			"WHERE docletId IN (SELECT docletId FROM doclets WHERE " +
			"javaName=?) AND assignmentId=?");
		docletParametersPrepStmt.setLong(2, assignmentId);

		Vector  v       = new Vector(); 
		Vector  threads = new Vector();
		Scanner scan    = new Scanner(new File("Doclets.txt"));
		String  docletName;

		while (scan.hasNext()) {
			//store doclet as docletName
			docletName = scan.nextLine();
			System.out.println("Attempting to get class: " + docletName);

			try {
				//create class for doclet
				Class c = Class.forName(docletName);

				System.out.println("Attempting to get instance of " +
					"the class: " +	docletName);

				//create new instance of the class
				ComtorDoclet cd = (ComtorDoclet) c.newInstance();

				//Start thread
				System.out.println("Starting " + docletName);

				DocletThread docThrd = new DocletThread();
				docThrd.setRootDoc(rootDoc);
				docThrd.setDocletSectionsPrepStatements(
					docletSectionsPrepStmt, docletParametersPrepStmt);
				docThrd.setAnalyzer(cd);
				docThrd.start();
				threads.add(docThrd);
			} catch (ClassNotFoundException e) {
				System.err.println("Class not found." + e);
			} catch (ExceptionInInitializerError e) {
				System.err.println(e);
			} catch (LinkageError e) {
				System.err.println(e);
			}
		}

		// Wait for all threads
		for (int i = 0; i < threads.size(); i++) {
			DocletThread docThrd = (DocletThread) threads.get(i);
			docThrd.join();
			Properties tmp = docThrd.getProperties();

			if (tmp != null) {
				//add the resulting property list to the vector
				v.addElement(tmp);
			}
		}
            
		/****************************************************************/
		//The following TWO lines are for debugging only. Please consult
		//debug.txt for details, which can be found in the
		//designdoc/tutorials directory. To enable debugging, UNCOMMENT
		//the following TWO lines.
		
		//ComtorDebugger dump = new ComtorDebugger();
		//dump.generateDebugFile(v);
		
		/****************************************************************/

		// Create report generator, set its configuration and pass it the
		// results vector.
		GenerateReport report = new GenerateReport();
		report.setConfigFilename(configFile);
		report.generateReport(v);
            
		scan.close();
		docletSectionsPrepStmt.close();
		db.close();
	}

        // exceptions
        catch (InterruptedException e) {
            System.err.println("InterruptedException");
            System.err.println(e.toString());
        } catch (InstantiationException ie) {
            System.err.println("InstantiationException");
            System.err.println(ie.toString());
        } catch (IllegalAccessException iae) {
            System.err.println("IllegalAccessException");
            System.err.println(iae.toString());
        } catch (IOException ioe) {
            System.err.println("IOException");
            System.err.println(ioe.toString());
        } catch (SQLException sqle) {
            System.err.println("SQLException");
            System.err.println(sqle.toString());
        } catch (Exception e) {
            System.err.println("Other Exception");
            System.err.println(e.toString());
        }

        return true;
    }

    /**
     * The DocletThread class is used to run each doclet as a thread
     *
     * @author Stephen Sigwart
     */
    private static class DocletThread extends Thread {
        private Properties        list = null;
        private ComtorDoclet      doclet;
        private PreparedStatement docletParametersPrepStmt;
        private PreparedStatement docletSectionsPrepStmt;
        private RootDoc           rootDoc;

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
        public void setDocletSectionsPrepStatements(
        		PreparedStatement docletSectionsPrepStmt,
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
            System.out.println("Running analyzer\n" + doclet);

            // Get assignment specific information from database
            try {

                // Get and set grading options
                docletSectionsPrepStmt.setString(1, (doclet.getClass()).getName());

                ResultSet result = docletSectionsPrepStmt.executeQuery();

                if (result.first()) {
                    do {
                        doclet.setGradingBreakdown(result.getString(1),
                        	result.getFloat(2));
                    } while (result.next());
                }

                // Get and set grading paramters
                docletParametersPrepStmt.setString(1,
                	(doclet.getClass()).getName());
                result = docletParametersPrepStmt.executeQuery();

                if (result.first()) {
                    do {
                        doclet.setGradingParameter(
                        	result.getString(1), result.getString(2));
                    } while (result.next());
                }
            } catch (SQLException e) {
                System.out.println(e);
            }

            // call the analyze method to perform the analysis
            list = doclet.analyze(rootDoc);
            System.out.println("Analyzer completed.\n" + doclet);
        }

        /**
         * Runs the doclet on the root document
         *
         * @return Property list created by doclet
         */
        public Properties getProperties() {
            return list;
        }
    }
}
