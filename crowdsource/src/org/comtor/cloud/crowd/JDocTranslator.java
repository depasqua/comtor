/************************************************************************
 *  Crowd-Sourced Comments
 *  Copyright (C) 2012 The College of New Jersey
 *  Copyright (C) 2012 Michael E. Locasto
 *
 *  This program is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see
 *  <http://www.gnu.org/licenses/>.
 ***********************************************************************/
package org.comtor.cloud.crowd;

import com.sun.javadoc.*;

/**
 * This utility class understands how to translate {@link
 * com.sun.javadoc.Doc} classes (and its children) into {@link
 * CodeChunk} objects.
 *
 * I'm going to purposefully refrain from javadoc'ing any of the
 * methods of this class. No methods are private; all are static.
 * The code should be self-documenting and obvious.
 *
 * The need for this class is clear: it centralizes this common
 * translation and avoids complicating the JDocClientDoclet code.
 * 
 * @author Michael E. Locasto
 */
public final class JDocTranslator
{

    public static CodeChunk translateMethod(MethodDoc m)
    {
	CodeChunk tmp = null;

	tmp = new CodeChunk();

	tmp.setFilepath(m.position().file().getName());
	tmp.setType(CodeChunk.CCHUNK_METHOD_TYPE);
	tmp.setStartLine(m.position().line());
	tmp.setEndLine(-1); //XXX
	
	tmp.setCodeType(m.returnType().qualifiedTypeName());
	tmp.setText(""); //XXX
	tmp.setName(m.name()); //is this the var name?
	tmp.setComment(m.getRawCommentText());

	return tmp;
    }

    public static CodeChunk translateField(FieldDoc f)
    {
	CodeChunk tmp = null;
       
	tmp = new CodeChunk();

	tmp.setFilepath(f.position().file().getName());
	tmp.setType(CodeChunk.CCHUNK_FIELD_TYPE);
	tmp.setStartLine(f.position().line());
	tmp.setEndLine(-1); //XXX
	
	tmp.setCodeType(f.type().qualifiedTypeName());
	tmp.setText(""); //XXX
	tmp.setName(f.name()); //is this the var name?
	tmp.setComment(f.getRawCommentText());
	
	return tmp;
    }

}