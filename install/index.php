<?php
// define the migrations that exist in the /migrations folder
// this is used to determine what DB and config migrations to run.
$migration_releases = array ("1.0", "1.2", "1.3");

// Define default administrator password
define("ADMIN_PASSWORD", "cmhXE1d/ItCiM");  // Password is "comtor"
session_start();

// Reset link
if (empty($_POST) && isset($_GET['reset']))
  session_unset();

if (!isset($_SESSION['step']))
  $_SESSION['step'] = 0;

// Store directory information in session
if (!isset($_SESSION["paths"]))
{
  chdir("..");
  $_SESSION["paths"] = array(
    "www" => getcwd() . DIRECTORY_SEPARATOR . "www",
    "private" => getcwd() . DIRECTORY_SEPARATOR . "comtor_data" . DIRECTORY_SEPARATOR . "config",
    "code" => getcwd() . DIRECTORY_SEPARATOR . "comtor_data" . DIRECTORY_SEPARATOR . "code",
    "uploads" => getcwd() . DIRECTORY_SEPARATOR . "comtor_data" . DIRECTORY_SEPARATOR . "uploads",
    "resources" => getcwd() . DIRECTORY_SEPARATOR . "comtor_data" . DIRECTORY_SEPARATOR . "resources"
  );   
  chdir("install");
}

// Specifcy where to look for the config.php file. 
// The installer will use this to pull the old config file and write to it.
// This file should NOT be included with the packaged code when distributed.
$config_php_path = $_SESSION["paths"]["private"].DIRECTORY_SEPARATOR."config.php";

// Current install step
$step = $_SESSION['step'];

// Setup step names
$steps = array(
  "Software Check",
  "Configuration Setup",
  "Administrator Setup",
//  "Directory Setup",
  "Write Configs",
  "Cron Job Setup",
  "Source Code Compilation",
  "Finish"
);

// Process form data
if (!empty($_POST) || isset($_GET['upgrade']) || $_GET['submit'])
{
  $error = null;
  switch ($step)
  {
    case 0:
        // Pick Install or Upgrade
        if (isset($_GET['upgrade']))
        {
            $_SESSION['upgrade'] = ($_GET['upgrade'])? true : false;
            $step++;
            if ($_SESSION['upgrade'])
                $step++; // if we're upgrading, skip the Software check
        }
    break;
    case 1:
      // Check fields
      if (!empty($_POST['pass']) && $_POST['pass'] == "T")
      {
        // Increment step
        $step++;
      }

      break;
    // MySQL setup
    case 2:
        if ($_SESSION['config_done'])
          if ($_SESSION['upgrade'])
            $step += 5;
          else
            $step++;
      break;
    // Admin user setup
    case 3:
      // Check fields
      if (!empty($_POST['name']) /*&& !empty($_POST['school'])*/ && !empty($_POST['email']))
      {
        // TODO: Check $_SESSION vars
        $con = @mysql_connect($_SESSION["mysql"]['server'], $_SESSION["mysql"]['username'], $_SESSION["mysql"]['password']);
        if ($con && mysql_select_db($_SESSION["mysql"]['dbname']))
        {
            // Insert admin user
            $sql = sprintf("INSERT INTO users (name, email, password, validatedDT, passwordChangeDT, acctType, acctStatus, schoolId) VALUES (\"%s\", \"%s\", \"%s\", NOW(), NOW(), \"admin\", \"enabled\", %d)", mysql_real_escape_string($_POST["name"]), mysql_real_escape_string($_POST["email"]), mysql_real_escape_string(ADMIN_PASSWORD), 4);
            if (mysql_query($sql))
            {
              // Store information in session
              $_SESSION["admin"] = array(
                "email"=>$_POST['email'],
                //"school"=>$_POST['school'],
                "name"=>$_POST['name']
              );

              // Increment step
              $step++;
            }
            else
            {
              $error = "Failed to create administrator account.";
            }

          // Close database
          mysql_close($con);
        }
        else
          $error = "Failed to connect to database.";
      }
      else
        $error = "Please fill in all fields.";

      break;
    
    // Configuring
    case 4:
        
        // Create www folder config file
        ob_start();
        require_once("www_config_php_template.php");
        $configData = ob_get_clean();
        
        // Open www folder config file
        $filename = $_SESSION["paths"]["www"].DIRECTORY_SEPARATOR."config.php";
        if ((is_writable($filename) || @touch ($filename)) && $fh = fopen($filename, "w"))
        {
          // Write to file and close
          if (!fwrite($fh, $configData))
            $error .= "Failed to write to \"".$filename."\"<br/>";
          fclose($fh);
        }
        else
          $error .= "Failed to write to \"".$filename."\"<br/>";    

        // Create java config file
        ob_start();
        require_once("java_properties_template.php");
        $configData = ob_get_clean();

        // Open java config file
        $filename = $_SESSION["paths"]["private"].DIRECTORY_SEPARATOR."java.properties";
        if ((is_writable($filename) || @touch ($filename)) && $fh = fopen($filename, "w"))
        {
          // Write to file and close
          if (!fwrite($fh, $configData))
            $error .= "Failed to write to \"".$filename."\"<br/>";
          fclose($fh);
        }
        else
          $error .= "Failed to write to \"".$filename."\"<br/>";
          
        // Create bash script config file
        ob_start();
        require_once("config_sh_template.php");
        $configData = ob_get_clean();

        // Open bash script config file
        $filename = $_SESSION["paths"]["www"].DIRECTORY_SEPARATOR."scripts".DIRECTORY_SEPARATOR."config.sh";
        if ((is_writable($filename) || @touch ($filename)) && $fh = fopen($filename, "w"))
        {
          // Write to file and close
          if (!fwrite($fh, $configData))
            $error .= "Failed to write to \"".$filename."\"<br/>";
          fclose($fh);
        }
        else
          $error .= "Failed to write to \"".$filename."\"<br/>";

        // Chmod scripts
        $filename = $_SESSION["paths"]["www"].DIRECTORY_SEPARATOR."scripts".DIRECTORY_SEPARATOR."javadoc.sh";
        if (!is_executable($filename) && (!is_writable($filename) || !chmod($filename, 0755)))
          $error .= "Failed to chmod 755 \"".$filename."\"<br/>";
        // Chmod scripts          
        $filename = $_SESSION["paths"]["www"].DIRECTORY_SEPARATOR."scripts".DIRECTORY_SEPARATOR."config.sh";
        if (!is_executable($filename) && (!is_writable($filename) || !chmod($filename, 0755)))
          $error .= "Failed to chmod 755 \"".$filename."\"<br/>";
        
        // Check permissions on template_c
        $filename = $_SESSION["paths"]["www"].DIRECTORY_SEPARATOR."templates_c";
        if (!is_writable($filename))
          $error .= "Change the permissions on \"".$filename."\" to allow the apache user to write to the directory.<br/>";
        // Check permissions on uploads
        $filename = $_SESSION["paths"]["uploads"];
        if (!is_writable($filename))
          $error .= "Change the permissions on \"".$filename."\" to allow the apache user to write to the directory.<br/>";
          
        // Check permissions on mispellings file
        $filename = $_SESSION["paths"]["resources"].DIRECTORY_SEPARATOR."mispellings";
        if (!is_writable($filename))
          $error .= "Change the permissions on \"".$filename."\" to allow the apache user to write to the file.<br/>";
          
        // Check for error
        if (empty($error))
        {
          // Increment step
          $step++;
        }

        
      break;
    // Crontab
    case 5:
      $step++;
      break;  
    //Compile source code
    case 6:
      $step++;
      break;
  }
}

// Check if there was posted data that was successfully processed
if ((!empty($_POST) || isset($_GET['upgrade'])  || $_GET['submit']) && ($step > $_SESSION['step']))
{
  $_SESSION['step'] = $step;
  session_commit();
  header("Location: index.php");
}

// Get page for current step
ob_start();
require_once("html/step".$step.".php");
$pagedata = ob_get_clean();

require_once("html/htmlmain.php");

session_commit();

function parentDir($path)
{
  $dirs = explode(DIRECTORY_SEPARATOR, $path);
  if (count($dirs) == 0)
    return false;
  if (count($dirs) == 1)
    return DIRECTORY_SEPARATOR;
  unset($dirs[count($dirs)-1]);
  return implode(DIRECTORY_SEPARATOR, $dirs);
}

function fileInIncludePath($file)
{
  $paths = explode(PATH_SEPARATOR, get_include_path());
  $found = false;
  for ($i = 0; !$found && $i < count($paths); $i++)
  {
    if (file_exists($paths[$i] . DIRECTORY_SEPARATOR . $file))
      $found = true;
  }
      
  return $found;
}

?>
