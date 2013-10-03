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

import org.comtor.cloud.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.fasterxml.jackson.databind.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This Java servlet provides API-level key request functionality to various non-web interface clients.
 *
 * @author Peter DePasquale
 */
public class APIKeyRequest extends HttpServlet {
	private static Logger logger = LogManager.getFormatterLogger(APIKeyRequest.class.getName());

	/**
	 * Handles the post request on this servlet (if any) by passing it to the get method (above)
	 * @param request the HTTP request object
	 * @param response the HTTP response object
	 *
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.entry(request, response);
		AWSServices.init();
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");

		String ipAddress = request.getRemoteAddr(); 		// User's IP Address
		String hostname = request.getRemoteHost(); 			// User's host name
		String email = request.getParameter("email");
		logger.debug("Attempting API key creation for:\n\tIP:%s\n\thostname:%s\n\temail:%s", ipAddress, hostname, email);

		Map<String, String> responseStruct = new HashMap<String, String>();
		if (email != null && !email.equals("")) {
			if (AWSServices.addUser(email, ipAddress, hostname, request)){ 
				responseStruct.put("request_processed", "new user API key generated, notification email sent");
				logger.debug("New API key created successfully");
			}
			else{
				responseStruct.put("request_processed", "false; failed to add a new user: " + email);
				logger.warn("Failed to create API key (Failed to add user)");
			}

		} else{
			responseStruct.put("request_processed", "false; bad input");
			logger.debug("Failed to create API key (Bad input)");
		}

		ObjectMapper mapper = new ObjectMapper();
		String userResponse = mapper.writeValueAsString(responseStruct);
		out.println(userResponse);

		logger.exit(userResponse);
	}
}
