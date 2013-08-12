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
import java.net.*;
import java.text.*;
import java.util.*;
import org.comtor.drivers.*;
import org.json.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class HTMLReporter extends COMTORReporter {
	private static Logger logger = LogManager.getLogger(HTMLReporter.class.getName());

	/**
	 * Creates the HTML-based report file for this execution of COMTOR. The method takes the
	 * Vector of JSON strings (module reports) and outputs them to the comtorReport.txt file.
	 *
	 * @param jsonReports a Vector of String object that each contain a specific module's
	 * analysis report in JSON format.
	 */
	public void generateReportFile(Vector<String> jsonReports) {
		logger.entry();

		// Set the path for the location of the report output file.
		String filename = "comtorReport.html";
		try {
			logger.trace("Attempting to create output report: " + filename);
			removeOldReport(filename);

			// Create a new output report file, and prepare it for writing
			PrintWriter outFilePW = new PrintWriter(new BufferedWriter(new FileWriter(
				new File(Comtor.getCodeDir(), filename))));

			// Write the header of the output file
			SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			formatter.setTimeZone(Calendar.getInstance().getTimeZone());

			outFilePW.println("<!DOCTYPE html>");
			outFilePW.println("<html>");
			outFilePW.println("	<head>");
			outFilePW.println("		<title>COMTOR Execution Report - " + formatter.format(new java.util.Date()) + "</title>");
			outFilePW.println("		<style type=\"text/css\">");
			outFilePW.println("			.toppad { margin-top: 20px; }");
			outFilePW.println("			.cuddle { margin-bottom: 0px; }");
			outFilePW.println("			.code { font-family: \"Lucida Console\", Monaco, monospace; font-size: 9pt; font-weight: bold; }");
			outFilePW.println("			.hidden { display: none; }");
 			outFilePW.println("			.unhidden { display: block; }");
			outFilePW.println("			td.ok { background-color: #DFF0D8; }");
			outFilePW.println("			td.problem { background-color: #F2DEDE; }");
			outFilePW.println("		</style>");
			outFilePW.println();
			outFilePW.println("		<link type=\"text/css\" href=\"http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/css/bootstrap-combined.min.css\" rel=\"stylesheet\">");
			outFilePW.println("		<script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-1.8.3.min.js\"></script>");
			outFilePW.println("		<script type=\"text/javascript\" src=\"http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/js/bootstrap.min.js\"></script>");
			outFilePW.println();
			outFilePW.println("		<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
			outFilePW.println("		<meta name=\"description\" content=\"COMTOR Execution Report\">");
			outFilePW.println("		<meta name=\"author\" content=\"comtor.org\">");
			outFilePW.println("	</head>");
			outFilePW.println();
			outFilePW.println("	<body>");

			generateHTMLBody(jsonReports, outFilePW);
			generateHTMLFooter(outFilePW);

		} catch (IOException e) {
			logger.error(e);
		}
	}

	/** 
	 * Creates the footer of the HTML report. This is separated out so that we can reuse the body of the report without
	 * the HTMl header / footer in cases like the webcat-based report.
	 * 
	 * @param outFilePW The destination print writer (output file)
	 */
	private void generateHTMLFooter(PrintWriter outFilePW) {
		logger.entry();
		outFilePW.println("	</body>");
		outFilePW.println("</html>");
		outFilePW.close();			
		logger.exit();
	}

	/**
	 * Creates the body of the HTML report. This is separated out so that we can reuse the body of the report without
	 * the HTMl header / footer in cases like the webcat-based report.
	 * 
	 * @param jsonReports a Vector of String object that each contain a specific module's
	 * analysis report in JSON format.
	 *
	 * @param outFilePW The destination print writer (output file)
	 */
	private void generateHTMLBody(Vector<String> jsonReports, PrintWriter outFilePW) {
		logger.entry();

		try {
			// Write the header of the debug file
			SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			formatter.setTimeZone(Calendar.getInstance().getTimeZone());

			outFilePW.println("		<div class=\"container\">");
			outFilePW.println("		<div class=\"row toppad\">");
			outFilePW.println("			<div class=\"span8\" id=\"top\"><h1>COMTOR Execution Report</h1>");
			outFilePW.println("				<p class=\"muted\">Report created: " + formatter.format(new java.util.Date()) + "</p>");
			outFilePW.println("			</div>");
			outFilePW.print("			<div class=\"span2 offset2\"><img src=\"http://www.comtor.org/website/images/comtor/comtorLogo.png\" class=\"pull-right\" ");
			outFilePW.println("alt=\"COMTOR logo\"/></div>");
			outFilePW.println("		</div>");

			outFilePW.println("			<table summary=\"Execution score summary by module\" class=\"table table-bordered table-condensed\">");
			outFilePW.print("				<caption>Table 1 - Execution Score Summary</caption>");
			outFilePW.println("				<tbody>");
			outFilePW.println("					<tr><td><strong>Module Name</strong></td><td><strong>Score</strong></td></tr>\n");

			String reportName = null;
			String reportNameAnchor = null;

			Iterator iter = jsonReports.iterator();
			while (iter.hasNext()) {
				String results = (String) iter.next();
				currentReport = new JSONObject(results);
				reportName = currentReport.getJSONObject("information").getString("name");
				String moduleScore = currentReport.getJSONObject("results").getString("score");

				logger.trace("Iterating through report: " + reportName);
				String cellClass = "";
				if (!moduleScore.contains(".")) {
					if (Integer.parseInt(moduleScore.substring(0, moduleScore.length()-1)) <= 70)
						cellClass = " class=\"warning\"";
					if (Integer.parseInt(moduleScore.substring(0, moduleScore.length()-1)) <= 50)
						cellClass = " class=\"error\"";
				}

				outFilePW.print("					<tr");
				if (!cellClass.equals(""))
					outFilePW.print(cellClass);
				outFilePW.print("><td><a href=\"#" + getReportNameAsAnchor() + "\">" + reportName + "</a></td>");
				outFilePW.println("<td>");

				if (moduleScore.endsWith("%")) {
					int success = Integer.valueOf(moduleScore.substring(0, moduleScore.length()-1));
					int fail = 100 - success;
					outFilePW.println("						<div class=\"progress\" style=\"margin-bottom: 0px; z-index: 2\">");
					outFilePW.println("						<div class=\"bar bar-success\" style=\"width: " +  success + "%\">");
					outFilePW.println("						<span>" + moduleScore + "</span></div>");
					outFilePW.println("						<div class=\"bar bar-danger\" style=\"width: " +  fail + "%\"></div></div>");

				} else
					outFilePW.println(moduleScore);
				outFilePW.println("					</td></tr>\n");
			}

			outFilePW.println("				</tbody>");
			outFilePW.println("			</table>");

			// Iterate through the Vector of JSON report strings and print them to the report file
			iter = jsonReports.iterator();
			while (iter.hasNext()) {
				String results = (String) iter.next();
				currentReport = new JSONObject(results);
				reportName = getReportName();
				reportNameAnchor = getReportNameAsAnchor();
				boolean disputableReport = false;
				logger.trace("Creating report for module: " + reportName);

				if (reportName.equals("Offensive Words") || reportName.equals("Spell Check"))
					disputableReport = true;

				outFilePW.println("			<p style=\"clear: both;\"></p>");
				outFilePW.println("			<h3 class=\"cuddle\" style=\"display: inline; float: left;\" id=\"" + reportNameAnchor + "\">" + reportName);
				outFilePW.println("			</h3>");

				outFilePW.println("			<div class=\"unhidden\" id=\"" + reportNameAnchor + "Report\" style=\"clear:both; padding-left: 25px\">");

 				String descr = getReportDescription();
				if (descr != null)
					outFilePW.println("					<p>" + descr + "</p>\n");

				String[] amble = getAmbleHTML("preamble");
				if (amble != null && amble.length != 0) {
					outFilePW.println("					<h4 class=\"toppad\">Pre-Report Notes</h4>");
					outFilePW.println("					<ul class=\"unstyled\">");
					for (int index = 0; index < amble.length; index++) {
						outFilePW.println("						<li>" + amble[index] + "</li>");
					}
					outFilePW.println("					</ul>\n");
				}

				String scoreStr = getScoreHTML();
				if (scoreStr != null) {
					outFilePW.println("					<h4 class=\"toppad\">Score</h4>");
					outFilePW.println("					<p>Your score for this analysis module is: " + scoreStr + ".</p>");
				}

				outFilePW.println("				<h4 class=\"toppad\">Analysis</h4>");
				String [] classesList = getClassNames();
				if (classesList != null && classesList.length > 0) {
					Arrays.sort(classesList);
					outFilePW.println("				<table summary=\"Module analysis\" class=\"table table-bordered table-condensed\">");
					outFilePW.println("					<thead>");
					outFilePW.println("						<tr><th>Entity name</th><th>Comment level</th><th>Analysis</th></tr>");
					outFilePW.println("					</thead>\n");
					outFilePW.println("					<tbody>");
					for (int index = 0; index < classesList.length; index++) {
						String classname = classesList[index];
						String spanner = "";
						setCurrentClass(classname);

						// Output the class-level results
						int spanIssues = getNumberOfAnalysisIssues(ReportItem.CLASS, null);
						if (spanIssues != 0)
							spanner = " rowspan=\"" + spanIssues + "\"";
						else
							spanner = "";
						
						if (spanIssues != 0) {
							outFilePW.println("						<tr><td" + spanner + "><span class=\"code\">" + classname + "</span></td><td" + spanner + ">class</td>");
							for (int issueNum = 0; issueNum < spanIssues; issueNum++) {
								if (issueNum != 0)
									outFilePW.print("						<tr>");
								else
									outFilePW.print("							");
								outFilePW.print("<td class=\"problem\"><i class=\"icon-remove\">&nbsp;</i> " + getClassIssueByNum(issueNum) + "</td></tr>\n");
							}

						} else {
							spanIssues = getNumberOfSummaryItems(ReportItem.CLASS, null);
							if (spanIssues != 0)
								spanner = " rowspan=\"" + spanIssues + "\"";
							else
								spanner = "";

							if (spanIssues != 0) {
								outFilePW.println("						<tr><td" + spanner + "><span class=\"code\">" + classname + "</span></td><td" + spanner + ">class</td>");
								for (int issueNum = 0; issueNum < spanIssues; issueNum++) {
									if (issueNum != 0)
										outFilePW.println("						<tr>");
									outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> " + getClassSummaryItemByNumber(issueNum) + "</td></tr>\n");
								}
							}
						}

						// Output the constructor-level results
						String [] constructorsList = getConstructorNames();
						if (constructorsList != null) {
							Arrays.sort(constructorsList);
							for (int constrNum = 0; constrNum < constructorsList.length; constrNum++) {
								String constructorName = constructorsList[constrNum];

								// Constructor issues
								spanIssues = getTotalNumberOfConstructorIssues(constructorName);
								if (spanIssues > 0) {
									spanner = " rowspan=\"" + spanIssues + "\"";
									outFilePW.print("						<tr><td" + spanner + "><span class=\"code\">" + constructorName + "</span></td>");
	
									spanIssues = getNumberOfAnalysisIssues(ReportItem.CONSTRUCTOR, constructorName);
									if (spanIssues != 0) {
										spanner = " rowspan=\"" + spanIssues + "\"";
										outFilePW.println("<td" + spanner + ">constructor</td>");
										for (int issueNum = 0; issueNum < spanIssues; issueNum++) {
											if (issueNum != 0)
												outFilePW.println("						<tr>");
											outFilePW.print("							<td class=\"problem\"><i class=\"icon-remove\">&nbsp;</i>" +
												getMemberIssueSummaryItemByNum(ReportItem.CONSTRUCTOR, constructorName, ReportIssueType.ISSUE, issueNum));
											outFilePW.println("</td></tr>\n");
										}

										String [] paramNames;
										// Constructor @throws
										if (getThrowsAnalyzed()) {
											int numIssues = getNumberOfConstructorIssues(constructorName, "throws");
											spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";

											if (numIssues != 0)
												outFilePW.println("						<tr><td" + spanner + ">constructor <span class=\"code\">@throws</span></td>");

											paramNames = getExecutableParamThrowsNames("constructors", "throws", constructorName);
											if (paramNames != null) {
												for (int paramNum = 0; paramNum < paramNames.length; paramNum++) {
													String[] paramIssues = getExecutableParamThrowsIssues("constructors", constructorName, "throws", paramNames[paramNum]);
													if (paramIssues != null) {
														numIssues = paramIssues.length;

														if (numIssues != 0)
															for (int issueNum = 0; issueNum < numIssues; issueNum++) {
																if (issueNum != 0 || (issueNum == 0 && paramNum != 0))
																	outFilePW.print("						<tr>");
																else
																	outFilePW.print("							");

																outFilePW.print("<td class=\"problem\"><i class=\"icon-remove\">&nbsp;</i> throws <span class=\"code\">");
																outFilePW.print(paramNames[paramNum] + "</span>: " + paramIssues[issueNum]);
																outFilePW.println("</td></tr>\n");
															}
														// else
														// 	outFilePW.println("							<td></td></tr>\n");
													} else {
														if (paramNum != 0)
															outFilePW.print("						<tr>");
														else
															outFilePW.print("							");
														outFilePW.println("<td></td></tr>\n");
													}
												}
											} // else
												// outFilePW.println("							<td></td></tr>\n");
										}

										// Constructor @parameters
										if (getParamsAnalyzed()) {
											int numIssues = getNumberOfConstructorIssues(constructorName, "parameters");
											spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";

											if (numIssues != 0)
												outFilePW.println("						<tr><td" + spanner + ">constructor <span class=\"code\">@param</span></td>");

											paramNames = getExecutableParamThrowsNames("constructors", "parameters", constructorName);
											if (paramNames != null) {
												for (int paramNum = 0; paramNum < paramNames.length; paramNum++) {
													String[] paramIssues = getExecutableParamThrowsIssues("constructors", constructorName, "parameters", paramNames[paramNum]);
													if (paramIssues != null) {
														numIssues = paramIssues.length;
														if (numIssues != 0)
															for (int issueNum = 0; issueNum < numIssues; issueNum++) {
																if (issueNum != 0 || (issueNum == 0 && paramNum > 0))
																	outFilePW.print("						<tr>");
																else
																	outFilePW.print("							");
																outFilePW.print("<td class=\"problem\"><i class=\"icon-remove\">&nbsp;</i> parameter <span class=\"code\">");
																outFilePW.print(paramNames[paramNum] + "</span>: " + paramIssues[issueNum]);
																outFilePW.println("</td></tr>\n");
															}
													}
												}
											}
										}
									}
								} else {
									spanIssues = getNumberOfSummaryItems(ReportItem.CONSTRUCTOR, constructorName);
									if (spanIssues != 0) {
										spanner = (spanIssues != 0) ? " rowspan=\"" + spanIssues + "\"" : "";
										outFilePW.println("						<tr><td" + spanner + "><span class=\"code\">" + constructorName + "</span></td>");
										outFilePW.println("							<td" + spanner + ">constructor</td>");
										for (int issueNum = 0; issueNum < spanIssues; issueNum++) {
											outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> " +
												getMemberIssueSummaryItemByNum(ReportItem.CONSTRUCTOR, constructorName, ReportIssueType.SUMMARY, issueNum) + "</td></tr>\n");
										}
									}
								}
							}
						}

						// Output the method-level results
						String [] methodsList = getMethodNames();
						if (methodsList != null) {
							Arrays.sort(methodsList);
							for (int methodNum = 0; methodNum < methodsList.length; methodNum++) {
								String methodName = methodsList[methodNum];

								spanIssues = getTotalNumberOfMethodIssues(methodName);
								if (spanIssues != 0) {
									spanner = " rowspan=\"" + spanIssues + "\"";
									outFilePW.print("						<tr><td" + spanner + "><span class=\"code\">" + methodName + "</span></td>");

									int numMethodIssues = getNumberOfAnalysisIssues(ReportItem.METHOD, methodName);
									if (numMethodIssues != 0)
										spanner = " rowspan=\"" + numMethodIssues + "\"";
									else
										spanner = "";

									if (numMethodIssues != 0) {
										outFilePW.println("<td" + spanner + ">method</td>");
										for (int issueNum = 0; issueNum < numMethodIssues; issueNum++) {
											if (issueNum != 0)
												outFilePW.println("						<tr>");
											outFilePW.print("							<td class=\"problem\"><i class=\"icon-remove\">&nbsp;</i> " +
												getMemberIssueSummaryItemByNum(ReportItem.METHOD, methodName, ReportIssueType.ISSUE, issueNum));
											outFilePW.println("</td></tr>\n");
										}
									}

									int numMethodSummaryItems = getNumberOfSummaryItems(ReportItem.METHOD, methodName);
									if (numMethodSummaryItems != 0) {
										for (int issueNum = 0; issueNum < numMethodSummaryItems; issueNum++) {
											if (issueNum != 0 || (issueNum == 0 && numMethodIssues > 0))
												outFilePW.println("						<tr>");
											outFilePW.println("<td" + spanner + ">method</td>");
											outFilePW.println("							<td class=\"ok\">&nbsp;<i class=\"icon-ok\"></i> " +
													getMemberIssueSummaryItemByNum(ReportItem.METHOD, methodName, ReportIssueType.SUMMARY, issueNum) + "</td></tr>\n");
										}
									}

									String [] paramNames;
									// Method @throws
									if (getThrowsAnalyzed()) {
										int numIssues = getNumberOfMethodIssues(methodName, "throws");
										spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";

										if (numIssues > 0)
											outFilePW.println("						<tr><td" + spanner + ">method <span class=\"code\">@throws</span></td>");

										paramNames = getExecutableParamThrowsNames("methods", "throws", methodName);
										if (paramNames != null) {
											for (int paramNum = 0; paramNum < paramNames.length; paramNum++) {
												String[] paramIssues = getExecutableParamThrowsIssues("methods", methodName, "throws", paramNames[paramNum]);
												if (paramIssues != null) {
													numIssues = paramIssues.length;

													if (numIssues != 0)
														for (int issueNum = 0; issueNum < numIssues; issueNum++) {
															if (issueNum != 0 || (issueNum == 0 && paramNum > 0))
																outFilePW.println("						<tr>");

															outFilePW.print("							<td class=\"problem\"><i class=\"icon-remove\">&nbsp;</i> throws <span class=\"code\">");
															outFilePW.print(paramNames[paramNum] + "</span>: " + paramIssues[issueNum]);
															outFilePW.println("</td></tr>\n");
														}
												}
											}
										}
									}

									// Method @parameters
									if (getParamsAnalyzed()) {
										int numIssues = getNumberOfMethodIssues(methodName, "parameters");
										spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";

										if (numIssues > 0)
											outFilePW.println("						<tr><td" + spanner + ">method <span class=\"code\">@param</span></td>");

										paramNames = getExecutableParamThrowsNames("methods", "parameters", methodName);
										if (paramNames != null) {
											for (int paramNum = 0; paramNum < paramNames.length; paramNum++) {
												String[] paramIssues = getExecutableParamThrowsIssues("methods", methodName, "parameters", paramNames[paramNum]);
												if (paramIssues != null) {
													numIssues = paramIssues.length;

													if (numIssues != 0)
														for (int issueNum = 0; issueNum < numIssues; issueNum++) {
															if (issueNum != 0 || (issueNum == 0 && paramNum != 0))
																outFilePW.println("						<tr>");

															outFilePW.print("							<td class=\"problem\"><i class=\"icon-remove\">&nbsp;</i> parameter <span class=\"code\">");
															outFilePW.print(paramNames[paramNum] + "</span>: " + paramIssues[issueNum]);
															outFilePW.println("</td></tr>\n");
														}
												}
											}
										}
									}

									// Method @returns
									if (getReturnsAnalyzed()) {
										int numIssues = getNumberOfMethodIssues(methodName, "returns");
										spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";

										if (numIssues != 0) {
											outFilePW.println("						<tr><td" + spanner + ">method <span class=\"code\">@return</span></td>");
											for (int issueNum = 0; issueNum < numIssues; issueNum++) {
												if (issueNum != 0)
													outFilePW.print("						<tr>");
												else
													outFilePW.print("							");
												outFilePW.print("<td class=\"problem\"><i class=\"icon-remove\">&nbsp;</i> " + getMethodIssueByNum(methodName, "returns", issueNum));
												outFilePW.print("</td></tr>\n");
											}
										}
									}
								}
							}
						}

						// Output the field-level results
						if (getFieldsAnalyzed()) {
							String [] fieldsList = getFieldNames();
							if (fieldsList != null) {
								Arrays.sort(fieldsList);
								for (int fieldNum = 0; fieldNum < fieldsList.length; fieldNum++) {
									String fieldName = fieldsList[fieldNum];

									int numFieldIssues = getNumberOfAnalysisIssues(ReportItem.FIELD, fieldName);
									if (numFieldIssues != 0)
										spanner = " rowspan=\"" + numFieldIssues + "\"";
									else
										spanner = "";

									if (numFieldIssues != 0) {
										outFilePW.println("						<tr><td" + spanner + "><span class=\"code\">" + fieldName + "</span></td><td" + spanner + ">field</td>");
										for (int issueNum = 0; issueNum < numFieldIssues; issueNum++) {
											if (issueNum != 0)
												outFilePW.print("						<tr>");
											else
												outFilePW.print("							");
											outFilePW.print("<td class=\"problem\"><i class=\"icon-remove\">&nbsp;</i> " +
													getMemberIssueSummaryItemByNum(ReportItem.FIELD, fieldName, ReportIssueType.ISSUE, issueNum));
											outFilePW.println("</td></tr>\n");
										}

									} else {
										int numFieldSummaryItems = getNumberOfSummaryItems(ReportItem.FIELD, fieldName);
										if (numFieldSummaryItems != 0)
											spanner = " rowspan=\"" + numFieldSummaryItems + "\"";
										else
											spanner = "";

										if (numFieldSummaryItems != 0) {
											outFilePW.println("						<tr><td" + spanner + "><span class=\"code\">" + fieldName + "</span></td><td" + spanner + ">field</td>");
											for (int issueNum = 0; issueNum < numFieldSummaryItems; issueNum++) {
												if (issueNum != 0)
													outFilePW.print("						<tr>");
												else
													outFilePW.print("							");
												outFilePW.println("<td class=\"ok\"><i class=\"icon-ok\"></i> " +
														getMemberIssueSummaryItemByNum(ReportItem.FIELD, fieldName, ReportIssueType.SUMMARY, issueNum) + "</td></tr>\n");
											}
										}
									}
								}
							}
						}
					}

					outFilePW.println("					</tbody>");
					outFilePW.println("				</table>\n");
				}

				outFilePW.println("				<div class=\"hidden\" id=\"" + getReportNameAsAnchor() + "ContentPost\">");
				amble = getAmbleHTML("postamble");
				if (amble != null && amble.length != 0) {
					outFilePW.println("				<h4 class=\"toppad\">Post-Report Notes</h4>");
					outFilePW.println("				<ul class=\"unstyled\">");
					for (int index = 0; index < amble.length; index++) {
						outFilePW.println("					<li>" + amble[index] + "</li>");
					}
					outFilePW.println("				</ul>\n");
				}

				String[] metrics = getExecutionHTML();
				if (metrics != null && metrics.length != 0) {
					outFilePW.println("				<h4 class=\"toppad\">Execution Metrics</h4>");
					outFilePW.println("				<ul class=\"unstyled\">");
					for (int index = 0; index < metrics.length; index++) {
						outFilePW.println("					<li>" + metrics[index] + "</li>");
					}
					outFilePW.println("				</ul>");
				}
				outFilePW.println("				</div>\n");

				outFilePW.println("				<p class=\"muted\"><small><a href=\"#top\">Return to top</a></small></p>");
				outFilePW.println("			</div>\n");
			}

			outFilePW.println("			<hr style=\"clear:both;\">");
			outFilePW.println("			<p class=\"muted\"><small>This report was generated by the COMTOR project. See <a href=\"http://www.comtor.org\">www.comtor.org</a> ");
			outFilePW.println("for more information.</small></p>\n");

			outFilePW.println("		</div>\n");

		} catch (JSONException je) {
			logger.error(je);

		} finally {
			logger.exit();
		}
	}
}
