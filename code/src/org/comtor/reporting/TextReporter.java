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
import org.json.*;

public class TextReporter {
	JSONObject currentReport;

	/**
	 * Creates the textual report file for this execution of COMTOR. The method takes the
	 * Vector of JSON strings (module reports) and outputs them to the comtorReport.txt file.
	 *
	 * @param jsonReports a Vector of String object that each contain a specific module's
	 * analysis report in JSON format.
	 */
	public void generateTextReportFile(Vector<String> jsonReports) {
		// Set the path for the location of the report output file.
		String path = "comtorReport.txt";
		try {

			// Check to see the output file currently exists, if so, delete the old file to
			// replace it with a new one. We should improve this to use a date/time stamp in
			// the file name so that deletion is redundant.
			File outputFile = new File(path);
			if (outputFile.exists())
				outputFile.delete();

			// Create a new output report file, and prepare it for writing
			PrintWriter outFilePW = new PrintWriter(new BufferedWriter(new FileWriter(path)));

			// Write the header of the debug file
			outFilePW.println("COMTOR Execution Report");
			outFilePW.println((new java.util.Date()).toString());

			// Iterate through the Vector of JSON report strings and print them to the report file
 			for (String results : jsonReports) {
				outFilePW.println("==========================================");
 
 				currentReport = new JSONObject(results);
 				outFilePW.println(getInfoBlock());
 				
 				String preamble = getAmbleBlock("preamble");
 				if (preamble != null)
 					outFilePW.println(preamble);

 				outFilePW.println(removeLastNewline(getClassesBlock()));

 				String chartblock = getChartBlock();
 				if (chartblock != null)
 					outFilePW.println(chartblock);

 				String postamble = getAmbleBlock("postamble");
 				if (postamble != null)
 					outFilePW.println(postamble);

 				outFilePW.println(removeLastNewline(getExecutionBlock()));
				outFilePW.println("==========================================");
				outFilePW.println(results);
 			}

			outFilePW.println("==========================================");
			outFilePW.close();
		}
		catch (IOException e) {
			System.out.println(e);

		} catch (JSONException je) {
			System.err.println(je);
		}
	}

	/**
	 *
	 *
	 *
	 */
	private String removeLastNewline(String input) {
		if (input.charAt(input.length()-1) == System.getProperty("line.separator").charAt(0))
			return input.substring(0, input.length()-2);
		else
			return input;
	}

	/**
	 * Returns a String representation of the report's information block as plain text.
	 *
	 * @return the String representation
	 */
	public String getInfoBlock() {
		String newLine = System.getProperty("line.separator");
		String result = "";
		try {
			JSONObject info = currentReport.getJSONObject("information");

			result += "Analysis Module: " + info.getString("name").trim() + " - " + 
				info.getString("description") + newLine;

		} catch (JSONException je) {
			System.err.println(je);
		}
		return result;
	}

	/**
	 * Returns a text formatted block of information for any charts created by this module
	 * 
	 * @return A String representation of the charting report
	 */
	public String getChartBlock() {
		String newLine = System.getProperty("line.separator");
		String result = null;
		try {
			JSONArray chartsArray = currentReport.getJSONObject("results").getJSONArray("charts");
			if (chartsArray.length() != 0) {
				result = "Charts: " + newLine;
				for (int index = 0; index < chartsArray.length(); index++) {
					JSONObject chart = chartsArray.getJSONObject(index);
					JSONArray keynames = chart.names();
					String key = keynames.getString(0);
					result += "\t" + key + ": " + chart.getString(key) + newLine;
				}
			}

		} catch (JSONException je) {
			System.err.println(je);
		}
		return result;
	}

	/**
	 * Returns a text formatted block of information of the execution timing and metrics 
	 * form executing this module.
	 *
	 * @retuns A String representation of the extuction timing/metrics information
	 */
	public String getExecutionBlock() {
		String newLine = System.getProperty("line.separator");
		String result = null;
		try {
			JSONObject results = currentReport.getJSONObject("results");
			JSONObject timing = results.getJSONObject("timing");
			JSONArray metrics = results.getJSONArray("metrics");
			result = "Execution time: " + timing.getString("execution time") + "(ms)" + newLine + newLine;
			result += "Execution metrics: " + newLine;
			for (int index = 0; index < metrics.length(); index++)
				result += "\t" + metrics.get(index) + newLine;

		} catch (JSONException je) {
			System.err.println(je);
		}
		return result;
	}

	/**
	 * Returns a String representation of the report's [pre | post] -amble notes as plain text.
	 *
	 * @param blockType A String containing the note type to return (generally "preamble" or "postamble")
	 * @return the String representation
	 */
	public String getAmbleBlock(String blockType) {
		String newLine = System.getProperty("line.separator");
		String result = null;
		try {
			JSONObject notes = currentReport.getJSONObject("results").getJSONObject("notes");
			JSONArray preambleArray = notes.getJSONArray(blockType);
			if (preambleArray.length() != 0) {
				result = "Notes:" + newLine;
				for (int index = 0; index < preambleArray.length(); index++)
					result += "\t" + preambleArray.getString(index) + newLine;
			}

		} catch (JSONException je) {
			System.err.println(je);
		}
		return result;
	}

	/**
	 * Returns a String representation of the report's classes block as plain text.
	 *
	 * @return the String representation
	 */
	public String getClassesBlock() {
		String newLine = System.getProperty("line.separator");
		String result = null;
		try {
			JSONObject classes = currentReport.getJSONObject("results").getJSONObject("classes");
			String[] keys = JSONObject.getNames(classes);

			if (keys.length > 0) {
				Arrays.sort(keys);
				result = "Class Analysis: " + newLine;
				for (String name : keys) {
					result += "\t" + name + ":" + newLine;
					JSONArray classArray = classes.getJSONArray(name);
					for (int index = 0; index < classArray.length(); index++)
						result += "\t\t" + classArray.getString(index) + newLine;
					result += newLine;
				}
			}
		} catch (JSONException je) {
			System.err.println(je);
		}
		return result;
	}

	/**
	 * Accepts a Vector of properties (analysis report) and writes it to a file. Each Vector location
	 * contains the Properties list of a report for one analysis module.
	 *
	 * @param resultsVector The Vector of Properties objects which contains the report for each
	 * analysis module.
	 */
	private static void generateOutputFile(Vector<Properties> resultsVector) {
	
		// Set the path for the location of the report output file.
		String path = "comtorReport.txt";
		try {

			// Check to see the output file currently exists, if so, delete the old file to
			// replace it with a new one. We should improve this to use a date/time stamp in
			// the file name so that deletion is redundant.
			File outputFile = new File(path);
			if (outputFile.exists())
				outputFile.delete();

			// Create a new output report file, and prepare it for writing
			PrintWriter outFilePW = new PrintWriter(new BufferedWriter(new FileWriter(path)));

			// Write the header of the debug file
			outFilePW.println("COMTOR Execution Report");
			outFilePW.println((new java.util.Date()).toString());
			outFilePW.println("==========================================");
			
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
 					outFilePW.println(key + '\t' + results.getProperty(key));
				outFilePW.println("------------------------------------------------------");
			}
			outFilePW.close();
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
}