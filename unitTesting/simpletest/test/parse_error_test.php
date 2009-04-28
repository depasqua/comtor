<?php
// $Id: parse_error_test.php,v 1.1 2009-04-28 17:58:30 ssigwart Exp $
require_once('../unit_tester.php');
require_once('../reporter.php');

$test = &new TestSuite('This should fail');
$test->addFile('test_with_parse_error.php');
$test->run(new HtmlReporter());
?>