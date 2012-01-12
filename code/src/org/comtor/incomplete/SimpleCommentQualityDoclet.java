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
 * $Id: SimpleCommentQualityDoclet.java,v 1.1.1.1 2006/07/10 16:22:03 locasto2 Exp $
 **************************************************************************/
package org.comtor.imcomplete;


import com.sun.javadoc.*;

/**
 * The <code>SimpleCommentQualityDoclet</code> class
 * is a small test example of how to write a {@link Doclet}
 * that basically follows the tutorial HOWTO at:
 * <p>
 * <code>http://java.sun.com/j2se/1.4.2/docs/tooldocs/javadoc/overview.html</code>
 * @author Michael Locasto
 */
public final class SimpleCommentQualityDoclet
{


   /**
    * The entry point for processing provides an
    * object to access the various attributes of all
    * the classes being documented.
    *
    * @param rootDoc  the root of the documentation tree
    * @returns some boolean value
    */
   public static boolean start(RootDoc rootDoc)
   {
      ClassDoc[] classes = rootDoc.classes();
      for(int i=0;i<classes.length;i++)
      {
         System.out.println(classes[i]);
      }
      return true;
   }
}
