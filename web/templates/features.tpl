<h2>Comment Analysis Features</h2>
<p>
New features are constantly being added so check back frequently for information on new additions!
</p>

<p>
COMTOR generates a report based on a user's submitted source code. Currently there are five modules, listed below, along with a short descrip
tion of their purpose.
</p>

<ul>
  <li>
    Check for Tags (requires the presence of Javadoc tags)
    <p class="indentAll">
       Searches submitted source code to evaluate whether all user-defined methods contain the correct use of three core Javadoc tags.</p>

    <p class="indentAll">
        This module verifys that the <span class="tag">@return</span>, <span class="tag">@param</span>, and <span class="tag">@throws</span> 
tags are used appropriately. All methods that return a non-void value must have a valid <span class="tag">@return</span> tag in the comment b
lock above the method's header line.  The <span class="tag">@return</span> tag should not be present for methods with a return type of void. 
All method parameters should have an <span class="tag">@param</span> tag with an comment indicating the purpose of the parameter. There shoul
d not be any <span class="tag">@param</span> tags for non-existent parameters. Each exception that a method throws should have an <span class
="tag">@throws</span> tag. There should not be any <span class="tag">@throws</span> tags present in methods that do not throw exceptions.</p>

    <p class="indentAll">
Example:
</p>
<pre>
<p class="example">/**
 * Attempts to recursively traverse the maze. Inserts special characters
 * indicating locations that have been tried and that eventually become part
 * of the solution.
 *
 * @param row provides the row index at which the traversal starts
 * @param column provides the column index at which the traversal starts
 * @return true if the maze is solved, false otherwise
 * @throws NullPointerException if the parameters reference an invalid maze cell
 */
 public boolean traverse (int row, int column) throws NullPointerException</pre>
    </p>
 </li>

 <li>
  Percentage Methods
  <p class="indentAll">
    Calculates the average length of words in comments immediately preceding methods.
  </p>
 </li>

 <li>
  Comment Average Ratio
  <p class="indentAll">
    Determines the length (in words) of each method's comments and the average length of method comments for each class.
  </p>
 </li>

 <li>
  Method Interactions
  <p class="indentAll">
    Outputs which user-defined methods are called from other user-defined methods.
  </p>
 </li>

 <li>
 Spell Check
 <p class="indentAll">
    Checks the spelling of all words present in all comments within the source code file(s). This module attempts to recognize user-defined v
ariable, method, and class names as valid words in an effort to reduce the number of false-positives.
 </p>
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
