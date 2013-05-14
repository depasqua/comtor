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
package org.comtor.analyzers;

import java.io.*;
import java.net.*;
import java.util.*;
import com.sun.javadoc.*;

import org.comtor.cloud.*;
import org.comtor.drivers.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Util {
	private static Logger logger = LogManager.getLogger(Util.class.getName());

	/**
	 * Attempts to load the local and web-based versions of data lists. This method is used to load
	 * both dictionary files and java class lists into HashSets for use in the spell checker.
	 * Whenever possible, the local copy will be updated if its internal version number is less than
	 * the web-based version number.
	 *
	 * @param fileName the base of the filename to attempt to load
	 * @param dataList a reference to a hash set of strings which will hold the data list
	 * @param download if set to false, do not attempt to access the host for downloads
	 * @return returns a true value if the data list is loaded (via download or local files). This
	 *         act as a flag for subsequent download attempts (if download is false, skip download)
	 */
	public static boolean loadDataList(String fileName, HashSet<String> dataList, 
			boolean download, String analyzerName) {
		logger.entry();
		if (dataList == null)
			dataList = new HashSet<String> ();
			
		// Attempt to load/fetch the most recent dictionary list.
		Mode currentMode = ComtorStandAlone.getMode();
		File tempdir = ComtorStandAlone.getTempDir();

		String localFileName = tempdir.toString() + System.getProperty("file.separator") + fileName;
		logger.debug("Temp dir for downloading files: " + tempdir);

		String netFileName = "http://www.comtor.org/" + fileName;
		Scanner netScanner = null;
		String netFileVersion = null;
		Scanner localScanner = null;
		String localFileVersion = null;
		boolean result = download;
		
		if (download) {
			try {
				// Attempt to open / read most recent list on web
				netScanner = new Scanner((new URL(netFileName)).openStream());			
				netFileVersion = netScanner.nextLine();
			} catch (MalformedURLException mue) {
				logger.error(mue);  // Bad URL, it's ok, default to local copy if possible.
				result = false;
			} catch (IOException ioe) {
				// No access to the 'net, default to local copy if possible.
				logger.error("Exception from " + analyzerName);
				logger.error("\t" + ioe);
				result = false;
			}
		}
		
		try {
			// Attempt to open / read local copy
			File localFile = new File (localFileName);
			logger.debug("Local file to open: " + localFileName);
			if (localFile.exists()) {
				localScanner = new Scanner(localFile);
				localFileVersion = localScanner.nextLine();
			}

		} catch (IOException ioe) {
			logger.error(ioe);
		}
		
		// Determine what version(s) we have, rewrite local version if needed
		logger.debug("Attempting to detemine local vs. net version");
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
				logger.debug("Local file not found, terminating thread.");
				logger.error(fnfe);
				System.exit(1);
			}

		} else if (localFileVersion == null && netFileVersion == null) {
			// Well, there's not much else we can do...
			logger.error("No files to compare, terminating thread.");
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
					logger.error(fnfe);
					System.exit(1);
				}
			} else
				// Load the local version, no update was necessary
				loadFileToList(localFileName, dataList);
		} else
			// Use the local version, 'net is unavailable.
			loadFileToList(localFileName, dataList);
		
		logger.exit();
		return result;
	}
	
	/**
	 * Attempts to load the specified local file into the specified hash set. The input assumes the
	 * first line is a version number and it is disregarded for this use.
	 *
	 * @param localFileName the name of the local file to read
	 * @param dataList a reference to the hash set into which the file contents are placed
	 */
	private static void loadFileToList(String localFileName, HashSet<String> dataList) {
		logger.entry();
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
			logger.error(ioe);
		}
		logger.exit();
	}

	/**
	 * Returns a comma separated list of parameter types (e.g. int, int, float).
	 *
	 * @param params an array of parameters for an executable object (constructor, method)
	 * @return a string representation of the list of parameter types
	 */
	public static String getParamTypeList(ExecutableMemberDoc exec) {
		logger.entry();
		String paramList = "";
		for (Parameter param : exec.parameters()) {
			if (paramList.length() != 0)
				paramList += ", ";
			paramList += param.type();
		}
		logger.exit();
		return paramList;
	}

	/**
	 * Tests a constructor to determine if it is a compiler-generated nullary constructor.
	 * This test is based on the fact that Javadoc apparently assigns the same line and column
	 * number for the source position of the nullary constructor as it does for the 
	 * constructor's enclosing class.
	 *
	 * @pararm constr The constructor to test
	 * @return returns a true value if the constructor's line and column numbers match
	 * the same for the enclosing class.
	 */
	public static boolean isNullaryConstructor(ConstructorDoc constr) {
		logger.entry();
		int constrLine = constr.position().line();
		int constrCol = constr.position().column();
		int classLine = constr.containingClass().position().line();
		int classCol = constr.containingClass().position().column();
		Parameter[] params = constr.parameters();
		boolean result = false;

		if (constrLine == classLine && constrCol == classCol && params.length != 0)
			result = true;
		logger.exit();
		return result;
	}

	/**
	 * Wraps the specified string afer the length-th character at the first 
	 * occurrence that is a space.
	 *
	 * @param strToWrap the string that will be wrapped
	 * @param length the minimum number of characters per line that will appear
	 * @return a new string wrapped at the first space character after length characters
	 */
	public static String stringWrapAfter(String strToWrap, int length) {
		logger.entry();
		StringBuilder strBld = new StringBuilder(strToWrap);
		int strIndex = 0;

		while ((strIndex = strBld.indexOf(" ", strIndex + length)) != -1)
			strBld.replace(strIndex, strIndex + 1, "\n");

		logger.exit();
		return strBld.toString();
	}
}