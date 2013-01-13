package offensiveTest;

import java.lang.*;

/**
 * This class exists to test the presence of offensive word occurrences in various comment
 * locations. Here, we are testing that every possible location at all times contains at least
 * two offensive word occurrences. This is a class comment, xxx, and it contains, xxx.
 *
 * @author xxx xxx
 */
public class AllOffensiveWords {
	/** Testing a field level comment for xxx and xxx. **/
	private int foo;
	
	/** 
	 * Testing the body of a constructor comment with xxx, and xxx. Also testing
	 * an offensive word in the 'throws' tags and in 'param' tags.
	 *
	 * @param two xxx xxx
	 * @param three xxx xxx.
	 * @throws NullPointerException xxx xxx
	 */
	public AllOffensiveWords (int two, int three) throws NullPointerException {
		if (two > three)
			foo = 1;
		else
			foo = 2;
	}

	/**
	 * Testing an offensive word, as in xxx, in the body of a method comment, in a 'param'
	 * comment and in a 'throws' comment. Also testing the 'return' comment. XXX.
	 * 
	 * @param arg1 XXX XXX
	 * @param arg2 xxx xxx
	 * @throws NullPointerException XXX XXX
	 * @return xxx xxx
	 */
	public int someMethod (String arg1, String arg2) throws NullPointerException {
		int variable;
		variable = 0;
		variable++;
	}
}