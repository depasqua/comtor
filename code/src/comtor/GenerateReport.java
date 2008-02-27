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
  * $Id: GenerateReport.java,v 1.17 2008-02-27 00:56:43 ssigwart Exp $
  **************************************************************************/
package comtor;

import comtor.analyzers.*;
import java.io.*;
import java.util.*;
import java.text.*;

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
    try
    {
      PrintWriter prt = new PrintWriter(new FileWriter("ComtorReport.php")); //write to ComtorReport.php

      // Open php tag
      prt.println("<?php");

      // Connect to the MySQL database
      prt.println("mysql_connect('localhost', 'brigand2', 'joeBrig');");
      prt.println("mysql_select_db('comtor');");

      String dir = System.getProperty("user.dir"); // Current working directory
      BufferedReader rd = new BufferedReader(new FileReader(dir + "/userID.txt")); // Read userID.txt to get userID
      String userID = rd.readLine(); // Store userID from text file
      rd.close();

      // Insert report information into masterReport table
      String query = "INSERT INTO masterReports(userID) VALUES ('" + userID + "')";
      prt.println("mysql_query(\"" + query + "\");");
      // Get the autoincremented id for the data just entered
      prt.println("$id = mysql_insert_id();");

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
        prt.println("$select = mysql_query(\"SELECT reportID FROM reports WHERE reportName='" + report + "'\");");
        prt.println("$row = mysql_fetch_array($select);");
        prt.println("$reportID = $row['reportID'];");

        // Insert id from masterReports and doclet id into the database
        query = "INSERT INTO masterDoclets(masterReportId, docletReportId) VALUES ('{$id}', '{$reportID}')";
        prt.println("mysql_query(\"" + query + "\");");
        // Get the autoincremented id for the data just entered
        prt.println("$subId = mysql_insert_id();");

        // Go through array of property list data pairs (length is -2 cause we don't need the title, description, & dateTime)
        for(int j=0; j < arr.length-2; j++)
        {
          if(arr[j] != null)
          {
            // Insert data pair into the database
            query = "INSERT INTO docletReports(reportId, attribute, value) VALUES ('{$subId}', '" + arr[j] + "', '" + list.getProperty("" + arr[j]) + "')";
            prt.println("mysql_query(\"" + query + "\");");
          }
        }
      }

      // Close php tag
      prt.println("?>");

      prt.close();
    }

    catch(Exception ex)
    {
      System.err.print("An exception was thrown!!!");
      System.err.println(ex.toString());
    }
  }
}
