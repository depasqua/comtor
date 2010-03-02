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
* $Id:$ PreAndPostCheck.java,v 1.0 2009/03/25 09:08:00 radanne Exp $
**************************************************************************/

package comtor.analyzers;

import comtor.*;
import comtor.analyzers.GeneralResources.*;
import com.sun.javadoc.*;
import java.util.*;
import java.text.*;

/***********************************************************************
* The <code>PreAndPostCheck</code> class checks for the presence of
* pre and post condition tags. The _text tags are used for text based
* descriptions while the _exp tags are used for expressions.
*
* @author Ruth Dannenfelser
***********************************************************************/
public class PreAndPostCheck implements ComtorDoclet
{
	private Properties prop = new Properties();
 	private HashMap<String, Float> gradeBreakdown = new HashMap<String, Float>();
 	private HashMap<String, Integer> possibleScore = new HashMap<String, Integer>();
 	private HashMap<String, Integer> points = new HashMap<String, Integer>();
	
 	//Default Constructor
 	public PreAndPostCheck()
 	{
		//Set defaults for grade breakdown
		gradeBreakdown.put("Pre", new Float(2.0));
		gradeBreakdown.put("Post", new Float(2.0));
	
		//Set defaults for scores
		possibleScore.put("Pre", new Integer(0));
		possibleScore.put("Post", new Integer(0));
 		points.put("Pre", new Integer(0));
		points.put("Post", new Integer(0));
	}
		
	/*****************************************************************
	* Examine each class, obtain each method. Check for pre tag.
	* Check for post tag. Make sure comments following the tag
	* have some substance.
	*
	* @param rootDoc  the root of the documentation tree
	* @returns Properties list
	*****************************************************************/
	public Properties analyze (RootDoc rootDoc)
	{
		// Set doclet title (Must match title on website for this to work)
		prop.setProperty("title", "Pre and Post Check");
		
	 	//variable declarations
		String pre_text = "@pre_text";
		String post_text = "@post_text";
		String pre_exp = "@pre_exp";
		String post_exp = "@post_exp";
		int prePoints = 0;
 		int postPoints = 0;
 		int preMax = 0;
 		int postMax = 0;
 		int fullCredit = 5;
		int halfCredit = (int)(fullCredit/2);
		int onlyHasTagCredit = 1;
		

		ClassDoc[] classes = rootDoc.classes(); //obtain the list of classes to be checked
		
		//process each class
		for(int i=0; i < classes.length; i++)
		{
			String classID = numberFormat(i);
			prop.setProperty("" + classID, "Class: " + classes[i].name()); //store class name in property list
	  		
			MethodDoc[] methods = new MethodDoc[0];
			methods = classes[i].methods(); // Get methods for the class
			//process each method
			for(int j=0; j < methods.length; j++)
			{
				//set the max amount of points
				preMax += fullCredit;
				postMax += fullCredit;
				
				String methodID = numberFormat(j);
				prop.setProperty(classID + "." + methodID, "Method: " + methods[j].name()); //store method name
	    						
				//get the list of pre and post tags in the documentation
				Tag[] preTextTags = methods[j].tags(pre_text);    		
				Tag[] postTextTags = methods[j].tags(post_text); 
	    			Tag[] preExpTags = methods[j].tags(pre_exp);
				Tag[] postExpTags = methods[j].tags(post_exp);

				//if there is no precondition text tag present
				if ((preTextTags.length == 0) && (preExpTags.length == 0))
				{
	  				prop.setProperty(classID + "." + methodID + ".a", "No @pre_text or @pre_exp tag used for " + methods[j].name() + ". Please write a precondition for this method.");
				}
				else if (preTextTags.length == 1) //there is one @pre_text tag for this method
				{      		
	  				if (preTextTags[0].text().trim().equals("")) //if there is no text following the tag
	  				{	
	    					prop.setProperty(classID + "." + methodID + ".a", "There is a @pre_text tag used for " + methods[j].name() + ". However there is no description of the precondition. Please write a precondition.");
	    					prePoints += onlyHasTagCredit;
	  				}
	  				else //there is text after the tag
	  				{
	    					//get the comment following the @pre_text tag
	    					String preTextComment = "";
	    					for (int k = 0; k < preTextTags.length; k++)
		      					preTextComment += preTextTags[k].text();
	    		
	    					if(contains(preTextComment, 1)) //precondition comment text contains a key word
	    					{
	      						if(preTextComment.length() > 5) //acceptable length
	      						{
	        						prop.setProperty(classID + "." + methodID + ".a", "There is a @pre_text tag used for " + methods[j].name() + ". This comment is CORRECT.");          
	        						prePoints += fullCredit;
	      						}
		      					else 
					      		{
	        						prop.setProperty(classID + "." + methodID + ".a", "There is a @pre_text tag used for " + methods[j].name() + ". More description of the precondition is needed for full credit.");
	        						prePoints += halfCredit;
	      						}
	    					}
	    					else //precondition comment does not contain a key word
	    					{
	      						if(preTextComment.length() > 5) //acceptable length
	      						{
	        						prop.setProperty(classID + "." + methodID + ".a", "There is a @pre_text tag used for " + methods[j].name() + ". A better description of the precondition is needed. Talk about the parameters and the conditions before the method executes.");
	        						prePoints += halfCredit;
	      						}
	      						else
	      						{
								prop.setProperty(classID + "." + methodID + ".a", "There is a @pre_text tag used for " + methods[j].name() + ". A better description of the precondition is needed. Try talking about the parameters and the conditions before the method executes.\n Also elaborate on the precondition more. ");
	        						prePoints += halfCredit;
	      						}
	    					}
	  				}
	  			}
	  			if(preTextTags.length > 1) //more than one @pre_text tag for a method
	  			{
	    				prop.setProperty(classID + "." + methodID + ".a", "There should not be more than one @pre_text tag used for " + methods[j].name() + ". Please remove duplicate @pre tags.");
	    				prePoints += halfCredit;
	  			}
	    			if(preExpTags.length == 1) //there is a @pre_exp tag used for a method
				{
					if (preExpTags[0].text().trim().equals(""))//if there is no expression following the tag
					{
						prop.setProperty(classID + "." + methodID + ".c", "There is a @pre_exp tag used for " + methods[j].name() + ". However there is no expression following the tag. Please write a precondition expression for this method.");
						prePoints += onlyHasTagCredit;
					}
					else //there is text after the tag
					{
						//get the comment following the @pre_exp tag
                                                String preExpComment = "";
                                                for (int k = 0; k < preExpTags.length; k++)
                                                {
                                                        preExpComment += preExpTags[k].text();
                                                }
						if(isExpression(preExpComment)) //if the comment is a valid expression
						{
							prop.setProperty(classID + "." + methodID + ".c", "There is a @pre_exp tag used for " + methods[j].name() + ". This comment is CORRECT.");
							prePoints += fullCredit;
						}
						else //comment is not a valid expression
						{
							prop.setProperty(classID + "." + methodID + ".c", "There is a @pre_exp tag used for " + methods[j].name() + ". However no valid expression was provided. Please write a valid precondition expression.");
	  						prePoints += halfCredit;
						}
					}
				}
				if (preExpTags.length > 1)
				{
					prop.setProperty(classID + "." + methodID + ".c", "There is more than one @pre_exp tag used for " + methods[j].name() + ". Only one @pre_exp tag should be used per method.");
					prePoints += halfCredit;
				}
				//if there is no postcondition tag present
				if ((postTextTags.length == 0) && (postExpTags.length == 0))
				{
	  				prop.setProperty(classID + "." + methodID + ".b", "No @post_text or @post_exp tag used for " + methods[j].name() + ". Please write a postcondition for this method.");
				}
				else if (postTextTags.length == 1) //there is one @post_text tag for this method
				{
	  		
	  				if (postTextTags[0].text().trim().equals("")) //if there is no text following the tag
	  				{	
	    					prop.setProperty(classID + "." + methodID + ".b", "There is a @post_text tag used for " + methods[j].name() + ". However there is no description of the postcondition. Please write a postcondition.");
	    					postPoints += onlyHasTagCredit;
	  				}
	  				else //there is text after the tag
	  				{
	    					//get the comment following the @post_text tag
	    					String postTextComment = "";
	    					for (int k = 0; k < postTextTags.length; k++)
	    					{
	      						postTextComment += postTextTags[k].text();
	    					}
	      		
	  					if(contains(postTextComment, 2)) //postcondition text comment contains a key word
	  					{
	    						if(postTextComment.length() > 5) //acceptable length
	    						{
	      							prop.setProperty(classID + "." + methodID + ".b", "There is a @post_text tag used for " + methods[j].name() + ". This comment is CORRECT.");            
	      							postPoints += fullCredit;
	    						}
	   				 		else
	    						{
	      							prop.setProperty(classID + "." + methodID + ".b", "There is a @post_text tag used for " + methods[j].name() + ". More description of the postcondition is needed for full credit.");
	      							postPoints += halfCredit;
	    						}
	    					}
	    					else //postcondition text comment does not contain a key word
	    					{
	      						if(postTextComment.length() > 5)//acceptable length
	      						{
	        						prop.setProperty(classID + "." + methodID + ".b", "There is a @post_text tag used for " + methods[j].name() + ". A better description of the postcondition is needed. Talk about the values returned or modified and the state of the program after the method executes.");
	        						postPoints += halfCredit;
	     				 		}
	      						else
	      						{
								prop.setProperty(classID + "." + methodID + ".b", "There is a @post_text tag used for " + methods[j].name() + ". A better description of the postcondition is needed. Try talking about the modified and/or returned values and the state of the program after the method executes.\n Also elaborate on the postcondition more.");
	        						postPoints += halfCredit;
	      						}
	    					}
	   
	      				}
	  			}
	  			if(postTextTags.length > 1) //more than one @post_text tag for a method
	  			{
	    				prop.setProperty(classID + "." + methodID + ".b", "There should not be more than one @post_text tag used for " + methods[j].name() + ". Please remove duplicate @post tags.");
	    				postPoints += halfCredit;
	  			}
				if(postExpTags.length == 1) //there is a @post_exp tag used for a method
                                {
                                        if(postExpTags[0].text().trim().equals("")) //if there is no expression following the tag
                                        {
                                                prop.setProperty(classID + "." + methodID + ".d", "There is a @post_exp tag used for " + methods[j].name() + ". However there is no expression following the tag. Please write an expresssion.");
                                                postPoints += onlyHasTagCredit;
                                        }
                                        else //there is text after the tag
                                        {
                                                //get the comment following the @post_exp tag
                                                String postExpComment = "";
                                                for (int k = 0; k < postExpTags.length; k++)
                                                {
                                                        postExpComment += postExpTags[k].text();
                                                }
                                                if(isExpression(postExpComment)) //if the comment is a valid expression
                                                {
                                                        prop.setProperty(classID + "." + methodID + ".d", "There is a @post_exp tag used for " + methods[j].name() + ". This comment is CORRECT.");
                                                        postPoints += fullCredit;
                                                }
                                                else //comment is not a valid expression
                                                {
                                                        prop.setProperty(classID + "." + methodID + ".d", "There is a @post_exp tag used for " + methods[j].name() + ". However the postcondition given is not a valid expression. Please write a vaild expression.");
                                                        postPoints += halfCredit;
						}
 					}
                                }
                                if (postExpTags.length > 1)
                                {
                                        prop.setProperty(classID + "." + methodID + ".d", "There is more than one @post_exp tag used for " + methods[j].name() + ". Only one @post_exp tag should be used per method. Please remove extra tags.");
                                        postPoints += halfCredit;
                                }
			}
		}
		//set the points in the map
		possibleScore.put("Pre", new Integer(preMax));
		points.put("Pre", new Integer(prePoints));
		possibleScore.put("Post", new Integer(postMax));
		points.put("Post", new Integer(prePoints));
//		setGradingBreakdown("Pre", new Float(preMax));
//		setGradingBreakdown("Post", new Float(postMax));
 		prop.setProperty("score", "" + getGrade());

 		return prop;
	}	
	/***************************************************************************
	* This method is used to indicate the maximum number of point obtainable
	* for each section of the module. 
	*
	* @param section maxGrade
	***************************************************************************/
	public void setGradingBreakdown(String section, float maxGrade)
	{
		gradeBreakdown.put(section, new Float(maxGrade));
	}
	
	/****************************************************
	* Returns the grade for the code analyzed
	*
	* @returns float
	***************************************************/
	public float getGrade()
	{
		int score, possible;
		float max, total = (float)0.0, percent;
	
		// Define sections
		String sections[] = new String[4];
		int id = 0;
		sections[id++] = "Pre";
		sections[id++] = "Post";
		
		// Get grade for each section
 		for (int i = 0; i < id; i++)
		{
			possible = possibleScore.get(sections[i]);
			score = points.get(sections[i]);
			max = gradeBreakdown.get(sections[i]);

			percent = (possible == 0) ? (float)1.0 : (float)score/possible;
			total += percent * max;
		}

		return total;
	
	}
	/************************************************************
	* This function allows professors to customize the grading
	*
	* @param param value
	************************************************************/
	public void setGradingParameter(String param, String value)
	{
		//No customizable parameters yet!
	}
	/****************************************************************
	* This is useful for configuration information such as paths to 
	* essential files.
	*
	* @param props
	****************************************************************/
	public void setConfigProperties(Properties props)
	{
		//aren't needed	
	}
	/****************************************************************
	* Used to convert an integer value to 3 digits (ex. 7 --> 007)
	*
	* @returns String @param value
	****************************************************************/
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
	/****************************************************************
	* This method takes a string of text and checks if it contains
	* key precondition descriptive words (choice 1) or key
	* postcondition descriptive words (choice 2)
	* returns true if descriptive words are present otherwise
	* returns false.
	*
	* @return boolean @param commentText @param choice
	****************************************************************/
	private boolean contains(String commentText, int choice)
	{
		String text = commentText.toLowerCase();
		//check for precondition key words
		if (choice == 1)
		{
		 	 if( (text.indexOf("parameter")!= -1) || (text.indexOf("takes")!= -1) || (text.indexOf("before")!= -1)  || (text.indexOf("input")!= -1) || (text.indexOf("define")!= -1) || (text.indexOf("require")!= -1) || (text.indexOf("assume")!= -1) || (text.indexOf("cause")!= -1) || (text.indexOf("current")!= -1)|| (text.indexOf("set")!= -1) || (text.indexOf("equal")!= -1) || (text.indexOf("true")!= -1)|| (text.indexOf("false")!= -1))
		 	 	return true;
		 	 else
		 	 	return false;
		}
		//check for postcondition key words
		if (choice == 2)
		{
			if((text.indexOf("return")!= -1) || (text.indexOf("after")!= -1) || (text.indexOf("output")!= -1) || (text.indexOf("change")!= -1) || (text.indexOf("differ")!= -1) || (text.indexOf("final")!= -1)|| (text.indexOf("write")!= -1) || (text.indexOf("wrote")!= -1)|| (text.indexOf("true")!= -1)|| (text.indexOf("false")!= -1))
				return true;
			else
				return false;
		}

		System.out.println("An error has occurred within contains method");
		return false;	
	}
	/*************************************************************************
	 * This method calls antlr to determine if the string given is a 
   	 * valid expression. Returns true if expression is valid.
	 *
   	 * @param String representing the text following a tag   
	 * @return boolean
         ***********************************************************************/
         private boolean isExpression (String commentText)
	 {
		return JavaExpressionTester.checkExpression(commentText);
	 }
}
