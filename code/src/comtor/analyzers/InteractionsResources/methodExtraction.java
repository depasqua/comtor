// $ANTLR 3.1.1 src/comtor/analyzers/InteractionsResources/methodExtraction.g 2008-11-05 17:02:53
package comtor.analyzers.InteractionsResources;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class methodExtraction extends Lexer {
    public static final int CLASS=10;
    public static final int ESC=17;
    public static final int CHAR=19;
    public static final int ID=7;
    public static final int EOF=-1;
    public static final int QID=9;
    public static final int TYPE=11;
    public static final int IMPORT=6;
    public static final int WS=4;
    public static final int ARG=12;
    public static final int TEMPLATE=8;
    public static final int QIDStar=5;
    public static final int SL_COMMENT=16;
    public static final int CALL=14;
    public static final int COMMENT=15;
    public static final int METHOD=13;
    public static final int STRING=18;

        public int numMeths = 0;
        public int numClasses = 0;
        public ArrayList classes = new ArrayList();
        public ArrayList classesPerFile = new ArrayList();
        public ArrayList callsPerMethod = new ArrayList();    // arraylist of arraylists
        public ArrayList methodNames = new ArrayList();
        public ArrayList temp = new ArrayList();

      

    // delegates
    // delegators

    public methodExtraction() {;} 
    public methodExtraction(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public methodExtraction(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "src/comtor/analyzers/InteractionsResources/methodExtraction.g"; }

    public Token nextToken() {
        while (true) {
            if ( input.LA(1)==CharStream.EOF ) {
                return Token.EOF_TOKEN;
            }
            state.token = null;
    	state.channel = Token.DEFAULT_CHANNEL;
            state.tokenStartCharIndex = input.index();
            state.tokenStartCharPositionInLine = input.getCharPositionInLine();
            state.tokenStartLine = input.getLine();
    	state.text = null;
            try {
                int m = input.mark();
                state.backtracking=1; 
                state.failed=false;
                mTokens();
                state.backtracking=0;

                if ( state.failed ) {
                    input.rewind(m);
                    input.consume(); 
                }
                else {
                    emit();
                    return state.token;
                }
            }
            catch (RecognitionException re) {
                // shouldn't happen in backtracking mode, but...
                reportError(re);
                recover(re);
            }
        }
    }

    public void memoize(IntStream input,
    		int ruleIndex,
    		int ruleStartIndex)
    {
    if ( state.backtracking>1 ) super.memoize(input, ruleIndex, ruleStartIndex);
    }

    public boolean alreadyParsedRule(IntStream input, int ruleIndex) {
    if ( state.backtracking>1 ) return super.alreadyParsedRule(input, ruleIndex);
    return false;
    }// $ANTLR start "IMPORT"
    public final void mIMPORT() throws RecognitionException {
        try {
            int _type = IMPORT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            Token name=null;

            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:18:5: ( 'import' WS name= QIDStar ( WS )? ';' )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:18:8: 'import' WS name= QIDStar ( WS )? ';'
            {
            match("import"); if (state.failed) return ;

            mWS(); if (state.failed) return ;
            int nameStart51 = getCharIndex();
            mQIDStar(); if (state.failed) return ;
            name = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, nameStart51, getCharIndex()-1);
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:18:33: ( WS )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( ((LA1_0>='\t' && LA1_0<='\n')||LA1_0=='\r'||LA1_0==' ') ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:18:33: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match(';'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IMPORT"

    // $ANTLR start "CLASS"
    public final void mCLASS() throws RecognitionException {
        try {
            int _type = CLASS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            Token name=null;

            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:21:5: ( 'class' WS name= ID ( WS )? ( TEMPLATE )? ( 'extends' WS QID ( WS )? ( TEMPLATE )? )? ( 'implements' WS QID ( WS )? ( TEMPLATE )? ( ',' ( WS )? QID ( WS )? ( TEMPLATE )? )* )? '{' )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:21:8: 'class' WS name= ID ( WS )? ( TEMPLATE )? ( 'extends' WS QID ( WS )? ( TEMPLATE )? )? ( 'implements' WS QID ( WS )? ( TEMPLATE )? ( ',' ( WS )? QID ( WS )? ( TEMPLATE )? )* )? '{'
            {
            match("class"); if (state.failed) return ;

            mWS(); if (state.failed) return ;
            int nameStart81 = getCharIndex();
            mID(); if (state.failed) return ;
            name = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, nameStart81, getCharIndex()-1);
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:21:27: ( WS )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( ((LA2_0>='\t' && LA2_0<='\n')||LA2_0=='\r'||LA2_0==' ') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:21:27: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:21:31: ( TEMPLATE )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='<') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:21:31: TEMPLATE
                    {
                    mTEMPLATE(); if (state.failed) return ;

                    }
                    break;

            }

            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:21:41: ( 'extends' WS QID ( WS )? ( TEMPLATE )? )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='e') ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:21:42: 'extends' WS QID ( WS )? ( TEMPLATE )?
                    {
                    match("extends"); if (state.failed) return ;

                    mWS(); if (state.failed) return ;
                    mQID(); if (state.failed) return ;
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:21:59: ( WS )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( ((LA4_0>='\t' && LA4_0<='\n')||LA4_0=='\r'||LA4_0==' ') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:21:59: WS
                            {
                            mWS(); if (state.failed) return ;

                            }
                            break;

                    }

                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:21:63: ( TEMPLATE )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0=='<') ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:21:63: TEMPLATE
                            {
                            mTEMPLATE(); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:7: ( 'implements' WS QID ( WS )? ( TEMPLATE )? ( ',' ( WS )? QID ( WS )? ( TEMPLATE )? )* )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0=='i') ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:8: 'implements' WS QID ( WS )? ( TEMPLATE )? ( ',' ( WS )? QID ( WS )? ( TEMPLATE )? )*
                    {
                    match("implements"); if (state.failed) return ;

                    mWS(); if (state.failed) return ;
                    mQID(); if (state.failed) return ;
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:28: ( WS )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( ((LA7_0>='\t' && LA7_0<='\n')||LA7_0=='\r'||LA7_0==' ') ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:28: WS
                            {
                            mWS(); if (state.failed) return ;

                            }
                            break;

                    }

                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:32: ( TEMPLATE )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0=='<') ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:32: TEMPLATE
                            {
                            mTEMPLATE(); if (state.failed) return ;

                            }
                            break;

                    }

                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:42: ( ',' ( WS )? QID ( WS )? ( TEMPLATE )? )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==',') ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:43: ',' ( WS )? QID ( WS )? ( TEMPLATE )?
                    	    {
                    	    match(','); if (state.failed) return ;
                    	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:47: ( WS )?
                    	    int alt9=2;
                    	    int LA9_0 = input.LA(1);

                    	    if ( ((LA9_0>='\t' && LA9_0<='\n')||LA9_0=='\r'||LA9_0==' ') ) {
                    	        alt9=1;
                    	    }
                    	    switch (alt9) {
                    	        case 1 :
                    	            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:47: WS
                    	            {
                    	            mWS(); if (state.failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    mQID(); if (state.failed) return ;
                    	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:55: ( WS )?
                    	    int alt10=2;
                    	    int LA10_0 = input.LA(1);

                    	    if ( ((LA10_0>='\t' && LA10_0<='\n')||LA10_0=='\r'||LA10_0==' ') ) {
                    	        alt10=1;
                    	    }
                    	    switch (alt10) {
                    	        case 1 :
                    	            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:55: WS
                    	            {
                    	            mWS(); if (state.failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:59: ( TEMPLATE )?
                    	    int alt11=2;
                    	    int LA11_0 = input.LA(1);

                    	    if ( (LA11_0=='<') ) {
                    	        alt11=1;
                    	    }
                    	    switch (alt11) {
                    	        case 1 :
                    	            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:22:59: TEMPLATE
                    	            {
                    	            mTEMPLATE(); if (state.failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);


                    }
                    break;

            }

            match('{'); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		if(numClasses > 0)
              		{
              			classesPerFile.add(callsPerMethod);
              			callsPerMethod = new ArrayList();
              		}
              		numClasses++;
              		classes.add((name!=null?name.getText():null));
              	
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLASS"

    // $ANTLR start "METHOD"
    public final void mMETHOD() throws RecognitionException {
        try {
            int _type = METHOD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            Token name=null;

            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:4: ( TYPE WS name= ID ( WS )? '(' ( WS )? ( ARG ( WS )? ( ',' ( WS )? ARG ( WS )? )* )? ')' ( WS )? ( 'throws' WS QID ( WS )? ( ',' ( WS )? QID ( WS )? )* )? '{' )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:6: TYPE WS name= ID ( WS )? '(' ( WS )? ( ARG ( WS )? ( ',' ( WS )? ARG ( WS )? )* )? ')' ( WS )? ( 'throws' WS QID ( WS )? ( ',' ( WS )? QID ( WS )? )* )? '{'
            {
            mTYPE(); if (state.failed) return ;
            mWS(); if (state.failed) return ;
            int nameStart168 = getCharIndex();
            mID(); if (state.failed) return ;
            name = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, nameStart168, getCharIndex()-1);
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:22: ( WS )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( ((LA14_0>='\t' && LA14_0<='\n')||LA14_0=='\r'||LA14_0==' ') ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:22: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('('); if (state.failed) return ;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:30: ( WS )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( ((LA15_0>='\t' && LA15_0<='\n')||LA15_0=='\r'||LA15_0==' ') ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:30: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:34: ( ARG ( WS )? ( ',' ( WS )? ARG ( WS )? )* )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( ((LA20_0>='A' && LA20_0<='Z')||LA20_0=='_'||(LA20_0>='a' && LA20_0<='z')) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:36: ARG ( WS )? ( ',' ( WS )? ARG ( WS )? )*
                    {
                    mARG(); if (state.failed) return ;
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:40: ( WS )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( ((LA16_0>='\t' && LA16_0<='\n')||LA16_0=='\r'||LA16_0==' ') ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:40: WS
                            {
                            mWS(); if (state.failed) return ;

                            }
                            break;

                    }

                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:44: ( ',' ( WS )? ARG ( WS )? )*
                    loop19:
                    do {
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( (LA19_0==',') ) {
                            alt19=1;
                        }


                        switch (alt19) {
                    	case 1 :
                    	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:45: ',' ( WS )? ARG ( WS )?
                    	    {
                    	    match(','); if (state.failed) return ;
                    	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:49: ( WS )?
                    	    int alt17=2;
                    	    int LA17_0 = input.LA(1);

                    	    if ( ((LA17_0>='\t' && LA17_0<='\n')||LA17_0=='\r'||LA17_0==' ') ) {
                    	        alt17=1;
                    	    }
                    	    switch (alt17) {
                    	        case 1 :
                    	            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:49: WS
                    	            {
                    	            mWS(); if (state.failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    mARG(); if (state.failed) return ;
                    	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:57: ( WS )?
                    	    int alt18=2;
                    	    int LA18_0 = input.LA(1);

                    	    if ( ((LA18_0>='\t' && LA18_0<='\n')||LA18_0=='\r'||LA18_0==' ') ) {
                    	        alt18=1;
                    	    }
                    	    switch (alt18) {
                    	        case 1 :
                    	            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:57: WS
                    	            {
                    	            mWS(); if (state.failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop19;
                        }
                    } while (true);


                    }
                    break;

            }

            match(')'); if (state.failed) return ;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:70: ( WS )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( ((LA21_0>='\t' && LA21_0<='\n')||LA21_0=='\r'||LA21_0==' ') ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:35:70: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:36:6: ( 'throws' WS QID ( WS )? ( ',' ( WS )? QID ( WS )? )* )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0=='t') ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:36:7: 'throws' WS QID ( WS )? ( ',' ( WS )? QID ( WS )? )*
                    {
                    match("throws"); if (state.failed) return ;

                    mWS(); if (state.failed) return ;
                    mQID(); if (state.failed) return ;
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:36:23: ( WS )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( ((LA22_0>='\t' && LA22_0<='\n')||LA22_0=='\r'||LA22_0==' ') ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:36:23: WS
                            {
                            mWS(); if (state.failed) return ;

                            }
                            break;

                    }

                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:36:27: ( ',' ( WS )? QID ( WS )? )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( (LA25_0==',') ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:36:28: ',' ( WS )? QID ( WS )?
                    	    {
                    	    match(','); if (state.failed) return ;
                    	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:36:32: ( WS )?
                    	    int alt23=2;
                    	    int LA23_0 = input.LA(1);

                    	    if ( ((LA23_0>='\t' && LA23_0<='\n')||LA23_0=='\r'||LA23_0==' ') ) {
                    	        alt23=1;
                    	    }
                    	    switch (alt23) {
                    	        case 1 :
                    	            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:36:32: WS
                    	            {
                    	            mWS(); if (state.failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    mQID(); if (state.failed) return ;
                    	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:36:40: ( WS )?
                    	    int alt24=2;
                    	    int LA24_0 = input.LA(1);

                    	    if ( ((LA24_0>='\t' && LA24_0<='\n')||LA24_0=='\r'||LA24_0==' ') ) {
                    	        alt24=1;
                    	    }
                    	    switch (alt24) {
                    	        case 1 :
                    	            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:36:40: WS
                    	            {
                    	            mWS(); if (state.failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop25;
                        }
                    } while (true);


                    }
                    break;

            }

            match('{'); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              	//System.out.println(" * " + (name!=null?name.getText():null));
              	if(numMeths > 0)
              	{
              		callsPerMethod.add(temp);
              		temp = new ArrayList();
              	}
                      numMeths++;
                      methodNames.add((name!=null?name.getText():null));
                   
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "METHOD"

    // $ANTLR start "CALL"
    public final void mCALL() throws RecognitionException {
        try {
            int _type = CALL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            Token name=null;

            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:48:4: (name= QID ( WS )? '(' )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:48:6: name= QID ( WS )? '('
            {
            int nameStart256 = getCharIndex();
            mQID(); if (state.failed) return ;
            name = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, nameStart256, getCharIndex()-1);
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:48:15: ( WS )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( ((LA27_0>='\t' && LA27_0<='\n')||LA27_0=='\r'||LA27_0==' ') ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:48:15: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('('); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              	if((name!=null?name.getText():null).equals("for") || (name!=null?name.getText():null).equals("if") || (name!=null?name.getText():null).equals("else") || (name!=null?name.getText():null).equals("while")|| (name!=null?name.getText():null).equals("catch") );
              		// do nothing
              	else
              	{
              		//System.out.println("   - " + (name!=null?name.getText():null));
              		if(numMeths> 0)temp.add((name!=null?name.getText():null));
              	}
                   
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CALL"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:59:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:59:6: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (state.failed) return ;

            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:59:11: ( options {greedy=false; } : . )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0=='*') ) {
                    int LA28_1 = input.LA(2);

                    if ( (LA28_1=='/') ) {
                        alt28=2;
                    }
                    else if ( ((LA28_1>='\u0000' && LA28_1<='.')||(LA28_1>='0' && LA28_1<='\uFFFF')) ) {
                        alt28=1;
                    }


                }
                else if ( ((LA28_0>='\u0000' && LA28_0<=')')||(LA28_0>='+' && LA28_0<='\uFFFF')) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:59:38: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

            match("*/"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "SL_COMMENT"
    public final void mSL_COMMENT() throws RecognitionException {
        try {
            int _type = SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:62:4: ( '//' ( options {greedy=false; } : . )* '\\n' )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:62:6: '//' ( options {greedy=false; } : . )* '\\n'
            {
            match("//"); if (state.failed) return ;

            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:62:11: ( options {greedy=false; } : . )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0=='\n') ) {
                    alt29=2;
                }
                else if ( ((LA29_0>='\u0000' && LA29_0<='\t')||(LA29_0>='\u000B' && LA29_0<='\uFFFF')) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:62:38: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);

            match('\n'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SL_COMMENT"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:65:5: ( '\"' ( options {greedy=false; } : ESC | . )* '\"' )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:65:8: '\"' ( options {greedy=false; } : ESC | . )* '\"'
            {
            match('\"'); if (state.failed) return ;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:65:12: ( options {greedy=false; } : ESC | . )*
            loop30:
            do {
                int alt30=3;
                int LA30_0 = input.LA(1);

                if ( (LA30_0=='\"') ) {
                    alt30=3;
                }
                else if ( (LA30_0=='\\') ) {
                    int LA30_2 = input.LA(2);

                    if ( (LA30_2=='\"') ) {
                        alt30=1;
                    }
                    else if ( (LA30_2=='\\') ) {
                        alt30=1;
                    }
                    else if ( (LA30_2=='\'') ) {
                        alt30=1;
                    }
                    else if ( ((LA30_2>='\u0000' && LA30_2<='!')||(LA30_2>='#' && LA30_2<='&')||(LA30_2>='(' && LA30_2<='[')||(LA30_2>=']' && LA30_2<='\uFFFF')) ) {
                        alt30=2;
                    }


                }
                else if ( ((LA30_0>='\u0000' && LA30_0<='!')||(LA30_0>='#' && LA30_0<='[')||(LA30_0>=']' && LA30_0<='\uFFFF')) ) {
                    alt30=2;
                }


                switch (alt30) {
            	case 1 :
            	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:65:38: ESC
            	    {
            	    mESC(); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:65:44: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);

            match('\"'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "CHAR"
    public final void mCHAR() throws RecognitionException {
        try {
            int _type = CHAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:68:5: ( '\\'' ( options {greedy=false; } : ESC | . )* '\\'' )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:68:8: '\\'' ( options {greedy=false; } : ESC | . )* '\\''
            {
            match('\''); if (state.failed) return ;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:68:13: ( options {greedy=false; } : ESC | . )*
            loop31:
            do {
                int alt31=3;
                int LA31_0 = input.LA(1);

                if ( (LA31_0=='\'') ) {
                    alt31=3;
                }
                else if ( (LA31_0=='\\') ) {
                    int LA31_2 = input.LA(2);

                    if ( (LA31_2=='\'') ) {
                        alt31=1;
                    }
                    else if ( (LA31_2=='\\') ) {
                        alt31=1;
                    }
                    else if ( (LA31_2=='\"') ) {
                        alt31=1;
                    }
                    else if ( ((LA31_2>='\u0000' && LA31_2<='!')||(LA31_2>='#' && LA31_2<='&')||(LA31_2>='(' && LA31_2<='[')||(LA31_2>=']' && LA31_2<='\uFFFF')) ) {
                        alt31=2;
                    }


                }
                else if ( ((LA31_0>='\u0000' && LA31_0<='&')||(LA31_0>='(' && LA31_0<='[')||(LA31_0>=']' && LA31_0<='\uFFFF')) ) {
                    alt31=2;
                }


                switch (alt31) {
            	case 1 :
            	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:68:39: ESC
            	    {
            	    mESC(); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:68:45: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop31;
                }
            } while (true);

            match('\''); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CHAR"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:70:6: ( ( ' ' | '\\t' | '\\n' | '\\r\\n' )+ )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:70:8: ( ' ' | '\\t' | '\\n' | '\\r\\n' )+
            {
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:70:8: ( ' ' | '\\t' | '\\n' | '\\r\\n' )+
            int cnt32=0;
            loop32:
            do {
                int alt32=5;
                switch ( input.LA(1) ) {
                case ' ':
                    {
                    alt32=1;
                    }
                    break;
                case '\t':
                    {
                    alt32=2;
                    }
                    break;
                case '\n':
                    {
                    alt32=3;
                    }
                    break;
                case '\r':
                    {
                    alt32=4;
                    }
                    break;

                }

                switch (alt32) {
            	case 1 :
            	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:70:9: ' '
            	    {
            	    match(' '); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:70:13: '\\t'
            	    {
            	    match('\t'); if (state.failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:70:18: '\\n'
            	    {
            	    match('\n'); if (state.failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:70:23: '\\r\\n'
            	    {
            	    match("\r\n"); if (state.failed) return ;


            	    }
            	    break;

            	default :
            	    if ( cnt32 >= 1 ) break loop32;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(32, input);
                        throw eee;
                }
                cnt32++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "QID"
    public final void mQID() throws RecognitionException {
        try {
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:73:7: ( ID ( '.' ID )* )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:73:10: ID ( '.' ID )*
            {
            mID(); if (state.failed) return ;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:73:13: ( '.' ID )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0=='.') ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:73:14: '.' ID
            	    {
            	    match('.'); if (state.failed) return ;
            	    mID(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "QID"

    // $ANTLR start "QIDStar"
    public final void mQIDStar() throws RecognitionException {
        try {
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:77:5: ( ID ( '.' ID )* ( '.*' )? )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:77:8: ID ( '.' ID )* ( '.*' )?
            {
            mID(); if (state.failed) return ;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:77:11: ( '.' ID )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0=='.') ) {
                    int LA34_1 = input.LA(2);

                    if ( ((LA34_1>='A' && LA34_1<='Z')||LA34_1=='_'||(LA34_1>='a' && LA34_1<='z')) ) {
                        alt34=1;
                    }


                }


                switch (alt34) {
            	case 1 :
            	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:77:12: '.' ID
            	    {
            	    match('.'); if (state.failed) return ;
            	    mID(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);

            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:77:21: ( '.*' )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0=='.') ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:77:21: '.*'
                    {
                    match(".*"); if (state.failed) return ;


                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "QIDStar"

    // $ANTLR start "TYPE"
    public final void mTYPE() throws RecognitionException {
        try {
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:80:7: ( QID ( '[]' )? )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:80:9: QID ( '[]' )?
            {
            mQID(); if (state.failed) return ;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:80:13: ( '[]' )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0=='[') ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:80:13: '[]'
                    {
                    match("[]"); if (state.failed) return ;


                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "TYPE"

    // $ANTLR start "ARG"
    public final void mARG() throws RecognitionException {
        try {
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:83:7: ( TYPE WS ID )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:83:9: TYPE WS ID
            {
            mTYPE(); if (state.failed) return ;
            mWS(); if (state.failed) return ;
            mID(); if (state.failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end "ARG"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:86:6: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:86:8: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:86:32: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( ((LA37_0>='0' && LA37_0<='9')||(LA37_0>='A' && LA37_0<='Z')||LA37_0=='_'||(LA37_0>='a' && LA37_0<='z')) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "ESC"
    public final void mESC() throws RecognitionException {
        try {
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:89:8: ( '\\\\' ( '\"' | '\\'' | '\\\\' ) )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:89:11: '\\\\' ( '\"' | '\\'' | '\\\\' )
            {
            match('\\'); if (state.failed) return ;
            if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "ESC"

    // $ANTLR start "TEMPLATE"
    public final void mTEMPLATE() throws RecognitionException {
        try {
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:92:12: ( '<' ( WS )? QID ( WS )? '>' ( WS )? )
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:92:14: '<' ( WS )? QID ( WS )? '>' ( WS )?
            {
            match('<'); if (state.failed) return ;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:92:18: ( WS )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( ((LA38_0>='\t' && LA38_0<='\n')||LA38_0=='\r'||LA38_0==' ') ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:92:18: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            mQID(); if (state.failed) return ;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:92:26: ( WS )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( ((LA39_0>='\t' && LA39_0<='\n')||LA39_0=='\r'||LA39_0==' ') ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:92:26: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('>'); if (state.failed) return ;
            // src/comtor/analyzers/InteractionsResources/methodExtraction.g:92:34: ( WS )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( ((LA40_0>='\t' && LA40_0<='\n')||LA40_0=='\r'||LA40_0==' ') ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // src/comtor/analyzers/InteractionsResources/methodExtraction.g:92:34: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "TEMPLATE"

    public void mTokens() throws RecognitionException {
        // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:39: ( IMPORT | CLASS | METHOD | CALL | COMMENT | SL_COMMENT | STRING | CHAR | WS )
        int alt41=9;
        alt41 = dfa41.predict(input);
        switch (alt41) {
            case 1 :
                // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:41: IMPORT
                {
                mIMPORT(); if (state.failed) return ;

                }
                break;
            case 2 :
                // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:48: CLASS
                {
                mCLASS(); if (state.failed) return ;

                }
                break;
            case 3 :
                // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:54: METHOD
                {
                mMETHOD(); if (state.failed) return ;

                }
                break;
            case 4 :
                // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:61: CALL
                {
                mCALL(); if (state.failed) return ;

                }
                break;
            case 5 :
                // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:66: COMMENT
                {
                mCOMMENT(); if (state.failed) return ;

                }
                break;
            case 6 :
                // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:74: SL_COMMENT
                {
                mSL_COMMENT(); if (state.failed) return ;

                }
                break;
            case 7 :
                // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:85: STRING
                {
                mSTRING(); if (state.failed) return ;

                }
                break;
            case 8 :
                // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:92: CHAR
                {
                mCHAR(); if (state.failed) return ;

                }
                break;
            case 9 :
                // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:97: WS
                {
                mWS(); if (state.failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1_methodExtraction
    public final void synpred1_methodExtraction_fragment() throws RecognitionException {   
        // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:41: ( IMPORT )
        // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:41: IMPORT
        {
        mIMPORT(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_methodExtraction

    // $ANTLR start synpred2_methodExtraction
    public final void synpred2_methodExtraction_fragment() throws RecognitionException {   
        // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:48: ( CLASS )
        // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:48: CLASS
        {
        mCLASS(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_methodExtraction

    // $ANTLR start synpred3_methodExtraction
    public final void synpred3_methodExtraction_fragment() throws RecognitionException {   
        // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:54: ( METHOD )
        // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:54: METHOD
        {
        mMETHOD(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_methodExtraction

    // $ANTLR start synpred4_methodExtraction
    public final void synpred4_methodExtraction_fragment() throws RecognitionException {   
        // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:61: ( CALL )
        // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:61: CALL
        {
        mCALL(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_methodExtraction

    // $ANTLR start synpred5_methodExtraction
    public final void synpred5_methodExtraction_fragment() throws RecognitionException {   
        // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:66: ( COMMENT )
        // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:66: COMMENT
        {
        mCOMMENT(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_methodExtraction

    // $ANTLR start synpred6_methodExtraction
    public final void synpred6_methodExtraction_fragment() throws RecognitionException {   
        // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:74: ( SL_COMMENT )
        // src/comtor/analyzers/InteractionsResources/methodExtraction.g:1:74: SL_COMMENT
        {
        mSL_COMMENT(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_methodExtraction

    public final boolean synpred2_methodExtraction() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_methodExtraction_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_methodExtraction() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_methodExtraction_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_methodExtraction() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_methodExtraction_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_methodExtraction() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_methodExtraction_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_methodExtraction() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_methodExtraction_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_methodExtraction() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_methodExtraction_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA41 dfa41 = new DFA41(this);
    static final String DFA41_eotS =
        "\16\uffff";
    static final String DFA41_eofS =
        "\16\uffff";
    static final String DFA41_minS =
        "\1\11\1\uffff\1\0\3\uffff\1\0\2\uffff\1\0\1\uffff\1\0\2\uffff";
    static final String DFA41_maxS =
        "\1\172\1\uffff\1\0\3\uffff\1\0\2\uffff\1\0\1\uffff\1\0\2\uffff";
    static final String DFA41_acceptS =
        "\1\uffff\1\11\1\uffff\1\2\1\3\1\4\1\uffff\1\5\1\6\1\uffff\1\10\1"+
        "\uffff\1\1\1\7";
    static final String DFA41_specialS =
        "\2\uffff\1\0\3\uffff\1\1\2\uffff\1\2\1\uffff\1\3\2\uffff}>";
    static final String[] DFA41_transitionS = {
            "\2\1\2\uffff\1\1\22\uffff\1\1\1\uffff\1\15\4\uffff\1\12\7\uffff"+
            "\1\6\21\uffff\32\11\4\uffff\1\11\1\uffff\2\11\1\2\5\11\1\13"+
            "\21\11",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "\1\uffff",
            "",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA41_eot = DFA.unpackEncodedString(DFA41_eotS);
    static final short[] DFA41_eof = DFA.unpackEncodedString(DFA41_eofS);
    static final char[] DFA41_min = DFA.unpackEncodedStringToUnsignedChars(DFA41_minS);
    static final char[] DFA41_max = DFA.unpackEncodedStringToUnsignedChars(DFA41_maxS);
    static final short[] DFA41_accept = DFA.unpackEncodedString(DFA41_acceptS);
    static final short[] DFA41_special = DFA.unpackEncodedString(DFA41_specialS);
    static final short[][] DFA41_transition;

    static {
        int numStates = DFA41_transitionS.length;
        DFA41_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA41_transition[i] = DFA.unpackEncodedString(DFA41_transitionS[i]);
        }
    }

    class DFA41 extends DFA {

        public DFA41(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 41;
            this.eot = DFA41_eot;
            this.eof = DFA41_eof;
            this.min = DFA41_min;
            this.max = DFA41_max;
            this.accept = DFA41_accept;
            this.special = DFA41_special;
            this.transition = DFA41_transition;
        }
        public String getDescription() {
            return "1:1: Tokens options {k=1; backtrack=true; } : ( IMPORT | CLASS | METHOD | CALL | COMMENT | SL_COMMENT | STRING | CHAR | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA41_2 = input.LA(1);

                         
                        int index41_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_methodExtraction()) ) {s = 3;}

                        else if ( (synpred3_methodExtraction()) ) {s = 4;}

                        else if ( (synpred4_methodExtraction()) ) {s = 5;}

                         
                        input.seek(index41_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA41_6 = input.LA(1);

                         
                        int index41_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_methodExtraction()) ) {s = 7;}

                        else if ( (synpred6_methodExtraction()) ) {s = 8;}

                         
                        input.seek(index41_6);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA41_9 = input.LA(1);

                         
                        int index41_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_methodExtraction()) ) {s = 4;}

                        else if ( (synpred4_methodExtraction()) ) {s = 5;}

                         
                        input.seek(index41_9);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA41_11 = input.LA(1);

                         
                        int index41_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_methodExtraction()) ) {s = 12;}

                        else if ( (synpred3_methodExtraction()) ) {s = 4;}

                        else if ( (synpred4_methodExtraction()) ) {s = 5;}

                         
                        input.seek(index41_11);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 41, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}