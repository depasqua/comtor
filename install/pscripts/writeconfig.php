<?php

// try writing the config file, and return any errors
// where is the target config.php located?
$filename = $config_php_path;

// construct special case variables from the collected information
$toconfig_dir = $_SESSION["paths"]["www"].DIRECTORY_SEPARATOR;
$_SESSION['toconfig']['UPLOAD_DIR'] = $_SESSION["paths"]["uploads"].DIRECTORY_SEPARATOR;

    if ($fp = fopen($filename, 'w'))
    {
        if (fwrite($fp, "<?php\n")===FALSE)
        {
            $errors = 1;
            $errorlist[] = "Cannot write to the file!";
        }
        // write each toconfig entry to the config file, one-by-one
        foreach ($_SESSION['toconfig'] as $key => $value)
            fwrite($fp, 'define("'.$key.'", "'.$value.'");'."\n");
        fwrite($fp, '$dir = "'.$toconfig_dir.'";'."\n");
        fwrite($fp, 'define("DEVELOPMENT", false);'."\n");
        fwrite($fp, 'define("EMAIL_FROM", "Comment Mentor <no-reply@localhost>");'."\n");
        fwrite($fp, 'define("EMAIL_SEND", true);'."\n");
        fwrite($fp, "?>\n");
        fclose($fp);
        $_SESSION['config_done'] = true;
    }
    else
    {
        $errors = 1;
        $errorlist[] = "Could not open the file!";
    }


?>