function getXMLHttp()
{
  xmlHttp = null;
  // Firefox, Opera, IE7, etc.
  if (window.XMLHttpRequest)
  {
    xmlHttp = new XMLHttpRequest();
  }
  // IE6, IE5
  else if (window.ActiveXObject)
  {
    try
    {
      xmlHttp=new ActiveXObject("Msxml2.XMLHTTP");
    }
    catch (e)
    {
      xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
  }
  else
  {
    alert("Your browser does not support XMLHTTP.");
  }
  return xmlHttp;
}
