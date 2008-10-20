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
 * $Id:$
 **************************************************************************/
package comtor.analyzers;

import comtor.*;
import java.util.*;
import com.sun.javadoc.*;

public interface ComtorDoclet
{
  //the analyze method runs each doclet on the RootDoc
  //it returns a property list with the results
  public Properties analyze(RootDoc root);

  /*************************************************************************
  * Sets the grading breakdown for the doclet.
  *
  * @param section Name of the section to set the max grade for
  * @param maxGrade Maximum grade for the section
  *************************************************************************/
  public void setGradingBreakdown(String section, float maxGrade);

  /*************************************************************************
  * Returns the grade for the doclet.
  *************************************************************************/
  public float getGrade();

  /*************************************************************************
  * Sets a parameter used for doclet grading.
  *
  * @param param Name of the grading parameter
  * @param value Value of the parameter
  *************************************************************************/
  public void setGradingParameter(String param, String value);
}
