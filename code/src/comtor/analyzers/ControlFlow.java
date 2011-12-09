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
package comtor.analyzers;

import comtor.*;
import com.sun.javadoc.*;
import java.util.*;
import java.text.*;
/**
 * The ControlFlow class is an analysis module used to validate the user-documentation of the control flow of a method. 
 * This Javadoc based implementation can only check whether method calls documented are possible within the directory sent to COMTOR, with no regards to access modifiers or  
 * their actual existence in the source code 
 * The analysis is therefore not accurate in relation to what is contained in the source, and also does not check classes from the Java API yet. 
 * uses the @control_flow tag
 *
 */
public class ControlFlow implements ComtorDoclet
{
	private Properties prop = new Properties();
	private HashMap<String, Float> gradeBreakdown =
		new HashMap<String, Float>();
	private HashMap<String, Integer> possibleScore = 
		new HashMap<String, Integer>();
	private HashMap<String, Integer> points = new HashMap<String, Integer>(); 
        
	//The exact javadoc tag the analyzer will be looking for
        private String tagName="@control_flow";

	/**
	 * need to implement scoring methods
	 */
	public ControlFlow()
	{
		
	}

	/**
	 * Examine each class, obtain each method. Check for controlFlow tag.
	 * Check each part of the received tag for accuracy.
	 * Javadoc implementation, when finished, will only be able to check accuracy of tagged based on RootDoc info. 
	 * A different parser will be needed to traverse source code.
	 *
	 * The current architecture of the analyze method works as follows: 
	 * Get list of all classes in rootDoc.
	 * For each class do the following: 
	 * {     
	 *	Store the name of class as property www.xxx.000.000, 
	 *      www is a 3 digit representation of the package the class was found in. (000) if unpackaged. 
         *      Other package numbers are assigned in the order the first class of a given package is traversed. package numbers stored in int arraylist object packages
	 *      xxx is a 3 digit representation of the index at which the class was called, which is reset for each package.
	 *      
	 * 	Get list of all constructors in the current class 
	 *      If the constructor has a @controlFlow tag, and the tag is not empty, do the following: 
	 *           Record name of constructor into location in Properties object prop in form xxx.yyy.000 where xxx is the class and yyy is the method "number" 
	 *           run processTag for the String representation of the tag found
	 *      
	 * 	The process listed above for constructors in the above paragraph is then repeated for every non-constructor method of the class    
	 * }
	 * After all the methods and constructors of the current class have been processed, the loop goes to the next class and repeats itself until all classes are processed 
	 *
	 * Once each class has been processed, analyze returns a Properties object containing the method's evaluation.
	 *
	 * See "processTag" and "processMethodCall", as they are the main helper methods of analyze and contain most of the code modifying the Properties object  
	 *
	 * @param 	rootDoc  the root of the documentation tree
	 * @return	Properties list
	 */
	public Properties analyze(RootDoc rootDoc)
	{
	    
		//Make list of every class in rootDoc
                ClassDoc[] classes= rootDoc.classes();

		//Stores the name of each unique package found, starting at index 1 ( 0 is reserved for unpackaged classes)
		ArrayList<String> packages= new ArrayList<String>();
		// Stores the number of classes contained in each package listed in packages. 
		// The class number for a class is equal to the classCount, after the class' addition to the count
		ArrayList<Integer> classCount= new ArrayList<Integer>();
		
	        //reserve 0 as package number for unpackaged classes. "" is returned whenever a package name is non-existent in call ClassDoc.containingPackage.name()
		packages.add(0,"");		
		//Set unpackaged classes count to 0
		classCount.add(0,0);
                
		//Used to determine what the first 3 digits of the property will be, based in which package the class is in and where it appears in ArrayList packages
		//Value remains at 0 if it belongs to no package.                                                                                                             
		int packageNumber;

		//Iterate through each class, then check each method and constructor for the existence and correctness of controlFlow tags
		for(int i=0; i<classes.length; i++) 
		{ 
		    // Determine package number and class number for the class being analyzed
		    packageNumber=-1;
		    /* Find out if class is in a previously-found package. 
		     * If so, give class a package number the same as that package's index. 
		     * If not, add the package name to the list.*/ 
		    String packageName=classes[i].containingPackage().name();
		    int p;
		    for(p=0; p<packages.size(); p++)
			{ 
			    // if there is a package found with a name matching the current class' package
			    if(packages.get(p).equals(packageName))
				{
				    // class uses the package number of package with name matching its own 
				    packageNumber=p; 
				    // increment classCount for that package. classCount beco
				    classCount.set(packageNumber, classCount.get(packageNumber)+1);
				    //exit loop if package name is found
				    p=packages.size()+5;
				} 
			}
		    // if no pre-existing package number was found
		    if(packageNumber==-1) 
			{ 
			    // Add package name to packages array, thus giving it a number		
			    packages.add(packageName);
			    // Also update classCount for the new class (at the end of ArrayList), setting it to 1
			    classCount.add(1);
		            // set packageNumber equal to new package number at (the end of the ArrayList)
			    packageNumber=packages.size()-1;
			    prop.setProperty(numberFormat(packageNumber)+".000.000.000", packageName);
			}
		
				
		//The number corresponding to the current class (resets for each package)
		int classID=classCount.get(packageNumber);
		
		prop.setProperty(numberFormat(packageNumber)+"."+numberFormat(classID)+".000.000", classes[i].name());
		
		//Processes each constructor of the current class
		ConstructorDoc[] constructors=classes[i].constructors();
		int j;
		for(j=0; j<constructors.length; j++)
		    {
			//if the @controlFlow tag exists for a constructor and has text, run processTag to check the validity
			String controlFlowText=getTagText(constructors[j],tagName);
			if(!controlFlowText.equals(""))
			    {    
				processTag(rootDoc,packageNumber, classID, j+1, classes[i], constructors[j].name(), controlFlowText);
			    }
		    }
		//Processes the current class' methods
		MethodDoc[] methods= classes[i].methods();
		for(int k=0; k<methods.length; k++)
		    {
			//if the @controlFlow tag exists for a method and has text, run processTag to check the validity   
			String controlFlowText=getTagText(methods[k],tagName);
			if(!controlFlowText.equals(""))
			    {    
				processTag(rootDoc,packageNumber, classID, j+k+1, classes[i], methods[k].name(), controlFlowText);
			    }
		    }
		}
   		// before adding a section for unpackaged classes to prop, make sure there actually are unpackaged classes in the directory
		if(classCount.get(0)>0)
		    prop.setProperty("000.000.000.000", "Unpackaged Classes:");
	 //Return properties object containing feedback on the tags that were checked. Most of the prop modification is done in processTag and processMethodCall
	    return prop;
	}
/** 
  * Checks each non-whitespace tag found in a method or constructor by analyze. processTag writes to prop a message concerning the overall 
 * correctness of the tag (in relation to what ControlFlow can verify). 
 * Each method call within a given tag is checked by processMethodCall. 
 * processMethodCall returns true or false, true if the method call meets ControlFlow's current definition of correctness, false otherwise. 
 * If any calls to processMethodCall return false, processTag writes a message telling the user so, if not, it claims the tag is "correct".

 * @param rootDoc same RootDoc sent to analyze method. passed on to processMethodCall. 
 *
 * @param pkgID number assigned to package of tagged method, used to organize prop object. first number of four number tag. 
 * @param classID number assigned to class of tagged method. second number of four in tag. 
 * @param methodID number assigned to tagged method. third number of tag. 
 * 
 * @param taggedMethodClass class containing the tagged method. used by processMethodCall
 * @param methodName name of the tagged class. Used when writing to the prop object, as well as by processMethodCall
 * 
 * @param tagContents String representation of the contents of the tag being checked. 
 * e.g. for the tag @control_flow method1!method2, tagContents="method1!method2"
*/
private void processTag(RootDoc rootDoc, int pkgID, int classID, int methodID, ClassDoc taggedMethodClass, String methodName, String tagContents) 
{ 
    //used to make fourth number in tag for writing to prop, to print result of processTag so it does not overwrite any outcomes of processMethodCall
    int methodCount=0;
    int index=0;
    // index referring to the end of the last symbol checked in the method call
    int tokenBegin;
    String currentChar;
    boolean tagValid=true;
    /* Syntax for a tag should always be such that the first method call is to the tagged method e.g. a tag for method1 should start as method1!method2! 
	A call to processMethodCall with this variable being true will make processMethodCall return false if the first method call tag is not the tagged method*/
    boolean firstMethod=true;
    /*iterates through the tag, calling to proce4ssMethodCall each time either a '!' or the end of the String is reached */
    do{
	currentChar=""+tagContents.charAt(index);
	tokenBegin=index;
	//checks for token separating method calls, or the end of rthe String. Will need to be updated to an array-like structure if tag syntax is expanded beyond just '!'	
	while(!currentChar.equals("!") && index<tagContents.length())
	    {
		index++;
		if(index<tagContents.length())
		    currentChar=""+tagContents.charAt(index);
	    }
	// Once a token (!) is found or the end of the tag is reached, the portion of the String from the last section checked to the token or end of string is checked by processMethodCall
	// Any method call returning false will make processTag label the entire tag as incorrect 
	if(!processMethodCall(rootDoc, pkgID, classID, methodID, methodCount+1, tagContents.substring(tokenBegin,index), taggedMethodClass, methodName, firstMethod))
	    tagValid=false;
	// processMethodCall does not check the method call refers to the tagged method after the first method call is checked	
	firstMethod=false;
	methodCount++;
	index++;
	
    }while(index<=tagContents.length());
    // Determines what to report to user regarding overall success of tag. 
    if(tagValid) 
	prop.setProperty(numberFormat(pkgID)+"."+numberFormat(classID)+"."+numberFormat(methodID)+"."+numberFormat(methodCount+1), 
			 tagContents+" is a correct tag.");
    else
	prop.setProperty(numberFormat(pkgID)+"."+numberFormat(classID)+"."+numberFormat(methodID)+"."+numberFormat(methodCount+1), 
			 tagContents+" is an incorrect tag.");
    return;
}
/** 
 * Checks each method call for accuracy (so far, processMethodCall only makes sure that a method call consists of a valid sequence of a package, Class, field(s), and method. 
 * While processMethodCall can determine if the package contains the class in the tag, and the class contains the field and etc, it does not validate a 
 * method call's existence in the source code
 * 
 * @return boolean value indicating validity of method call sent to processMethodCall. Currently, "false" means an error was detected in method call
 * syntax or content, whereas "true" means no error was found. Returning "true" does not mean a method call exists in the source code, as of now. 
 * 
 * @param rootDoc same RootDoc object received by analyze and processTag. used to find and validate classes, packages, and methods in method call
 * 
 * @param pkgID number assigned to package of tagged method, used to organize prop object. first number of four number tag. 
 * @param classID number assigned to class of tagged method. second number of four in tag. 
 * @param methodID number assigned to tagged method. third number of tag. 
 * @param partID number assigned to each method call of a tag, 1 being the leftmost and incrementing as each method call is processed. fourth number in tag
 * 
 * @param methodCall String representation of the method call being checked by this method 
 * 
 * @param taggedMethodClass class containing the tagged method. used for instances where the class or package name is not in method call.
 * 
 * @param callingMethodName the name of the tagged method. used to make sure first method call refers to the tagged method
 * @param firstMethod boolean indicating whether or not this is the first method call of the tag to be checked.
*/

private boolean processMethodCall(RootDoc rootDoc, int pkgID, int classID, int methodID, int partID, String methodCall, ClassDoc taggedMethodClass,String callingMethodName,boolean firstMethod)
{ 
    int index=methodCall.length()-1;
    String currentChar;
    //int value of where tghe last symbol in the method call ended
    int tokenEnd;
    // number of fields processMethodCall believes exist in the call
    int fieldCount;
    //stores name of each symbol processMethodCall believes to be a field. ArrayList is filled left to right, and then checked right to left later in code
    ArrayList <String> fields= new ArrayList<String>();
    // name of method in method call (should be rightmost symbol)
    String methodName=null; 
    // qualified class name found in the method call (or assumed, see code below)
    String className=null;
    // package that contains the tagged method. This is the leftmost method in the TAG the method call is a part of, not necessarily the current method 
    //call.
    String taggedPackageName= taggedMethodClass.containingPackage().name();
    //boolean values used to keeop track if the part of the method call they refer to has been found.
    boolean methodFound=false;
    boolean classFound=false;
    
    /* Iterates through methodCall String one character at a time, from right to left.
     * Every time a period is found, the String goes through a series of checks:
     *
     * If it is the first period to be found (or reaches the end of the String before a period), the String to the left of it is considered to be the method name.
     *  
     * Each subsequent check is to determine if the remainder is a class name, and which parts are fields until the end of the String is reached.
     *  
     * If the remainder is not a class name or qualified class name, the section from the last period found to the next one (or the end of the String)
     * is stored as a potential field name, and is no longer checked as part of the remainder.
     *
     * If no class name or qualified class name can be found in the remainder of the tag before reaching the end, the method is assumed to be a method 
     * of the same class as the tagged method.
     */	
    
    // iterates through entire methodCall String from right to left, or until a class name has been found
    while(index>=0 && !classFound)
    {
	currentChar=""+methodCall.charAt(index);
	tokenEnd=index+1;
	//Look at each character, if a period or end of string is reached, check the portion of the string between this and the last check(if not first)
	while(!currentChar.equals(".") && index>0)
	    {
		index--;
		if(index>=0)
		    currentChar=""+methodCall.charAt(index);
	    }
	//This is run for the first token found, the method name
	if(!methodFound)
	    {
		//if-else statement adjusts index so period is not included in method name
		if(currentChar.equals("."))
		    methodName=methodCall.substring(index+1, tokenEnd);
		else 
		    methodName=methodCall.substring(index, tokenEnd);
		//if it is the first method call in the tag (determined by processTag, above), it should match the name of the tagged method
		//if it is the first method call and does NOT match the tagged method,return false and notify user of the error
		//continue checking the method call otherwise
		if(firstMethod && !callingMethodName.equals(methodName))
		    {   
			prop.setProperty(numberFormat(pkgID)+"."+numberFormat(classID)+"."+numberFormat(methodID)+"."+numberFormat(partID), 
					 methodName +" is not a valid tag: first method in tag is not the original method containing @controlFlow tag ("
					 +callingMethodName+").");
			return false;
		    }
		methodFound=true;
	    }
	// These checks are run for the remainder of the methodCall String after the method name is found 
	else 
	    { 
		//check if remainder is fully qualified name of a class 
		if(rootDoc.classNamed(methodCall.substring(0,tokenEnd)) != null)
		    {    
			//if true, make remaining portion of method call the class name 
			className=methodCall.substring(0,tokenEnd);
			classFound=true;
		    }
		
		//check if remainder is the name of a class in the same package as the method the tag was found in by appending package of the tagged method to the remaining part of the method call
		else if(rootDoc.classNamed(taggedPackageName+"."+methodCall.substring(0, tokenEnd)) != null)
		    { 
			//if true, append remainder of methodCall to the package name to get the fully qualified class name
			className=taggedPackageName+"."+methodCall.substring(0,tokenEnd); 
			classFound=true;
		    }
		
		//if not, add current token to field array.
		else 
		    {
			if(index==0) 
			    fields.add(methodCall.substring(0,tokenEnd));
			else
			    fields.add(methodCall.substring(index+1,tokenEnd));
		    }	       
	    }
	// Checks for the class name continue until either the class name is found or the end of the string is reached
	index--;
    }
    //if a class name is not found in the method call String, assume the method belongs to the class of the tagged method
    if(!classFound) 
	{    
	    className=taggedMethodClass.name();
	    if(taggedPackageName != "") 
		className=taggedPackageName +"."+ className; 
        }
    /* Get the ClassDoc of whatever class ControlFlow believes the method is a member of. Following the if-else ladder above, this will be one of the following:
     * 1) The qualified name of the class specified in the tag 
     * 2) The package of the tagged method appended to a class name contained in the method call
     * 3) If there is class name at all, the class will be assumed to have the same qualified name as the tagged method.
     */
    ClassDoc currentClass=rootDoc.classNamed(className);   
    
    //see if class indicated by className has the fields from list, if any potential fields were found
    //in the case of multiple consecutive fields (i.e. fields calling fields) the loop will check if the first field belongs to whatever class was found
    // the next field will be looked for in the previous field's class type and so on until either all fields are found or a field is not found
    
    //initialized true for cases where there are no potential fields found
    boolean fieldFound=true; 
    //iterates through each member of fields ArrayList, from right to left.
    for(int i=fields.size()-1; i>=0; i--)
	{
		//set fals at each iteration, only made true again if field at index i is found in currentClass
		fieldFound=false;
		//Checks each field of the current class to find a match for the field at index i
		for(FieldDoc currentField : currentClass.fields())
		{
		    /* If currentClass contains a field with the same name as fields.get(i), set the class to look in for the next field (if any) 
		     *  equal to the class fields.get(i) is made from. Also set fieldFound=true, allowing the loop to continue.*/
		    if(currentField.name().equals(fields.get(i)))
			{	
			    currentClass=currentField.type().asClassDoc();
			    fieldFound=true;
			}
		} 
	    /* if there is a field in the array that does not exist in the class immediately before it, processMethodCall will return false 
	       also writes a method to prop indicating the field not being found as a valid symbol*/
	    if(!fieldFound){
		prop.setProperty(numberFormat(pkgID)+"."+numberFormat(classID)+ "." +numberFormat(methodID)+ "." +numberFormat(partID), "Symbol not found: "+fields.get(i));
		return false;
	    }
	
	}

    // Checking to see if the method exists in the last type in the method call.
    //This will be the class of the rightmost field if fields are present, otherwise it will be the class found when looking for a class name

    // check class' methods for method indicated by methodName	
	for(MethodDoc currentMethod : currentClass.methods()) 
	    {
		if(currentMethod.name().equals(methodName))
		    {    
			prop.setProperty(numberFormat(pkgID)+"."+numberFormat(classID) + "." + numberFormat(methodID) + "."+numberFormat(partID), 
					 methodName + " is a valid method of class " + currentClass.name());
			return true;  
		    }
	    }
	// check class' constructors for constructor indicated by methodName
	for(ConstructorDoc currentConstructor : currentClass.constructors())
	    {
		if(currentConstructor.name().equals(methodName))
		    {
			prop.setProperty(numberFormat(pkgID)+"."+numberFormat(classID) + "."+numberFormat(methodID) + "."+numberFormat(partID), 
					 methodName + " is a valid constructor of class " + currentClass.name());
			return true;
		    }
	    }
	
    
    // if the method/constructor cannot be found anywhere in the current class, write that to prop, and return false. 
	prop.setProperty(numberFormat(pkgID)+"."+numberFormat(classID)+"."+numberFormat(methodID)+"."+numberFormat(partID),
	methodName + " does not exist in class " + currentClass.name());
    
    return false;
}


/*************************************************************************
 * Converts an integer value to 3 digits (ex. 7 --> 007)
 *
 * @return Returns the value formatted as a three digit string, padded
 * with zeros.
 ************************************************************************/
private String numberFormat(int value)
{
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
	    float total=0;
	
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
	     
	}


	/*************************************************************************
	 * Sets the configuration properties loaded from the config file
	 
	 * @param props Properties list
	 *************************************************************************/
	public void setConfigProperties(Properties props)
	{
		// Configuration properties are not used in this module.
	}
    /***************************************************************************** 
         * Finds the tag within the doc and returns it. returns empty String if not found. Heavily based on checkForTag method of CheckForTags class.
         * @param Doc object to be searched 
         * @param String name of tag to be looked for (starting with @) 
         * @return String text contents of specified tag type before the first whitespace, empty string returned if tag has no text   
	 *************************************************************************/
    public String getTagText(Doc doc, String tag)
    { 
	Tag[] tags= doc.tags(tag); 
        if(tags.length>0)
	{
	    for(int i=0; i<tags[0].text().length(); i++) 
		if(tags[0].text().charAt(i)==' ') 
		    return tags[0].text().substring(0,i);
	    return tags[0].text();
	}
        else 
            return ""; 
    }
}

