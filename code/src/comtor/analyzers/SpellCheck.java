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
import java.util.regex.*;

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

	// All comment words in lowercase
	private HashSet<String> allWords = new HashSet<String>();

	// Correctly spelled words
	private HashSet<String> words = new HashSet<String>();

	// User defined symbols such as class, field, method, and parameter names
	private HashSet<String> userSymbols = new HashSet<String>();

	// User-defined valid words (symbols)
	private HashSet<String> validWords = new HashSet<String>();

	// Words that are potentially wrong
	private HashSet<String> potentialWords = new HashSet<String>();

	// Dictionary of English words
	private HashSet<String> dictionary = new HashSet<String>();

	// HashSet that contains the list of Java class names (good words)
	private HashSet<String> javaClassNamesList = new HashSet<String>();
	
	/**
	 * Examine each class and its methods. Calculate the length of each method's comments.
	 *
	 * @param rootDoc the root of the documentation tree provided by the JavaDoc parser
	 * @return Properties list
	 */
	public Properties analyze(RootDoc rootDoc) {
		prop.setProperty("title", "Spell Checker");
		
		// Array of Java keywords. Note that 'java' is part of this list. While not a keyword,
		// it is common enough to make it suitable to add to this list.
		String[] javaKeywords = {"abstract", "assert", "boolean", "break", "byte", "case", "catch",
			"char", "class", "const", "continue", "default", "do", "double", "else", "enum",
			"extends", "final", "finally", "float", "for", "goto", "if", "implements", "import",
			"instanceof", "int", "interface", "long", "native", "new", "package", "private",
			"protected", "public", "return", "short", "strictfp", "super", "switch", "synchronized",
			"this", "throw", "throws", "transient", "try", "void", "volatile", "while", "java"};

		// Build the initial list of valid words, starting first with the list created above.
		for (String jword : javaKeywords)
			validWords.add(jword);
		
		// Define a counter for the properties list
		int num = 0;
		
		// Load the dictionary and java class listing (both sets contain valid 'words')	
		loadDataList("dictionary.txt", dictionary);
		loadDataList("javaclasslist.txt", javaClassNamesList);
		System.err.println("right in dict: " + validWord("right"));

		//-------------------------------------//
		// Obtain the comments for the rootDoc.
		parseComment(rootDoc.commentText());

		// Obtain the comments for each class
		for (ClassDoc docClass : rootDoc.classes())
   			processClass(docClass);

		// Start the report output, printing misspelled words
		Iterator<String> it = potentialWords.iterator();
		while (it.hasNext()) {
			String word = it.next();

			// Ensure that the word is not class/methods/parameter/etc name
			if (userSymbols.contains(word)) {
				it.remove();
				words.add(word);
			}

			// Ensure that the word is not class/method/parameter/etc name with 's' added to end
			else if (word.length() != 0 && word.charAt(word.length()-1) == 's' &&
					userSymbols.contains(word.substring(0, word.length()-1))) {
				it.remove();
				words.add(word);
			}
      		else {
				// If there are misspellings, but none have been printed yet,
				// write the leading line. Otherwise, print the word.
				if (num == 0) {
					prop.setProperty("000.000.", "The following word(s) is/are misspelled:");
					num++;
				}
				prop.setProperty("000.000." + num, word);
				num++;
			}
		}
		
		// If there were no misspellings:	
		if (num == 0)
			prop.setProperty("000.000", "All words correctly spelled.");


		// Set the score for this analysis and return the property list (report)
		prop.setProperty("score", "" + getGrade());
		return prop;
	}

	/**
	 * Attempts to load the local and web-based versions of data lists. This method is used to load
	 * both dictionary files and java class lists into HashSets for use in the spell checker.
	 * Whenever possible, the local copy will be updated if its internal version number is less than
	 * the web-based version number.
	 *
	 * @param fileName the base of the filename to attempt to load
	 * @param dataList a reference to a hash set of strings which will hold the data list
	 */
	private void loadDataList(String fileName, HashSet<String> dataList) {
		if (dataList == null)
			dataList = new HashSet<String> ();
			
		// Attempt to load/fetch the most recent dictionary list.
		String netFileName = "http://www.comtor.org/" + fileName;
		String localFileName = System.getProperty("user.dir") +
							   System.getProperty("file.separator") + fileName;
		Scanner netScanner = null;
		String netFileVersion = null;
		Scanner localScanner = null;
		String localFileVersion = null;
		
		try {
			// Attempt to open / read most recent list on web
			netScanner = new Scanner((new URL(netFileName)).openStream());			
			netFileVersion = netScanner.nextLine();
		} catch (MalformedURLException mue) {
			System.err.println(mue);  // Bad URL, it's ok, default to local copy if possible.
		} catch (IOException ioe) {
			System.err.println(ioe); // No access to the 'net, default to local copy if possible.
		}

		try {
			// Attempt to open / read local copy
			File localFile = new File (localFileName);
			if (localFile.exists()) {
				localScanner = new Scanner(localFile);
				localFileVersion = localScanner.nextLine();
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
		
		// Determine what version(s) we have, rewrite local version if needed
		if (localFileVersion == null && netFileVersion != null) {
			// Write a new local copy and use the 'net version
			try {
				PrintStream localFile = new PrintStream (new File(localFileName));
				localFile.println(netFileVersion);
				while (netScanner.hasNextLine()) {
					String line = netScanner.nextLine();
					localFile.println(line);
					dataList.add(line.toLowerCase());
				}
				localFile.close();
			} catch (FileNotFoundException fnfe) {
				System.err.println(fnfe);
				System.exit(1);
			}
		} else if (localFileVersion == null && netFileVersion == null) {
			// Well, there's not much else we can do...
			System.exit(1);
		} else if (localFileVersion != null && netFileVersion != null) {
			// Check for freshness of local version
			if (localFileVersion.compareTo(netFileVersion) < 0) {
				// Update local copy, net copy is fresher
				try {
					PrintStream localFile = new PrintStream (new File(localFileName));
					localFile.println(netFileVersion);
					while (netScanner.hasNextLine()) {
						String line = netScanner.nextLine();
						localFile.println(line);
						dataList.add(line.toLowerCase());
					}
					localFile.close();
				} catch (FileNotFoundException fnfe) {
					System.err.println(fnfe);
					System.exit(1);
				}
			} else
				// Load the local version, no update was necessary
				loadFileToList(localFileName, dataList);
		} else
			// Use the local version, 'net is unavailable.
			loadFileToList(localFileName, dataList);
	}
	
	/**
	 * Attempts to load the specified local file into the specified hash set. The input assumes the
	 * first line is a version number and it is disregarded for this use.
	 *
	 * @param localFileName the name of the local file to read
	 * @param dataList a reference to the hash set into which the file contents are placed
	 */
	private void loadFileToList(String localFileName, HashSet<String> dataList) {
		try {
			Scanner scan = null;
			File localFile = new File (localFileName);
			if (localFile.exists()) {
				scan = new Scanner(localFile);
				scan.nextLine(); // drop the version number, we don't care right here
			}
			while (scan.hasNextLine())
				dataList.add(scan.nextLine().toLowerCase());
	
			scan.close();
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
	}

	/**
	* Processes a single class
	*
	* @param class Class to process
	*/
	private void processClass(ClassDoc classDoc) {
		// Add class name and class comment to list of valid words
		userSymbols.add(classDoc.name());
		parseComment(classDoc.commentText());

		// Get all of the class' fields and the field's comments as well
		for (FieldDoc classFields : classDoc.fields()) {
			userSymbols.add(classFields.name());
			parseComment(classFields.commentText());
		}

		// Get inner classes, template type, tag comments (if any) and process their comments
		for (ClassDoc innerClass : classDoc.innerClasses())
			processClass(innerClass);

		for (TypeVariable type : classDoc.typeParameters())
			userSymbols.add(type.typeName());

		for (Tag tagComment : classDoc.tags())
			parseComment(tagComment.text());

		// Process each method in the class
		MethodDoc[] methods = classDoc.methods();
		for (int methodNum = 0; methodNum < methods.length; methodNum++)
		{
			// Add method name to list of valid words
			userSymbols.add(methods[methodNum].name());

			// Obtain the method parameters and process each
			Parameter[] params = methods[methodNum].parameters();
			for (int paramNum = 0; paramNum < params.length; paramNum++) {
				userSymbols.add(params[paramNum].name());
				userSymbols.add(params[paramNum].typeName());
			}

			// Obtain the comments for method's tags (param, returns, etc.) and process each
			for (Tag tagComment : methods[methodNum].tags())
				parseComment(tagComment.text());

			// Obtain the method's comments and process
			parseComment(methods[methodNum].commentText());
		}
	}

	/**
	 * Parses the words (tokens) out of comments, removes punctuation, and adds each word to
	 * the word list.
	 *
	 * @param comment The comment string which we wish to parse
	 */
	private void parseComment(String comment) {
		// Replace parenthesis, brackets, dashes, and periods with spaces
		comment = comment.replaceAll("[()<>-]|\\+"," ");

		// Create Scanner to process the comment string
		Scanner scan = new Scanner(comment);
   		while (scan.hasNext()) {
			String tmp = scan.next();
			
			// Add the processed word to the list of all words from all comments
			if (allWords.add(tmp.toLowerCase())) {

				// Remove the punctuation, if any
				String tmp2 = tmp.replaceAll("[\\p{Punct}&&[^']]", "");

				// If the word is a valid word, add it to the list of correctly spelled words
				if (validWord(tmp2))
					words.add(tmp2);
				else {
					// Verify that the error was not due to something like "num+1" in the comment
					tmp2 = tmp.replaceAll("[\\p{Punct}&&[^']]", " ");
					Scanner scan2 = new Scanner(tmp2);
					while (scan2.hasNext()) {
						String tmp3 = scan2.next();

						// Recheck the validity of the potential word
						if (validWord(tmp3))
							words.add(tmp3);
						else {   
							// Check to see if the "word" is a valid if a number
							try	{
								Integer.parseInt(tmp3);
								words.add(tmp3);
							}
							catch (NumberFormatException e) {
								// Ignore words that contain @ symbol. This will prevent Javadoc
								// tags and E-mail addresses from being considered incorrectly
								// spelled. Ignore words that contain a dot '.' (filename?).
								int idx = tmp.lastIndexOf('.');
								if (!tmp.contains("@") && (idx == -1 || idx == tmp.length())) {
									tmp = tmp.replaceAll("[\\p{Punct}&&[^_]]","");
									if (!tmp.equals("")) 
										// Don't add words that are no longer
										// present following modification (e.g. '?.?')
										potentialWords.add(tmp);
								}
							}
						}
					}
				}
			}
		}
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
		if (word.length() == 0 || validWords.contains(word) || javaClassNamesList.contains(word))
			return true;

		// Check against dictionary file
		if (dictionary != null)
			if (dictionary.contains(word))
				return true;
			else
				return false;
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
		if (words.size() + potentialWords.size() == 0)
			return (float) 0.0;

		float ratio = (float) words.size() / (words.size() + potentialWords.size());
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