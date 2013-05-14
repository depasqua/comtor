/**
 *  Comment Mentor: A Comment Quality Assessment Tool
 *  Copyright (C) 2010 The College of New Jersey
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.comtor.analyzers;

import org.comtor.drivers.*;
import com.sun.javadoc.*;
import java.util.*;
import java.text.*;

/**
 * The CheckForTags class is an analysis module used to validate the 
 * @return, @throw, and @param tags.
 *
 * @author Joe Brigandi
 * @author Peter DePasquale
 */
public class CheckForTags implements ComtorDoclet
{
	// Possible places to check for option tags
	private final int OVERVIEW = 0x0001;
	// Package-specific information is not yet supported
	// private final int PACKAGE = 0x0002;
	private final int CLASS = 0x0004;
	private final int FIELD = 0x0008;
	private final int METHOD = 0x0010;
	
	// Variables indicating optional tag requirements
	private int requireAuthor = 0;
	private int requireVersion = 0;
	private int requireSee = 0;
	private int requireSince = 0;
	
	// Possible values for each optional tag
	private final int maskAuthor = OVERVIEW | CLASS;
	private final int maskVersion = OVERVIEW | CLASS;
	private final int maskSee = OVERVIEW | CLASS | FIELD | METHOD;
	private final int maskSince = OVERVIEW | CLASS | FIELD | METHOD;
	
	private Properties prop = new Properties();
	private HashMap<String, Float> gradeBreakdown =
		new HashMap<String, Float>();
	private HashMap<String, Integer> possibleScore = 
		new HashMap<String, Integer>();
	private HashMap<String, Integer> points = new HashMap<String, Integer>();

	/**
	 * The module's constructor intializes the default values
	 * related to scoring.
	 */
	public CheckForTags()
	{
		// Set the default values for grade breakdown
		gradeBreakdown.put("Return", new Float(4.0));
		gradeBreakdown.put("Param", new Float(5.0));
		gradeBreakdown.put("Throws", new Float(1.0));
		gradeBreakdown.put("Author", new Float(0.0));
		gradeBreakdown.put("Version", new Float(0.0));
		gradeBreakdown.put("See", new Float(0.0));
		gradeBreakdown.put("Since", new Float(0.0));
		
		// Set the default values for possible scores
		possibleScore.put("Return", new Integer(0));
		possibleScore.put("Param", new Integer(0));
		possibleScore.put("Throws", new Integer(0));
		possibleScore.put("Author", new Integer(0));
		possibleScore.put("Version", new Integer(0));
		possibleScore.put("See", new Integer(0));
		possibleScore.put("Since", new Integer(0));
		
		// Set the default values for the points earned
		points.put("Return", new Integer(0));
		points.put("Param", new Integer(0));
		points.put("Throws", new Integer(0));
		points.put("Author", new Integer(0));
		points.put("Version", new Integer(0));
		points.put("See", new Integer(0));
		points.put("Since", new Integer(0));
	}

	/**
	 * Examine each class, obtain each method. Check for returns tag.
	 * Check for throw tag. Check for param tags.
	 *
	 * @param 	rootDoc  the root of the documentation tree
	 * @return	Properties list
	 */
	public Properties analyze(RootDoc rootDoc)
	{
		// Set doclet title
		// The doclet title must match the entry in the doclets table in
		// the database.
		prop.setProperty("title", "Check for Tags");
		
		// Check for author tag if required
		if ((requireAuthor & OVERVIEW) == OVERVIEW)
		{
			// Add to possible points
			possibleScore.put("Author", new Integer(1));
		
			// Check if the tag exists and contains text
			if (checkForTag(rootDoc, "@author"))
			{
				points.put("Author", new Integer(1));
				prop.setProperty("000", "Found @author tag.");
			}
			else
				prop.setProperty("000",
					"@author tag required, but not found.");
		}
	
		// Check for version tag if required
		if ((requireVersion & OVERVIEW) == OVERVIEW)
		{
			// Add to possible points
			possibleScore.put("Version", new Integer(1));
			
			// Check if the tag exists and contains text
			if (checkForTag(rootDoc, "@version"))
			{
				points.put("Version", new Integer(1));
				prop.setProperty("001", "Found @version tag.");
			}
			else
				prop.setProperty("001",
					"@version tag required, but not found.");
		}

		// Check for see tag if required
		if ((requireSee & OVERVIEW) == OVERVIEW)
		{
			// Add to possible points
			possibleScore.put("See", new Integer(1));
			
			// Check if the tag exists and contains text
			if (checkForTag(rootDoc, "@see"))
			{
				points.put("See", new Integer(1));
				prop.setProperty("002", "Found @see tag.");
			}
			else
				prop.setProperty("002", "@see tag required, but not found.");
		}

		// Check for since tag if required
		if ((requireSince & OVERVIEW) == OVERVIEW)
		{
			// Add to possible points
			possibleScore.put("Since", new Integer(1));
			
			// Check if the tag exists and contains text
			if (checkForTag(rootDoc, "@since"))
			{
				points.put("Since", new Integer(1));
				prop.setProperty("003", "Found @since tag.");
			}
			else
				prop.setProperty("003", "@since tag required, but not found.");
		}

		// Count variables for score
		int return_max = 0, return_points = 0;
		int param_max = 0, param_points = 0;
		int throws_max = 0, throws_points = 0;
		
		// Process each class
		ClassDoc[] classes = rootDoc.classes();
		
		for (int i=0; i < classes.length; i++)
		{
			// Leave numbers 0 - 24 open for general class properties
			String classID = numberFormat(i + 25);
			
			// Store the name of the class being processed
			prop.setProperty("" + classID, "Class: " + classes[i].name());
			
			// Check for author tag if required
			if ((requireAuthor & CLASS) == CLASS)
			{
				// Add to possible points
				possibleScore.put("Author",
					new Integer(possibleScore.get("Author") + 1));
		
				// Check if the tag exists and contains text
				if (checkForTag(classes[i], "@author"))
				{
					points.put("Author", new Integer(points.get("Author") + 1));
					prop.setProperty(classID + ".000", "Found @author tag.");
				}
				else
					prop.setProperty(classID + ".000",
						"@author tag required, but not found.");
			}

			// Check for version tag if required
			if ((requireVersion & CLASS) == CLASS)
			{
				// Add to possible points
				possibleScore.put("Version",
					new Integer(possibleScore.get("Version") + 1));
				
				// Check if the tag exists and contains text
				if (checkForTag(classes[i], "@version"))
				{
					points.put("Version",
						new Integer(points.get("Version") + 1));
					prop.setProperty(classID + ".001", "Found @version tag.");
				}
				else
					prop.setProperty(classID + ".001",
						"@version tag required, but not found.");
			}

			// Check for see tag if required
			if ((requireSee & CLASS) == CLASS)
			{
				// Add to possible points
				possibleScore.put("See",
					new Integer(possibleScore.get("See") + 1));
				
				// Check if the tag exists and contains text
				if (checkForTag(classes[i], "@see"))
				{
					points.put("See", new Integer(points.get("See") + 1));
					prop.setProperty(classID + ".002", "Found @see tag.");
				}
				else
					prop.setProperty(classID + ".002",
						"@see tag required, but not found.");
			}

			// Check for since tag if required
			if ((requireSince & CLASS) == CLASS)
			{
				// Add to possible points
				possibleScore.put("Since",
				new Integer(possibleScore.get("Since") + 1));
				
				// Check if the tag exists and contains text
				if (checkForTag(classes[i], "@since"))
				{
					points.put("Since", new Integer(points.get("Since") + 1));
					prop.setProperty(classID + ".003", "Found @since tag.");
				}
				else
					prop.setProperty(classID + ".003",
						"@since tag required, but not found.");
			}

			// Id to use in field or method portion of number format
			// Leave numbers 0 - 24 open for general class properties;
			int id2 = 25;
			
			// Get fields
			FieldDoc[] fields = classes[i].fields();
			for (int j = 0; j < fields.length; j++)
			{
				String fieldId = numberFormat(id2++);

				// Store field name
				prop.setProperty(classID + "." + fieldId,
					"Field: " + fields[j].name());
			
				// Check for @see tag if required
				if ((requireSee & FIELD) == FIELD)
				{
					// Add to possible points
					possibleScore.put("See", 
						new Integer(possibleScore.get("See") + 1));
			
					// Check if the tag exists and contains text
					if (checkForTag(fields[j], "@see"))
					{
						points.put("See", new Integer(points.get("See") + 1));
						prop.setProperty(classID + "." + fieldId + ".002",
							"Found @see tag.");
					}
					else
						prop.setProperty(classID + "." + fieldId + ".002",
							"@see tag required, but not found.");
				}
			
				// Check for @since tag if required
				if ((requireSince & FIELD) == FIELD)
				{
					// Add to possible points
					possibleScore.put("Since",
					new Integer(possibleScore.get("Since") + 1));
					
					// Check if the tag exists and contains text
					if (checkForTag(fields[j], "@since"))
					{
						points.put("Since",
							new Integer(points.get("Since") + 1));
						prop.setProperty(classID + "." + fieldId + ".003",
							"Found @since tag.");
					}
					else
						prop.setProperty(classID + "." + fieldId + ".003",
							"@since tag required, but not found.");
				}
			}  // end of loop processing fields

			// =========================================
			// METHOD-LEVEL PROCESSSING
			// =========================================
			MethodDoc[] methods = classes[i].methods();
			for (int j = 0; j < methods.length; j++)
			{
				String methodID = numberFormat(id2++);
			
				// Store method name
				prop.setProperty(classID + "." + methodID, "Method: " +
					methods[j].name());
			
				// ===============================
				// Check for @see tag, if required
				// ===============================
				if ((requireSee & METHOD) == METHOD)
				{
					// Add to possible points
					possibleScore.put("See",
						new Integer(possibleScore.get("See") + 1));
				
					// Check if the tag exists and contains text
					if (checkForTag(methods[j], "@see"))
					{
						points.put("See", new Integer(points.get("See") + 1));
						prop.setProperty(classID + "." + methodID + ".002",
							"Found @see tag.");
					}
					else
						prop.setProperty(classID + "." + methodID + ".002",
							"@see tag required, but not found.");
				}
				
				// =================================
				// Check for @since tag, if required
				// =================================
				if ((requireSince & METHOD) == METHOD)
				{
					// Add to possible points
					possibleScore.put("Since",
						new Integer(possibleScore.get("Since") + 1));
		
					// Check if the tag exists and contains text
					if (checkForTag(methods[j], "@since"))
					{
						points.put("Since",
						new Integer(points.get("Since") + 1));
						prop.setProperty(classID + "." + methodID + ".003",
							"Found @since tag.");
					}
					else
						prop.setProperty(classID + "." + methodID + ".003",
							"@since tag required, but not found.");
				}

				// =========================
				// Checking the @return tags
				// =========================
				// Obtain the actual return type of method
				String returnType = methods[j].returnType().typeName();

				// Obtain all @return tags for the current method.
				Tag[] returnTags = methods[j].tags("@return");
				
				// Increase the maximum number of points one can earn from this
				// section. Points are awarded below if the results are correct
				// or are partially correct.
				return_max += 4;
				
				// If return type is void, no @return tag is required.
				if (returnType.equals("void"))
				{
					if (returnTags.length == 0)
					{
						// No @return tag(s) present in comments, correct
						prop.setProperty(classID + "." + methodID + ".b",
							"@return tag not required by this method.");
						return_points += 4;
					}
					else
					{
						// If one or more @return tag is present, incorrect
						prop.setProperty(classID + "." + methodID + ".b",
							"The @return tag should not be present when " +
							"the method's return type is 'void'. " +
							"(PARTIALLY CORRECT)");
						return_points += 3;
					}
				}
				else
				// If return type is not void, one @return tag is required.
				{
					// If exactly one @return tag is present in the comments
					if (returnTags.length == 1)
						// Check that a non-blank comment string is present
						// following the @return tag.
						if (returnTags[0].text().trim().equals(""))
						{	// A blank comment string was found
							prop.setProperty(classID + "." + methodID + ".b", 
								"Although a @return tag is present, there " +
								"is no comment following the tag to describe " +
								"the return type, " + returnType + 
								".  (PARTIALLY CORRECT)");
							return_points += 2;
						}
						else
						{
							// A non-blank comment string was found
							prop.setProperty(classID + "." + methodID + ".b",
								"A @return tag is present and there is a " +
								"comment describing the return type.");
							return_points += 4;
						}

					else if (returnTags.length==0)
						// If no @return tags are present in the comments,
						// incorrect
						prop.setProperty(classID + "." + methodID + ".b", 
						"There is no @return tag present to describe the " +
						"returned value (of type '" + returnType + 
						"'). (INCORRECT)");

					else if (returnTags.length > 1)
						// If multiple @return tags are located in the comments,
						// then this is too vague, incorrect.
						prop.setProperty(classID + "." + methodID + ".b", 
						"Multiple @return tags were detected. A method's " +
						"comments should only be documented with one @return " +
						"tag, assuming the method returns a value or " +
						"reference. (INCORRECT)");
				}

				// ========================
				// Checking the @param tags
				// ========================
				Tag[] paramTags = methods[j].tags("@param");
				Parameter[] parameter = methods[j].parameters();
				int paramCount = 0;
				
				// Iterate through list of parameters for this method
				for (int s = 0; s < parameter.length; s++)
				{
					boolean check = false;
					for (int q = 0; !check && q < paramTags.length; q++)
					{
						ParamTag ptag = (ParamTag) paramTags[q];
						// Determine if the @param tag value matches the
						// formal parameter name
						if (ptag.parameterName().equals(parameter[s].name()))
							check = true;

						// If we have a @param tag / formal parameter match
						if (check)
						{
							String count = numberFormat(paramCount);
							
							// Check to see if this @param tag has a comment
							if (ptag.parameterComment().trim().equals(""))
								prop.setProperty(classID + "." + methodID +
									".c" + count, "The @param tag associated " +
									"with the formal parameter '" +
									parameter[s].name() + "' is present, but " +
									"the comment for this parameter is " +
									"missing from the tag. (INCORRECT)");
							else
							{
								prop.setProperty(classID + "." + methodID +
									".c" + count, "A @param tag is present, " +
									"the parameter is named, and there is " +
									"a comment describing the parameter '" +
									parameter[s].name() + "'.");
								param_points++;
							}
							paramCount++;
						}
					}
					// If we never find a match for the current formal
					// parameter, then alert the user.
					if (!check)
					{
						String count = numberFormat(paramCount);
						prop.setProperty(classID + "." + methodID + ".c" +
							count,  "There is no @param tag present to " +
							"describe the parameter named '" + parameter[s].name() + "'. (INCORRECT)");
						paramCount++;
					}
					param_max++;
				}
				
				// Iterate through list of param tags, searching to tags that
				// to not match a formal parameter.
				for (int s = 0; s < paramTags.length; s++)
				{
					ParamTag ptag = (ParamTag) paramTags[s];
					
					boolean check = false;
					for (int q = 0; !check && q < parameter.length; q++)
						// If the param tag matches the actual parameter name
						if (ptag.parameterName().equals(parameter[q].name()))
							check = true;
	
					if (!check)
					{
						String count = numberFormat(paramCount);
						prop.setProperty(classID + "." + methodID + ".c" +
							count, "The @param tag named '" +
								ptag.name() + "' fails to match any " + 
								"formal parameter. (INCORRECT)");
						
						// Increase the maximum points for this parameter
						// by one, such that it lowers the score.
						paramCount++;
						param_max++;
					}
				}

				// =========================
				// Checking the @throws tags
				// =========================
				String throwParam = "@throws";
				Tag[] throwsTags = methods[j].tags(throwParam);
				ClassDoc[] exceptions = methods[j].thrownExceptions();
				int throwsCount=0;
				
				// Iterate through the list of exceptions
				for (int s = 0; s < exceptions.length; s++)
				{
					boolean check = false;
					for (int q = 0; !check && q < throwsTags.length; q++)
					{
						ThrowsTag ttag = (ThrowsTag) throwsTags[q];

						// If the @throws tag matches the actual exception
						if (ttag.exceptionName().equals(exceptions[s].name()))
							check = true;
	
						if (check)
						{
							// Format number to 3 digits
							String num = numberFormat(throwsCount); 

							// Check that there is a comment
							if (ttag.exceptionComment().trim().equals(""))
								prop.setProperty(classID + "." + methodID +
									".d" + num, "The @throws tag associated " +
									"with the exception named '" + exceptions[s].name() + "' is present, " +
									"but the comment for this exception " + 
									"is missing from the tag. (INCORRECT)");
							else
							{
								prop.setProperty(classID + "." + methodID +
								".d" + num, "A @throws tag is present, the " +
								"exception is named, and there is a comment " +
								"describing the exception named '" +
								exceptions[s].name() + ".");
								throws_points++;
							}
							throwsCount++;
						}
					} // end of loop

					if (!check)
					{
						//format number to 3 digits
						String num = numberFormat(throwsCount);
						prop.setProperty(classID + "." + methodID + ".d" +
						num, "There is no @throws tag present to describe " +
						"the exception named '" + exceptions[s].name() +
						". (INCORRECT)");
						throwsCount++;
					}
					throws_max++;
				}

				// Iterate through list of throw tags
				for (int s = 0; s < throwsTags.length; s++)
				{
					ThrowsTag ttag = (ThrowsTag) throwsTags[s];
					
					boolean check = false;
					for (int q = 0; !check && q < exceptions.length; q++)
						// If the throws tag matches the actual exception name
						if (ttag.exceptionName().equals(exceptions[q].name()))
							check = true;

					if (!check)
					{
						String num = numberFormat(throwsCount);
						prop.setProperty(classID + "." + methodID + ".d" + num,
							"The @throws tag named '" + throwsTags[s].text() + 
							"' fails to match an exception type. (INCORRECT)");

						// Increase the maximum points for throws by one, 
						// such that it lowers the score.
						throwsCount++;
						throws_max++;
					}
				}
			}
		}

		// Store the maximum number of points for each category in the map.
		possibleScore.put("Return", new Integer(return_max));
		possibleScore.put("Param", new Integer(param_max));
 		possibleScore.put("Throws", new Integer(throws_max));
		
		// Store the number of points obtained for each category in the map.
		points.put("Return", new Integer(return_points));
		points.put("Param", new Integer(param_points));
 		points.put("Throws", new Integer(throws_points));
		
		// Store the overall grade for this module in the map.
		prop.setProperty("score", "" + getGrade());
		
		return prop;
	}

	/*************************************************************************
	 * Converts an integer value to 3 digits (ex. 7 --> 007)
	 *
	 * @return Returns the value formatted as a three digit string, padded
	 * with zeros.
	 ************************************************************************/
	private String numberFormat(int value)
	{
// 		DecimalFormat formatter = new DecimalFormat("000");
// 		return formatter.format(value);
		String newValue;
		if (value < 10)
			newValue = "00" + value;
		else if (value<100)
			newValue = "0" + value;
		else
			newValue = "" + value;
	
		return newValue;
	}

	/*************************************************************************
	 * Sets the grading breakdown for this analysis module, set by sections.
	 *
	 * @param sectionName The section name
	 * @param maxGrade The maximum grade for the specified section
	 *************************************************************************/
	public void setGradingBreakdown(String sectionName, float maxGrade)
	{
		gradeBreakdown.put(sectionName, new Float(maxGrade));
	}


	/*************************************************************************
	 * Returns the grade for this analysis module.
	 *
	 * @return The grade for the doclet
	 *************************************************************************/
	public float getGrade()
	{
		int score, possible;
		int idx = 0;
		float max, total = (float) 0.0, percent;
	
		// Define the grading sections
		String sections[] = new String[7];
		sections[idx++] = "Return";
		sections[idx++] = "Param";
		sections[idx++] = "Throws";
	
		// Add in the optional sections
		if (requireAuthor != 0)
			sections[idx++] = "Author";
		if (requireVersion != 0)
			sections[idx++] = "Version";
		if (requireSee != 0)
			sections[idx++] = "See";
		if (requireSince != 0)
			sections[idx++] = "Since";
	
		// Obtain the grade for each section
		for (int i = 0; i < idx; i++)
		{
			possible = possibleScore.get(sections[i]);
			score = points.get(sections[i]);
			max = gradeBreakdown.get(sections[i]);
			
			percent = (possible == 0) ? (float) 1.0 : (float) score/possible;
			total += percent * max;
		}
	
		return total;
	}


	/*************************************************************************
	 * Sets a parameter used for doclet grading.
	 *
	 * @param paramName The name of the grading parameter
	 * @param paramValue The grading value of the parameter
	 *************************************************************************/
	public void setGradingParameter(String paramName, String paramValue)
	{
		// Parse options from value
		try
		{
			int combination = 0;
			Scanner scan = new Scanner(paramValue);
			while (scan.hasNext())
			{
				// Determine the option
				String option = scan.next();
				if (option.equalsIgnoreCase("OVERVIEW"))
					combination |= OVERVIEW;
				if (option.equalsIgnoreCase("CLASS"))
					combination |= CLASS;
				if (option.equalsIgnoreCase("FIELD"))
					combination |= FIELD;
				if (option.equalsIgnoreCase("METHOD"))
					combination |= METHOD;
			}
       
			// Optional tags
			if (paramName.equals("Author"))
			{
				// Mask value with possible values
				requireAuthor = combination & maskAuthor;
			}
			else if (paramName.equals("Version"))
			{
				// Mask value with possible values
				requireVersion = combination & maskVersion;
			}
			else if (paramName.equals("See"))
			{
				// Mask value with possible values
				requireSee = combination & maskSee;
			}
			else if (paramName.equals("Since"))
			{
      			// Mask value with possible values
				requireSince = combination & maskSince;
			}
     	}
		catch (Exception e)
		{}
	}


	/*************************************************************************
	 * Sets the configuration properties loaded from the config file
	 *
	 * @param props Properties list
	 *************************************************************************/
	public void setConfigProperties(Properties props)
	{
		// Configuration properties are not used in this module.
	}


	/*************************************************************************
	 * Checks if the specified Doc object contains at least one instance of 
	 * the specified tag.  The tag must also contain some non-blank text.
	 *
	 * @param doc The Doc object searched
	 * @param tag The name of the tag to look for, must include the @ sign
	 * @return Boolean indicating if the tag was found.
	 *************************************************************************/
	private boolean checkForTag(Doc doc, String tag)
	{
		boolean returnValue = false;

		// Check if the tag exists and contains text
		Tag [] tags = doc.tags(tag);

		if (tags.length > 0)
		{
			// Search for at least one occurence of the specified tag
			// that also contains non-blank corresponding text
			for (int index = 0; !returnValue && index < tags.length; index++)
				if (!tags[index].text().equals(""))
					returnValue = true;
		}

		return returnValue;
	}

	/**
	 * Returns the string representation of this module's report (JSON format)
	 *
	 * @return a string value containing the JSON report
	 */
	public String getJSONReport() {
		return null;
	}
}