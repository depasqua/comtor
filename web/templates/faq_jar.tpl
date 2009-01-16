<h2>How to Make a Jar File</h2>

<p>
It is possible to make an archive that can be executed by Java. These are called JAR files. To do this you must store compiled class files in the archive, since Java source files cannot be run.
</p>

<h4>Steps to Make an Executable Jar</h4>
<ol>
  <li>
    <p>
      The first thing that needs to be done is to compile the Java source code files.
    </p>
  </li>
  <li>
    <p>
      All JAR files contain something called a manifest file which lets Java know which
      class needs to be executed first. The manifest file is a simple text file that
      contains the name of the class that will be executed when the JAR is executed.
      The manifest file is case sensitive and you do not need to include the .class file
      extension. Also the manifest file must end in a blank line. The file will be two lines
      long, the first containing the name of the main class, the second being a newline.
    </p>

    <p>
       If your main class is in Main.class your manifest file would look as follows:
       <ol>
         <li>Main</li>
         <li>&nbsp;</li>
       </ol>
    </p>
  </li>
  <li>
    <p>
      Once you have the .class files and the manifest file the JAR can be made using
      the following command:<br/>

      <code>jar cmf manifest.txt example.jar *.class</code><br/>

      The command runs the jar utility with the following options:
      - cm has the utility include the manifest.txt file
      - f names this jar file example.jar
      - *.class includes every class file in the directory. If only certain class files
      are to be included they can be listed individually in place of *.class
    </p>
  </li>
</ol>

<h4>To execute the Jar file:</h4>
<p>
Running a Java program contained in a Jar file is very similar to running a program contained in a .class file. The following command can be used for executable Jar files:<br/>
<code>java -jar example.jar</code>
</p>

<div style="font-style: italic;">
For more information:<br/>
<a href="http://java.sun.com/docs/books/tutorial/deployment/jar/">http://java.sun.com/docs/books/tutorial/deployment/jar/</a><br/>
Sun’s Jar Tutorial
</div>
