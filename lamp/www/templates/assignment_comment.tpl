<form action="" method="post" name="form">
<div>

<label for="comment">Comment:</label>

<br/>

<textarea cols="30" rows="7" id="comment" name="comment">{$comment}</textarea>

<br/>

<input name="loc" value="{$smarty.get.loc|htmlentities}" type="hidden" />

<input name="securityRand" value="{$rand}" type="hidden" />
<input name="securityPage" value="{$page_sec}" type="hidden" />
<input type="submit" value="Set Comment" />
</div>
</form>
