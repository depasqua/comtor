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

public class AWSServices {
	/**
	 * Sends the COMTOR report to the specified recipient. This method is called after
	 * the report is stored to S3 (by storeReportS3), as it assumes the report has been
	 * generated and stored.
	 *
	 * @param toAddr the recipient's email address
	 * @param url the url of the report's location
	 * @param req the servlet handling the inbound HTTP request
	 */
	public static void sendReportEmail(String toAddr, String url, HttpServletRequest req) {
		String comtorEmail = "comtor@tcnj.edu";
		try {
			PropertiesCredentials credentials = new PropertiesCredentials(
					new File("AwsCredentials.properties"));
			AmazonSimpleEmailService ses = new AmazonSimpleEmailServiceClient(credentials);
			
			// Create a new Message
			com.amazonaws.services.simpleemail.model.Message msg =
				new com.amazonaws.services.simpleemail.model.Message().withSubject(new Content("COMTOR Results"));
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
			msgText += "Thank you for your submission to the COMTOR system. Your report is "; 
			msgText += "now available for access/download at the following URL: " + url + ". ";
			msgText += "This link will remain active for a period of 5 days.\n\n";
			msgText += "You can reach the COMTOR team at " + comtorEmail + ".";

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
		} catch (IOException ieo) {
			System.err.println(ieo);
		}
	}

	/**
	 * Stores the specified report on the S3 store.
	 *
	 * @param report A File reference to the report created by this execution of COMTOR
	 * @param sessionID The session name/id of the currently executing session
	 * @return The URL of the downloadable report file
	 */
	public static URL storeReportS3(File report, String sessionID) {
		URL reportURL = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmssSSSZ");
			String key = sessionID + '-' + formatter.format(new Date());
			String bucketName = "org.comtor.reports";
			GregorianCalendar expiring = new GregorianCalendar();
			expiring.add(Calendar.DATE, 5);

			// Obtain AWS credentials
			AmazonS3 s3 = new AmazonS3Client(new PropertiesCredentials(
				new File("AwsCredentials.properties")));
			s3.putObject(new PutObjectRequest(bucketName, key, report));
			reportURL = s3.generatePresignedUrl(bucketName, key, expiring.getTime());

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (IOException ieo) {
			System.out.println(ieo);
		} catch (NullPointerException np) {
			System.out.println(np);
		}
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

		try {
			AmazonSimpleDB sdb = new AmazonSimpleDBClient(new PropertiesCredentials(
					new File("AwsCredentials.properties")));

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
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon SimpleDB, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with SimpleDB, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (IOException ieo) {
			System.out.println(ieo);
		}
	}
}