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
  * $Id: CheckForTags.java,v 1.12 2006/11/15 06:19:41 brigand2 Exp $
  **************************************************************************/
package comtor.analyzers;

import comtor.*;
import com.sun.javadoc.*;
import java.util.*;
import java.text.*;

/**
 * The <code>CheckForTags</code> class is a tool to check for
 * JavaDocs with returns, throw, and param tags.
 *
 * @author Joe Brigandi
 */
public class CheckForTags implements ComtorDoclet
{
  Properties prop = new Properties();
  HashMap<String, Float> gradeBreakdown = new HashMap<String, Float>();
  HashMap<String, Integer> possibleScore = new HashMap<String, Integer>();
  HashMap<String, Integer> points = new HashMap<String, Integer>();

  /**
   * Constructor
   */
  public CheckForTags()
  {
    // Set default values for grade breakdown
    gradeBreakdown.put("Return", new Float(4.0));
    gradeBreakdown.put("Param", new Float(5.0));
    gradeBreakdown.put("Throws", new Float(1.0));

    // Set default values for scores
    possibleScore.put("Return", new Integer(0));
    points.put("Return", new Integer(0));
    possibleScore.put("Param", new Integer(0));
    points.put("Param", new Integer(0));
    possibleScore.put("Throws", new Integer(0));
    points.put("Throws", new Integer(0));
  }

  /**
   * Examine each class, obtain each method. Check for returns tag.
   * Check for throw tag. Check for param tags.
   *
   * @param rootDoc  the root of the documentation tree
   * @returns Properties list
   */
  public Properties analyze(RootDoc rootDoc)
  {
    prop.setProperty("title", "Check for Tags"); //doclet title
//    prop.setProperty("description", "Check for proper use of Javadoc comments with return, throw, and param tags."); //doclet description
//    DateFormat dateFormat = new SimpleDateFormat("M/d/yy h:mm a");
//    java.util.Date date = new java.util.Date();
//    prop.setProperty("date", "" + dateFormat.format(date)); //doclet date & time

    ClassDoc[] classes = rootDoc.classes();

    // Count variables for score
    int return_max = 0, return_points = 0, param_max = 0, param_points = 0, throws_max = 0, throws_points = 0;

    for(int i=0; i < classes.length; i++)
    {
      String classID = numberFormat(i);
      prop.setProperty("" + classID, "Class: " + classes[i].name()); //store class name
      MethodDoc[] methods = new MethodDoc[0];
      methods = classes[i].methods();

      for(int j=0; j < methods.length; j++)
      {
        String methodID = numberFormat(j);
        prop.setProperty(classID + "." + methodID, "Method: " + methods[j].name()); //store method name

        //return tags
        String returnParam = "@return";
        Tag[] returnTags = methods[j].tags(returnParam); //return tag in documentation
        String returntype = methods[j].returnType().typeName(); //actual return type of method

        return_max += 4;

        if(returntype=="void") //if return type is void
        {
          if(returnTags.length==0) //if no tags are present in the comments
          {
            prop.setProperty(classID + "." + methodID + ".b", "Analyzed method " + methods[j].name() + "s declared return type and @return tag. The declared return type is void and there is no @return tag present in the comments.");
            return_points += 4;
          }
          else //if at least one tag is present
          {
            prop.setProperty(classID + "." + methodID + ".b", "Analyzed method " + methods[j].name() + "s declared return type and @return tag. The declared return type is void but an @return tag is present in the comments. There should be no @return tag since the declared return type is void.");
            return_points += 3;
          }
        }
        else //if return type is NOT void
        {
          if(returnTags.length==1) //if exactly one return tag is present in the comments
          {
            // Check that there is a comment
            if (returnTags[0].text().trim().equals(""))
            {
              prop.setProperty(classID + "." + methodID + ".b", "Analyzed method " + methods[j].name() + "s declared return type and @return tag. The declared return type is " + returntype + " and there is an @return tag present in the comments, but no comment");
              return_points += 2;
            }
            else
            {
              prop.setProperty(classID + "." + methodID + ".b", "Analyzed method " + methods[j].name() + "s declared return type and @return tag. The declared return type is " + returntype + " and there is an @return tag present in the comments.");
              return_points += 4;
            }
          }
          else if(returnTags.length==0) //if no tags are present
            prop.setProperty(classID + "." + methodID + ".b", "Analyzed method " + methods[j].name() + "s declared return type and @return tag. The declared return type is " + returntype + " but there is no @return tag present in the comments.");
          else if(returnTags.length > 1) //if more than one tag is present
            prop.setProperty(classID + "." + methodID + ".b", "Analyzed method " + methods[j].name() + "s declared return type and @return tag. The declared return type is " + returntype + " but there is " + returnTags.length + " @return tags present in the comments.  There should only be one @return tag.");
        }

        //param tags
        String param = "@param";
        Parameter[] parameter = new Parameter[0];
        parameter = methods[j].parameters(); //actual parameters of the method
        Tag[] paramTags = methods[j].tags(param); //param tags in documentation
        int paramCount=0;

        for(int s=0; s < parameter.length; s++) //go through list of parameters
        {
          boolean check = false;
          for(int q=0; q < paramTags.length; q++)
          {
            ParamTag ptag = (ParamTag)paramTags[q];
            if(ptag.parameterName().equals(parameter[s].name())) //if the param tag matches the actual parameter name
              check = true;
            if(check)
            {
              String count = numberFormat(paramCount);

              // Check that there is a comment
              if (ptag.parameterComment().trim().equals(""))
              {
                prop.setProperty(classID + "." + methodID + ".c" + count, "Analyzed method " + methods[j].name() + "s parameter " + parameter[s].typeName() + " " + parameter[s].name() + ". The parameter and parameter type match the @param tag in the comments, but no comment.");
                paramCount++;
              }
              else
              {
                prop.setProperty(classID + "." + methodID + ".c" + count, "Analyzed method " + methods[j].name() + "s parameter " + parameter[s].typeName() + " " + parameter[s].name() + ". The parameter and parameter type match the @param tag in the comments.");
                param_points++;
                paramCount++;
              }
              break;
            }
          }
          if(!check)
          {
            String count = numberFormat(paramCount);
            prop.setProperty(classID + "." + methodID + ".c" + count, "Analyzed method " + methods[j].name() + "s parameter " + parameter[s].typeName() + " " + parameter[s].name() + ". There is no @param tag present for this parameter.");
            paramCount++;
          }
          param_max++;
        }

        for(int s=0; s < paramTags.length; s++) //go through list of param tags
        {
          ParamTag ptag = (ParamTag)paramTags[s];

          boolean check = false;
          for(int q=0; q < parameter.length; q++)
          {
            if(ptag.parameterName().equals(parameter[q].name())) //if the param tag matches the actual parameter name
              check = true;
          }
          if(!check)
          {
            String count = numberFormat(paramCount);
            prop.setProperty(classID + "." + methodID + ".c" + count, "Analyzed method " + methods[j].name() + "s @param tag " + paramTags[s].text() + ". There is no parameter in the method for the this @param tag.");
            paramCount++;
            param_max++;  // Add one to the maximum points for parameter so that it lowers the score
          }
        }

        //throws tags
        String throwParam = "@throws";
        Tag[] throwsTags = methods[j].tags(throwParam); //throw tags in the documentation
        ClassDoc[] exceptions = methods[j].thrownExceptions(); //actual exceptions for the method
        int throwsCount=0;

        for(int s=0; s < exceptions.length; s++) //go through list of exceptions
        {
          boolean check = false;
          for(int q=0; q < throwsTags.length; q++)
          {
            ThrowsTag ttag = (ThrowsTag)throwsTags[q];
            if(ttag.exceptionName().equals(exceptions[s].name())) //if the throws tag matches the actual exception in the method
              check = true;
            if(check)
            {
              String num = numberFormat(throwsCount); //format number to 3 digits

              // Check that there is a comment
              if (ttag.exceptionComment().trim().equals(""))
                prop.setProperty(classID + "." + methodID + ".d" + num, "Analyzed method " + methods[j].name() + "s exception " + exceptions[s].name() + ". The exception matches the @throws tag in the comments, but no comment.");
              else
              {
                prop.setProperty(classID + "." + methodID + ".d" + num, "Analyzed method " + methods[j].name() + "s exception " + exceptions[s].name() + ". The exception matches the @throws tag in the comments.");
                throws_points++;
              }
              throwsCount++;
              break;
            }
          }
          if(!check)
          {
            String num = numberFormat(throwsCount); //format number to 3 digits
            prop.setProperty(classID + "." + methodID + ".d" + num, "Analyzed method " + methods[j].name() + "s exception " + exceptions[s].name() + ". There is no @throws tag present for this exception.");
            throwsCount++;
          }
          throws_max++;
        }

        for(int s=0; s < throwsTags.length; s++) //go through list of throw tags
        {
          ThrowsTag ttag = (ThrowsTag)throwsTags[s];

          boolean check = false;
          for(int q=0; q < exceptions.length; q++)
          {
            if (ttag.exceptionName().equals(exceptions[q].name())) //if the throws tag matches the actual exception in the method
              check = true;
          }
          if(!check)
          {
            String num = numberFormat(throwsCount); //format number to 3 digits
            prop.setProperty(classID + "." + methodID + ".d" + num, "Analyzed method " + methods[j].name() + "s @throws tag " + throwsTags[s].text() + ". There is no exception in the method for this @throws tag.");
            throwsCount++;
            throws_max++;  // Add one to the maximum points for throws so that it lowers the score
          }
        }
      }
    }

    // Set the points in the map
    possibleScore.put("Return", new Integer(return_max));
    points.put("Return", new Integer(return_points));
    possibleScore.put("Param", new Integer(param_max));
    points.put("Param", new Integer(param_points));
    possibleScore.put("Throws", new Integer(throws_max));
    points.put("Throws", new Integer(throws_points));

    prop.setProperty("score", "" + getGrade());

    return prop;
  }

  //used to convert an integer value to 3 digits (ex. 7 --> 007)
  private String numberFormat(int value)
  {
    String newValue;
    if(value<10)
      newValue = "00" + value;
    else if(value<100)
      newValue = "0" + value;
    else
      newValue = "" + value;

    return newValue;
  }

  /*************************************************************************
  * Sets the grading breakdown for the doclet.
  *
  * @param section Name of the section to set the max grade for
  * @param maxGrade Maximum grade for the section
  *************************************************************************/
  public void setGradingBreakdown(String section, float maxGrade)
  {
    gradeBreakdown.put(section, new Float(maxGrade));
  }

  /*************************************************************************
  * Returns the grade for the doclet.
  *************************************************************************/
  public float getGrade()
  {
    int score, possible;
    float max, total = (float)0.0, percent;

    String sections[] = {"Return", "Param", "Throws"};

    for (int i = 0; i < sections.length; i++)
    {
      possible = possibleScore.get(sections[i]);
      score = points.get(sections[i]);
      max = gradeBreakdown.get(sections[i]);

      percent = (possible == 0) ? (float)1.0 : (float)score/possible;
      total += percent * max;
    }

    return total;
  }

  /*************************************************************************
  * Sets a parameter used for doclet grading.
  *
  * @param param Name of the grading parameter
  * @param value Value of the parameter
  *************************************************************************/
  public void setGradingParameter(String param, String value)
  {
    // No customizable parameters
  }
}
