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

import java.io.*;

/**
 * The <code>CodeStoreClient</code> class represents clients of {@link
 * ICodeStore} objects.  This class is abstract so you cannot (and
 * should not) construct an instance of it.
 * 
 * The job of clients is to read source code from some data source,
 * parse it in a meaningful way to understand its structure or
 * semantics, and then store it into a backing code store.
 * 
 * Usually this is accomplished by reading from some flat
 * representation (such as a flat source file or a jar), but may
 * alternatively involve reading the file from a source code
 * management or revision control system like cvs, svn, or git.
 * 
 * The abstract client (i.e., this class) provides some default
 * plumbing, but really requires a subclass to know how to read from a
 * specific data source. This plumbing is particularly centered around
 * reading configuration file options and initializing the chosen
 * backing store (e.g., database).  Subclasses are responsible for
 * initializing the data <em>source</em> (e.g., svn).
 *
 * @author Michael E. Locasto
 */
public abstract class CodeStoreClient
{
    /** Every child has a handle to a generic CodeStore */
    protected ICodeStore m_store = null;

    /** 
     * Every child has a handle to a configuration object; we call
     * the constructor here in order to make sure the backing
     * Properties object is constructed as well.
     */
    protected CodeStoreConf m_conf = new CodeStoreConf();

    /**
     * This method is meant to supply children with common functionality
     * related to reading the configuration file and setting up various
     * objects.  The purpose here is to avoid replicating this code into
     * every type of client.  Actually initializing the code store may be
     * going a step too far, but on the other hand...we'll see how it works
     * in practice. Children can override and provide their own definition
     * if they understand the plumbing here enough.
     * 
     * Open the configuration file.
     * Load configuration file into m_conf
     * Create appropriate ICodeStore type.
     * 
     */
    protected boolean executeConfig(String file)
    {
	FileInputStream fin = null;
	String codeStoreClass = "";
	Class clazz = null;

	try
	{
	    fin = new FileInputStream(file);
	    m_conf.m_specific.loadFromXML(fin);

	}catch(IOException ioex){
	    System.err.println("["+this.getClass().getName()
			       +"]: error in executeConfig(): "
			       +ioex.getMessage());
	    return false;
	}

	try
        { 
	    fin.close();
	}catch(IOException ioex1){
	    System.err.println("["+this.getClass().getName()
			       +"]: error in executeConfig() closing conf file: "
			       +ioex1.getMessage());
	    //do anything to m_conf or m_store?
	    return false;
	}

	try
	{
	    codeStoreClass = m_conf.m_specific.getProperty(ConfigKeys.CODESTORE_TYPE);
	    System.out.println("["+this.getClass().getName()
			       +"]: creating store type: "+codeStoreClass);

	    clazz = Class.forName(codeStoreClass);
	    m_store = (ICodeStore)clazz.newInstance();

	}catch(IllegalAccessException iaex){
	    System.err.println("["+this.getClass().getName()
			       +"]: kickoff(): access denied to code store class file: "
			       +iaex.getMessage());
	    return false;
	}catch(InstantiationException iex){
	    System.err.println("["+this.getClass().getName()
			       +"]: kickoff(): failed to instantiate code store object: "
			       +iex.getMessage());
	    return false;
	}catch(ClassNotFoundException cnfex){
	    System.err.println("["+this.getClass().getName()
			       +"]: kickoff(): failed to find code store class definition: "
			       +cnfex.getMessage());
	    return false;
	}

	m_store.initStore(m_conf);

	return true;
    }

}