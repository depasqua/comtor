package noComments;

import java.lang.*;

public class Test {
	private int foo;
	
	public Test (int one) {
	}
	
	public Test (int two, int three) throws NullPointerException {
	}

	public static void main (String [] args) throws NullPointerException {
		int variable;
		variable = 0;
		variable++;
	}
	
	public boolean returnTrue() {
		return true;
	}
}