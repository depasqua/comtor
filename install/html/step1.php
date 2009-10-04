<?php

// Check if all checks passed
$pass = true;

// Check PHP version
$str = phpversion();
$php = (strlen($str > 0) && $str[0] == '5');
$pass = $pass && $php;

// Check for pear extensions
$pear_pswd = fileInIncludePath("Text/Password.php");
$pear_mail = fileInIncludePath("Mail.php");
$pass = $pass && $pear_pswd && $pear_mail;

function checkForProgram($program)
{
  if (empty($program))
    return false;

  // Check for crontab
  $out = array();
  exec("which ".escapeshellarg($program), $out, $rtn);
  return ($rtn == 0);
}

$_SESSION['aliases'] = array();

function setPathConfig($varName, $program)
{
  // Add to aliases array
  $_SESSION['aliases'][$program] = &$_SESSION[$varName];

  if (isset($_POST[$varName]))
  {
    if (empty($_POST[$varName]))
      unset($_SESSION[$varName]);
    else
      $_SESSION[$varName] = $_POST[$varName];
  }
  if (!isset($_SESSION[$varName]))
  {
    $_SESSION[$varName] = exec("which ".escapeshellarg($program), $out, $rtn);
    if ($rtn != 0) 
      $_SESSION[$varName] = null;
  }
}

// Other programs to check for
$otherPrograms = array("chmod", "mkdir", "mv", "rm", "find", "cat", "grep");

// Check if the user defined a paths to programs
setPathConfig("javaPath", "java");
setPathConfig("javacPath", "javac");
setPathConfig("javadocPath", "javadoc");
setPathConfig("jarPath", "jar");

foreach ($otherPrograms as $tmp)
  setPathConfig($tmp."Path", $tmp);

// Check java/javac/javadoc version 1.5 or greater
define("JAVA_VERSION_REGEXP", "/\\\"?1.[5-9].*\\\"?/");
$out = array();
if (!empty($_SESSION['javaPath']))
  exec($_SESSION['javaPath']." -version 2>&1", $out, $rtn);
$java = (count($out) > 0 && preg_match(JAVA_VERSION_REGEXP, $out[0]));
$pass = $pass && $java;

$out = array();
if (!empty($_SESSION['javacPath']))
  exec($_SESSION['javacPath']." -version 2>&1", $out, $rtn);
$javac = (count($out) > 0 && preg_match(JAVA_VERSION_REGEXP, $out[0]));
$pass = $pass && $javac;

$out = array();
if (!empty($_SESSION['javadocPath']))
  exec($_SESSION['javadocPath']." -J-version 2>&1", $out, $rtn);
$javadoc = (count($out) > 0 && preg_match(JAVA_VERSION_REGEXP, $out[0]));
$pass = $pass && $javadoc;

// Check for jar
$jar = checkForProgram($_SESSION['jarPath']);
$pass = $pass && $jar;

// Check for other programs
foreach ($otherPrograms as $tmp)
{
  $$tmp = checkForProgram($_SESSION[$tmp.'Path']);
  $pass = $pass && $$tmp;
}

// Get Java CLASSPATH
$cp = exec("echo $CLASSPATH");
if (empty($cp))
  $cp = ".";

// Get path to jars
chdir("..");
$jars = getcwd() . DIRECTORY_SEPARATOR . "comtor_data" . DIRECTORY_SEPARATOR . "code" . DIRECTORY_SEPARATOR;
// Add all jars to the class path
if ($handle = opendir($jars)) 
{
  /* This is the correct way to loop over the directory. */
  define("JAR_REGEXP", "/.*\.jar$/");
  while (false !== ($file = readdir($handle)))
    if (preg_match(JAR_REGEXP, $file))
      $cp .= ":" . $jars . $file;
      
  $_POST['cp'] = $cp;  
  
  closedir($handle);
} 
chdir("install");

$antlr = $conJ = false;
if ($java && $javac && chdir("code"))
{
  // Setup classpath
  $cpshell = "";
  if (isset($_POST['cp']))
  {
    $_SESSION['javacp'] = escapeshellarg($_POST['cp']);
    $cpshell = "-cp ".$_SESSION['javacp']." ";
  }

  // Check for ANTLR
  $antlr = false;
  if ($php)
  {
    $out = array();
    exec($_SESSION['javacPath']." {$cpshell} -d " . sys_get_temp_dir() . " AntlrCheck.java 2>&1", $out, $rtn);
    $antlr = ($rtn == 0);
    unlink(sys_get_temp_dir() . DIRECTORY_SEPARATOR . "AntlrCheck.class");
  }
  
  // Check for MySQL Connector/J
  define("CONNECTOR_J_REGEXP", "/^MySQL: Succeed$/");
  $out = array();
  exec($_SESSION['javaPath'] . " {$cpshell}SystemCheck 2>&1", $out, $rtn);
  $conJ = (count($out) > 0 && preg_match(CONNECTOR_J_REGEXP, $out[0]));  
  chdir("..");
}
$pass = $pass && $antlr && $conJ;

// Check for crontab
$cron = checkForProgram("crontab");
$pass = $pass && $cron;

?>


<h1>Software Check</h1>

<?php require_once("error_box.php"); ?>

<p>
Checking for required software.
If java or javac fail, MySQL Connector/J and ANTLR will fail as well.
Please correct the problems with java and javac first. 
If any of the PEAR extensions fail, please install them and ensure that they are in the include path.
Please note that the "javadoc.sh" script requires some common utilities such as mkdir, rm, jar, javac, and find.  If you have issues submitting code, please check this file to determine where the script is looking for these utilities. 
You can find the software in the following locations:
</p>

<table>
<tr>
  <th>Software</th>
  <th>Location</th>
</tr>
<tr>
  <td>PHP 5</td>
  <td><a href="http://us.php.net">http://us.php.net</a></td>
</tr>
<tr>
  <td>PEAR</td>
  <td><a href="http://pear.php.net">http://pear.php.net</a></td>
</tr> 
<tr>
  <td>Java 1.5 or above</td>
  <td><a href="http://java.sun.com">http://java.sun.com</a></td>
</tr>
<tr>
  <td>Javadoc</td>
  <td><a href="http://java.sun.com/j2se/javadoc/">http://java.sun.com/j2se/javadoc/</a></td>
</tr>
<tr>
  <td>MySQL Connector/J</td>
  <td><a href="http://dev.mysql.com/downloads/connector/j/5.1.html">http://dev.mysql.com/downloads/connector/j/5.1.html</a></td>
</tr>
<tr>
  <td>ANTLR</td>
  <td><a href="http://www.antlr.org/download.html">http://www.antlr.org/download.html</a></td>
</tr>
</table>

<form name="form" method="post" action="">
<div>

  <fieldset>
    <legend>Java Settings</legend>
    <div class="floatContainer">
      <label for="javaPath">Java path: </label>
      <input id="javaPath" name="javaPath" type="text" value="<?php echo $_SESSION['javaPath']; ?>"/>
    </div>
    <div class="floatContainer">
      <label for="javacPath">Javac path: </label>
      <input id="javacPath" name="javacPath" type="text" value="<?php echo $_SESSION['javacPath']; ?>"/>
    </div>
    <div class="floatContainer">
      <label for="javadocPath">Javadoc path: </label>
      <input id="javadocPath" name="javadocPath" type="text" value="<?php echo $_SESSION['javadocPath']; ?>"/>
    </div>
    <div class="floatContainer">
      <label for="jarPath">Jar path: </label>
      <input id="jarPath" name="jarPath" type="text" value="<?php echo $_SESSION['jarPath']; ?>"/>
    </div>
    
    <?php 
    foreach ($otherPrograms as $tmp)
    { 
    ?>
    <div class="floatContainer">
      <label for="<?php echo $tmp; ?>Path"><?php echo ucfirst($tmp); ?> path: </label>
      <input id="<?php echo $tmp; ?>Path" name="<?php echo $tmp; ?>Path" type="text" value="<?php echo $_SESSION[$tmp.'Path']; ?>"/>
    </div>
    <?php 
    }
    ?>
    
    
    <div class="floatContainer">
      <label for="cp">Classpath: </label>
      <textarea id="cp" name="cp" rows="5" cols="50"><?php echo !empty($_POST['cp']) ? $_POST['cp'] : $cp; ?></textarea>
    </div>
  </fieldset>

  <table class="full">
  <tr>
    <td>PHP 5</td>
    <td class="status"><?php echo $php ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
  </tr>
  
  <tr>
    <td>PEAR Mail</td>
    <td class="status"><?php echo $pear_mail ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
  </tr>
  
  <tr>
    <td>PEAR Text/Password</td>
    <td class="status"><?php echo $pear_pswd ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
  </tr>

  <tr>
    <td>Java Version 1.5+</td>
    <td class="status"><?php echo $java ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
  </tr>
  
  <tr>
    <td>Javac Version 1.5+</td>
    <td class="status"><?php echo $javac ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
  </tr>
  
  <tr>
    <td>Javadoc</td>
    <td class="status"><?php echo $javadoc ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
  </tr>
  
  <tr>
    <td>Jar</td>
    <td class="status"><?php echo $jar ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
  </tr>
  
  <tr>
    <td>MySQL Connector/J</td>
    <td class="status"><?php echo $conJ ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
  </tr>
  
  <tr>
    <td>Antlr</td>
    <td class="status"><?php echo $antlr ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
  </tr>

  <tr>
    <td>Crontab</td>
    <td class="status"><?php echo $cron ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
  </tr>
  
  <?php 
  foreach ($otherPrograms as $tmp)
  { 
  ?>
  <tr>
    <td><?php echo ucfirst($tmp); ?></td>
    <td class="status"><?php echo $$tmp ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
  </tr>
  <?php 
  }
  ?>
  
  </table>
  
  <input type="hidden" name="pass" value="<?php echo $pass ? "T" : "F"; ?>" />

  <span class="link" onclick="document.form.submit();"><?php echo $pass ? "Next" : "Check Again"; ?></span>
</div>
</form>
