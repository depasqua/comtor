{if is_array($doclets) && count($doclets) > 0}
  <h3>Doclet Usage</h3>
  {foreach from=$doclets item="d"}
    <h6>{$d.docletName}: </h6> selected {$d.numRows} times<br/>
  {/foreach}
{/if}
