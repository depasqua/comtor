{if is_string($content)}
{$content}
{else}
  {if isset($content.error)}<span class="error">{$content.error}</span>{/if}
  {if isset($content.success)}<span class="success">{$content.success}</span>{/if}
{/if}
