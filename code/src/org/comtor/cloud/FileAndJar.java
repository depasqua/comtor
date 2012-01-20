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

import org.comtor.drivers.*;
import org.comtor.analyzers.*;

@SuppressWarnings("serial")
public class FileAndJar extends HttpServlet {
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
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		// Get Session ID
		HttpSession session = request.getSession();
		String session_id = session.getId();

		// Create Directory Based on Unique Session ID
		File session_fileDir = new File(destinationDir + java.io.File.separator + session_id);
		session_fileDir.mkdir();

		// Set the size threshold (1 MB), above which content will be stored on disk.
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		fileItemFactory.setSizeThreshold(1 * 1024 * 1024);

		// Set the temporary directory to store the uploaded files of size above threshold.
		fileItemFactory.setRepository(tmpDir);
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		String pathToFile = new String();
		try {
			// Handle FORM Request from user upload
			List items = uploadHandler.parseRequest(request);
			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();

				if (item.isFormField()) {
					; // TBD
				} 
				else {
					// Obtain the root path of the servlet, concatenate with destination location
					pathToFile = getServletContext().getRealPath("/") + "files" +
						java.io.File.separator + session_id; 
					
					// Obtain the uploaded filename and write the file to the destination location
					File file = new File(pathToFile + java.io.File.separator + item.getName());
					item.write(file);
					
					// Unjar the uploaded jar file, then start the processing.
					getContents(file, pathToFile);
					ComtorStandAlone.setMode(Mode.CLOUD);
					Comtor.start(pathToFile);
				}  
			}
			// Show the report
			try {
				File reportFile = new File(pathToFile + java.io.File.separator + "comtorReport.txt");
				String reportURLString = AWSServices.storeReportS3(reportFile, session_id).toString();
				// Rewrite protocol of URL for report to eliminate https (certificate warnings)
				if (reportURLString.startsWith("https"))
					reportURLString = reportURLString.replaceFirst("https", "http");
				String requestURL = request.getRequestURL().toString();
				String url = requestURL.substring(0, requestURL.lastIndexOf("/"));
				
				// Start of report
				out.println("<html><head/><body>");
				out.println("<a href=\"" + url + "\">Return to home</a>&nbsp;&nbsp;");
				out.println("<a href=\"" + reportURLString + "\">Report URL</a><br/><hr/>");

				Scanner scan = new Scanner(reportFile);
				out.println("<pre>");
				while (scan.hasNextLine())
					out.println(scan.nextLine());
				out.println("</pre><hr/>");

				out.println("<a href=\"" + url + "\">Return to home</a>&nbsp;&nbsp;");
				out.println("<a href=\"" + reportURLString + "\">Report URL</a><br/>");
				out.println("</body></html>");
				
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
	private void getContents(File file, String path) {
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
			System.err.println("Error encountered while unjarring");
		}
	}
}