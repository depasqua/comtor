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
 * $Id: CommentQualityRecord.java,v 1.1 2006-09-08 02:00:26 locasto Exp $
 **************************************************************************/
package comtor;

/**
 * The <code>CommentQualityRecord</code> is the parent class
 * that defines the data returned by a {@link FeatureTool}.
 * There is NO 1to1 correspondence between a FeatureTool and a
 * subclass of this class. Although such a mapping may naturally
 * occur in practice, it is not a requirement of the system. For
 * most purposes, this class should serve nicely.
 *
 * @author Michael Locasto
 */
public class CommentQualityRecord
{
   public static final int CONTENT_TYPE = 100;
   public static final int STYLE_TYPE = 200;

   protected String m_identifier = this.getClass().getName();
   protected StringBuffer m_message;
   protected int m_category = CommentQualityRecord.CONTENT_TYPE;

   public void setCategory(int category)
      throws IllegalArgumentException
   {
      switch(category)
      {
      case CommentQualityRecord.CONTENT_TYPE:
         m_category = category;
         break;
      case CommentQualityRecord.STYLE_TYPE:
         m_category = category;
         break;
      default:
         //unrecgonized type, default to CONTENT_TYPE
         throw new IllegalArgumentException("Unrecognized comment record type.");
         break;
      }
   }

   public int getCategory()
   {
      return m_category;
   }

   /** 
    * Major assumption: the only way that m_category was set was
    * through the accessor, thus it must have a legal value, so
    * the default case should NEVER be invoked, but it may be if
    * the dev forgets to add a case for a new comment category.
    */
   public String getCategoryAsString()      
   {
      switch(category)
      {
      case CommentQualityRecord.CONTENT_TYPE:
         return "CONTENT_TYPE";
         break;
      case CommentQualityRecord.STYLE_TYPE:
         return "STYLE_TYPE";
         break;
      default:
         return "Unrecognized comment record type.";
         //perhaps throw illegalStateException
         break;
      }
      
   }

   public void addToMessage(String s)
   {
      m_message.append(s);
   }

   public String getMessage()
   {
      return m_message.toString();
   }

   public String getName()
   {
      return m_identifier;
   }
}
