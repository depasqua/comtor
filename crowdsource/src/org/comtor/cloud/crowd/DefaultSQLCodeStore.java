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

import java.util.ArrayList;

/**
 * The <code>DefaultSQLCodeStore</code> object provides a way of
 * talking to a backing store that is most likely a relational
 * database.
 *
 * @author Michael E. Locasto
 */
public class DefaultSQLCodeStore
    implements ICodeStore
{

    public boolean isStoreInitialized()
    {
	return false;
    }

    public boolean initStore(CodeStoreConf config)
    {
	return false;
    }

    public boolean closeStore()
    {
	return true;
    }

    public int count()
    {
	return 0;
    }

    public void store(CodeChunk chunk)
    {
	switch(chunk.getType())
	{
	case CodeChunk.CCHUNK_FIELD_TYPE:
	    break;
	case CodeChunk.CCHUNK_METHOD_TYPE:
	    break;
	default:
	    System.err.println("["+this.getClass().getName()
			       +"]: store(): unhandled type ("
			       +chunk.getType()+")");
	    break;
	}

	return;
    }

    public void annotate(CodeChunk chunk, CodeNote n)
    {
	return;
    }

    public void rate(CodeChunk chunk, CodeRating r)
    {
	return;
    }

    public CodeChunk fetch(String name)
    {
	return null;
    }

    public ArrayList<CodeChunk> fetchMultiple(String name)
    {
	return null;
    }

    public CodeChunk fetch(int id)
    {
	return null;
    }

}