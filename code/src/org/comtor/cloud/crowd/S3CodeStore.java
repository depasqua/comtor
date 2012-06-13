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
 * The <code>S3CodeStore</code> object provides a backing store that
 * uses Amazon Web Services (AWS) Simple Storage Service S3).
 *
 * @author Michael E. Locasto
 */
public class S3CodeStore
    implements ICodeStore
{

    private boolean m_isInitialized = false;

    /**
     * Default constructor
     */
    public S3CodeStore()
    {

    }

    public boolean isStoreInitialized()
    {
	return m_isInitialized;
    }

    /**
     * We expect configuration parameters to include S3
     * credentials (username, key)
     */
    public boolean initStore(CodeStoreConf config)
    {
	//pull out properties file, get key "bucket.name"

	return m_isInitialized;
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