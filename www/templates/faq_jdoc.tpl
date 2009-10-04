<h2>Javadoc Tutorial</h2>

<p>
Javadoc comments can only be used in Java source code.
</p>

<p>
Javadoc comments are similar to regular comments except they begin with the /** symbol, and end with the */ symbol. They also use the following tags.
</p>

<h4>Uses for Javadoc:</h4>
<p>
Javadoc comments allow programmers to easily generate an API (application programmer interface). By writing java code with these comments you can then run the javadoc (ex: javadoc HelloWorld.java) command to generate documentation in HTML format for any public classes and public and protected methods and variables. Any standard HTML code can also be used inside a javadoc comment.
</p>

<h4>Javadoc Tags which COMTOR Utilizes:</h4>
<span class="tag">@param</span> - Indicates the name of a formal parameter followed by a short description. If there are multiple parameters, <span class="tag">@param</span> tags should be defined in the exact same order as the parameters are declared in the method declaration.<br/>
<span class="tag">@return</span> - Indicates the return type of a method. Do not use for void methods.<br/>
<span class="tag">@throws</span> -	Indicates the exceptions the method throws.<br/>

<h4>Other Important Javadoc Tags:</h4>
<span class="tag">@author</span> - Indicates the name of the author of the source code.<br/>
<span class="tag">@deprecated</span> - Indicates that the item is a member of the depreciated API.<br/>
<span class="tag">@see</span> -	Used to refer to related classes.<br/>
<span class="tag">@version</span> - Indicates the version of your source code.<br/>
<span class="tag">@since</span> -	Also can be used to indicate the version of the source code.<br/>

<p>
You can also use the &lt;code&gt; tag for all Java keywords, names and code samples, like this:<br/>
&lt;code&gt;aMethod()&lt;/code&gt;
</p>

<h4>Sample Code with Javadoc Tags:</h4>
{literal}
<code>
<pre>
/**
 * Sample.java – a class that demonstrates the use of javadoc
 * comments.
 * @author Ruth Dannenfelser
 * @version 2.0
 * @see Sample2
 */
 public class Sample extends Sample2 {

  public String words;

  /**
   * Retrieve the value of words.
   * @return String data type.
   */
   public String getWords()
   {
      return words;
   }

  /**
   * Set the value of words.
   * @param someWords A variable of type String.
   */
   public void setWords(String someWords)
   {
      words = newWords;
   }
}
</pre>
</code>
{/literal}

<h4>For More Info on Javadoc visit:</h4>
<div style="font-style: italic;">
Sun’s Website: <a href="http://java.sun.com/j2se/javadoc/writingdoccomments/">http://java.sun.com/j2se/javadoc/writingdoccomments/</a><br/>
Sourceforge Java Workshop: <a href="http://javaworkshop.sourceforge.net/chapter4.html#Javadoc+Comments">http://javaworkshop.sourceforge.net/chapter4.html#Javadoc+Comments</a><br/>
</div>
