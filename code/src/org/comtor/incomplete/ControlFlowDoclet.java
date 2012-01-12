/***************************************************************************
 *  Comment Mentor: A Comment Quality Assessment Tool
 *  Copyright (C) 2006 Michael E. Locasto
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
 * $Id: ControlFlowDoclet.java,v 1.1 2006/08/16 22:24:02 locasto Exp $
 **************************************************************************/
package org.comtor.incomplete;


import com.sun.javadoc.*;

/**
 * 
 * 
 * 
 *
 * @author Michael Locasto
 */
public final class ControlFlowDoclet
{

   /** The control flow tag declarator. */
   private static String m_tagName = "controlflow";


   /**
    * Entry point.
    *
    * @param rootDoc  the root of the documentation tree
    * @returns some boolean value
    */
   public static boolean start(RootDoc rootDoc)
   {
      ClassDoc[] classes = rootDoc.classes();
      MethodDoc[] methods = new MethodDoc[0];
      for(int i=0;i<classes.length;i++)
      {
         methods = classes[i].methods();
         for(int j=0;j<methods.length;j++)
         {
            Tag[] tags = methods[j].tags(m_tagName);
            if(tags.length > 0)
            {
               System.out.println("method: "+methods[j].name()
                                  +" ("+tags[0].text()+" chars)");
            }
            
         }
      }
      return true;
   }
}
