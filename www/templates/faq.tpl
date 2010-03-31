{literal}
<script type="text/javascript">
	$(function() {
		$("#tabs").tabs();
		$("#gen_about_comtor").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_about_features").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_about_created").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_about_whyUse").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_about_whoBenefit").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_about_openSource").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_about_icons").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_acct_create").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_acct_email").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_trouble_bugs").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_trouble_login1").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_trouble_pswd").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_trouble_login2").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_trouble_temppswd").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_trouble_compile").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_trouble_dropbox").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#gen_trouble_main").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#student_enroll").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#student_submit").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#student_lang").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#student_javadoc").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#student_compile").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#student_jar").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#inst_acct_status").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#inst_acct_link").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#inst_acct_notify").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#inst_course_add").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#inst_course_edit").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#inst_course_enroll").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#inst_course_drop").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#inst_course_dropbox").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#inst_course_email").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#inst_assign_add").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#inst_assign_edit").dialog({
			autoOpen: false, modal: true, width: 600
		});
		$("#inst_assign_delete").dialog({
			autoOpen: false, modal: true, width: 600
		});
	});
</script>
{/literal}

<div id="tabs">
	<ul>
		<li><a href="#tabs-1">General</a></li>
		<li><a href="#tabs-2">Students</a></li>
		<li><a href="#tabs-3">Instructors</a></li>
	</ul>

	<div id="tabs-1">
		<h3 class="questions">About COMTOR</h3>
		<ul>
			<li class="question"><a href="#" onclick="jQuery('#gen_about_comtor').dialog('open'); return false">What does COMTOR stand for?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_about_features').dialog('open'); return false">What features does COMTOR measure?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_about_created').dialog('open'); return false">Who created COMTOR?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_about_whyUse').dialog('open'); return false">Why should I use this system?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_about_whoBenefit').dialog('open'); return false">Who will benefit most from this system?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_about_openSource').dialog('open'); return false">Is COMTOR an open source project?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_about_icons').dialog('open'); return false">I saw an icon I didn't understand. What does it mean?</a></li>
		</ul>
		<h3 class="questions">Account Management</h3>
		<ul>
			<li class="question"><a href="#" onclick="jQuery('#gen_acct_create').dialog('open'); return false">How do I create an account?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_acct_email').dialog('open'); return false">Do I have to enter my college email address?</a></li>
		</ul>
		<h3 class="questions">Troubleshooting</h3>
		<ul>
			<li class="question"><a href="#" onclick="jQuery('#gen_trouble_bugs').dialog('open'); return false">Where should I report bugs?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_trouble_login1').dialog('open'); return false">COMTOR keeps telling me my username does not exist. Why can't I log in?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_trouble_pswd').dialog('open'); return false">I forgot my password. Now what do I do?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_trouble_login2').dialog('open'); return false">I created an account and entered my email and my email password. Why can't I log in?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_trouble_temppswd').dialog('open'); return false">I created an account but no temporary password was sent to my email. How do I retrieve my account?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_trouble_compile').dialog('open'); return false">COMTOR says my code does not compile, but I know it does. What should I do?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_trouble_dropbox').dialog('open'); return false">Where is my professor's dropbox?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#gen_trouble_main').dialog('open'); return false">Why do I always get points deducted for not commenting main?</a></li>
		</ul>
	</div>

	<div id="tabs-2">
		<h3 class="questions">Classes and Submissions</h3>
		<ul>
			<li class="question"><a href="#" onclick="jQuery('#student_enroll').dialog('open'); return false">How do I join a class?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#student_submit').dialog('open'); return false">How do I submit source code to a professor's dropbox for analysis?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#student_lang').dialog('open'); return false">What types of source code can I submit to COMTOR?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#student_javadoc').dialog('open'); return false">What is a Javadoc comment?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#student_compile').dialog('open'); return false">How do I compile and run a Java program?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#student_jar').dialog('open'); return false">What is a JAR file?</a></li>
		</ul>
	</div>
	
	<div id="tabs-3">
		<h3 class="questions">Account Management</h3>
		<ul>
			<li class="question"><a href="#" onclick="jQuery('#inst_acct_status').dialog('open'); return false">How do I obtain instructor status?</a></li>
			<li class="question"><a href="#" onclick="jQuery('#inst_acct_link').dialog('open'); return false">What type of link should I provide for professor status verification?</a><br></li>
			<li class="question"><a href="#" onclick="jQuery('#inst_acct_notify').dialog('open'); return false">How do I choose what types of emails I receive from COMTOR?</a><br></li>
		</ul>
		<h3 class="questions">Course Management</h3>
		<ul>
		<li class="question"><a href="#" onclick="jQuery('#inst_course_add').dialog('open'); return false">How do I add a course?</a></li>
		<li class="question"><a href="#" onclick="jQuery('#inst_course_edit').dialog('open'); return false">How do I edit course information?</a></li>
		<li class="question"><a href="#" onclick="jQuery('#inst_course_enroll').dialog('open'); return false">How do I enroll students in a course?</a></li>
		<li class="question"><a href="#" onclick="jQuery('#inst_course_drop').dialog('open'); return false">How do I drop students from a course?</a></li>
		<li class="question"><a href="#" onclick="jQuery('#inst_course_dropbox').dialog('open'); return false">How do I view a student's dropbox?</a></li>
		<li class="question"><a href="#" onclick="jQuery('#inst_course_email').dialog('open'); return false">How do I send emails to students in a course?</a></li>
		</ul>
		<h3 class="questions">Assignment Management</h3>
		<ul>
		<li class="question"><a href="#" onclick="jQuery('#inst_assign_add').dialog('open'); return false">How do I add an assignment?</a></li>
		<li class="question"><a href="#" onclick="jQuery('#inst_assign_edit').dialog('open'); return false">How do I edit an assignment?</a></li>
		<li class="question"><a href="#" onclick="jQuery('#inst_assign_delete').dialog('open'); return false">How do I delete an assignment?</a></li>
		</ul>
	</div>
</div>

<div class="answers">
	<!-- General -->
		<!-- About -->

		<div id="gen_about_comtor" title="What does COMTOR stand for?">
		<p class="answer">COMTOR is short for COMment menTOR.</p></div>

		<div id="gen_about_features" title="What features does COMTOR measure?">
		<p class="answer"><a href="features.php">Click here</a> to check out what features we measure.</p></div>

		<div id="gen_about_created" title="Who created COMTOR?">
		<p class="answer">COMTOR was developed by computer science students at The College of New Jersey working under the direction of Dr. DePasquale. Check out the <a href="about.php">About page</a> for the complete history of COMTOR.</p></div>

		<div id="gen_about_whyUse" title="Why should I use this system?">
		<p class="answer">The detailed use of documentation is a significantly underestimated way of improving software quality. COMTOR attempts to assist students in learning how to create quality documentation for their source code, by evaluating submitted source code against an overall standard.</p></div>

		<div id="gen_about_whoBenefit" title="Who will benefit most from this system?">
		<p class="answer">We believe that computer science majors at any level can benefit from using this system.</p></div>

		<div id="gen_about_openSource" title="Is COMTOR an open source project?">
		<p class="answer">Yes. Comment Mentor's code is licensed under the terms of the <a href="http://www.gnu.org/licenses/old-licenses/gpl-2.0.html">GNU GPL, version 2</a>. The source code is distributed as the software download itself, and you are free to make changes to the software, as long as your changes are distributed under the GPL.
		You can also find COMTOR on <a href="http://sourceforge.net/projects/comtor/">SourceForge.net</a>.</p></div>

		<div id="gen_about_icons" title="I saw an icon I didn't understand. What does it mean?">
		<p class="answer"><img src="img/icons/edit.png" /> Edit<br />
		<img src="img/icons/magnifying_glass.png" /> View<br />
		<img src="img/icons/lock.png" /> Disable or Lock<br />
		<img src="img/icons/unlock.png" /> Enable or Unlock<br />
		<img src="img/icons/delete.png" /> Delete<br /></p></div>

		<!-- Account Management -->

		<div id="gen_acct_create" title="How do I create an account?">
		<p class="answer">Click <span class="underline">Create An Account</span> on the left navigation bar and fill in the fields.</p></div>

		<div id="gen_acct_email" title="Do I have to enter my college email address?">
		<p class="answer">No. You can enter any valid email address.</p></div>

		<!-- Troubleshooting -->

		<div id="gen_trouble_bugs" title="Where should I report bugs?">
		<p class="answer">COMTOR is still in development. If you notice some bugs or other problems we would like to know. Please send us a quick email describing the bug.</p></div>

		<div id="gen_trouble_login1" title="COMTOR keeps telling me my username does not exist. Why can't I log in?">
		<p class="answer">Make sure you are typing the email address you signed up with correctly. If you have not signed up you will need to create an account before you log in.</p></div>

		<div id="gen_trouble_pswd" title="I forgot my password. Now what do I do?">
		<p class="answer">Click on <span class="underline">Password Recovery</span> on the left navigation bar and enter your email address.</p></div>

		<div id="gen_trouble_login2" title="I created an account and entered my email and my email password. Why can't I log in?">
		<p class="answer">Please check your email for a temporary password once you create your account. You can then use your email address for the username and enter the provided temporary password in the password field.</p></div>

		<div id="gen_trouble_temppswd" title="I created an account but no temporary password was sent to my email. How do I retrieve my account?">
		<p class="answer">You may have mistyped your email address. A common mistake is accidently entering .com instead of .edu or vice versa. Please try creating an account again.</p></div>

		<div id="gen_trouble_compile" title="COMTOR says my code does not compile, but I know it does. What should I do?">
		<p class="answer">Make sure once again that the code you are trying to upload does compile. You may be accidently uploading an old version of your code. Also make sure that you have included all nonstandard packages in the JAR file.</p></div>

		<div id="gen_trouble_dropbox" title="Where is my professor's dropbox?">
		<p class="answer">Please see the <a href="tutorials.php">video tutorials</a> section, which includes a tutorial on how to submit an assignment to a dropbox.</p></div>

		<div id="gen_trouble_main" title="Why do I always get points deducted for not commenting main?">
		<p class="answer"><code>public static void main (String[] args)</code> actually is a function and thus should be commented using the appropriate Javadoc tags.</p></div>


	<!-- Students -->

	<div id="student_enroll" title="How do I join a class?">
	<p class="answer"><ol>
		<li>Login (if you have not already created an account please create one).</li>
		<li>Click <a href="courses.php">Courses</a> under Course Management in the left hand navigation bar.</li>
		<li>Find the appropriate course and click Enroll.</li>
		<li>The system will then prompt you with a message box asking if you want to enroll.</li>
		<li>Click OK.</li>
	</ol></p></div>

	<div id="student_submit" title="How do I submit source code to a professor's dropbox for analysis?">
	<p class="answer"><ol>
		<li>Login (if you have not already created an account please create one).</li>
		<li>Click on the appropriate course in the listing displayed on the welcome screen.</li>
		<li>Click on Add New Assignment.</li>
		<li>Select your JAR file.</li>
		<li>Select the appropriate assignment dropbox.</li>
		<li>Click the checkboxes to include modules.</li>
		<li>Click the Analyze Comments button.</li>
		<li>View your report and submitted source code.</li>
	</ol></p></div>

	<div id="student_lang" title="What types of source code can I submit to COMTOR?">
	<p class="answer">Currently COMTOR only evaluates Javadoc comments in Java source code.</p></div>

	<div id="student_javadoc" title="What is a Javadoc comment?">
	<p class="answer">Check out the <a href="faq_jdoc.php">Javadoc pages</a>.</p></div>

	<div id="student_compile" title="How do I compile and run a Java program?">
	<p class="answer"><a href="faq_compile.php">Click here</a> for more about compiling and running Java programs.</p></div>

	<div id="student_jar" title="What is a JAR file?">
	<p class="answer"><a href="faq_jar.php">Click here</a> for more about JAR files.</p></div>

	<!-- Instructors -->
		<!-- Account Management -->

		<div id="inst_acct_status" title="How do I obtain instructor status?">
		<p class="answer">If you have an account,
		<ol>
			<li>Click <a href="requests.php">Requests</a> under Account Setup in the COMTOR menu to the left.</li>
			<li>Change the account type to professor and enter a small amount of text that we can use to verify that you are a professor.</li>
			<li>Click the <span class="underline">Request Account Type Change</span> link.</li>
			<li>Your request will be displayed on the page. Please wait until an administrator can process your request.</li>
		</ol>
		If you do not have an account,
		<ol>
			<li>Create an account in the regular way.</li>
			<li>Check the <span class="underline">Request Professor Status</span> checkbox and enter a small amount of text that we can use to verify that you are a professor.</li>
			<li>Please wait until an administrator can process your request.</li>
		</ol></p></div>

		<div id="inst_acct_link" title="What type of link should I provide for professor status verification?">
		<p class="answer">Please provide a link to a website that shows that you are professor at a given institution. This website also needs to have your email address listed, which should match the email address you provided for your username.</p></div>

		<div id="inst_acct_notify" title="How do I choose what types of emails I receive from COMTOR?">
		<p class="answer"><ol>
			<li>Click <a href="email_notify_edit.php">E-mail Notifications</a> under Account Setup in the COMTOR menu to the left.</li>
			<li>Select the frequency that you want to be sent emails.</li>
			<li>Select the events for which you would like to receive emails.</li>
			<li>Click the Submit button.</li>
		</ol></p></div>

		<!-- Course Management -->

		<div id="inst_course_add" title="How do I add a course?">
		<p class="answer"><ol>
			<li>Click on <a href="courseAddForm.php">Add Course</a> under Course Management in the COMTOR menu to the left.</li>
			<li>Fill in the required information and click the Submit button.</li>
		</ol></p></div>

		<div id="inst_course_edit" title="How do I edit course information?">
		<p class="answer"><ol>
			<li>Click <a href="courses.php">Courses</a> under Course Management in the COMTOR menu to the left.</li>
			<li>Find the course you would like to edit and click the pencil icon in the right column.</li>
			<li>After editing the information, click the Submit button.</li>
		</ol></p></div>

		<div id="inst_course_enroll" title="How do I enroll students in a course?">
		<p class="answer"><ol>
			<li>Click <a href="courses.php">Courses</a> under Course Management in the COMTOR menu to the left.</li>
			<li>Find the appropriate course and click the magnifying glass icon in the right column.</li>
			<li>At the bottom of the page, click the link for <span class="underline">Enroll Additional Students</span>.</li>
			<li>Find the student you wish to enroll and click the <span class="underline">Enroll</span> link next to their name.</li>
		</ol></p></div>

		<div id="inst_course_drop" title="How do I drop students from a course?">
		<p class="answer">Please note that this will disassociate all submissions from the course. This cannot be undone.
		<ol>
			<li>Click <a href="courses.php">Courses</a> under Course Management in the COMTOR menu to the left.</li>
			<li>Find the appropriate course and click the magnifying glass icon in the right column.</li>
			<li>Find the student you wish to drop and click the <span class="underline">Drop Student</span> link next to their name.</li>
		</ol></p></div>

		<div id="inst_course_dropbox" title="How do I view a student's dropbox?">
		<p class="answer"><ol>
			<li>Click <a href="courses.php">Courses</a> under Course Management in the COMTOR menu to the left.</li>
			<li>Find the appropriate course and click the magnifying glass icon in the right column.</li>
			<li>Find the student's name and click the <span class="underline">View Dropbox</span> link next to it.</li>
		</ol></p></div>

		<div id="inst_course_email" title="How do I send emails to students in a course?">
		<p class="answer"><ol>
			<li>Select the appropriate course from the select box in the top right corner of the page.</li>
			<li>Click <span class="underline">E-mail</span> in the Modules menu to the left.</li>
			<li>Select the students you would like to email (Hold Ctrl to select multiple students).</li>
			<li>Fill in the subject and the message and press Submit.</li>
		</ol></p></div>

		<!-- Assignment Management -->

		<div id="inst_assign_add" title="How do I add an assignment?">
		<p class="answer"><ol>
			<li>Select the appropriate course from the select box in the top right corner of the page.</li>
			<li>Click <span class="underline">View Course Dropbox</span> in the Modules menu to the left.</li>
			<li>Click <span class="underline">Add New Assignment</span> at the top of the page.</li>
			<li>Fill in the name and the open and close dates for the assignment.</li>
			<li>If you want specific modules to be run, check the checkbox next to the modules to be run.</li>
			<li>Fill in the grading information and parameters if you do not wish to use the default values.</li>
			<li>Click the Submit button.</li>
		</ol></p></div>

		<div id="inst_assign_edit" title="How do I edit an assignment?">
		<p class="answer"><ol>
			<li>Select the appropriate course from the select box in the top right corner of the page.</li>
			<li>Click <span class="underline">View Course Dropbox</span> in the Modules menu to the left.</li>
			<li>Click <span class="underline">Edit</span> next to the assignment you wish to edit.</li>
			<li>Edit the assignment information.</li>
			<li>If you want specific modules to be run, check the checkbox next to the modules to be run.</li>
			<li>Fill in the grading information and parameters if you do not wish to use the default values. (Note: You may not edit these values if there has already been a submission for this assignment.)</li>
			<li>Click the Submit button.</li>
		</ol></p></div>

		<div id="inst_assign_delete" title="How do I delete an assignment?">
		<p class="answer"><ol>
			<li>Select the appropriate course from the select box in the top right corner of the page.</li>
			<li>Click <span class="underline">View Course Dropbox</span> in the Modules menu to the left.</li>
			<li>Click the <span class="underline">Delete</span> link next to the assignment you wish to delete. (Note: This will disassociate all reports for this assignment from the course. This cannot be undone.)</li>
		</ol></p></div>
</div>
