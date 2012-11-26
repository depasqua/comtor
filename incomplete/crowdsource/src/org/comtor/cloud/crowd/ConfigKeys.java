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
 * The <code>ConfigKeys</code> class provides a number of string
 * constants used to identify various "standard" configuration options
 * that might be stored in an {@link CodeStoreConf} object.
 *
 * @author Michael E. Locasto
 */
public final class ConfigKeys
{
    /** 
     * A String identifying the unique key for specifying which Code
     * Store to use
     */
    public static final String CODESTORE_TYPE = "crowd.conf.codestore.type";

    /** 
     * A String identifying the unique key for specifying which
     * (source) file to analyze 
     */
    public static final String SINGLE_SRC_FILE_TARGET = "crowd.conf.src.file";

    /**
     * A String identifying the unique key for specifying the
     * connection URL, URI, or string; a value mapped by this key in essence
     * holds information specifying how to connect to the backing store
     * data source.
     */
    public static final String CONNECTION_STRING = "crowd.conf.dst.connection.string";

    public static final String CONNECTION_USERNAME = "crowd.conf.dst.connection.username";

    public static final String CONNECTION_PASSWORD = "crowd.conf.dst.connection.password";

    public static final String CONNECTION_DRIVER = "crowd.conf.dst.connection.driver";

}