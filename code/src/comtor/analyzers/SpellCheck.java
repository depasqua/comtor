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
  * $Id: SpellCheck.java,v 1.2 2008-11-05 22:16:52 ssigwart Exp $
  **************************************************************************/
package comtor.analyzers;

import comtor.*;
import comtor.analyzers.SpellCheckResources.*;
import com.sun.javadoc.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import java.io.*;
import org.antlr.runtime.*;

/**
 * The <code>SpellCheck</code> class is a tool to check spelling in comments.
 * It is capable of determining is a word is misspelled or a java class name.
 * Incorrect spellings for the same word are only counted once.
 *
 * @author Stephen Sigwart
 */
public final class SpellCheck implements ComtorDoclet
{
  private String dict_file = "/home/comtor/words";
  private String mispellingsFilename = "/home/comtor/mispellings";

  // Output stream for mispelled words
  private PrintStream mispellingsFile = null;

  private Properties prop = new Properties();
  private float maxScore = 5;

  // All words in lowercase
  private HashSet<String> allWords = new HashSet<String>();

  // Correctly spelled words
  private HashSet<String> words = new HashSet<String>();

  // Words such as class, field, method, and parameter names
  private HashSet<String> javaWords = new HashSet<String>();

  // User defined valid words
  private HashSet<String> validWords = new HashSet<String>();

  // Words that are potentially wrong
  private HashSet<String> potentialWords = new HashSet<String>();

  // Random access dictionary file
  private RandomAccessFile dict = null;

  /**
   * Constructor
   */
  public SpellCheck()
  {
    // Add some acceptable words
    validWords.add("java");
    validWords.add("int");
  }

  /**
   * Examine each class and its methods. Calculate the length of each method's comments.
   *
   * @param rootDoc the root of the documentation tree
   * @return Properties list
   */
  public Properties analyze(RootDoc rootDoc)
  {
    prop.setProperty("title", "Spell Checker"); //doclet title

    // Parse input files to get variable and class names
    try
    {
      // Current working directory
      String dir = System.getProperty("user.dir");
      System.out.println("User directory: " + dir);
      BufferedReader fileNames =  new BufferedReader(new FileReader(dir+"/source.txt"));
      String line;
      while((line = fileNames.readLine()) != null)
      {
        CharStream input = new ANTLRFileStream(line);
        spellCheckGrammarLexer lex = new spellCheckGrammarLexer(input);
        TokenStream tokens = new CommonTokenStream(lex);
        spellCheckGrammarParser parser = new spellCheckGrammarParser(tokens);
        parser.compilationUnit();

        for (int i = 0; i < lex.varNames.size(); i++)
          javaWords.add((String)lex.varNames.get(i));

        // Parse all comments
        for (int i = 0; i < lex.comments.size(); i++)
          parseComment((String)lex.comments.get(i));

        //System.out.println(lex.packages);
      }
    }
    catch (Exception e)
    {
      System.out.println(e);
    }

/* No longer needed since all comments are fetched by ANTLR
    // Get comments for root document
    parseComment(rootDoc.commentText());

    // Get comments for each class
    ClassDoc[] classes = rootDoc.classes();
    for(int i = 0; i < classes.length; i++)
      processClass(classes[i]);
*/

    // Print misspelled words
    Iterator<String> it = potentialWords.iterator();
    int num = 0;
    String word;
    while (it.hasNext())
    {
      word = it.next();

      // Check that word is not class/methods/parameter/etc name
      if (javaWords.contains(word))
      {
        it.remove();
        words.add(word);
      }
      // Check that word is not class/methods/parameter/etc name with 's' added
      // to end
      else if (word.charAt(word.length()-1) == 's' && javaWords.contains(word.substring(0, word.length()-1)))
      {
        it.remove();
        words.add(word);
      }
      else
      {
        prop.setProperty("000.000."+num, "The following word was misspelled: " + word);
        num++;

        // Add the word to the mispellings file
        if (mispellingsFile != null)
          mispellingsFile.println(word);
      }
    }
    if (num == 0)
      prop.setProperty("000.000.0", "All words correctly spelled.");

    prop.setProperty("score", "" + getGrade());

    return prop; //return the property list
  }

  /*************************************************************************
  * Processes a single class
  *
  * @param class Class to process
  *************************************************************************/
  private void processClass(ClassDoc classDoc)
  {
    // Add class name to list of valid words
    javaWords.add(classDoc.name());

    // Add class comment
    parseComment(classDoc.commentText());

    // Get all fields
    FieldDoc[] fields = classDoc.fields();
    for(int h = 0; h < fields.length; h++)
    {
      javaWords.add(fields[h].name());

      // Add field comment
      parseComment(fields[h].commentText());
    }

    // Get inner classes
    ClassDoc[] inner_classes = classDoc.innerClasses();
    for(int h = 0; h < inner_classes.length; h++)
      processClass(inner_classes[h]);

    // Get template types
    TypeVariable[] types = classDoc.typeParameters();
    for(int h = 0; h < types.length; h++)
      javaWords.add(types[h].typeName());

    // Get comments for tags
    Tag[] tags = classDoc.tags();
    for (int h = 0; h < tags.length; h++)
      parseComment(tags[h].text());

    // Get comments for each method
    MethodDoc[] methods = classDoc.methods();
    for(int j = 0; j < methods.length; j++)
    {
      // Add method name to list of valid words
      javaWords.add(methods[j].name());

      // Get parameters
      Parameter[] params = methods[j].parameters();
      for(int h = 0; h < params.length; h++)
      {
        javaWords.add(params[h].name());
        javaWords.add(params[h].typeName());
      }

      // Get comments for tags
      tags = methods[j].tags();
      for (int h = 0; h < tags.length; h++)
        parseComment(tags[h].text());

      // Get method comments
      parseComment(methods[j].commentText());
    }
  }

  /*************************************************************************
  * Parses words out of comments and removes punctuation
  *
  * @param comment Comment to parse
  *************************************************************************/
  private void parseComment(String comment)
  {
    // Replace parenthesis, brackets, dashes, and periods with spaces
    comment = comment.replaceAll("[()<>-]|\\+"," ");

    // Create Scanner
    Scanner scan = new Scanner(comment);
    String tmp, tmp2;
    while (scan.hasNext())
    {
      tmp = scan.next();

      if (allWords.add(tmp.toLowerCase()))
      {
        tmp2 = tmp.replaceAll("[\\p{Punct}&&[^']]","");

        if (validWord(tmp2))
          words.add(tmp2);
        else
        {
          // Check that error was not due to something like "num+1" in
          // the comment
          tmp2 = tmp.replaceAll("[\\p{Punct}&&[^']]"," ");
          Scanner scan2 = new Scanner(tmp2);
          while (scan2.hasNext())
          {
            String tmp3 = scan2.next();

            if (validWord(tmp3))
              words.add(tmp3);
            else
            {
              // Valid if a number
              try
              {
                Integer.parseInt(tmp3);
                words.add(tmp3);
              }
              catch (NumberFormatException e)
              {
                // Ignore words that contain @ symbol.  This will prevent
                // javadoc tags and E-mail addresses from being considered
                // incorrectly spelled.
                // Ignore words that contain a dot '.' in the middle as they
                // could be filename.
                int idx = tmp.lastIndexOf('.');
                if (!tmp.contains("@") && (idx == -1 || idx == tmp.length()))
                {
                  tmp = tmp.replaceAll("[\\p{Punct}&&[^_]]","");
                  potentialWords.add(tmp);
                }
              }
            }
          }
        }
      }
    }
  }

  /*************************************************************************
  * Checks that the given word is in the dictionary or a user defined word.
  *
  * @param word Word to check
  *************************************************************************/
  private boolean validWord(String word)
  {
    word = word.toLowerCase();

    // Check against user defined words
    if (validWords.contains(word) || word.length() == 0)
      return true;

    // Check against UNIX dictionary file
    if (dict != null)
    {
      try
      {
        long length = dict.length();

        // Do binary search for word
        long start = 0, mid;
        mid = start + (length - start)/2;
        int rec = 0;  // Just in case of infinite loop
        while (rec < 250 && start < length && mid < dict.length() && mid >= 0)
        {
          rec++;

          dict.seek(mid);

          // Ignore this line because we may be in the middle of a word
          String ignore;
          if (mid != 0)
            ignore = dict.readLine();

          if (dict.getFilePointer() < length)
          {
            long filePos = dict.getFilePointer();
            if (filePos < start)
              break;

            // Load the word
            String tmp = dict.readLine().toLowerCase();

            if (tmp.equals(word))
              return true;
            else if (word.compareTo(tmp) < 0)
              length = filePos;
            else
              start = dict.getFilePointer();
            mid = start + (length - start)/2;
          }
          else
          {
              mid = start-1;
              if (start == 0)
                mid = 0;
          }
        }
      }
      catch (Exception e)
      {
        System.out.println("Error searching for word in dictionary.");
        System.out.println(e);
      }
    }

    return false;
  }

  /*************************************************************************
  * Sets the grading breakdown for the doclet.
  *
  * @param section Name of the section to set the max grade for
  * @param maxGrade Maximum grade for the section
  *************************************************************************/
  public void setGradingBreakdown(String section, float maxGrade)
  {
    if (section.equals("Spelling"))
      maxScore = maxGrade;
  }

  /*************************************************************************
  * Returns the grade for the doclet.
  *************************************************************************/
  public float getGrade()
  {
    if (words.size() + potentialWords.size() == 0)
      return (float)0.0;

    float ratio = (float)words.size()/(words.size() + potentialWords.size());

    return ratio * maxScore;
  }

  /*************************************************************************
  * Sets a parameter used for doclet grading.
  *
  * @param param Name of the grading parameter
  * @param value Value of the parameter
  *************************************************************************/
  public void setGradingParameter(String param, String value)
  {
    if (param.equals("Valid Words"))
    {
      Scanner scan = new Scanner(value);
      while (scan.hasNext())
        validWords.add(scan.next().toLowerCase());
    }
  }

  /*************************************************************************
  * Sets the configuration properties loaded from the config file
  *
  * @param props Properties list
  *************************************************************************/
  public void setConfigProperties(Properties props)
  {
    // Get dictionary file and misspellings file
    dict_file = props.getProperty("dictionary");
    mispellingsFilename = props.getProperty("mispellings");

    // Open dictionary file
    try
    {
      dict = new RandomAccessFile(dict_file, "r");
    }
    catch(Exception e)
    {
      System.out.println("Failed to load dictionary.");
      dict = null;
    }

    // Open mispelled words file
    try
    {
      mispellingsFile = new PrintStream(new FileOutputStream(mispellingsFilename, true));
    }
    catch(Exception e)
    {
      System.out.println("Failed to open mispellings file.");
      mispellingsFile = null;
    }
  }
}
