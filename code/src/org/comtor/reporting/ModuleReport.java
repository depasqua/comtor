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

import org.json.*;

/** 
 * This class represents a COMTOR module's report. Essentially it wraps the structure of the report
 * and aids in buildling the JSON structure that will contain the report.
 *
 * @author Peter DePasquale
 */
public class ModuleReport {
	private JSONObject report = new JSONObject();
	private JSONArray currentClass = null;
	private JSONArray currentPackage = null;

	/**
	 * This constructor creates the basic framework of a JSON-based report for COMTOR. A number of the
	 * internal structural aspects of the report are created here, so this constructor should always be 
	 * used when creating a report object.
	 *
	 * @pararm moduleName a String object that contains the name of the module creating this report
	 * @param moduleDescription a String object that provides a description of the module's analysis
	 */
	public ModuleReport (String moduleName, String moduleDescription) {
		// Create the information nested object and add it to the report object
		try {
			JSONObject info = new JSONObject();
			info.put("name", moduleName);
			info.put("description", moduleDescription);

			JSONObject notes = new JSONObject();
			notes.put("preamble", new JSONArray());
			notes.put("postamble", new JSONArray());

			JSONObject results = new JSONObject();
			results.put("notes", notes);
			results.put("classes", new JSONObject());
			results.put("packages", new JSONObject());
			results.put("metrics", new JSONArray());
			results.put("timing", new JSONObject());
			results.put("charts", new JSONArray());

			report.put("information", info);			
			report.put("results", results);

		} catch (JSONException je) {
			System.err.println(je);
		}
	}

	/** 
	 * Adds the specified chart to the chart array.
	 *
	 * @param title A String title for this chart
	 * @param url A String url for the chart (via Google Chart API)
	 */
	public void addChart(String title, String url) {
		if (report != null) {
			try {
				JSONArray obj = report.getJSONObject("results").getJSONArray("charts");
				JSONObject newchart = new JSONObject();
				newchart.put(title, url);
				obj.put(newchart);

			} catch (JSONException je) {
				System.err.println(je);
			}
		}
	}

	/**
	 * Adds the specific key/value pair to the timing information block.
	 *
	 * @param key the key value used to store the corresponding value
	 * @param value the value to be stored (associated with the key)
	 */
	public void addTimingString(String key, String value) {
		if (report != null) {
			try {
				JSONObject obj = report.getJSONObject("results").getJSONObject("timing");
				obj.put(key, value);

			} catch (JSONException je) {
				System.err.println(je);
			}
		}
	}

	/** 
	 * Adds the specific metric string to the report's metric information block.
	 * 
	 * @parram msg the message String to be added to the metric information block of the report
	 */
	public void addMetric (String msg) {
		if (report != null) {
			try {
				JSONArray array = report.getJSONObject("results").getJSONArray("metrics");
				array.put(msg);

			} catch (JSONException je) {
				System.err.println(je);
			}
		}
	}

	/**
	 * Appends the specified message string to the end of the [pre|post]-amble array
	 * object contained in the report's information object.
	 *
	 * @param type the type of note to add (preamble or postamble)
	 * @param msg the message to append
	 */
	public void appendToAmble (String type, String msg) {
		if (report != null) {
			try {
				JSONArray array = report.getJSONObject("results").getJSONObject("notes").getJSONArray(type);
				array.put(msg);

			} catch (JSONException je) {
				System.err.println(je);
			}
		}
	}

	/**
	 * Appends the specified key/String pair to the specified top-level object
	 * within the report.
	 *
	 * @param objectName the keyname of the top-level object (e.g. "information")
	 * @param key the keyname of the corresponding long being inserted
	 * @param value the string value being inserted
	 */
	public void appendStringToObject (String objectName, String key, String value) {
		if (report != null) {
			try {
				JSONObject obj = report.getJSONObject(objectName);
				obj.put(key, value);

			} catch (JSONException je) {
				System.err.println(je);
			}
		}
	}

	/**
	 * Appends the specified key/long pair to the specified top-level object
	 * within the report.
	 *
	 * @param objectName the keyname of the top-level object (e.g. "information")
	 * @param key the keyname of the corresponding long being inserted
	 * @param value the long value being inserted
	 */
	public void appendLongToObject (String objectName, String key, long value) {
		if (report != null) {
			try {
				JSONObject obj = report.getJSONObject(objectName);
				obj.put(key, value);

			} catch (JSONException je) {
				System.err.println(je);
			}
		}
	}

	/**
	 * Adds a new class array to the current report in the "classes" section. Once added,
	 * a reference is set (currentClass) to the current class we're reporting on. This is
	 * used by the module to pass in "report strings" (messages) regarding analysis of the
	 * current class.
	 *
	 * @param classname the String name of the new class
	 */
	public void addClass(String classname) {
		if (report != null) {
			try {
				JSONObject obj = report.getJSONObject("results").getJSONObject("classes");
				JSONArray subarray = new JSONArray();
				obj.put(classname, subarray);
				currentClass = subarray;

			} catch (JSONException je) {
				System.err.println(je);
			}
		}
	}

	/**
	 * Adds a new package array to the current report in the "package" section. Once added,
	 * a reference is set (currentPackage) to the current package we're reporting on. This is
	 * used by the module to pass in "report strings" (messages) regarding analysis of the
	 * current package.
	 *
	 * @param packagename the String name of the new package
	 */
	public void addPackage(String packagename) {
		if (report != null) {
			try {
				JSONObject obj = report.getJSONObject("results").getJSONObject("packages");
				JSONArray subarray = new JSONArray();
				obj.put(packagename, subarray);
				currentPackage = subarray;

			} catch (JSONException je) {
				System.err.println(je);
			}
		}
	}

	/**
	 * Appends the String message to the current class's report object
	 *
	 * @param msg the message to append to the current class object in the report.
	 */
	public void appendClassMessage(String msg) {
		if (report != null) {
			currentClass.put(msg);
		}
	}

	/**
	 * Appends the String message to the current packages's report object
	 *
	 * @param msg the message to append to the current package object in the report.
	 */
	public void appendPacakageMessage(String msg) {
		if (report != null) {
			currentPackage.put(msg);
		}
	}

	/**
	 * Returns a string representation of this report object.
	 *
	 * @return a string object that represents the JSON object containing the report.
	 */
	public String toString() {
		String result = new String();

		try {
			result = report.toString(3);
		} catch (JSONException je) {
			System.err.println(je);
		}
		return result;
	}
}