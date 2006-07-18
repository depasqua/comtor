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
 *	 Free Software Foundation, Inc.
 *	 59 Temple Place, Suite 330 
 *	 Boston, MA  02111-1307  USA
 *
 * $Id: PercentageMethods.java,v 1.2 2006-07-18 16:46:19 locasto Exp $
 **************************************************************************/
package comtor;


import com.sun.javadoc.*;

/**
 * The <code>PercentageMethods</code> class is a tool to
 * measure a the percentage of methods in a class that are
 * documented with a JavaDoc header.
 *
 * @author Michael Locasto
 */
public final class PercentageMethods
{


   /**
    * Examine each class, obtain each method. See if rawComment text
    * has a length more than zero. If so, count it in the total frequency
    * of commented methods for that class. Obtain percentage of commented
    * methods per class.
    *
    * @param rootDoc  the root of the documentation tree
    * @returns some boolean value
    */
   public static boolean start(RootDoc rootDoc)
   {
      long methodsCommented = 0L;
      double percentCommented = 0.0;
      ClassDoc[] classes = rootDoc.classes();
      MethodDoc[] methods = new MethodDoc[0];
      for(int i=0;i<classes.length;i++)
      {
         methods = classes[i].methods();
         for(int j=0;j<methods.length;j++)
         {
            if(methods[j].getRawCommentText().length() > 0)
            {
               methodsCommented++;
            }
         }
         if(0!=methods.length)
         {
            percentCommented = (double)((1.0*methodsCommented)/methods.length);
            System.out.format("%3.3f percent (%d/%d) of class %s\'s methods are commented.%n", percentCommented, methodsCommented, methods.length, classes[i]);
            /*
            System.out.println(percentCommented
                               +" percent ("+methodsCommented
                               +"/"
                               +methods.length
                               +") of class " 
                               +classes[i]
                               +"\'s methods are commented.");
            */
         }else{
            System.out.println("class " 
                               +classes[i]
                               +"has no JavaDoc\'d methods.");
         }
         percentCommented = 0.0;
         methodsCommented = 0;         
      }
      return true;
   }
}
