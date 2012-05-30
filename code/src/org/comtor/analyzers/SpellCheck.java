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
package org.comtor.analyzers;

import org.comtor.drivers.*;
import org.comtor.reporting.*;
import com.sun.javadoc.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

/**
 * The SpellCheck class is a tool to check spelling in comments. It is capable of determining
 * if a word is misspelled or a Java class name. Incorrect spellings for the same word are only
 * counted once. The dictionary word list comes from the public domain Yet Another Word List (YAWL)
 * available at: http://gtoal.com/wordgames/yawl
 *
 * @author James Sant'Angelo
 * @author Peter DePasquale
 */
public final class SpellCheck implements ComtorDoclet {
	private float maxScore = 5;
	private long totalWordCount = 0;
	private long duplicateWord = 0;
	
	// Counters for the classes
	private int numClasses = 0;

	// Correctly spelled words (goodWords), incorrectly spelled words (badWords), user-defined
	// valid words (validWords) [from the www interface], and user-defined symbols (userSymbols)
	// [from the source code]
	private HashSet<String> goodWords = new HashSet<String>();
	private HashSet<String> badWords = new HashSet<String>();
	private HashSet<String> validWords = new HashSet<String>();
	private HashSet<String> userSymbols = new HashSet<String>();

	// Dictionary of English words, Java class names, and HTML tags
	private HashSet<String> dictionary = new HashSet<String>();

	// JSON analysis report from this module's execution
	ModuleReport report = new ModuleReport ("Spell Check", "This module looks to compare words in comments against " +
		"an English dictionary. The analysis includes the list of known Java API classes (JDK), HTML tags and attributes, " +
		"Java keywords, and user-defined symbols from the submitted code (class names, method names, field names, etc.) " +
		"COMTOR uses JavaDoc and JavaDoc does not provide access to local variables within methods and blocks. Thus, the " +
		"word list is not exhaustive.");

	/**
	 * Performs pre-analysis initialization tasks. For example, loading the various dictionaries
	 * and lists
	 *
	 * @param rootDoc a reference to the top of the parse tree
	 */
	private void init (RootDoc rootDoc) {
		// Load the dictionary and java class listing (both sets contain valid 'words')
		String name = "comtor.analyzers.SpellCheck";
		boolean downloadAttempt = Util.loadDataList("dictionary.txt", dictionary, true, 
			this.getClass().getName());
		Util.loadDataList("javaclasslist.txt", dictionary, downloadAttempt, this.getClass().getName());
		Util.loadDataList("htmltags.txt", dictionary, downloadAttempt, this.getClass().getName());
		Util.loadDataList("javaKeywords.txt", dictionary, downloadAttempt, this.getClass().getName());
		
		// Add all user-defined packages, classes, class variables, methods, and parameter names to
		// a list of 'valid' words that may be referenced in a comment. Note that javaDoc does not
		// provide access to local variables within methods and blocks. Thus the list is not
		// exhaustive.
		for (PackageDoc packageDoc : rootDoc.specifiedPackages()) {
			userSymbols.add(packageDoc.name().toLowerCase());
			
			// Also add the components of the package name to the list of symbols
			Scanner packagenameScanner = new Scanner(packageDoc.name().replaceAll("\\.", " "));
			while (packagenameScanner.hasNext())
				userSymbols.add(packagenameScanner.next().toLowerCase());
			
			// Add enumerations to the user-symbol list
			for (ClassDoc enumDoc : packageDoc.enums())
				initAddClassMembers(enumDoc);
	
			// Add interfaces to the user-symbol list
			for (ClassDoc classDoc : packageDoc.interfaces())
				initAddClassMembers(classDoc);
				
			// Add errors to the user-symbol list
			for (ClassDoc classDoc : packageDoc.errors())
				initAddClassMembers(classDoc);
				
			// Add exceptions to the user-symbol list
			for (ClassDoc classDoc : packageDoc.exceptions())
				initAddClassMembers(classDoc);
		}
		
		// Add classes to the user-symbol list
		for (ClassDoc classDoc : rootDoc.classes()) {
			userSymbols.add(classDoc.name().toLowerCase());
			userSymbols.add(classDoc.qualifiedName().toLowerCase());
			initAddClassMembers(classDoc);
		}
	}
	
	/**
	 * Adds class and class-member information to the list of user-symbols
	 *
	 * @param classDoc a reference to the ClassDoc object to process
	 */
	private void initAddClassMembers (ClassDoc classDoc) {
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
				initAddClassMembers(excepts);
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
				if (!userSymbols.contains(excepts.name().toLowerCase()))
					initAddClassMembers(excepts);
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
			if (!userSymbols.contains(member.name().toLowerCase()))
				initAddClassMembers(member);
		}
		
		// Recursively do the same for all interfaces implemented by this class
		for (ClassDoc member : classDoc.interfaces()) {
			userSymbols.add(member.name().toLowerCase());
			userSymbols.add(member.qualifiedName().toLowerCase());
			if (!userSymbols.contains(member.name().toLowerCase()))
				initAddClassMembers(member);
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
	 * @param rootDoc the root of the documentation tree provided by the Javadoc parser
	 * @return Properties list containing the result set
	 */
	public Properties analyze (RootDoc rootDoc) {
		// prop.setProperty(formatter.format(-1), "The following word(s) is/are misspelled. Note " +
		// 	"that Javadoc tags (e.g. @param, @return, etc.) are not considered as comments.");
		
		// Initialize the required tables
		init(rootDoc);
		
		// Capture the starting time, just prior to the start of the analysis
		long startTime = new Date().getTime();

		// Obtain and process the comments for each class
		for (ClassDoc classDoc : rootDoc.classes()) {
   			numClasses++;
   			processClass(classDoc);
   		}

		// Capture the ending time, just after the termination of the analysis
		long endTime = new Date().getTime();

		// Set the score for this analysis and return the property list (report)
		// prop.setProperty("score", "" + getGrade());
		String percentFormat = NumberFormat.getPercentInstance().format(((float) badWords.size() / totalWordCount)); 
		report.addMetric(percentFormat + " of the words in the comments were misspelled.");
		report.addMetric(totalWordCount + " words analyzed.");
		report.addMetric(numClasses + " class(es) were processed.");
		report.addMetric(goodWords.size() + " correctly spelled words.");
		report.addMetric(badWords.size() + " incorrectly spelled words.");
		report.addMetric(duplicateWord + " duplicate words (spelled correctly or incorrectly).");

		report.appendLongToObject("information", "classes processed", numClasses);

		report.appendToAmble("preamble", "The @return tag is currently not processed.");

		report.addTimingString("start time", Long.toString(startTime));
		report.addTimingString("end time", Long.toString(endTime));
		report.addTimingString("execution time", Long.toString(endTime - startTime));

		return null;
	}

	/**
	 * Processes a single class and it's comments to determine if there are any misspelled words
	 * in the comments.
	 *
	 * @param class Class to process
	 * @param classID The value of the classID counter, used to generate the reporting entry
	 */
	private void processClass (ClassDoc classDoc) {
		report.addItem(ReportItem.CLASS, classDoc.qualifiedName());

		// Process the class' comments
		boolean misspellingFound = parseComment(classDoc.commentText(), classDoc.position());
		
		// Process all of the class's tag comments
		// for (Tag tagComment : classDoc.tags()) {
		// 	misspellingFound = parseComment(tagComment.text(), tagComment.position());
		// }

		// Process each constructor in the class
		for (ConstructorDoc constructor : classDoc.constructors()) {
			report.addItem(ReportItem.CONSTRUCTOR, constructor.qualifiedName());

			// Obtain the method's comments and process
			misspellingFound = parseComment(constructor.commentText(), constructor.position());

			// Obtain the comments for method's tags (param, returns, etc.) and process each
			// for (Tag tagComment : constructor.tags()) {
			// 	misspellingFound = parseComment(tagComment.text(), tagComment.position());
			// }
		}
		
		// Process each method in the class
		for (MethodDoc method : classDoc.methods()) {
			report.addItem(ReportItem.METHOD, method.qualifiedName());

			// Obtain the method's comments and process
			misspellingFound = parseComment(method.commentText(), method.position());

			// Parse the parameter's comments (@param tag)
			for (ParamTag param : method.paramTags()) {
				report.addItem(ReportItem.PARAMETER, param.parameterName());
				misspellingFound = parseComment(param.parameterComment(), param.position());
			}
			
			// Obtain the comments for method's tags (param, returns, etc.) and process each
			// for (Tag tagComment : method.tags()) {
			// 	misspellingFound = parseComment(tagComment.text(), tagComment.position());
			// }
		}	

		// Process all of the class' fields and the field's comments as well
		for (FieldDoc classField : classDoc.fields()) {
			report.addItem(ReportItem.FIELD, classField.qualifiedName());
			misspellingFound = parseComment(classField.commentText(), classField.position());
		}
		
		// Inner classes don't need to be processed. They are listed as part of the package-level
		// iteration of classes (in analyze())
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
	 * @return returns a true value if there was a misspelled word found.
	 */
	private boolean parseComment(String comment, SourcePosition position) {
		boolean result = false;

		// Replace all punctuation with spaces and then use a Scanner to process the "words"
		// in the comment string
		Scanner scan = new Scanner(comment.replaceAll("[\\p{Punct}&&[^']]", " "));
		
   		while (scan.hasNext()) {
			String word = scan.next();
			totalWordCount++;
			
			// If the word is a valid word, add it to the list of correctly spelled words
			if (validWord(word)) {
				if (!goodWords.add(word))
					duplicateWord++;
			} else {
				// Check to see if the "word" is a valid if a number
				try	{
					Integer.parseInt(word);
					if (!goodWords.add(word))
						duplicateWord++;
				}
				// Conclude that the "word" is misspelled and place it in the correct list
				catch (NumberFormatException e) {
					report.appendMessage(ReportItem.LASTITEM, "'" + word + "' considered a misspelled word " +
						"on/near line " + position.line());
					result = true;
					if (!badWords.add(word))
						duplicateWord++;
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
	private boolean validWord(String baseWord) {
		String word = baseWord.toLowerCase();			
			
		// Check against zero-length words
		if (word.length() == 0)
			return true;

		// Check against single chars of a 'special' nature (e.g. ', ") which may originate
		// from things like '?', etc. which we see occasionally
		if (word.length() == 1 && (word.equals("\"") || word.equals("'")))
			return true;

		// Check for possessives (e.g. java's)
		if (word.length() > 2 && word.substring(word.length()-2).equals("\'s"))
			return validWord(word.substring(0, word.length()-2));

		// Check for single quoted words (e.g. 'addConfigured')
		if (word.length() >= 3 && word.charAt(0) == '\'' &&
				word.charAt(word.length()-1) == '\'')
			return validWord(word.substring(1, word.length()-1));
		
		// Determine if the word in question is in the list of valid words, symbols, or the
		// dictionary
		if (validWords.contains(word) || userSymbols.contains(word) || dictionary.contains(word))
			return true;

		// Ensure that the word is not a valid pluralized word.
		// Note that this check must not precede the one for possessives or for the dictionary
		if (word.length() > 1 && word.charAt(word.length()-1) == 's')
			return validWord(word.substring(0, word.length()-1));

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

	/**
	 * Returns the string representation of this module's report (JSON format)
	 *
	 * @return a string value containing the JSON report
	 */
	public String getJSONReport() {
		return report.toString();
	}
}