<div class='center'>Welcome to COMTOR.  Please use the links to the left to navigate the different pages.</div>

{if isset($requests) && is_array($requests) }
<h3>Requests</h3>
  {foreach from=$requests item="req"}
    {if $req.num > 0}
      {$req.name}: <span class="requestsPending">{$req.num}</span><br/>
    {else}
      {$req.name}: {$req.num}<br/>
    {/if}
  {/foreach}
{/if}

<h3>Courses</h3>
{if is_array($courses) && count($courses) > 0 }
  {foreach from=$courses item="c"}
    <h4><a href="{$url|default:'courses.php'}?courseId={$c.courseId}">{$c.section}: {$c.name} ({$c.semester})</a></h4>
  {/foreach}
{elseif $resultMsg|default:true}
  <h5 class="center">No Results Found</h5>
{/if}

{if $smarty.session.acctType == "admin"}
{literal}
<script type="text/javascript" src="scripts/XMLhttp.js"></script>
<script type="text/javascript">
<!--

// Load RSS feeds
window.onload = function()
{
  var xmlHttp1 = getXMLHttp();
  var xmlHttp2 = getXMLHttp();
  
  // Set up response handles
  xmlHttp1.onreadystatechange=function()
  {
    if(xmlHttp1.readyState == 4)
    {
      var rss1 = document.getElementById("rss1");
      if (rss1 != null)
        rss1.innerHTML = xmlHttp1.responseText;
    }
  }
  
  // Set up response handles
  xmlHttp2.onreadystatechange=function()
  {
    if(xmlHttp2.readyState == 4)
    {
      var rss2 = document.getElementById("rss2");
      if (rss2 != null)
        rss2.innerHTML = xmlHttp2.responseText;
    }
  }
  
  // Send requests  
  xmlHttp1.open("GET", "ajax/sourceforgeActivityRss.php", true);
  xmlHttp1.send(null);
  xmlHttp2.open("GET", "ajax/sourceforgeSummaryRss.php", true);
  xmlHttp2.send(null);
}

//-->
</script>
{/literal}

<div id="rss1"><img src="img/loading.gif"/></div>
<div id="rss2"><img src="img/loading.gif"/></div>

{/if}

{include file="template_rssItems.tpl" rss=$sourceforgeActivityRss rssTitle="Sourceforge Activity RSS Feed"}

{include file="template_rssItems.tpl" rss=$sourceforgeSummaryRss rssTitle="Sourceforge Summary RSS Feed"}
