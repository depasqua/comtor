<h1>System Usage</h1>
<h6>Name: </h6>{$name}<br/>
<h6>E-Mail Address: </h6>{$email}<br/>
<h6>School: </h6>{$school}<br/>

<h3>Account History</h3>
<h6>Account created: </h6>{strtodate format=$dateFormat date=$dateTime}<br/>
<h6>Account Validated: </h6>{if $validatedDT === null}N/A{else}{strtodate format=$dateFormat date=$validatedDT}{/if}<br/>
<h6>Password last changed: </h6>{if $passwordChangeDT === null}N/A{else}{strtodate format=$dateFormat date=$passwordChangeDT}{/if}<br/>
<h6>Last login: </h6>{if $lastLogin === null}N/A{else}{strtodate format=$dateFormat date=$lastLogin}{/if}<br/>

{include file="doclet_usage.tpl"}
