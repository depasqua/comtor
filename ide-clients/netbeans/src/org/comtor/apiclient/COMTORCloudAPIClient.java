/**
 * Comment Mentor: A Comment Quality Assessment Tool 
 * Copyright (C) 2013 The College of New Jersey
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.comtor.apiclient;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.BufferedHttpEntity;

/**
 * This class represents the client side communications endpoint for talking
 * to the COMTOR Cloud API service.
 * 
 * This class underwent two fundamental re-designs, first moving from the "old"
 * COMTOR API with direct URL fetching and Amazon services to direct URL
 * fetching from the redesigned COMTOR API. We then had to modify it for
 * POST multipart/form uploads. We used the Apache HTTPClient package to
 * handle that task for us.
 * 
 * @author Peter J. DePasquale
 * @author Michael E. Locasto 
 */
public final class COMTORCloudAPIClient
{
    
    public static final String API_KEY_REQEUST_URI 
           // = "http://cloud.comtor.org/authservDev/keyrequest?email=";
             = "http://dev.comtor.org:8080/apiServ/keyrequest";
    public static final String APISERV_REQUEST_URI
           // = "http://cloud.comtor.org/authservDev/authserv?apikey=";
             = "http://dev.comtor.org:8080/apiServ/submit";
        
    /** An in-memory copy of the user's COMTOR API Key */
    private String m_APIKey = "";
    
    /** Default constructor */
    public COMTORCloudAPIClient(){}
    
    /** 
     * Allow classes that get a handle to this client to ask for
     * a copy of the API key.
     * 
     * @returns a copy of the API key as a java.lang.String
     */
    public String getKey(){ return m_APIKey; }
    
    /**
     * Allow clients of this class to tell us what the API key is. NB for
     * developers: we should eventually implement a loading mechanism that
     * looks for a persistent copy of the key in a properties file, etc.
     * 
     * At the moment, the user of the UI has to remind us of the key
     * upon startup.
     * 
     * @param String k - the API key
     */
    public void setKey(String k){ m_APIKey = new String(k); }
    
    /**
     * Given an email, issue an HTTP request to create (or retrieve) an API 
     * key. If the key already exists, no new key is created and the key
     * associated with the supplied email address is emailed to that address.
     * If the email address does not have a key associated with it, a key is
     * created.
     * 
     * @security - none; stealing someone's API key was trivial, now it requires
     *             access to their email
     * @param email - UTF-8 encoded email address. caller responsible for UTF-8
     */
    public boolean createAPIKey(String email)
    {
        boolean ret = false; //was the key gen request successful
        String inputLine; //line-by-line input of the HTTP response body
        String JSONResponse = "";
        JSONObject respkey;
        String APIKeyResponse = "";
        
        BufferedReader in = null;
        URL u = null;
        HttpURLConnection httpConn = null;
        String httpResponse = "";
                
        try 
        {
            //email = URLEncoder.encode(email, "UTF-8");
            u = new URL(COMTORCloudAPIClient.API_KEY_REQEUST_URI);
            httpConn = (HttpURLConnection) u.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod("POST");

            OutputStreamWriter writer = 
                    new OutputStreamWriter(httpConn.getOutputStream());
            writer.write("email="+email);
            writer.close();
            
            if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {        
                httpResponse = httpConn.getResponseMessage();
                if(httpResponse.equals("OK"))
                {
                    in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                    while ( null!=(inputLine = in.readLine()))
                    {
                        JSONResponse = inputLine;
                    }
                    in.close();
                    respkey = new JSONObject(JSONResponse);
                    APIKeyResponse = respkey.getString("request_processed");
                    if(APIKeyResponse.equals("new user API key generated, notification email sent"))
                    {
                        ret = true;
                    }else if(APIKeyResponse.startsWith("false; failed to add a new user:")){
                        ret = true;
                    }else if(APIKeyResponse.equals("false; bad input")){
                        ret = false;
                    }else{
                        ret = false;
                    }
                }else{
                    APIKeyResponse = "API key request failed";
                    ret = false;
                }
            }else{
                ret = false;
            }
        } catch (Exception eee) {
            System.err.println("error handling text in COMTOR email input");
        }
        return ret;
    }

    /**
     * A small method for reading a byte at a time from a JAR file.
     * 
     * @param file the file to process
     */
    private String readJARFile(File file)
    {
        StringBuilder filedata = new StringBuilder();
        int b = -1;
        
        try{
            FileInputStream fin = new FileInputStream(file);
            
            while(-1!=(b=fin.read()))
            {
                filedata.append(b);
            }
        }catch(Exception ex){
            System.err.println("["+this.getClass().getName()
                                  +"]: problem reading JAR file for upload: "
                                  + ex.getMessage());
        }
        return filedata.toString();
    }

    /**
     * Sanity check destination parameter before passing it to the COMTOR
     * cloud service.
     * 
     * @param s - a string, hopefully containing either "email" or "http". This
     * should typically come from the IDE code, which we've hopefully written
     * correctly, but we don't actually know what objects have a handle to us
     * and whether they are passing a correct value.
     */
    private boolean validateSink(String s)
    {
        boolean ret = false;
        
        ret = s.equals("email") || s.equals("http");
        
        return ret;
    }
    
    /**
     * Sanity check format before passing it to the COMTOR web service.
     * 
     * NB: we purposefully ignore "json" as a legal value, although the
     * COMTOR web service is happy to return it. For our purposes, either
     * HTML or plaintext suffices via email or for display in the IDE.
     * 
     * Further enhancements to the IDE may eventually prefer to receive a
     * JSON response.
     */
    private boolean validateFormat(String f)
    {
        boolean ret = false;
        
        ret = f.equals("text") || f.equals("html");
        
        return ret;
    }

    /**
     * Perform validation of the response we get back from COMTOR.
     * 
     * This is not validation of the internal structure of the answer,
     * (i.e., a langsec example if I've ever seen one), but rather validation 
     * of the object and general properties of the HTTP response.
     */
    private boolean validateResponse(HttpEntity resEntity, 
                                     HttpResponse response)
    {
        boolean ret = false;
                    
        if(null==resEntity)
        {
            System.err.println("resEntity was null");
            ret = false;
            return ret;
        }
        
        if(response.getStatusLine().getStatusCode()!=200)
        {
            System.err.println("status code was "+ response.getStatusLine().getStatusCode());
            ret = false;
            return ret;
        }
            
        System.err.println("["+this.getClass().getName()+"]: "+response.getStatusLine().toString());
        
        /*
         * this produces a NullPointerException
        if(!resEntity.getContentType().getValue().equals("application/json"))
        {
            //this line produces a null pointer exception
            System.err.println("Content-encoding was "+resEntity.getContentEncoding().getValue());
            System.err.println("Content-type was "+resEntity.getContentType().getValue());
            ret = false;
            return ret;
        }
        */
        
        //here, content length is still -1 for http responses; we haven't
        //yet read the response
        if(resEntity.getContentLength()<=0)
        {
            System.err.println("content length was "+resEntity.getContentLength());
            //ret = false;
            //return ret;
        }
                
              
        ret = true;
        return ret;
    }
    
    /**
     * Submission code for API client. This method must be passed an
     * API key string, and a File object for the jar file to be
     * submitted. Code partially copied from the example:
     * @link http://hc.apache.org/httpcomponents-client-ga/httpmime/examples/org/apache/http/examples/entity/mime/ClientMultipartFormPost.java
     * 
     * @see http://www.ietf.org/rfc/rfc1867.txt
     * 
     * @param apiKey the COMTOR API key for the submission, this
     * encodes the user's email address
     * @param jarFile the Java jar file containing the source code to
     * be analyzed by COMTOR
     * @param format - "text, html"
     * @param sink - email, http
     * 
     * @return a COMTORWebServiceResponse object that is either a successfully
     *  constituted response object or an instance of that object that is a thin
     *  wrapper around an error message and a boolean false value indicating a
     *  failed submission. Returns a true value if the submission was executed
     *  successfully, false otherwise.
     */
    public COMTORWebServiceResponse submit(String apiKey, 
                                           File jarFile,
                                           String format,
                                           String sink)
    {
        COMTORWebServiceResponse ret = null;
        
        HttpClient client = null;
        HttpPost post = null; 
        FileBody bin;
        StringBody comment; 
        StringBody key;
        StringBody rtype;
        StringBody rform;
        StringBody icli;
            
        MultipartEntity reqEntity;
        HttpResponse response;
        HttpEntity resEntity;

        if(null==apiKey || apiKey.equals(""))
        {
            System.err.println("Invalid or empty API key");
            return new COMTORWebServiceResponse("Invalid or empty API key");
        }
        
        if(null==jarFile)
        {
            System.err.println("bad or null JAR file handle");
            return new COMTORWebServiceResponse("bad or null JAR file handle");
        }
                
        if(false==validateSink(sink))
        {
            System.err.println("bad sink identifier");
            return new COMTORWebServiceResponse("bad sink identifier");
        }
        
        if(false==validateFormat(format)){
            System.err.println("bad format identifier");
            return new COMTORWebServiceResponse("bad format identifier");
        }
        
        client = new DefaultHttpClient();
        
        try 
        {
            post = new HttpPost(COMTORCloudAPIClient.APISERV_REQUEST_URI);
            bin = new FileBody(jarFile);
            comment = new StringBody("JAR File of length "
                                     +jarFile.length()
                                     +" bytes.");
            key = new StringBody(apiKey);
            rtype = new StringBody(sink);
            rform = new StringBody(format);
            icli = new StringBody("netbeans");
            reqEntity = new MultipartEntity();
            reqEntity.addPart("comment", comment);
            reqEntity.addPart("apikey", key);
            reqEntity.addPart("response_type", rtype);
            reqEntity.addPart("response_format", rform);
            reqEntity.addPart("interface_client", icli);
            reqEntity.addPart("jarfile", bin);

            post.setEntity(reqEntity);
            
            response = client.execute(post);
            resEntity = response.getEntity();
                        
            if(false==validateResponse(resEntity, response))
            {
                System.err.println("Response validation failed");
                ret = new COMTORWebServiceResponse("Response validation failed");
                return ret;
            }else{
                resEntity = new BufferedHttpEntity(resEntity);
                
                System.err.println("buffered resEntity size: "
                                    +resEntity.getContentLength());
                
                ret = new COMTORWebServiceResponse();
                ret.set_raw(EntityUtils.toString(resEntity));
                if(!ret.loadFromRaw())
                {
                    System.err.println("failed to load all of response");
                    //fail here.
                    ret.isErr = true;
                    ret.errmesg = "failed to load all of response";
                }else{
                    System.err.println("["
                                       +this.getClass().getName()
                                       +"] COMTORWebServiceResponse.loadFromRaw() succeeded");
                }
            }
            //EntityUtils.consume(resEntity);
        } catch (Exception eee) {
            //eee.printStackTrace();
            System.err.println("["+this.getClass().getName()
                                  +"]: error submitting JAR file: "
                                  +eee.getMessage());
            ret = new COMTORWebServiceResponse("["+eee.getClass().getName()+"] exception occurred during submitting of JAR file");
        }finally{
            if(null!=post)
            {
                post.releaseConnection();
            }
            try{
                client.getConnectionManager().shutdown();
            }catch(Exception ignore){
                ret = new COMTORWebServiceResponse("ignored ["+ignore.getClass().getName()+"] exception during shutdown of HTTP connection");
            }
        }
        
        return ret;   
    }

    
    /**
     * Old Submission code for API client. This method must be passed an
     * API key string, and a File object for the jar file to be
     * submitted.
     * 
     * Not suitable for use with COMTOR cloud service.
     * Code partially copied from:
     * @link https://developers.google.com/appengine/docs/java/urlfetch/usingjavanet#Simple_Requests_With_URLs
     * 
     * @param apiKey the COMTOR API key for the submission, this
     * encodes the user's email address
     * @param jarFile the Java jar file containing the source code to
     * be analyzed by COMTOR
     * @return Returns a true value if the submission was executed
     * successfully, false otherwise.
     */
    @Deprecated
    public boolean manual_submit(String apiKey, 
                                 File jarFile)
    {
        boolean ret = false; //was the key gen request successful
        String filedata;
        String responseLine; //line-by-line input of the HTTP response body
        JSONObject response;
        String JSONResponse = "";
        BufferedReader in = null;
        URL u = null;
        HttpURLConnection httpConn = null;
        String httpResponse = "";
        
        HttpClient client = new DefaultHttpClient();
        
        if(null==apiKey || apiKey.equals(""))
            return false;
        
        if(null==jarFile)
            return false;
                
        filedata = readJARFile(jarFile);
        
        try 
        {        
            u = new URL(COMTORCloudAPIClient.APISERV_REQUEST_URI);
            httpConn = (HttpURLConnection) u.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setRequestProperty("ENCTYPE", "multipart/form-data");
            httpConn.setRequestMethod("POST");
                   
            OutputStreamWriter writer = 
                    new OutputStreamWriter(httpConn.getOutputStream());            
            writer.write("apikey="+apiKey);
            writer.write("&response_type=email");
            writer.write("&response_format=text");
            writer.write("&interface_client=netbeans");
            writer.write("&jarfilesize="+filedata.length());
            writer.write("&jarfileexists="+jarFile.exists());
            writer.write("&jarfilename="+jarFile.getCanonicalPath());
            writer.write("&jarfile="+URLEncoder.encode(filedata.toString(), "UTF-8"));
            writer.close();
            
            if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                //NB: written so that the last line of input is treated as the key
                while ( null!=(responseLine = in.readLine()))
                {
                    JSONResponse = responseLine;                    
                }
                in.close();
                response = new JSONObject(JSONResponse);
                response.getString("html_report_url");
                response.getString("response_type");
                response.getString("uploaded file size");
                response.getString("email_from_apikey");
                response.getString("html_report_contents");
                response.getString("json_report_contents");
                response.getString("response_format");                
                response.getString("uploaded filename");
                response.getString("interface_client");
                response.getString("apikey");
                ret = true;
            }else{
                ret = false;
            }
        } catch (Exception eee) {
            System.err.println("error handling text in COMTOR email input");
        }
        return ret;   
    }

}
