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
package org.comtor.authserv;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import com.amazonaws.services.securitytoken.model.*;

import com.fasterxml.jackson.databind.*;

/**
 * This Java servlet provides authentication for an API client to COMTOR. Upon request, the submitted
 * API key is checked for valid/invalid state. If the key is invalid, a JSON string is returned to the client
 * with the key echoed and the valid name/value pair set to invalid. If the key is valid, the same is returned 
 * (valid name/value paid is set to valid) and three additional values are set: the AWS access key, secret key,
 * and a session token. All of these are required for the client to connect to AWS to submit their 'job' for 
 * processing.
 *
 * @author Peter DePasquale
 */
public class APIAuth extends HttpServlet {	   
	/**
	 * Handles the get request on this URI
	 *
	 * @param request the HTTP request object
	 * @param response the HTTP reponse object
	 *
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AWSServices.init();

		String submittedKey = request.getParameter("apikey");

		if (submittedKey != null) {
			boolean validKey = AWSServices.checkAPIKey(submittedKey);
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");

			Map<String, String> responseStruct = new HashMap<String, String>();
			responseStruct.put("apikey", submittedKey);
			responseStruct.put("valid", Boolean.toString(validKey));
			if (validKey) {
				Credentials creds = AWSServices.getSessionToken();
				responseStruct.put("accessKey", creds.getAccessKeyId());
				responseStruct.put("secretKey", creds.getSecretAccessKey());
				responseStruct.put("sessionToken", creds.getSessionToken());
			}

			ObjectMapper mapper = new ObjectMapper();
			String userResponse = mapper.writeValueAsString(responseStruct);
			out.println(userResponse);
		}
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
