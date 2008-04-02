<?php session_start(); ?>
<?php include_once("header.php"); ?>

<table width="90%" cellpadding="1" cellspacing="1" border="0">
       <tr>
        <td align="left">
         <b>Installation Questions</b><br/>
         <ol>
            <li>Where can I get Comment Mentor?
              <br/><br/>
              <i>You can download the <a href="comtor.tar.gz">latest version</a>.
              Sorry, we don't have public CVS access yet.</i>
              <br/>
            </li>
            <li>What are the installation requirements?
        <br/><br/>
        <i>You need to download and install the <a href="http://ant.apache.org/">Ant build tool</a> from the Apache Software Foundation. You will need a recent <a href="http://java.sun.com/">Java Development Kit</a> (CoMtor is developed with the JDK 5.x -- it should work).</i>
        <br/>
      </li>
            <li>How do I install Comment Mentor locally?
        <br/><br/>
        <i>
          <ol>
      <li> Download the tar.gz file </li>
      <li> <code>tar -zxf comtor.tar.gz</code> </li>
      <li> <code>cd comtor/code</code> </li>
      <li> <code>ant</code> </li>
    </ol>
        </i>
        <br/>
      </li>
            <li>How do I use the web interface/service?
        <br/><br/>
        <i>We are still working on it. Check back, and use the command line version in the meanwhile.</i>
        <br/>
      </li>
            <li>Can I run the web service locally?
        <br/><br/>
        <i>Sure -- once we write the code for it, we will package it
           with the rest of the comtor download. You will probably
     need to get familiar with downloading and installing a
     servlet container such as Apache's
     <a href="http://tomcat.apache.org/">Tomcat</a>.
        </i>
        <br/>
      </li>
          </ol>
          <b>General Questions</b><br/>
          <ol>
            <li>I don't like Java. What other languages are you going to support?</li>
            <li>I like Java -- what other languages are you planning to support?</li>
            <li>Are you guys inventing a comment language here?</li>
            <li>How is this different than source code annotation?</li>
            <li>How can I help?</li>
            <li>Is Comment Mentor FOSS (Free/Open Source Software)?
        <br/><br/>
          <i>Yes. Comment Mentor's code is licensed under the terms
       of the <a href="http://www.gnu.org/licenses/gpl.txt">GNU GPL, version 2</a>.
       The source code is distributed as the software download
       itself, and you are free to make changes to the software,
       as long as your changes are distributed under the GPL.
    </i>
        <br/>
      </li>
          </ol>
          <b>Technical Questions</b>
          <ol>
            <li>What features are you measuring?
             <br/><br/>
             <i>We've got your <a href="features.html">list of features right here</a>.</i><br/>
            </li>
          </ol>
        </td>
       </tr>
    </table>

<?php include_once("footer.php"); ?>
