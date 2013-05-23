/**
 *  Comment Mentor: A Comment Quality Assessment Tool
 *  Copyright (C) 2013 The College of New Jersey
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

public class WebcatReporter extends COMTORReporter {
	private static Logger logger = LogManager.getLogger(WebcatReporter.class.getName());
	private static int buttonIDNum = 0;

	/**
	 * Creates the HTML that backs a 'dispute' button used in certain HTML report values.
	 *
	 * @param elementID the HTML ID of the button we are about to create
	 * @param reportName the string name of the report (used in submitting the word to the project team)
	 * @param submittedWord a string containing the word or phrase being reported. For convenience we currently
	 *        use the entire report's error statement (an "issue" in JSON parlance from our report structure)
	 * @return a string representation of the output necessary to create an HTML button (using Bootstrap) for display.
	 */
	private String createDisputeButton(String elementID, String reportName, String submittedWord) {
		logger.entry();
		String result = "<span class=\"pull-right\">\n";
		try {
			String word = submittedWord.substring(1, submittedWord.indexOf("'", 1));
			word = URLEncoder.encode(word, "UTF-8");
			String report = URLEncoder.encode(reportName, "UTF-8");
			result += "	<a id=\"" + elementID + "\" class=\"btn btn-primary btn-mini\" href=\"javascript:submitWord('" + 
				word + "', '" + report + "', '" + elementID + "');\">Dispute</a>\n";

		} catch (UnsupportedEncodingException uee) {
			logger.error(uee);
		}

		result += "</span>";
		logger.exit();
		return result;
	}

	/**
	 * Returns the next button id number. Used to generate uniquely numbered "dispute" buttons (number is used in the ID
	 * of the button)
	 *
	 * @return the next button number in sequence
	 */
	private int getNextButtonNum() {
		return buttonIDNum++;
	}

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

			outFilePW.println("		<div id=\"modalWarning\" class=\"modal hide fade\" tabindex=\"-1\" data-keyboard=\"true\">");
			outFilePW.println("			<div class=\"modal-header\">");
			outFilePW.println("				<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>");
			outFilePW.println("				<h3>A note about disputes...</h3>");
			outFilePW.println("			</div>");
			outFilePW.println("			<div class=\"modal-body\">");
			outFilePW.println("				<img src=\"http://www.comtor.org/website/images/comtor/comtorLogo.png\" class=\"pull-left\" alt=\"COMTOR logo\" width=\"100px\"></img>" +
					"<p>Please note that each dispute results in an email being sent to the development team.</p>");
			outFilePW.println();
			outFilePW.println("				<p>Dispute decisions are not automatic and a development team member will review the dispute and adjudicate any spelling/offensive word " +
					"changes to the system as soon as possible. Please do not rerun the report and expect to see the dispute resolved immediately. Dispute adjudication decisions " +
					"are not currently emailed to you, but we may consider doing this in the future.</p>");
			outFilePW.println();
			outFilePW.println("				<p>We appreciate your feedback and welcome your input to make COMTOR the best possible system.</p>");
			outFilePW.println("			</div>");
			outFilePW.println("			<div class=\"modal-footer\">");
			outFilePW.println("				<a href=\"#\" class=\"btn btn-primary\" data-dismiss=\"modal\">OK</a>");
			outFilePW.println("			</div>");
			outFilePW.println("		</div>");

			outFilePW.println("		<div class=\"container\">");
			outFilePW.println("		<div class=\"row toppad\">");
			outFilePW.println("			<div class=\"span8\" id=\"top\"><h1>COMTOR Execution Report</h1>");
			outFilePW.println("				<p class=\"muted\">Report created: " + formatter.format(new java.util.Date()) + "</p>");
			outFilePW.println("			</div>");
			outFilePW.print("			<div class=\"span2 offset2\"><img src=\"http://www.comtor.org/website/images/comtor/comtorLogo.png\" class=\"pull-right\" ");
			outFilePW.println("alt=\"COMTOR logo\"/></div>");
			outFilePW.println("		</div>");

			outFilePW.println("			<table class=\"table table-bordered table-condensed\">");
			outFilePW.print("				<caption>Table 1 - Execution Score Summary - (rows in beige contain scores &lt;= 70%, ");
			outFilePW.println("rows in red contain scores &lt;= 50%)</caption>");
			outFilePW.println("				<tbody>");
			outFilePW.println("					<tr><td><strong>Module Name</strong></td><td><strong>Action</strong></td><td><strong>Score</strong></td></tr>\n");

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
				outFilePW.print("<td><a href=\"javascript:unhideReport('" + getReportNameAsAnchor() + "Report');\" id=\"" + getReportNameAsAnchor());
				outFilePW.print("ReportButton\" class=\"btn btn-mini btn-primary\">Hide report</a></td>");
				outFilePW.println("<td>" + moduleScore + "</td></tr>\n");
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
				outFilePW.println("				<a href=\"javascript:unhideContent('" + reportNameAnchor + "Content');\" id=\"" + reportNameAnchor + "ContentButton\" class=\"btn btn-mini btn-primary\">Display pre-/post-analysis content</a>");
				outFilePW.println("			</h3>");

				outFilePW.println("			<div class=\"unhidden\" id=\"" + reportNameAnchor + "Report\" style=\"clear:both; padding-left: 25px\">");
 				outFilePW.println("				<div class=\"hidden\" id=\"" + reportNameAnchor + "ContentPre\">");

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

				outFilePW.println("				</div>\n");

				outFilePW.println("				<h4 class=\"toppad\">Analysis</h4>");
				String [] classesList = getClassNames();
				if (classesList != null && classesList.length > 0) {
					Arrays.sort(classesList);
					outFilePW.println("				<table class=\"table table-bordered table-condensed\">");
					outFilePW.println("					<thead>");
					outFilePW.println("						<tr><th>Entity name</th><th>Comment level</th><th>Analysis</th></tr>");
					outFilePW.println("					</thead>\n");
					outFilePW.println("					<tbody>");
					for (int index = 0; index < classesList.length; index++) {
						String classname = classesList[index];
						String spanner = "";
						setCurrentClass(classname);

						// Output the class-level results
						int numClassIssues = getNumberOfAnalysisIssues(ReportItem.CLASS, null);
						int numClassSummaryItems = getNumberOfSummaryItems(ReportItem.CLASS, null);
						int spanIssues = numClassIssues + numClassSummaryItems;
						if (spanIssues != 0)
							spanner = " rowspan=\"" + spanIssues + "\"";
						else
							spanner = "";
						outFilePW.println("						<tr><td" + spanner + "><span class=\"code\">" + classname + "</span></td><td" + spanner + ">class</td>");
						if (numClassIssues != 0) {
							for (int issueNum = 0; issueNum < numClassIssues; issueNum++) {
								if (issueNum != 0)
									outFilePW.print("						<tr>");
								else
									outFilePW.print("							");
								outFilePW.print("<td class=\"problem\"><i class=\"icon-remove\"></i> " + getClassIssueByNum(issueNum));
								if (disputableReport)
									outFilePW.print(createDisputeButton("disputeButton" + getNextButtonNum(), reportName, getClassIssueByNum(issueNum)));
								outFilePW.println("</td></tr>\n");
							}
						}

						if (numClassSummaryItems != 0) {
							for (int issueNum = 0; issueNum < numClassSummaryItems; issueNum++) {
								if (issueNum != 0 || (issueNum == 0 && numClassIssues > 0))
									outFilePW.println("						<tr>");
								outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> " + getClassSummaryItemByNumber(issueNum) + "</td></tr>\n");
							}
						}

						if (numClassIssues == 0 && numClassSummaryItems == 0)
							outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>\n");

						// Output the constructor-level results
						String [] constructorsList = getConstructorNames();
						if (constructorsList != null) {
							Arrays.sort(constructorsList);
							for (int constrNum = 0; constrNum < constructorsList.length; constrNum++) {
								String constructorName = constructorsList[constrNum];

								// Constructor issues
								spanIssues = getTotalNumberOfConstructorIssues(constructorName);
								if (spanIssues != 0)
									spanner = " rowspan=\"" + spanIssues + "\"";
								else
									spanner = "";
								outFilePW.print("						<tr><td" + spanner + "><span class=\"code\">" + constructorName + "</span></td>");

								int numConstrIssues = getNumberOfAnalysisIssues(ReportItem.CONSTRUCTOR, constructorName);
								int numConstrSummaryItems = getNumberOfSummaryItems(ReportItem.CONSTRUCTOR, constructorName);
								spanIssues = numConstrIssues + numConstrSummaryItems;
								if (spanIssues != 0)
									spanner = " rowspan=\"" + spanIssues + "\"";
								else
									spanner = "";
								outFilePW.println("<td" + spanner + ">constructor</td>");

								if (numConstrIssues != 0) {
									for (int issueNum = 0; issueNum < numConstrIssues; issueNum++) {
										if (issueNum != 0)
											outFilePW.println("						<tr>");
										outFilePW.print("							<td class=\"problem\"><i class=\"icon-remove\"></i> " +
											getMemberIssueSummaryItemByNum(ReportItem.CONSTRUCTOR, constructorName, ReportIssueType.ISSUE, issueNum));
										if (disputableReport)
											outFilePW.print(createDisputeButton("disputeButton" + getNextButtonNum(), reportName,
												getMemberIssueSummaryItemByNum(ReportItem.CONSTRUCTOR, constructorName, ReportIssueType.ISSUE, issueNum)));
										outFilePW.println("</td></tr>\n");
									}
								}

								if (numConstrSummaryItems != 0) {
									for (int issueNum = 0; issueNum < numConstrSummaryItems; issueNum++) {
										if (issueNum != 0 || (issueNum == 0 && numConstrIssues > 0))
											outFilePW.println("						<tr>");
										outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> " +
												getMemberIssueSummaryItemByNum(ReportItem.CONSTRUCTOR, constructorName, ReportIssueType.SUMMARY, issueNum) + "</td></tr>\n");
									}
								}

								if (numConstrIssues == 0 && numConstrSummaryItems == 0)
									outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>\n");

								String [] paramNames;

								// Constructor @throws
								if (getThrowsAnalyzed()) {
									int numIssues = getNumberOfConstructorIssues(constructorName, "throws");
									spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";
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

														outFilePW.print("<td class=\"problem\"><i class=\"icon-remove\"></i> throws <span class=\"code\">");
														outFilePW.print(paramNames[paramNum] + "</span>: " + paramIssues[issueNum]);
														if (disputableReport)
															outFilePW.print(createDisputeButton("disputeButton" + getNextButtonNum(), reportName, paramIssues[issueNum]));
														outFilePW.println("</td></tr>\n");
													}
												else
													outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>\n");
											} else {
												if (paramNum != 0)
													outFilePW.print("						<tr>");
												else
													outFilePW.print("							");
												outFilePW.println("<td class=\"ok\"><i class=\"icon-ok\"></i> throws: <span class=\"code\">" + paramNames[paramNum] + "</span>: No problems detected.</td></tr>\n");
											}
										}
									} else
										outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> Detected no @throws tags to analyze.</td></tr>\n");
								}

								// Constructor @parameters
								if (getParamsAnalyzed()) {
									int numIssues = getNumberOfConstructorIssues(constructorName, "parameters");
									spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";
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
														outFilePW.print("<td class=\"problem\"><i class=\"icon-remove\"></i> parameter <span class=\"code\">");
														outFilePW.print(paramNames[paramNum] + "</span>: " + paramIssues[issueNum]);
														if (disputableReport)
															outFilePW.print(createDisputeButton("disputeButton" + getNextButtonNum(), reportName, paramIssues[issueNum]));
														outFilePW.println("</td></tr>\n");
													}
												else
													outFilePW.println("<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>\n");
											} else {
												if (paramNum != 0)
													outFilePW.print("						<tr>");
												else
													outFilePW.print("							");
												outFilePW.println("<td class=\"ok\"><i class=\"icon-ok\"></i> parameter <span class=\"code\">" + paramNames[paramNum] + "</span>: No problems detected.</td></tr>\n");
											}
										}
									} else
										outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> Detected no @param tags to analyze.</td></tr>\n");
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
								if (spanIssues != 0)
									spanner = " rowspan=\"" + spanIssues + "\"";
								else
									spanner = "";
								outFilePW.print("						<tr><td" + spanner + "><span class=\"code\">" + methodName + "</span></td>");

								int numMethodIssues = getNumberOfAnalysisIssues(ReportItem.METHOD, methodName);
								int numMethodSummaryItems = getNumberOfSummaryItems(ReportItem.METHOD, methodName);
								spanIssues = numMethodIssues + numMethodSummaryItems;
								if (spanIssues != 0)
									spanner = " rowspan=\"" + spanIssues + "\"";
								else
									spanner = "";
								outFilePW.println("<td" + spanner + ">method</td>");

								if (numMethodIssues != 0) {
									for (int issueNum = 0; issueNum < numMethodIssues; issueNum++) {
										if (issueNum != 0)
											outFilePW.println("						<tr>");
										outFilePW.print("							<td class=\"problem\"><i class=\"icon-remove\"></i> " +
											getMemberIssueSummaryItemByNum(ReportItem.METHOD, methodName, ReportIssueType.ISSUE, issueNum));
										if (disputableReport)
											outFilePW.print(createDisputeButton("disputeButton" + getNextButtonNum(), reportName, getMemberIssueSummaryItemByNum(ReportItem.METHOD, methodName, ReportIssueType.ISSUE, issueNum)));
										outFilePW.println("</td></tr>\n");
									}
								}

								if (numMethodSummaryItems != 0) {
									for (int issueNum = 0; issueNum < numMethodSummaryItems; issueNum++) {
										if (issueNum != 0 || (issueNum == 0 && numMethodIssues > 0))
											outFilePW.println("						<tr>");
										outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> " +
												getMemberIssueSummaryItemByNum(ReportItem.METHOD, methodName, ReportIssueType.SUMMARY, issueNum) + "</td></tr>\n");
									}
								}

								if (numMethodIssues == 0 && numMethodSummaryItems == 0)
									outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>\n");

								String [] paramNames;

								// Method @throws
								if (getThrowsAnalyzed()) {
									int numIssues = getNumberOfMethodIssues(methodName, "throws");
									spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";
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

														outFilePW.print("							<td class=\"problem\"><i class=\"icon-remove\"></i> throws <span class=\"code\">");
														outFilePW.print(paramNames[paramNum] + "</span>: " + paramIssues[issueNum]);
														if (disputableReport)
															outFilePW.println(createDisputeButton("disputeButton" + getNextButtonNum(), reportName, paramIssues[issueNum]));
														outFilePW.println("</td></tr>\n");
													}
												else
													outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>\n");
											} else {
												if (paramNum != 0)
													outFilePW.print("						<tr>");
												else
													outFilePW.print("							");
												outFilePW.println("<td class=\"ok\"><i class=\"icon-ok\"></i>throws: <span class=\"code\">" + paramNames[paramNum] + "</span>:  No problems detected.</td></tr>\n");
											}
										}
									} else
										outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> Detected no @throws tags to analyze</td></tr>\n");
								}

								// Method @parameters
								if (getParamsAnalyzed()) {
									int numIssues = getNumberOfMethodIssues(methodName, "parameters");
									spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";
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

														outFilePW.print("							<td class=\"problem\"><i class=\"icon-remove\"></i> parameter <span class=\"code\">");
														outFilePW.print(paramNames[paramNum] + "</span>: " + paramIssues[issueNum]);
														if (disputableReport)
															outFilePW.print(createDisputeButton("disputeButton" + getNextButtonNum(), reportName, paramIssues[issueNum]));
														outFilePW.println("</td></tr>\n");
													}
												else
													outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>\n");
											} else {
												if (paramNum != 0)
													outFilePW.print("						<tr>");
												else
													outFilePW.print("							");
												outFilePW.println("<td class=\"ok\"><i class=\"icon-ok\"></i> parameter <span class=\"code\">" + paramNames[paramNum] + "</span>: No problems detected.</td></tr>\n");
											}
										}
									} else
										outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> Detected no @params tags to analyze.</td></tr>\n");
								}

								// Method @returns
								if (getReturnsAnalyzed()) {
									int numIssues = getNumberOfMethodIssues(methodName, "returns");
									spanner = (numIssues != 0) ? " rowspan=\"" + numIssues + "\"" : "";
									outFilePW.println("						<tr><td" + spanner + ">method <span class=\"code\">@return</span></td>");
									if (numIssues != 0)
										for (int issueNum = 0; issueNum < numIssues; issueNum++) {
											if (issueNum != 0)
												outFilePW.print("						<tr>");
											else
												outFilePW.print("							");
											outFilePW.print("<td class=\"problem\"><i class=\"icon-remove\"></i> " + getMethodIssueByNum(methodName, "returns", issueNum));
											if (disputableReport)
												outFilePW.print(createDisputeButton("disputeButton" + getNextButtonNum(), reportName, getMethodIssueByNum(methodName, "returns", issueNum)));
											outFilePW.print("</td></tr>\n");
										}
									else
										outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>\n");
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
									int numFieldSummaryItems = getNumberOfSummaryItems(ReportItem.FIELD, fieldName);
									spanIssues = numFieldIssues + numFieldSummaryItems;
									if (spanIssues != 0)
										spanner = " rowspan=\"" + spanIssues + "\"";
									else
										spanner = "";
									outFilePW.println("						<tr><td" + spanner + "><span class=\"code\">" + fieldName + "</span></td><td" + spanner + ">field</td>");

									if (numFieldIssues != 0) {
										for (int issueNum = 0; issueNum < numFieldIssues; issueNum++) {
											if (issueNum != 0)
												outFilePW.print("						<tr>");
											else
												outFilePW.print("							");
											outFilePW.print("<td class=\"problem\"><i class=\"icon-remove\"></i> " +
													getMemberIssueSummaryItemByNum(ReportItem.FIELD, fieldName, ReportIssueType.ISSUE, issueNum));
											if (disputableReport)
												outFilePW.print(createDisputeButton("disputeButton" + getNextButtonNum(), reportName, getMemberIssueSummaryItemByNum(ReportItem.FIELD, fieldName, ReportIssueType.ISSUE, issueNum)));
											outFilePW.println("</td></tr>\n");
										}
									}

									if (numFieldSummaryItems != 0) {
										for (int issueNum = 0; issueNum < numFieldSummaryItems; issueNum++) {
											if (issueNum != 0 || (issueNum == 0 && numFieldIssues > 0))
												outFilePW.print("						<tr>");
											else
												outFilePW.print("							");
											outFilePW.println("<td class=\"ok\"><i class=\"icon-ok\"></i> " +
													getMemberIssueSummaryItemByNum(ReportItem.FIELD, fieldName, ReportIssueType.SUMMARY, issueNum) + "</td></tr>\n");
										}
									}

									if (numFieldIssues == 0 && numFieldSummaryItems == 0)
										outFilePW.println("							<td class=\"ok\"><i class=\"icon-ok\"></i> No problems detected.</td></tr>\n");
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
			outFilePW.close();
		}
		catch (IOException e) {
			logger.error(e);

		} catch (JSONException je) {
			logger.error(je);
		}

		finally {
			logger.exit();
		}
	}
}
