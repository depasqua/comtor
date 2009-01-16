<h2>Compiling and Running a Java Program</h2>

<p>
Note: To compile and run a Java program, you will first need a computer with the Java SE Development Kit 6 (JDK 6). If the JDK is not on the machine you are using to develop, choose the correct version based on the operating system you have and follow the installation prompts.
</p>

<p>
Once you have created your Java source file (a file ending in .java) it needs to be compiled before it can be run.
</p>

<h4>To compile your Java program:</h4>

<ol>
  <li>
    Open up a command prompt or terminal and navigate to the directory that contains your source code.
  </li>
  <li>
    Type the following command into the terminal:<br/>
    <code>javac [Sourcefile].java</code><br/>
    where [Sourcefile] is replaced by the name of the file containing your program source code.
  </li>
  <li>
    If there is more than one file that you wish to compile you may list them individually:<br/>
    <code>javac one.java two.java three.java</code>
  </li>
  <li>
    To compile all java files in a particular directory the following command may be used:<br/>
    <code>javac *.java</code>
  </li>
</ol>

<p>
For each class contained in the source code a .class file will be created using the name of the class. By default these .class files will be placed in the directory from which the javac command was executed. These class files are used to execute a Java program.
</p>

<h4>To run the Java program:</h4>
<ol>
  <li>
    Using the terminal, navigate to the directory containing the .class files for your Java program.
  </li>
  <li>
  	Type the following command into the terminal:<br/>
    <code>java [MainClass]</code><br/>
    where [MainClass] is replaced by the name of the class containing the Main method.<br/>
    Note: do not include the .class extension here.
  </li>
</ol>

<div style="font-style: italic;">
For more information:<br/>
<a href="http://java.sun.com/docs/books/tutorial/getStarted/cupojava/win32.html">http://java.sun.com/docs/books/tutorial/getStarted/cupojava/win32.html</a><br/>
Java Tutorial for getting started in Windows
</div>
