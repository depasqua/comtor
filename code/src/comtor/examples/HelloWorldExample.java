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
 *	 Free Software Foundation, Inc.
 *	 59 Temple Place, Suite 330 
 *	 Boston, MA  02111-1307  USA
 *
 * $Id: HelloWorld.java,v 1.1.1.1 2006/07/10 16:22:03 locasto2 Exp $
 **************************************************************************/
package comtor.examples;

/**
 * The <code>HelloWorldExample</code> class is a small test example of 
 * a class that we can run our Doclets on.
 *
 * @author Michael Locasto
 */
public final class HelloWorldExample
{
   /**
    * This member variable contains an ID string derived
    * from the time that the constructor of an instance of
    * this class is invoked.
    */
   private String m_data = "";

   /**
    * A fairly pointless constructor that simply initializes
    * an instance data member.
    */
   public HelloWorld()
   {
      m_data = "time id: "+System.currentTimeMillis();
   }

   public void thisIsUnCommented()
   {
      //do a bunch of complicated stuff here.
      double d = 7.8;
      double f = 9.0;
      double x = (double)d/f;
      System.out.println(x);
   }

   /**
    * Another pointless method.
    */
   public void readMe()
   {
      int x = 5;
      if(x!=5)
         readMe();
   }

   /**
    * Give me that old Hello, World feeling.
    *
    * @param args cmd line args
    */
   public static void main(String [] args)
   {
      System.out.println("Hello, world.");
   }
}
