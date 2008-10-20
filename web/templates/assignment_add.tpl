<form action="" method="post" name="form">
<div>

<label for="name">Assignment Name: <input id="name" name="name" value="{$name}" /></label>

<br/>

<label>Assignment Open Date:</label>
{html_select_date time=$openTime|default:$smarty.now prefix='Open_' start_year=2000 end_year=2035}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
{html_select_time time=$openTime|default:$smarty.now prefix='Open_' use_24_hours=false minute_interval=5 display_seconds=false display_meridian=true}

<br/>

<label>Assignment Close Date:</label>
{html_select_date time=$closeTime|default:$smarty.now prefix='Close_' start_year=2000 end_year=2035}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
{html_select_time time=$closeTime|default:$smarty.now prefix='Close_' use_24_hours=false minute_interval=5 display_seconds=false display_meridian=true}

<br/>

<div class="reportOptions">
  <h4>Mandatory Report Options (If none are selected, user can choose options)</h4>
  {foreach from=$doclets item="d"}
    <div class="doclet">
      <input type="checkbox" class="checkbox" name="doclets[]" value="{$d.docletId}" {if is_array($assignmentOptions) && in_array($d.docletId, $assignmentOptions)}checked="checked"{/if}/>
      <span class="doclet_name">{$d.docletName}:</span>
      <br/>
      <span class="doclet_description">{$d.docletDescription}</span>
    </div>

    {* Grading Sections *}
    {if is_array($d.gradeSections) && count($d.gradeSections) > 0 }
      <div class="docletSubsection">
        <h6>Grading Breakdown</h6>
        {foreach from=$d.gradeSections item="gs"}
        <input type="text" class="fewChars" {if !($gradeInputsEnabled|default:true)}disabled="disabled"{/if} name="dgs[{$gs.docletGradeSectionId}]" value="{gradeSectionInputValue docletGradeSectionId=$gs.docletGradeSectionId}" />
        <input type="hidden" name="dgs_prev[{$gs.docletGradeSectionId}]" value="{gradeSectionInputValue docletGradeSectionId=$gs.docletGradeSectionId}" />
        <span style="font-weight: bold;">{$gs.sectionName}</span>: {$gs.sectionDesc}<br/>
        {/foreach}
      </div>
    {/if}

    {* Grading Parameters *}
    {if is_array($d.gradeParams) && count($d.gradeParams) > 0 }
      <div class="docletSubsection">
        <h6>Grading Parameters</h6>
        {foreach from=$d.gradeParams item="gp"}
        <input type="text" {if !($gradeInputsEnabled|default:true)}disabled="disabled"{/if} name="dgp[{$gp.docletGradeParameterId}]" value="{gradeParameterInputValue docletGradeParameterId=$gp.docletGradeParameterId}" />
        <input type="hidden" name="dgp_prev[{$gp.docletGradeParameterId}]" value="{gradeParameterInputValue docletGradeParameterId=$gp.docletGradeParameterId}" />
        <span style="font-weight: bold;">{$gp.parameterName}</span>: {$gp.parameterDesc}<br/>
        {/foreach}
      </div>
    {/if}
  {/foreach}
</div>

</div>

<div class="center">
  <input name="securityRand" value="{$rand}" type="hidden" />
  <input name="securityPage" value="{$page_sec}" type="hidden" />
  <input type="submit" value="{if $editing|default:false}Update{else}Add{/if} Assignment" />
</div>
</form>
