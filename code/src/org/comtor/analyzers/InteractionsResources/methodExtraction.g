lexer grammar methodExtraction;
  options {filter=true;}

  @header {package org.comtor.analyzers.InteractionsResources;}

  @members
  {
    public int numMeths = 0;
    public int numClasses = 0;
    public ArrayList classes = new ArrayList();
    public ArrayList classesPerFile = new ArrayList();
    public ArrayList callsPerMethod = new ArrayList();    // arraylist of arraylists
    public ArrayList methodNames = new ArrayList();
    public ArrayList temp = new ArrayList();

  }
  IMPORT
    :  'import' WS name=QIDStar WS? ';'
    ;
  CLASS
    :  'class' WS name=ID WS? TEMPLATE? ('extends' WS QID WS? TEMPLATE?)?
      ('implements' WS QID WS? TEMPLATE? (',' WS? QID WS? TEMPLATE?)*)? '{'
	{
		if(numClasses > 0)
		{
			classesPerFile.add(callsPerMethod);
			callsPerMethod = new ArrayList();
		}
		numClasses++;
		classes.add($name.text);
	}
    ;

  METHOD
   : TYPE WS name=ID WS? '(' WS? ( ARG WS? (',' WS? ARG WS?)* )? ')' WS?
     ('throws' WS QID WS? (',' WS? QID WS?)*)? '{'
    {
	//System.out.println(" * " + $name.text);
	if(numMeths > 0)
	{
		callsPerMethod.add(temp);
		temp = new ArrayList();
	}
        numMeths++;
        methodNames.add($name.text);
     };
  CALL
   : name=QID WS? '('
   {
	if($name.text.equals("for") || $name.text.equals("if") || $name.text.equals("else") || $name.text.equals("while")|| $name.text.equals("catch") );
		// do nothing
	else
	{
		//System.out.println("   - " + $name.text);
		if(numMeths> 0)temp.add($name.text);
	}
     };
  COMMENT
   : '/*' (options {greedy=false;} : . )* '*/'
   ;
  SL_COMMENT
   : '//' (options {greedy=false;} : . )* '\n'
   ;
  STRING
    :  '"' (options {greedy=false;}: ESC | .)* '"'
    ;
  CHAR
    :  '\'' (options {greedy=false;}: ESC | .)* '\''
    ;
  WS : (' '|'\t'|'\n'|'\r\n')+
   ;
  fragment
  QID :  ID ('.' ID)*
    ;
  fragment
  QIDStar
    :  ID ('.' ID)* '.*'?
    ;
  fragment
  TYPE: QID '[]'?
   ;
  fragment
  ARG : TYPE WS ID
   ;
  fragment
  ID : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*
   ;
  fragment
  ESC  :  '\\' ('"'|'\''|'\\')
   ;
  fragment
  TEMPLATE : '<' WS? QID WS? '>' WS?;
