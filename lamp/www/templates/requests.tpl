<h3>Requests</h3>

{* Add links for different requests *}
{if $req_acctType|default:false}
  <form name="acctTypeForm" method="GET" action="requestChangeAcctType.php">
    <fieldset>
    <legend>Account Type<legend>
    <select name="acctType">
      {foreach from=$acctTypes item="type"}
        <option value="{$type}" {if $type == $acctType}selected="selected"{/if}>{$type|capitalize}</option>
      {/foreach}
    </select>
    <br/>
    <label for="comment">Please provide a short comment or link to a website indicating why your account type should be changed:</label>
    <br/>
    <input type="text" id="comment" name="comment" style="width: 100%;"/>
    <br/>
    <input type="hidden" name="userId" value="{$userId}" />
    <a href="javascript:document.acctTypeForm.submit();">Request Account Type Change</a>
    </fieldset>
  </form>
{/if}
{if $req_acctDelete|default:false}
  <a href="requestRemoveAcct.php">Request Account Removal</a>
{/if}


{* Account Type Change *}
<h4>Account Type Change Requests</h4>
{if is_array($req_acct_type) && count($req_acct_type) > 0 }
<table class="data">
<tr>
  {if $smarty.session.userId != $userId}
    <th>User</th>
  {/if}
  <th>Account Type</th>
  <th>Comment</th>
  <th>Status</th>
  <th class="large">Actions</th>
</tr>

{foreach from=$req_acct_type item="req"}
<tr>
  {if $smarty.session.userId != $userId}
    <td>{$req.name}</td>
  {/if}
  <td>{$req.acct_type}</td>
  <td>{$req.comment}</td>
  <td>{$req.status}</td>
  {* Actions *}
  <td>
    <a href="request_delete.php?type=acctType&amp;req_id={$req.req_id}">Delete Request</a>
    {if $smarty.session.acctType == 'admin' }
      <br/>
      <a href="request_accept.php?type=acctType&amp;req_id={$req.req_id}">Accept Request</a>
      <br/>
      <a href="request_reject.php?type=acctType&amp;req_id={$req.req_id}">Reject Request</a>
    {/if}
  </td>
</tr>
{/foreach}

</table>
{elseif $resultMsg|default:true}
<h5 class="center">No Results Found</h5>
{/if}


{* Account Removal *}
<h4>Account Removal Requests</h4>
{if is_array($req_acct_removal) && count($req_acct_removal) > 0 }
<table class="data">
<tr>
  {if $smarty.session.userId != $userId}
    <th>User</th>
  {/if}
  <th>Status</th>
  <th class="large">Actions</th>
</tr>

{foreach from=$req_acct_removal item="req"}
<tr>
  {if $smarty.session.userId != $userId}
    <td>{$req.name}</td>
  {/if}
  <td>{$req.status}</td>
  {* Actions *}
  <td>
    <a href="request_delete.php?type=acctRemove&amp;userId={$req.userId}">Delete Request</a>
    {if $smarty.session.acctType == 'admin' }
      <br/>
      <a href="request_accept.php?type=acctRemove&amp;userId={$req.userId}">Accept Request</a>
      <br/>
      <a href="request_reject.php?type=acctRemove&amp;userId={$req.userId}">Reject Request</a>
    {/if}
  </td>
</tr>
{/foreach}

</table>
{elseif $resultMsg|default:true}
<h5 class="center">No Results Found</h5>
{/if}
