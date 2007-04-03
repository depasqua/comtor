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
  * $Id: CommentAvgRatio.java,v 1.8 2006/11/14 22:43:16 brigand2 Exp $
  **************************************************************************/
package comtor.analyzers;

import comtor.*;
import com.sun.javadoc.*;
import java.util.*;
import java.text.*;

/**
 * The <code>CommentAvgRatio</code> class is a tool to
 * measure the average length of comments for methods
 * in a class.
 *
 * @author Joe Brigandi
 */
public final class CommentAvgRatio implements ComtorDoclet
{
  Properties prop = new Properties();
  long commentLength=0;
  
  /**
   * Examine each class and its methods. Calculate the length 
   * of each method's comments. 
   *
   * @param rootDoc  the root of the documentation tree
   * @returns Properties list
   */
  public Properties analyze(RootDoc rootDoc)
  {
    prop.setProperty("title", "Comment Average Ratio");
    prop.setProperty("description", "Calculate the length of each method's comments in a given class.");
    DateFormat dateFormat = new SimpleDateFormat("M/d/yy h:mm a");
    java.util.Date date = new java.util.Date();
    prop.setProperty("date", "" + dateFormat.format(date));
    
    ClassDoc[] classes = rootDoc.classes();
    
    for(int i=0; i < classes.length; i++)
    {
      String classID = numberFormat(i);
      prop.setProperty("" + classID, "Class: " + classes[i].name());
      int total=0;
      
      MethodDoc[] methods = new MethodDoc[0];
      methods = classes[i].methods();
      for(int j=0; j < methods.length; j++)
      {
        String methodID = numberFormat(j);
        Scanner scan = new Scanner(methods[j].getRawCommentText());
        int count=0;
        while(scan.hasNext()) 
        { 
          scan.next();
          count++;
        }
        commentLength = count;
        prop.setProperty(classID + "." + methodID, "The length of comments for the method '" + methods[j].name() + "' is " + commentLength + " words.");
        total+=commentLength;
      }
      
      String methodLength = numberFormat(methods.length);
      if(methods.length==0)
        prop.setProperty(i + "." + methodLength, "There are no methods in the class '" + classes[i].name() + "'.");
      else{
        int average = total/methods.length;
        prop.setProperty(classID + "." + methodLength, "The average length of comments for the class '" + classes[i].name() + "' is " + average + " words.");
      }
    }
    return prop;
  }
  
  private String numberFormat(int value)
  {
    String newValue;
    if(value<10)
      newValue = "00" + value;
    else if(value<100)
      newValue = "0" + value;
    else
      newValue = "" + value;
    
    return newValue;
  }
}