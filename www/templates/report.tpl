{literal}
<script type='text/javascript'>
<!--
function showFile(id)
{
  var element = document.getElementById(id);
  if (element.style.visibility == "visible")
  {
    element.style.visibility = "hidden";
    element.style.display = "none";
  }
  else
  {
    element.style.visibility = "visible";
    element.style.display = "block";
  }
}

//-->
</script>
{/literal}

<h3>{strtodate format="l F j, Y @ g:i:s A" date=$dateTime}</h3>
<!-- <h4>Score: {$score|string_format:"%.2f"} out of {$max_score|string_format:"%.2f"}</h4> -->

<div id='submittedFiles'>
  <h3>Submitted Files</h3>
  Click a filename to view:<br/>

  {foreach name="files" from=$files item="f"}
    <a href="javascript:showFile('fileContents{$smarty.foreach.files.index}');">{$f.filename}</a><br/>
    <code class="javacode" id="fileContents{$smarty.foreach.files.index}" style="visibility: hidden; display: none;">{$f.contents}</code>
  {/foreach}
</div>

{if isset($compilationError)}

<div class="compilationError">
<h1>Compilation Error</h1>
<pre><code>{$compilationError}</code></pre>
</div>

{else}
  {foreach from=$doclets item="d"}
  {if !empty($d.props) }
    <div class='report'>
      <div class='docletDesc'>{$d.docletName}<br><small>{$d.docletDescription}</small></div>
      <!-- <div class='docletScore'>Score: {$d.score|string_format:"%.2f"} out of {$d.max_score|string_format:"%.2f"}</div> -->
      {foreach from=$d.props item="prop"}
        {if $prop.class == 'class'}<hr />{/if}
        <div class='{$prop.class}'>{$prop.value}</div>
      {/foreach}
    </div>
  {/if}
  {/foreach}
{/if}
