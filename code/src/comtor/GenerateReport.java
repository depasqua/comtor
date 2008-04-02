/***************************************************************************
  *  Comment Mentor: A Comment Quality Assessment Tool
  *  Copyright (C) 2005 Michael E. Locasto
  *
  *  This program is free software; you can redistribute it and/or modify
  *  it under the terms of the GNU General Public License as published by
  *  the Free Software Foundation; either version 2 of the License, or
  *  (at your option) any later version.
  *
  *  This program is distributed in the hope that it will be useful, but
  *  WITHOUT ANY WARRANTY; without even the implied warranty of
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  *  General Public License for more details.
  *
  *  You should have received a copy of the GNU General Public License
  *  along with this program; if not, write to the:
  *  Free Software Foundation, Inc.
  *  59 Temple Place, Suite 330
  *  Boston, MA  02111-1307  USA
  *
  * $Id: GenerateReport.java,v 1.18 2008-04-02 02:19:27 ssigwart Exp $
  **************************************************************************/
package comtor;

import comtor.analyzers.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.sql.*;

/**
 * The <code>GenerateReport</code> class is a tool to generate a report
 * from a vector containing property lists.
 *
 * @author Joe Brigandi
 */
public class GenerateReport
{
  /**
   * Takes a vector full of property lists and generates a report.
   *
   * @param v is a Vector of property lists
   */
  public void generateReport(Vector v)
  {
    // Load the database properties from properties file
    Properties properties = new Properties();
    try
    {
      properties.load(new FileInputStream("/home/sigwart4/private/java.properties"));
    }
    catch (IOException e)
    {
      System.out.println("Error opening config file");
    }
    String url = properties.getProperty("databaseUrl");
    String username = properties.getProperty("username");
    String password = properties.getProperty("password");
    Connection con = null;


    boolean hasDriver = false;
    // Create class for the driver
    try
    {
      Class.forName("com.mysql.jdbc.Driver");
      hasDriver = true;
    }
    catch (Exception e)
    {
      System.out.println("Failed to load MySQL JDBC driver class.");
    }

    // Create connection to database if the driver was found
    if (hasDriver)
    {
      try
      {
        con = DriverManager.getConnection(url, username, password);
      }
      catch(SQLException e)
      {
        System.out.println( "Couldn't get connection!" );
      }
    }


    // Check that a connection was made
    if (con != null)
    {
      String dir = "";
      long id = -1;

      // Store results from the report into the database
      try
      {
        dir = System.getProperty("user.dir"); // Current working directory
        BufferedReader rd = new BufferedReader(new FileReader(dir + "/userID.txt")); // Read userID.txt to get userID
        String userID = rd.readLine(); // Store userID from text file
        rd.close();

        // Insert the report into the table and get the auto_increment id for it
        Statement stmt = con.createStatement();
        stmt.executeUpdate("INSERT INTO masterReports(userID) VALUES ('" + userID + "')");
        ResultSet result = stmt.getGeneratedKeys();
        result.next();
        id = result.getLong(1);

        // Close the statement
        stmt.close();

        // Prepare statement for getting doclet id
        PreparedStatement reportPrepStmt = con.prepareStatement("SELECT reportID FROM reports WHERE reportName=? LIMIT 1");

        // Prepare statement for the subreports of a report
        PreparedStatement subReportPrepStmt = con.prepareStatement("INSERT INTO masterDoclets(masterReportId, docletReportId) VALUES (?, ?)");

        // Prepare statement for inserting properties
        PreparedStatement propertyPrepStmt = con.prepareStatement("INSERT INTO docletReports(reportId, attribute, value) VALUES (?, ?, ?)");

        // Go through vector to access property lists (one for each doclet)
        for(int i=0; i < v.size(); i++)
        {
          Properties list = new Properties();
          list = (Properties)v.get(i);

          // Store property list in an array
          String[] arr = new String[0];
          arr = list.keySet().toArray(arr);
          Arrays.sort(arr); //sort array

          // Query database to get the id of the doclet
          String report = list.getProperty("title");
          reportPrepStmt.setString(1, report);
          result = reportPrepStmt.executeQuery();
          long docletId = 0;
          if (result.next())
          {
            docletId = result.getLong(1);
          }

          // Insert id from masterReports and doclet id into the database
          subReportPrepStmt.setLong(1, id);
          subReportPrepStmt.setLong(2, docletId);
          subReportPrepStmt.executeUpdate();

          // Get the autoincremented id for the data just entered
          result = subReportPrepStmt.getGeneratedKeys();
          result.next();
          long subReportId = result.getLong(1);

          // Go through array of property list data pairs (length is -2 cause we don't need the title, description, & dateTime)
          for(int j=0; j < arr.length-2; j++)
          {
            if(arr[j] != null)
            {
              // Insert data pair into the database
              propertyPrepStmt.setLong(1, subReportId);
              propertyPrepStmt.setString(2, arr[j]);
              propertyPrepStmt.setString(3, list.getProperty("" + arr[j]));
              propertyPrepStmt.executeUpdate();
            }
          }
        }

        // Close the prepare statements
        reportPrepStmt.close();
        subReportPrepStmt.close();
        propertyPrepStmt.close();
      }
      catch (Exception e)
      {
        System.out.println("Exception Occurred");
      }


      // Store the java files for the report
      try
      {
        // Prepare statement for storing files
        PreparedStatement filePrepStmt = con.prepareStatement("INSERT INTO files(reportId, filename, contents) VALUES ("+id+", ?, ?)");

        // Get the list of files from source.txt
        BufferedReader rd = new BufferedReader(new FileReader(dir + "/source.txt")); // Read userID.txt to get userID
        while (rd.ready())
        {
           String filename = rd.readLine(); // Store userID from text file
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
      catch (IOException e)
      {
        System.out.println("I/O Exception Occured.");
      }
      catch (SQLException e)
      {
        System.out.println("SQL Exception Occured.");
      }
    }
  }
}
