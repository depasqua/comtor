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
