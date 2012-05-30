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
 * and aids in building the JSON structure that will contain the report.
 *
 * @author Peter DePasquale
 */
public class ModuleReport {
	private JSONObject report = new JSONObject();
	private JSONArray currentPackage = null;
	private JSONObject currentClass = null;
	private JSONObject currentMethod = null;
	private JSONObject currentField = null;
	private JSONArray currentThrows = null;
	private JSONArray currentParameter = null;
	private ReportItem lastItem = null;

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

			JSONObject classes = new JSONObject();

			JSONObject results = new JSONObject();
			results.put("notes", notes);
			results.put("classes", classes);
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
	 * Adds a new report item array to the current report. Once added, a reference is set
	 * to the current item that we are reporting on. This is used by the module to pass in
	 * "report strings" (messages) regarding analysis of the current item.
	 *
	 * @param itemType a enum type desiganting where the item will be added
	 * @param classname the String name of the new class
	 */
	public void addItem(ReportItem itemType, String name) {
		if (report != null) {
			try {
				JSONObject obj = null;
				JSONArray subarray = new JSONArray();
				JSONObject subobject = new JSONObject();

				switch (itemType) {
					case PACKAGE:
						obj = report.getJSONObject("results").getJSONObject("packages");
						obj.put(name, subarray);
						currentPackage = subarray;
						lastItem = ReportItem.PACKAGE;
						break;

					case CLASS:
						obj = report.getJSONObject("results").getJSONObject("classes");
						obj.put(name, subobject);
						subobject.put("issues", new JSONArray());
						subobject.put("constructors", new JSONObject());
						subobject.put("methods", new JSONObject());
						subobject.put("fields", new JSONObject());
						currentClass = subobject;
						lastItem = ReportItem.CLASS;
						break;

					case CONSTRUCTOR:
						obj = currentClass.getJSONObject("constructors");
						obj.put(name, subobject);
						subobject.put("issues", new JSONArray());
						subobject.put("parameters", new JSONObject());
						subobject.put("throws", new JSONObject());
						currentMethod = subobject;
						lastItem = ReportItem.CONSTRUCTOR;
						break;

					case METHOD:
						obj = currentClass.getJSONObject("methods");
						obj.put(name, subobject);
						subobject.put("issues", new JSONArray());
						subobject.put("parameters", new JSONObject());
						subobject.put("throws", new JSONObject());
						subobject.put("returns", new JSONObject());
						currentMethod = subobject;
						lastItem = ReportItem.METHOD;
						break;

					case PARAMETER:
						obj = currentMethod.getJSONObject("parameters");
						obj.put(name, subarray);
						currentParameter = subarray;
						lastItem = ReportItem.PARAMETER;
						break;

					case FIELD:
						obj = currentClass.getJSONObject("fields");
						obj.put(name, subobject);
						subobject.put("issues", new JSONArray());
						currentField = subobject;
						lastItem = ReportItem.FIELD;
						break;

					case THROWS:
						obj = currentMethod.getJSONObject("throws");
						obj.put(name, subarray);
						currentThrows = subarray;
						lastItem = ReportItem.THROWS;
						break;
				}

			} catch (JSONException je) {
				System.err.println(je);
			}
		}
	}

	/**
	 * Appends the String message to the specified report item's object
	 *
	 * @param itemType a enum type desiganting where the item will be appended
	 * @param msg the message to append in the report.
	 */
	public void appendMessage(ReportItem itemType, String msg) {
		if (report != null) {
			switch (itemType) {
				case PACKAGE:
					currentPackage.put(msg);
					break;

				case CLASS:
					try {
						currentClass.getJSONArray("issues").put(msg);
			
					} catch (JSONException je) {
						System.err.println(je);
					}
				break;

				case CONSTRUCTOR:
				case METHOD:
					try {
						currentMethod.getJSONArray("issues").put(msg);
			
					} catch (JSONException je) {
						System.err.println(je);
					}
					break;

				case PARAMETER:
					currentParameter.put(msg);
					break;

				case FIELD:
					try {
						currentField.getJSONArray("issues").put(msg);
			
					} catch (JSONException je) {
						System.err.println(je);
					}
					break;

				case THROWS:
					currentThrows.put(msg);
					break;

				case LASTITEM:
					appendMessage(lastItem, msg);
					break;
			}
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