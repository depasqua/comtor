/**
 *  Comment Mentor: A Comment Quality Assessment Tool
 *  Copyright (C) 2010 The College of New Jersey
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package comtor.analyzers;

import comtor.*;
import java.util.*;
import com.sun.javadoc.*;

public interface ComtorDoclet
{
  /**
   * The analyze method in each analysis module performs the analysis and
   * operates on the parsed source code (parsed by Javadoc program) as a 
   * com.sun.javadoc.RootDoc object. The method returns a property list with 
   * the grading results and the report.
	*/
	public Properties analyze(RootDoc root);

  /**
   * Sets the grading breakdown for the doclet.
   *
   * @param section The name of the section for which the max grade is set
   * @param maxGrade The maximum grade for the section
   */
  public void setGradingBreakdown(String section, float maxGrade);

 /**
  * Returns the grade for the doclet.
  * 
  * @return returns a float value of the grade for the doclet (post-processing)
  */
  public float getGrade();

 /**
  * Sets a parameter used for doclet grading.
  *
  * @param param The name of the grading parameter
  * @param value The value of the grading parameter
  */
  public void setGradingParameter(String param, String value);

 /**
  * Sets the configuration properties loaded from the config file
  *
  * @param props A Properties list object containing any module configuration
  * properties.
  */
  public void setConfigProperties(Properties props);
}
