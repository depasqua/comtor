<h2>Comment Analysis Features</h2>
<p>New features are constantly being added so check back frequently for information on new additions!</p>

<p>COMTOR generates a report based on a user's submitted source code. Currently there are five modules, listed below, along with a short description of their purpose.</p>

<ul>
	<li><span class="bold">Spell Checker</span>
		<p class="indentAll">Checks the spelling in comments. (Note: There may be some words that are spelled correctly, marked incorrect because they are not present in the dictionary.)</p>
	
		<p class="indentAll"><span class="bold">Report Example:</span></p>
	
		<div class='reportExample'>
			<div class='docletDesc'>Spell Checker (Checks the spelling in comments. (Note: There may be some words that are spelled correctly, marked incorrect because they are not present in the dictionary.))</div>
			<hr />
			<div class='class'>Class: edu.tcnj.FileOne</div>
			<div class='member'>Class comments</div>
			<div class='comment'>foobar</div>
			<div class='comment'>COMTOR</div>
			<div class='member'>Field / instance variable comments (field: foo)</div>
			<div class='comment'>agout</div>
			<div class='member'>Method comments (method: main)</div>
			<div class='comment'>jjj</div>
			<div class='member'>Method tag comments (method: main, tag: @param)</div>
			<div class='comment'>listr</div>
	
			<div class='class'>Class: edu.tcnj.FileTwo</div>
			<div class='member'>Class comments</div>
			<div class='comment'>Commentr</div>
	
			<div class='metricsHeader'>Analysis Metrics:</div>
			<div class='metricsBody'>Analysis time: 9 ms</div>
			<div class='metricsBody'>A total of 2 class(es) were processed.</div>
			<div class='metricsBody'>There were 17 correctly spelled words. Duplicate words were counted once.</div>
			<div class='metricsBody'>There were 6 incorrectly spelled words. Duplicate words were counted once.</div>
			<div class='metricsBody'>There were 5 duplicate words (spelled correctly or incorrectly).</div>
			<div class='metricsBody'>21% of the words in the comments were misspelled.</div>
			<div class='metricsBody'>There were a total of 28 words analyzed.</div>
		</div>
	</li>

	<li><span class="bold">Check for Tags (requires the presence of Javadoc tags)</span>
		<p class="indentAll">Searches submitted source code to evaluate whether all user-defined methods contain the correct use of three core Javadoc tags.</p>

		<p class="indentAll">This module verifys that the <span class="tag">@return</span>, <span class="tag">@param</span>, and <span class="tag">@throws</span> tags are used appropriately. All methods that return a non-void value must have a valid <span class="tag">@return</span> tag in the comment block above the method's header line.  The <span class="tag">@return</span> tag should not be present for methods with a return type of void. All method parameters should have an <span class="tag">@param</span> tag with an comment indicating the purpose of the parameter. There should not be any <span class="tag">@param</span> tags for non-existent parameters. Each exception that a method throws should have an <span class="tag">@throws</span> tag. There should not be any <span class="tag">@throws</span> tags present in methods that do not throw exceptions.</p>
        
        <p class="indentAll"><span class="bold">Code Example:</span></p>

<pre>
<p class="codeExample">/**
 * Attempts to recursively traverse the maze. Inserts special characters
 * indicating locations that have been tried and that eventually become part
 * of the solution.
 *
 * @param row provides the row index at which the traversal starts
 * @param column provides the column index at which the traversal starts
 * @return true if the maze is solved, false otherwise
 * @throws NullPointerException if the parameters reference an invalid maze cell
 */
 public boolean traverse (int row, int column) throws NullPointerException</p></pre>

    <p class="indentAll">
<span class="bold">Report Example:</span>
</p>
	<div class='reportExample'>
	<div class='docletDesc'>Check for Tags (Check for the proper use of returns, throw, and param tags.)</div>
	<div class='docletScore'>3.00 out of 10.00</div>
	<hr />
	<div class='class'>Class: TestClass</div>
	
	<div class='method'>Field: message</div>
	<div class='method'>Method: getMessage</div>
	<div class='comment'>Analyzed method getMessages declared return type and @return tag. This comment is CORRECT.</div>
	<div class='comment'>Analyzed method getMessages exception NullPointerException. This comment is INCORRECT. There is no @throws tag present for this exception.</div>
	<div class='comment'>Analyzed method getMessages @throws tag a NullPointerException if the message is null. This comment is INCORRECT. The exception name does not match the exception name, ( a NullPointerException if the message is null ) following the @throws tag.</div>
	<hr />
	<div class='class'>Class: ComtorTester</div>
	
	<div class='method'>Method: main</div>
	<div class='comment'>Analyzed method mains declared return type and @return tag. This comment is CORRECT.</div>
	<div class='comment'>Analyzed method mains parameter java.lang.String[] args. This comment is INCORRECT. There is no @param tag present for this parameter.</div>
	</div>
 </li>


 <li><span class="bold">Percentage Methods</span>
  <p class="indentAll">
    Calculates the average length of words in comments which immediately precede method definitions.
  </p>
    <p class="indentAll">
<span class="bold">Report Example:</span>
  </p>
	<div class='reportExample'>
		<div class='docletDesc'>Percentage Methods (Calculate the percentage of commented methods per class.)</div>
		<div class='docletScore'>5.00 out of 5.00</div>
		<hr />
		<div class='class'>Class: TestClass</div>		
		<div class='method'>100 percent (1/1) of class TestClasss methods are commented.</div>
		<hr />
		<div class='class'>Class: ComtorTester</div>
		<div class='method'>100 percent (1/1) of class ComtorTesters methods are commented.</div>
	</div>
 </li>

 <li><span class="bold">Comment Average Ratio</span>
  <p class="indentAll">
    Determines the length (in words) of each method's comments and the average length of method comments for each class.
  </p>
    <p class="indentAll">
<span class="bold">Report Example:</span>
</p>
	<div class='reportExample'>
		<div class='docletDesc'>Comment Average Ratio (Calculate the length of each method's comments in a class. If you are not getting full credit, try increasing the length of your comments.)</div>
		<div class='docletScore'>5.00 out of 5.00</div>
		<hr />
		<div class='class'>Class: TestClass</div>
		<div class='method'>The length of comments for the method getMessage is 33 words.</div>
		<div class='method'>The average length of comments for the class TestClass is 33 words.</div>
		<hr />
		<div class='class'>Class: ComtorTester</div>
		<div class='method'>The length of comments for the method main is 66 words.</div>
		<div class='method'>The average length of comments for the class ComtorTester is 66 words.</div>
	</div>
 </li>

 <li><span class="bold">Method Interactions</span>
  <p class="indentAll">
    Outputs which user-defined methods are called from other user-defined methods.
  </p>
    <p class="indentAll">
<span class="bold">Report Example:</span>
</p>
	<div class='reportExample'>
		<div class='docletDesc'>Method Interactions (Creates a method call graph for each user defined method. This does not count towards the overall grade.)</div>
		<div class='docletScore'>0.00 out of 0.00</div>
		<hr />
		<div class='class'>TestClass Class</div>
		<div class='method'>TestClass Method</div>
		<div class='method'>getMessage Method</div>
		<hr />
		<div class='class'>ComtorTester Class</div>	
	</div>
	</li>
	
</ul>

<h2>Additional Features Provided</h2>
<p>
In addition to simply submitting reports, COMTOR facilitates professor-student interaction. Professors can create an account which allows them to create a course and a dropbox for class assignments. Students then can enroll in their professor's course and submit their class assignments to the dropbox for analysis. Professors can view the source code and the reports generated from these submissions, as well assign point values for the various modules and view corresponding scores. There is also an email feature which allows both students and professors to compose emails to anyone registered that particular course.
</p>

{*
<h2>Planned Feature Tools</h2>
<ul>
 <li>
  Method Calls
  <p class="indentAll">
    Creates a method call graph for each user defined method.
  </p>
 </li>
</ul>
*}
