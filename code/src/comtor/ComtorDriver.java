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
  * $Id: ComtorDriver.java,v 1.10 2007-01-04 13:44:27 depasqua Exp $
  **************************************************************************/
package comtor;

import com.sun.javadoc.*;
import comtor.analyzers.*;
import java.util.*;
import java.io.*;

/**
 * The <code>ComtorDriver</code> class is a tool to
 * run doclets and pass a vector of property lists to
 * the report generator.
 *
 * @author Joe Brigandi
 */
public class ComtorDriver
{ 
  /**
   * Accepts a property list from the called doclets
   * and puts them in a vector.  It then passes the 
   * vector to the report generator.
   *
   * @param rootDoc  the root of the documentation tree
   * @returns boolean value
   */
  public static boolean start(RootDoc rootDoc)
  {
	  try
	  {
          Vector v = new Vector();
		  Scanner scan = new Scanner(new File("Doclets.txt"));
		  String docletName;
		  while(scan.hasNext())
		  {
			  docletName = scan.nextLine();
			  Class c = Class.forName(docletName);
			  ComtorDoclet cd = (ComtorDoclet) c.newInstance();
			  Properties list = cd.analyze(rootDoc);
			  v.addElement(list);
		  }
		  
		  GenerateReport report = new GenerateReport();
		  report.generateReport(v);
		  
		  scan.close();
	  }
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