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
  * $Id: Driver.java,v 1.1 2006-10-18 05:50:27 brigand2 Exp $
  **************************************************************************/

import java.io.*;
import java.util.*;

/**
 * The <code>Driver</code> class is a driver to generate reports from the doclets.
 *
 * @author Joe Brigandi
 */
public class Driver
{
  /**
   * Generates reports from the doclets.
   */
  public static void main(String[] args)
  {
    try
    {
      FileWriter outstream = new FileWriter("report.txt");
      PrintWriter prt = new PrintWriter(outstream);            
      
      CheckForTags generate = new CheckForTags();
      Properties prop = generate.getPropertyList();
      
      String[] arr = new String[0];
      arr = prop.keySet().toArray(arr);
      Arrays.sort(arr);
      
      for(int i=0; i < arr.length; i++)
      {
        if(arr[i] != null)
          prt.println(arr[i] + "   " + prop.getProperty("" + arr[i]));
      }
      
      prt.close();
      outstream.close();
    }
    
    catch(Exception ex) 
    {
      System.err.print("!!!An exception was thrown!!!");
      System.err.println(ex.toString());
    }
  }
}