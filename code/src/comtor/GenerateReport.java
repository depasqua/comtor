/***************************************************************************
  *  Comment Mentor: A Comment Quality Assessment Tool
  *  Copyright (C) 2005 Michael E. Locasto
  *
  *  This program is free software; you can redistribute it and/or modify
  *  it under the terms of the GNU General Public License as published by
  *  the Free Software Foundation; either version 2 of the License, or
  *  (at your option) any later version.
  *
  *  This program is distributed in the hope that it will be useful, but
  *  WITHOUT ANY WARRANTY; without even the implied warranty of 
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
  *  General Public License for more details.
  *
  *  You should have received a copy of the GNU General Public License
  *  along with this program; if not, write to the:
  *  Free Software Foundation, Inc.
  *  59 Temple Place, Suite 330 
  *  Boston, MA  02111-1307  USA
  *
  * $Id: GenerateReport.java,v 1.16 2007-05-02 22:32:36 brigand2 Exp $
  **************************************************************************/
package comtor;

import comtor.analyzers.*;
import java.io.*;
import java.util.*;
import java.text.*;

/**
 * The <code>GenerateReport</code> class is a tool to generate a report
 * from a vector containing property lists.
 *
 * @author Joe Brigandi
 */
public class GenerateReport
{
  /**
   * Takes a vector full of property lists and generates a report.
   *
   * @param v is a Vector of property lists
   */
  public void generateReport(Vector v)
  {
    try
    {
      PrintWriter prt = new PrintWriter(new FileWriter("ComtorReport.php")); //write to ComtorReport.php         
      
      // HTML and CSS code for the report
      prt.println("<html>");
      prt.println("<head>"); 
      prt.println("<title>Comment Mentor</title>");
      prt.println("<style type=text/css>");
      prt.println("body { background-color: #fafad2;}");
      prt.println("h3{text-align: center; font-size: 26px}");
      prt.println("#report{border:double; padding: 10px; font-size: 20px}");
      prt.println("#links{font-size: 18px; padding: 5px; padding-left: 15px}");
      prt.println("#footer{font-size: 14px; padding-left: 10px;}");
      prt.println("#class{font-size: 18px; font-weight: bold}");
      prt.println("#method{padding-left:25px; padding-top:5px; font-size: 14px; font-style: italic}");
      prt.println("#comment{padding-left:50px; font-size: 12px}");
      prt.println("a{ color: #000099; text-decoration: underline; font-size: 14px;}");
      prt.println("a:visited{ color: #0000FF;}");
      prt.println("a:hover{ color: #000000; text-decoration: underline}");
      prt.println("</style>");
      prt.println("<script language=\"javascript\">");
      prt.println("function toggleDiv(divid){"); //the toggleDiv functions allows us to hide the source code
      prt.println("if(document.getElementById(divid).style.display == 'none'){");
      prt.println("document.getElementById(divid).style.display = 'block';");
      prt.println("}else{");
      prt.println("document.getElementById(divid).style.display = 'none';");
      prt.println("}}");
      prt.println("</script>");
      prt.println("</head>");
      prt.println("<body>");
      
      //connect to the MySQL database
      prt.println("<?");
      prt.println("mysql_connect('localhost', 'brigand2', 'joeBrig');");
      prt.println("mysql_select_db('comtor');");
      prt.println("?>");
      
      DateFormat dateFormat = new SimpleDateFormat("M/d/yy h:mm a");
      java.util.Date date = new java.util.Date();
      String timeDate = dateFormat.format(date);
      
      prt.println("<a name=\"top\"></a><h3>");
      prt.println("Comment Mentor Report<br />");
      prt.println(timeDate + "<br />");
      prt.println("<a href=\"javascript:print()\">PRINT THIS PAGE!</a>"); //print option
      prt.println("</h3>");
      
      String dir = System.getProperty("user.dir"); //current working directory
      BufferedReader rd = new BufferedReader(new FileReader(dir + "/userID.txt")); //read userID.txt to get userID
      String userID = rd.readLine(); //store userID from text file 
      rd.close();      
      
      BufferedReader br = new BufferedReader(new FileReader(dir + "/source.txt")); //read source.txt to get list of source code files to analyze
      String line;
      prt.println("Java Source Code...<br />");
      while((line = br.readLine()) != null)   
      {
        prt.println("<a href=\"javascript:;\" onmousedown=\"toggleDiv('" + line + "');\">" + line + "</a><br />"); //create div for source file
        
        prt.println("<div id=\"" + line + "\" style=\"display:none\">");
        
        BufferedReader in = new BufferedReader(new FileReader(line));
        String str;
        while ((str = in.readLine()) != null) {
          prt.println("<pre>" + str + "</pre>"); //pre tags fix formatting of source code
        }
        in.close();
        
        prt.println("</div>"); //end div
      }
      br.close();
      prt.println("<br />");
      
      //show list of source files to link back to
      prt.println("<div id=\"links\">Reports Generated:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      for(int i=0; i < v.size(); i++)
      {
        Properties list = new Properties();
        list = (Properties)v.get(i);
        prt.println("<a href=\"#" + list.getProperty("title") + "\">" + list.getProperty("title") + "</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      }
      prt.println("</div>"); 
      
      for(int i=0; i < v.size(); i++) //go through vector to access property lists (one for each doclet)
      {
        Properties list = new Properties();
        list = (Properties)v.get(i);
        
        //store property list in an array
        String[] arr = new String[0];
        arr = list.keySet().toArray(arr);
        Arrays.sort(arr); //sort array
        
        String report = list.getProperty("title");
        prt.println("<a name=\"" + list.getProperty("title") + "\"></a><div id=\"report\">"); //doclet title
        prt.println("Report: " + list.getProperty("title") + " (" + list.getProperty("description") + ")<br />"); //doclet header (title & description)
        prt.println("<?");
        prt.println("$select = mysql_query(\"SELECT reportID FROM reports WHERE reportName='" + report + "'\");"); //query database to get the id of the doclet
        prt.println("$row = mysql_fetch_array($select);");
        prt.println("$reportID = $row['reportID'];");
        prt.println("?>");
        
        for(int j=0; j < arr.length-2; j++) //go through array of property list data pairs (length is -2 cause we don't need the title, description, & dateTime)
        {
          if(arr[j] != null)
          {
            String temp = arr[j];
            if(temp.length() == 3) //if the index is length 3, then it is a class name (ex. 002)
              prt.println("<hr><div id=\"class\">" + list.getProperty("" + arr[j]) + "</div>");
            else if(temp.length() == 7) //if the index is length 7, then it is a method name (ex. 002.011)
              prt.println("<div id=\"method\"><li>" + list.getProperty("" + arr[j]) + "</div>");
            else if(temp.length() > 7) //if the index is length 8 or more, then it is an attribute (ex. 002.011.a)
              prt.println("<div id=\"comment\">- " + list.getProperty("" + arr[j]) + "</div>");
            
            //insert data pair into the database
            prt.println("<?");
            prt.println("mysql_query(\"INSERT INTO data(userID, reportID, attribute, value) VALUES ('" + userID + "', '$reportID', '" + arr[j] + "', '" + list.getProperty("" + arr[j]) + "')\");");
            prt.println("?>");
          }
        }
        prt.println("<a href=\"#top\">Top</a></div><br /><br />");
      }
      
      prt.println("<div id=\"footer\">Related Links:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"); //related links
      prt.println("<a href=\"http://java.sun.com/j2se/1.3/docs/tooldocs/win32/javadoc.html#javadoctags\">Javadoc Tags</a>");
      prt.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"http://java.sun.com/j2se/javadoc/\">Javadoc Tutorial</a></div>");
      prt.println("</body>");
      prt.println("</html>");
      
      prt.close();
    }
    
    catch(Exception ex) 
    {
      System.err.print("!!!An exception was thrown!!!");
      System.err.println(ex.toString());
    }
  }
}