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
 * $Id: CheckForTags.java,v 1.3 2006-10-11 15:58:40 brigand2 Exp $
 **************************************************************************/

import com.sun.javadoc.*;

/**
 * The <code>CheckForTags</code> class is a tool
 * to check for JavaDocs with returns, throw, and
 * param tags.
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
    ClassDoc[] classes = rootDoc.classes();
    MethodDoc[] methods = new MethodDoc[0];
    
    for(int i=0; i < classes.length; i++)
    {
      methods = classes[i].methods();
      for(int j=0; j < methods.length; j++)
      {
        Parameter[] parameter = new Parameter[0];
        parameter = methods[j].parameters();
        
        System.out.println("\nEntering method " + methods[j].name() + "...complete");
        
        //param tags
        String param = "@param";
        Tag[] paramTags = methods[j].tags(param);
        
        if(parameter.length==paramTags.length)
        {
          for(int r=0; r < parameter.length; r++)
          {
            System.out.println("Retrieving parameter: " + parameter[r].typeName() + " " + parameter[r].name());
            
            boolean check = false;
            for(int k=0; k < paramTags.length; k++) 
            {
              if(paramTags[k].text().startsWith(parameter[r].name()))
                check = true;
              if(check)
              {
                System.out.println("Parameter check...complete");
                break;
              }
            }
            if(!check)
              System.out.println("Parameter check...missing");
          }
        }
        else
          System.out.println("Number of parameters in the method (" + parameter.length + ") do not match the number of parameters in the comments (" + paramTags.length + ")");
        
        //return tags
        String returnParam = "@return";
        Tag[] returnTags = methods[j].tags(returnParam);
        String returntype = methods[j].returnType().typeName();
        
        System.out.print("Processing return tag...");
        if(returntype=="void")
        {
          if(returnTags.length==0)
            System.out.println("complete");
          else
            System.out.println("failed");
        }
        else
        {
          if(returnTags.length==1)
          {
            if(returnTags[0].text().startsWith(returntype))
              System.out.println("complete");
            else
              System.out.println("types differ");
          }
          else if(returnTags.length==0) 
            System.out.println("missing");
          
          else if(returnTags.length > 1)
            System.out.println("too many return tags");
        }
        
        //throws tags
        //ClassDoc[] exceptions = methods[j].thrownExceptions();
        Type[] type = methods[j].thrownExceptionTypes();
        for(int w=0; w < type.length; w++)
        {
          System.out.println(type[w].typeName());
        }
      }
    }
    return true;
  }
}