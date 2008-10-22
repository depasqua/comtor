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
  * $Id: GenerateReport.java,v 1.22 2008-11-05 22:08:16 ssigwart Exp $
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
  private String configFile = null;

  /**
   * Sets the config file to use for database connection
   *
   * @param filename Path to database config file
   */
  public void setConfigFilename(String filename)
  {
    configFile = filename;
  }

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
      if (configFile == null)
      {
        System.out.println("Database config file not set.");
        return;
      }
      else
        properties.load(new FileInputStream(configFile));
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
      long userEventId = -1;

      // Store results from the report into the database
      try
      {
        dir = System.getProperty("user.dir"); // Current working directory
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

        // Prepare statement for getting docletId
        PreparedStatement docletPrepStmt = con.prepareStatement("SELECT docletId FROM doclets WHERE docletName=? LIMIT 1");

        // Prepare statement for the docletEvents of a userEvent
        PreparedStatement docletEventPrepStmt = con.prepareStatement("INSERT INTO docletEvents(userEventId, docletId, score) VALUES (?, ?, ?)");

        // Prepare statement for inserting doclet outputs
        PreparedStatement docletOutputItemsPrepStmt = con.prepareStatement("INSERT INTO docletOutputItems(docletEventId, attribute, value) VALUES (?, ?, ?)");

        // Go through vector to access property lists (one for each doclet)
        for(int i=0; i < v.size(); i++)
        {
          Properties list = new Properties();
          list = (Properties)v.get(i);

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
          {
            docletId = result.getLong(1);
          }

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
          for(int j=0; j < arr.length-2; j++)
          {
            if(arr[j] != null)
            {
              // Insert data pair into the database
              docletOutputItemsPrepStmt.setString(2, arr[j]);
              docletOutputItemsPrepStmt.setString(3, list.getProperty("" + arr[j]));
              docletOutputItemsPrepStmt.executeUpdate();
            }
          }
        }

        // Close the prepare statements
        docletPrepStmt.close();
        docletEventPrepStmt.close();
        docletOutputItemsPrepStmt.close();
      }
      catch (Exception e)
      {
        System.out.println("Exception Occurred");
        System.out.println(e);
      }


      // Store the java files for the report
      try
      {
        // Prepare statement for storing files
        PreparedStatement filePrepStmt = con.prepareStatement("INSERT INTO files(userEventId, filename, contents) VALUES ("+userEventId+", ?, ?)");

        // Get the list of files from source.txt
        BufferedReader rd = new BufferedReader(new FileReader(dir + "/source.txt")); // Read userId.txt to get userId
        while (rd.ready())
        {
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
