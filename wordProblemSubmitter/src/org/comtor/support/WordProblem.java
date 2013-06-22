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
package org.comtor.support;

import org.comtor.cloud.*;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.amazonaws.*;
import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.auth.PropertiesCredentials;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class provides support for user-submitted declaration of false positives on spelling and offensive word results for
 * the project reports. This can be adopted for other modules as well.
 * @author Peter DePasquale
 */
public class WordProblem extends HttpServlet {
	private static Logger logger = LogManager.getLogger(WordProblem.class.getName());

	/**
	 * Called by the server (via the service method) to allow a servlet to handle a POST request.
	 * See {@link http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServlet.html#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 * 
	 * @author Peter J. DePasquale
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.entry();	
		AWSServices.init();
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");

		String reportName = request.getParameter("reportName");
		String submittedWord = request.getParameter("submittedWord");

		if (reportName != null && submittedWord != null && reportName.length() > 0 && submittedWord.length() > 0) {
			boolean success = AWSServices.submitDisputedWord(reportName, submittedWord);

			if (success)
				out.println(success);

		} else
			out.println("There were problems with the submission: reportName='" + reportName + 
				"' and submittedWord='" + submittedWord + "'.");
		logger.exit();
	}

	/**
	 * Called by the server (via the service method) to allow a servlet to handle a GET request.
	 * See {@link http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServlet.html#doGet(
	 *       javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}