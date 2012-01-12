package org.comtor.analyzers;

import org.antlr.runtime.*;
import org.comtor.drivers.*;
import org.comtor.analyzers.InteractionsResources.*;
import com.sun.javadoc.*;
import java.util.*;
import java.text.*;
import java.io.*;

public class Interactions implements ComtorDoclet
{
  // Properties for the doclet run
  Properties prop = new Properties();

  // All user-defined methods
  public static ArrayList allUserMethods = new ArrayList();

  // All .java files
  ArrayList javaFiles = new ArrayList();

  // Stores all class names
  ArrayList javaClassNames = new ArrayList();
  // Stores all methods in all files (Two-D)
  ArrayList javaMethodsNames = new ArrayList();
  // Stores all calls in all methods in all files (Three-D)
  ArrayList javaCallsNames = new ArrayList();

  // Final data-structure
  ArrayList finalList = new ArrayList();

  // Does stuff
  public Properties analyze(RootDoc rootDoc)
  {
    // fill javaFiles
    try
    {
      // source.txt is populated by the following command:
      // find [directory code is in] -name "*.java" > source.txt
      String dir = System.getProperty("user.dir"); // Current working directory
      System.out.println("User directory: " + dir);
      BufferedReader input =  new BufferedReader(new FileReader(dir+"/source.txt"));
      String line;
      while((line = input.readLine()) != null)
      {
        System.out.println("Read line: " + line);
        javaFiles.add(line);
      }
    }
    catch(Exception e){System.out.println("You did something wrong: " + e);}


    // Get list of all user-defined methods
    ClassDoc[] classes = rootDoc.classes();
    for(int i = 0; i < classes.length; i++)
    {
      // Get the methods for this class
      MethodDoc[] methods = new MethodDoc[0];
      methods = classes[i].methods();
      for(int j = 0; j < methods.length; j++)
            {
        allUserMethods.add(methods[j].name());
      }
    }

    // Get list of all calls
    for(int i = 0; i < javaFiles.size(); i++)
    {
      // Each java file gets processed by the antlr lexer
      try
      {
      System.out.println((String)javaFiles.get(i));
      CharStream input = new ANTLRFileStream((String)javaFiles.get(i));
      methodExtraction lex = new methodExtraction(input);
      TokenStream tokens = new CommonTokenStream(lex);
      tokens.toString();
      lex.callsPerMethod.add(lex.temp);
      lex.classesPerFile.add(lex.callsPerMethod);

      // List of all classes
//      for(int j = 0; j < lex.classesPerFile.size(); j++)
      for(int j = 0; j < lex.classes.size(); j++)
        javaClassNames.add((String)lex.classes.get(j));

      // List of all methods
      javaMethodsNames.add(lex.methodNames);

      // List of all calls
      javaCallsNames.add(lex.callsPerMethod);
      }
      catch(Exception e){System.out.println(e);}
    }

    // Merge lists of calls with list of user-defined methods
    String methCall;
    int tempI;
    ArrayList methods, calls, tempMeths, tempCalls;
    for(int x = 0; x < javaClassNames.size(); x++)
    {
      methods = (ArrayList)javaMethodsNames.get(x);
      tempMeths = new ArrayList();
      for(int y = 0; y < methods.size(); y++)
      {
        tempCalls = new ArrayList();
        calls = (ArrayList)((ArrayList)(javaCallsNames.get(x))).get(y);
        for(int z = 0; z < calls.size(); z++)
        {
          methCall = (String)calls.get(z);
          int indx = methCall.indexOf('.');
    	  methCall = methCall.substring(indx+1,methCall.length());
    	  System.out.println(methCall);
          // If user-defined, add to final list
          if(crossReference(methCall))
            tempCalls.add(methCall);
        }
        tempMeths.add(tempCalls);
      }
      finalList.add(tempMeths);
    }

    // Set properties of doclet test
    // Doclet title
    prop.setProperty("title", "Method Interactions");
    // Store classes, methods, and calls in the prop vector
    ArrayList temp1, temp2;
    String classID, methodID;
    for(int x = 0; x < javaClassNames.size(); x++)
    {
      // Stores class name
      classID = numberFormat(x);
           prop.setProperty("" + classID, javaClassNames.get(x) + " Class");
      temp1 = (ArrayList)javaMethodsNames.get(x);
      for(int y = 0; y < temp1.size(); y++)
      {
        // Stores method name
        methodID = numberFormat(y);
        prop.setProperty("" + classID + "." + methodID, temp1.get(y) + " Method");
        temp2 = (ArrayList)((ArrayList)(finalList.get(x))).get(y);
        for(int z = 0; z < temp2.size(); z++)
        {
          prop.setProperty("" + classID + "." + methodID + ".d" + z, "Call made: " + temp2.get(z));
        }
      }
    }

    prop.setProperty("score", "0.0");

    // Returns properties for ComtorDriver to generate a Report
    return prop;
  }

  // Checks if a method is user-defined
  public static boolean crossReference(String methName)
  {
    for(int i = 0; i < allUserMethods.size(); i++)
      if(allUserMethods.get(i).equals(methName))
        return true;

    return false;
  }

  // Used to convert an integer value to 3 digits (ex. 7 --> 007)
  private String numberFormat(int value)
  {
    String newValue;
    if(value<10)
      newValue = "00" + value;
    else if(value<100)
      newValue = "0" + value;
    else
      newValue = "" + value;

    return newValue;
  }

  // Do nothing currently
  public void setGradingBreakdown(String section, float maxGrade){}
  public float getGrade(){return 0;}
  public void setGradingParameter(String param, String value){}

  /*************************************************************************
  * Sets the configuration properties loaded from the config file
  *
  * @param props Properties list
  *************************************************************************/
  public void setConfigProperties(Properties props)
  {
    // Don't need them
  }
}
