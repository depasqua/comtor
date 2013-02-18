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
import java.text.*;
import java.util.*;
import java.security.*;
import java.math.BigInteger;

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.http.*;

import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.services.dynamodb.model.*;
import com.amazonaws.services.dynamodb.*;
import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.securitytoken.*;
import com.amazonaws.services.securitytoken.model.*;
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
 * @author Peter DePasquale
 */
public class AWSServices {
	private static PropertiesCredentials credentials = null;
	private static AmazonDynamoDBAsyncClient dynamoDB = null;
	private static AmazonSimpleEmailService ses = null;
	private static AWSSecurityTokenServiceClient sts = null;
	private static String ownerTable = "org.comtor.keyOwners";
	private static String keyTable = "org.comtor.keyDetails";
	
	/**
	 * Initializer method to read the credentials for AWS and to set up the necessary query and database objects
	 * prior to first use.
	 */
	public static void init() {
		try {
			credentials = new PropertiesCredentials(new File("APICredentials.properties"));
			dynamoDB = new AmazonDynamoDBAsyncClient(credentials);
			ses = new AmazonSimpleEmailServiceClient(credentials);
			sts = new AWSSecurityTokenServiceClient(credentials);

		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			System.err.println(fnfe);

		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.err.println(ioe);
		}
	}

	/**
	 * Queries the db to determine if the specified API key is valid and active.
	 *
	 * @param apikey The string value of the API key
	 * @return A true value if the key is valid and active, false otherwise
	 */
	public static boolean checkAPIKey(String apiKey) {
		boolean result = false;

		// Initialize query request object
		GetItemRequest getItemRequest = new GetItemRequest()
			.withTableName(keyTable)
			.withKey(new com.amazonaws.services.dynamodb.model.Key(new AttributeValue().withS(apiKey)))
			.withAttributesToGet("active");

		GetItemResult queryResult = dynamoDB.getItem(getItemRequest);
		if (queryResult.getItem() != null) {
			AttributeValue keyActiveValue = queryResult.getItem().get("active");
			result = Boolean.valueOf(keyActiveValue.getS());
		}

		return result;
	}

	/**
	 * Obtains and returns a set of temporary AWS API credentials. This is used for external API clients,
	 * to COMTOR so that they can upload their work .jar files to S3 and to place a message on SQS for 
	 * the API "server" to process the uploaded jar file.
	 * 
	 * @return a set of AWS credentials
	 */
	public static Credentials getSessionToken() {
		GetSessionTokenRequest request = new GetSessionTokenRequest()
			.withDurationSeconds(900);

		GetSessionTokenResult requestResult = sts.getSessionToken(request);
		return requestResult.getCredentials();
	}

	/**
	 * Queries the db to determine if the given email corresponds to an API key. The state of the key
	 * active/inactive is not considered.
	 * 
	 * @param email The string value of the email we wish to query
	 * @return The string value of the API key if it exists, null otherwise.
	 */
	public static String getAPIKey(String email) {
		String apiKey = null;

		// Initialize query request object
		GetItemRequest getItemRequest = new GetItemRequest()
			.withTableName(ownerTable)
			.withKey(new com.amazonaws.services.dynamodb.model.Key(new AttributeValue().withS(email)))
			.withAttributesToGet("apikey");

		GetItemResult result = dynamoDB.getItem(getItemRequest);
		if (result.getItem() != null) {
			AttributeValue apiVal = result.getItem().get("apikey");
			apiKey = apiVal.getS();
		}
		return apiKey;
	}

	/**
	 * Create and put user data item in DynamoDB table
	 * 
	 * @param email		The user's email that is requesting a key
	 * @param ipAddress	User's IP Address for metrics
	 * @param hostname	User's ISP Host for metrics
	 * @return			Return successful = TRUE if DynamoDB request is successful, else FALSE
	 */
	public static boolean addUser(String email, String ipAddress, String hostname, HttpServletRequest request) {
		 boolean successful = false;
		 
		 // If database does not already contain the specified key for the email, insert a new one.
		 if (getAPIKey(email) == null) {
			String apiKey = generateSHAKey(email);

			// User items contains API Key (MD5), Email address, IP address, and host name
			Map<String, AttributeValue>[] items = createUserDataItems(apiKey, email, ipAddress, hostname);

			// Place items in their respective databases
			PutItemRequest putItemRequest = new PutItemRequest(ownerTable, items[0]);
			dynamoDB.putItem(putItemRequest);

			putItemRequest = new PutItemRequest(keyTable, items[1]);
			dynamoDB.putItem(putItemRequest);

			successful = true;
		 }

		// Send the user an email with the API key
		sendKeyEmail(email, getAPIKey(email), request);

		 return successful;	
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
	 * Creates two user data items for storage in DynamoDB. The first is used to look up the presence of an
	 * API key based on the email addres (email is the hash key). The second is used to look up the validity
	 * of a key (apikey is the hash key). This unusual approach is taken so that we don't have to use the 
	 * SCAN query approach in the db, and we can do a straight lookup in the respective tables. Thus, two
	 * tables are employed to store a user's API key
	 * 
	 * @param apiKey The MD5'ed string of the user's email that is the hash key value for this particular table
	 * @param email The user's email that is requesting a key
	 * @param ipAddress User's IP Address for metrics
	 * @param hostname User's ISP Host for metrics
	 * @return Returns the user data objects in an Map array
	 */
	private static Map<String, AttributeValue>[] createUserDataItems(String apikey,
			String email, String ipAddress, String hostname) {

		Map[] dataPair = new Map[2];
		// Get date and time of put request
		Calendar cal = Calendar.getInstance();
		
		// Create item with parameters listed above
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		dataPair[0] = item;
		item.put("email", new AttributeValue(email));
		item.put("apikey", new AttributeValue(apikey));
		AttributeValue dateAttr = new AttributeValue();
		dateAttr.setN(Long.toString(cal.getTimeInMillis()));
		item.put("date", dateAttr);
		item.put("ip", new AttributeValue(ipAddress));
		item.put("host", new AttributeValue(hostname));
		
		item = new HashMap<String, AttributeValue>();
		item.put("apikey", new AttributeValue(apikey));
		item.put("active", new AttributeValue(Boolean.toString(true)));
		item.put("email", new AttributeValue(email));
		dataPair[1] = item;

		return dataPair;
	}

	/**
	 * Encode the spefified string value using SHA-256 and returns the encoding as a String.
	 *
	 * @param src The source string to encode
	 * @return The source string encoded as a SHA-256 string.
	 */
	private static String generateSHAKey(String email) {
		
		// String buffer to deal with string byte data
		StringBuffer strBuffer = new StringBuffer();
		
		try {
			// Random long generator for seed
			Random generator = new Random();
			long random_seed = generator.nextLong();
			
			// Get encoder (used 256 to make length of key manageable. 512 seems a bit long for our purposes.
			MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
			
			// Get bytes from email string and update digest with the email first, then add the seed.
			msgDigest.update(email.getBytes());
			msgDigest.update(getBytes(random_seed));
			
			// Get Byte array from encoded string
			byte byteData[] = msgDigest.digest();
			
			// Iterate through string's bytes and append to the buffer we will return.
			for (int i = 0; i < byteData.length; i++)
				strBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));

		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		}
		
		// Return the encoded buffer as a string
		return strBuffer.toString();
	}
	
	/**
	 * Returns the corresponding array of bytes from the specified long value.
	 *
	 * @param val The Long wrapper object that contains the base value.
	 * @return The array of bytes that represents the spefieid long value 'val'
	 */
	private static byte[] getBytes(Long val){
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(byteArray);

		try {
			dataOutput.writeLong(val);

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return byteArray.toByteArray();
	}
}