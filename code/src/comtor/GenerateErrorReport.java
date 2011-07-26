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
import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.*;

/**
 * The <code>GenerateErrorReport</code> class is a tool to insert a
 * compilation error into the database
 *
 * @author Joe Brigandi
 */
public class GenerateErrorReport
{
  /**
   * Takes a vector full of property lists and generates a report.
   *
   * @param args Command line arguments.  args[0] should be the config
   *             filename.
   */
  public static void main(String [] args)
  {
    // Load the database properties from properties file
    Properties properties = new Properties();

    // Load config file
    String configFile = null;
    if (args.length > 0)
      configFile = args[0];

    try {
      if (configFile == null) {
        System.out.println("Database config file not set.");
        return;
      }
      else
        properties.load(new FileInputStream(configFile));
    }
    catch (IOException e) {
      System.out.println("Error opening config file.");
    }
    
    String url = properties.getProperty("databaseUrl");
    String username = properties.getProperty("username");
    String password = properties.getProperty("password");
    String dir = System.getProperty("user.dir");          // Current working directory
    Connection con = null;

    // Try to open file containing javac output
    String output = "";
    try {
      BufferedReader outputReader = new BufferedReader(new FileReader(dir + "/CompileOut.txt"));

      while (outputReader.ready())
        output += outputReader.readLine() + '\n';

      // Close file
      outputReader.close();
    }
    catch (FileNotFoundException e) {
      System.out.println("Error opening compilation output file.");
      return;
    }
    catch (IOException e) {
      System.out.println("I/O Exception Occured.");
      return;
    }

    boolean hasDriver = false;
    // Create class for the driver
    try {
      Class.forName("com.mysql.jdbc.Driver");
      hasDriver = true;
    }
    catch (Exception e) {
      System.out.println("Failed to load MySQL JDBC driver class.");
    }

    // Create connection to database if the driver was found
    if (hasDriver) {
      try {
        con = DriverManager.getConnection(url, username, password);
      }
      catch(SQLException e) {
        System.out.println( "Couldn't get connection!" );
      }
    }

    // Check that a connection was made
    if (con != null) {
      long userEventId = -1;

      // Store results from the report into the database
      try {
        BufferedReader rd = new BufferedReader(new FileReader(dir + "/userId.txt")); // Read userId.txt to get userId
        String userId = rd.readLine(); // Store userId from text file
        rd.close();

        // Insert the report into the table and get the auto_increment id for it
        Statement stmt = con.createStatement();
        stmt.executeUpdate("INSERT INTO userEvents (userId) VALUES ('" + userId + "')");
        ResultSet result = stmt.getGeneratedKeys();
        result.next();
        userEventId = result.getLong(1);

        // Close the statement
        stmt.close();

        // Prepare statement for adding the compilation error to the userEvent
        PreparedStatement compErrorPrepStmt = con.prepareStatement("INSERT INTO userEventCompilationErrors(userEventId, output) VALUES (?, ?)");

        // Insert userEventId and docletId into the database
        compErrorPrepStmt.setLong(1, userEventId);
        compErrorPrepStmt.setString(2, output);
        compErrorPrepStmt.executeUpdate();

        // Close the prepare statements
        compErrorPrepStmt.close();
      }
      catch (Exception e) {
        System.out.println("Exception Occurred");
        System.out.println(e);
      }

      // Store the java files for the report
      try {
        // Prepare statement for storing files
        PreparedStatement filePrepStmt = con.prepareStatement("INSERT INTO files(userEventId, filename, contents) VALUES ("+userEventId+", ?, ?)");

        // Get the list of files from source.txt
        BufferedReader rd = new BufferedReader(new FileReader(dir + "/source.txt")); // Read userId.txt to get userId
        while (rd.ready()) {
           String filename = rd.readLine(); // Store userId from text file
           // Remove the "src/" from the beginning to get the real file name
           String realname = filename.substring(4);
           filePrepStmt.setString(1, realname);

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
        System.out.println("I/O Exception Occured.");
      }
      catch (SQLException e) {
        System.out.println("SQL Exception Occured.");
      }
    }
  }
}
