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
import java.sql.*;

/**
 * The <code>DefaultSQLCodeStore</code> object provides a way of
 * talking to a backing store that is most likely a relational
 * database.  This class was developed against MySQL 5.5 inside an
 * Amazon web services micro RDB host.
 *
 * @author Michael E. Locasto
 */
public class DefaultSQLCodeStore
    implements ICodeStore
{

    private boolean m_initialized = false;
    private String m_driver = "";
    private String m_user = "";
    private String m_pass = "";
    private String m_cxn = "";

    /**
     * Simply use one sql Connection object for now; no connection pooling.
     */
    private Connection m_conn = null;

    public boolean isStoreInitialized()
    {
	return m_initialized;
    }

    public boolean initStore(CodeStoreConf config)
    {
	boolean status = false;

	m_driver = config.m_specific.getProperty(ConfigKeys.CONNECTION_DRIVER);
	m_user = config.m_specific.getProperty(ConfigKeys.CONNECTION_USERNAME);
	m_pass = config.m_specific.getProperty(ConfigKeys.CONNECTION_PASSWORD);
	m_cxn = config.m_specific.getProperty(ConfigKeys.CONNECTION_STRING);

	//check these parameters for sanity

	//create database driver instance
	try 
	{
            Class.forName(m_driver).newInstance();
        } catch (Exception ex) {
	    System.err.println("["+this.getClass().getName()
			       +"] initStore(): failed to load JDBC driver ("
			       +m_driver
			       +") because: "
			       +ex.getMessage());
	    status = false;
	    return status;
        }

	//create connection
	try 
	{
	    m_conn = DriverManager.getConnection(m_cxn, m_user, m_pass);

	    status = true;
	} catch (SQLException sqlex) {
	    status = false;
	    // handle any errors
	    System.err.println("SQLException: " + sqlex.getMessage());
	    System.err.println("SQLState: " + sqlex.getSQLState());
	    System.err.println("VendorError: " + sqlex.getErrorCode());
	}

	m_initialized = status;
	return status;
    }

    public boolean closeStore()
    {
	try{
	    m_conn.close();
	}catch(SQLException ex){
	    System.err.println("["+this.getClass().getName()
			       +"]: closeStore(): Problem closing SQL connection: "
			       +ex.getMessage());
	}
	return true;
    }

    /**
     * Report how many code chunks the database is storing.
     */
    public int count()
    {
	int c = 0;
	
	//issue a SELECT COUNT(*) FROM methods;
	//issue a SELECT COUNT(*) FROM fields;
	//sum the two results and report

	return c;
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