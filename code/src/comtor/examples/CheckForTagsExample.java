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
  * $Id: CheckForTagsExample.java,v 1.2 2006/10/31 20:08:33 brigand2 Exp $
  **************************************************************************/

import java.io.*;

/**
 * The <code>CheckForTagsExample</code> class is for testing purposes.
 * It tests all possible combinations for CheckForTags.java to make sure 
 * it works correctly.
 *
 * @author Joe Brigandi
 */
public class CheckForTagsExample
{
  public static void main(String[] args)
  {}

//////////CONSTRUCTORS//////////
  /**
   * Correct Example for default constructor
   */
  public CheckForTagsExample()
  {}

  /**
   * Correct Example with params
   * @param name
   */
   public CheckForTagsExample(String name, int age)
   {}

//////////RETURN TAGS//////////
  /**
   * Correct Example
   */
  public void setName()
  {}
  
  /**
   * Correct Example
   * @return phone number
   */
  public int getPhone()
  {}
  
  /**
   * ERROR: The tag is missing
   */
  public String getName()
  {}
  
  /**
   * ERROR: The tag has no comment
   * @return 
   */
  //public int getAge()
  //{}
  
  /**
   * ERROR: The type is missing
   * @return nothing
   */
  public void setPhone()
  {}
  
  /**
   * ERROR: Too many return tags
   * @return this
   * @return that
   */
  public int checkNum()
  {}
  
//////////PARAM TAGS//////////
  /**
   * Correct Example
   * @param firstName
   * @param lastName
   */
  public void attach(String firstName, String lastName)
  {}
  
  /**
   * ERROR: Missing param tag
   * @param firstName
   */
  public void detach(String firstName, String lastName)
  {}
  
  /**
   * ERROR: Missing parameter
   * @param name
   * @param firstName
   * @param lastName
   */
  public void call(String firstName, String lastName)
  {}
  
//////////THROW TAGS////////// 
  /**
   * Correct Example
   * @throws IOException
   */
  public void clean() throws IOException
  {}
  
  /**
   * ERROR: Missing exception
   * @throws IOException
   */
  public void add()
  {}
  
  /**
   * ERROR: Missing throws tag
   */
  public void subtract() throws IOException
  {}
}