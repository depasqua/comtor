<h2>Comment Analysis Features</h2>
<p>
New features are constantly being added so check back frequently for information on new additions!
</p>

<p>
COMTOR generates a report based on a user's submitted source code. Currently there are four modules, check for tags, comment average ratio, percentage methods, and method interactions.
</p>

<ul>
  <li>
    Check for Tags
    <p class="indentAll">
       Searches submitted source code to evaluate whether all methods are appropriately commented. An appropriately commented method should have all of the necessary javadoc tags used correctly.<br/>
       Checks that the <span class="tag">@return</span>, <span class="tag">@param</span>, and <span class="tag">@throws</span> tags are used appropriately.  All methods that return a non-void value must have an <span class="tag">@return</span> tag.  The <span class="tag">@return</span> tag should not be present in methods with a return type of void.  All method parameters should have an <span class="tag">@param</span> tag with an appropriate comment.  There should not be any <span class="tag">@param</span> tags for non-existant parameters.  Each exceptions that a method throws should have an <span class="tag">@throws</span> tag.  There should not be any <span class="tag">@throws</span> tags that the method will no throw.
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
