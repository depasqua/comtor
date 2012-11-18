/**
 *  Comment Mentor: A Comment Quality Assessment Tool
 *  Copyright (C) 2012 The College of New Jersey
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
package org.comtor.keygen;

import org.comtor.keygen.cloud.*;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

/**
 * This Java Servlet handles the API key creation request. The Servlet hashes the user-
 * entered e-mail address to create an API key and places key and user attributes into
 * AWS DynamoDB.
 * 
 * Servlet implementation class KeyGen
 *
 * @author Kevin Coughlin
 * @author Peter DePasquale
 */
public class KeyGen extends HttpServlet {	   
	/**
	 * Handles the get request on this URI
	 *
	 * @param request the HTTP request object
	 * @param response the HTTP reponse object
	 *
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		AWSServices.init();
		
		String email = request.getParameter("email"); 		// User E-mail
		String ipAddress = request.getRemoteAddr(); 		// User's IP Address
		String hostname = request.getRemoteHost(); 			// User's host name

		// Check the recaptcha
		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		reCaptcha.setPrivateKey("6LdGhdMSAAAAAC-weLKOjQk_WczD_1gLcmpwJZv1");

		String challenge = request.getParameter("recaptcha_challenge_field");
		String uresponse = request.getParameter("recaptcha_response_field");
		ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(ipAddress, challenge, uresponse);

		if (reCaptchaResponse.isValid() && email != null) {
			// Attempt to put user in DynamoDB
			boolean success = AWSServices.addUser(email, ipAddress, hostname);
			String apikey = AWSServices.getAPIKey(email);

			// Determine success of db insertion
			RequestDispatcher dispatcher = null;
			if (success) {
				// Send the user an email with the API key and forward to the response page.
				AWSServices.sendKeyEmail(email, apikey, request);
				dispatcher = getServletContext().getRequestDispatcher("/keyGenerated.jsp?email=" + email + "&apikey=" + apikey);

			} else {
				// Resend email with API key and forward to response page.
				AWSServices.resendKeyEmail(email, apikey, request);
				dispatcher = getServletContext().getRequestDispatcher("/duplicateEmail.jsp?email=" + email);
			}

			dispatcher.forward(request, response);

		} else {
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/badRecaptcha.jsp");
			dispatcher.forward(request, response);
		}

		out.close();
	}

	/**
	 * Handles the post request on this servlet (if any) by passing it to the get method (above)
	 * @param request the HTTP request object
	 * @param response the HTTP reponse object
	 *
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Handle POST same as GET method
		doGet(request, response);
	}
}
