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
import java.net.*;
import java.util.*;
import java.text.*;
import java.security.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.http.*;

import org.comtor.util.*;

import org.apache.commons.io.*;

import com.amazonaws.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.simpledb.*;
import com.amazonaws.services.simpledb.model.*;
import com.amazonaws.services.dynamodb.*;
import com.amazonaws.services.dynamodb.model.*;
import com.amazonaws.auth.PropertiesCredentials;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class AWSServices {
	private static Logger logger = LogManager.getLogger(AWSServices.class.getName());
	private static PropertiesCredentials apiCredentials = null;
	private static AmazonS3 s3Client = null;
	private static AmazonDynamoDBAsyncClient dynamoDBClient = null;
	private static AmazonSimpleEmailService sesClient = null;
	private static String ownerTable = "org.comtor.keyOwners";
	private static String keyTable = "org.comtor.keyDetails";

	/**
	 * Initializer method to read the credentials for AWS and to set up the necessary query and database objects
	 * prior to first use.
	 */
	public static void init() {
		logger.entry();
		try {
			logger.debug("Attempting to access credentials: " + System.getProperty("catalina.base") + java.io.File.separator + "APICredentials.properties");
			File apiCreds = new File(System.getProperty("catalina.base") + java.io.File.separator + "APICredentials.properties");
			apiCredentials = new PropertiesCredentials(apiCreds);

			s3Client = new AmazonS3Client(apiCredentials);
			dynamoDBClient = new AmazonDynamoDBAsyncClient(apiCredentials);
			sesClient = new AmazonSimpleEmailServiceClient(apiCredentials);

		} catch (FileNotFoundException fnfe) {
			logger.catching(fnfe);

		} catch (IOException ioe) {
			logger.catching(ioe);
		}

		logger.exit();
	}

	/**
	 * Queries the APIdb to determine if the specified API key is valid and active.
	 *
	 * @param apikey The string value of the API key
	 * @return A true value if the key is valid and active, false otherwise
	 */
	public static boolean checkAPIKey(String apiKey) {
		logger.entry();
		boolean result = false;

		// Initialize query request object
		GetItemRequest getItemRequest = new GetItemRequest()
			.withTableName(keyTable)
			.withKey(new com.amazonaws.services.dynamodb.model.Key(new AttributeValue().withS(apiKey)))
			.withAttributesToGet("active");

		GetItemResult queryResult = dynamoDBClient.getItem(getItemRequest);
		if (queryResult.getItem() != null) {
			AttributeValue keyActiveValue = queryResult.getItem().get("active");
			result = Boolean.valueOf(keyActiveValue.getS());
		}

		return logger.exit(result);
	}

	/**
	 * Queries the APIdb to determine the matching email address for the specified API key.
	 *
	 * @param apikey The string value of the API key
	 * @return The email address associated with the specified API key, null otherwise.
	 */
	public static String getEmailFromKey(String apiKey) {
		logger.entry(apiKey);
		String emailStr = null;

		// Initialize query request object
		GetItemRequest getItemRequest = new GetItemRequest()
			.withTableName(keyTable)
			.withKey(new com.amazonaws.services.dynamodb.model.Key(new AttributeValue().withS(apiKey)))
			.withAttributesToGet("email");
		GetItemResult queryResult = dynamoDBClient.getItem(getItemRequest);
		if (queryResult.getItem() != null) {
			AttributeValue keyActiveValue = queryResult.getItem().get("email");
			emailStr = keyActiveValue.getS();
		}

		return logger.exit(emailStr);
	}

	/**
	 * Queries the db to determine if the given email corresponds to an API key. The state of the key
	 * active/inactive is not considered.
	 * 
	 * @param email The string value of the email we wish to query
	 * @return The string value of the API key if it exists, null otherwise.
	 */
	public static String getAPIKey(String email) {
		logger.entry(email);
		String apiKey = null;

		// Initialize query request object
		GetItemRequest getItemRequest = new GetItemRequest()
			.withTableName(ownerTable)
			.withKey(new com.amazonaws.services.dynamodb.model.Key(new AttributeValue().withS(email)))
			.withAttributesToGet("apikey");

		GetItemResult result = dynamoDBClient.getItem(getItemRequest);
		if (result.getItem() != null) {
			AttributeValue apiVal = result.getItem().get("apikey");
			apiKey = apiVal.getS();
		}

		return logger.exit(apiKey);
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

		logger.entry(apikey, email, ipAddress, hostname);
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

		return logger.exit(dataPair);
	}

	/**
	 * Create and put user data item in DynamoDB table. This method does check to determine if the email address 
	 * is already bound to an API key.
	 * 
	 * @param email		The user's email that is requesting a key
	 * @param ipAddress	User's IP Address for metrics
	 * @param hostname	User's ISP Host for metrics
	 * @param request	The request object, needed to pass to the email subsystem to obtain the COMTOR logo in the email
	 * @return			Return successful = TRUE if DynamoDB request is successful, else FALSE
	 */
	public static boolean addUser(String email, String ipAddress, String hostname, HttpServletRequest request) {
		logger.entry(email, ipAddress, hostname, request);
		boolean successful = false;

		// If database does not already contain the specified key for the email, insert a new one.
		if (getAPIKey(email) == null) {
			String apiKey = generateSHAKey(email);

			// User items contains API Key (MD5), Email address, IP address, and host name
			Map<String, AttributeValue>[] items = createUserDataItems(apiKey, email, ipAddress, hostname);

			// Place items in their respective databases
			PutItemRequest putItemRequest = new PutItemRequest(ownerTable, items[0]);
			dynamoDBClient.putItem(putItemRequest);

			putItemRequest = new PutItemRequest(keyTable, items[1]);
			dynamoDBClient.putItem(putItemRequest);

			successful = true;
		}

		// Send the user an email with the API key
		sendAPIKeyEmail(email, getAPIKey(email), request);

		return logger.exit(successful);
	}

	/**
	 * Sends the provided email to the specified recipient.
	 *
	 * @param toAddr the recipient's email address
	 * @param body the body of the email to be sent
	 */
	public static void sendEmail(String toAddr, String bodyStr) {
		logger.entry(toAddr, bodyStr);
		String comtorEmail = "comtor@tcnj.edu";

		try {
			// Create a new Message
			com.amazonaws.services.simpleemail.model.Message msg =
				new com.amazonaws.services.simpleemail.model.Message().withSubject(new Content("COMTOR Results"));
			SendEmailRequest request = new SendEmailRequest().withSource(comtorEmail);
			Destination dest = new Destination().withToAddresses(toAddr);
			request.withDestination(dest);

			Content htmlContent = new Content().withData(bodyStr);
			Body msgBody = new Body().withHtml(htmlContent);
			msg.setBody(msgBody);
			
			request.setMessage(msg);
			sesClient.sendEmail(request);

		} catch (AmazonClientException e) {
			logger.catching(e);
		}

		logger.exit();
	}

	/**
	 * Sends an email to the specified recipient. This method is called after
	 * the API key has been generated.
	 *
	 * @param toAddr the recipient's email address
	 * @param apiKey the API for the specified email address
	 * @param req the servlet handling the inbound HTTP request	 
	 */
	public static void sendAPIKeyEmail(String toAddr, String apiKey, HttpServletRequest req) {
		logger.entry(toAddr, apiKey, req);

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
			sesClient.sendEmail(request);

		} catch (AmazonClientException ace) {
			logger.catching(ace);
		}

		logger.exit();
	}

	/**
	 * Encode the spefified string value using SHA-256 and returns the encoding as a String.
	 *
	 * @param src The source string to encode
	 * @return The source string encoded as a SHA-256 string.
	 */
	private static String generateSHAKey(String email) {
		logger.entry(email);

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
			logger.catching(nsae);
		}
		
		// Return the encoded buffer as a string
		return logger.exit(strBuffer.toString());
	}
	
	/**
	 * Returns the corresponding array of bytes from the specified long value.
	 *
	 * @param val The Long wrapper object that contains the base value.
	 * @return The array of bytes that represents the spefieid long value 'val'
	 */
	private static byte[] getBytes(Long val) {
		logger.entry(val);
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(byteArray);

		try {
			dataOutput.writeLong(val);

		} catch (IOException ioe) {
			logger.catching(ioe);
		}

		return logger.exit(byteArray.toByteArray());
	}

	/**
	 * Stores the specified report on the S3 store.
	 *
	 * @param report A File reference to the report created by this execution of COMTOR
	 * @param prefix The prefix String to be used as part of the stored file name
	 * @return The URL of the downloadable report file
	 */
	public static URL storeReportS3(File report, String prefix) {
		logger.entry(report, prefix);

		URL reportURL = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmssSSSZ");
			String key = prefix + '-' + formatter.format(new Date());
			String bucketName = "org.comtor.reports";
			GregorianCalendar expiring = new GregorianCalendar();
			expiring.add(Calendar.DATE, 5);

			// Obtain AWS credentials
			logger.debug("S3 key: " + key);
			logger.debug(s3Client);
			s3Client.putObject(new PutObjectRequest(bucketName, key, report));
			reportURL = s3Client.generatePresignedUrl(bucketName, key, expiring.getTime());

		} catch (AmazonServiceException ase) {
			logger.catching(ase);

		} catch (AmazonClientException ace) {
			logger.catching(ace);

		} catch (NullPointerException np) {
			logger.catching(np);
		}

		return logger.exit(reportURL);
	}
	
	/**
	 * Logs the request handled by this instance of the system to the SDB service.
	 * 
	 * @param requestIP the String representation of the requesting client's IP address
	 * @param emailAddr the String representation of the client's email address
	 * @param dateTime the String representation of the current date/time 
	 */
	public static void storeCloudUse(String requestIP, String emailAddr, Long dateTime, InterfaceSystem frontEnd, File jsonReport) {
		logger.entry(requestIP, emailAddr, dateTime, frontEnd);

		try {
			String tableName = "org.comtor.usage";
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			item.put("email", new AttributeValue(emailAddr));
			item.put("datetime", new AttributeValue().withN(Long.toString(dateTime)));
			switch (frontEnd) {
				case WWW: 
					item.put("accessMechanism", new AttributeValue("www"));
					break;

				case WEBCAT:
					item.put("accessMechanism", new AttributeValue("webcat"));
					break;

				case ECLIPSE:
					item.put("accessMechanism", new AttributeValue("eclipse"));
					break;

				case NETBEANS:
					item.put("accessMechanism", new AttributeValue("netbeans"));
					break;


				default:
					item.put("accessMechanism", new AttributeValue("unknown"));
					break;
			}
			item.put("ipAddress", new AttributeValue(requestIP));
			item.put("jsonReport", new AttributeValue(FileUtils.readFileToString(jsonReport)));

			PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
			PutItemResult putItemResult = dynamoDBClient.putItem(putItemRequest);

		} catch (AmazonServiceException ase) {
			logger.catching(ase);

		} catch (AmazonClientException ace) {
			logger.catching(ace);

		} catch (IOException ieo) {
			logger.catching(ieo);
		}

		logger.exit();
	}
}