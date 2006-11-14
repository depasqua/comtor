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
 * $Id: CommentAvgRatio.java,v 1.7 2006-11-14 21:41:20 brigand2 Exp $
 **************************************************************************/

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
	prop.setProperty("description", "This doclet calculates the number of characters for each method's comments in the class.");
	DateFormat dateFormat = new SimpleDateFormat("M/d/yy h:mm a");
	java.util.Date date = new java.util.Date();
	prop.setProperty("date", "" + dateFormat.format(date));
	
    ClassDoc[] classes = rootDoc.classes();
    
    for(int i=0; i < classes.length; i++)
    {
      prop.setProperty("" + i, "class: " + classes[i].name());
      long total=0;
      
      MethodDoc[] methods = new MethodDoc[0];
      methods = classes[i].methods();
      for(int j=0; j < methods.length; j++)
      {
        commentLength = methods[j].getRawCommentText().length();
        if(j<10)
          prop.setProperty(i + ".00" + j, "The length of comments for the method '" + methods[j].name() + "' is " + commentLength + " characters.");
        else if(j<100)
          prop.setProperty(i + ".0" + j, "The length of comments for the method '" + methods[j].name() + "' is " + commentLength + " characters.");
        else
          prop.setProperty(i + "." + j, "The length of comments for the method '" + methods[j].name() + "' is " + commentLength + " characters.");
        
        total+=commentLength;
      }
      long average=0;
      average = total/methods.length;
      if(methods.length<10)
        prop.setProperty(i + ".00" + methods.length, "The average length of comments for the class '" + classes[i].name() + "' is " + average + " characters.");
      else if(methods.length<100)
        prop.setProperty(i + ".0" + methods.length, "The average length of comments for the class '" + classes[i].name() + "' is " + average + " characters.");
      else
        prop.setProperty(i + "." + methods.length, "The average length of comments for the class '" + classes[i].name() + "' is " + average + " characters.");
    }
    return prop;
  }
}