<?php
  function securityFormInputs()
  {
    // Create random string and store in session with expiration time and another random string associated with this page
    $random1 = "";
    for ($i = 0; $i < rand(8, 12); $i++)
      $random1 .= (string)rand(0, 999) . chr(rand(65, 90));
    $random2 = "";
    for ($i = 0; $i < rand(8, 12); $i++)
      $random2 .= (string)rand(0, 999) . chr(rand(65, 90));

    $security = array("page"=>$random2, "rand"=>$random1, "expire"=>time()+600);

    if (!isset($_SESSION['security']))
      $_SESSION['security'] = array();
    array_push($_SESSION['security'], $security);

    return array('rand' => $random1, 'page_sec' => $random2);
  }

  /*****************************************************************************
  * Function returns codes to indicate if the security information is valid:
  *   0 - Session variable to store security info is not set
  * + 1 - Submission is secure
  *   2 - Random security number incorrect
  *   3 - Security information found and rand number correct but expired
  *   4 - Page not found in session variable
  *****************************************************************************/
  function checkSecurity($page, $rand)
  {
    if (!isset($_SESSION['security']))
    {
      $_SESSION['msg']['error'] = "Security error.";
      return 0;
    }

    // Return value
    $rtn = 4;  // Page info not found

    reset($_SESSION['security']);

    // Check if first one is valid
    $arr = current($_SESSION['security']);
    do
    {
      if ($arr['page'] == $page)
      {
        if ($arr['rand'] == $rand)
          if ($arr['expire'] > time())
            $rtn = 1;    // Valid
          else
          {
            $rtn = 3;    // Expired
            $_SESSION['msg']['error'] = "Security error.  Form timed out.";
          }
        else
        {
          $rtn = 2;      // Random number incorrect
          $_SESSION['msg']['error'] = "Security error.";
        }
        unset($_SESSION['security'][key($_SESSION['security'])]);
      }
    } while ($arr = next($_SESSION['security']));

    reset($_SESSION['security']);

    if ($rtn == 4)
      $_SESSION['msg']['error'] = "Security error.";

    return $rtn;
  }
?>
