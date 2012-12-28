package org.comtor.apiclient;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.sqs.*;
import com.amazonaws.services.sqs.model.*;

import com.fasterxml.jackson.databind.*;

public class APIClient {

	/**
	 * Sample main method for the API client. Submits a pre-provided jar file to the server
	 * for processing.
	 */
	public static void main(String [] args) {
		try {
			Scanner scan = new Scanner (System.in);
			System.out.println("Enter a path to a jar file to submbit: " );
			String filepath = scan.nextLine();
			System.out.println("Enter an API key to use for submitting the request: " );
			String key = scan.nextLine();
			boolean result = submit(key, new File(filepath));

		} catch (NullPointerException npe) {
			System.err.println(npe);
		}
	}

	/**
	 * Submission code for API client. This method must be passed an API key string, and a File object
	 * for the jar file to be submitted.
	 * 
	 * @param apiKey the COMTOR API key for the submission, this encodes the user's email address
	 * @param jarFile the Java jar file continaing the source code to be analyzed by COMTOR
	 * @return Returns a true value if the submission was executed successfully, false otherwise.
	 */
	public static boolean submit(String apiKey, File jarFile) {
		AmazonSQS sqs = null;
		AmazonS3 s3 = null;
		String authserv = "http://cloud.comtor.org/authservDev/authserv?apikey=" + apiKey;
		String sqsserv = "https://queue.amazonaws.com/557248582542/comtor_api_server";

		try {
			// Obtain the AWS credentials from the server and create client objects
			Scanner scan = new Scanner(new URL(authserv).openStream());
			String authResponse = "";
			while (scan.hasNextLine())
				authResponse += scan.nextLine();
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> authdata = mapper.readValue(authResponse, Map.class);

			BasicSessionCredentials basicSessionCredentials = new BasicSessionCredentials(authdata.get("accessKey"), 
					authdata.get("secretKey"), authdata.get("sessionToken"));
			sqs = new AmazonSQSClient(basicSessionCredentials);
			s3 = new AmazonS3Client(basicSessionCredentials);

			// Place the provided jar file on S3
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSSZ");
			String dateStr = formatter.format(new Date());
			String key = apiKey + "-" + dateStr + ".jar";
			s3.putObject(new PutObjectRequest("org.comtor.apiqueue", key, jarFile));

			// Create the Java properties message to inform the server and send it to SQS
			Properties msg = new Properties();
			msg.setProperty("key", apiKey);
			msg.setProperty("date", dateStr);
			msg.setProperty("numModules", "4");
			msg.setProperty("doclet1", "org.comtor.analyzers.CheckAuthor");
			msg.setProperty("doclet2", "org.comtor.analyzers.SpellCheck");
			msg.setProperty("doclet3", "org.comtor.analyzers.PercentageMethods");
			msg.setProperty("doclet4", "org.comtor.analyzers.OffensiveWords");
			msg.setProperty("doclet5", "org.comtor.analyzers.CommentAvgRatio");

			SendMessageRequest request = new SendMessageRequest(sqsserv, msg.toString());
			sqs.sendMessage(request);

		} catch (FileNotFoundException fnfe) {
			System.err.println("AWS credentials properties file not found. Terminating.");
			return false;

		} catch (IOException ioe) {
			System.err.println(ioe);
			return false;
		}

		return true;
	}
}
