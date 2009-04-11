{literal}
<script type="text/javascript">
<!--

function validateAssignment(form)
{
  // Check that assignment name is not null
  if (form.name == null || form.name.value == "")
  {
    alert("Assignment name is requiured.");
    return false;
  }
  // Check that start date is before end date
  else
  {
    // Check that all fields exist first
    if (form.Open_Month == null || form.Open_Day == null || form.Open_Year == null ||
        form.Open_Hour == null || form.Open_Minute == null || form.Open_Meridian == null ||
        form.Close_Month == null || form.Close_Day == null || form.Close_Year == null ||
        form.Close_Hour == null || form.Close_Minute == null || form.Close_Meridian == null)
    {
      alert("Open and close times are required.");
      return false;
    }
    else
    {
      // Convert 12-hour to 24-hour
      var startHour = parseInt(form.Open_Hour.value, 10);
      if (form.Open_Meridian.value == "am")
        startHour = 0;
      else if (form.Open_Meridian.value == "pm" && startHour < 12)
        startHour += 12;
      var endHour = parseInt(form.Close_Hour.value, 10);
      if (form.Close_Meridian.value == "am")
        endHour = 0;
      else if (form.Close_Meridian.value == "pm" && endHour < 12)
        endHour += 12;
      
      // Construct dates
      var startDt = Date.UTC(parseInt(form.Open_Year.value, 10), parseInt(form.Open_Month.value, 10)-1, parseInt(form.Open_Day.value, 10), startHour, parseInt(form.Open_Minute.value, 10))
      var endDt = Date.UTC(parseInt(form.Close_Year.value, 10), parseInt(form.Close_Month.value, 10)-1, parseInt(form.Close_Day.value, 10), endHour, parseInt(form.Close_Minute.value, 10))  
      
      // Compare dates
      if (endDt <= startDt)
      {
        alert("Open time must be before close time.");
        return false;
      }
      
    }
        
  }
  return true;
}

//-->
</script>
{/literal}

<form action="" method="post" name="form" onsubmit="return validateAssignment(this);">
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
