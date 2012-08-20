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
package org.comtor.cloud;

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.util.jar.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.comtor.util.*;
import org.comtor.drivers.*;
import org.comtor.analyzers.*;

@SuppressWarnings("serial")
public class CloudUpload extends HttpServlet {
	private static final String DESTINATION_DIR_PATH = "/files";
	private File tmpDir;
	private File destinationDir;

	/**
	 * Called by the servlet container to indicate to a servlet that the servlet is being placed 
	 * into service. See 
	 * {@link http://docs.oracle.com/javaee/6/api/javax/servlet/GenericServlet.html#init(javax.servlet.ServletConfig)}.<p>
	 * This implementation stores the ServletConfig object it receives from the servlet container
	 * for later use. When overriding this form of the method, call super.init(config).
	 * 
	 * @param config the ServletConfig object that contains configutation information for this servlet
	 * @throws ServletException if an exception occurs that interrupts the servlet's normal operation
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletSupport.setTempDir((File) getServletContext().getAttribute("javax.servlet.context.tempdir"));
		destinationDir = new File(getServletContext().getRealPath(DESTINATION_DIR_PATH));
		if (!destinationDir.isDirectory())
			throw new ServletException(DESTINATION_DIR_PATH + " is not a directory.");
	}

	/**
	 * Called by the server (via the service method) to allow a servlet to handle a POST request.
	 * See {@link http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServlet.html#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String emailAddress = "";
		String requesterIPAddress = request.getRemoteAddr();
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		// Get Session ID
		HttpSession session = request.getSession();
		String sessionID = session.getId();

		// Create Directory Based on Unique Session ID
		File session_fileDir = new File(destinationDir + java.io.File.separator + sessionID);
		session_fileDir.mkdir();

		// Set the size threshold (1 MB), above which content will be stored on disk.
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		fileItemFactory.setSizeThreshold(1 * 1024 * 1024);

		// Set the temporary directory to store the uploaded files of size above threshold.
		fileItemFactory.setRepository(tmpDir);
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		String pathToFile = new String();

		try {
			// Obtain the root path of the servlet, concatenate with destination location
			pathToFile = getServletContext().getRealPath("/") + "files" +
						java.io.File.separator + sessionID;
			String docletFileName = pathToFile + java.io.File.separator + "docletList.properties";
			BufferedWriter fwrite = new BufferedWriter(new FileWriter(new File(docletFileName)));
			fwrite.write("doclet1 : org.comtor.analyzers.SpellCheck");
			fwrite.newLine();
			fwrite.write("doclet2 : org.comtor.analyzers.OffensiveWords");
			fwrite.newLine();
			fwrite.write("doclet3 : org.comtor.analyzers.CheckAuthor");
			fwrite.newLine();
			fwrite.write("doclet4 : org.comtor.analyzers.PercentageMethods");
			fwrite.newLine();
			fwrite.write("doclet5 : org.comtor.analyzers.CommentAverageRatio");
			fwrite.newLine();
			fwrite.write("doclet6 : org.comtor.analyzers.BasicInfo");
			fwrite.newLine();

			// Handle HTTP form request data
			Iterator itr = uploadHandler.parseRequest(request).iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();

				if (item.isFormField()) {
					if (item.getFieldName().equals("email"))
						emailAddress = new String(item.get());
				} else {
					// Obtain the uploaded filename and write the file to the destination location
					File file = new File(pathToFile + java.io.File.separator + item.getName());
					item.write(file);
					
					// Unjar the uploaded jar file
					extractJarFile(file, pathToFile);				
				}
			}
			// Close docletList.properties file
			fwrite.close();

			// Commence processing
			ComtorStandAlone.setMode(Mode.CLOUD);
			Comtor.start(pathToFile);

			// Store the report, shorten the report URL, and email the report to the user
			try {
				File reportFile = new File(pathToFile + java.io.File.separator + "comtorReport.txt");
				String reportURLString = AWSServices.storeReportS3(reportFile, sessionID).toString();

				// Rewrite protocol of URL for report to eliminate https (certificate warnings)
				if (reportURLString.startsWith("https"))
					reportURLString = reportURLString.replaceFirst("https", "http");
				
				// Shorten the url via Bitly and send email to user.
				reportURLString = BitlyServices.shortenUrl(reportURLString);

				// Construct body of email message and send it
				String contextBase = "http://" + request.getServerName();
				String contextPath = request.getContextPath();
				if (request.getServerPort() != 80)
					contextBase += ":" + request.getServerPort();

				if (contextPath.equals(""))
					contextBase += "/";
				else
					contextBase += contextPath;

				String msgText = "<img style=\"margin-left: auto; margin-right: auto; display: block;\" " +
						"src=\"" + contextBase + "/images/comtor/comtorLogo.png\" width=\"160\" " +
						"alt=\"COMTOR logo\"/>";
				msgText += "Thank you for your submission to the COMTOR system. Your report is "; 
				msgText += "now available for access/download at the following URL: " + reportURLString + ". ";
				msgText += "This link will remain active for a period of 5 days.\n\n";
				msgText += "You can reach the COMTOR team at comtor@tcnj.edu.";
				AWSServices.sendEmail(emailAddress, msgText);

//				String requestURL = request.getRequestURL().toString();
//				String home = requestURL.substring(0, requestURL.lastIndexOf("/"));

				// Record cloud usage
				GregorianCalendar now = new GregorianCalendar();
				AWSServices.storeCloudUse(requesterIPAddress, sessionID, reportURLString,
						emailAddress, now.getTime().toString());
				
				// Start of output returned
				out.println("<!DOCTYPE html>");
				out.println("<html lang=\"en\">");
				out.println("\t<head>");

				out.println("\t\t<meta http-equiv=\"refresh\" content=\"5;URL=" + contextBase + "\">");
				out.println("\t\t<title>/** COMTOR **/</title>");
				out.println("\t\t<link rel=\"stylesheet\" href=\"stylesheets/app.css\"/>");
				out.println("\t\t<link rel=\"stylesheet\" href=\"stylesheets/foundation.css\"/>");
				out.println("\t\t<link rel=\"stylesheet\" href=\"stylesheets/ie.css\"/>");

				// Read the analytics code for inclusion
				RequestDispatcher analyticsDispatcher = request.getRequestDispatcher("/fragments/googleAnalytics.js");
				analyticsDispatcher.include(request, response);

				out.println("\t\t<script type=\"text/javascript\" src=\"scripts/jquery.min.js\"></script>");
				out.println("\t\t<script type=\"text/javascript\" src=\"scripts/app.js\"></script>");
				out.println("\t\t<script type=\"text/javascript\" src=\"scripts/foundation.js\"></script>");
				out.println("\t\t<script type=\"text/javascript\" src=\"scripts/modernizr.foundation.js\"></script>");
				out.println("\t\t<script type=\"text/javascript\" src=\"scripts/validate.min.js\"></script>");
				out.println("\t</head>");

				// Read the upload acknowledgement code for inclusion
				RequestDispatcher uploadDispatcher = request.getRequestDispatcher("/fragments/uploadAcknowledge.html");
				uploadDispatcher.include(request, response);

				out.println("</html>");
			} catch (Exception ex) {
				System.err.println(ex);
			}
		} catch (FileUploadException ex) {
			System.err.println("Error encountered while parsing the request" + ex);
		} catch (Exception ex) {
			System.err.println("Error encountered while uploading file" + ex);
		}
	}

	/**
	 * Called by the server (via the service method) to allow a servlet to handle a GET request.
	 * See {@link http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServlet.html#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
		doPost(request, response);
	}
	
	/**
	 * Processes the contents of the uploaded jar file. Extracts the file to the specified path.
	 *
	 * @param file A reference to the File object to extract
	 * @param path A String object representing the path into which the file is extracted.
	 */
	@SuppressWarnings("rawtypes")
	public static void extractJarFile(File file, String path) {
		try {
			JarFile jar = new JarFile(file);
			Enumeration jarEnum = jar.entries();

			while (jarEnum.hasMoreElements()) {
				JarEntry entry = (JarEntry) jarEnum.nextElement();
				String entryName = entry.getName();
				File entryFile = new File(path, java.io.File.separator + entryName);
				
				// If the entry is a directory, create it, unless it's the META-INF
				if (!entryName.startsWith("META-INF"))
					if (entry.isDirectory())
						entryFile.mkdir();
					else {
						InputStream in = jar.getInputStream(entry); 
						OutputStream output = new FileOutputStream(entryFile);
	
						while (in.available() > 0)
							output.write(in.read());
	
						output.close();
						in.close();
					}
			}
		} catch (Exception ex) {
			System.err.println("Error encountered while unjarring.");
		}
	}
}