package spellCheck;

import java.lang.*;

/**
 * This class tests various misspelled words in various comment locations. Here we are testing 'aa' 
 * misspeldt word in the class comment area. We won't test the '@author' location, as anything that follows
 * the '@author' tag is assumed to be a proper name.
 *
 * @author javajava java
 */
public class Test {
	/** Anither misspelling, this time in a field. **/
	private int foo;
	
	/**
	 * This constructor testz misspellings in the constructor comment block, in both '@param' tags,
	 * and in the '@throws' tag.
	 * 
	 * @param two Two is not spelleded correctly.
	 * @param three Threess is not spelled correctly.
	 * @throws Throws a NullPointerException when a refernce variable is null and attempted to be
	 * used in a non-assigment expression.
	 */
	public Test (int two, int three) throws NullPointerException {
		int variable;
		variable = 0;
		variable++;
	}

	/**
	 * This method testz misspellings in the method comment block, in both '@param' tags,
	 * and in the '@throws' tag.
	 * 
	 * @param two Two is not spelleded correctly.
	 * @param three Threess is not spelled correctly.
	 * @throws Throws a NullPointerException when a refernce variable is null and attempted to be
	 * used in a non-assigment expression.
	 */
	public static void main (String [] args) throws NullPointerException {
		int variable;
		variable = 0;
		variable++;
	}
	
	/**
	 * This method tests misspellings in the '@return' tag.
	 * 
	 * @return Returns a trueue value in all cases.
	 */
	public boolean returnTrue() {
		return true;
	}
}