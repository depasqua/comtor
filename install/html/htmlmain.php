<?php echo '<?xml version="1.0" encoding="utf-8"?>'; ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs" lang="cs">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <title>COMTOR Install Wizard</title>
    
    <link rel="stylesheet" type="text/css" href="style.css"></link>
  </head>
  <body>
  
  <img src="img/logo.gif" alt="COMTOR" />
  
  <div id="steps">
  <h3>
    Install Steps<br/>
    <a href="?reset=T">Restart</a>
  </h3>
  
  <ul>
  <?php
  // Output steps
  $count = count($steps);
  for($i = 0; $i < $count; $i++)
  {
  ?>
    <li <?php
          if ($i+1 < $step)
            echo "class=\"finished\"";
          else if ($i+1 == $step)
            echo "class=\"current\"";
        ?>
    >
      <?php echo ($i+1) . ". " . $steps[$i]; ?>
    </li>
  <?php
  }
  ?>
  </ul>
  </div>

  <div id="contentContainer">
    <?php echo $pagedata ?>
  </div>

  </body>
</html>
