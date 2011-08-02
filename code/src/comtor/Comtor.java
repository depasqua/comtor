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
	
	/**
	 * Main method for the Comtor Standalone program.
	 *
	 * @param args Parameters for the program. The only useful parameters are ones we can directly
	 * pass to Javadoc, as everything really starts there and calls our doclet.
	 */
	public static void main (String [] args) {
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
					
				// Find packages in the sourcepath dir(s)
				String[] packageList = findPackages(argsMap.get("sourcepath"));
				for (String pkg : packageList) {
					System.out.println("Java package found: " + pkg);
				}

				// Process the limiting option, if applicable
				if (argsMap.containsKey("limitto")) {
					System.out.println("Limiting to the following package(s):");
					Scanner scanlimits = new Scanner(argsMap.get("limitto"));
					scanlimits.useDelimiter(";");
					while (scanlimits.hasNext()) {
						String limitpkg = scanlimits.next();
						for (String pkg : packageList) {
							if (pkg.equals(limitpkg)) {
								System.out.println("\t" + limitpkg);
								jdocsoptions.add(limitpkg);
							}
						}
					}
				} else
					for (String pkg : packageList) {
						jdocsoptions.add(pkg);
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
			for (String option : jdocsoptions) {
				optionslist[index] = jdocsoptions.elementAt(index);
				index++;
			}
	
			// Execute the javadoc processor handing it a String name of the application (for error
			// output), the name of the doclet to execute (our debug version of the master doclet),
			// and an array of arguments
			com.sun.tools.javadoc.Main.execute("COMTOR", "comtor.ComtorStandAlone", optionslist);
		}
	}
	
	/**
	 * Locates all java packages in the provided path(s).
	 *
	 * @param dirPath a semi-colon separated list of directories where packaged code may exist
	 * @return an array of String of java packages
	 */
	public static String[] findPackages(String filePath) {
		Vector<String> packageListing = new Vector<String>();
		LinkedList<String> dirsToProcess = new LinkedList<String>();
		
		Scanner scanpath = new Scanner(filePath);
		scanpath.useDelimiter(";");
		while (scanpath.hasNext()) {
			String dir = scanpath.next();
			try {
				// Add any found subdirs to a list to process
				File targetDir = new File(dir);
				if (targetDir.isDirectory()) {
					// Check this directory's contents for subdirectories
					String[] subDirList = targetDir.list();
					for (String subDir : subDirList) {
						File subDirPath = new File(dir + System.getProperty("file.separator") + subDir);
						if (subDirPath.isDirectory()) {
							if (containsJavaFiles(subDirPath))
								packageListing.add(subDir);

							// Obtain a list of sub-sub dirs
							String[] subSubDirList = subDirPath.list();
							for (String itemName : subSubDirList) {
								File candidate = new File(subDirPath + 
										System.getProperty("file.separator") + itemName);
								if (candidate.isDirectory())
									dirsToProcess.add(subDirPath +
											System.getProperty("file.separator") + itemName);
							}
						}
					}
					// Show dirs left to process
					while (dirsToProcess.size() > 0) {
						String item = dirsToProcess.removeFirst();
						File candidate = new File(item);
						if (containsJavaFiles(candidate)) {
							item = item.replaceAll(System.getProperty("file.separator"), ".");
							packageListing.add(item.substring(item.indexOf(".")+1));
						}
						
						// Check its contents
						String [] listing = candidate.list();
						for (String newitem : listing) {
							File newFile = new File(item + System.getProperty("file.separator") + newitem);
							if (newFile.isDirectory())
								dirsToProcess.add(item + System.getProperty("file.separator") + newitem);
						}
					}	
				}
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		
		// Process the list of dirs for subdirs. Any dir with java files in it is a package
		// (except the top level ones)
		return (String[]) packageListing.toArray(new String[packageListing.size()]);
	}
	
	/**
	 * Checks to see if the specified directory contains any java files.
	 *
	 * @param directory the name of a directory to check for the presence of java files.
	 * @return a true value if there are java files present, false otherwise
	 */
	public static boolean containsJavaFiles(File directory) {
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
	public static String[] findFiles(String filePath) {
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
	public static String optionsListing() {
		String result = "\nUsage: Comtor -classpath <path> [options]\n\n";
		result += "Required arguments:\n";
		result += "-classpath <path>\tThe path(s) where javadoc will find user class files\n\n";

		result += "Options:\n";
		result += "-sourcepath <path>\tThe search path(s) for finding default-packaged " +
			"(non-packaged) source files (.java) when\n";
		result += "\t\t\tpassing package names into javadoc.\n\n";

		result += "-limitto <path>\t\tLimit Comtor to process only those source code files found " +
			"in the specified packages.\n\n";

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
		}
		return valid;
	}
}