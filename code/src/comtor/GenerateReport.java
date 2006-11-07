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
  * $Id: GenerateReport.java,v 1.3 2006-11-07 04:32:20 brigand2 Exp $
  **************************************************************************/

import java.io.*;
import java.util.*;

/**
 * The <code>GenerateReport</code> class is a tool to
 * generate a report from a vector containing property lists.
 *
 * @author Joe Brigandi
 */
public class GenerateReport
{
  public void generateReport(Vector v)
  {
    try
    {
      FileWriter outstream = new FileWriter("Comtor Report.txt");
      PrintWriter prt = new PrintWriter(outstream);            
      
      int size = v.size();
      for(int i=0; i < size; i++)
      {
        Properties list = new Properties();
        list = v.get(0);
      
        String[] arr = new String[0];
        arr = list.keySet().toArray(arr);
        Arrays.sort(arr);
        
        for(int j=0; j < arr.length; j++)
        {
          if(arr[j] != null)
            prt.println(list.getProperty("" + arr[j]));
        }
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