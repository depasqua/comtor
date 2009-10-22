<?php

// Define default administrator password
define("ADMIN_PASSWORD", "cmhXE1d/ItCiM");  // Password is "comtor"
session_start();

// Reset link
if (empty($_POST) && isset($_GET['reset']))
  session_unset();

if (!isset($_SESSION['step']))
  $_SESSION['step'] = 1;

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

// Current install step
$step = $_SESSION['step'];

// Setup step names
$steps = array(
  "Software Check",
  "Database Setup",
  "Administrator Setup",
//  "Directory Setup",
  "Configuring",
  "Cron Job Setup",
  "Source Code Compilation",
  "Finish"
);

// Process form data
if (!empty($_POST))
{
  $error = null;
  switch ($step)
  {
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
      // Constants
      define("SCHEMA_PATH", "schema.sql");
      define("MYSQL_DATA_PATH", escapeshellarg("data.sql"));

      // Check fields
      if (!empty($_POST['server']) && !empty($_POST['username']) && !empty($_POST['password']) && !empty($_POST['dbname']))
      {
        $con = @mysql_connect($_POST['server'], $_POST['username'], $_POST['password']);
        if ($con)
        {
          // Check that database exists
          if (!mysql_select_db($_POST['dbname']))
            $error = "Database \"{$_POST['dbname']}\" does not exist or you do not have permission to access it.  Please make sure the database exists and that the username is granted all privileges on the database.";
          else
          {
            // Check that there are no tables in the database
            $result = mysql_query("SHOW TABLES");
            if ((!$result || mysql_num_rows($result)) && !isset($_SESSION['dbempty']))
              $error = "Database already contains tables.  Please drop all tables in database \"{$_POST['dbname']}\".";
            else
            {
              $_SESSION['dbempty'] = true;
              
              // Create new schema with view definers changed
              $host = ($_POST['server'] == "localhost") ? "localhost" : escapeshellarg($_POST['server']);
              $tmpFilename = tempnam(sys_get_temp_dir(), "SQL");
              if (($fh_in = @fopen(SCHEMA_PATH, "r")) && ($fh_out = @fopen($tmpFilename, "w")))
              {                 
                while ($line = fgets($fh_in))
                {
                  $serverName = ($_POST['server'] == "localhost") ? "localhost" : $_SERVER['SERVER_NAME'];
                  $line = str_replace("`comtor`@`localhost`", "`{$_POST['username']}`@`{$serverName}`", $line);
                  fwrite($fh_out, $line);
                }                   
                fclose($fh_in);
                fclose($fh_out);
              
                // Import the database schema
                $hostOpt = ($host == "localhost") ? "" : "-h " . $host;  
                $cmd = sprintf("mysql %s -u %s -p%s %s < %s 2>&1", $hostOpt, escapeshellarg($_POST['username']), escapeshellarg($_POST['password']), escapeshellarg($_POST['dbname']), escapeshellarg($tmpFilename));
                exec($cmd, $output, $rtn);
                unlink($tmpFilename);
                if ($rtn != 0)
                  $error = "Error importing schema. MySQL Output:<br/>" . nl2br(implode("<br/>", $output)); 
                else if (!empty($output))
                  $error = "Error importing schema. MySQL Output:<br/>" . nl2br(implode("<br/>", $output));
                else
                {
                  // Import the database data
                  $cmd = sprintf("mysql %s -u %s -p%s %s < %s 2>&1", $hostOpt, escapeshellarg($_POST['username']), escapeshellarg($_POST['password']), escapeshellarg($_POST['dbname']), MYSQL_DATA_PATH);
                  exec($cmd, $output);
                  if (!empty($output))
                    $error = "Error importing required data. MySQL Output:<br/>" . nl2br(implode("<br/>", $output));
    
                  // Store information in session
                  $_SESSION["mysql"] = array(
                    "server"=>$_POST['server'],
                    "username"=>$_POST['username'],
                    "password"=>$_POST['password'],
                    "dbname"=>$_POST['dbname']
                  );
    
                  // Increment step
                  $step++;
                }
              }
              else
                $error = "Error importing schema.<br/>";
            }
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
    // Admin user setup
    case 3:
      // Check fields
      if (!empty($_POST['name']) && !empty($_POST['school']) && !empty($_POST['email']))
      {
        // TODO: Check $_SESSION vars
        $con = @mysql_connect($_SESSION["mysql"]['server'], $_SESSION["mysql"]['username'], $_SESSION["mysql"]['password']);
        if ($con && mysql_select_db($_SESSION["mysql"]['dbname']))
        {
          // Insert school name and get auto_increment
          $sql = sprintf("INSERT INTO schools(school) VALUES (\"%s\")", mysql_real_escape_string($_POST["school"]));
          if (mysql_query($sql) && ($schoolId = mysql_insert_id()))
          {
            // Insert admin user
            $sql = sprintf("INSERT INTO users (name, email, password, validatedDT, passwordChangeDT, acctType, acctStatus, schoolId) VALUES (\"%s\", \"%s\", \"%s\", NOW(), NOW(), \"admin\", \"enabled\", %d)", mysql_real_escape_string($_POST["name"]), mysql_real_escape_string($_POST["email"]), mysql_real_escape_string(ADMIN_PASSWORD), $schoolId);
            if (mysql_query($sql))
            {
              // Store information in session
              $_SESSION["admin"] = array(
                "email"=>$_POST['email'],
                "school"=>$_POST['school'],
                "name"=>$_POST['name']
              );

              // Increment step
              $step++;
            }
            else
            {
              $error = "Failed to create administrator account.";
            }
          }
          else
            $error = "Failed to create administrator account.";

          // Close database
          mysql_close($con);
        }
        else
          $error = "Failed to connect to database.";
      }
      else
        $error = "Please fill in all fields.";

      break;
    // Directory locations
    /*
    case 4:
      // Convert to real paths
      if (!empty($_POST['www']) && !empty($_POST['private']) && !empty($_POST['code']) && !empty($_POST['uploads']) && !empty($_POST['resources']))
      {
        // Convert to real paths
        $wwwPath = realpath($_POST['www']);
        $wwwParent = parentDir($wwwPath);
        $privatePath = realpath($_POST['private']);
        $privateParent = parentDir($privatePath);
        $codePath = realpath($_POST['code']);
        $codeParent = parentDir($codePath);
        $uploadsPath = realpath($_POST['uploads']);
        $uploadsParent = parentDir($uploadsPath);
        $resourcesPath = realpath($_POST['resources']);
        $resourcesParent = parentDir($resourcesPath);
        
        // Check that the paths if these directories are empty, can be created, and if we can write to these paths
        $error = "";
        if (file_exists($wwwPath))
        {
          if (is_dir($wwwPath))
          {
            // Check if it is an empty directory
            if (count(scandir($wwwPath)) > 2)
              $error .= "Web directory \"{$wwwPath}\" must be empty.<br/>";
            else if (!is_writable($wwwPath)) 
              $error .= "Web directory \"{$wwwPath}\" is not writable.  Please change the owner to the apache user.<br/>";
          }
          else
            $error .= "Web directory name already exists as a file.<br/>";
        }
        else if (!is_writable($wwwParent))
          $error .= "Cannot create web directory. Please chmod 777 \"".$wwwParent."\" to allow it to be writable.<br/>";
          
        if (file_exists($privatePath))
        {
          if (is_dir($privatePath))
          {
            // Check if it is an empty directory
            if (count(scandir($privatePath)) > 2)
              $error .= "Config file directory \"{$privatePath}\" must be empty.<br/>";
            else if (!is_writable($privatePath)) 
              $error .= "Config file directory \"{$privatePath}\" is not writable.  Please change the owner to the apache user.<br/>";
          }
          else
            $error .= "Config file directory name already exists as a file.<br/>";
        }
        else if (!is_writable($privateParent))
          $error .= "Cannot create config file directory. Please chmod 777 \"".$privateParent."\" to allow it to be writable.<br/>";
        
        if (file_exists($codePath))
        {
          if (is_dir($codePath))
          {
            // Check if it is an empty directory
            if (count(scandir($codePath)) > 2)
              $error .= "Java code directory \"{$codePath}\" must be empty.<br/>";
            else if (!is_writable($codePath)) 
              $error .= "Java code directory \"{$codePath}\" is not writable.  Please change the owner to the apache user.<br/>";
          }
          else
            $error .= "Java code directory name already exists as a file.<br/>";
        }
        else if (!is_writable($codeParent))
          $error .= "Cannot create Java code file directory. Please chmod 777 \"".$codeParent."\" to allow it to be writable.<br/>";

        if (file_exists($uploadsPath))
        {
          if (is_dir($uploadsPath))
          {
            // Check if it is an empty directory
            if (count(scandir($uploadsPath)) > 2)
              $error .= "Uploads directory \"{$uploadsPath}\" must be empty.<br/>";
            else if (!is_writable($uploadsPath)) 
              $error .= "Uploads directory \"{$uploadsPath}\" is not writable.  Please change the owner to the apache user.<br/>";
          }
          else
            $error .= "Uploads directory name already exists as a file.<br/>";
        }
        else if (!is_writable($uploadsParent))
          $error .= "Cannot create uploads directory. Please chmod 777 \"".$uploadsParent."\" to allow it to be writable.<br/>";
          
        if (file_exists($resourcesPath))
        {
          if (is_dir($resourcesPath))
          {
            // Check if it is an empty directory
            if (count(scandir($resourcesPath)) > 2)
              $error .= "Resources directory \"{$resourcesPath}\" must be empty.<br/>";
            else if (!is_writable($resourcesPath)) 
              $error .= "Resources directory \"{$resourcesPath}\" is not writable.  Please change the owner to the apache user.<br/>";
          }
          else
            $error .= "Resources directory name already exists as a file.<br/>";
        }
        else if (!is_writable($resourcesParent))
          $error .= "Cannot create resources directory. Please chmod 777 \"".$resourcesParent."\" to allow it to be writable.<br/>";
          
        // Check for error
        if (empty($error))
        {
          // Copy the folders
          $cmd = realpath(".")."/copyDirs.sh ".$wwwPath." ".$privatePath." ".$codePath." ".$uploadsPath." ".$resourcesPath;
          exec($cmd, $output, $rtn);
          switch ($rtn)
          {
            case 0:
              // Copy code jar
              if (copy("comtor.jar", $codePath.DIRECTORY_SEPARATOR."comtor.jar") && mkdir($wwwPath.DIRECTORY_SEPARATOR."templates_c"))
              {            
                // Store information in session
                $_SESSION["paths"] = array(
                  "www"=>$wwwPath,
                  "private"=>$privatePath,
                  "code"=>$codePath,
                  "uploads"=>$uploadsPath,
                  "resources"=>$resourcesPath
                );                             

                // Increment step
                $step++;
              }
              else
                $error .= "Failed to create copy java code.<br/>";
              break;
            case 1:
              $error .= "Failed to create web directory.<br/>";
              break;
            case 2:
              $error .= "Failed to create config file directory.<br/>";
              break;
            case 3:
              $error .= "Failed to create Java code directory.<br/>";
              break;
            case 4:
              $error .= "Failed to create uploads directory.<br/>";
              break;
            case 5:
              $error .= "Failed to create resources directory.<br/>";
              break;
          }
        }
      }
      else
        $error = "Please fill in all fields.";
        
      break;
    */
    // Configuring
    case 4:
      // Check fields
      if (!empty($_POST['smtp']) && !empty($_POST['url']))
      {
        // Store information in session
        $_SESSION["smtp"] = $_POST['smtp'];
        $_SESSION["url"] = $_POST['url'];
        
        clearstatcache();
        
        // Create config file
        ob_start();
        require_once("config_php_template.php");
        $configData = ob_get_clean();
        
        // Open config file
        $error = "";
        $filename = $_SESSION["paths"]["private"].DIRECTORY_SEPARATOR."config.php";
        if ((is_writable($filename) || @touch ($filename)) && $fh = fopen($filename, "w"))
        {
          // Write to file and close
          if (!fwrite($fh, $configData))
            $error .= "Failed to write to \"".$filename."\"<br/>";
          fclose($fh);
        }
        else
          $error .= "Failed to write to \"".$filename."\"<br/>";
          
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
      }
      else
        $error = "Please fill in all fields.";
        
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
if (!empty($_POST) && $step == $_SESSION['step'] + 1)
{
  $_SESSION['step']++;
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
