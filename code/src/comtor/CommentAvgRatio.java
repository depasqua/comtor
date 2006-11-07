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
 * $Id: CommentAvgRatio.java,v 1.4 2006-11-07 04:32:34 brigand2 Exp $
 **************************************************************************/

import com.sun.javadoc.*;
import java.util.*;

/**
 * The <code>CommentAvgRatio</code> class is a tool to
 * measure the average ratio of comment length/size to
 * method length/size.
 *
 * @author Joe Brigandi
 */
public final class CommentAvgRatio
{
  Properties prop = new Properties();
  long commentLength=0;
  
  /**
   * Examine each class and its methods. Calculate the length 
   * of each method's comments. 
   *
   * @param rootDoc  the root of the documentation tree
   * @returns boolean value
   */
  public Properties analyze(RootDoc rootDoc)
  {
    ClassDoc[] classes = rootDoc.classes();
    
    for(int i=0; i < classes.length; i++)
    {
      long total=0;
      
      MethodDoc[] methods = new MethodDoc[0];
      methods = classes[i].methods();
      for(int j=0; j < methods.length; j++)
      {
        commentLength = methods[j].getRawCommentText().length();
        prop.setProperty(i + "." + j, "The length of comments for the method '" + methods[j].name() + "' is " + commentLength + " characters.");
        total+=commentLength;
      }
      long average=0;
      average = total/methods.length;
      prop.setProperty("" + i, "The average length of comments for the class '" + classes[i].name() + "' is " + average + " characters.");
    }
    return prop;
  }
}