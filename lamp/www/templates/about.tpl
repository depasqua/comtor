<h2>About COMTOR</h2>

<p>The Comment Mentor was conceived in mid-2005 as an educational tool to assist both seasoned developers and new programmers learn better source code-level documentation strategies. Current conventions for documenting at the source level are not well-codified. Because of this lack of standards, it is hard to empirically measure the &quot;quality&quot; of a comment, and therefore difficult to objectively measure comment quality over time or in relation to the developing code base. </p>

<p>Those folks responsible for teaching the science, art, hobby, and job of programming (e.g., high-school teachers, college professors, technical advisors, and managers) often lack a tool for formally &quot;grading&quot; a student's (or developer/programmer's) comments. The state of the art amounts to hand-generated notes or verbal feedback at varying levels of detail. This details can range from the unhelpful &quot;OK, looks like you put a few comments in.&quot; on a homework printout to precise criticism of how the comments (and code structure in general) deviates from accepted programming standards of the organization (often generated during a formal and exhausting peer code review). This range illustrates that comment &quot;grading&quot; is an imprecise, labor-intensive procedure at best.</p>

<p>As a result, there is little incentive to offer meaningful feedback, and little incentive for students and new programmers to actually improve their comment style and substance along with the code. New developers and student programmers really are not stakeholders in their code or assignments: they typically work on a piece of code short term (3 months at the most, and more typically a week or two) and throw it over the wall with little thought for maintenance.</p>

<p>Comment Mentor attempts to address these difficulties by providing a tool that automates the grading process. Not only does it assist the grader, it can assist the student or developer before code is submitted by making sure comments are at some threshold level of quality before code is submitted. Guessing games are, in large part, eliminated. Of course, some may still be tempted to game the system, but any improvement is likely to have a large impact.</p>

<p>Comment Mentor can currently be used only as a web application, though we are considering porting to a command-line tool as well. It currently understands how to parse Java language source code because it takes advantage of the Javadoc tool and API. Other languages are slated for development; we welcome your suggestions and help on developing the appropriate tools.</p>

<p>Comment Mentor is <span style="font-weight: bold;">not</span> a compiler. It doesn't alter your source code, only derives measurements from comments that are encoded a certain way.</p>

<p>Comment Mentor does <span style="font-weight: bold;">not</span> assess the quality of other software engineering documentation. In particular, it does not use design diagrams, requirements specification lists, dataflow diagrams, or other documents. We expect that some of this information is best described in those documents as well as source-level comments, and if this information is in comments, we can use it as part of our scoring model, but Comment Mentor doesn't explicitly deal with those other types of documentation.</p>

<p>Good luck, and we hope you enjoy using the tool. We are happy to receive any feedback you may have at: <br/>
<div class="center"><code>{mailto address="comtor@tcnj.edu" encode='javascript' subject='COMTOR'}</code></div>

You can also visit the <a href="http://cs.tcnj.edu">CS Dept. website</a>.