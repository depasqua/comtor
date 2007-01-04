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
 * $Id: CntrlFlow.java,v 1.1 2006/08/16 22:24:02 locasto Exp $
 **************************************************************************/
package comtor.examples;


/**
 * The <code>CntrlFlowExample</code> class is a small test example of 
 * a class that we can run our Doclets on.
 *
 * @author Michael Locasto
 */
public final class CntrlFlowExample
{

   /**
    * @controlflow root->this
    * @cflow
    */
   public void a()
   {
      b();
   }
   
   /**
    * @controlflow root->a()->this
    * @cflow root->a()->this
    */
   public void b()
   {
      c();
   }
   
   /**
    * @controlflow root->a()->b()->this
    * @cflow root->a()->b()->this
    */
   public void c()
   {
      d();
   }

   /**
    * @controlflow root->a()->b()->c()->this
    * @cflow root->a()->b()->c()->this
    */
   public void d()
   {
      System.out.println("d(): hello.");
   }

   /**
    * Control flow testing.
    *
    * @param args cmd line args
    */
   public static void main(String [] args)
   {
      CntrlFlow cf_app = new CntrlFlow();
      cf_app.a();
   }
}
