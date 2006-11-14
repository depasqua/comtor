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
  * $Id: ComtorDriver.java,v 1.8 2006-11-14 16:24:37 depasqua Exp $
  **************************************************************************/

import com.sun.javadoc.*;
import java.util.*;
import java.lang.*;
import java.awt.*;

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
    Vector v = new Vector();
    Class c = Class.forName("CheckForTags");
    ComtorDoclet cd = (ComtorDoclet) c.newInstance();
    Properties list = cd.analyze(rootDoc);
    v.addElement(list);

/**	
    CheckForTags cft = new CheckForTags(); 
    Properties cftList = cft.analyze(rootDoc); 
    v.addElement(cftList);
    
    CommentAvgRatio car = new CommentAvgRatio();
    Properties carList = car.analyze(rootDoc);
    v.addElement(carList);
	
	PercentageMethods pm = new PercentageMethods();
	Properties pmList = pm.analyze(rootDoc);
	v.addElement(pmList);
**/ 
    GenerateReport report = new GenerateReport();
    report.generateReport(v);
    
    return true;
  }
}