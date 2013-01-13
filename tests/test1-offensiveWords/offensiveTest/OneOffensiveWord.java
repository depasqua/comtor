package offensiveTest;

import java.lang.*;

/**
 * This class exists to test single offensive word occurrences in various comment
 * locations. Here we are testing a single offensive word in the body of a class
 * comment, as in xxx. We are also testing single offensive word in an 'author' tag.
 *
 * @author xxx
 */
public class OneOffensiveWord {
	/** Testing an offensive word (xxx) in a field level comment. **/
	private int foo;
	
	/**
	 * Testing an offensive word in the body of a constructor comment, and 
	 * an offensive word, like xxx, in the 'param' tag comment.
	 *
	 * @param one testing xxx
	 */
	public OneOffensiveWord (int one) {
	}
	
	/** 
	 * Testing an offensive word in the body of a constructor comment, as in xxx.
	 * Also testing an offensive word in the 'throws' tags and in a
	 * 'param' tags.
	 *
	 * @param two xxx
	 * @param three This comment contains no offensive words.
	 * @throws NullPointerException xxx
	 */
	public OneOffensiveWord (int two, int three) throws NullPointerException {
		if (two > three)
			foo = 1;
		else
			foo = 2;
	}

	/**
	 * Testing an offensive word, as in xxx, in the body of a method comment, in a 'param'
	 * comment and in a 'throws' comment.
	 * 
	 * @param args the argument under test, which might be the value of xxx
	 * @throws NullPointerException if xxx, then this is thrown
	 */
	public static void main (String [] args) throws NullPointerException {
		int variable;
		variable = 0;
		variable++;
	}
	
	/**
	 * Testing an offensive word, as in xxx, in the body of a method comment, and also
	 * in a 'returns' comment.
	 *
	 * @return Returns the value of xxx if we feel like it, 'foo' otherwise.
	 */ 
	public boolean returnTrue() {
		return true;
	}
}