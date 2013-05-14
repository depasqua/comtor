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
package org.comtor.reporting;

import java.io.*;
import java.text.*;
import java.util.*;
import org.comtor.drivers.*;
import org.json.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TextReporter extends COMTORReporter {
	private static Logger logger = LogManager.getLogger(TextReporter.class.getName());

	/**
	 * Creates the textual report file for this execution of COMTOR. The method takes the
	 * Vector of JSON strings (module reports) and outputs them to the comtorReport.txt file.
	 *
	 * @param jsonReports a Vector of String object that each contain a specific module's
	 * analysis report in JSON format.
	 */
	public void generateReportFile(Vector<String> jsonReports) {
		logger.entry();

		// Set the path for the location of the report output file.
		String filename = "comtorReport.txt";
		try {
			logger.trace("Attempting to create output report: " + filename);
			removeOldReport(filename);

			// Create a new output report file, and prepare it for writing
			PrintWriter outFilePW = new PrintWriter(new BufferedWriter(new FileWriter(
				new File(Comtor.getCodeDir(), filename))));

			// For debugging, create a JSON Output file
			PrintWriter jsonFile = new PrintWriter(new BufferedWriter(new FileWriter(
				new File(Comtor.getCodeDir(), "jsonOut.txt"))));

			// Write the header of the debug file
			SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			formatter.setTimeZone(Calendar.getInstance().getTimeZone());

			outFilePW.println("COMTOR Execution Report - " + formatter.format(new java.util.Date()));
			outFilePW.println("===========================================");
			outFilePW.println("| Execution Score Summary                 |");
			outFilePW.println("===========================================");
			outFilePW.println("| Module Name                  | Score    |");
			outFilePW.println("-------------------------------------------");

			Iterator iter = jsonReports.iterator();
 			while (iter.hasNext()) {
 				String results = (String) iter.next();
				currentReport = new JSONObject(results);
 				String moduleName = currentReport.getJSONObject("information").getString("name");
 				String moduleScore = currentReport.getJSONObject("results").getString("score");

 				outFilePW.format("| %1$-29s| %2$-9s|\n", moduleName, moduleScore);
 			}
			outFilePW.println("===========================================\n");

			// Iterate through the Vector of JSON report strings and print them to the report file
			iter = jsonReports.iterator();
 			while (iter.hasNext()) {
 				String results = (String) iter.next();
				outFilePW.print("==========================================");
				outFilePW.println("=========================================="); 					
 
				currentReport = new JSONObject(results);
				logger.trace("Creating report for module: " + getReportName());
 				outFilePW.println(getInfoBlock());
 				String preamble = getAmbleBlock("preamble");
 				if (preamble != null)
 					outFilePW.println(preamble);

 				outFilePW.println(removeLastNewline(getClassesBlock()));

 				String typeHistogram = getTypeHistogram();
 				if (typeHistogram != null)
 					outFilePW.println(typeHistogram);

 				String chartblock = getChartBlock();
 				if (chartblock != null)
 					outFilePW.println(chartblock);

 				String postamble = getAmbleBlock("postamble");
 				if (postamble != null)
 					outFilePW.println(postamble);

 				String scoreStr = getScoreBlock();
 				if (scoreStr != null)
 					outFilePW.println(scoreStr);

 				outFilePW.println(removeLastNewline(getExecutionBlock()));

 				if (!iter.hasNext()) {
					outFilePW.print("==========================================");
					outFilePW.println("=========================================="); 					
 				}

				jsonFile.println(results);
 			}

			outFilePW.close();
			jsonFile.close();
		}
		catch (IOException e) {
			System.out.println(e);
			logger.trace(e);

		} catch (JSONException je) {
			System.err.println(je);
			logger.trace(je);
		}

		logger.exit();
	}
}