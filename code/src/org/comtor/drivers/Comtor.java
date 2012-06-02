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
package org.comtor.drivers;

import java.io.*;
import java.util.*;

/**
 * This class contains a main method (wrapper) used to call the stand alone version of COMTOR. This 
 * version is used to check the processing and output of modules as they are developed (debugging),
 * as well, it is the 'standalone' version of the application to be deployed for research and/or
 * to the cloud.
 *
 * @author Peter DePasquale
 */
public class Comtor {
	private static HashMap<String,String> argsMap = new HashMap<String, String>();
	private static Properties docPropList = new Properties();
	private static String codeDir = "";
	
	/**
	 * Main method for the Comtor standalone program.
	 *
	 * @param args Parameters for the program. The only useful parameters are ones we can directly
	 * pass to Javadoc, as everything really starts there and calls our doclet.
	 */
	public static void main (String [] args) {
		// Validate the argument list and print the options listing if validation fails.
		if (!checkArgs(args)) {
			System.err.println(optionsListing());
			System.exit(1);
		} else {
			// Process the arguments
			if (argsMap.containsKey("dir")) {
				start(argsMap.get("dir"));
			}
		}
	}
	
	/**
	 * This processes the source code path and commences execution. More importantly, this method
	 * is where other interface can connect to the analyzer. That is, the cloud implementation can
	 * simply call this method to commence execution.
	 *
	 * @param dirpath the directory path (a single dir) which is searched for both pacakged and
	 * non-packaged source code.
	 */
	public static void start(String dirpath) {
		Vector<String> jdocsoptions = new Vector<String>();

		if (dirpath.equals("."))
			dirpath = System.getProperty("user.dir");

		codeDir = dirpath;
	
		// Loads the list of doclets to execute from the config file in the from which
		// execution is originating
		loadDocletList(dirpath);
		
		// Find non-packaged code in the specified dir and add them to the options vector
		String[] fileList = findFiles(dirpath);
		for (String file : fileList)
			jdocsoptions.add(file);
	
		// Find packages in the specified dir and add them to the options vector.
		System.err.println("Found the following packages:");
		LinkedList<String> packageList = findPackages(codeDir);
		
		// Add the -private option to the javadoc parameter list, ensuring that ALL classes
		// and members are processed.
		jdocsoptions.add("-private");

		jdocsoptions.add("-sourcepath");
		jdocsoptions.add(codeDir);

		// Add the list of packages to the options list being sent to Javadoc
		for (String pkg : packageList)
			jdocsoptions.add(pkg);

		// Process the options list for preparation in handing it to Javadoc
		System.err.println("\njavadoc options: ");

		String[] optionslist = new String[jdocsoptions.size()];
		int index = 0;
		for (String option : jdocsoptions) {
			optionslist[index] = jdocsoptions.elementAt(index);
			System.err.print(" " + optionslist[index++]);
		}
		System.err.println();

		// Execute the javadoc processor handing it a String name of the application (for error
		// output), the name of the doclet to execute (our stand alone version of the master doclet
		// that does attempt to database its results), and an array of arguments.
		com.sun.tools.javadoc.Main.execute("COMTOR", "org.comtor.drivers.ComtorStandAlone", optionslist);
		
		System.err.println("Execution complete.");
	}
	
	/**
	 * Returns the directory in which unjarred source code files exist for processing. This was
	 * required since we can't depend on the user.dir system property when on Tomcat/in the cloud.
	 *
	 * @return the string path of the location of the source code files (which in turn will be the
	 * location of the report output file).
	 */
	public static String getCodeDir() {
		return codeDir;
	}
	
	/**
	 * Locates all Java packages in the provided path.
	 *
	 * @param dirpath a directory where packaged code may exist
	 * @return a linked list of String names of Java packages
	 */
	private static LinkedList<String> findPackages(String dirpath) {
		LinkedList<String> packageListing = new LinkedList<String>();
		LinkedList<String> dirsToProcess = new LinkedList<String>();
		
		try {
			// Add any found subdirs to a list to process
			File targetDir = new File(dirpath);
			if (targetDir.isDirectory()) {
				dirsToProcess = addAllSubDirs(targetDir, dirsToProcess);
			}
		} catch (Exception e) {
			System.err.println(e);
		}

		// Process the list of 'found' directories looking for additional directories
		// with Java files in them, indicating packaged code.
		String fileSep = System.getProperty("file.separator");
		while (dirsToProcess.size() > 0) {
			String item = dirsToProcess.removeFirst();
			item = item.substring(item.indexOf(dirpath) + dirpath.length() +1);
			File candidate = new File(dirpath + fileSep + item);
			if (containsJavaFiles(candidate)) {
				item = item.replaceAll(fileSep, ".");
				packageListing.add(item);
				System.err.println("\t" + item);
			}
		}	

		// Process the list of dirs for subdirs. Any dir with Java files in it is a package
		// (except the top level ones)
		return packageListing;
	}
	
	/**
	 * Constructs a list of directories to process (where source code may be located)
	 *
	 * @param dir a File object (a directory) which will be recursively checked for java files and
	 * other subdirectories to process
	 * @return a reference to the modified linked list of directory paths
	 **/
	private static LinkedList<String> addAllSubDirs(File dir, LinkedList<String> list) {
		String [] dirListing = dir.list();
		for (String subDirName : dirListing) {
			File subdir = new File(dir.getPath() + System.getProperty("file.separator") + subDirName);
			if (subdir.isDirectory()) {
				if (containsJavaFiles(subdir))
					list.add(subdir.getPath());
				list = addAllSubDirs(subdir, list);
			}
		}
		return list;
	}
	
	/**
	 * Checks to see if the specified directory contains any java files.
	 *
	 * @param directory the name of a directory to check for the presence of java files.
	 * @return a true value if there are java files present, false otherwise
	 */
	private static boolean containsJavaFiles(File directory) {
		boolean result = false;
		try {
			if (directory.isDirectory()) {
				String [] dirListing = directory.list(new JavaFilter());
				if (dirListing.length > 0)
					result = true;
			}
		} catch(Exception e) {
			System.err.println(e);
		}
		return result;
	}

	/**
	 * Locates all of the Java source code files (ending with .java) in the directory
	 * specified in the path. Subdirectories are assumed to be packaged code and are processed
	 * separately.
	 *
	 * @param filePath a director path where unpackaged code may exist
	 * @return an array of Strings of dir/filenames of unpackaged java source code files
	 */
	private static String[] findFiles(String dirpath) {
		String fileSeparator = System.getProperty("file.separator");
		Vector<String> fileListVector = new Vector<String>();
		File targetDir = null;
		try {
			targetDir = new File(dirpath);
			if (targetDir.isDirectory())
				for (String val : targetDir.list(new JavaFilter()))
					fileListVector.add(dirpath + fileSeparator + val);
		} catch(Exception e) {
			System.err.println(e);
			System.exit(1);
		}
		
		System.err.println("Unpackaged source files found in dir: " + dirpath + fileSeparator);
		for (String filename : fileListVector) {
			String basename = filename.substring(filename.lastIndexOf(fileSeparator) + 1);
			System.err.println("\t" + basename);
		}
		
		System.err.println();
		return (String[]) fileListVector.toArray(new String[fileListVector.size()]);
	}			

	/** 
	 * Builds the application's options string for printing.
	 *
	 * @return a string suitable for printing, used to detail the program's run time options
	 */
	private static String optionsListing() {
		String result = "\nUsage: java -jar comtor.jar -dir dirname\n\n";
		result += "Options:\n";
		
		result += "-dir dirname\t\tSpecified the pathname of the directory in which COMTOR will "; 
		result += "search for Java source code\n\t\t\tfiles (packaged and non-packaged).\n\n";
		
		result += "-help | --help\t\tThis help message\n";
		result += "\n\n";
		return result;
	}
	 
	/**
	* Check that options have the correct arguments and builds a hashmap of argument options.
	*
	* @param	options the array of command line arguments
	* @return 	true if the options are valid, false otherwise
	*/
	public static boolean checkArgs(String[] args) {
		boolean valid = true;
		
		for (int index = 0; valid && index < args.length; index++) {
			if (args[index].equals("-help") || args[index].equals("--help"))
				valid = false;

			// All following arguments should have one value that follows each argument
			if (index+1 >= args.length || args[index+1] == null)
				valid = false;

			else if (valid && args[index].equals("-dir"))
				argsMap.put("dir", args[++index]);
		}
		return valid;
	}
	
	/**
	 * Returns the list of doclets to execute
	 *
	 * @return the list of doclets to execute
	 */
	public static Properties getDocletList() {
		return docPropList;
	}
	
	/**
	 * Attempts to load the doclet list (to execute specified doclets) through a Java properties
	 * file
	 */
	private static void loadDocletList(String location) {
		try {
			File docletListFile = new File (location, "docletList.properties");
			System.err.println("Attempting to load docletList.properties file from : " + location);
			if (docletListFile.exists()) {
				docPropList.load(new FileInputStream(docletListFile));

			} else {
				System.err.println("A 'docletList.properties' file must exist, providing the fully " +
					"qualified class names of the doclets to execute as a Javadoc list of name value " +
					"pairs for the doclet[x] name.\nFor example: ");
				System.err.println("\tdoclet1 : org.comtor.analyzers.SpellCheck");
				System.err.println("\tdoclet2 : org.comtor.analyzers.ReadingLevel");
				System.exit(1);
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
			System.exit(1);
		}
	}
}