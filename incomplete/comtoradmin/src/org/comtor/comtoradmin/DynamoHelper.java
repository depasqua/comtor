package org.comtor.comtoradmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.DeleteItemRequest;
import com.amazonaws.services.dynamodb.model.DeleteItemResult;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.ScanRequest;
import com.amazonaws.services.dynamodb.model.ScanResult;

public class DynamoHelper {
	static String accesskey = "";
	static String secretkey = "";
	
	static BasicAWSCredentials credentials = new BasicAWSCredentials(accesskey, secretkey);
	static AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials);
	
	/**
	 * Scan DynamoDB
	 */
	public static List<Map<String, AttributeValue>> scanDynamo(String tableName){
		ScanResult result = client.scan(createScanRequest(tableName));
		    
		return result.getItems();
	}
	
	/**
	 * Create Scan Request
	 */
	public static ScanRequest createScanRequest(String tableName){
		ScanRequest scanRequest = new ScanRequest()
	    .withTableName(tableName);
						
		return scanRequest;
	}
	
	/**
	 * Print Results from DynamoDB Scan
	 */
    private static void printItem(Map<String, AttributeValue> attributeList) {
        for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
            String attributeName = item.getKey();
            AttributeValue value = item.getValue();
            System.out.println(attributeName
                    + " "
                    + (value.getS() == null ? "" : "S=[" + value.getS() + "]")
                    + (value.getN() == null ? "" : "N=[" + value.getN() + "]")
                    + (value.getSS() == null ? "" : "SS=[" + value.getSS() + "]")
                    + (value.getNS() == null ? "" : "NS=[" + value.getNS() + "] \n"));
        }
    }
    /**
     * Create a request to delete an item from a DynamoDB table
     * @param key
     * @param email
     * @return
     */
    public static DeleteItemRequest deleteRequest(String apikey, String email){
    	
    	Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
    	item.put("apikey", new AttributeValue(apikey));
    	item.put("email", new AttributeValue(email));
    	
    	System.out.println(item.get("apikey"));
    	System.out.println(item.get("email"));
    	
    	Key itemKey = new Key()
			.withHashKeyElement(item.get("apikey"))
    		.withRangeKeyElement(item.get("email"));
    	
		DeleteItemRequest deleteRequest = new DeleteItemRequest("comtorusers", itemKey);
		
    	return deleteRequest;
    }
    /**
     * Delete the item(s) specified in the request object from the DynamoDB table
     * @param action
     * @param key
     * @param email
     * @return
     */
    public static boolean deleteItem(String action, String key, String email){
    	System.out.println(action);
		DeleteItemRequest deleteRequest = deleteRequest(key, email);
		
		DeleteItemResult deleteResult = client.deleteItem(deleteRequest);
    	System.out.println(deleteResult.toString());
    	
    	return false;
    }
}
