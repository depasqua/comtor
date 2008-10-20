{literal}
<script type="text/javascript">
<!--
function verify()
{
  var missing = Array();
  if (document.form.name.value == "")
    missing.push("Doclet name");
  if (document.form.desc.value == "")
    missing.push("Description");
  if (document.form.javaName.value == "")
    missing.push("Java class name");

  // Alert if fields are empty and cancel form submit
  if (missing.length > 0)
  {
    var msg = "You are required to complete the following fields: ";

    for (var i = 0; i < missing.length; i++)
      msg += "\n\t" + missing[i];

    alert(msg);
    return false;
  }

  return confirm("Are all fields filled in for each row of \"Grading Sections\" and \"Grading Parameters\"");
}

function rowChanged(id)
{
  // Sections
  var regex = /dgs_([0-9])+/;
  var result = regex.exec(id);
  if (result != null)
  {
    var elem = document.getElementById("dgs_"+result[1]+"_changed");
    if(elem != null && elem.value == "0")
    {
      var row = document.getElementById("sectionRowCopy");
      var table = document.getElementById("sectionTable");

      if (row != null && table != null)
      {
        row.id = null;

        var next = parseInt(result[1]) + 1;
        var html = row.innerHTML.replace(new RegExp("dgs_"+result[1], "gi"), "dgs_"+next);

        var newRow = table.insertRow(table.rows.length);
        newRow.id = "sectionRowCopy";
        newRow.innerHTML = html;
      }

      elem.value = "1";
    }
  }

  // Parameters
  regex = /dgp_([0-9])+/;
  result = regex.exec(id);
  if (result != null)
  {
    var elem = document.getElementById("dgp_"+result[1]+"_changed");
    if(elem != null && elem.value == "0")
    {
      var row = document.getElementById("paramRowCopy");
      var table = document.getElementById("paramTable");

      if (row != null && table != null)
      {
        row.id = null;

        var next = parseInt(result[1]) + 1;
        var html = row.innerHTML.replace(new RegExp("dgp_"+result[1], "gi"), "dgp_"+next);

        var newRow = table.insertRow(table.rows.length);
        newRow.id = "paramRowCopy";
        newRow.innerHTML = html;
      }

      elem.value = "1";
    }
  }
}
//-->
</script>
{/literal}

<form action="" method="post" name="form">
<div>

<label for="name" class="leftFloatSmall">Doclet Name:</label>
<input id="name" name="name" value="{$name}" />

<br class="clearFloat" />

<label for="javaName" class="leftFloatSmall">Java Class:</label>
<input id="javaName" name="javaName" value="{$javaName}" />

<br class="clearFloat" />

<label for="desc" class="leftFloatSmall">Description:</label>
<textarea id="desc" name="desc" cols="50" rows="3" style="width: 75%; height: 3em;">{$desc}</textarea>

<br class="clearFloat" />

{* Grading Sections *}
<div class="docletSubsection">
  <h6>Grading Sections</h6>

  <br/>

  <table id="sectionTable">
    <tr>
      <th>Section Name</th>
      <th>Description</th>
      <th>Default Maximum Grade</th>
    </tr>

    <tr id="sectionRowCopy">
      <td>
        <label for="dgs_1_name" style="display: none;">Section Name</label>
        <input type="text" id="dgs_1_name" name="sectionName[]" onkeydown="rowChanged(this.id);"/>
        <input type="hidden" disabled="disabled" id="dgs_1_changed" name="dgp_1_changed" value="0" />
      </td>

      <td>
        <label for="dgs_1_desc" style="display: none;">Section Description</label>
        <textarea id="dgs_1_desc" name="sectionDesc[]" cols="50" rows="3" style="width: 250px; height: 5em;" onkeydown="rowChanged(this.id);"></textarea>
      </td>

      <td>
        <label for="dgs_1_default" style="display: none;">Default Maximum Grade</label>
        <input type="text" id="dgs_1_default" name="sectionDefault[]" onkeydown="rowChanged(this.id);"/>
      </td>
    </tr>
  </table>
</div>


{* Grading Parameters *}
<div class="docletSubsection">
  <h6>Grading Parameters</h6>

  <br/>

  <table id="paramTable">
    <tr>
      <th>Parameter Name</th>
      <th>Description</th>
      <th>Default Value</th>
    </tr>

    <tr id="paramRowCopy">
      <td>
        <label for="dgp_1_paramName" style="display: none;">Parameter Name</label>
        <input type="text" id="dgp_1_paramName" name="paramName[]" onkeydown="rowChanged(this.id);" />
        <input type="hidden" disabled="disabled" id="dgp_1_changed" name="dgp_1_changed" value="0" />
      </td>

      <td>
        <label for="dgp_1_paramDesc" style="display: none;">Parameter Description</label>
        <textarea id="dgp_1_paramDesc" name="paramDesc[]" cols="50" rows="3" style="width: 250px; height: 5em;" onkeydown="rowChanged(this.id);"></textarea>
      </td>

      <td>
        <label for="dgp_1_paramValue" style="display: none;">Default Value</label>
        <input type="text" id="dgp_1_paramValue" name="paramValue[]" onkeydown="rowChanged(this.id);" />
      </td>
    </tr>
  </table>
</div>

</div>
<div class="center">
<input name="securityRand" value="{$rand}" type="hidden" />
<input name="securityPage" value="{$page_sec}" type="hidden" />
<input type="submit" value="{if $editing|default:false}Update{else}Add{/if} Doclet" onclick="return verify();" />
</div>
</form>
