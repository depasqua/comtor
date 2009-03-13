<?php

chdir("..");

require_once("smarty/Smarty.class.php");
require_once("XML/RSS.php");

$tpl = new Smarty();
    
// Get RSS feed from sourceforge
$url = "http://sourceforge.net/export/rss2_keepsake.php?group_id=215309";
$rss = &new XML_RSS($url);
$rss->parse();
$tpl->assign("rss", $rss->getItems());
$tpl->assign("rssTitle", "Sourceforge Activity RSS Feed");

// Display template
$tpl->display("template_rssItems.tpl");

?>
