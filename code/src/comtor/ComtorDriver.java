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
  **************************************************************************/
package comtor;

import com.sun.javadoc.*;
import comtor.analyzers.*;
import java.util.*;
import java.io.*;
import java.text.*;
import java.sql.*;

/**
 * The <code>ComtorDriver</code> class is a tool to run doclets
 * and pass a vector of property lists to the report generator.
 *
 * @author Joe Brigandi
 */
public class ComtorDriver
{
  /**
   * Check for doclet-added options. Returns the number of arguments you must specify on the command line for the given option. For example, "-d docs" would return 2.
   *
   * @param option Command line argument
   * @return int Number of arguments on the command line for an option including the option name itself. Zero return means option not known. Negative value means error occurred.
   */
   public static int optionLength(String option)
   {
     if (option.equals("--config-file"))
       return 2;

     if (option.equals("--assignment-id"))
       return 2;

     return 0;
   }

  /**
   * Required function
   * @param options Command line arguments
   * @param reporter
   * @return int Number of arguments on the command line for an option including the option name itself. Zero return means option not known. Negative value means error occurred.
   */
  public static boolean validOptions(String[][] options, DocErrorReporter reporter)
  {
    return true;
  }

  /**
   * Accepts a property list from the called doclets and puts them
   * in a vector. It then passes the  vector to the report generator.
   *
   * @param rootDoc  the root of the documentation tree
   * @return boolean value
   */
  public static boolean start(RootDoc rootDoc)
  {
    // Look through options to get config file and assignmentId
    int assignmentId = -1;
    String configFile = null;
    String options[][] = rootDoc.options();
    for (int i = 0; (assignmentId == -1 || configFile == null) && i < options.length; i++)
    {
      if (options[i][0].equals("--config-file"))
      {
        System.out.println("Config file: " + options[i][1]);
        configFile = options[i][1];
      }
      else if (options[i][0].equals("--assignment-id"))
      {
        System.out.println("Assignment Id: " + options[i][1]);
        assignmentId = Integer.parseInt(options[i][1]);
      }
    }
    if (configFile == null || assignmentId == -1)
    {
      System.out.println("Missing either config file or assignment id.");
      return true;
    }

    try
    {
      // Get database connection so that we can get doclet grading settings and parameters
      Connection db = ComtorDatabase.getConnection(configFile);
      if (db == null)
        return true;

      // Prepare statement to get grading information
      PreparedStatement docletSectionsPrepStmt = db.prepareStatement("SELECT sectionName, maxGrade FROM docletGradeSections dGS LEFT JOIN assignmentGradeBreakdownsView aGBW ON dGS.docletGradeSectionId = aGBW.docletGradeSectionId WHERE docletId IN (SELECT docletId FROM doclets WHERE javaName=?) AND assignmentId=?");
      docletSectionsPrepStmt.setLong(2, assignmentId);

      // Prepare statement to get grading parameters
      PreparedStatement docletParametersPrepStmt = db.prepareStatement("SELECT parameterName, param FROM docletGradeParameters dGP LEFT JOIN assignmentGradeParametersView aGPW ON dGP.docletGradeParameterId = aGPW.docletGradeParameterId WHERE docletId IN (SELECT docletId FROM doclets WHERE javaName=?) AND assignmentId=?");
      docletParametersPrepStmt.setLong(2, assignmentId);

      Vector v = new Vector(); // create new vector
      Vector threads = new Vector(); // create new vector
      Scanner scan = new Scanner(new File("Doclets.txt")); //read list of doclets
      String docletName;
      while(scan.hasNext())
      {
        docletName = scan.nextLine(); //store doclet as docletName
        System.out.println("Attempting to get class: " + docletName);
        try
        {
          Class c = Class.forName(docletName); //create class for doclet
          System.out.println("Attempting to get instance of class: " + docletName);
          ComtorDoclet cd = (ComtorDoclet) c.newInstance(); //create new instance of the class

          // Start thread
          System.out.println("Starting " + docletName);
          DocletThread docThrd = new DocletThread();
          docThrd.setRootDoc(rootDoc);
          docThrd.setDocletSectionsPrepStatements(docletSectionsPrepStmt, docletParametersPrepStmt);
          docThrd.setAnalyzer(cd);
          docThrd.start();
          threads.add(docThrd);
	}
	catch (ClassNotFoundException e)
	{
	  System.out.println("Class not found.");
	}
        catch (ExceptionInInitializerError e)   
	{
	  System.out.println(e);
	}
        catch (LinkageError e)   
	{
	  System.out.println(e);
	}
      }

      // Wait for all threads
      for (int i = 0; i < threads.size(); i++)
      {
        DocletThread docThrd = (DocletThread)threads.get(i);
        docThrd.join();
        Properties tmp = docThrd.getProperties();
        if (tmp != null)
          v.addElement(tmp); //add the resulting property list to the vector
      }

      GenerateReport report = new GenerateReport();
      report.setConfigFilename(configFile);
      report.generateReport(v); //pass the vector to the generate report class

      scan.close();
      docletSectionsPrepStmt.close();
      db.close();
    }

    //exceptions
    catch(InterruptedException e)
    {
      System.out.println("InterruptedException");
      System.err.println(e.toString());
    }
    catch(InstantiationException ie)
    {
      System.out.println("InstantiationException");
      System.err.println(ie.toString());
    }
    catch(IllegalAccessException iae)
    {
      System.out.println("IllegalAccessException");
      System.err.println(iae.toString());
    }
    catch(IOException ioe)
    {
      System.out.println("IOException");
      System.err.println(ioe.toString());
    }
    catch(SQLException sqle)
    {
      System.out.println("SQLException");
      System.err.println(sqle.toString());
    }
    catch (Exception e)
    {
      System.out.println("Other Exception");
      System.out.println(e.toString());
    }

    return true;
  }

  /**
   * The <code>DocletThread</code> class is used to run each doclet as a thread
   *
   * @author Stephen Sigwart
   */
  private static class DocletThread extends Thread
  {
    private ComtorDoclet doclet;
    private Properties list = null;
    private RootDoc rootDoc;
    private PreparedStatement docletSectionsPrepStmt;
    private PreparedStatement docletParametersPrepStmt;

    /**************************************************************************
    * Sets the root document that the doclet will work with
    * @param rootDoc The root document that the doclet will work with
    **************************************************************************/
    public void setRootDoc(RootDoc rootDoc)
    {
      this.rootDoc = rootDoc;
    }

    /**************************************************************************
    * Sets the doclet that will process the root document
    * @param docletSectionsPrepStmt Prepared statement used to get doclet
    *                               grading section info
    * @param docletParametersPrepStmt Prepared statement used to get doclet
    *                                 parameters for grading
    **************************************************************************/
    public void setDocletSectionsPrepStatements(PreparedStatement docletSectionsPrepStmt, PreparedStatement docletParametersPrepStmt)
    {
      this.docletSectionsPrepStmt = docletSectionsPrepStmt;
      this.docletParametersPrepStmt = docletParametersPrepStmt;
    }

    /**************************************************************************
    * Sets the doclet that will process the root document
    * @param doclet Doclet that will process the root document
    **************************************************************************/
    public void setAnalyzer(ComtorDoclet doclet)
    {
      this.doclet = doclet;
    }

    /**************************************************************************
    * Runs the doclet on the root document
    **************************************************************************/
    public void run()
    {
      System.out.println("Running analyzer\n"+doclet);

      // Get assignment specific information from database
      try
      {
        // Get and set grading options
        docletSectionsPrepStmt.setString(1, (doclet.getClass()).getName());
        ResultSet result = docletSectionsPrepStmt.executeQuery();
        if (result.first())
        {
          do
          {
            doclet.setGradingBreakdown(result.getString(1), result.getFloat(2));
          }while (result.next());
        }

        // Get and set grading paramters
        docletParametersPrepStmt.setString(1, (doclet.getClass()).getName());
        result = docletParametersPrepStmt.executeQuery();
        if (result.first())
        {
          do
          {
            doclet.setGradingParameter(result.getString(1), result.getString(2));
          }while (result.next());
        }
      }
      catch(SQLException e)
      {
        System.out.println(e);
      }

      list = doclet.analyze(rootDoc); //call the analyze method to run Javadoc
      System.out.println("Done analyzer\n"+doclet);
    }

    /**************************************************************************
    * Runs the doclet on the root document
    * @return Property list created by doclet
    **************************************************************************/
    public Properties getProperties()
    {
      return list;
    }
  }
}
