package offensiveTest;

import java.lang.*;

/**
 * This class exists to test the absence of offensive word occurrences in various comment
 * locations. Here we are testing the body of a class command and an 'author' tag.
 *
 * @author Peter J DePasquale
 */
public class NoOffensiveWords {
	/** Testing a field level comment. **/
	private int foo;
	
	/**
	 * Testing the body of a constructor comment, and the 'param' tag comment.
	 *
	 * @param one There is nothing to see here. Move along
	 */
	public NoOffensiveWords (int one) {
		if (two > three)
			foo = 1;
		else
			foo = 2;
	}
	
	/** 
	 * Testing the body of a constructor comment and two 'param' tags and a
	 * 'throws' tag.
	 *
	 * @param two These are not the parameters you are looking for.
	 * @param three This comment contains no offensive words.
	 * @throws NullPointerException It's a trap!
	 */
	public NoOffensiveWords (int two, int three) throws NullPointerException {
		if (two > three)
			foo = 1;
		else
			foo = 2;
	}

	/**
	 * Testing the body of a method comment, in a single 'param' comment and a 'throws' comment.
	 * 
	 * @param args There is no argument here.
	 * @throws NullPointerException Pointer? We don't need no stinking pointers.
	 */
	public static void main (String [] args) throws NullPointerException {
		int variable;
		variable = 0;
		variable++;
	}
	
	/**
	 * Testing the body of a method comment and 'returns' comment
	 *
	 * @return Returns the value of true. Always.
	 */ 
	public boolean returnTrue() {
		return true;
	}
}