<h1>Software Check</h1>

<?php require_once("error_box.php"); ?>

<p>
Checking for required software. You can find the software in the following locations:
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

<?php

// Check PHP version
$str = phpversion();
$php = (strlen($str > 0) && $str[0] == '5');

// Check java version 1.5 or greater
define("JAVA_VERSION_REGEXP", "/\\\"1.[5-9].*\\\"/");
$out = array(); 
exec("java -version 2>&1", $out, $rtn);
$java = (count($out) > 0 && preg_match(JAVA_VERSION_REGEXP, $out[0]));

// Check javadoc 
exec("which javadoc", $out, $rtn);
$javadoc = ($rtn == 0);

$antlr = $conJ = false;
if (chdir("code"))
{
  // Setup classpath
  $cpshell = "";
  if (isset($_POST['cp']))
  {
    $_SESSION['javacp'] = escapeshellarg($_POST['cp']);
    $cpshell = "-cp ".$_SESSION['javacp']." ";
  }

  // Check for ANTLR
  $out = array();
  exec("javac {$cpshell}AntlrCheck.java 2>&1", $out, $rtn);
  $antlr = ($rtn == 0);
  
  // Check for MySQL Connector/J
  define("CONNECTOR_J_REGEXP", "/^MySQL: Succeed$/");
  $out = array();
  exec("java {$cpshell}SystemCheck 2>&1", $out, $rtn);
  $conJ = (count($out) > 0 && preg_match(CONNECTOR_J_REGEXP, $out[0]));
  chdir("..");
}

// Check for crontab
$out = array();
exec("which crontab", $out, $rtn);
$cron = ($rtn == 0);

// Check if all checks passed
$pass = $php && $java && $javadoc && $antlr && $conJ && $cron;

// Get Java CLASSPATH
$cp = exec("echo $CLASSPATH");
if (empty($cp))
  $cp = ".";

?>

<form name="form" method="post" action="">
<div>

  <fieldset>
    <legend>Java Settings</legend>
    <div class="floatContainer">
      <label for="cp">Classpath: </label>
      <input type="text" id="cp" name="cp" value="<?php echo !empty($_POST['cp']) ? $_POST['cp'] : $cp; ?>" />
    </div>
  </fieldset>

  <table class="full">
  <tr>
    <td>PHP 5</td>
    <td class="status"><?php echo $php ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
  </tr>

  <tr>
    <td>Java Version 1.5+</td>
    <td class="status"><?php echo $java ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
  </tr>
  
  <tr>
    <td>Javadoc</td>
    <td class="status"><?php echo $javadoc ? "<span class=\"succeed\">Success</span>" : "<span class=\"fail\">Failed</span>" ?></td>
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
  </table>
  
  <input type="hidden" name="pass" value="<?php echo $pass ? "T" : "F"; ?>" />

  <span class="link" onclick="document.form.submit();"><?php echo $pass ? "Next" : "Check Again"; ?></span>
</div>
</form>
