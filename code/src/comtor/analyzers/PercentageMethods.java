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
  * $Id: PercentageMethods.java,v 1.4 2006/11/14 21:41:20 brigand2 Exp $
  **************************************************************************/
package comtor.analyzers;

import comtor.*;
import com.sun.javadoc.*;
import java.util.*;
import java.text.*;

/**
 * The <code>PercentageMethods</code> class is a tool to
 * measure a the percentage of methods in a class that are
 * documented with a JavaDoc header.
 *
 * @author Michael Locasto
 */
public class PercentageMethods implements ComtorDoclet
{
  Properties prop = new Properties();
  
  /**
   * Examine each class, obtain each method. See if rawComment text
   * has a length more than zero. If so, count it in the total frequency
   * of commented methods for that class. Obtain percentage of commented
   * methods per class.
   *
   * @param rootDoc  the root of the documentation tree
   * @returns some boolean value
   */
  public Properties analyze(RootDoc rootDoc)
  {
    prop.setProperty("title", "Percentage Methods");
    prop.setProperty("description", "Calculate the percentage of commented methods in a given class.");
    DateFormat dateFormat = new SimpleDateFormat("M/d/yy h:mm a");
    java.util.Date date = new java.util.Date();
    prop.setProperty("date", "" + dateFormat.format(date)); 
    
    int methodsCommented = 0;
    double percentCommented = 0.0;
    ClassDoc[] classes = rootDoc.classes();
    MethodDoc[] methods = new MethodDoc[0];
    for(int i=0;i<classes.length;i++)
    {
      String classID = numberFormat(i);
      prop.setProperty("" + classID, "Class: " + classes[i].name());
      methods = classes[i].methods();
      for(int j=0;j<methods.length;j++)
      {
        if(methods[j].getRawCommentText().length() > 0)
          methodsCommented++;
      }
      if(0!=methods.length)
      {
        percentCommented = (double)((1.0*methodsCommented)/methods.length);
        prop.setProperty("" + classID + ".000", Math.round(percentCommented*100) + " percent (" + methodsCommented + "/" + methods.length + ") of class " + classes[i].name() + "s methods are commented.");
      }
      else
        prop.setProperty("" + classID + ".000", "Class: " + classes[i].name() + "has no JavaDoc\'d methods.");
      
      methodsCommented = 0;
      percentCommented = 0.0;         
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
