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
 * $Id: FeaturePipeline.java,v 1.1 2006/09/08 02:00:26 locasto Exp $
 **************************************************************************/
package org.comtor.incomplete;

import com.sun.javadoc.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The <code>FeaturePipeline</code> class is the core
 * of the Comtor software: it defines the ordering and
 * invocation of all the tools on the Javadoc comment
 * collection.
 *
 * @author Michael Locasto
 */
public final class FeaturePipeline
{

   private ArrayList<CommentQualityRecord> m_commentQualityRecords;

   private ArrayList<FeatureTool> m_pipeline;

   private void readConfig()
   {

   }

   private void my_initialize()
   {
      

   }

   /**
    * The entry point for processing provides an
    * object to access the various attributes of all
    * the classes being documented.
    *
    * @param rootDoc  the root of the documentation tree
    * @return some boolean value
    */
   public static boolean start(RootDoc rootDoc)
   {
      ClassDoc[] classes;
      Iterator i = null;
      readConfig();
      my_initialize();
      classes = rootDoc.classes();
      i = m_pipeline.values().iterator();

      while(i.hasNext())
      {
         tool = (FeatureTool)i.next();
         for(int i=0;i<classes.length;i++)
         {
            m_commentQualityRecords.add(tool.process(classes[i]));
         }
      }

      //correlate/aggregate quality reports

      //display quality reports

      return true;
   }
}
