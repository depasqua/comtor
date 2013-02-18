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
 * This Java servlet provides 
 *
 * @author Peter DePasquale
 */
public class APIKeyRequest extends HttpServlet {	   
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
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");

		String ipAddress = request.getRemoteAddr(); 		// User's IP Address
		String hostname = request.getRemoteHost(); 			// User's host name
		String email = request.getParameter("email");

		if (email != null) {
			boolean success = AWSServices.addUser(email, ipAddress, hostname, request);
			Map<String, String> responseStruct = new HashMap<String, String>();
			responseStruct.put("request_processed", "true");

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
