{literal}
<script type="text/javascript">
	$(function() {
		$("#openCalendar").datepicker();
		$("#closeCalendar").datepicker();
		$("#openTimeEntry").timeEntry({
			spinnerImage: "jQuery/timeentry/spinnerText.png",
			spinnerSize: [30, 20, 8],
			useMouseWheel: true
		});
		$("#closeTimeEntry").timeEntry({
			spinnerImage: "jQuery/timeentry/spinnerText.png",
			spinnerSize: [30, 20, 8],
			useMouseWheel: true
		});
	});
</script>
{/literal}

<form action="" method="post" name="form">
<div>

<label for="name">Assignment Name: <input id="name" name="name" value="{$name}" /></label>
<br/>

<label>Assignment Open Date:</label>
<input type="text" class="openCalendar" id="openCalendar" name="openDate" value="{$openDate}" size="10" />
<input type="text" class="openTimeEntry" id="openTimeEntry" name="openTime" value="{$openTime}" size="7" />
<br/>

<label>Assignment Close Date:</label>
<input type="text" class="closeCalendar" id="closeCalendar" name="closeDate" value="{$closeDate}" size="10" />
<input type="text" class="closeTimeEntry" id="closeTimeEntry" name="closeTime" value="{$closeTime}" size="7" />
<br/>

<h4>Mandatory Report Options (If none are selected, user can choose options)</h4>
<div class="reportOptions doclets">
  {foreach from=$doclets item="d"}
    <div class="doclet {cycle values="odd,even"}">
      <input type="checkbox" class="checkbox" name="doclets[]" value="{$d.docletId}" {if is_array($assignmentOptions) && in_array($d.docletId, $assignmentOptions)}checked="checked"{/if}/>
      <span class="doclet_name">{$d.docletName}:</span>
      <br/>
      <span class="doclet_description">{$d.docletDescription}</span>
    
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
    
    </div>
  {/foreach}
</div>

</div>

<div class="center">
  <input name="securityRand" value="{$rand}" type="hidden" />
  <input name="securityPage" value="{$page_sec}" type="hidden" />
  <input type="submit" value="{if $editing|default:false}Update{else}Add{/if} Assignment" />
</div>
</form>
