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
 * $Id: CommentAvgRatio.java,v 1.1 2006-10-11 15:53:37 brigand2 Exp $
 **************************************************************************/

import com.sun.javadoc.*;

/**
 * The <code>CommentAvgRatio</code> class is a tool to
 * measure the average ratio of comment length/size to
 * method length/size.
 *
 * @author Joe Brigandi
 */
public final class CommentAvgRatio
{
  
  /**
   * Examine each class, obtain each method. Get comment length.
   * Get method length. Calculate ratio of comment length to 
   * method length. Calculate average of the ratios.
   *
   * @param rootDoc  the root of the documentation tree
   * @returns boolean value
   */
  public static boolean start(RootDoc rootDoc)
  {
    long commentLength = 0;
    long methodLength = 0;
    long ratio = 0;
    long avgRatio = 0;
    ClassDoc[] classes = rootDoc.classes();
    MethodDoc[] methods = new MethodDoc[0];
    
    for(int i=0; i < classes.length; i++)
    {
      methods = classes[i].methods();
      for(int j=0; j < methods.length; j++)
      {
        commentLength = methods[j].getRawCommentText().length();
        methodLength = methods[j].length();
        ratio = commentLength/methodLength;
        System.out.println("method: " + methods[j].name() + " ("+ratio+" chars)");
        avgRatio+=ratio;
      }
    }
    System.out.println("average ratio of comment length to method length: " + avgRatio);
    return true;
  }
}