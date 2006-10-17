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
  * $Id: CheckForTags.java,v 1.4 2006-10-17 22:12:16 brigand2 Exp $
  **************************************************************************/

import com.sun.javadoc.*;
import java.util.*;

/**
 * The <code>CheckForTags</code> class is a tool to check
 * for JavaDocs with returns, throw, and param tags.
 *
 * @author Joe Brigandi
 */
public final class CheckForTags
{ 
  /**
   * Examine each class, obtain each method. Check for
   * returns tag. Check for throw tag. Check for param tags.
   *
   * @param rootDoc  the root of the documentation tree
   * @returns boolean value
   */
  public static boolean start(RootDoc rootDoc)
  {
    Properties prop = new Properties();
    ClassDoc[] classes = rootDoc.classes();
    
    for(int i=0; i < classes.length; i++)
    {
      prop.setProperty("" + i, "Entered class " + classes[i].name());
      MethodDoc[] methods = new MethodDoc[0];
      methods = classes[i].methods();
      
      for(int j=0; j < methods.length; j++)
      {
        Parameter[] parameter = new Parameter[0];
        parameter = methods[j].parameters();
        prop.setProperty(i + "." + j, "Entered method " + methods[j].name());
        
/////////////////param tags/////////////////
        String param = "@param";
        Tag[] paramTags = methods[j].tags(param);
        
        if(parameter.length>=paramTags.length)
        {        
          for(int r=0; r < parameter.length; r++)
          { 
            boolean check = false;
            for(int k=0; k < paramTags.length; k++) 
            {
              if(paramTags[k].text().startsWith(parameter[r].name()))
                check = true;
              if(check)
              {
                prop.setProperty(i + "." + j + ".a" + r, "Processing parameter '" + parameter[r].name() + "'...complete");
                break;
              }
            }
            if(!check)
              prop.setProperty(i + "." + j + ".a" + r, "Processing parameter '" + parameter[r].name() + "'...missing");
          }
        }
        else if(parameter.length < paramTags.length)
        {
          prop.setProperty(i + "." + j + ".a", "Processing parameters...too many param tags");
        }
        
/////////////////return tags/////////////////
        String returnParam = "@return";
        Tag[] returnTags = methods[j].tags(returnParam);
        String returntype = methods[j].returnType().typeName();
        
        if(returntype=="void")
        {
          if(returnTags.length==0)
            prop.setProperty(i + "." + j + ".b", "Processing return tag...complete");
          else
            prop.setProperty(i + "." + j + ".b", "Processing return tag...no return tag needed for void");
        }
        else
        {
          if(returnTags.length==1)
          {
            if(returnTags[0].text().startsWith(returntype))
              prop.setProperty(i + "." + j + ".b", "Processing return tag...complete");
            else
              prop.setProperty(i + "." + j + ".b", "Processing return tag...types differ");
          }
          else if(returnTags.length==0) 
            prop.setProperty(i + "." + j + ".b", "Processing return tag...missing");
          
          else if(returnTags.length > 1)
            prop.setProperty(i + "." + j + ".b", "Processing return tag...too many return tags");
        }
        
/////////////////throws tags/////////////////
        String throwParam = "@throws";
        Tag[] throwsTags = methods[j].tags(throwParam);
        ClassDoc[] exceptions = methods[j].thrownExceptions();
        
        if(exceptions.length>=throwsTags.length)
        {
          for(int s=0; s < exceptions.length; s++)
          {
            boolean check = false;
            for(int q=0; q < throwsTags.length; q++)
            {
              if(throwsTags[q].text().startsWith(exceptions[s].name()))
                check = true;
              if(check)
              {
                prop.setProperty(i + "." + j + ".c" + s, "Processing exception '" + exceptions[s].name() + "'...complete");
                break;
              }
            }
            if(!check)
              prop.setProperty(i + "." + j + ".c" + s, "Processing exception '" + exceptions[s].name() + "'...missing");
          }
        }
        else if(exceptions.length < throwsTags.length)
        {
          prop.setProperty(i + "." + j + ".c", "Processing exceptions...too many throw tags");
        }
      }
    }
    
    String[] arr = new String[0];
    arr = prop.keySet().toArray(arr);
    Arrays.sort(arr);
    
    for(int i=0; i < arr.length; i++){
      if(arr[i] != null)
        System.out.println(arr[i] + "   " + prop.getProperty("" + arr[i]));
    } 
    
    return true;
  }
}