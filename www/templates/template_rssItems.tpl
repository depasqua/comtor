{if isset($rss) && is_array($rss) }
<h3>{$rssTitle|default:"RSS Feed"}</h3>
  {foreach from=$rss item="item"}
    <div class="rssItem{cycle values=" odd,"}">
      <h4>{$item.title} (<a href={$item.link}>View</a>)</h4>
      {assign var="pubdate" value="$item.pubdate|strtotime}
      {"l F n, Y g:i:sA"|date:$pubdate}
      <p>{$item.description}</p>
    </div>
  {/foreach}
{/if}
