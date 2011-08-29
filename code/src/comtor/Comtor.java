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

import java.io.*;
import java.util.*;

/**
 * This class contains a main method (wrapper) used to call the debug version of COMTOR. The 
 * debug version is used to check the processing and output of modules as they are developed, as
 * well as used for the 'standalone' version of the application to be deployed for research and/or
 * to the cloud.
 *
 */
public class Comtor {
	private static HashMap<String,String> argsMap = new HashMap<String, String>();
	private static Properties docPropList = new Properties();
	
	/**
	 * Main method for the Comtor standalone program.
	 *
	 * @param args Parameters for the program. The only useful parameters are ones we can directly
	 * pass to Javadoc, as everything really starts there and calls our doclet.
	 */
	public static void main (String [] args) {
	
		// Loads the list of doclets to execute from the config file in the from which
		// execution is originating
		loadDocletList();
		
		Vector<String> jdocsoptions = new Vector<String>();

		// Validate the argument list and print the options listing if validation fails.
		if (!checkArgs(args)) {
			System.out.println(optionsListing());
			System.exit(1);
		} else {
			// Process the arguments
			if (argsMap.containsKey("sourcepath")) {
				jdocsoptions.add("-sourcepath");
				jdocsoptions.add(argsMap.get("sourcepath"));

				// Find non-packaged code in sourcepath dir(s) and add it to the options vector
				String[] fileList = findFiles(argsMap.get("sourcepath"));
				for (String file : fileList)
					jdocsoptions.add(file);
					
				// Find packages in the sourcepath dir(s). Remove any upper level directories
				// encountered, until we reach the trimto dir, if applicable
				System.out.println("Found the following packages:");
				LinkedList<String> packageList = findPackages(argsMap.get("sourcepath"), 
					argsMap.get("trimto"));

				// Process the limiting option, if applicable
				if (argsMap.containsKey("limitto")) {
					Scanner scanlimits = new Scanner(argsMap.get("limitto"));
					scanlimits.useDelimiter(";");
					
					// Place the list of limiting packages into its own linked list for processing
					System.out.println("\nLimiting to the following package(s):");
					LinkedList<String> limits = new LinkedList<String>();
					while (scanlimits.hasNext()) {
						String limitPkg = scanlimits.next();
						limits.add(limitPkg);
						System.out.println("\t" + limitPkg);
					}
					
					// Remove any known packages that are not in the limitto list
					for (String limit : limits) {
						for (String pkg : packageList) {
							if (pkg.equals(limit)) {
								jdocsoptions.add(pkg);
							}
						}
					}				
					
				} else {					
					// Add the list of packages to the options list being sent to Javadoc
					for (String pkg : packageList) {
						jdocsoptions.add(pkg);
					}
				}
			}
			if (argsMap.containsKey("classpath")) {
				jdocsoptions.add("-classpath");
				jdocsoptions.add(argsMap.get("classpath"));
			}
			
			// Add the -private option to the javadoc parameter list, ensuring that ALL classes
			// and members are processed.
			jdocsoptions.add("-private");
			
			String[] optionslist = new String[jdocsoptions.size()];
			int index = 0;
			System.out.println("\njavadoc options: ");
			for (String option : jdocsoptions) {
				optionslist[index] = jdocsoptions.elementAt(index);
				System.out.println("\t"+optionslist[index]);
				index++;
			}
			System.err.println();
	
			// Execute the javadoc processor handing it a String name of the application (for error
			// output), the name of the doclet to execute (our debug version of the master doclet),
			// and an array of arguments
			com.sun.tools.javadoc.Main.execute("COMTOR", "comtor.ComtorStandAlone", optionslist);
		}
	}
	
	/**
	 * Locates all Java packages in the provided path(s).
	 *
	 * @param dirPath a semi-colon separated list of directories where packaged code may exist
	 * @param trimDr a dir name which will be used to trim the path of any candidate package, up
	 * to the first occurrence of the trimDir value
	 * @return a linked list of String names of Java packages
	 */
	private static LinkedList<String> findPackages(String filePath, String trimDir) {
		LinkedList<String> packageListing = new LinkedList<String>();
		LinkedList<String> dirsToProcess = new LinkedList<String>();
		
		Scanner scanpath = new Scanner(filePath);
		scanpath.useDelimiter(";");
		while (scanpath.hasNext()) {
			String dir = scanpath.next();
			try {
				// Add any found subdirs to a list to process
				File targetDir = new File(dir);
				if (targetDir.isDirectory()) {
					dirsToProcess.add(targetDir.getPath());
					dirsToProcess = addAllSubDirs(targetDir, dirsToProcess);
				}
			} catch (Exception e) {
				System.err.println(e);
			}
		}

		// Process the list of 'found' directories looking for additional directories
		// with Java files in them, indicating packaged code.
		String fileSep = System.getProperty("file.separator");
		while (dirsToProcess.size() > 0) {
			String item = dirsToProcess.removeFirst();
			File candidate = new File(item);
			if (containsJavaFiles(candidate)) {
				// We've found suspected package locations..., trim as needed
				int location = -1;
				if (trimDir != null) {
					location = item.indexOf(fileSep + trimDir + fileSep);
					// If we are using trimto, then only add dirs that have been trimmed
					if (location != -1) {
						item = item.substring(location+1);
						item = item.replaceAll(fileSep, ".");
						packageListing.add(item);
						System.out.println("\t"+item);
					}

				}
				else {
					item = item.replaceAll(fileSep, ".");
					packageListing.add(item);
					System.out.println("\t"+item);
				}
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
	 * Locates all of the Java source code files (ending with .java) in the directories
	 * specified in the path. Path directories are specified with a semi-colon delimited list
	 *
	 * @param filePath a semi-colon separated list of directories where unpackaged code may exist
	 * @return an array of Strings of dir/filenames of unpackaged java source code files
	 */
	private static String[] findFiles(String filePath) {
		Vector<String> fileListVector = new Vector<String>();
		Scanner scanpath = new Scanner(filePath);
		scanpath.useDelimiter(";");
		while (scanpath.hasNext()) {
			String dir = scanpath.next();
			try {
				File targetDir = new File (dir);
				if (targetDir.isDirectory())
					for (String val : targetDir.list(new JavaFilter()))
						fileListVector.add(dir + System.getProperty("file.separator") + val);
			} catch(Exception e) {
				System.err.println(e);
			}
		}
		return (String[]) fileListVector.toArray(new String[fileListVector.size()]);
	}			

	/** 
	 * Builds the application's options string for printing.
	 *
	 * @return a string suitable for printing, used to detail the program's run time options
	 */
	private static String optionsListing() {
		String result = "\nUsage: Comtor -classpath <path> [options]\n\n";
		result += "Required arguments:\n";
		result += "-classpath <path>\tThe path(s) where javadoc will find user class files\n\n";

		result += "Options:\n";
		result += "-sourcepath <path>\tThe search path(s) for finding default-packaged " +
			"(non-packaged) source files (.java) when\n";
		result += "\t\t\tpassing package names into javadoc.\n\n";

		result += "-limitto <path>\t\tLimit Comtor to process only those source code files found " +
			"in the specified packages.\n\n";
			
		result += "-trimto <dir>\t\t\n\n";

		result += "-help\t\t\tThis help message\n";
		result += "\nNote that where applicable, <path> values are semi-colon separated directory lists\n";
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
			if (args[index].equals("-help"))
				valid = false;

			// All following arguments should have one value that follows
			if (index+1 >= args.length || args[index+1] == null)
				valid = false;

			if (valid && args[index].equals("-sourcepath"))
				argsMap.put("sourcepath", args[++index]);
			
			else if (valid && args[index].equals("-classpath"))
				argsMap.put("classpath", args[++index]);

			else if (valid && args[index].equals("-limitto"))
				argsMap.put("limitto", args[++index]);
				
			else if (valid && args[index].equals("-trimto"))
				argsMap.put("trimto", args[++index]);
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
	private static void loadDocletList() {
		try {
			File docletListFile = new File (System.getProperty("user.dir").concat(
					"/docletList.properties"));
			if (docletListFile.exists()) {
				docPropList.load(new FileInputStream(docletListFile));
					
			} else {
				System.err.println("A 'docletList.properties' file must exist, providing the fully " +
					"qualified class names of the doclets to execute.\nFor example, " +
					"comtor.analyzers.SpellCheck");
				System.err.println("This file must be in Java properties format. For example: ");
				System.err.println("\tdoclet1 : comtor.analyzers.SpellCheck");
				System.err.println("\tdoclet2 : comtor.analyzers.ReadingLevel");
				System.exit(1);
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
			System.exit(1);
		}
	}
}