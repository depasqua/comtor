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

import java.util.Hashtable;
import java.util.ArrayList;

/**
 * The <code>DefaultHTCodeStore</code> class provides a simple backing
 * store based on an in-memory {@link java.lang.Hashtable} hashtable.
 *
 *
 * @author Michael E. Locasto
 */
public class DefaultHTCodeStore
    implements ICodeStore
{

    /** The backing store */
    private Hashtable m_table = new Hashtable();
    
    public boolean isStoreInitialized()
    {
	return true;
    }

    public int count()
    {
	return m_table.size();
    }

    public boolean initStore(CodeStoreConf config)
    {
	return true;
    }

    public boolean closeStore()
    {
	return true;
    }

    public void store(CodeChunk chunk)
    {
	m_table.put(chunk.getName(), chunk);
    }

    public void annotate(CodeChunk chunk, CodeNote n)
    {
	//need a "note" hashtable keyed off same as m_table
	return;
    }

    public void rate(CodeChunk chunk, CodeRating r)
    {
	//need a "ratings" hashtable keyed off same as m_table
	return;
    }

    public CodeChunk fetch(String name)
    {
	return (CodeChunk)m_table.get(name);
    }

    public ArrayList<CodeChunk> fetchMultiple(String name)
    {
	ArrayList<CodeChunk> a = new ArrayList<CodeChunk>();
	a.add( (CodeChunk)m_table.get(name) );
	return a;
    }

    /**
     * Not yet implemented and will always return <code>null</code>.
     *
     * @returns null
     */
    public CodeChunk fetch(int id)
    {
	return null;
    }

}