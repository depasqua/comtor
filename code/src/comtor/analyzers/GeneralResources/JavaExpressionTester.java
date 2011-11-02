/***************************************************************************
*  Comment Mentor: A Comment Quality Assessment Tool
*  Copyright (C) 2005 Michael E. Locasto
*
*  This program is free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful, but
*  WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with this program; if not, write to the:
*  Free Software Foundation, Inc.
*  59 Temple Place, Suite 330
*  Boston, MA  02111-1307  USA
**************************************************************************/
package comtor.analyzers.GeneralResources;

import comtor.analyzers.GeneralResources.*;
import java.util.*;
import java.text.*;
import org.antlr.runtime.*;

/**************************************************************************
* The <code>JavaExpressionTester</code> class checks that the given input
* is a valid java expression.  However, it does not check that the 
* variables or methods exist.  
*
* @author Stephen Sigwart
**************************************************************************/
public class JavaExpressionTester
{
	/**************************************************************************
	* Tests the given expression to see if it is a valid expression
	* 
	* @param expr Text to test
	* 
	* @return boolean Returns true if the text is a valid expression		
	**************************************************************************/
	public static boolean checkExpression(String expr)
	{
		// Create input stream for text
		CharStream input = new ANTLRStringStream(expr);
		
		// Create parser
		JavaLexer lex = new JavaLexer(input);
		TokenStream tokens = new CommonTokenStream(lex);
		JavaParser parser = new JavaParser(tokens);
		
		// Parse
		try
		{
			parser.expression_entry();
		}
		catch (RecognitionException e)
		{
		  return false;
		}
		
		// Check for errors and return
		return !parser.hasErrors();
	}
}
