{if is_array($doclets) && count($doclets) > 0}
  <h3>Module Usage (modules not executed are not listed)</h3>
  {foreach from=$doclets item="d"}
    {if $d.numRows <> 0}
    	<h6>{$d.docletName}: </h6> selected {$d.numRows} times<br/>
    {/if}
  {/foreach}
{/if}
