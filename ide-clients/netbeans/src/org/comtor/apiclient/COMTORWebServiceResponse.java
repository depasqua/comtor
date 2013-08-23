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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class encapsulates a response object from the COMTORCloudAPIClient's
 * submit() method.
 * 
 * It also contains a convienience method fir wrapping the plaintext
 * result in simple HTML with &lt;pre&gt; tags.
 *
 * @author Michael E. Locasto
 */
public final class COMTORWebServiceResponse 
{
    private String m_raw;
    private JSONObject response;
    
    private String response_type;
    private String response_format;

    private String uploaded_filename;
    private String uploaded_file_size; //file size of submitted JAR file
    private String email_from_apikey; //email bound to m_apikey
    private String apikey; //key used in request
    private String interface_client;
    
    private String html_report_url; //option, present only for HTML replies
    private String html_report_contents; //option
    private String json_report_contents; //option
    private String text_report_contents; //option

    public boolean isErr = false;
    public String errmesg;
    
    /**
     * Successful responses will want to create the object via this
     * constructor.
     */
    public COMTORWebServiceResponse()
    {
        isErr = false;
        errmesg = null;
    }
    
    /** 
     * A convieniance constructor for creating an "error" version of the
     * response.
     * 
     * Takes a single string argument specifying the problem. Automatically
     * sets the isErr field to true.
     */
    public COMTORWebServiceResponse(String errorMessage)
    {
        isErr = true;
        errmesg = errorMessage;
    }
    
    /**
     * @return the raw content
     */
    public String get_raw() {
        return m_raw;
    }

    /**
     * @param m_raw the m_raw to set
     */
    public void set_raw(String m_raw) {
        this.m_raw = m_raw;
    }

    /**
     * Populate all the fields of this object from the "m_raw" field by asking
     * JSON to parse the raw content for us and then we individually load the
     * fields.
     * 
     * @precondition the "m_raw" field has been set
     * @postcondition all valid fields are set, "m_raw" remains unmodified
     * 
     * @return true if all required fields loaded successfully, false if an
     *  error occurred
     */
    public boolean loadFromRaw()
    {
        boolean ret = false;
        
        //we can't trust that all these fields are present, but for now
        //we are optimisitic
        try{
            response = new JSONObject(m_raw);
            response_type = response.optString("response_type");
            uploaded_file_size = response.optString("uploaded file size");
            email_from_apikey = response.optString("email_from_apikey");
            response_format = response.optString("response_format");
            uploaded_filename = response.optString("uploaded filename");
            apikey = response.optString("apikey");
            interface_client = response.getString("interface_client");
            
            html_report_url = response.optString("html_report_url");
            html_report_contents = response.optString("html_report_contents");
            text_report_contents = response.optString("text_report_contents");
            json_report_contents = response.optString("json_report_contents");
            
            ret = true;
        }catch(JSONException jsonex){
            System.err.println("JSON extraction / translation unsuccessful");
            System.err.println(jsonex.getMessage());
            ret = false;
        }

        return ret;
    }
    
    /**
     * @return the response
     */
    public JSONObject getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(JSONObject response) {
        this.response = response;
    }

    /**
     * @return the response_type
     */
    public String getResponse_type() {
        return response_type;
    }

    /**
     * @param response_type the response_type to set
     */
    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    /**
     * @return the response_format
     */
    public String getResponse_format() {
        return response_format;
    }

    /**
     * @param response_format the response_format to set
     */
    public void setResponse_format(String response_format) {
        this.response_format = response_format;
    }

    /**
     * @return the uploaded_filename
     */
    public String getUploaded_filename() {
        return uploaded_filename;
    }

    /**
     * @param uploaded_filename the uploaded_filename to set
     */
    public void setUploaded_filename(String uploaded_filename) {
        this.uploaded_filename = uploaded_filename;
    }

    /**
     * @return the uploaded_file_size
     */
    public String getUploaded_file_size() {
        return uploaded_file_size;
    }

    /**
     * @param uploaded_file_size the uploaded_file_size to set
     */
    public void setUploaded_file_size(String uploaded_file_size) {
        this.uploaded_file_size = uploaded_file_size;
    }

    /**
     * @return the email_from_apikey
     */
    public String getEmail_from_apikey() {
        return email_from_apikey;
    }

    /**
     * @param email_from_apikey the email_from_apikey to set
     */
    public void setEmail_from_apikey(String email_from_apikey) {
        this.email_from_apikey = email_from_apikey;
    }

    /**
     * @return the apikey
     */
    public String getApikey() {
        return apikey;
    }

    /**
     * @param apikey the apikey to set
     */
    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    /**
     * @return the interface_client
     */
    public String getInterface_client() {
        return interface_client;
    }

    /**
     * @param interface_client the interface_client to set
     */
    public void setInterface_client(String interface_client) {
        this.interface_client = interface_client;
    }

    /**
     * @return the html_report_contents
     */
    public String getHtml_report_contents() {
        return html_report_contents;
    }

    /**
     * @param html_report_contents the html_report_contents to set
     */
    public void setHtml_report_contents(String html_report_contents) {
        this.html_report_contents = html_report_contents;
    }

    /**
     * @return the json_report_contents
     */
    public String getJson_report_contents() {
        return json_report_contents;
    }

    /**
     * @param json_report_contents the json_report_contents to set
     */
    public void setJson_report_contents(String json_report_contents) {
        this.json_report_contents = json_report_contents;
    }

    /**
     * @return the text_report_contents
     */
    public String getText_report_contents() {
        return text_report_contents;
    }

    /**
     * Wrap the text report contents in valid HTML with pre tags.
     * 
     */
    public String getTextReportContentsAsXML()
    {
        StringBuilder sb = new StringBuilder(text_report_contents.length());
        //sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
        //sb.append(" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        //sb.append("\n");
        //sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
        sb.append("<html><body>\n");
        sb.append("<pre>\n");
        sb.append(text_report_contents);
        sb.append("\n</pre>\n");
        sb.append("</body></html>\n");
        return sb.toString();
    }
    
    /**
     * @param text_report_contents the text_report_contents to set
     */
    public void setText_report_contents(String text_report_contents) {
        this.text_report_contents = text_report_contents;
    }

    /**
     * @return the html_report_url
     */
    public String getHtml_report_url() {
        return html_report_url;
    }

    /**
     * @param html_report_url the html_report_url to set
     */
    public void setHtml_report_url(String html_report_url) {
        this.html_report_url = html_report_url;
    }
    
}
