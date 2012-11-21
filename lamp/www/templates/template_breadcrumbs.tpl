<div class="breadcrumbs">
{foreach name="breadcrumbs" from=$breadcrumbs item="crumb"}
  {if !$smarty.foreach.breadcrumbs.last}
    <a href="{$crumb.href}">{$crumb.text}</a> &raquo;
  {else}
    {$crumb.text}
  {/if}
{/foreach}
</div>
