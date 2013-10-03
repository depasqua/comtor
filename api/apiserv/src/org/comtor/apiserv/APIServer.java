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
package org.comtor.apiserv;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.io.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;

import com.fasterxml.jackson.databind.*;

import org.comtor.util.*;
import org.comtor.cloud.*;
import org.comtor.drivers.*;
import org.comtor.analyzers.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This Java servlet provides API-level server functionality.
 *
 * @author Peter DePasquale
 */
public class APIServer extends HttpServlet {	   
	private static final String DESTINATION_DIR_PATH = "/files";
	private static Logger logger = LogManager.getFormatterLogger(APIServer.class.getName());

	/**
	 * Handles the post request on this servlet (if any) by passing it to the get method (above)
	 * @param request the HTTP request object
	 * @param response the HTTP reponse object
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.entry(request, response);
		AWSServices.init();
		String requesterIPAddress = request.getRemoteAddr();
		String requestedResponseTypes = null;
		String requestedResponseFormats = null;
		String submittedAPIKey = null;
		String interfaceClient = null;
		String webcat_uuid = null;
		String webcat_institution = null;
		String fileName = null;
		long fileSize = 0;
		boolean extractedJar = false;
		InterfaceSystem clientType = InterfaceSystem.WWW;
		File destinationDir = new File(getServletContext().getRealPath(DESTINATION_DIR_PATH));
		if (!destinationDir.isDirectory()){
			destinationDir.mkdir();
			logger.debug("Upload directory missing. Creating directory at: %s", destinationDir);
		}

		PrintWriter out = response.getWriter();
		response.setContentType("application/json");

		Map<String, String> responseStruct = new HashMap<String, String>();
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		if (isMultipart) {
			// Get Session ID
			HttpSession session = request.getSession();
			String sessionID = session.getId();
			String emailAddress = null;

			// Create directory based on unique session ID
			File session_fileDir = new File(destinationDir + java.io.File.separator + sessionID);
			session_fileDir.mkdir();
			logger.debug("Creating session specific directory at: %s", session_fileDir);

			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			String pathToFile = destinationDir + java.io.File.separator + sessionID;
			
			// parse this request by the handler this gives us a list of items from the request
			try {
				int requiredItems = 0;
				List items = upload.parseRequest(request);
				upload.setSizeMax(10485760L); // approx 10MB file upload limit
				Iterator itr = items.iterator();

				while (itr.hasNext()) {
					FileItem item = (FileItem) itr.next();
				        
					if (item.isFormField()) {
						// Fetch the form items that are not part of the file upload (form fields)
						String fieldName = item.getFieldName();
						if (fieldName.equals("apikey")) {
							requiredItems++;
							submittedAPIKey = item.getString();
							logger.debug("Submitted apikey: %s", submittedAPIKey);
							if (submittedAPIKey != null && !submittedAPIKey.equals("") && AWSServices.checkAPIKey(submittedAPIKey))
								emailAddress = AWSServices.getEmailFromKey(submittedAPIKey);
							if (emailAddress == null)
								requiredItems--;

						} else if (fieldName.equals("response_type")) {
							// Possible supported valid values here are "email", "http"
							requiredItems++;
							requestedResponseTypes = item.getString();							
							if (requestedResponseTypes == null || (!requestedResponseTypes.contains("email") && !requestedResponseTypes.contains("http")))
								requiredItems--;
							logger.debug("Submitted response_type: %s", requestedResponseTypes);

						} else if (fieldName.equals("response_format")) {
							// Possible supported valid values here are "text", "html", "json" (and others later)
							requiredItems++;
							requestedResponseFormats = item.getString();
							if (requestedResponseFormats == null || (!requestedResponseFormats.contains("text") && !requestedResponseFormats.contains("html") &&
								!requestedResponseFormats.contains("json")))
								requiredItems--;
							logger.debug("Submitted response_format: " + requestedResponseFormats);

						} else if (fieldName.equals("webcat_uuid")) {
							// Supports WebCat passing us the UUID of the user 
							webcat_uuid = item.getString();							
							logger.debug("Submitted webcat_uuid: %s", webcat_uuid);

						} else if (fieldName.equals("webcat_institution")) {
							// Supports WebCat passing us the user's institution name
							webcat_institution = item.getString();							
							logger.debug("Submitted webcat_institution: %s", webcat_institution);

						} else if (fieldName.equals("interface_client")) {
							// Pass the interface client (Eclipse, WebCat, Netbeans, etc. to the API)
							requiredItems++;
							interfaceClient = item.getString();							
							if (interfaceClient.equals("webcat"))
								clientType = InterfaceSystem.WEBCAT;
							else if (interfaceClient.equals("netbeans"))
								clientType = InterfaceSystem.NETBEANS;
							else if (interfaceClient.equals("eclipse"))
								clientType = InterfaceSystem.ECLIPSE;
							else if (interfaceClient.equals("www"))
								clientType = InterfaceSystem.WWW;
							else 
								requiredItems--;
							logger.debug("Submitted interface_client: %s", interfaceClient);
						}

						// add new param violations_only (defaults to true) and only shows bad "issues", otherwise if set to false;
						// shows good results and violations in the output report.

					} else {
						// item.getName() returns whatever filename the client provides in the request, so we need to
						// strip out the path content, if present, and get just the name.
						fileName = item.getName();
						logger.debug("Attempting to process the file upload: %s", fileName);
						if (fileName != null & !fileName.equals("")) {
							// Write the uploaded file to the temp path (pathToFile) in the servlets context
							requiredItems++;
							fileName = FilenameUtils.getName(fileName);

							if (fileName != null ) {
								File uploadedFile = new File(pathToFile, fileName);
								if (uploadedFile == null)
									requiredItems--;
									//logger.debug("No file found to process"); is this an ERROR?
				
								item.write(uploadedFile);
								fileSize = item.getSize();
								extractedJar = extractJarFile(uploadedFile, pathToFile);
							}
						}
					}
				}

				// Only process if we have a valid set of parameters
				if (requiredItems == 5 && extractedJar) {
					if (!interfaceClient.equals("webcat")) {
						responseStruct.put("apikey", submittedAPIKey);
						responseStruct.put("interface_client", interfaceClient);
						responseStruct.put("email_from_apikey", emailAddress);
						responseStruct.put("response_type", requestedResponseTypes);
						responseStruct.put("response_format", requestedResponseFormats);
						responseStruct.put("uploaded filename", fileName);
						responseStruct.put("uploaded file size", fileSize + "");
					}

					// Create the list of analysis modules to execute
					String docletFileName = pathToFile + java.io.File.separator + "docletList.properties";
					BufferedWriter fwrite = new BufferedWriter(new FileWriter(new File(docletFileName)));
					fwrite.write("doclet1 : org.comtor.analyzers.SpellCheck");
					logging.debug("Adding 'SpellCheck' to list of analysis modules");
					fwrite.newLine();
					fwrite.write("doclet2 : org.comtor.analyzers.OffensiveWords");
					logging.debug("Adding 'OffensiveWords' to list of analysis modules");
					fwrite.newLine();
					fwrite.write("doclet3 : org.comtor.analyzers.CheckAuthor");
					logging.debug("Adding 'CheckAuthor' to list of analysis modules");
					fwrite.newLine();
					fwrite.write("doclet4 : org.comtor.analyzers.PercentageMethods");
					logging.debug("Adding 'PercentageMethods' to list of analysis modules");
					fwrite.newLine();
					fwrite.write("doclet5 : org.comtor.analyzers.CommentAvgRatio");
					logging.debug("Adding 'CommentAvgRatio' to list of analysis modules");
					fwrite.newLine();
					fwrite.close();

					// Commence processing
					if (clientType == InterfaceSystem.WEBCAT){
						ComtorStandAlone.setMode(Mode.WEBCAT);
						logger.debug("Execution mode set to: WEBCAT.");
					}
					else{
						ComtorStandAlone.setMode(Mode.API);
						logger.debug("Execution mode set to: API.");
					}

					ComtorStandAlone.setTempDir((File) getServletContext().getAttribute("javax.servlet.context.tempdir"));
					logger.debug("Temp dir set to: %s", ComtorStandAlone.getTempDir());
					Comtor.start(pathToFile);

					// Store the report, shorten the report URL, and email the report to the user
					logger.debug("Output files produced to: %s", pathToFile + java.io.File.separator);
					File textReportFile = new File(pathToFile + java.io.File.separator + "comtorReport.txt");
					String textReportURLString = AWSServices.storeReportS3(textReportFile, sessionID).toString();
					File htmlReportFile = new File(pathToFile + java.io.File.separator + "comtorReport.html");
					String htmlReportURLString = AWSServices.storeReportS3(htmlReportFile, sessionID).toString();

					// Rewrite protocol of URL for report to eliminate https (certificate warnings)
					if (textReportURLString.startsWith("https"))
						textReportURLString = textReportURLString.replaceFirst("https", "http");
					if (htmlReportURLString.startsWith("https"))
						htmlReportURLString = htmlReportURLString.replaceFirst("https", "http");
					
					// Shorten the url via Bitly and send email to user.
					textReportURLString = BitlyServices.shortenUrl(textReportURLString);
					htmlReportURLString = BitlyServices.shortenUrl(htmlReportURLString);
					
					// Construct body of email message and send it
					String contextBase = "http://" + request.getServerName();
					String contextPath = request.getContextPath();

					if (request.getServerPort() != 80)
						contextBase += ":" + request.getServerPort();

					if (contextPath.equals(""))
						contextBase += "/";
					else
						contextBase += contextPath;

					if (requestedResponseTypes.contains("email")) {
						String msgText = "<img style=\"margin-left: auto; margin-right: auto; display: block;\" " +
								"src=\"" + contextBase + "/images/comtor/comtorLogo.png\" width=\"160\" " +
								"alt=\"COMTOR logo\"/>";
						msgText += "Thank you for your submission to the COMTOR system. Your report is "; 
						msgText += "now available for access/download as follows at the following URLs:\n<ul>";

						if (requestedResponseFormats.contains("html"))
							msgText += "<li>HTML version: <a href=\"" + htmlReportURLString + "\">" + htmlReportURLString + "</a></li>";

						if (requestedResponseFormats.contains("text"))
							msgText += "<li>Text version: <a href=\"" + textReportURLString + "\">" + textReportURLString + "</a></li>";

						msgText += "</ul>These links will remain active for a period of 5 days.\n\n";
						msgText += "If you have any questions about this system, you can reach the COMTOR team at comtor@tcnj.edu.";
						logger.debug("Send email to: {} with the following message: {}", emailAddress, msgText);
						AWSServices.sendEmail(emailAddress, msgText);
					}

					String htmlReportContents = null;
					if (requestedResponseTypes.contains("http")) {
						if (requestedResponseFormats.contains("html")) {
							htmlReportContents = FileUtils.readFileToString(htmlReportFile);
							if (!interfaceClient.equals("webcat")) {
								responseStruct.put("html_report_url", htmlReportURLString);
								responseStruct.put("html_report_contents", htmlReportContents);
							}
						}

						if (requestedResponseFormats.contains("text")) {
							responseStruct.put("text_report_url", textReportURLString);
							String textReportContents = FileUtils.readFileToString(textReportFile);
							if (!interfaceClient.equals("webcat"))
								responseStruct.put("text_report_contents", textReportContents);
						} 

						if (requestedResponseFormats.contains("json")) {
							File jsonReportFile = new File(pathToFile + java.io.File.separator + "jsonOut.txt");
							String jsonReportContents = FileUtils.readFileToString(jsonReportFile);
							if (!interfaceClient.equals("webcat"))
								responseStruct.put("json_report_contents", jsonReportContents);
						}
					}

					// Record cloud usage, but not for the dev version.
					if (!contextPath.endsWith("Dev")) {
						GregorianCalendar now = new GregorianCalendar();
						AWSServices.logCloudUse(requesterIPAddress, emailAddress, now.getTimeInMillis(), clientType);
						File jsonFile = new File(pathToFile + java.io.File.separator + "jsonOut.txt");
						AWSServices.logCOMTORReport(jsonFile, emailAddress + "-" + now.getTimeInMillis());
					}

					String userResponse = null;
					if (!interfaceClient.equals("webcat")) {
						ObjectMapper mapper = new ObjectMapper();
						userResponse = mapper.writeValueAsString(responseStruct);
					} else 
						userResponse = htmlReportContents;

					out.println(userResponse);

				} else {
					logger.debug("Terminating processing with invalid parameter count: %s", requiredItems);
				}

			} catch (FileUploadException fue) {
				logger.catching(fue);

			} catch (Exception e) {
				logger.catching(e);
			}
		}
		logger.exit();
	}

	/**
	 * Processes the contents of the uploaded jar file. Extracts the file to the specified path.
	 *
	 * @param file A reference to the File object to extract
	 * @param path A String object representing the path into which the file is extracted.
	 * @return a true value if the file was successfully extracted, false otherwise
	 */
	@SuppressWarnings("rawtypes")
	private static boolean extractJarFile(File file, String path) {
		logger.entry(file, path);
		boolean result = false;

		try {
			JarFile jar = new JarFile(file);
			Enumeration jarEnum = jar.entries();

			while (jarEnum.hasMoreElements()) {
				JarEntry entry = (JarEntry) jarEnum.nextElement();
				String entryName = entry.getName();

				// If the entry is a directory, create it, unless it's the META-INF, source code files
				// are handled below...
				if (!entryName.startsWith("META-INF"))
					if (entry.isDirectory()) {
						File entryFile = new File(path, java.io.File.separator + entryName);
						entryFile.mkdir();

					} else {
						File entryFile = new File(path, java.io.File.separator + entryName);
						if (entryName.endsWith(".java")) {
							// Reconstruct the dir path, if present
							// Some jar files may not include entries for directories in the contents,
							// so we need to ensure that the leading path is reconstructed prior to
							// file extraction.
							Scanner pathScan = new Scanner(entryName);
							pathScan.useDelimiter("/");
							int tokenCount = 0;
							while (pathScan.hasNext()) {
								pathScan.next();
								tokenCount++;
							}
							pathScan = new Scanner(entryName);
							pathScan.useDelimiter("/");

							// Make directories (nested) for all but the last token in the entryName.
							File newDir = new File(path);
							if (!newDir.exists()) {
								newDir.mkdir();
							}

							String newPath = null;
							for (int index=0; index < tokenCount-1; index++) {
								newPath = newDir.getPath();
								newDir = new File(newPath, java.io.File.separator + pathScan.next());
								if (!newDir.exists()) {
									newDir.mkdir();
								}
							}

							// Write the extracted file to the filesystem
							InputStream in = jar.getInputStream(entry); 
							OutputStream output = new FileOutputStream(entryFile);
		
							while (in.available() > 0)
								output.write(in.read());
		
							output.close();
							in.close();
							result = true;
						}
					}
			}
		} catch (Exception ex) {
			logger.catching(ex);
			result = false;
		}

		return logger.exit(result);
	}
}