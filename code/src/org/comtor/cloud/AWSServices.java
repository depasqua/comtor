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

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.http.*;

import com.amazonaws.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.simpledb.*;
import com.amazonaws.services.simpledb.model.*;
import com.amazonaws.auth.PropertiesCredentials;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class AWSServices {
	private static Logger logger = LogManager.getLogger(org.comtor.cloud.AWSServices.class.getName());

	private static File awscreds = new File ("AwsCredentials.properties");

	/**
	 * Sends the provided email to the specified recipient.
	 *
	 * @param toAddr the recipient's email address
	 * @param body the body of the email to be sent
	 */
	public static void sendEmail(String toAddr, String bodyStr) {
		logger.entry();
		String comtorEmail = "comtor@tcnj.edu";
		try {
			PropertiesCredentials credentials = new PropertiesCredentials(awscreds);
			AmazonSimpleEmailService ses = new AmazonSimpleEmailServiceClient(credentials);
			
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
			ses.sendEmail(request);

		} catch (AmazonClientException e) {
			e.printStackTrace();
			logger.error("Caught a AmazonClientException, which means that there was a "
					+ "problem sending your message to Amazon's E-mail Service check the "
					+ "stack trace for more information.");
			
		} catch (IOException ieo) {
			logger.error(ieo);
		}
		logger.exit();
	}

	/**
	 * Stores the specified report on the S3 store.
	 *
	 * @param report A File reference to the report created by this execution of COMTOR
	 * @param prefix The prefix String to be used as part of the stored file name
	 * @return The URL of the downloadable report file
	 */
	public static URL storeReportS3(File report, String prefix) {
		logger.entry();
		URL reportURL = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmssSSSZ");
			String key = prefix + '-' + formatter.format(new Date());
			String bucketName = "org.comtor.reports";
			GregorianCalendar expiring = new GregorianCalendar();
			expiring.add(Calendar.DATE, 5);

			// Obtain AWS credentials
			AmazonS3 s3 = new AmazonS3Client(new PropertiesCredentials(awscreds));
			s3.putObject(new PutObjectRequest(bucketName, key, report));
			reportURL = s3.generatePresignedUrl(bucketName, key, expiring.getTime());

		} catch (AmazonServiceException ase) {
			logger.error("Caught an AmazonServiceException, which means the request made it "
					+ "to Amazon S3, but was rejected with an error response for some reason.");
			logger.error("Error Message:    " + ase.getMessage());
			logger.error("HTTP Status Code: " + ase.getStatusCode());
			logger.error("AWS Error Code:   " + ase.getErrorCode());
			logger.error("Error Type:       " + ase.getErrorType());
			logger.error("Request ID:       " + ase.getRequestId());

		} catch (AmazonClientException ace) {
			logger.error("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with S3, "
					+ "such as not being able to access the network.");
			logger.error("Error Message: " + ace.getMessage());

		} catch (IOException ieo) {
			logger.error(ieo);

		} catch (NullPointerException np) {
			logger.error(np);
		}
		logger.exit();
		return reportURL;
	}
	
	/**
	 * Logs the request handled by this instance of the system to the SDB service.
	 * 
	 * @param requestIP the String representation of the requesting client's IP address
	 * @param sessionNum the String representation of the client's session ID
	 * @param reportURL the String representation of the report's URL
	 * @param emailAddr the String representation of the client's email address
	 * @param dateTime the String representation of the current date/time 
	 */
	public static void storeCloudUse(String requestIP, String sessionID, String reportURL,
			String emailAddr, String dateTime) {
		logger.entry();
		try {
			AmazonSimpleDB sdb = new AmazonSimpleDBClient(new PropertiesCredentials(awscreds));

			// Set the domain to use
			String myDomain = "org.comtor.cloud.usage";
			List<ReplaceableItem> data = new ArrayList<ReplaceableItem>();
			data.add(new ReplaceableItem(dateTime).withAttributes(
				new ReplaceableAttribute("IP Address", requestIP, true),
				new ReplaceableAttribute("Session ID", sessionID, true),
				new ReplaceableAttribute("Report URL", reportURL, true),
				new ReplaceableAttribute("Email Address", emailAddr, true)));
			
			sdb.batchPutAttributes(new BatchPutAttributesRequest(myDomain, data));

		} catch (AmazonServiceException ase) {
			logger.error("Caught an AmazonServiceException, which means the request made it "
					+ "to Amazon SimpleDB, but was rejected with an error response for some reason.");
			logger.error("Error Message:    " + ase.getMessage());
			logger.error("HTTP Status Code: " + ase.getStatusCode());
			logger.error("AWS Error Code:   " + ase.getErrorCode());
			logger.error("Error Type:       " + ase.getErrorType());
			logger.error("Request ID:       " + ase.getRequestId());

		} catch (AmazonClientException ace) {
			logger.error("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with SimpleDB, "
					+ "such as not being able to access the network.");
			logger.error("Error Message: " + ace.getMessage());

		} catch (IOException ieo) {
			logger.error(ieo);
		}
		logger.exit();
	}
}