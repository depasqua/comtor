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

import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.services.dynamodb.model.*;
import com.amazonaws.services.dynamodb.*;

public class DynamoUtils {
	private static String tableName = "org.comtor.apikeys";
	/**
	 * This method attempts to scan the DynamoDB to determine the owner (email address) of the specified
	 * API key. The request is made against the table specified in static String variable 'tableName'.
	 *
	 * @param apiKey The MD5'ed string of the user's email that is the hash key value for this particular table
	 * @return the email address of the user specified by the apiKey, or a null if there is no entry for the key
	 */
	public static String getEmailFromKey(String apiKey) {
		String result = null;
		AmazonDynamoDBAsyncClient db = null;

		if (apiKey != null) {
			try {
				PropertiesCredentials creds = new PropertiesCredentials(new File("AwsCredentials.properties"));
				db = new AmazonDynamoDBAsyncClient(creds);

				HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();

				// Set the search condition (the row entry in the db contains a matching api key) and
				// insert it in the scan filter for the scan.
				Condition condition = new Condition()
					.withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue().withS(apiKey));
				scanFilter.put("apikey", condition);

				// Request scan with condition created above on designated table
				ScanRequest scanRequest = new ScanRequest(tableName)
					.withScanFilter(scanFilter);

				// Obtain the scan result and parse out the first (only) result
				ScanResult scanResult = db.scan(scanRequest);
				if (scanResult != null && scanResult.getCount().intValue() != 0) {
					Map<String, AttributeValue> item = scanResult.getItems().get(0);
					result = item.get("email").getS();
				}

			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
				System.err.println(fnfe);

			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.err.println(ioe);
			}

			if (db != null)
				db.shutdown();
		}

		return result;
	}
}