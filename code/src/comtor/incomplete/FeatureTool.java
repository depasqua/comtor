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
 * $Id: FeatureTool.java,v 1.1 2006/09/08 02:00:26 locasto Exp $
 **************************************************************************/
package comtor;

import com.sun.javadoc.*;

/**
 * The <code>FeatureTool</code> is an interface that
 * defines the service offered by a feature measurement
 * tool. A tool takes in a {@link ClassDoc} object and
 * returns a {@link CommentQualityReport}.
 *
 * @author Michael Locasto
 */
public interface FeatureTool
{
   /**
    * Generate a comment quality report for a class.
    */
   public CommentQualityRecord process(ClassDoc cdoc);
}
