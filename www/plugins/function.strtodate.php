<?php

function smarty_function_strtodate($params, &$smarty)
{
  // Default parameters
  $format = "c";
  $date = "";

  // Process the parameters
  foreach ($params as $key=>$value)
    switch($key)
    {
      case "format":
          $format = $value;
        break;
      case "date":
        $date = $value;
        break;
    }

  $date = strtotime($date);

  return date($format, $date);
}

?>
