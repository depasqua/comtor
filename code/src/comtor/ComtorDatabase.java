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
 * The ComtorDatabase class attempts to establish a database connection for
 * logging the data analysis data.
 *
 * @author Joe Brigandi
 * @author Peter DePasquale
 */
public class ComtorDatabase
{
	/**
	 * Gets a database connection
	 *
	 * @param configFile String representation of the database configuration file
	 *
	 * @return a reference to the connection for the database
	 */
	public static Connection getConnection(String configFile)
	{
		// Load the database properties from properties file
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(configFile));
		}
		catch (IOException e) {
			System.out.println("Error opening the database configuration file.");
		}

		String dbUrl = properties.getProperty("databaseUrl");
		String dbUName = properties.getProperty("username");
		String dbPasswd = properties.getProperty("password");
		Connection dbConnection = null;

		try {
			// Attempt to load the database driver
			Class.forName("com.mysql.jdbc.Driver");

			// Establish a connection to the database if the driver was loaded
			dbConnection = DriverManager.getConnection(dbUrl, dbUName, dbPasswd);

			// Check that a connection was made
			if (dbConnection == null) {
				System.out.println("Failed to connect to the database.");
				System.out.println("URL: " + dbUrl);
				System.out.println("Username: " + dbUName);
			}
		}
		catch (SQLException e) {
			System.out.println("Failed to obtain a connection to the database.");
			System.out.println("URL: " + dbUrl);
			System.out.println("Username: " + dbUName);
		}
		catch (Exception e) {
			System.out.println("Failed to load MySQL JDBC driver.");
			System.out.println(e);
		}

		return dbConnection;
	}
}