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
  **************************************************************************/
  
package comtor;

import comtor.analyzers.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.sql.*;

public class ComtorDebugger
{
	//Method to generate the properties list dump.
	public void generateDebugFile(Vector v)
	{
		String path = new String();
		int index;
		
		//Finding the path to which to dump the information.
		path = System.getProperty("user.dir");

		index = path.lastIndexOf("/");
		path = path.substring(0, index);
		index = path.lastIndexOf("/");
 		path = path.substring(0, index);
		path = path.concat("/uploads/debug/dump");
		
		try
		{
			//Testing if the file already exists, deleting if so.
			File test = new File(path);
			if (test.exists())
				test.delete();
				
			//Create a new file, and prepare it to write to.
			test.createNewFile();
			PrintWriter dumpFile = new PrintWriter(new BufferedWriter(new FileWriter(path)));
			
			//Temporary properties list to handle the passed vector of properties lists.
			Properties list = new Properties();
			
			//Vector to hold all of the properties, ease of printing.
			Vector all = new Vector();
		
			//For each vector element, each of which is a properties list...
			for(int i=0; i < v.size(); i++)
			{
				list = (Properties)v.get(i);
				
				//Get all the keys in the properties list.
				Enumeration em = list.keys();
				//While there are keys to be processed in the keylist...
				while(em.hasMoreElements())
				{
					//Add both the key and its value to the vector.
					String str = (String)em.nextElement();
					all.add(str);
					all.add(list.get(str));
				}
				//These are separators used to delineate between two separate reports.
				all.add("------------------");
				all.add("------------------");
			}
			
			//For all the elements in our 'all' vector...
			for (int count = 0; count < all.size(); count++)
			{
				//Write the key name.
				dumpFile.write((String)all.get(count));
				//If we are still not near the end (so we won't throw a null pointer)...
				//then increment the vector so that we obtain the key's value as well.
				if (count < all.size() - 2)
					count++;
				//Tab between the key name and its value.
				dumpFile.write(" \t");
				//Writing the value...
				dumpFile.write((String)all.get(count));
				dumpFile.write("\n");
			}
			dumpFile.close();
		}
		catch (IOException e)
		{
		}
	}
}

