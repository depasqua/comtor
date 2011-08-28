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
package comtor.analyzers;

import java.io.*;
import java.net.*;
import java.util.*;

public class Util {
	/**
	 * Attempts to load the local and web-based versions of data lists. This method is used to load
	 * both dictionary files and java class lists into HashSets for use in the spell checker.
	 * Whenever possible, the local copy will be updated if its internal version number is less than
	 * the web-based version number.
	 *
	 * @param fileName the base of the filename to attempt to load
	 * @param dataList a reference to a hash set of strings which will hold the data list
	 */
	public static void loadDataList(String fileName, HashSet<String> dataList) {
		if (dataList == null)
			dataList = new HashSet<String> ();
			
		// Attempt to load/fetch the most recent dictionary list.
		String netFileName = "http://www.comtor.org/" + fileName;
		String localFileName = System.getProperty("user.dir") +
							   System.getProperty("file.separator") + fileName;
		Scanner netScanner = null;
		String netFileVersion = null;
		Scanner localScanner = null;
		String localFileVersion = null;
		
		try {
			// Attempt to open / read most recent list on web
			netScanner = new Scanner((new URL(netFileName)).openStream());			
			netFileVersion = netScanner.nextLine();
		} catch (MalformedURLException mue) {
			System.err.println(mue);  // Bad URL, it's ok, default to local copy if possible.
		} catch (IOException ioe) {
			System.err.println(ioe); // No access to the 'net, default to local copy if possible.
		}

		try {
			// Attempt to open / read local copy
			File localFile = new File (localFileName);
			if (localFile.exists()) {
				localScanner = new Scanner(localFile);
				localFileVersion = localScanner.nextLine();
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
		
		// Determine what version(s) we have, rewrite local version if needed
		if (localFileVersion == null && netFileVersion != null) {
			// Write a new local copy and use the 'net version
			try {
				PrintStream localFile = new PrintStream (new File(localFileName));
				localFile.println(netFileVersion);
				while (netScanner.hasNextLine()) {
					String line = netScanner.nextLine();
					localFile.println(line);
					dataList.add(line.toLowerCase());
				}
				localFile.close();
			} catch (FileNotFoundException fnfe) {
				System.err.println(fnfe);
				System.exit(1);
			}
		} else if (localFileVersion == null && netFileVersion == null) {
			// Well, there's not much else we can do...
			System.exit(1);
		} else if (localFileVersion != null && netFileVersion != null) {
			// Check for freshness of local version
			if (localFileVersion.compareTo(netFileVersion) < 0) {
				// Update local copy, net copy is fresher
				try {
					PrintStream localFile = new PrintStream (new File(localFileName));
					localFile.println(netFileVersion);
					while (netScanner.hasNextLine()) {
						String line = netScanner.nextLine();
						localFile.println(line);
						dataList.add(line.toLowerCase());
					}
					localFile.close();
				} catch (FileNotFoundException fnfe) {
					System.err.println(fnfe);
					System.exit(1);
				}
			} else
				// Load the local version, no update was necessary
				loadFileToList(localFileName, dataList);
		} else
			// Use the local version, 'net is unavailable.
			loadFileToList(localFileName, dataList);
	}
	
	/**
	 * Attempts to load the specified local file into the specified hash set. The input assumes the
	 * first line is a version number and it is disregarded for this use.
	 *
	 * @param localFileName the name of the local file to read
	 * @param dataList a reference to the hash set into which the file contents are placed
	 */
	private static void loadFileToList(String localFileName, HashSet<String> dataList) {
		try {
			Scanner scan = null;
			File localFile = new File (localFileName);
			if (localFile.exists()) {
				scan = new Scanner(localFile);
				scan.nextLine(); // drop the version number, we don't care right here
			}
			while (scan.hasNextLine())
				dataList.add(scan.nextLine().toLowerCase());
	
			scan.close();
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
	}
}