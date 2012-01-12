package org.comtor.cloud;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.comtor.drivers.*;

@SuppressWarnings("serial")
public class FileAndJar extends HttpServlet {
	private static final String TMP_DIR_PATH = "/tmp";
	private File tmpDir;
	private static final String DESTINATION_DIR_PATH = "/files";
	private File destinationDir;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		tmpDir = new File(TMP_DIR_PATH);
		if (!tmpDir.isDirectory()) {
			throw new ServletException(TMP_DIR_PATH + " is not a directory");
		}
		String realPath = getServletContext().getRealPath(DESTINATION_DIR_PATH);
		destinationDir = new File(realPath);
		if (!destinationDir.isDirectory()) {
			throw new ServletException(DESTINATION_DIR_PATH
					+ " is not a directory");
		}
	}

	@SuppressWarnings("rawtypes")
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		// Get Session ID
		HttpSession session = request.getSession();
		String session_id = session.getId();

		// Create Directory Based on Unique Session ID
		File session_fileDir = new File(destinationDir + java.io.File.separator
				+ session_id);
		session_fileDir.mkdir();

		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();

		// Set the size threshold (1 MB), above which content will be stored on
		// disk.
		fileItemFactory.setSizeThreshold(1 * 1024 * 1024);

		// Set the temporary directory to store the uploaded files of size above
		// threshold.
		fileItemFactory.setRepository(tmpDir);
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);

		try {
			// Handle FORM Request from User Upload
			List items = uploadHandler.parseRequest(request);
			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				// If is Form Field
				if (item.isFormField()) {
					//
					// Get email address
					//String email = item.getString();

					// Send email
					//SendJavaMail e = new SendJavaMail();
					//e.sendMail(email);
					//
				} 
				else {
					// Get root path of HelloWorld.java
					String path = getServletContext().getRealPath("/"); 
					
					// Root of Session ID Directory
					String pathToFile = path + "files" + java.io.File.separator
							+ session_id; 
					
					// Get Filename of Jar From Upload Form
					String jarName = item.getName(); 
					// Write file to the ultimate location. '/files/SESSION_ID/'
					File file = new File(pathToFile + java.io.File.separator + jarName);
					item.write(file);
					
					// UnJar the Uploaded Jar File
					ExtractJar extract = new ExtractJar();
					extract.getContents(file, pathToFile);
					
					// Call Comtor
					Comtor.start(pathToFile);	
				}
				out.close();
			}
		} catch (FileUploadException ex) {
			log("Error encountered while parsing the request", ex);
		} catch (Exception ex) {
			log("Error encountered while uploading file", ex);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}