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

import com.amazonaws.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.auth.PropertiesCredentials;

public class AWSServices {
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
			Date expiring = new Date(112, 2, 31);

			// Obtain AWS credentials
			AmazonS3 s3 = new AmazonS3Client(new PropertiesCredentials(new File("AwsCredentials.properties")));
			s3.putObject(new PutObjectRequest(bucketName, key, report));
			reportURL = s3.generatePresignedUrl(bucketName, key, expiring);

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
}