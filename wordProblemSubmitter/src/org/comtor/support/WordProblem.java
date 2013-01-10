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
package org.comtor.support;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.amazonaws.*;
import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.auth.PropertiesCredentials;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class WordProblem extends HttpServlet {

	/**
	 * Called by the server (via the service method) to allow a servlet to handle a POST request.
	 * See {@link http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServlet.html#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 * 
	 * @author Peter J. DePasquale
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");

		String reportName = request.getParameter("reportName");
		String submittedWord = request.getParameter("submittedWord");

		if (reportName != null && submittedWord != null && reportName.length() > 0 && submittedWord.length() > 0) {
			boolean success = AWSServices.submitWord(reportName, submittedWord);

			if (success) {
				out.println(success);
			}
		} else
			out.println("There were problems with the submission: reportName='" + reportName + 
				"' and submittedWord='" + submittedWord + "'.");
	}

	/**
	 * Called by the server (via the service method) to allow a servlet to handle a GET request.
	 * See {@link http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServlet.html#doGet(
	 *       javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
		doPost(request, response);
	}
}

class AWSServices {
	private static Logger logger = LogManager.getLogger(org.comtor.support.AWSServices.class.getName());
	private static File awscreds = new File ("AwsCredentials.properties");

	/**
	 * Allows for the submisssion of a word by a user that the user feels is non spelled incorrectly or not offensive.
	 * This servlet permits the user to submit the problem via this service which will email it to the COMTOR team for 
	 * consideration of correction.
	 *
	 * @param reportName the name of the COMTOR report that generated the issue in question again the specified word
	 * @param word the word that the user feels is incorrect
	 * @return returns a true value if the email was sent successfully, false otherwise
	 * 
	 * @author Peter J. DePasquale
	 */
	public static boolean submitWord(String reportName, String word) {
		logger.entry();
		String comtorEmail = "comtor@tcnj.edu";
		String toAddress = "peter.depasquale@gmail.com";
		boolean result = false;

		try {
			PropertiesCredentials credentials = new PropertiesCredentials(awscreds);
			AmazonSimpleEmailService ses = new AmazonSimpleEmailServiceClient(credentials);
			
			// Create a new Message
			com.amazonaws.services.simpleemail.model.Message msg =
				new com.amazonaws.services.simpleemail.model.Message().withSubject(new Content("COMTOR Result Dispute"));
			SendEmailRequest request = new SendEmailRequest().withSource(comtorEmail);
			Destination dest = new Destination().withToAddresses(toAddress);
			request.withDestination(dest);

			Content htmlContent = new Content().withData("A user has submitted \"" + word +
				"\" as incorrectly identified in the \"" + reportName + "\" analysis report.");
			Body msgBody = new Body().withText(htmlContent);
			msg.setBody(msgBody);
			
			request.setMessage(msg);
			ses.sendEmail(request);
			result = true;

		} catch (AmazonClientException e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(baos));
			logger.error(baos.toString());
			logger.error("Caught a AmazonClientException, which means that there was a "
					+ "problem sending your message to Amazon's E-mail Service check the "
					+ "stack trace for more information.");
			
		} catch (IOException ieo) {
			logger.error(ieo);
		}
		logger.exit();
		return result;

	}
}