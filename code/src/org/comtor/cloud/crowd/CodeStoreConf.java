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

import java.util.Properties;

/**
 * The <code>CodeStoreConf</code> object contains configuration
 * information that enables a class implementing the {@link
 * ICodeStore} interface to connect to a specific data store, such as
 * a Hashtable, an SQL database, a AWS simple cloud store, an AWS S3
 * bucket, etc.
 *
 * @see ConfigKeys for more information on various "standard" keys used
 * to identify built-in configuration options
 *
 * @author Michael E. Locasto
 */
public final class CodeStoreConf
{
    /** Code-store specific configuration parameters. 
     * For example, this might be an S3 bucket name.
     * (e.g., "COMTOR.crowdcom")
     */
    public Properties m_specific;

    /**
     * Make sure we construct the properties object
     * because clients of this class will want to refer
     * to it directly.
     */
    public CodeStoreConf()
    {
	m_specific = new Properties();
    }

}