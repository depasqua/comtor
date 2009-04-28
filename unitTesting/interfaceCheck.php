<?php

require_once('simpletest/autorun.php');
require_once('simpletest/browser.php');

require_once('/home/comtor/comtor_dev/config.php');
require_once('/home/comtor/comtor_dev/mysqlFunctions.php');

$con = @mysql_connect(MYSQL_HOST, MYSQL_USERNAME, MYSQL_PASSWORD);
@mysql_select_db(MYSQL_DB);

// Default usernames
define("DEFAULT_USERNAME", "test@localhost");
define("DEFAULT_PASSWORD", "comtorunittest");
define("DEFAULT_PROF", "prof@localhost");
define("DEFAULT_PROF_PASSWORD", "comtorunittest");
define("DEFAULT_ADMIN", "admin@localhost");
define("DEFAULT_ADMIN_PASSWORD", "comtorunittest");
define("DEFAULT_ADMIN2", "admin2@localhost");
define("DEFAULT_ADMIN2_PASSWORD", "comtorunittest");
define("NEW_USERNAME", "comtorUnitTestCreatedAccount@localhost");
define("START_URL", 'http://depasquale-1.tcnj.edu/comtor_dev/');

// Contains static methods needed by multiple tests
class Common
{
  // Logs in and returns bool indicating success or not
  public static function login(&$browser, $username = DEFAULT_USERNAME, $password = DEFAULT_PASSWORD)
  {
    // Go to website
    $browser = new SimpleBrowser();
    $browser->restart();
    $browser->get(START_URL);
    
    // Login
    $browser->setField('email', $username);
    $browser->setField('password', $password);
    $browser->click('Login');
    
    // Check that the logout link is present (therefore login was successful)
    return $browser->isClickable("(Logout)");
  }
  
  // Logs out and returns bool indicating success or not
  public static function logout(&$browser)
  {
    // Logout
    $browser->click('(Logout)');
    
    // Check that the logout link is not present (therefore logout was successful)
    return !$browser->isClickable("(Logout)");
  }
  
  // Gets user id from email
  public static function getUserId($email)
  {
    global $con;
    $query = "SELECT userId FROM users WHERE email = '".$email."'";
    $result = @mysql_query($query, $con);
    return mysql_result($result, 0, 0);
  }
}
    
class LoginTest extends UnitTestCase 
{
  function testLoginLogout() 
  { 
    /* Test Successful login */
    $browser;
    $this->assertTrue(Common::login($browser, DEFAULT_USERNAME, DEFAULT_PASSWORD), "Failed to login");
    $this->assertTrue(Common::logout($browser), "Failed to logout");
    
    // Check professor account
    $this->assertTrue(Common::login($browser, DEFAULT_PROF, DEFAULT_PROF_PASSWORD), "Failed to login for professor account");
    $this->assertTrue(Common::logout($browser), "Failed to logout");
    
    // Check admin account
    $this->assertTrue(Common::login($browser, DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD), "Failed to login for admin account");
    $this->assertTrue(Common::logout($browser), "Failed to logout");
    
    /* Test Failed login */
    $this->assertFalse(Common::login($browser, 'test@localhost', 'badpassword'), "Login not rejected");
    
    /* Test Failed login */
    $this->assertFalse(Common::login($browser, 'badusername@localhost', 'badpassword'), "Login not rejected");
  }
}

class UsageTest extends UnitTestCase 
{
  function testUsagePage() 
  {     
    // Test default student account
    $this->usagePage();
    
    // Test professor account
    $this->usagePage(DEFAULT_PROF, DEFAULT_PROF_PASSWORD);
    
    // Test admin account
    $this->usagePage(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, true);
  }
  
  function usagePage($username = DEFAULT_USERNAME, $password = DEFAULT_PASSWORD) 
  {     
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, $username, $password), "Failed to login");
    
    // Go to system usage page
    $this->assertNotIdentical($browser->click('System Usage'), false, "System usage link does not exist");
        
    // Check for <h1>System Usage</h1>
    $this->assertEqual(preg_match("/<h1>System Usage<\\/h1>/i", $browser->getContent()), 1, "System usage header not found");
  }
}

class AccountInfoTest extends UnitTestCase 
{
  function testCreateStudentAccount()
  {
    $this->createAccount();
  }
  
  function createAccount()
  {
    global $con;
    $this->_createAccount("Unit Test New Account", "The College of New Jersey", NEW_USERNAME);
    
    // Get encrypted password
    $tmp = "abcdef";
    $query = "SELECT password FROM users WHERE email = '".DEFAULT_USERNAME."'";
    if ($result = @mysql_query($query, $con))
      $tmp = mysql_result($result, 0, 0);
    // Update the password in the database
    $query = "UPDATE users SET password = '{$tmp}' WHERE email = '".NEW_USERNAME."'";
    $this->assertNotIdentical(@mysql_query($query, $con), false, "Failed to change password in database");
  }
  
  function testcreateProfAccount()
  {
    global $con;
    $this->_createAccount("Unit Test New Account", "The College of New Jersey", "newprof@localhost", true, "http://www.comtor.org/");
    
    // Get encrypted password
    $tmp = "abcdef";
    $query = "SELECT password FROM users WHERE email = '".DEFAULT_USERNAME."'";
    if ($result = @mysql_query($query, $con))
      $tmp = mysql_result($result, 0, 0);
    // Update the password in the database
    $query = "UPDATE users SET password = '{$tmp}' WHERE email = 'newprof@localhost'";
    $this->assertNotIdentical(@mysql_query($query, $con), false, "Failed to change password in database");
    
    // Grant professor request
    $this->grantAcctTypeRequest(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, "newprof@localhost");
  }
  
  function testDeleteAccount()
  {
    $this->clickLinkForOtherAccount(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, NEW_USERNAME, "deleteAccount");
    $this->clickLinkForOtherAccount(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, "newprof@localhost", "deleteAccount");
  }

  function testInfoChange() 
  {
    // Test default student account
    $this->changeInfo();
    
    // Test professor account
    $this->changeInfo(DEFAULT_PROF, DEFAULT_PROF_PASSWORD);
    
    // Test admin account
    $this->changeInfo(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, true);
  }
  
  function testChangeUserEmail()
  {
    $this->editAccountEmail(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, DEFAULT_PROF, "changedEmail@localhost", true);
  }
  
  function testPasswordChange() 
  {
    // Test default student account
    $this->changePassword("Test");
    
    // Test professor account
    $this->changePassword("Test", DEFAULT_PROF, DEFAULT_PROF_PASSWORD);
    
    // Test admin account
    $this->changePassword("Test", DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, true);
  }
  
  function testDisableOwnAccount() 
  {
    // Test default student account
    $this->disableOwnAccount();
    $this->enableAccount();
    
    // Test professor account
    $this->disableOwnAccount(DEFAULT_PROF, DEFAULT_PROF_PASSWORD);
    $this->enableAccount(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, DEFAULT_PROF, DEFAULT_PROF_PASSWORD);
    
    // Test admin account
    $this->disableOwnAccount(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, true);
    $this->enableAccount(DEFAULT_ADMIN2, DEFAULT_ADMIN2_PASSWORD, DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD);
  }
  
  function testRecoverPassword() 
  {
    // Test default student account
    $this->recoverPassword();
    
    // Test professor account
    $this->recoverPassword(DEFAULT_PROF);
    
    // Test admin account
    $this->recoverPassword(DEFAULT_ADMIN);
  }
  
  function recoverPassword($username = DEFAULT_USERNAME)
  {
    // Get encrypted password
    global $con;
    $tmp = "abcdef";
    $query = "SELECT password FROM users WHERE email = '".$username."'";
    if ($result = @mysql_query($query, $con))
      $tmp = mysql_result($result, 0, 0);
  
    // Go to website
    $browser = new SimpleBrowser();
    $browser->restart();
    $this->assertNotIdentical($browser->get(START_URL), false, "Website not found");
    
    // Go to create account link
    $this->assertNotIdentical($browser->click("Password Recovery"), false, "Recover password link not found"); 
    
    // Change fields
    $browser->setField("email", $username);
    
    // Submit
    $this->assertNotIdentical($browser->click('Submit'), false, "Failed to submit form");
    
    // Check the message
    $msg = "Your password has been mailed to you!";
    $this->assertNotIdentical(strpos($browser->getContentAsText(), $msg), false, "Failed to create account");
    
    // Update the password in the database
    $query = "UPDATE users SET password = '{$tmp}' WHERE email = '".$username."'";
    $this->assertNotIdentical(@mysql_query($query, $con), false, "Failed to change password in database");  
  }
  
  function testDisableOtherAccount()
  {
    $this->clickLinkForOtherAccount(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, DEFAULT_USERNAME, "disableAccount");
    $this->enableAccount(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, DEFAULT_USERNAME, DEFAULT_PASSWORD);
  }

  function testChangeAccountType()
  { 
    // Change the account type for student user 
    $this->changeAccountType("professor", DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, DEFAULT_USERNAME);
  }
  
  function testNotificationChange() 
  {
    // Test default student account
    $this->changeNotification();
    
    // Test professor account
    $this->changeNotification("professor", DEFAULT_PROF, DEFAULT_PROF_PASSWORD);
    
    // Test admin account
    $this->changeNotification("admin", DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, true);
  }
  
  function _createAccount($name, $school, $email, $professor = false, $profLink = "")
  {    
    // Go to website
    $browser = new SimpleBrowser();
    $browser->restart();
    $this->assertNotIdentical($browser->get(START_URL), false, "Website not found");
    
    // Go to create account link
    $this->assertNotIdentical($browser->click("Create An Account"), false, "Create account link not found"); 
    
    // Change fields
    $browser->setField("name", $name);
    $browser->setField("school", $school);
    $browser->setField("email", $email);
    
    // Set professor information
    if ($professor)
    {
      $this->assertNotIdentical($browser->setField("prof_req", "on"), false, "Professor checkbox not found");  // May only work in Firefox
      $this->assertNotIdentical($browser->setField("prof_link", $profLink), false, "Professor link input not found");
    }
    
    // Register
    $this->assertNotIdentical($browser->click('Register'), false, "Failed to submit form");
    
    // Check the message
    $msg = "Your account has been successfully created.";
    $this->assertNotIdentical(strpos($browser->getContentAsText(), $msg), false, "Failed to create account for \"{$email}\"");
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
  }
  
  function changeNotification($acctType = "student", $username = DEFAULT_USERNAME, $password = DEFAULT_PASSWORD)
  {
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, $username, $password), "Failed to login");
    
    // Go to E-mail notifications page
    $this->assertNotIdentical($browser->click('E-mail Notifications'), false, "E-mail notifications link does not exist");
    
    // Get the current notification frequency
    $freq = $browser->getField("frequency");
    $types = $browser->getField("notify_types[]");
    $newFreq = $newTypes = array();
    
    // Student
    if ($acctType == "student")
    {
      $newFreq = NOTIFY_FREQ_ON_HOUR;
      $newTypes = array(NOTIFY_ASSIGNMENT_NEW, NOTIFY_ASSIGNMENT_DEADLINE_NEAR);
    }
    // Professor
    if ($acctType == "professor")
    {
      $newFreq = NOTIFY_FREQ_ON_HOUR;
      $newTypes = array(NOTIFY_STUDENT_SUBMISSION);
    }
    // Admin
    if ($acctType == "admin")
    {
      $newFreq = NOTIFY_FREQ_ON_HOUR;
      $newTypes = NOTIFY_REQUEST;
    }
    
    // Set the new values
    $this->assertTrue($browser->setField("frequency", $newFreq), "Notification frequency field does not exist for user ".$username);
    $this->assertTrue($browser->setField("notify_types[]", $newTypes), "Notification types field for does not exist");
    
    // Submit the form
    $this->assertNotIdentical($browser->click('Submit'), false, "Failed to submit form");
    
    // Check for success message
    $msg = "Notification options updated.";
    $this->assertNotIdentical(strpos($browser->getContentAsText(), $msg), false, "Failed to submit form"); 
    
    // Set the old values
    $this->assertTrue($browser->setField("frequency", $freq), "Notification frequency field does not exist");
    $this->assertTrue($browser->setField("notify_types[]", $types), "Notification types field does not exist");
    
    // Submit the form
    $this->assertNotIdentical($browser->click('Submit'), false, "Failed to submit form");
    
    // Check for success message
    $msg = "Notification options updated.";
    $this->assertNotIdentical(strpos($browser->getContentAsText(), $msg), false, "Failed to submit form");   
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
  }
  
  function goToAccountManagement($username = DEFAULT_USERNAME, $password = DEFAULT_PASSWORD, $admin = false)
  {
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, $username, $password), "Failed to login");
    
    // Go to account management page
    $this->assertNotIdentical($browser->click('Account Management'), false, "Account management link does not exist");
    
    // If admin, find this username in the list
    if ($admin)
    { 
      // Search for link for this username
      $pattern = "/<tr[^>]*>.*".preg_quote($username, "/").".*<a[^>]*href\\s*\\=\\s*\"(editAccount\\.php[^\"]*)\"[^>]*>.*<\\/tr[^>]*>/imsU";
      $num = preg_match($pattern, $browser->getContent(), $matches);
      $this->assertEqual($num, 1, "Edit account link does not exist. {$pattern}");
      if ($num)
      {
        // Find the absolute link that ends with this
        $arr = $browser->getUrls();
        $cnt = count($arr);
        $found = false;
        $pattern = "/" . preg_quote(htmlspecialchars_decode($matches[1]), "/") . "$/i";
        for ($i = 0; !$found && $i < $cnt; $i++)
        {
          if (preg_match($pattern, $arr[$i]) == 1)
          {
            $found = true;
            $this->assertNotIdentical($browser->get($arr[$i]), false, "Edit account link is invalid");
          }
        }
        
        $this->assertTrue($found, "Edit account link not found");
      } 
    }
    
    return $browser;
  }
  
  function changeInfo($username = DEFAULT_USERNAME, $password = DEFAULT_PASSWORD, $admin = false)
  {    
    // Login
    $browser = $this->goToAccountManagement($username, $password, $admin);
        
    // Get current name
    $name = $browser->getField("name");
    $names = array("Test Account 1a", "Test Account 1b");
    $idx = ($name == "Test Account 1a");
    
    // Change name
    $browser->setField("name", $names[$idx]);
    
    // Change school
    $browser->setField("school", "The College of New Jersey");
    
    $this->assertNotIdentical($browser->click('Update Account'), false, "Failed to submit form");
    
    // Check that name has been changed
    $this->assertEqual($browser->getField("name"), $names[$idx], "Failed to change user name");
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
  }
  
  function editAccountEmail($adminUsername, $adminPassword, $username, $newEmail, $changeBack = true)
  {    
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, $adminUsername, $adminPassword), "Failed to login");
    
    // Go to account management page
    $this->assertNotIdentical($browser->click('Account Management'), false, "Account management link does not exist");

    // Search for link to enable the given username
    $pattern = "/<tr[^>]*>.*".preg_quote($username, "/").".*<a[^>]*href\\s*\\=\\s*\"(editAccount\\.php[^\"]*)\"[^>]*>.*<\\/tr[^>]*>/imsU";
    $num = preg_match($pattern, $browser->getContent(), $matches);
    $this->assertEqual($num, 1, "Edit account link does not exist");
    if ($num)
    {
      // Find the absolute link that ends with this
      $arr = $browser->getUrls();
      $cnt = count($arr);
      $found = false;
      $pattern = "/" . preg_quote(htmlspecialchars_decode($matches[1]), "/") . "$/i";
      for ($i = 0; !$found && $i < $cnt; $i++)
      {
        if (preg_match($pattern, $arr[$i]) == 1)
        {
          $found = true;
          $this->assertNotIdentical($browser->get($arr[$i]), false, "Edit account link is invalid");
          
          // Get current name
          $name = $browser->getField("name");
          $names = array("Test Account 1a", "Test Account 1b");
          $idx = ($name == "Test Account 1a");
          
          // Change e-mail address
          $browser->setField("email", $newEmail);
          
          $this->assertNotIdentical($browser->click('Update Account'), false, "Failed to submit form");
          
          // Check that name has been changed
          $this->assertEqual($browser->getField("name"), $names[$idx], "Failed to change user E-mail");
          
          if ($changeBack)
            $this->editAccountEmail($adminUsername, $adminPassword, $newEmail, $username, false);
        }
      }
      
      $this->assertTrue($found, "Edit account link not found");
    } 
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
  }
  
  function changePassword($newPassword, $username = DEFAULT_USERNAME, $password = DEFAULT_PASSWORD, $admin = false, $first = true)
  {
    // Login
    $browser = $this->goToAccountManagement($username, $password, $admin);
    
    // Go to change password page
    $this->assertNotIdentical($browser->click('Change Password'), false, "Change password link does not exist for user {$username}");
    
    // Fill out form
    $this->assertTrue($browser->setField("oldPassword", $password), "Old password field does not exist");
    $this->assertTrue($browser->setField("newPassword", $newPassword), "New password field does not exist");
    $this->assertTrue($browser->setField("confirmPassword", $newPassword), "Confirm password field does not exist");
    $this->assertNotIdentical($browser->click('Submit'), false, "Failed to submit form");
    
    // Log out and log back in
    $this->assertTrue(Common::logout($browser), "Failed to logout");
    $this->assertTrue(Common::login($browser, $username, $newPassword), "Failed to login");
    
    // Change back to original
    if ($first)
      $this->changePassword($password, $username, $newPassword, $admin, false);
  }
  
  function disableOwnAccount($username = DEFAULT_USERNAME, $password = DEFAULT_PASSWORD, $admin = false)
  {    
    // Login
    $browser = $this->goToAccountManagement($username, $password, $admin);
    
    // Check Disable Account Link
    $this->assertNotIdentical($browser->click('Disable Account'), false, "Disable account link does not exist for user \"{$username}\"");
        
    // Try, unsuccessfully, to log back in
    $this->assertFalse(Common::login($browser, $username, $newPassword), "Login should have failed");
  }
  
  function enableAccount($adminUsername = DEFAULT_ADMIN, $adminPassword = DEFAULT_ADMIN_PASSWORD, $username = DEFAULT_USERNAME, $password = DEFAULT_PASSWORD)
  {    
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, $adminUsername, $adminPassword), "Failed to login");
    
    // Go to account management page
    $this->assertNotIdentical($browser->click('Account Management'), false, "Account management link does not exist");

    // Search for link to enable the given username
    $pattern = "/<tr[^>]*>.*".preg_quote($username, "/").".*<a[^>]*href\\s*\\=\\s*\"(enableAccount\\.php[^\"]*)\"[^>]*>.*<\\/tr[^>]*>/imsU";
    $num = preg_match($pattern, $browser->getContent(), $matches);
    $this->assertEqual($num, 1, "Enable account link does not exist");
    if ($num)
    {
      // Find the absolute link that ends with this
      $arr = $browser->getUrls();
      $cnt = count($arr);
      $found = false;
      $pattern = "/" . preg_quote(htmlspecialchars_decode($matches[1]), "/") . "$/i";
      for ($i = 0; !$found && $i < $cnt; $i++)
      {
        if (preg_match($pattern, $arr[$i]) == 1)
        {
          $found = true;
          $this->assertNotIdentical($browser->get($arr[$i]), false, "Enable account link is invalid");
        }
      }
      
      $this->assertTrue($found, "Enable account link not found");
    } 
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
        
    // Log in with enabled username
    $this->assertTrue(Common::login($browser, $username, $password), "Login failed");
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
  }
  
  function clickLinkForOtherAccount($adminUsername = DEFAULT_ADMIN, $adminPassword = DEFAULT_ADMIN_PASSWORD, $username = DEFAULT_USERNAME, $link)
  {    
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, $adminUsername, $adminPassword), "Failed to login");
    
    // Go to account management page
    $this->assertNotIdentical($browser->click('Account Management'), false, "Account management link does not exist");

    // Search for link to enable the given username
    $pattern = "/<tr[^>]*>.*".preg_quote($username, "/").".*<a[^>]*href\\s*\\=\\s*\"(".$link."\\.php[^\"]*)\"[^>]*>.*<\\/tr[^>]*>/imsU";
    $num = preg_match($pattern, $browser->getContent(), $matches);
    $this->assertEqual($num, 1, "Link does not exist");
    if ($num)
    {
      // Find the absolute link that ends with this
      $arr = $browser->getUrls();
      $cnt = count($arr);
      $found = false;
      $pattern = "/" . preg_quote(htmlspecialchars_decode($matches[1]), "/") . "$/i";
      for ($i = 0; !$found && $i < $cnt; $i++)
      {
        if (preg_match($pattern, $arr[$i]) == 1)
        {
          $found = true;
          $this->assertNotIdentical($browser->get($arr[$i]), false, "Link is invalid");
          
          // Check for success box
          $msg = "class=\"success\"";
          $this->assertNotIdentical(strpos($browser->getContent(), $msg), false, "Failed to submit form");
        }
      }
      
      $this->assertTrue($found, "Link not found");
    } 
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
  }
  
  function changeAccountType($newAccountType, $adminUsername = DEFAULT_ADMIN, $adminPassword = DEFAULT_ADMIN_PASSWORD, $username = DEFAULT_USERNAME, $restore = true)
  {    
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, $adminUsername, $adminPassword), "Failed to login");
    
    // Go to account management page
    $this->assertNotIdentical($browser->click('Account Management'), false, "Account management link does not exist");

    // Search for link to enable the given username
    $pattern = "/<tr[^>]*>.*".preg_quote($username, "/").".*((?:student)|(?:professor)|(?:admin)).*<a[^>]*href\\s*\\=\\s*\"(editAccount\\.php[^\"]*)\"[^>]*>.*<\\/tr[^>]*>/imsU";
    $num = preg_match($pattern, $browser->getContent(), $matches);
    $this->assertEqual($num, 1, "Edit account link does not exist");
    if ($num)
    {
      // Store old account type
      $oldType = $matches[1];
    
      // Find the absolute link that ends with this
      $arr = $browser->getUrls();
      $cnt = count($arr);
      $found = false;
      $pattern = "/" . preg_quote(htmlspecialchars_decode($matches[2]), "/") . "$/i";
      for ($i = 0; !$found && $i < $cnt; $i++)
      {
        if (preg_match($pattern, $arr[$i]) == 1)
        {
          $found = true;
          $this->assertNotIdentical($browser->get($arr[$i]), false, "Edit account link is invalid");
          
          // Change the account type
          $this->assertTrue($browser->setField("acctType", $newAccountType), "Account type field does not exist");
          $this->assertNotIdentical($browser->click('Update Account'), false, "Update account button does not exist");
          
          // Check that the account type is changed
          // First, go to account management page
          $this->assertNotIdentical($browser->click('Account Management'), false, "Account management link does not exist");
          $pattern = "/<tr[^>]*>.*".preg_quote($username, "/").".*".preg_quote($newAccountType, "/").".*<a[^>]*href\\s*\\=\\s*\"editAccount\\.php[^\"]*\"[^>]*>.*<\\/tr[^>]*>/imsU";
          $this->assertTrue(preg_match($pattern, $browser->getContent(), $matches) == 1, "Failed to change account type");
        }
      }
      
      $this->assertTrue($found, "Edit account link not found");
    } 
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
    
    // Restore the original account type
    if ($restore)
      $this->changeAccountType($oldType, $adminUsername, $adminPassword, $username, false);
  }
  
  function testRequests()
  {
    // Make request from student
    $this->makeAcctTypeRequest(DEFAULT_USERNAME, DEFAULT_PASSWORD, "Professor");
    
    // Make request from professor
    $this->makeAcctTypeRequest(DEFAULT_PROF, DEFAULT_PROF_PASSWORD, "Admin");
    
    // Create new account and request removal
    $this->createAccount();
    $this->makeAcctRemovalRequest(NEW_USERNAME, DEFAULT_PASSWORD);
    
    // Grant requests
    $this->grantAcctTypeRequest(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, DEFAULT_USERNAME);
    $this->denyAcctTypeRequest(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, DEFAULT_PROF);
    $this->grantAcctRemovalRequest(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, NEW_USERNAME);
    
    // Make request back to student
    $this->makeAcctTypeRequest(DEFAULT_USERNAME, DEFAULT_PASSWORD, "Student");
    $this->grantAcctTypeRequest(DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD, DEFAULT_USERNAME);
  }
  
  function makeAcctTypeRequest($username = DEFAULT_USERNAME, $password = DEFAULT_PASSWORD, $newAccountType) 
  {
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, $username, $password), "Failed to login");
    
    // Go to requests page
    $this->assertNotIdentical($browser->click('Requests'), false, "Requests link does not exist");
    
    // Change select box
    $this->assertTrue($browser->setField("acctType", $newAccountType), "Account type field does not exist");
    
    // Add comment
    $this->assertTrue($browser->setField("comment", "This request was generated by the unit tester."), "Comment field does not exist");
    
    // Submit
    $this->assertNotIdentical($browser->submitFormByName("acctTypeForm"), false, "Failed to submit form");    
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
  }
  
  function makeAcctRemovalRequest($username = DEFAULT_USERNAME, $password = DEFAULT_PASSWORD) 
  {
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, $username, $password), "Failed to login");
    
    // Go to requests page
    $this->assertNotIdentical($browser->click('Requests'), false, "Requests link does not exist");
    
    // Click the request deletion link
    $this->assertNotIdentical($browser->click('Request Account Removal'), false, "Request Account Removal link does not exist");    
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
  }
  
  function grantAcctRemovalRequest($adminUsername = DEFAULT_ADMIN, $adminPassword = DEFAULT_ADMIN_PASSWORD, $username = DEFAULT_USERNAME) 
  {
    global $con;
  
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, $adminUsername, $adminPassword), "Failed to login");
    
    // Go to requests page
    $this->assertNotIdentical($browser->click('Requests'), false, "Requests link does not exist");
    
    // Get user id of user
    $userId = Common::getUserId($username);
    
    // Search for link for this username
    $pattern = "/<a[^>]*href\\s*\\=\\s*\"(request_accept\\.php\?type=acctRemove[^\"]*userId={$userId})\"/imsU";
    $num = preg_match($pattern, $browser->getContent(), $matches);
    $this->assertTrue($num > 0, "Accept request link does not exist. {$pattern}");
    if ($num)
    {
      // Find the absolute link that ends with this
      $arr = $browser->getUrls();
      $cnt = count($arr);
      $found = false;
      $pattern = "/" . preg_quote(htmlspecialchars_decode($matches[1]), "/") . "$/i";
      for ($i = 0; !$found && $i < $cnt; $i++)
      {
        if (preg_match($pattern, $arr[$i]) == 1)
        {
          $found = true;
          $this->assertNotIdentical($browser->get($arr[$i]), false, "Accept request link is invalid");
        }
      }
      
      $this->assertTrue($found, "Accept request link not found");
    }    
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
  }
  
  function grantAcctTypeRequest($adminUsername = DEFAULT_ADMIN, $adminPassword = DEFAULT_ADMIN_PASSWORD, $username = DEFAULT_USERNAME) 
  {
    global $con;
  
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, $adminUsername, $adminPassword), "Failed to login");
    
    // Go to requests page
    $this->assertNotIdentical($browser->click('Requests'), false, "Requests link does not exist");
    
    // Get user id of user
    $userId = Common::getUserId($username);
    
    // Get request id for the user
    $reqId = -1; 
    $query = "SELECT req_id FROM request_acct_change WHERE userId = '".$userId."' ORDER BY req_id DESC";
    $this->assertNotIdentical($result = @mysql_query($query, $con), false, "Failed to get user id from database");
      $reqId = mysql_result($result, 0, 0);
    
    // Search for link for this username
    $pattern = "/<a[^>]*href\\s*\\=\\s*\"(request_accept\\.php\?type=acctType[^\"]*req_id={$reqId})\"/imsU";
    $num = preg_match($pattern, $browser->getContent(), $matches);
    $this->assertTrue($num > 0, "Accept request link does not exist. {$pattern}");
    if ($num)
    {
      // Find the absolute link that ends with this
      $arr = $browser->getUrls();
      $cnt = count($arr);
      $found = false;
      $pattern = "/" . preg_quote(htmlspecialchars_decode($matches[1]), "/") . "$/i";
      for ($i = 0; !$found && $i < $cnt; $i++)
      {
        if (preg_match($pattern, $arr[$i]) == 1)
        {
          $found = true;
          $this->assertNotIdentical($browser->get($arr[$i]), false, "Accept request link is invalid");
        }
      }
      
      $this->assertTrue($found, "Accept request link not found");
    }    
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
  }
  
  function denyAcctTypeRequest($adminUsername = DEFAULT_ADMIN, $adminPassword = DEFAULT_ADMIN_PASSWORD, $username = DEFAULT_USERNAME) 
  {
    global $con;
  
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, $adminUsername, $adminPassword), "Failed to login");
    
    // Go to requests page
    $this->assertNotIdentical($browser->click('Requests'), false, "Requests link does not exist");
    
    // Get user id of user
    $userId = Common::getUserId($username);
    
    // Get request id for the user
    $reqId = -1; 
    $query = "SELECT req_id FROM request_acct_change WHERE userId = '".$userId."' ORDER BY req_id DESC";
    $this->assertNotIdentical($result = @mysql_query($query, $con), false, "Failed to get user id from database");
      $reqId = mysql_result($result, 0, 0);
    
    // Search for link for this username
    $pattern = "/<a[^>]*href\\s*\\=\\s*\"(request_reject\\.php\?type=acctType[^\"]*req_id={$reqId})\"/imsU";
    $num = preg_match($pattern, $browser->getContent(), $matches);
    $this->assertTrue($num > 0, "Reject request link does not exist. {$pattern}");
    if ($num)
    {
      // Find the absolute link that ends with this
      $arr = $browser->getUrls();
      $cnt = count($arr);
      $found = false;
      $pattern = "/" . preg_quote(htmlspecialchars_decode($matches[1]), "/") . "$/i";
      for ($i = 0; !$found && $i < $cnt; $i++)
      {
        if (preg_match($pattern, $arr[$i]) == 1)
        {
          $found = true;
          $this->assertNotIdentical($browser->get($arr[$i]), false, "Reject request link is invalid");
        }
      }
      
      $this->assertTrue($found, "Reject request link not found");
    }    
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
  }
}


class SystemInfoTest extends UnitTestCase 
{
  function testViewAllAccounts() 
  {
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD), "Failed to login");
    
    // Go to account management page
    $this->assertNotIdentical($browser->click('Account Management'), false, "Account management link does not exist");
    
    // TODO (Maybe): Count the number of rows in the tables and compare with COUNT(*) from database    
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
    
    return $browser;
  }
  
  function testViewAdminReports() 
  {
    // Login
    $browser;
    $this->assertTrue(Common::login($browser, DEFAULT_ADMIN, DEFAULT_ADMIN_PASSWORD), "Failed to login");
    
    // Go to account management page
    $this->assertNotIdentical($browser->click('Admin Reports'), false, "Admin reports link does not exist");
    
    // TODO: Should I check the page content    
    
    // Log out
    $this->assertTrue(Common::logout($browser), "Logout failed");
    
    return $browser;
  }
}

@mysql_close();

?>
