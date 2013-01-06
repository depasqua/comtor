package offensiveTest;

import java.lang.*;

/**
 * This is a comment. It is bull shit.
 *
 * @author Peter DePasquale
 * @since 1.4
 */
public class Test3 {
	private int field1;

	private Integer field3;

	/** fucking test fuck **/
	private String field2;
	
	/**
	 * This is a comment. It is fuck.
	 * 
	 * @param foo shit
	 */
 	public Test3 (int foo) {
		;	
	}
	
	/** 
	 * Mizspelling in a constructrror
	 *
	 * @param foo mizspeling in a param
	 * @param bar this spelling is fine.
	 * @throws ArrayIndexOutOfBoundsException mizspelling in a throws
	 * @throws ClassCastException anotherz misspelling in a throws
	 */
	public Test3 (boolean foo, int bar) throws ArrayIndexOutOfBoundsException, ClassCastException {
		;	
	}
	
	public Test3 (String foo) {
		;	
	}
	
	/**
	 * This is a comment. It is ass.
	 * 
	 * @param args cock
	 */
	public static void main (String [] args) {
		int variable = 0;
	}
	
	/**
	 * @throws ArrayIndexOutOfBoundsException no misspelling in a throws
	 * @throws ClassCastException another non misspelling in a throws
	* @return fuck thiz shit
	*/
	public boolean returnTrue() throws ArrayIndexOutOfBoundsException, ClassCastException  {
		return true;
	}

	/** 
	 * Some comment.
	 *
	 * @param alpha Mizspelled.
	 * @param beta Also mizspelleded.
	 * @throws ArrayIndexOutOfBoundsException mizspelling in a throws
	 * @throws ClassCastException anotherz misspelling in a throws
	 * @return nothing
	 */
	public boolean returnTrue (int alpha, int beta) throws ArrayIndexOutOfBoundsException, ClassCastException {
		return true;
	}
}