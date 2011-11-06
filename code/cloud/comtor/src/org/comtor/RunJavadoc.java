package org.comtor;

public class RunJavadoc {
	public void runJavadoc(String candy) {
		String[] javadocargs = { candy + "HelloWorld.java", "-d",
				candy + "javadocFiles" };
		System.out.println(javadocargs);
		com.sun.tools.javadoc.Main.execute(javadocargs);
		System.out.println("Path is: " + candy + "HelloWorld.java");
	}
}
