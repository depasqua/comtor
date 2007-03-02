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
  * $Id: GenerateReport.java,v 1.10 2007-03-02 22:52:45 brigand2 Exp $
  **************************************************************************/
package comtor;

import comtor.analyzers.*;
import java.io.*;
import java.util.*;
import java.text.*;

/**
 * The <code>GenerateReport</code> class is a tool to
 * generate a report from a vector containing property lists.
 *
 * @author Joe Brigandi
 */
public class GenerateReport
{
  /**
   * Takes a vector full of property lists and
   * generates a report.
   *
   * @param v is a Vector of property lists
   */
  public void generateReport(Vector v)
  {
    try
    {
      PrintWriter prt = new PrintWriter(new FileWriter("ComtorReport.html"));            
      
      prt.println("<html>");
      prt.println("<head>"); 
      prt.println("<title>Comment Mentor</title>");
      prt.println("<style type=text/css>");
      prt.println("body { background-color: #fafad2;}");
      prt.println("h3{text-align: center; font-size: 26px}");
      prt.println("#report{border:double; padding: 10px; font-size: 20px}");
      prt.println("#links{font-size: 18px; padding: 5px; padding-left: 15px}");
      prt.println("#class{font-size: 18px; font-weight: bold}");
      prt.println("#method{padding-left:25px; padding-top:5px; font-size: 14px; font-style: italic}");
      prt.println("#comment{padding-left:50px; font-size: 12px}");
      prt.println("a{ color: #000099; text-decoration: underline; font-size: 14px;}");
      prt.println("a:visited{ color: #0000FF;}");
      prt.println("a:hover{ color: #000000; text-decoration: underline}");
      prt.println("</style>");
      prt.println("</head>");
      prt.println("<body>");
      
      DateFormat dateFormat = new SimpleDateFormat("M/d/yy h:mm a");
      java.util.Date date = new java.util.Date();
      String timeDate = dateFormat.format(date);
 
      prt.println("<a name=\"top\"></a><h3>");
      prt.println("Comment Mentor Report<br />");
      prt.println(timeDate + "<br />");
      prt.println("<a href=\"javascript:print()\">PRINT THIS PAGE!</a>");
      prt.println("</h3>");
      
      prt.println("<div id=\"links\">Reports Generated:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      for(int i=0; i < v.size(); i++)
      {
        Properties list = new Properties();
        list = (Properties)v.get(i);
        prt.println("<a href=\"#" + list.getProperty("title") + "\">" + list.getProperty("title") + "</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      }
      prt.println("</div>"); 
      
      for(int i=0; i < v.size(); i++)
      {
        Properties list = new Properties();
        list = (Properties)v.get(i);
        
        String[] arr = new String[0];
        arr = list.keySet().toArray(arr);
        Arrays.sort(arr);
        
        prt.println("<a name=\"" + list.getProperty("title") + "\"></a><div id=\"report\">");
        prt.println("Report: " + list.getProperty("title") + " (" + list.getProperty("description") + ")<br />");
        
        for(int j=0; j < arr.length-2; j++)
        {
          if(arr[j] != null)
          {
            String temp = arr[j];
            if(temp.length() == 3)
              prt.println("<hr><div id=\"class\">" + list.getProperty("" + arr[j]) + "</div>");
            else if(temp.length() == 7)
              prt.println("<div id=\"method\"><li>" + list.getProperty("" + arr[j]) + "</div>");
            else if(temp.length() > 7)
              prt.println("<div id=\"comment\">- " + list.getProperty("" + arr[j]) + "</div>");
          }
        }
        prt.println("<a href=\"#top\">Top</a></div><br /><br />");
      }
      
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