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

public class HTMLReporter extends COMTORReporter {
	private static Logger logger = LogManager.getLogger(HTMLReporter.class.getName());

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
		String filename = "comtorReport.html";
		try {
			logger.trace("Attempting to create output report: " + filename);
			removeOldReport(filename);

			// Create a new output report file, and prepare it for writing
			PrintWriter outFilePW = new PrintWriter(new BufferedWriter(new FileWriter(
				new File(Comtor.getCodeDir(), filename))));

			// Write the header of the debug file
			SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			formatter.setTimeZone(Calendar.getInstance().getTimeZone());

			outFilePW.println("<!DOCTYPE html>");
			outFilePW.println("<html>");
			outFilePW.print("	<head>\n		<title>COMTOR Execution Report - ");
			outFilePW.println(formatter.format(new java.util.Date()) + "</title>");
			outFilePW.println("<style type=\"text/css\">");
			outFilePW.println("	.toppad { margin-top: 20px; }");
			outFilePW.println("	.cuddle { margin-bottom: 0px; }");
			outFilePW.println("	td.ok { background-color: #DFF0D8; }");
			outFilePW.println("	td.problem { background-color: #F2DEDE; }");
			outFilePW.println("	tt { font-size: 9pt; font-weight: bold; }");
			outFilePW.println("	.hidden { display: none; }");
 			outFilePW.println("	.unhidden { display: block; }");
			outFilePW.println("</style>");
			outFilePW.println();
			outFilePW.println("<script type=\"text/javascript\">");
			outFilePW.println("function unhideContent(spanID) {");
			outFilePW.println("  var preID = spanID + 'Pre';");
			outFilePW.println("  var postID = spanID + 'Post';");
			outFilePW.println("  var buttonID = spanID + 'Button';");
			outFilePW.println();
			outFilePW.println("  var preElement = document.getElementById(preID);");
			outFilePW.println("  if (preElement) {");
			outFilePW.println("    preElement.className=(preElement.className=='hidden')?'unhidden':'hidden';");
			outFilePW.println("  }");
			outFilePW.println();
			outFilePW.println("  var postElement = document.getElementById(postID);");
			outFilePW.println("  if (postElement) {");
			outFilePW.println("    postElement.className=(postElement.className=='hidden')?'unhidden':'hidden';");
			outFilePW.println("  }");
			outFilePW.println();
			outFilePW.println("  var buttonElement = document.getElementById(buttonID);");
			outFilePW.println("  if (buttonElement) {");
			outFilePW.println("    buttonElement.innerHTML=(buttonElement.innerHTML=='Display pre-/post-analysis content')?'Hide pre-/post-analysis content':'Display pre-/post-analysis content';");
			outFilePW.println("  }");
			outFilePW.println("}");

			outFilePW.println("function unhideReport(reportID) {");
			outFilePW.println("  var report = document.getElementById(reportID);");
			outFilePW.println("  if (report) {");
			outFilePW.println("    report.className=(report.className=='hidden')?'unhidden':'hidden';");
			outFilePW.println("  }");
			outFilePW.println();
			outFilePW.println("  var buttonID = reportID + 'Button';");
			outFilePW.println("  var buttonElement = document.getElementById(buttonID);");
			outFilePW.println("  if (buttonElement) {");
			outFilePW.println("    buttonElement.innerHTML=(buttonElement.innerHTML=='Display report')?'Hide report':'Display report';");
			outFilePW.println("  }");
			outFilePW.println("}");
			outFilePW.println("</script>");
			outFilePW.println();

			outFilePW.println("		<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
			outFilePW.println("		<meta name=\"description\" content=\"COMTOR Execution Report\">");
			outFilePW.println("		<meta name=\"author\" content=\"comtor.org\">");
			outFilePW.println("		<link href=\"http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/css/bootstrap-combined.min.css\" rel=\"stylesheet\">");
			outFilePW.println("	</head>");
			outFilePW.println();
			outFilePW.println("	<body>");
			outFilePW.println("		<div class=\"container\">");
			outFilePW.println("		<div class=\"row toppad\">");
			outFilePW.println("			<div class=\"span8\"><h1><a name=\"top\"></a>COMTOR Execution Report</h1>");
			outFilePW.println("				<p class=\"muted\">Report created: " + formatter.format(new java.util.Date()) + "</p>");
			outFilePW.println("			</div>");
			outFilePW.print("			<div class=\"span2 offset2\"><img src=\"http://www.comtor.org/website/images/comtor/comtorLogo.png\" classs=\"pull-right\" ");
			outFilePW.println("alt=\"COMTOR logo\"/></div>");
			outFilePW.println("		</div>");

			logger.trace("Creating table of summary results.");
			outFilePW.println("			<table class=\"table table-bordered table-condensed\">");
			outFilePW.println("				<caption>Table 1 - Execution Score Summary - (rows in beige contain scores &lt;= 70%, ");
			outFilePW.println("rows in red contain scores &lt;= 50%)</caption>");
			outFilePW.println("				<tbody>");
			outFilePW.println("					<tr><td><strong>Module Name</strong></td><td><strong>Action</strong></td><td><strong>Score</strong></td></tr>");

			Iterator iter = jsonReports.iterator();
			while (iter.hasNext()) {
				String results = (String) iter.next();
				currentReport = new JSONObject(results);
				String moduleName = currentReport.getJSONObject("information").getString("name");
				String moduleScore = currentReport.getJSONObject("results").getString("score");
				String cellClass = "";
				if (Integer.parseInt(moduleScore.substring(0, moduleScore.length()-1)) <= 70)
					cellClass = " class=\"warning\"";
				if (Integer.parseInt(moduleScore.substring(0, moduleScore.length()-1)) <= 50)
					cellClass = " class=\"error\"";

				outFilePW.print("					<tr");
				if (!cellClass.equals(""))
					outFilePW.print(cellClass);
				outFilePW.print("><td><a href=\"#" + getReportNameAsAnchor() + "\">" + moduleName + "</a></td>");
				outFilePW.print("<td><a href=\"javascript:unhideReport('" + getReportNameAsAnchor() + "Report');\" id=\"" + getReportNameAsAnchor());
				outFilePW.print("ReportButton\" class=\"btn btn-mini btn-primary\">Hide report</a></td>");
				outFilePW.println("<td>" + moduleScore + "</td></tr>");
			}

			outFilePW.println("				</tbody>");
			outFilePW.println("			</table>");

			// Iterate through the Vector of JSON report strings and print them to the report file
			iter = jsonReports.iterator();
			while (iter.hasNext()) {
				String results = (String) iter.next();
				currentReport = new JSONObject(results);
				logger.trace("Creating report for module: " + getReportName());

				String reportName = getReportNameAsAnchor();
				outFilePW.println("<p style=\"clear: both;\"/>");
				outFilePW.println("<h3 class=\"cuddle\" style=\"display: inline; float: left;\"><a name=\"" + reportName + "\"></a>" + getReportName());
				// outFilePW.println("	<a href=\"javascript:unhideReport('" + reportName + "Report');\" id=\"" + reportName + "ReportButton\" class=\"btn btn-mini btn-primary\" style=\"margin-left: 25px;\">Hide report</a>");
				outFilePW.println("	<a href=\"javascript:unhideContent('" + reportName + "Content');\" id=\"" + reportName + "ContentButton\" class=\"btn btn-mini btn-primary\">Display pre-/post-analysis content</a>");
				outFilePW.println("</h3>");

				outFilePW.println("<div class=\"unhidden\" id=\"" + getReportNameAsAnchor() + "Report\" style=\"clear:both; padding-left: 25px\">");
 				outFilePW.println("	<div class=\"hidden\" id=\"" + getReportNameAsAnchor() + "ContentPre\">");
 				String descr = getReportDescription();
				if (descr != null)
					outFilePW.println("	<p>" + descr + "</p>");

				String[] amble = getAmbleHTML("preamble");
				if (amble != null && amble.length != 0) {
					outFilePW.println("	<h4 class=\"toppad\">Pre-Report Notes</h4>");
					outFilePW.println("<ul class=\"unstyled\">");
					for (int index = 0; index < amble.length; index++) {
						outFilePW.println("<li>" + amble[index] + "</li>");
					}
					outFilePW.println("</ul>");
				}

				String scoreStr = getScoreHTML();
				if (scoreStr != null) {
					outFilePW.println("<h4 class=\"toppad\">Score</h4>");
					outFilePW.println("<p>Your score for this analysis module is: " + scoreStr + ".</p>");
				}

				outFilePW.println("	</div>");

				outFilePW.println("<h4 class=\"toppad\">Analysis</h4>");
				String [] classesList = getClassNames();
				if (classesList != null && classesList.length > 0) {
					Arrays.sort(classesList);
					outFilePW.println("			<table class=\"table table-bordered table-condensed\">");
					outFilePW.println("				<thead>");
					outFilePW.println("					<tr><th>Entity name</th><th>Comment level</th><th>Analysis</th></tr>");
					outFilePW.println("				</thead>");
					outFilePW.println("				<tbody>");
					for (int index = 0; index < classesList.length; index++) {
						String classname = classesList[index];
						String spanner = "";
						setCurrentClass(classname);

						// Output the class-level results
						logger.trace("Outputting the class-level results: " + classname);
						int numClassIssues = getNumberOfClassIssues();
						int numClassSummaryItems = getNumberOfClassSummaryItems();
						int spanIssues = numClassIssues + numClassSummaryItems;
						if (spanIssues != 0)
							spanner = " rowspan=\"" + spanIssues + "\"";
						else
							spanner = "";
						outFilePW.println("					<tr><td" + spanner + "><tt>" + classname + "</tt></td>");
						outFilePW.println("						<td" + spanner + ">class</td>");
						if (numClassIssues != 0) {
							for (int issueNum = 0; issueNum < numClassIssues; issueNum++) {
								if (issueNum != 0)
									outFilePW.println("					<tr>");

								outFilePW.println("						<td class=\"problem\"><i class=\"icon-remove\"></i> " + getClassIssueByNum(issueNum) + "</td></tr>\n");
							}
						}

						if (numClassSummaryItems != 0) {
							for (int issueNum = 0; issueNum < numClassSummaryItems; issueNum++) {
								if (issueNum != 0)
									outFilePW.println("					<tr>");

								outFilePW.println("						<td class=\"ok\"><i class=\"icon-ok\"></i> " + getClassSummaryItemByNumber(issueNum) + "</td></tr>\n");
							}
						}

						if (numClassIssues == 0 && numClassSummaryItems == 0)
							outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>");

						// Output the constructor-level results
						String [] constructorsList = getConstructorNames();
						if (constructorsList != null) {
							Arrays.sort(constructorsList);
							for (int constrNum = 0; constrNum < constructorsList.length; constrNum++) {
								String constructorName = constructorsList[constrNum];
								int totalNumIssues = getTotalNumberOfConstructorIssues(constructorName);
								spanner = (totalNumIssues != 0) ? " rowspan=\"" + totalNumIssues + "\"" : "";
								outFilePW.println("					<tr><td" + spanner + "><tt>" + constructorName + "</tt></td>");

								// Constructor issues
								int numIssues = getNumberOfConstructorIssues(constructorName, "issues");
								spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";
								outFilePW.println("						<td" + spanner + ">constructor</td>");
								if (numIssues != 0)
									for (int issueNum = 0; issueNum < numIssues; issueNum++) {
										if (issueNum != 0)
											outFilePW.println("			<tr>");
										outFilePW.println("				<td class=\"problem\"><i class=\"icon-remove\"></i> " + getConstructorIssueByNum(constructorName, "issues", issueNum) + "</td></tr>\n");
									}
								else
									outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>");

								String [] paramNames;

								// Constructor @throws
								if (getThrowsAnalyzed()) {
									numIssues = getNumberOfConstructorIssues(constructorName, "throws");
									spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";
									outFilePW.println("					<tr><td" + spanner + ">constructor <tt>@throws</tt></td>");

									paramNames = getExecutableParamThrowsNames("constructors", "throws", constructorName);
									if (paramNames != null) {
										for (int paramNum = 0; paramNum < paramNames.length; paramNum++) {
											String[] paramIssues = getExecutableParamThrowsIssues("constructors", constructorName, "throws", paramNames[paramNum]);
											if (paramIssues != null) {
												numIssues = paramIssues.length;

												if (numIssues != 0)
													for (int issueNum = 0; issueNum < numIssues; issueNum++) {
														if (issueNum != 0)
															outFilePW.println("			<tr>");

														outFilePW.print("			<td class=\"problem\"><i class=\"icon-remove\"></i> throws <tt>");
														outFilePW.println(paramNames[paramNum] + "</tt>: " + paramIssues[issueNum] + "</td></tr>\n");
													}
												else
													outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>");
											} else
												outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> throws: <tt>" + paramNames[paramNum] + "</tt>: No problems detected.</td></tr>");
										}
									} else
										outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> Detected no @throws tags to analyze.</td></tr>");
								}

								// Constructor @parameters
								if (getParamsAnalyzed()) {
									numIssues = getNumberOfConstructorIssues(constructorName, "parameters");
									spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";
									outFilePW.println("					<tr><td" + spanner + ">constructor <tt>@param</tt></td>");

									paramNames = getExecutableParamThrowsNames("constructors", "parameters", constructorName);
									if (paramNames != null) {
										for (int paramNum = 0; paramNum < paramNames.length; paramNum++) {
											String[] paramIssues = getExecutableParamThrowsIssues("constructors", constructorName, "parameters", paramNames[paramNum]);
											if (paramIssues != null) {
												numIssues = paramIssues.length;
												if (numIssues != 0)
													for (int issueNum = 0; issueNum < numIssues; issueNum++) {
														if (issueNum != 0)
															outFilePW.println("			<tr>");

														outFilePW.print("			<td class=\"problem\"><i class=\"icon-remove\"></i> parameter <tt>");
														outFilePW.println(paramNames[paramNum] + "</tt>: " + paramIssues[issueNum] + "</td></tr>\n");
													}
												else
													outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>");
											} else
												outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> parameter <tt>" + paramNames[paramNum] + "</tt>: No problems detected.</td></tr>");
										}
									} else
										outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> Detected no @param tags to analyze.</td></tr>");
								}
							}
						}

						// Output the method-level results
						String [] methodsList = getMethodNames();
						if (methodsList != null) {
							Arrays.sort(methodsList);
							for (int methodNum = 0; methodNum < methodsList.length; methodNum++) {
								String methodName = methodsList[methodNum];
								logger.trace("Outputting the method-level results: " + methodName);

								int totalNumIssues = getTotalNumberOfMethodIssues(methodName);
								spanner = (totalNumIssues != 0) ? " rowspan=\"" + totalNumIssues + "\"" : "";
								outFilePW.println("					<tr><td" + spanner + "><tt>" + methodName + "</tt></td>");

								// Method issues
								int numIssues = getNumberOfMethodIssues(methodName, "issues");
								spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";
								outFilePW.println("						<td" + spanner + ">method</td>");
								if (numIssues != 0)
									for (int issueNum = 0; issueNum < numIssues; issueNum++) {
										if (issueNum != 0)
											outFilePW.println("			<tr>");
										outFilePW.println("				<td class=\"problem\"><i class=\"icon-remove\"></i> " + getMethodIssueByNum(methodName, "issues", issueNum) + "</td></tr>\n");
									}
								else
									outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>");

								String [] paramNames;

								// Method @throws
								if (getThrowsAnalyzed()) {
									numIssues = getNumberOfMethodIssues(methodName, "throws");
									spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";
									outFilePW.println("					<tr><td" + spanner + ">method <tt>@throws</tt></td>");

									paramNames = getExecutableParamThrowsNames("methods", "throws", methodName);
									if (paramNames != null) {
										for (int paramNum = 0; paramNum < paramNames.length; paramNum++) {
											String[] paramIssues = getExecutableParamThrowsIssues("methods", methodName, "throws", paramNames[paramNum]);
											if (paramIssues != null) {
												numIssues = paramIssues.length;

												if (numIssues != 0)
													for (int issueNum = 0; issueNum < numIssues; issueNum++) {
														if (issueNum != 0)
															outFilePW.println("			<tr>");

														outFilePW.print("			<td class=\"problem\"><i class=\"icon-remove\"></i> throws <tt>");
														outFilePW.println(paramNames[paramNum] + "</tt>: " + paramIssues[issueNum] + "</td></tr>\n");
													}
												else
													outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>");
											} else
												outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i>throws: <tt>" + paramNames[paramNum] + "</tt>:  No problems detected.</td></tr>");
										}
									} else
										outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> Detected no @throws tags to analyze</td></tr>");
								}

								// Method @parameters
								if (getParamsAnalyzed()) {
									numIssues = getNumberOfMethodIssues(methodName, "parameters");
									spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";
									outFilePW.println("					<tr><td" + spanner + ">method <tt>@param</tt></td>");

									paramNames = getExecutableParamThrowsNames("methods", "parameters", methodName);
									if (paramNames != null) {
										for (int paramNum = 0; paramNum < paramNames.length; paramNum++) {
											String[] paramIssues = getExecutableParamThrowsIssues("methods", methodName, "parameters", paramNames[paramNum]);
											if (paramIssues != null) {
												numIssues = paramIssues.length;

												if (numIssues != 0)
													for (int issueNum = 0; issueNum < numIssues; issueNum++) {
														if (issueNum != 0)
															outFilePW.println("			<tr>");

														outFilePW.print("			<td class=\"problem\"><i class=\"icon-remove\"></i> parameter <tt>");
														outFilePW.println(paramNames[paramNum] + "</tt>: " + paramIssues[issueNum] + "</td></tr>\n");
													}
												else
													outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>");
											} else
												outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> parameter <tt>" + paramNames[paramNum] + "</tt>: No problems detected.</td></tr>");
										}
									} else
										outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> Detected no @params tags to analyze.</td></tr>");
								}

								// Method @returns
								if (getReturnsAnalyzed()) {
									numIssues = getNumberOfMethodIssues(methodName, "returns");
									spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";
									outFilePW.println("					<tr><td" + spanner + ">method <tt>@return</tt></td>");
									if (numIssues != 0)
										for (int issueNum = 0; issueNum < numIssues; issueNum++) {
											if (issueNum != 0)
												outFilePW.println("			<tr>");
											outFilePW.println("			<td class=\"problem\"><i class=\"icon-remove\"></i> " + getMethodIssueByNum(methodName, "returns", issueNum) +  "</td></tr>\n");
										}
									else
										outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>");
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
									int numIssues = getNumberOfFieldIssues(fieldName);

									logger.trace("Outputting the field-level results: " + fieldName);
									spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";
									outFilePW.println("					<tr><td" + spanner + "><tt>" + fieldName + "</tt></td><td" + spanner + ">field</td>");

									if (getNumberOfFieldIssues(fieldName) != 0)
										for (int issueNum = 0; issueNum < getNumberOfFieldIssues(fieldName); issueNum++) {
											logger.trace("Iterating on issue: " + getFieldIssueByNum(fieldName, issueNum));
											if (issueNum != 0)
												outFilePW.println("			<tr>");
											outFilePW.println("			<td class=\"problem\"><i class=\"icon-remove\"></i> " + getFieldIssueByNum(fieldName, issueNum) + "</td></tr>\n");
										}
									else
										outFilePW.println("								<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td>");
								}
							}
						}
					}

					outFilePW.println("				</tbody>");
					outFilePW.println("			</table>");
				}

				outFilePW.println("<div class=\"hidden\" id=\"" + getReportNameAsAnchor() + "ContentPost\">");
				amble = getAmbleHTML("postamble");
				if (amble != null && amble.length != 0) {
					outFilePW.println("<h4 class=\"toppad\">Post-Report Notes</h4>");
					outFilePW.println("<ul class=\"unstyled\">");
					for (int index = 0; index < amble.length; index++) {
						outFilePW.println("<li>" + amble[index] + "</li>");
					}
					outFilePW.println("</ul>");
				}

				String[] metrics = getExecutionHTML();
				if (metrics != null && metrics.length != 0) {
					outFilePW.println("<h4 class=\"toppad\">Execution Metrics</h4>");
					outFilePW.println("<ul class=\"unstyled\">");
					for (int index = 0; index < metrics.length; index++) {
						outFilePW.println("<li>" + metrics[index] + "</li>");
					}
					outFilePW.println("</ul>");
				}
				outFilePW.println("</div>");

				outFilePW.println("<p class=\"muted\"><small><a href=\"#top\">Return to top</a></small></p>");
				outFilePW.println("</div>");
			}

			outFilePW.println("<hr style=\"clear:both;\">");
			outFilePW.println("<p class=\"muted\"><small>This report was generated by the COMTOR project. See <a href=\"http://www.comtor.org\">www.comtor.org</a> ");
			outFilePW.println("for more information.</small></p>");

			outFilePW.println("		</div>");
			outFilePW.println("		<script src=\"http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/js/bootstrap.min.js\"></script>");
			outFilePW.println("	</body>");
			outFilePW.println("</html>");
			outFilePW.close();
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
