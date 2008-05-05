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
  * $Id: GenerateReport.java,v 1.19 2008-05-05 22:35:23 ssigwart Exp $
  **************************************************************************/
package comtor;

import com.sun.javadoc.*;
import comtor.analyzers.*;
import java.util.*;
import java.io.*;

/**
 * The <code>ComtorDriver</code> class is a tool to run doclets
 * and pass a vector of property lists to the report generator.
 *
 * @author Joe Brigandi
 */
public class ComtorDriver
{ 
  /**
   * Accepts a property list from the called doclets and puts them
   * in a vector. It then passes the  vector to the report generator.
   *
   * @param rootDoc  the root of the documentation tree
   * @returns boolean value
   */
  public static boolean start(RootDoc rootDoc)
  {
    try
    {
      Vector v = new Vector(); // create new vector
      Scanner scan = new Scanner(new File("Doclets.txt")); //read list of doclets
      String docletName;
      while(scan.hasNext())
      {
        docletName = scan.nextLine(); //store doclet as docletName
        Class c = Class.forName(docletName); //create class for doclet
        ComtorDoclet cd = (ComtorDoclet) c.newInstance(); //create new instance of the class
        Properties list = cd.analyze(rootDoc); //call the analyze method to run Javadoc
        v.addElement(list); //add the resulting property list to the vector
      }
      
      GenerateReport report = new GenerateReport(); 
      report.generateReport(v); //pass the vector to the generate report class
      
      scan.close();
    }
    
    //exceptions
    catch(ClassNotFoundException cnfe)
    {
      System.err.println(cnfe.toString());
    }
    catch(InstantiationException ie)
    {
      System.err.println(ie.toString());
    }
    catch(IllegalAccessException iae)
    {
      System.err.println(iae.toString());
    }
    catch(IOException ioe)
    {
      System.err.println(ioe.toString());
    }
    
    return true;
  }
}