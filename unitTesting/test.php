<?php

require_once('simpletest/autorun.php');

// Add comtor web folder to include path
$path = "/home/sigwart4/comtor/web";
set_include_path(get_include_path() . PATH_SEPARATOR . $path);

session_start();
require_once("smarty/Smarty.class.php");

class TestOfDatabaseConnecting extends UnitTestCase 
{
  function testConnection()
  {
    require("connect.php");
    $this->assertNotEqual(mysql_stat(), null, "Failed to connect to database");
  }
}

class TestOfLinksClass extends UnitTestCase 
{
  function testLinkClass()
  {
    $tpl = new Smarty();
    
    require_once("header1.php");
    
    // Heading
    $header = new Link("COMTOR Account", null, true);    
    $this->assertIdentical($header->name, "COMTOR Account", "Header link name incorrect");
    $this->assertIdentical($header->href, null, "Header link href should be null");
    $this->assertIdentical($header->heading, true, "Header link not classified as heading");
    $this->assertTrue(count($header->attrs) == 0, "Header link should not have attributes");
    
    // Normal link
    $link = new Link("Create Account", "test.php");    
    $this->assertIdentical($link->name, "Create Account", "Link name incorrect");
    $this->assertEqual($link->href, "test.php", "Link href incorrect");
    $this->assertIdentical($link->heading, false, "Link incorrectly classified as heading");
    
    // Link with attributes
    $link->addAttr("class", "test");
    $link->addAttr("id", "testId");
    $this->assertEqual($link->attrs[0]->attr, "class", "Link attribute incorrect");
    $this->assertEqual($link->attrs[0]->value, "test", "Link attribute incorrect");
    $this->assertEqual($link->attrs[1]->attr, "id", "Link attribute incorrect");
    $this->assertEqual($link->attrs[1]->value, "testId", "Link attribute incorrect");
  }
}

class TestOfSecurityFunctions extends UnitTestCase 
{
  function testSecurity()
  { 
    require_once("securityFunctions.php");
    
    // Check that security is not passed 
    $this->assertTrue(checkSecurity("abcdefghijklmn", "abcdefghijklmn") != 1, "Security function failed");
    
    // Get page security info
    $arr = securityFormInputs();
    $this->assertTrue(checkSecurity($arr['page_sec'], "abcdefghijklmn") != 1, "Security function failed");
    $this->assertTrue(checkSecurity($arr['page_sec'], $arr['rand']) != 1, "Security function failed");
    
    $arr = securityFormInputs();
    $this->assertTrue(checkSecurity("abcdefghijklmn", $arr['rand']) != 1, "Security function failed");
    $this->assertTrue(checkSecurity($arr['page_sec'], $arr['rand']) == 1, "Security function failed");
    
    // Get page security info
    $arr = securityFormInputs();
    $this->assertTrue(checkSecurity($arr['page_sec'], $arr['rand']) == 1, "Security function failed");
    
    // Get page security info
    $arr1 = securityFormInputs();
    $arr2 = securityFormInputs();
    $arr3 = securityFormInputs();
    $arr4 = securityFormInputs();
    $arr5 = securityFormInputs();
    $this->assertTrue(checkSecurity($arr1['page_sec'], $arr1['rand']) == 1, "Security function failed");
    $this->assertTrue(checkSecurity($arr2['page_sec'], $arr2['rand']) == 1, "Security function failed");
    $this->assertTrue(checkSecurity($arr3['page_sec'], $arr3['rand']) == 1, "Security function failed");
    $this->assertTrue(checkSecurity($arr4['page_sec'], $arr4['rand']) == 1, "Security function failed");
    $this->assertTrue(checkSecurity($arr5['page_sec'], $arr5['rand']) == 1, "Security function failed");
  }
}

class TestOfRunPhp extends UnitTestCase 
{
  function testFileExtension()
  { 
    $filename = "test.jar";
    $this->assertEqual(file_extension($filename), "jar", "File extension incorrect");
    
    $filename = "test.tar.gz";
    $this->assertEqual(file_extension($filename), "gz", "File extension incorrect");
    
    $filename = "README";
    $this->assertEqual(file_extension($filename), "README", "File extension incorrect");
  }
}

// From run.php
function file_extension($filename)
{
  return end(explode(".", $filename));
}

class TestOfReportsPhp extends UnitTestCase 
{
  function testFormatDateTime()
  { 
    $timestamp = "2009/03/02 16:24:34"; 
    $str = formatDateTime($timestamp);
    $this->assertEqual($str, "Monday March 2, 2009 @ 4:24:34 PM", "Date formatted incorrectly");
  }
  
  function testColorize()
  { 
    $str = "// Test\nTest/*ing*/\nHi";
    $str = htmlspecialchars($str);
    $str = str_replace(" ", "&nbsp;", $str);
    $str = nl2br($str);
    $str = colorize($str);
    
    // Expected result
    $testStr = "<span style=\"color: #008000;\">//&nbsp;Test<br /></span>\nTest<span style=\"color: #008000;\">/*ing*/</span><br />\nHi";

    $this->assertEqual($str, $testStr, "Code colorized incorrectly");
  }
}

// Format date from reports.php
function formatDateTime($timestamp)
{
  $year=substr($timestamp,0,4);
  $month=substr($timestamp,5,2);
  $day=substr($timestamp,8,2);
  $hour=substr($timestamp,11,2);
  $minute=substr($timestamp,14,2);
  $second=substr($timestamp,17,2);

  //$date = date("M d, Y @ g:i:s A", mktime($hour, $minute, $second, $month, $day, $year));
  $date = date("l F j, Y @ g:i:s A", mktime($hour, $minute, $second, $month, $day, $year));

  return $date;
}

// Colorize from reports.php
function colorize($str)
{
  // Make comments green
  $pattern = '/(\/\*.*\*\/)/msU';
  $replacement = '<span style="color: #008000;">$1</span>';
  $str = preg_replace($pattern, $replacement, $str);

  // Make comments green
  $pattern = '/(\/\/.*<br ?\/?>)/i';
  $replacement = '<span style="color: #008000;">$1</span>';
  $str = preg_replace($pattern, $replacement, $str);

  return $str;
}

?>
