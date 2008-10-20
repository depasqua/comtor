{literal}
<script type="text/javascript" src="scripts/XMLhttp.js"></script>
<script type="text/javascript">
<!--

function verify()
{
  if (document.form.assignmentId.value == "")
  {
    alert("You must select an assignment.");
    return false;
  }

  return true;
}

function getReportingOptions(assignmentId)
{
  // Check that assignmentId is a number
  if (assignmentId != "" && !isNaN(assignmentId))
  {
    // Get XML HTTP object
    var xmlHttp = getXMLHttp();

    if (xmlHttp == null)
    {}
    else
    {
      xmlHttp.onreadystatechange = function()
      {
        if(xmlHttp.readyState == 4 && xmlHttp.responseXML != null)
        {
          // Parse returned XML
          var xmlDoc = xmlHttp.responseXML.documentElement;
          var doclets = xmlDoc.getElementsByTagName("doclet");

          // Uncheck all checkboxes
          if (doclets.length > 0)
          {
            {/literal}
            {foreach from=$doclets item="d"}
            elem = document.getElementById("doclet_{$d.docletId}");
            {literal}
            if (elem != null)
            {
              elem.checked = false;
              elem.disabled = (doclets.length > 0) ? true : false;
            }
            {/literal}
            {/foreach}
            {literal}
          }
          else
          {
            {/literal}
            {foreach from=$doclets item="d"}
            elem = document.getElementById("doclet_{$d.docletId}");
            if (elem != null)
              elem.disabled = (doclets.length > 0) ? true : false;
            {/foreach}
            {literal}
          }

          for (i = 0; i < doclets.length; i++)
          {
            // Get doclet id
            var docletId = doclets[i].childNodes[0].nodeValue;

            elem = document.getElementById("doclet_"+docletId);
            if (elem != null)
            {
              elem.checked = true;
              elem.disabled = true;
            }
          }
        }
      }

      // Make request asynchronously
      xmlHttp.open("GET","assignment_options.php?assignment_id="+assignmentId,true);
      xmlHttp.send(null);
    }
  }
}

//-->
</script>
{/literal}

<form action="run.php" method="post" enctype="multipart/form-data" name="form">
<div class='center'>
  <div class='gapBelowSmall'>
    <input type="file" name="file" size="30"/>
    <select name="sourceType">
      <option>Java Source Jar</option>
      <option disabled="disabled">C Source</option>
      <option disabled="disabled">Bash Source</option>
      <option disabled="disabled">Pascal Source</option>
    </select>
  </div>

  {*
  {if is_array($courses) && count($courses) > 0}
  <div class='gapBelowSmall'>
    Submit for course:
    <select name='courseId'>
      <option value=""></option>
      {foreach from=$courses item="c"}
        <option value="{$c.courseId}">{$c.semester}: {$c.section}</option>
      {/foreach}
    </select>
  </div>
  {/if}
  *}

  {if is_array($assignments) && count($assignments) > 0}
  {assign var="format" value="n/j/Y g:i A"}
  <div class="note">
    After selecting an assignment, checkboxes may be disabled indicating required doclets.
  </div>
  <div class='gapBelowSmall'>
    Assignment:
    <select name='assignmentId' onchange="getReportingOptions(this.value);">
      <option value=""></option>
      {foreach from=$assignments item="a"}
        <option value="{$a.assignmentId}">
        {$a.name}
        ({strtodate format=$format date=$a.openTime} - {strtodate format=$format date=$a.closeTime})
        </option>
      {/foreach}
    </select>
  </div>
  {/if}

  <div class="doclets">
    {foreach from=$doclets item="d"}
      <div class="doclet">
        <input type="checkbox" class="checkbox" id="doclet_{$d.docletId}" name="doclet[]" value="{$d.javaName}"/>
        <span class="doclet_name">{$d.docletName}</span>
        <br/>
        <span class="doclet_description">{$d.docletDescription}</span>
      </div>
    {/foreach}
  </div>

  <input type="submit" name="submit" value="Analyze Comments" onclick="return verify();"/>
  <input type="Reset" value="Reset"/>
</div>
</form>
