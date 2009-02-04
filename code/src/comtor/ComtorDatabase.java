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
  **************************************************************************/
package comtor;

import comtor.analyzers.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.sql.*;

/**
 * The <code>ComtorDatabase</code> class sets up a database connection
 *
 * @author Joe Brigandi
 */
public class ComtorDatabase
{
  private String configFile = null;

  /**
   * Gets a database connection
   *
   * @param configFile Config file for comtor database
   */
  public static Connection getConnection(String configFile)
  {
    // Load the database properties from properties file
    Properties properties = new Properties();
    try
    {
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
      System.out.println(e);
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
        System.out.println("Couldn't get connection!");
        System.out.println("URL: \"" + url+"\"");
        System.out.println("Username: \"" + username+"\"");
//        System.out.println(e);
        e.printStackTrace();
      }
    }


    // Check that a connection was made
    if (con == null)
    {
      System.out.println("Couldn't get connection!");
    }

    return con;
  }
}
