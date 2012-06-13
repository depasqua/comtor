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

/**
 * The <code>CodeChunk</code> class represents a piece of source code.
 * It closely corresponds to a chunk of plain text, but has several
 * attributes that represent the meta-data and structure of the code
 * chunk contained in the snippet of plain text.
 * 
 * It is used as a transport layer to store and fetch pieces of a
 * source file into a data store via the {@link ICodeStore} interface.
 *
 * @author Michael E. Locasto
 */
public final class CodeChunk
{

    public static final int CCHUNK_RAW_TEXT_TYPE         = 1001;
    public static final int CCHUNK_METHOD_TYPE           = 1000;
    public static final int CCHUNK_CONSTRUCTOR_TYPE      = 2000;
    public static final int CCHUNK_CLASS_TYPE            = 4000;
    public static final int CCHUNK_FIELD_TYPE            = 8000;

    // ------------------------------------- meta-data

    /** full URI/URL/absolute path of the source file this CodeChunk
     * belongs to */
    private String m_filepath;

    /** line number (in original source file) where the code chunk starts */
    private int m_start;

    /** line number (in original source file) where the code chunk ends */
    private int m_end;

    /** Indicates the "type" of this chunk: (field? function?) XXX should be an enum */
    private int m_type;

    // ------------------------------------- structure

    /** The actual text of the code chunk; raw. */
    private String m_text;

    /** The "header" comment associated with this chunk; raw text. */
    private String m_comment;

    /** The name of this code chunk; likely the method name or the
     * variable name */
    private String m_name;
    
    /** Either the return type (if chunk is a method) or the plain
     * type (if chunk is a variable) */
    private String m_codetype;

    // ------------------------------------- methods

    public String getName(){ return m_name.toString(); }
    public String getText(){ return m_text.toString(); }
    public String getComment(){ return m_comment.toString(); }
    public int getType(){ return m_type; }

    public void setText(String t){ m_text = t; }
    public void setName(String n){ m_name = n; }
    public void setType(int t){ m_type = t; }

}