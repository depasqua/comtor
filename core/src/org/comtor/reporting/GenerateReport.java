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
package org.comtor.reporting;

import org.comtor.analyzers.*;
import org.comtor.drivers.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.sql.*;

/**
 * The GenerateReport class is a tool to generate a report from a vector containing the analysis
 * results in property lists.
 *
 * @author Joe Brigandi
 * @author Peter DePasquale
 */
public class GenerateReport {
	private String configFile = null;

	/**
	 * Sets the config file to use for database connection
	 *
	 * @param filename Path to database config file
	 */
	public void setConfigFilename(String filename) {
		configFile = filename;
	}

	/**
	 * Takes a vector full of property lists and generates a report.
	 *
	 * @param resultsVector is a Vector of property lists
	 */
	public void generateReport(Vector<Properties> resultsVector) {
		// Load the database properties from properties file
		Properties properties = new Properties();
		Connection conn = ComtorDriver.getConnection(configFile);

		// Check that a connection was made
		if (conn != null) {
			long userEventId = -1;
			
			// Store results from the report into the database
			String dir = System.getProperty("user.dir");
			try {
				// Open the userId.txt file and read the userId 
				BufferedReader rd = new BufferedReader(new FileReader(dir + "/userId.txt"));
				String userId = rd.readLine();
				rd.close();
				
				// Insert the report into the table and get the auto_increment id for it
				Statement stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO userEvents (userId) VALUES ('" + userId + "')");
				ResultSet result = stmt.getGeneratedKeys();
				result.next();
				userEventId = result.getLong(1);
				stmt.close();
				
				// Prepare database statements
				PreparedStatement docletPrepStmt = conn.prepareStatement("SELECT docletId FROM " +
					"doclets WHERE docletName=? LIMIT 1");
				PreparedStatement docletEventPrepStmt = conn.prepareStatement("INSERT INTO " +
					"docletEvents(userEventId, docletId, score) VALUES (?, ?, ?)");
				PreparedStatement docletOutputItemsPrepStmt = conn.prepareStatement("INSERT " +
					"INTO docletOutputItems(docletEventId, attribute, value) VALUES (?, ?, ?)");
				
				// Go through vector to access property lists (one for each doclet)
				for (int i=0; i < resultsVector.size(); i++) {
					Properties list = resultsVector.get(i);
					
					// Store property list in an array
					String[] arr = new String[0];
					arr = list.keySet().toArray(arr);
					Arrays.sort(arr); //sort array
					
					// Query database to get the docletId
					String report = list.getProperty("title");
					docletPrepStmt.setString(1, report);
					result = docletPrepStmt.executeQuery();
					long docletId = 0;
					if (result.next())
						docletId = result.getLong(1);
					
					// Get score for the doclet
					String tmp = list.getProperty("score");
					float score = (float)0.0;
					if (tmp != null)
						score = Float.parseFloat(tmp);
					
					// Insert userEventId and docletId into the database
					docletEventPrepStmt.setLong(1, userEventId);
					docletEventPrepStmt.setLong(2, docletId);
					docletEventPrepStmt.setFloat(3, score);
					System.out.println("Adding doclet event.");
					System.out.println(docletEventPrepStmt.executeUpdate());
					
					// Get the autoincremented id for the data just entered
					result = docletEventPrepStmt.getGeneratedKeys();
					result.next();
					long docletEventId = result.getLong(1);
					
					// Go through array of property list data pairs (length is -2 because we don't need "title" and "score")
					docletOutputItemsPrepStmt.setLong(1, docletEventId);
					for (int j=0; j < arr.length-2; j++) {
						if (arr[j] != null) {
						  // Insert data pair into the database
						  docletOutputItemsPrepStmt.setString(2, arr[j]);
						  docletOutputItemsPrepStmt.setString(3, list.getProperty("" + arr[j]));
						  docletOutputItemsPrepStmt.executeUpdate();
						}
					}
    		    }

				// Close the prepared statements
				docletPrepStmt.close();
				docletEventPrepStmt.close();
				docletOutputItemsPrepStmt.close();
			}
			catch (Exception e) {
				System.out.println("Exception Occurred");
				System.out.println(e);
			}

			// Store the Java files for the report
			try {
				// Prepare statement for storing files
				PreparedStatement filePrepStmt = conn.prepareStatement("INSERT INTO " +
						"files(userEventId, filename, contents) VALUES ("+userEventId+", ?, ?)");
				
				// Obtain the list of source code files from source.txt
				BufferedReader rd = new BufferedReader(new FileReader(dir + "/source.txt"));
				
				while (rd.ready()) {
					String filename = rd.readLine(); // Store userId from text file
					// Remove the "src/" from the beginning to get the real file name
					//String realname = filename.substring(4);
					filePrepStmt.setString(1, filename);
					
					// Read in the contents of the files
					String contents = "";
					File javaFile = new File(dir+"/"+filename);
					int length = (int)javaFile.length();
					
					// Add parameter for file contents to the prepared statement and execute it
					filePrepStmt.setCharacterStream(2, new BufferedReader(new FileReader(javaFile)), length);
					filePrepStmt.executeUpdate();
				}
				rd.close();
			}
			catch (IOException e) {
				System.out.println("I/O Exception Occurred.");
			}
			catch (SQLException e) {
				System.out.println("SQL Exception Occurred.");
			}
		}
	}
}
