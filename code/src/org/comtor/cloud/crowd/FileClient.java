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
 * You shouldn't use this for anything - it is a proof of concept.
 *
 * @author Michael E. Locasto
 */
public class FileClient
    extends CodeStoreClient
{
    private ICodeStore m_store = null;
    private CodeStoreConf m_conf = new CodeStoreConf();
    private String m_conffile = "";
    private String m_filename = "";
    private BufferedReader m_fin = null;

    /**
     * Set up a few internal objects for use and call the
     * internal kickoff routine. 
     */
    public FileClient(String [] args)
    {
	m_conffile = args[0];
	try{
	    kickoff();
	}catch(Exception e){
	    e.printStackTrace();
	}
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
	    tmp.setName(""+lineNum);
	    m_store.store(tmp);
	    line = getAnotherLine();
	    lineNum++;
	}
	return;
    }

    /**
     * Open the configuration file.
     * Load configuration file into m_conf
     * Create appropriate ICodeStore type.
     * Open the indicated file.
     * Read in and parse the file.
     * Store code chunks into the code store.
     */
    private void kickoff()
	throws NullPointerException
    {
	String codestoreclass = "";
	Class clazz = null;
	File f = null;
	File conf = null;
	FileInputStream fin = null;

	try
	{
	    fin = new FileInputStream(m_conffile);
	    m_conf.m_specific.loadFromXML(fin);

	    m_filename = m_conf.m_specific.getProperty("crowd.conf.src.file");
	    System.out.println("[FileClient]: examining file: "+m_filename);
	    f = new File(m_filename);
	    m_fin = new BufferedReader(new FileReader(f));

	}catch(IOException ioex){
	    System.err.println("[FileClient]: error in kickoff(): "+ioex.getMessage());
	    return;
	}

	try
	{
	    codestoreclass = m_conf.m_specific.getProperty("crowd.conf.codestore.type");
	    System.out.println("[FileClient]: storing to store type: "+codestoreclass);

	    clazz = Class.forName(codestoreclass);
	    m_store = (ICodeStore)clazz.newInstance();
	}catch(IllegalAccessException iaex){
	    System.err.println("[FileClient]: kickoff(): access denied to code store class file: "
			       +iaex.getMessage());
	    return;
	}catch(InstantiationException iex){
	    System.err.println("[FileClient]: kickoff(): failed to instantiate code store object: "
			       +iex.getMessage());
	    return;
	}catch(ClassNotFoundException cnfex){
	    System.err.println("[FileClient]: kickoff(): failed to find code store class definition: "
			       +cnfex.getMessage());
	    return;
	}

	m_store.initStore(m_conf);

	if(null!=m_store && m_store.isStoreInitialized())
	{
	    doStorage();
	}else{
	    System.err.println("[FileClient]: Code Store not initialized.");
	}

	//output number of entries in the ICodeStore
	System.out.println("[FileClient]: number of code entries in code store: "
			   +m_store.count());

	try
        { 
	    fin.close();
	    m_fin.close();
	}catch(IOException ioex1){
	    System.err.println("[FileClient]: error in kickoff() closing input file: "
			       +ioex1.getMessage());
	}

	m_store.closeStore();

	return;
    }

    /**
     * java org.comtor.cloud.crowd.FileClient &lt;configfile&gt; [filename]
     */
    public static void main(String [] args)
    {
	if(null==args)
	    return;

	new FileClient(args);
    }
}