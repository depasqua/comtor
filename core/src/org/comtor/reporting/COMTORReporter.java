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
import org.comtor.drivers.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public abstract class COMTORReporter {
	private static Logger logger = LogManager.getLogger(COMTORReporter.class.getName());

	protected JSONObject currentReport;
	protected JSONObject currentClass;

	/**
	 * Creates a report output file based on the current execution of COMTOR. The method takes the
	 * Vector of JSON strings (module reports) and creates the report file. Children of this class provide specific
	 * report outputting capabilities.
	 *
	 * @see org.comtor.reporting.HTMLReporter
	 * @see org.comtor.reporting.TextReporter
	 *
	 * @param jsonReports a Vector of String object that each contain a specific module's
	 * analysis report in JSON format.
	 */
	public abstract void generateReportFile(Vector<String> jsonReports);

	/**
	 * Returns a string array of the names of all of the classes contained in the current report.
	 * This method assumes that the currentReport variable was previously set.
	 *
	 * @return an array of Strings, each of which is the fully qualified name of a class processed by this report. If the
	 * report processed no classes, a null value is returned.
	 */
	public String[] getClassNames() {
		String[] classnameKeys = null;

		try {
			JSONObject classes = currentReport.getJSONObject("results").getJSONObject("classes");

			if (classes != null)
				classnameKeys = JSONObject.getNames(classes);

		} catch (JSONException je) {
			logger.error(je);
		}
		return classnameKeys;
	}

	/**
	 * Returns a String array of the names of all of the constructors contained in the current class. This method
	 * assumes that the currentClass variable was previously set.
	 *
	 * @return an array of Strings, each of which is the fully qualified name of a constructor in the current
	 * class. If there are no constructors, a null value is returned.
	 */
	protected String[] getConstructorNames() {
		String[] result = null;

		try {
			if (currentClass != null)
				result = JSONObject.getNames(currentClass.getJSONObject("constructors"));

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns a String array of the names of all of the methods contained in the current class. This method
	 * assumes that the currentClass variable was previously set.
	 *
	 * @return an array of Strings, each of which is the fully qualified name of a method in the current
	 * class. If there are no methods, a null value is returned.
	 */
	protected String[] getMethodNames() {
		String[] result = null;

		try {
			if (currentClass != null)
				result = JSONObject.getNames(currentClass.getJSONObject("methods"));

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns a String array of the names of all of the fields contained in the current class. This method
	 * assumes that the currentClass variable was previously set.
	 *
	 * @return an array of Strings, each of which is the fully qualified name of a field in the current
	 * class. If there are no fields, a null value is returned.
	 */
	protected String[] getFieldNames() {
		String [] result = null;

		try {
			if (currentClass != null)
				result = JSONObject.getNames(currentClass.getJSONObject("fields"));

		} catch (JSONException je) {
			logger.error(je);
		}

		return result;
	}

	/**
	 * Sets the currentClass variable to the JSONObject that points to the class object in the current report.
	 * 
	 * @param the String name of the class (fully qualified)
	 */
	public void setCurrentClass(String classname) {
		if (currentReport != null) {
			try {
				JSONObject classesBlock = currentReport.getJSONObject("results").getJSONObject("classes");
				currentClass =classesBlock.getJSONObject(classname);

			} catch (JSONException je) {
				logger.error(je);
			}
		}
	}

	/**
	 * Returns a String representation of the report's classes block as plain text. Used for the plain text
	 * output report. May contain extra labels around data. Do not use for HTML or PDF output.
	 *
	 * @return the String representation
	 */
	protected String getClassesBlock() {
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
						result += "\t\tClass comment analysis:" + newLine;
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
									result += "\t\tConstructor comment analysis:" + newLine;
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
										result += "\t\t\t\t" + paramName + " (@param " + paramName + "): " + newLine;
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
										result += "\t\t\t\t" + throwsName + " (@throws " + throwsName + "): " + newLine;
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
						result += "\t\tMethod comment analysis:" + newLine;
						classOutputCreated = true;
					}

					if (methodnameKeys != null && methodnameKeys.length > 0) {
						// For each method...
						for (String methodname : methodnameKeys) {
							String buildResult = "";
							JSONObject method = methodsObj.getJSONObject(methodname);
							// Deal with method issues...
						 	JSONArray methodIssuesArray = method.getJSONArray("issues");
						 	if (methodIssuesArray.length() > 0) {
								for (int index = 0; index < methodIssuesArray.length(); index++)
									buildResult += "\t\t\t\t" + methodIssuesArray.getString(index) + newLine;
								buildResult += newLine;
							}

							// Deal with parameter issues...
							JSONObject paramObj = method.getJSONObject("parameters");
							String[] paramNames = JSONObject.getNames(paramObj);
							if (paramNames != null && paramNames.length > 0) {
								// For each parameter...
								for (String paramName : paramNames) {
									JSONArray paramIssuesArray = paramObj.getJSONArray(paramName);
									if (paramIssuesArray.length() > 0) {
										buildResult += "\t\t\t\t" + paramName + " (@param " + paramName + "): " + newLine;
										for (int index = 0; index < paramIssuesArray.length(); index++)
											buildResult += "\t\t\t\t\t" + paramIssuesArray.getString(index) + newLine;
										buildResult += newLine;
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
										buildResult += "\t\t\t\t" + throwsName + " (@throws " + throwsName + "): " + newLine;
										for (int index = 0; index < throwsIssuesArray.length(); index++)
											buildResult += "\t\t\t\t\t" + throwsIssuesArray.getString(index) + newLine;
										buildResult += newLine;
									}
								}
							}

							// Deal with returns issues...
							JSONArray returnsArr = method.getJSONArray("returns");
							if (returnsArr != null && returnsArr.length() > 0) {
								buildResult += "\t\t\t\t(@return): " + newLine;
								for (int index = 0; index < returnsArr.length(); index++)
									if (!returnsArr.isNull(index)) {
										buildResult += "\t\t\t\t\t" + returnsArr.getString(index) + newLine;
									}
								buildResult += newLine;
							}

							// Print method name only if there are results to print
							if (!buildResult.equals("")) {
								result += "\t\t\t" + methodname + ": " + newLine;
								result += buildResult;
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
									result += "\t\tField comment analysis: " + newLine;

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
						result += "\t\t" + classObj.getString("noProblemMessage") + newLine + newLine;
				}
			}

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the number of entries in the "issues" array contained in the specified report item type.
	 *
	 * @param itemType the type of item (class, constructor, method, field)
	 * @param itemName the name of the item which contains the issues (required for fields, params, throws only)
	 * @return the total number of issues present for the selected item.
	 */
	protected int getNumberOfAnalysisIssues(ReportItem itemType, String itemName) {
		int result = 0;
		JSONObject targetObject = null;
		JSONArray issuesArray = null;

		try {
			switch (itemType) {
			case CLASS:
				if (currentClass != null)
					issuesArray = currentClass.getJSONArray("issues");
				break;

			case FIELD:
				if (currentClass != null)
					issuesArray = currentClass.getJSONObject("fields").getJSONObject(itemName).getJSONArray("issues");
				break;

			case CONSTRUCTOR:
				if (currentClass != null)
					issuesArray = currentClass.getJSONObject("constructors").getJSONObject(itemName).getJSONArray("issues");
				break;

			case METHOD:
				if (currentClass != null) 
					issuesArray = currentClass.getJSONObject("methods").getJSONObject(itemName).getJSONArray("issues");
				break;
			}
			if (issuesArray != null)
				result = issuesArray.length();
	
		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the number of entries in the "summary" array contained in the specified report item type.
	 *
	 * @param itemType the type of item (class, constructor, method, parameter, field, throws)
	 * @param itemName the name of the item which contains the issues (required for fields, params, throws only)
	 * @return the total number of summary items present in the array.
	 */
	protected int getNumberOfSummaryItems(ReportItem itemType, String itemName) {
		int result = 0;
		JSONArray issuesArray = null;

		try {
			switch (itemType) {
			case CLASS:
				if (currentClass != null)
					issuesArray = currentClass.getJSONArray("summary");
				break;

			case FIELD:
				if (currentClass != null)
					issuesArray = currentClass.getJSONObject("fields").getJSONObject(itemName).getJSONArray("summary");
				break;

			case CONSTRUCTOR:
				if (currentClass != null)
					issuesArray = currentClass.getJSONObject("constructors").getJSONObject(itemName).getJSONArray("summary");
				break;

			case METHOD:
				if (currentClass != null)
					issuesArray = currentClass.getJSONObject("methods").getJSONObject(itemName).getJSONArray("summary");
				break;
			}
			if (issuesArray != null)
				result = issuesArray.length();

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the value of the current class's fields_analyzed variable in the current report.
	 *
	 * @return Returns a true value if the fields_analyzed value is set to true in the current report, false otherwise.
	 */
	public boolean getFieldsAnalyzed() {
		boolean result = false;

		try {
			if (currentClass != null)
				result = currentClass.getBoolean("fields_analyzed");

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the value of the current class's throws_analyzed variable in the current report.
	 *
	 * @return Returns a true value if the throws_analyzed value is set to true in the current report, false otherwise.
	 */
	public boolean getThrowsAnalyzed() {
		boolean result = false;

		try {
			if (currentClass != null)
				result = currentClass.getBoolean("throws_analyzed");

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the value of the current class's params_analyzed variable in the current report.
	 *
	 * @return Returns a true value if the params_analyzed value is set to true in the current report, false otherwise.
	 */
	public boolean getParamsAnalyzed() {
		boolean result = false;

		try {
			if (currentClass != null)
				result = currentClass.getBoolean("params_analyzed");

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the value of the current class's returns_analyzed variable in the current report.
	 *
	 * @return Returns a true value if the returns_analyzed value is set to true in the current report, false otherwise.
	 */
	public boolean getReturnsAnalyzed() {
		boolean result = false;

		try {
			if (currentClass != null)
				result = currentClass.getBoolean("returns_analyzed");

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the number of issues contained in the constructor blocks (issues, parameters, throws) for the specified constrcutor.
	 *
	 * @param constructorName the string name of the constructor over which we wish to count
	 * @return the sum of the number of constructor "issues"
	 */
	protected int getTotalNumberOfConstructorIssues(String constructorName) {
		int result = 0;

		try {
			JSONObject constructorsBlock = currentClass.getJSONObject("constructors");
			if (constructorsBlock != null) {
				JSONObject constructorSought = constructorsBlock.getJSONObject(constructorName);

				int length = constructorSought.getJSONArray("issues").length();
				result += length;

				JSONObject throwsObj = constructorSought.getJSONObject("throws");
				String[] names = JSONObject.getNames(throwsObj);
				length = 0;
				if (names != null) {
					for (int index = 0; index < names.length; index++)
						length += throwsObj.getJSONArray(names[index]).length();
				}
				result += length;

				JSONObject params = constructorSought.getJSONObject("parameters");
				names = JSONObject.getNames(params);
				length = 0;
				if (names != null) {
					for (int index = 0; index < names.length; index++)
						length += params.getJSONArray(names[index]).length();
				}
				result += length;
			}

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the number of issues contained in the method blocks (issues, returns, parameters, throws) for the specified method.
	 *
	 * @param methodName the string name of the method over which we wish to count
	 * @return the sum of the number of method "issues"
	 */
	protected int getTotalNumberOfMethodIssues(String methodName) {
		int result = 0;

		try {
			JSONObject methodsBlock = currentClass.getJSONObject("methods");
			if (methodsBlock != null) {
				JSONObject methodSought = methodsBlock.getJSONObject(methodName);
				int length = 0;

				result += methodSought.getJSONArray("issues").length() + methodSought.getJSONArray("summary").length();			
				result += methodSought.getJSONArray("returns").length();

				JSONObject throwsObj = methodSought.getJSONObject("throws");
				String[] names = JSONObject.getNames(throwsObj);
				if (names != null)
					for (int index = 0; index < names.length; index++)
						length += throwsObj.getJSONArray(names[index]).length();
				result += length;

				JSONObject params = methodSought.getJSONObject("parameters");
				names = JSONObject.getNames(params);
				length = 0;
				if (names != null)
					for (int index = 0; index < names.length; index++)
						length += params.getJSONArray(names[index]).length();
				result += length;
			}

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the number of entries in the "issues" array for the specified constructor.
	 * This method will iterate through all of the issues present returning the sum.
	 *
	 * @param constructorName the name of the constructor 
	 * @return the sum of the number of issues for the specified constructor
	 */
	protected int getNumberOfConstructorIssues(String constructorName, String type) {
		int result = 0;

		try {
			JSONObject constructorsBlock = currentClass.getJSONObject("constructors");
			if (constructorsBlock != null) {
				JSONObject constructorSought = constructorsBlock.getJSONObject(constructorName);
				if (constructorSought != null) {
					JSONArray issuesArray = null;
					if (type.equals("issues")) {
						issuesArray = constructorSought.getJSONArray(type);

						if (issuesArray != null)
							result = issuesArray.length();

					} else if (type.equals("parameters") || type.equals("throws")) {
						// These objects contain objects containing arrays
						JSONObject typeBlock = constructorSought.getJSONObject(type);
						if (typeBlock != null) {
							int length = 0;
							String[] entityNames = JSONObject.getNames(typeBlock);
							if (entityNames != null && entityNames.length > 0) {
								for (int entityNum = 0; entityNum < entityNames.length; entityNum++) {
									JSONArray entityArray = typeBlock.getJSONArray(entityNames[entityNum]);
									length += entityArray.length();
								}
								result = length;
							}
						}
					}
				}
			}

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the number of entries in the "issues" array for the specified method.
	 * This method will iterate through all of the issues present returning the sum.
	 *
	 * @param methodName the fully qualified name of the method sought
	 * @param type the "issue" type (e.g. "issues", "parameters", "returns", "throws")
	 * @return the sum of the number of issues for the specified method
	 */
	protected int getNumberOfMethodIssues(String methodName, String type) {
		int result = 0;

		try {
			JSONObject methodsBlock = currentClass.getJSONObject("methods");
			if (methodsBlock != null) {
				JSONObject methodSought = methodsBlock.getJSONObject(methodName);
				if (methodSought != null) {
					JSONArray issuesArray = null;
					if (type.equals("issues") || type.equals("returns"))
						// These objects contain arrays
						issuesArray = methodSought.getJSONArray(type);

					else if (type.equals("parameters") || type.equals("throws")) {
						// These objects contain objects containing arrays
						JSONObject typeBlock = methodSought.getJSONObject(type);
						if (typeBlock != null) {
							int length = 0;
							String[] entityNames = JSONObject.getNames(typeBlock);

							if (entityNames != null && entityNames.length > 0) {
								for (int entityNum = 0; entityNum < entityNames.length; entityNum++) {
									JSONArray entityArray = typeBlock.getJSONArray(entityNames[entityNum]);
									length += entityArray.length();
								}
								result = (entityNames.length > length) ? entityNames.length : length;
							}
						}
					}
					if (issuesArray != null)
						result = issuesArray.length();
				}
			}

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns a list of "issues" for the specified executable member's parameters/throws given a appropriate 
	 * entity type ("methods", "constructors"), executable name, subtype ("parameters", "throws"), and param/throws name
	 *
	 * @param type the type of executable entity to query ("methods", "constructors")
	 * @param executableName the name of the executable entity to query
	 * @param subType the subtype to query ("parameters", "throws")
	 * @param name the name of the parameter/throws to query
	 * @return the list of issues for the specified parameter as a string array
	 */
	protected String[] getExecutableParamThrowsIssues(String type, String executableName, String subType, String name) {
		String [] result = null;

		try {
			JSONObject methodsBlock = currentClass.getJSONObject(type);
			if (methodsBlock != null) {
				JSONObject parametersBlock = methodsBlock.getJSONObject(executableName).getJSONObject(subType);
				if (parametersBlock != null) {
					JSONArray paramIssues = parametersBlock.getJSONArray(name);
					if (paramIssues != null && paramIssues.length() > 0) {
						result = new String[paramIssues.length()];
						for (int issuesNum = 0; issuesNum < paramIssues.length(); issuesNum++) {
							result[issuesNum] = paramIssues.getString(issuesNum);
						}
					}
				}
			}
			return result;

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the list of parameter/throws names for the specified executable entity (method, constructor) given an appropriate
	 * type ("methods", "constructors") and subtype ("parameters", "throws")
	 *
	 * @param entityType the type of executable entity to query ("methods", "constructors")
	 * @param subType the subtype to query ("parameters", "throws")
	 * @param executableName the name of the executable entity to query
	 * @return the list of parameter names as a string array
	 */
	protected String[] getExecutableParamThrowsNames(String entityType, String subType, String executableName) {
		String [] result = null;

		try {
			JSONObject methodsBlock = currentClass.getJSONObject(entityType);
			if (methodsBlock != null) {
				JSONObject parametersBlock = methodsBlock.getJSONObject(executableName).getJSONObject(subType);
				if (parametersBlock != null)
					result = JSONObject.getNames(parametersBlock);
			}
			return result;

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the number of entries in the "issues" array for the specified field.
	 * This method will iterate through all of the issues present returning the sum.
	 *
	 * @param methodName 
	 * @return the sum of the number of issues for the specified field
	 */
	protected int getNumberOfFieldIssues(String fieldName) {
		int result = 0;

		try {
			JSONObject fieldsBlock = currentClass.getJSONObject("fields");
			if (fieldsBlock != null) {
				JSONObject fieldSought = fieldsBlock.getJSONObject(fieldName);
				if (fieldSought != null) {
					JSONArray issuesArray = fieldSought.getJSONArray("issues");
					if (issuesArray != null)
						result = issuesArray.length();
				}
			}

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the string representation of a specific class-comment issue as specified by the issue number.
	 *
	 * @param issueNum the specific issue number to return.
	 * @return a String representation of the class-comment issue.
	 */
	public String getClassIssueByNum(int issueNum) {
		String result = null;

		try {
			if (currentClass != null) {
				JSONArray issuesArray = currentClass.getJSONArray("issues");
				if (issuesArray != null && issuesArray.length() > 0)
					result = issuesArray.getString(issueNum);
			}

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/** 
	 * Returns the string representation of a specific class-summary report item as specified by the number.
	 *
	 * @param num the specific item number to return.
	 * @return a String representation of the class-sumamry item
	 */
	public String getClassSummaryItemByNumber(int itemNum) {
		String result = null;

		try {
			if (currentClass != null) {
				JSONArray summaryArray = currentClass.getJSONArray("summary");
				if (summaryArray != null && summaryArray.length() > 0)
					result = summaryArray.getString(itemNum);
			}

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the string representation of a specific constructor-level comment issue as specified by the issue number.
	 *
	 * @param constructorName the specific constructor which contains the issue sought.
	 * @param type the issue type to sum (i.e. "issues", "throws", "params", ...)
	 * @param issueNum the specific issue number to return.
	 * @return a String representation of the constructor-comment issue.
	 */
	public String getConstructorIssueByNum(String constructorName, String type, int issueNum) {
		String result = null;

		try {
			if (currentClass != null) {
				JSONObject constructorsBlock = currentClass.getJSONObject("constructors");
				if (constructorsBlock != null) {
					JSONObject constructorSought = constructorsBlock.getJSONObject(constructorName);
					if (constructorSought != null) {
						JSONArray issuesArray = null;
						if (type.equals("issues"))
							issuesArray = constructorSought.getJSONArray(type);

						if (issuesArray != null && issuesArray.length() > 0)
							result = issuesArray.getString(issueNum);
					}
				}
			}

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the string representation of a specific method-level comment issue as specified by the issue number.
	 *
	 * @param methodName the specific method which contains the issue sought.
	 * @param type the issue type to sum (i.e. "issues", "returns", "params", ...)
	 * @param issueNum the specific issue number to return.
	 * @return a String representation of the method-comment issue.
	 */
	public String getMethodIssueByNum(String methodName, String type, int issueNum) {
		String result = null;

		try {
			if (currentClass != null) {
				JSONObject methodBlock = currentClass.getJSONObject("methods");
				if (methodBlock != null) {
					JSONObject methodSought = methodBlock.getJSONObject(methodName);
					if (methodSought != null) {
						JSONArray issuesArray = null;
						if (type.equals("issues") || type.equals("returns"))
							issuesArray = methodSought.getJSONArray(type);

						if (issuesArray != null && issuesArray.length() > 0)
							result = issuesArray.getString(issueNum);
					}
				}
			}
		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 *
	 */
	public String getMethodIssueByNum(String methodName, String type, String entityName, int issueNum) {
		String result = null;

		try {
			if (currentClass != null) {
				JSONObject methodBlock = currentClass.getJSONObject("methods");
				if (methodBlock != null) {
					JSONObject methodSought = methodBlock.getJSONObject(methodName);
					if (methodSought != null) {
						JSONArray issuesArray = null;
						if (type.equals("parameters") || type.equals("throws")) {
							JSONObject typeSought = methodSought.getJSONObject(type);
							issuesArray = typeSought.getJSONArray(entityName);
						}

						if (issuesArray != null && issuesArray.length() > 0)
							result = issuesArray.getString(issueNum);
					}
				}
			}
		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the string representation of a specific field-level comment issue as specified by the issue number.
	 *
	 * @param memberType the type of member containing the issue sought (field, method, constructor)
	 * @param itemName the specific member name which contains the issue sought.
	 * @param itemType the report item type sought (issue or summary)
	 * @param issueNum the specific issue number to return.
	 * @return a String representation of the field-comment issue.
	 */
	public String getMemberIssueSummaryItemByNum(ReportItem memberType, String itemName, ReportIssueType itemType, int issueNum) {
		String result = null;

		try {
			if (currentClass != null) {
				JSONObject memberBlock = null;
				switch (memberType) {
				case CONSTRUCTOR:
					memberBlock = currentClass.getJSONObject("constructors");
					break;

				case METHOD:
					memberBlock = currentClass.getJSONObject("methods");
					break;

				case FIELD:
					memberBlock = currentClass.getJSONObject("fields");
					break;
				}

				if (memberBlock != null) {
					JSONObject itemSought = memberBlock.getJSONObject(itemName);
					if (itemSought != null) {

						JSONArray issuesArray = null;
						switch (itemType) {
							case ISSUE:
								issuesArray = itemSought.getJSONArray("issues");
								break;

							case SUMMARY:
								issuesArray = itemSought.getJSONArray("summary");
								break;
						}
						if (issuesArray != null && issuesArray.length() > 0)
							result = issuesArray.getString(issueNum);
					}
				}
			}

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Checks to determine if the specified methods block of JSON data contains any "issues" to
	 * output. Issues here can be from the analysis of the method's comments, the method's parameters'
	 * comments, the method's throws' comments, or the method's return comments. Returns a true value
	 * if there are issues to print. This method is used to pretty up the text-based output so that we
	 * only print method names/header lines if/when a method contains output that needs to be rendered.
	 *
	 * @param methods A JSONObject that contains the embodyment of a group of methods.
	 * @return a true value if any of the methods contained in the block (or its subordinate data
	 * contains analysis "issues" such that the methods need to be processed for output rendering.
	 */
	protected boolean containsIssues(JSONObject methods) {
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
									} else {
										JSONArray returnArr = method.getJSONArray("returns");
										if (returnArr != null && returnArr.length() > 0)
											result = true;
									}
								}
							}
						}
					}
				} catch (JSONException je) {
					logger.error(je);
				}
			}
		}

		return result;
	}

	/**
	 * Returns a text formatted block of information of the execution timing and metrics 
	 * form executing this module.
	 *
	 * @retuns A String representation of the execution timing/metrics information
	 */
	protected String getExecutionBlock() {
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
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns a text formatted block of information of the execution timing and metrics from executing this
	 * module. This version of the method provides no labels on the output. This method assumes that the 
	 * currentReport variable has been previously set.
	 *
	 * @retuns A String array containing the metrics information
	 */
	protected String[] getExecutionHTML() {
		String[] result = null;

		if (currentReport != null)
			try {
				JSONArray metrics = currentReport.getJSONObject("results").getJSONArray("metrics");
				result = new String[metrics.length()];
				for (int index = 0; index < metrics.length(); index++)
					result[index] = metrics.get(index).toString();

			} catch (JSONException je) {
				logger.error(je);
			}
		return result;
	}

	/**
	 * Returns a String representation of the report's [pre | post] -amble notes as plain text.
	 *
	 * @param blockType A String containing the note type to return (generally "preamble" or "postamble")
	 * @return the String representation
	 */
	protected String getAmbleBlock(String blockType) {
		String newLine = System.getProperty("line.separator");
		String result = null;
		try {
			JSONObject notes = currentReport.getJSONObject("results").getJSONObject("notes");
			JSONArray ambleArray = notes.getJSONArray(blockType);
			if (ambleArray.length() != 0) {
				result = "Notes:" + newLine;
				for (int index = 0; index < ambleArray.length(); index++)
					result += "\t" + ambleArray.getString(index) + newLine;
			}

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns a String representation of the report's [pre | post] -amble notes as plain text, used for the non-
	 * textual (HTML, PDF) output report.
	 *
	 * @param blockType A String containing the note type to return (generally "preamble" or "postamble")
	 * @return the String representation
	 */
	protected String[] getAmbleHTML(String blockType) {
		String result[] = null;
		try {
			JSONObject notes = currentReport.getJSONObject("results").getJSONObject("notes");
			JSONArray ambleArray = notes.getJSONArray(blockType);
			if (ambleArray.length() != 0) {
				result = new String[ambleArray.length()];
				for (int index = 0; index < ambleArray.length(); index++)
					result[index] = ambleArray.getString(index);
			}

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the name of the report block as plain text.
	 *
	 * @return the String name of the report block (module report name)
	 **/
	protected String getReportName() {
		String result = "";
		try {
			result += currentReport.getJSONObject("information").getString("name").trim();

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the name of the report block as all lowercase, and no spaces (used for link anchors).
	 *
	 * @return the String name of the report block in link anchor form.
	 **/
	protected String getReportNameAsAnchor() {
		String result = "";
		try {
			result += currentReport.getJSONObject("information").getString("name").trim();
			result = result.replaceAll("\\s", "");
			result = result.toLowerCase();

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns the report description as plain text.
	 *
	 * @return the report description as a String value
	 **/
	protected String getReportDescription() {
		String result = "";
		try {
			result += currentReport.getJSONObject("information").getString("description").trim();

		} catch (JSONException je) {
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns a String representation of the report name and description as plain text.
	 *
	 * @return the String representation of the report name and description
	 */
	protected String getInfoBlock() {
		String newLine = System.getProperty("line.separator");
		return "Analysis Module: " + getReportName() + newLine + getReportDescription() + newLine;
	}

	/**
	 * Returns a string representation of the final score (for the module), or a null if it is missing from the
	 * report.
	 *
	 * @return a string reference to the score as a percentage.
	 */
	protected String getScoreBlock() {
		String newLine = System.getProperty("line.separator");
		String result = null;

		try {
			JSONObject results = currentReport.getJSONObject("results");
			result = "Overall module score: " + results.getString("score") + newLine;

		} catch (JSONException je) {
			logger.error(je);
		}

		return result;
	}

	/**
	 * Returns a string representation of the final score (for the module), or a null if it is missing from the
	 * report. This version does not prefix a label to the score, used for HTML, PDF, etc. reports.
	 *
	 * @return a string reference to the score as a percentage.
	 */
	protected String getScoreHTML() {
		String result = null;

		try {
			result = currentReport.getJSONObject("results").getString("score");

		} catch (JSONException je) {
			logger.error(je);
		}

		return result;
	}

	/**
	 * Returns a string representation of the type histogram (if present) in the report, or a null otherwise.
	 *
	 * @return a string histogram report or a null value
	 */
	protected String getTypeHistogram() {
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
			logger.error(je);
		}
		return result;
	}

	/**
	 * Returns a text formatted block of information for any charts created by this module
	 * 
	 * @return A String representation of the charting report
	 */
	protected String getChartBlock() {
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
			logger.error(je);
		}
		return result;
	}

	/**
	 * Removes the extra last line, if present, from an output string. Used to pretty up the
	 * JSON-based report output.
	 *
	 * @param input A string of output from the JSON report. The last newline character will be
	 * removed from this input line.
	 * @return A new string output line with the last newline character removed.
	 */
	protected String removeLastNewline(String input) {
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
	 * Check to see the output file currently exists, if so, delete the old file to
	 * replace it with a new one. We should improve this to use a date/time stamp in
	 * the file name so that deletion is redundant.
	 *
	 * @param path the string name of the output file (just filename)
	 */
	protected void removeOldReport(String filename) {
		File outputFile = new File(Comtor.getCodeDir(), filename);
		if (outputFile.exists())
			outputFile.delete();
	}
}