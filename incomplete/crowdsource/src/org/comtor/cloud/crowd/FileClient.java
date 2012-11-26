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

import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * The <code>FileClient</code> class represents a simple {@link
 * ICodeStore} client that hacks a single file into pieces and
 * stores them into a code store.
 *
 * Ideally, you (the user, the system, the admin) should provide an
 * XML properties configuration file to this client; the client
 * creates or gets a handle to a specific {@link ICodeStore} class.
 * That object then proxies a connection to a back-end data store
 * such as a database.
 *
 * Currently, this demonstration class (i.e., FileClient) creates a
 * code store that simply provides an in-memory Java Hashtable.
 *
 * You shouldn't use this for anything - it is a proof of concept. It
 * also serves as a code example for writing other clients.
 *
 * @author Michael E. Locasto
 */
public class FileClient
    extends CodeStoreClient
{
    private String m_filename = "";
    private BufferedReader m_fin = null;

    /**
     * Set up a few internal objects for use and call the
     * internal kickoff routine. 
     */
    public FileClient(String [] args)
    {
	boolean configured = false;
	
	configured = executeConfig(args[0]);
	if(!configured)
	{
	    System.err.println("[FileClient]: configuration failed...aborting");
	    return;
	}
	kickoff(); // call internal service routine
    }

    /**
     * Read from the input file line by line.
     *
     * @returns a string holding a line of a file
     */
    private String getAnotherLine()
    {
	String line = null;

	try
	{
	    line = m_fin.readLine();
	}catch(IOException ioex){
	    System.err.println("[FileClient]: getAnotherLine(): "+ioex.getMessage());
	    line = null;
	}

	return line;
    }

    /**
     * Chop file into pieces. (either lines or rough-methods)
     * Iterate over pieces.
     * Create CodeChunk for each piece.
     * Store each CodeChunk into m_store.
     * Close file handle when done.
     *
     * @assumption: m_store is configured and operational
     * @assumption: m_fin is setup and open
     * @assumption: we bear responsibility for closing m_fin
     */
    private void doStorage()
    {
	CodeChunk tmp = null;
	String line = "";
	int lineNum = 0;

	line = getAnotherLine();
	while(line!=null)
	{
	    tmp = new CodeChunk();
	    tmp.setType(CodeChunk.CCHUNK_RAW_TEXT_TYPE);
	    tmp.setText(line);
	    tmp.setName(""+lineNum); //need a key, so line number serves in a pinch
	    m_store.store(tmp);
	    line = getAnotherLine();
	    lineNum++;
	}

	try
        { 
	    m_fin.close();
	}catch(IOException ioex){
	    System.err.println("[FileClient]: error in doStorage() closing input file: "
			       +ioex.getMessage());
	}

	return;
    }

    /**
     * Open the target (source) file.
     * Read in and parse the file (by calling doStorage()).
     * Store code chunks into the code store (via doStorage()).
     * Close the store.
     */
    private void kickoff()
    {
	try
	{
	    m_filename = m_conf.m_specific.getProperty(ConfigKeys.SINGLE_SRC_FILE_TARGET);
	    System.out.println("[FileClient]: examining file: "+m_filename);
	    m_fin = new BufferedReader(new FileReader(m_filename));

	}catch(IOException ioex){
	    System.err.println("[FileClient]: error in kickoff(): "+ioex.getMessage());
	    return;
	}

	if(null!=m_store && m_store.isStoreInitialized())
	{
	    doStorage();
	}else{
	    System.err.println("[FileClient]: Code Store not initialized.");
	}

	//output number of entries in the ICodeStore
	System.out.println("[FileClient]: number of code entries in code store: "
			   +m_store.count());

	m_store.closeStore();

	return;
    }

    /**
     * java org.comtor.cloud.crowd.FileClient &lt;configfile&gt;
     */
    public static void main(String [] args)
    {
	if(null==args)
	    return;

	new FileClient(args);
    }
}