{if is_array($courses) && count($courses) > 0 }
<h3>Select a course</h3>

{foreach from=$courses item="c"}
<a href="?courseId={$c.courseId}">{$c.section}: {$c.name} ({$c.semester})</a>
{/foreach}

</table>
{elseif $resultMsg|default:true}
<h5 class="center">No Results Found</h5>
{/if}
