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
 * $Id: ControlFlowFeature.java,v 1.1 2006-09-08 02:00:26 locasto Exp $
 **************************************************************************/
package comtor;


import com.sun.javadoc.*;

/**
 * The <code>ControlFlowFeature</code> tests for the presence of the
 * <code>@controlflow</code> tag. This tag documents the acceptable
 * NDFA transitions for the program in the form of a regular
 * expression. The first version of this feature merely tests whether
 * or not the tag is present; further work is needed to do either
 * static or dynamic analysis and see if the regular expression
 * actually holds.
 * 
 *
 * @author Michael Locasto
 */
public final class ControlFlowFeature 
   implements FeatureTool
{

   /** The control flow tag declarator. */
   private static String m_tagName = "controlflow";

   /**
    * Entry point.
    *
    * @param cdoc  the root of the documentation tree
    * @return a report on the tag
    */
   public CommentQualityRecord process(ClassDoc cdoc)
   {
      CommentQualityRecord record = new CommentQualityRecord();
      ClassDoc[] classes = cdoc.classes();
      MethodDoc[] methods = new MethodDoc[0];
      for(int i=0;i<classes.length;i++)
      {
         methods = classes[i].methods();
         for(int j=0;j<methods.length;j++)
         {
            Tag[] tags = methods[j].tags(m_tagName);
            //note that below we only take the 1st such instance
            // in any given method... 
            if(tags.length > 0)
            {
               record.addToMessage("method: ");
               record.addToMessage(methods[j].name());
               record.addToMessage(" (");
               record.addToMessage(tags[0].text());
               record.addToMessage(" chars)");
            }
            
         }
      }
      return record;
   }
}
