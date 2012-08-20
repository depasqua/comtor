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
			File outputFile = new File(Comtor.getCodeDir(), path);
			if (outputFile.exists())
				outputFile.delete();

			// Create a new output report file, and prepare it for writing
			PrintWriter outFilePW = new PrintWriter(new BufferedWriter(new FileWriter(
				new File(Comtor.getCodeDir(), path))));

			// For debugging, create a JSON Output file
			PrintWriter jsonFile = new PrintWriter(new BufferedWriter(new FileWriter(
				new File(Comtor.getCodeDir(), "jsonOut.txt"))));

			// Write the header of the debug file
			outFilePW.println("COMTOR Execution Report - " + (new java.util.Date()).toString());

			// Iterate through the Vector of JSON report strings and print them to the report file
			Iterator iter = jsonReports.iterator();
 			while (iter.hasNext()) {
 				String results = (String) iter.next();
				outFilePW.print("==========================================");
				outFilePW.println("==========================================");
 
 				currentReport = new JSONObject(results);
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

		} catch (JSONException je) {
			System.err.println(je);
		}
	}

	/**
	 * Removes the extra last line, if present, from an output string. Used to pretty up the
	 * JSON-based report output.
	 *
	 * @param input A string of output from the JSON report. The last newline character will be
	 * removed from this input line.
	 * @return A new string output line with the last newline character removed.
	 */
	private String removeLastNewline(String input) {
		String result = null;

		if (input == null)
			result = "";
		else if (input.charAt(input.length()-1) == System.getProperty("line.separator").charAt(0))
			result = input.substring(0, input.length()-1);
			else
				result = input;

		return result;
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
	 * Returns a string representation of the type histogram (if present) in the report, or a null otherwise.
	 *
	 * @return a string histrgram report or a null value
	 */
	private String getTypeHistogram() {
		String newLine = System.getProperty("line.separator");
		String result = "Field Type Histogram: " + newLine;
		try {
			JSONObject resultsObj = currentReport.getJSONObject("results");
			if (resultsObj.has("typeHistogram")) {
				JSONObject histoObj = resultsObj.getJSONObject("typeHistogram");
				Iterator iter = histoObj.keys();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					result += "\t" + key + ": " + histoObj.getInt(key) + newLine;
				}
			} else
				return null;

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
	private String getChartBlock() {
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
	private String getExecutionBlock() {
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
	private String getAmbleBlock(String blockType) {
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
	private String getClassesBlock() {
		String newLine = System.getProperty("line.separator");
		String result = null;
		boolean classOutputCreated = false;

		try {
			JSONObject classes = currentReport.getJSONObject("results").getJSONObject("classes");
			String[] classnameKeys = null;

			if (classes != null)
				classnameKeys = JSONObject.getNames(classes);

			if (classnameKeys != null && classnameKeys.length > 0) {
				Arrays.sort(classnameKeys);
				result = "Analysis: " + newLine;

				// For each class...
				for (String name : classnameKeys) {
					classOutputCreated = false;
					JSONObject classObj = classes.getJSONObject(name);
					result += "\t" + name + ":" + newLine;

					// Output the issues found in the class comment section
					JSONArray issuesArray = classObj.getJSONArray("issues");
					if (issuesArray.length() > 0) {
						result += "\t\tClass analysis:" + newLine;
						for (int index = 0; index < issuesArray.length(); index++)
							result += "\t\t\t" + issuesArray.getString(index) + newLine;
						result += newLine;
						classOutputCreated = true;
					}

					// Output the issues found in the constructors' comment section
					JSONObject constructors = classObj.getJSONObject("constructors");
					String[] constructorNameKeys = JSONObject.getNames(constructors);
					boolean constructorHeaderPrinted = false;
					if (constructorNameKeys != null && constructorNameKeys.length > 0) {
						// For each constructor...
						for (String constructorName : constructorNameKeys) {
							JSONObject constructorObj = constructors.getJSONObject(constructorName);
						 	issuesArray = constructorObj.getJSONArray("issues");
						 	if (issuesArray.length() > 0) {
						 		if (!constructorHeaderPrinted) {
									result += "\t\tConstructor analysis:" + newLine;
									constructorHeaderPrinted = true;
								}

								result += "\t\t\t" + constructorName + ": " + newLine;
								for (int index = 0; index < issuesArray.length(); index++)
									result += "\t\t\t\t" + issuesArray.getString(index) + newLine;

								result += newLine;
								classOutputCreated = true;
							}

							// Deal with parameter issues...
							JSONObject paramObj = constructorObj.getJSONObject("parameters");
							String[] paramNames = JSONObject.getNames(paramObj);
							if (paramNames != null && paramNames.length > 0) {
								// For each parameter...
								for (String paramName : paramNames) {
									JSONArray paramIssuesArray = paramObj.getJSONArray(paramName);
									if (paramIssuesArray.length() > 0) {
										result += "\t\t\t\t" + paramName + " (parameter): " + newLine;
										for (int index = 0; index < paramIssuesArray.length(); index++)
											result += "\t\t\t\t\t" + paramIssuesArray.getString(index) + newLine;
										result += newLine;
										classOutputCreated = true;
									}
								}
							}

							// Deal with throws issues...
							JSONObject throwsObj = constructorObj.getJSONObject("throws");
							String[] throwsNames = JSONObject.getNames(throwsObj);
							if (throwsNames != null && throwsNames.length > 0) {
								// For each throws...
								for (String throwsName : throwsNames) {
									JSONArray throwsIssuesArray = throwsObj.getJSONArray(throwsName);
									if (throwsIssuesArray.length() > 0) {
										result += "\t\t\t\t" + throwsName + " (throws): " + newLine;
										for (int index = 0; index < throwsIssuesArray.length(); index++)
											result += "\t\t\t\t\t" + throwsIssuesArray.getString(index) + newLine;
										result += newLine;
										classOutputCreated = true;
									}
								}
							}
						}
					}

					// Output the issues found in the methods' comment section
					JSONObject methodsObj = classObj.getJSONObject("methods");
					String[] methodnameKeys = JSONObject.getNames(methodsObj);

			 		if (containsIssues(methodsObj)) {
						result += "\t\tMethod analysis:" + newLine;
						classOutputCreated = true;
					}

					if (methodnameKeys != null && methodnameKeys.length > 0) {
						// For each method...
						for (String methodname : methodnameKeys) {
							JSONObject method = methodsObj.getJSONObject(methodname);
							// Deal with method issues...
						 	JSONArray methodIssuesArray = method.getJSONArray("issues");
						 	if (methodIssuesArray.length() > 0) {
								result += "\t\t\t" + methodname + ": " + newLine;
								for (int index = 0; index < methodIssuesArray.length(); index++)
									result += "\t\t\t\t" + methodIssuesArray.getString(index) + newLine;

								result += newLine;
							}

							// Deal with parameter issues...
							JSONObject paramObj = method.getJSONObject("parameters");
							String[] paramNames = JSONObject.getNames(paramObj);
							if (paramNames != null && paramNames.length > 0) {
								// For each parameter...
								for (String paramName : paramNames) {
									JSONArray paramIssuesArray = paramObj.getJSONArray(paramName);
									if (paramIssuesArray.length() > 0) {
										result += "\t\t\t\t" + paramName + " (parameter): " + newLine;
										for (int index = 0; index < paramIssuesArray.length(); index++)
											result += "\t\t\t\t\t" + paramIssuesArray.getString(index) + newLine;
										result += newLine;
									}
								}
							}

							// Deal with throws issues...
							JSONObject throwsObj = method.getJSONObject("throws");
							String[] throwsNames = JSONObject.getNames(throwsObj);
							if (throwsNames != null && throwsNames.length > 0) {
								// For each throws...
								for (String throwsName : throwsNames) {
									JSONArray throwsIssuesArray = throwsObj.getJSONArray(throwsName);
									if (throwsIssuesArray.length() > 0) {
										result += "\t\t\t\t" + throwsName + " (throws): " + newLine;
										for (int index = 0; index < throwsIssuesArray.length(); index++)
											result += "\t\t\t\t\t" + throwsIssuesArray.getString(index) + newLine;
										result += newLine;
									}
								}
							}
						}
					}

					// Output the issues found in the class fields' comment section(s)
					JSONObject issues = classObj.getJSONObject("fields");
					String[] fieldnameKeys = JSONObject.getNames(issues);
					boolean fieldHeaderPrinted = false;
					if (fieldnameKeys != null && fieldnameKeys.length > 0) {
						// For each field
						for (String fieldname : fieldnameKeys) {
							issuesArray = classObj.getJSONObject("fields").getJSONObject(fieldname).getJSONArray("issues");
							if (issuesArray.length() > 0) {
								if (!fieldHeaderPrinted)
									result += "\t\tField analysis: " + newLine;

								result += "\t\t\t" + fieldname + ": " + newLine;
								for (int index = 0; index < issuesArray.length(); index++)
									result += "\t\t\t\t" + issuesArray.getString(index) + newLine;

								result += newLine;
								classOutputCreated = true;
							}
						}
					}

					// If no output was created, print appropriate string message.
					if (classOutputCreated == false)
						result += "\t\tNo issues identified." + newLine + newLine;
				}
			}

		} catch (JSONException je) {
			System.err.println(je);
		}
		return result;
	}

	/**
	 * Checks to determine if the specified methods block of JSON data contains any "issues" to
	 * output. Issues here can be from the analysis of the method's comments, the method's parameters'
	 * comments, and the method's throws' comments. Returns a true valie if there are issues to
	 * print. This method is used to pretty up the text-based output so that we only print
	 * method names/header lines if/when a method contains output that needs to be rendered.
	 *
	 * @param methods A JSONObject that contains the embodyment of a group of methods.
	 * @return a true value if any of the methods contained in the block (or its subordinate data
	 * contains analysis "issues" such that the methods need to be processed for output rendering.
	 */
	private boolean containsIssues(JSONObject methods) {
		boolean result = false;

		String[] methodNames = JSONObject.getNames(methods);
		if (methodNames != null && methodNames.length > 0) {
			// For each method...
			for (String methodname : methodNames) {
				try {
					JSONObject method = methods.getJSONObject(methodname);
					JSONArray issuesArray = method.getJSONArray("issues");
					if (issuesArray.length() > 0)
						result = true;
					else {
						JSONObject paramObj = method.getJSONObject("parameters");
						String[] paramNames = JSONObject.getNames(paramObj);
						if (paramNames != null && paramNames.length > 0) {
							// For each param...
							for (String paramname : paramNames) {
								JSONArray paramIssuesArray = paramObj.getJSONArray(paramname);
								if (paramIssuesArray.length() > 0)
									result = true;
								else {
									JSONObject throwsObj = method.getJSONObject("throws");
									String[] throwsNames = JSONObject.getNames(throwsObj);
									if (throwsNames != null && throwsNames.length > 0) {
										// For each throws...
										for (String throwsname : throwsNames) {
											JSONArray throwsIssuesArray = throwsObj.getJSONArray(throwsname);
											if (throwsIssuesArray.length() > 0)
												result = true;
										}
									}
								}
							}
						}
					}
				} catch (JSONException je) {
					System.err.println("here 5: " + je);
				}
			}
		}

		return result;
	}
}