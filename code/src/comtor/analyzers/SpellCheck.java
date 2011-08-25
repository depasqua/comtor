/**
 *  Comment Mentor: A Comment Quality Assessment Tool
 *  Copyright (C) 2011 The College of New Jersey
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
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

/**
 * The SpellCheck class is a tool to check spelling in comments. It is capable of determining
 * if a word is misspelled or a Java class name. Incorrect spellings for the same word are only
 * counted once.
 *
 * @author James Sant'Angelo
 * @author Peter DePasquale
 */
public final class SpellCheck implements ComtorDoclet {
	// The analysis report (property list) returned by this doclet
	private Properties prop = new Properties();
	private float maxScore = 5;
	
	// Correctly spelled words from user comments
	private HashSet<String> goodWords = new HashSet<String>();

	// Incorrectly spelled words from user comments
	private HashSet<String> badWords = new HashSet<String>();

	// User-defined valid words (via the web interface)
	private HashSet<String> validWords = new HashSet<String>();

	// User defined symbols such as class, field, method, and parameter names
	private HashSet<String> userSymbols = new HashSet<String>();

	// Dictionary of English words, Java class names, and HTML tags
	private HashSet<String> dictionary = new HashSet<String>();

	// Define a counter for the properties list
	private int classID = 1;

	// Create a formatter for the report
	private DecimalFormat formatter = new DecimalFormat("000.000");
	
	/**
	 * Performs pre-analysis initialization tasks. For example, loading the various dictionaries
	 * and lists
	 *
	 * @param rootDoc a reference to the top of the parse tree
	 */
	private void init(RootDoc rootDoc) {
		// Build the array of Java keywords. Note that 'java' is part of this list. While not a
		// keyword, it is common enough to make it suitable to add to this list.
		String[] javaKeywords = {"abstract", "assert", "boolean", "break", "byte", "case", "catch",
			"char", "class", "const", "continue", "default", "do", "double", "else", "enum",
			"extends", "final", "finally", "float", "for", "goto", "if", "implements", "import",
			"instanceof", "int", "interface", "long", "native", "new", "package", "private",
			"protected", "public", "return", "short", "strictfp", "super", "switch", "synchronized",
			"this", "throw", "throws", "transient", "try", "void", "volatile", "while", "java"};

		// Load the dictionary and java class listing (both sets contain valid 'words')
		Util.loadDataList("dictionary.txt", dictionary);
		Util.loadDataList("javaclasslist.txt", dictionary);
		Util.loadDataList("htmltags.txt", dictionary);
		
		// Add all user-defined packages, classes, class variables, methods, and parameter names to
		// a list of 'valid' words that may be referenced in a comment. Note that javaDoc does not
		// provide access to local variables within methods and blocks. Thus the list is not
		// exhaustive.
		for (PackageDoc packageDoc : rootDoc.specifiedPackages()) {
			userSymbols.add(packageDoc.name().toLowerCase());
			Scanner packagenameScanner = new Scanner(packageDoc.name().replaceAll("\\.", " "));
			
			// Also add the components of the package name to the list of symbols
			while (packagenameScanner.hasNext())
				userSymbols.add(packagenameScanner.next().toLowerCase());
			
			// Add enumerations to the user-symbol list
			for (ClassDoc enumDoc : packageDoc.enums())
				addClassMembers(enumDoc);
	
			// Add interfaces to the user-symbol list
			for (ClassDoc classDoc : packageDoc.interfaces())
				addClassMembers(classDoc);
				
			// Add errors to the user-symbol list
			for (ClassDoc classDoc : packageDoc.errors())
				addClassMembers(classDoc);
				
			// Add exceptions to the user-symbol list
			for (ClassDoc classDoc : packageDoc.exceptions())
				addClassMembers(classDoc);
		}
		// Add classes to the user-symbol list
		for (ClassDoc classDoc : rootDoc.classes())
			addClassMembers(classDoc);
	}
	
	/**
	 * Adds class and class-member information to the list of user-symbols
	 *
	 * @param classDoc a reference to the ClassDoc object to process
	 */
	private void addClassMembers (ClassDoc classDoc) {
		userSymbols.add(classDoc.name().toLowerCase());
		userSymbols.add(classDoc.qualifiedName().toLowerCase());

		// Adds the class name of inner classes correctly
		if (classDoc.name().contains("."))
			userSymbols.add(classDoc.name().substring(classDoc.name().lastIndexOf(".")+1,
				classDoc.name().length()).toLowerCase());
		
		// Add constructors from the class to the list
		for (ConstructorDoc member : classDoc.constructors()) {
			userSymbols.add(member.name().toLowerCase());
			userSymbols.add(member.qualifiedName().toLowerCase());
			
			for (Parameter param : member.parameters())
				userSymbols.add(param.name().toLowerCase());

			for (ClassDoc excepts : member.thrownExceptions()) {
				userSymbols.add(excepts.name().toLowerCase());
				userSymbols.add(excepts.qualifiedName().toLowerCase());
			}
		}
		
		// Add methods from the class to the list
		for (MethodDoc member : classDoc.methods()) {
			userSymbols.add(member.name().toLowerCase());
			userSymbols.add(member.qualifiedName().toLowerCase());
			
			for (Parameter param : member.parameters())
				userSymbols.add(param.name().toLowerCase());

			for (ClassDoc excepts : member.thrownExceptions()) {
				userSymbols.add(excepts.name().toLowerCase());
				userSymbols.add(excepts.qualifiedName().toLowerCase());
			}
		}
		
		// Add fields from the class to the list
		for (FieldDoc member : classDoc.fields()) {
			userSymbols.add(member.name().toLowerCase());
			userSymbols.add(member.qualifiedName().toLowerCase());
		}
		
		// Recursively do the same for all inner classes found in this class
		for (ClassDoc member : classDoc.innerClasses()) {
			userSymbols.add(member.name().toLowerCase());
			userSymbols.add(member.qualifiedName().toLowerCase());
System.err.println("ClassID="+classID);
			processClass(member);
			classID++;
		}
		
		// Recursively do the same for all interfaces implemented by this class
		for (ClassDoc member : classDoc.interfaces()) {
			userSymbols.add(member.name().toLowerCase());
			userSymbols.add(member.qualifiedName().toLowerCase());
System.err.println("ClassID="+classID);
			processClass(member);
			classID++;
		}
		
		// Add the superclass to the list
 		ClassDoc superclass = classDoc.superclass();
		if (superclass != null) {
			userSymbols.add(superclass.name().toLowerCase());
			userSymbols.add(superclass.qualifiedName().toLowerCase());
		}
		
		// Add the superclass' type to the list
		Type superclassType = classDoc.superclassType();
		if (superclassType != null) {
			userSymbols.add(superclassType.qualifiedTypeName().toLowerCase());
			userSymbols.add(superclassType.simpleTypeName().toLowerCase());
			userSymbols.add(superclassType.typeName().toLowerCase());
		}
		
		// Add the class' type parameters (<E>) to the list
		for (TypeVariable type : classDoc.typeParameters()) {
			userSymbols.add(type.qualifiedTypeName().toLowerCase());
			userSymbols.add(type.simpleTypeName().toLowerCase());
			userSymbols.add(type.typeName().toLowerCase());
		}
	}
	
	/**
	 * Examine each class and its methods. Calculate the length of each method's comments.
	 *
	 * @param rootDoc the root of the documentation tree provided by the JavaDoc parser
	 * @return Properties list
	 */
	public Properties analyze(RootDoc rootDoc) {
		prop.setProperty("title", "Spell Checker");
		
		// Initialize the required tables
		init(rootDoc);
String[] symbolArry = userSymbols.toArray(new String[0]);
Arrays.sort(symbolArry);
System.err.println("Symbols:");
for (String elem : symbolArry) 
	System.err.println(elem);
		
		// Obtain and process the comments for the rootDoc.
		prop.setProperty(formatter.format(0), "The following word(s) is/are misspelled:");
		// Obtain and process the comments for each class
		for (ClassDoc classDoc : rootDoc.classes()) {
System.err.println("ClassID="+classID);
   			if (processClass(classDoc))
				prop.setProperty(formatter.format(classID), "Class: " + classDoc.qualifiedName());
   			classID++;
   		}

		// Set the score for this analysis and return the property list (report)
		prop.setProperty("score", "" + getGrade());
		prop.setProperty("metric1", "A total of " + classID + " classes were processed.");
		prop.setProperty("metric2", "There were " + goodWords.size() + " correctly spelled words.");
		prop.setProperty("metric3", "There were " + badWords.size() + " incorrectly spelled words.");
		prop.setProperty("metric4", "There were a total of " +
			(goodWords.size() + badWords.size()) + " words analyzed.");
		return prop;
	}

	/**
	 * Processes a single class
	 *
	 * @param class Class to process
	 * @param classID The value of the classID counter, used to generate the reporting entry
	 * @return a true value if a word was misspelled, false otherwise
	 */
	private boolean processClass(ClassDoc classDoc) {
		boolean result = false; // only change (once) if there were any misspellings
		float memberNum = 0.000f;
		DecimalFormat smallFormatter = new DecimalFormat(".000");

		// Process the class' comments
		if (parseComment(classID, memberNum, classDoc.commentText()))
			result = true;
		
		// Get all of the class' fields and the field's comments as well
		for (FieldDoc classField : classDoc.fields()) {
			memberNum += 0.001f;
			if (parseComment(classID, memberNum, classField.commentText()))
				result = true;
		}
		
		// Process all of the class's tag comments
		for (Tag tagComment : classDoc.tags()) {
			memberNum += 0.001f;
			if (parseComment(classID, memberNum, tagComment.text()))
				result = true;
		}

		// Process each method in the class
		for (MethodDoc method : classDoc.methods()) {
			// Obtain the method's comments and process
			if (parseComment(classID, memberNum, method.commentText()))
				result = true;

			// Obtain the comments for method's tags (param, returns, etc.) and process each
			for (Tag tagComment : method.tags()) {
				if (parseComment(classID, memberNum, tagComment.text()))
					result = true;
			}
		}

		// Process each constructor in the class
		for (ConstructorDoc constructor : classDoc.constructors()) {
			// Obtain the method's comments and process
			if (parseComment(classID, memberNum++, constructor.commentText()))
				result = true;

			// Obtain the comments for method's tags (param, returns, etc.) and process each
			for (Tag tagComment : constructor.tags()) {
				if (parseComment(classID, memberNum++, tagComment.text()))
					result = true;
			}
		}
		
		// Get inner classes, template type, tag comments (if any) and process their comments
		for (ClassDoc innerClass : classDoc.innerClasses()) {
			classID++; // need to increment first here, as it was not incremented yet
System.err.println("ClassID="+classID);
			if (processClass(innerClass)) {
				prop.setProperty(formatter.format(classID), "Class: " + classDoc.qualifiedName());
				result = true;
			}
		}
		return result;
	}

	/**
	 * Parses the words (tokens) out of comments, removes punctuation, and checks the spelling of
	 * each word against the lists (java keywords, dictionary, userSymbols, etc). Correctly 
	 * spelled words are added to the "validWords" list, while words that are likely misspelled
	 * are added to the "potentialWords" list.
	 *
	 * @param commentString The comment string which we wish to parse
	 * @param classID The value of the classID counter, used to generate the reporting entry
	 * @param memberID The value of the memberID counter, used to generate the reporting entry
	 * @return a true value if a word was misspelled, false otherwise
	 */
	private boolean parseComment(int classID, float memberID, String commentString) {
		String fmtID = formatter.format(((float) classID) + memberID);
		DecimalFormat shortFormatter = new DecimalFormat("000");
		int itemID = 0;
		boolean result = false; // return true if we had misspelled words

		// Replace all punctuation with spaces and then use a Scanner to process the "words"
		// in the comment string
		Scanner scan = new Scanner(commentString.replaceAll("[\\p{Punct}&&[^']]", " "));
		
   		while (scan.hasNext()) {
			String word = scan.next();

			// If the word is a valid word, add it to the list of correctly spelled words
			if (validWord(word))
				goodWords.add(word);
			else {
				// Check to see if the "word" is a valid if a number
				try	{
					Integer.parseInt(word);
					goodWords.add(word);
				}
				// Conclude that the "word" is misspelled and place it in the correct list
				catch (NumberFormatException e) {
					int length = commentString.length() > 50 ? 50 : commentString.length();

					String wordID = fmtID + '.' + shortFormatter.format(itemID++);
					prop.setProperty(wordID, word);
					badWords.add(word);
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * Verifies that the given word is in the dictionary, java class name list
	 * or is a user-defined symbol.
	 *
	 * @param word Word to check
	 * @return returns a true if the parameter exists in the dictionaries or is a user-defined word
	 */
	private boolean validWord(String word) {
		word = word.toLowerCase();

		// Check against user-defined words, zero-length words, or java class name
		if (word.length() == 0 || validWords.contains(word) || userSymbols.contains(word) ||
			(dictionary != null && dictionary.contains(word)) )
			return true;
		else
			// Ensure that the word is not a valid word with 's' added to end
			if (word.length() != 0 && word.charAt(word.length()-1) == 's') {
				word = word.substring(0, word.length()-1);
				return validWord(word);
			}
			else
				// Check against single chars of a 'special' nature (e.g. ', ") which may originate
				// from things like '?', etc. which we see occasionally
				if (word.length() == 1 && (word.equals("\"") || word.equals("'")))
					return true;
				else
					return false;
	}

	/**
	* Sets the grading breakdown for the doclet.
	*
	* @param section Name of the section to set the max grade for
	* @param maxGrade Maximum grade for the section
	*/
	public void setGradingBreakdown(String section, float maxGrade) {
		if (section.equals("Spelling"))
			maxScore = maxGrade;
	}

	/**
	 * Returns the grade for the doclet.
	 *
	 * @return a float value representing the ratio of correctly spelt words
	 * from comments to the total number of works in the comments.
	 */
	public float getGrade() {
		if (goodWords.size() + badWords.size() == 0)
			return (float) 0.0;

		float ratio = (float) goodWords.size() / (goodWords.size() + badWords.size());
		return ratio * maxScore;
	}

	/**
	 * Sets a parameter used for doclet grading. Through this method, the instructor should be
	 * able to set specific valid words (perhaps the name of their institution, instructor name, etc.
	 * for inclusion in the list.
	 *
	 * @param param Name of the grading parameter
	 * @param value Value of the parameter
	 */
	public void setGradingParameter(String param, String value) {
		if (param.equals("Valid Words")) {
			Scanner scan = new Scanner(value);
			while (scan.hasNext())
				validWords.add(scan.next().toLowerCase());
		}
	}

	/**
	 * Sets the configuration properties loaded from the config file
	 *
	 * @param props Properties list
	 */
	public void setConfigProperties(Properties props) {
		// Not needed for this analyzer
	}
	
	/**
	 * Returns a string representation of this object
	 *
	 * @return the string name of this analyzer
	 */
	public String toString() {
		return "SpellCheck";
	}
}