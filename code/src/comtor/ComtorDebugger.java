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
import java.util.*;
import java.text.*;
import java.sql.*;

/**
 * The ComtorDebugger class generates the debugging dump of the properties list associated
 * with an execution of the analyzer(s).
 */
public class ComtorDebugger {

	public static void generateDebugFile(Vector<Properties> resultsVector) {
	
		// Set the path for the location of the debug dump file.
		String path = System.getProperty("user.dir").concat("/debug_dump.txt");
		try {

			// Check to see the debug file currently exists, if so, delete the old file to
			// replace it with a new one. We should improve this to use a date/time stamp in
			// the file name so that deletion is redundant.
			File debugDumpFile = new File(path);
			if (debugDumpFile.exists())
				debugDumpFile.delete();

			// Create a new debug dump file, and prepare it for writing
			PrintWriter dumpFile = new PrintWriter(new BufferedWriter(new FileWriter(path)));

			// Write the header of the debug file
			dumpFile.println("COMTOR Execution Report");
			dumpFile.println((new java.util.Date()).toString());
			dumpFile.println("==========================================");
			
			// For each vector element, each of which is a properties list from the reports created
 			for (Properties results : resultsVector) {
				// Fetch the keys, add them to an array, sort and then print results
				Enumeration keyList = results.keys();
				String keyArray[] = new String[results.size()];
				int index = 0;
				for (Enumeration e = keyList; e.hasMoreElements(); ) {
					String key = (String) e.nextElement();
					keyArray[index++] = key;
				}
				Arrays.sort(keyArray);
				
				for (String key : keyArray)
					dumpFile.println(key + '\t' + results.getProperty(key));

				dumpFile.println("------------------------------------------------------");
			}
			dumpFile.close();
		}
		catch (IOException e) {
			System.out.println(e);
			System.out.println("Path: " + path);
		}
	}
}