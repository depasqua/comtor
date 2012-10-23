package org.comtor.apiclient;

import java.io.*;
import java.util.*;
import java.text.*;

import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.sqs.*;
import com.amazonaws.services.sqs.model.*;


public class APIClient {
	public static void main(String [] args) {
		try {
			File jar = new File ("/Users/depasqua/Desktop/comtor/tester/tester.jar");
			APIClient.submit("2e46472a21af755d2383d703a3310875", jar);

		} catch (NullPointerException npe) {
			System.err.println(npe);
		}
	}

	public static boolean submit(String apiKey, File jarFile) {
		AmazonSQS sqs = null;
		AmazonS3 s3 = null;

		try {
			// Read AWS credentials and create client objects
			PropertiesCredentials awsCreds = new PropertiesCredentials(new File("AwsCredentials.properties"));
			sqs = new AmazonSQSClient(awsCreds);
			s3 = new AmazonS3Client(awsCreds);

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
			msg.setProperty("doclet0", "org.comtor.analyzers.BasicInfo");
			msg.setProperty("doclet1", "org.comtor.analyzers.SpellCheck");
			msg.setProperty("doclet2", "org.comtor.analyzers.PercentageMethods");
			msg.setProperty("doclet3", "org.comtor.analyzers.OffensiveWords");

			SendMessageRequest request = new SendMessageRequest("https://queue.amazonaws.com/557248582542/comtor_api_server",
					msg.toString());
			sqs.sendMessage(request);
			System.err.println("Message:\n" + msg.toString());

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
