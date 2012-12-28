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
package org.comtor.apiserv;

import java.io.*;
import java.util.*;
import java.text.*;
import java.util.logging.*;

import org.comtor.cloud.*;
import org.comtor.drivers.*;
import org.comtor.analyzers.*;

import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.sqs.*;
import com.amazonaws.services.sqs.model.*;
import com.amazonaws.services.dynamodb.*;
import com.amazonaws.services.dynamodb.model.*;

/**
 * This class represents our API server for COMTOR. The server is a background process that sleeps and wakes
 * every 15 seconds to determine if there is a job for it in the AWS SQS (message queue) which defines the 
 * COMTOR job to run. The message should include the API key for the user, which modules to execute, the 
 * jar file name (stored in S3) to process, and a date/time stamp. 
 * 
 * In time we should look at using the Apache commons daemon infrastructure (http://commons.apache.org/daemon/)
 * to more robustly provide this feature. For now, we'll have to hand start/stop it.
 *
 * @author Michael Murphy
 * @author Peter DePasquale
 */
public class APIServ implements Runnable {

	/** 
	 * Kicks off the server for processing COMTOR API requests. 
	 *
	 * @param args the list of command line arguments to the program.
	 */
	public static void main(String[] args)  {
		Thread server = new Thread(new APIServ());
		server.start();
	}

	/**
	 * The 'run' method is the main excutable portion of the server thread. Its job is to 
	 * wake every 15 seconds and process any API requests for COMTOR execution.
	 */
	public void run () {
		AmazonSQS sqs = null;
		AmazonS3 s3 = null;
		AmazonDynamoDBAsyncClient dynamo = null;

		try {
			// Read AWS credentials and create client objects
			PropertiesCredentials awsCreds = new PropertiesCredentials(new File("AwsCredentials.properties"));
			sqs = new AmazonSQSClient(awsCreds);
			s3 = new AmazonS3Client(awsCreds);
			dynamo = new AmazonDynamoDBAsyncClient(awsCreds);

		} catch (FileNotFoundException fnfe) {
			System.err.println("AWS credentials properties file not found. Terminating.");
			return;

		} catch (IOException ioe) {
			System.err.println(ioe);
			return;
		}

		// Shuts off the noisy logger from AWS
		java.util.logging.Logger.getLogger("com.amazonaws.request").setLevel(Level.OFF);

		String queueURL = "https://queue.amazonaws.com/557248582542/comtor_api_server";

		try {
			// Initialize the message request object. Sets the maximum number of messages to
			// return in the event that there are multiple messages awaiting processing.
			ReceiveMessageRequest rcvMsgRqst = new ReceiveMessageRequest(queueURL);
			rcvMsgRqst.setMaxNumberOfMessages(1);
			GetQueueAttributesRequest queueRequest = new GetQueueAttributesRequest(queueURL);
			queueRequest.withAttributeNames("ApproximateNumberOfMessages");
			
			while (true) {
				// Poll the queue for the number of messages. This is used to skip message processing
				// and put this thread to sleep for 15 seconds in the event that there are no messages
				// pending.
				GetQueueAttributesResult queueResult = sqs.getQueueAttributes(queueRequest);
				Map<String, String> resultMap = queueResult.getAttributes();
				String numMessages = resultMap.get("ApproximateNumberOfMessages");

				if (numMessages == null || Integer.parseInt(numMessages) == 0) {
					System.err.println("No messages found; sleeping...");
					// The queue is empty, sleep for 15 sec.
					Thread.sleep(15000);

				} else {
					// Process the messages on the queue, 1 at a time
					List<Message> messageList = sqs.receiveMessage(rcvMsgRqst).getMessages();
					if (messageList.size() != 0) {
						Message msg = messageList.get(0);
						Properties msgProps = new Properties();
						msgProps.load(new StringReader(msg.getBody()));
						String apiKeyString = msgProps.getProperty("key");

						File tempDir = new File(System.getProperty("java.io.tmpdir"));
						File comtorTmpDir = null;

						if (tempDir.isDirectory()) {
							// Create a COMTOR subdirectory in which to process the request
							SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmssSSSZ");
							String dateTimeStr = formatter.format(new Date());
							String sessionName = apiKeyString + '-' + dateTimeStr;
							comtorTmpDir = new File(tempDir, sessionName);
							if (!comtorTmpDir.exists())
								comtorTmpDir.mkdir();

							// Write modules to docletlist.properties here
							File moduleListFile = writeDocletListFile(comtorTmpDir, msgProps);

							// Retrieve the uploaded jar file from S3, extract its contents, and commence processing
							File localJar = copyJarFromS3(comtorTmpDir, msgProps, s3);
							CloudUpload.extractJarFile(localJar, comtorTmpDir.toString());
							ComtorStandAlone.setMode(Mode.API);
							Comtor.start(comtorTmpDir.toString());

							// Place analysis report on S3 for user and archive results
							File reportFile = new File(comtorTmpDir, "comtorReport.txt");
							archiveReport(sessionName, apiKeyString, dateTimeStr, reportFile, dynamo);							
							String reportURLString = AWSServices.storeReportS3(reportFile, apiKeyString).toString();

							// Rewrite protocol of URL for report to eliminate https (certificate warnings)
							if (reportURLString.startsWith("https"))
								reportURLString = reportURLString.replaceFirst("https", "http");
							
							// Shorten the url via Bitly and send email to user.
							reportURLString = BitlyServices.shortenUrl(reportURLString);
							String msgText = "<img style=\"margin-left: auto; margin-right: auto; display: block;\" " +
									"src=\"http://dev.comtor.org:8080/images/comtor/comtorLogo.png\" width=\"160\" " +
									"alt=\"COMTOR logo\"/>";
							msgText += "Thank you for your submission to the COMTOR system. Your report is "; 
							msgText += "now available for access/download at the following URL: " + reportURLString + ". ";
							msgText += "You can reach the COMTOR team at comtor@tcnj.edu.";
							
							String emailDest = DynamoUtils.getEmailFromKey(apiKeyString);
							if (emailDest != null)
								AWSServices.sendEmail(emailDest, msgText);

							// Delete the message, its corresponding S3 file, and the local files
							// sqs.deleteMessage(new DeleteMessageRequest(queueURL, messageList.get(0).getReceiptHandle()));
							// s3.deleteObject("org.comtor.apiqueue", props.getProperty("key") + "-" + props.getProperty("date") + ".jar");
							purgeDirectory(comtorTmpDir);
						}
					}
				}
			}

		} catch (AmazonServiceException ase) {
			System.err.println("Caught an AmazonServiceException, which means your request made it " +
				"to Amazon SQS, but was rejected with an error response for some reason.");
			System.err.println("Error Message:    " + ase.getMessage());
			System.err.println("HTTP Status Code: " + ase.getStatusCode());
			System.err.println("AWS Error Code:   " + ase.getErrorCode());
			System.err.println("Error Type:       " + ase.getErrorType());
			System.err.println("Request ID:       " + ase.getRequestId());
			ase.printStackTrace();
			
		} catch (AmazonClientException ace) {
			System.err.println("Caught an AmazonClientException, which means the client encountered " +
				"a serious internal problem while trying to communicate with SQS, such as not " +
				"being able to access the network.");
			System.err.println("Error Message: " + ace.getMessage());

		} catch (IOException ioe) {
			ioe.printStackTrace();

		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}		
	}

	/**
	 * Purges the spefied directory, including all contents, subdirectories and their contents.
	 * 
	 * @param dir A File object representing the directory to be purged.
	 */
	public void purgeDirectory(File dir) {
		if (dir != null) {
			File[] contents = dir.listFiles();

			for (File item : contents) {
				if (item.isDirectory())
					purgeDirectory(item);
				else
					item.delete();
			}
			dir.delete();
		}
	}

	/**
	 * Archives the newly created COMTOR report file to the DynamoDB for future analysis.
	 *
	 * @param reportID The intended ID of this DB entry - this is a combination of the user's key and the date/time
	 * @param apiKey The API key of the user executing the COMTOR request
	 * @param dateTime The date/time stamp of the report's creation (execution of COMTOR)
	 * @param reportFile A reference to a File object (the report file) so that the contents can be included
	 * in the archival
	 * @param s3 A reference to the S3 client to which the stored report file will be archived
	 */
	public void archiveReport(String reportID, String apiKey, String dateTime, File reportFile, AmazonDynamoDBAsyncClient db) {
		if (reportFile.exists() && reportFile.isFile()) {
			try {
				// Create a map of items to store
				Map<String, AttributeValue> archiveItem = new HashMap<String, AttributeValue>();
				archiveItem.put("reportID", new AttributeValue(reportID));
				archiveItem.put("apiKey", new AttributeValue(apiKey));
				archiveItem.put("dateTime", new AttributeValue(dateTime));

				BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(reportFile)));
				String inputContents = "";
				while (input.ready())
					inputContents += input.readLine();

				input.close();
				archiveItem.put("reportFile", new AttributeValue(inputContents));

				// Create AWS DynamoDB put item request with tablename and item created, put in DB
				PutItemRequest putItemRequest = new PutItemRequest("org.comtor.apireports", archiveItem);
				db.putItem(putItemRequest);

			} catch (IOException ioe) {
				System.err.println(ioe);
			}
		}
	}

	/**
	 * Copies the .jar file specified in the properties object (read from SQS message) to the specified
	 * directory. The .jar file should be located in S3, but the filename is encoded in the message. Once copied,
	 * the .jar file will need to be extracted (not performed by this method).
	 *
	 * @param dir The location into which the .jar file will be extracted.
	 * @param props The Java properties object that contains the message contents for this execution of COMTOR. The
	 * properties should contain the user's key and the date (among other things) that we will use to find the archived
	 * .jar file that was stored on S3.
	 * @param s3 A reference to the S3 client from which the stored .jar file will be fetched
	 * @return a reference to the copied jar file, or null if the operation fails.
	 */
	public File copyJarFromS3(File dir, Properties props, AmazonS3 s3) {
		File localJar = null;

		try {
			String fileName = props.getProperty("key") + "-" + props.getProperty("date") + ".jar";
			S3Object s3Object = s3.getObject(new GetObjectRequest("org.comtor.apiqueue", fileName));
			InputStream s3Reader = new BufferedInputStream(s3Object.getObjectContent());
			localJar = new File(dir, fileName);
			OutputStream jarWriter = new BufferedOutputStream(new FileOutputStream(localJar));

			// Read the jar file from S3 and save it locally for processing.
			// Javadoc requires the jar file to be locally accessible via the file system.
			int byteRead = -1;
			while ((byteRead = s3Reader.read()) != -1)
				jarWriter.write(byteRead);

			jarWriter.flush();
			jarWriter.close();
			s3Reader.close();

		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
		return localJar;
	}

	/**
	 * Writes the docletList.properties file in the specified working directory based on the
	 * specified properties object provided from the SQS message.
	 *
	 * @param dir The working directory into which the docletList.properties file will be written
	 * @param props The properties object which has been obtainined by reading and processing the SQS message
	 * @return returns a reference to the newly created docletList.properties file or a null value if something goes wrong.
	 */
	private File writeDocletListFile(File dir, Properties props) {
		int docletNum = 0;

		try {
			File docletList = new File(dir, "docletList.properties");
			PrintWriter writer = new PrintWriter(docletList);

			while (props.getProperty("doclet" + docletNum) != null) {
				writer.println("doclet" + docletNum + ": " + props.getProperty("doclet" + docletNum));
				docletNum++;
			}
			writer.close();
			return docletList;

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}
}
