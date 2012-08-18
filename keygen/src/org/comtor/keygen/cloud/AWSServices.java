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
package org.comtor.keygen.cloud;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.mail.*;
import java.security.*;
import java.math.BigInteger;
import javax.mail.internet.*;
import javax.servlet.http.*;

import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.services.dynamodb.model.*;
import com.amazonaws.services.dynamodb.*;
import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;

/**
 * This class helps to connect with DynamoDB and SES to perform the following:
 * 
 * - Query the specified table for the hash key value. Return boolean value whether or not it exists
 * - Create Item to put in DynamoDB Table
 * - Put Item in DynamoDB if query returns FALSE
 * - MD5 user's e-mail to create API Key
 * - email that user various 
 * 
 * @author Kevin Coughlin
 */
public class AWSServices {
	private static PropertiesCredentials credentials = null;
	private static AmazonDynamoDBAsyncClient dynamoDB = null;
	private static AmazonSimpleEmailService ses = null; 
	private static String tableName = "org.comtor.apikeys";		// DynamoDB Table to be used
	
	/**
	 * Initializer method to read the credentials for AWS and to set up the necessary query and database objects
	 * prior to first use.
	 */
	public static void init() {
		try {
			credentials = new PropertiesCredentials(new File("AwsCredentials.properties"));
			dynamoDB = new AmazonDynamoDBAsyncClient(credentials);
			ses = new AmazonSimpleEmailServiceClient(credentials);

		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			System.err.println(fnfe);

		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.err.println(ioe);
		}
	}

	/**
	 * Query DynamoDB Table specified in static String variable 'tableName'
	 * This method returns boolean true or false if it finds more than one
	 * result with the hash key value.
	 * 
	 * @param apiKey	The MD5'ed string of the user's email that is the hash key value for this particular table
	 * @param email		The user's email that is requesting a key
	 * @return			Return TRUE if Key already exists, else return FALSE
	 */
	public static boolean query(String apiKey, String email) {
		// Initialize query request object
		QueryRequest queryRequest = new QueryRequest();
		
		// Boolean to contain whether or not query returned results
		boolean exists = false;
		
		// Provide the query request the tablename and apiKey to check against, and return the count of matching items
		queryRequest
			.withTableName(tableName)
			.withHashKeyValue(new AttributeValue().withS(apiKey))
			.setCount(true);
		QueryResult result = dynamoDB.query(queryRequest);
		
		// There are matching items in table
		if (result.getCount() > 0)
			exists = true;
		else
			// There are not matching items in table
			exists = false;

		return exists;
	}

	/**
	 * Create and put item in DynamoDB Table
	 * 
	 * @param apiKey	The MD5'ed string of the user's email that is the hash key value for this particular table
	 * @param email		The user's email that is requesting a key
	 * @param ipAddress	User's IP Address for metrics
	 * @param hostname	User's ISP Host for metrics
	 * 
	 * @return			Return successful = TRUE if DynamoDB request is successful, else FALSE
	 */
	public static boolean putKey(String apiKey, String email, String ipAddress, String hostname) {
		 // Boolean to contain whether or not put request was successful
		 boolean successful = false;
		 
		 // If Query contains results (Query = True)
		 if (query(apiKey, email))
			 successful = false;

		 // Else there is no matching email, therefore create key
		 else {
			// User item contains API Key (MD5), Email address, IP address, and host name
			Map<String, AttributeValue> item = newItem(apiKey, email, ipAddress, hostname);

			// Create AWS DynamoDB put item request with tablename and item created, put in DB
			PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
			dynamoDB.putItem(putItemRequest);
			successful = true;
		 }
		 return successful;	
	}

	/**
	 * Create API User Object for storage in DynamoDB
	 * 
	 * @param apiKey The MD5'ed string of the user's email that is the hash key value for this particular table
	 * @param email The user's email that is requesting a key
	 * @param ipAddress User's IP Address for metrics
	 * @param hostname User's ISP Host for metrics
	 * @return Returns the created user data object
	 */
	private static Map<String, AttributeValue> newItem(String apikey, String email, String ipAddress, String hostname) {
		// Get date and time of put request
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String date = dateFormat.format(cal.getTime());
		
		// Create item with parameters listed above
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("apikey", new AttributeValue(apikey));
		item.put("email", new AttributeValue(email));
		item.put("date", new AttributeValue(date));
		item.put("ip", new AttributeValue(ipAddress));
		item.put("host", new AttributeValue(hostname));
		
		return item;
	}

	/**
	 * Convert input to MD5
	 * Currently used for e-mail to API key
	 * 
	 * @param input String to be MD5'ed
	 * @return The MD5 result
	 */
	public static String md5(String input) {
		// String to contain MD5 of input
		String md5 = null;
		
		// If string is null then return null
		if (input == null) return null;

		try {
			//Create MessageDigest object for MD5
			MessageDigest digest = MessageDigest.getInstance("MD5");
			
			// Update input string in message digest
			digest.update(input.getBytes(), 0, input.length());
			
			// Converts message digest value in base 16 (hex)
			md5 = new BigInteger(1, digest.digest()).toString(16);
		}

		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return md5;
	}

	/**
	 * Sends an email to the specified recipient. This method is called after
	 * the API key has been generated.
	 *
	 * @param toAddr the recipient's email address
	 * @param apiKey the API for the specified email address
	 * @param req the servlet handling the inbound HTTP request	 
	 */
	public static void sendKeyEmail(String toAddr, String apiKey, HttpServletRequest req) {
		String comtorEmail = "comtor@tcnj.edu";
		try {
			// Create a new Message
			com.amazonaws.services.simpleemail.model.Message msg =
				new com.amazonaws.services.simpleemail.model.Message().withSubject(new Content("COMTOR API Key Request"));
			SendEmailRequest request = new SendEmailRequest().withSource(comtorEmail);
			Destination dest = new Destination().withToAddresses(toAddr);
			request.withDestination(dest);

			String contextBase = "http://" + req.getServerName();
			String contextPath = req.getContextPath();

			if (req.getServerPort() != 80)
				contextBase += ":" + req.getServerPort();

			if (contextPath.equals(""))
				contextBase += "/";
			else
				contextBase += contextPath;

			String msgText = "<img style=\"margin-left: auto; margin-right: auto; display: block;\" " +
					"src=\"" + contextBase + "/images/comtor/comtorLogo.png\" width=\"160\" " +
					"alt=\"COMTOR logo\"/>";
			msgText += "<p>Thank you for your API key request for the " + toAddr + " email address. ";
			msgText += "Your COMTOR API key is: <b>" + apiKey + "</b></p>";
			msgText += "If you have any questions, you can always reach the COMTOR team at: " ;
			msgText += "<a href=\"mailto:" + comtorEmail + "\">" + comtorEmail + "</a>.";

			Content htmlContent = new Content().withData(msgText);
			Body body = new Body().withHtml(htmlContent);
			msg.setBody(body);
			
			request.setMessage(msg);
			ses.sendEmail(request);

		} catch (AmazonClientException e) {
			e.printStackTrace();
			System.err.println("Caught a AmazonClientException, which means that there was a "
					+ "problem sending your message to Amazon's E-mail Service check the "
					+ "stack trace for more information.");
		}
	}

	/**
	 * Resends an API key to the specified recipient. This method is called after
	 * the API key has been attempted to be generated and a key already exists for the 
	 * specified email address.
	 *
	 * @param toAddr the recipient's email address
	 * @param apiKey the API for the specified email address
	 * @param req the servlet handling the inbound HTTP request	 
	 */
	public static void resendKeyEmail(String toAddr, String apiKey, HttpServletRequest req) {
		String comtorEmail = "comtor@tcnj.edu";
		try {
			// Create a new Message
			com.amazonaws.services.simpleemail.model.Message msg =
				new com.amazonaws.services.simpleemail.model.Message().withSubject(new Content("COMTOR API Key Resend Request"));
			SendEmailRequest request = new SendEmailRequest().withSource(comtorEmail);
			Destination dest = new Destination().withToAddresses(toAddr);
			request.withDestination(dest);

			String contextBase = "http://" + req.getServerName();
			String contextPath = req.getContextPath();

			if (req.getServerPort() != 80)
				contextBase += ":" + req.getServerPort();

			if (contextPath.equals(""))
				contextBase += "/";
			else
				contextBase += contextPath;

			String msgText = "<img style=\"margin-left: auto; margin-right: auto; display: block;\" " +
					"src=\"" + contextBase + "/images/comtor/comtorLogo.png\" width=\"160\" " +
					"alt=\"COMTOR logo\"/>";
			msgText += "<p>Recently, you or someone requested a COMTOR API Key for the " + toAddr + " email address. ";
			msgText += "As a result, we are resending your COMTOR API key, which is: <b>" + apiKey + "</b></p>";
			msgText += "If you have any questions, you can always reach the COMTOR team at: " ;
			msgText += "<a href=\"mailto:" + comtorEmail + "\">" + comtorEmail + "</a>.";

			Content htmlContent = new Content().withData(msgText);
			Body body = new Body().withHtml(htmlContent);
			msg.setBody(body);
			
			request.setMessage(msg);
			ses.sendEmail(request);

		} catch (AmazonClientException e) {
			e.printStackTrace();
			System.err.println("Caught a AmazonClientException, which means that there was a "
					+ "problem sending your message to Amazon's E-mail Service check the "
					+ "stack trace for more information.");
		}
	}
}