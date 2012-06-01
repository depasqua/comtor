/**
 *  Comment Mentor: A Comment Quality Assessment Tool
 *  Copyright (C) 2012 The College of New Jersey
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.comtor.reporting;

import org.comtor.analyzers.*;
import java.io.*;
import java.util.*;
import java.text.*;

/**
 * The <code>GVSupport</code> class provides a set of utilities for
 * building a graphviz output program.
 *
 * Right now this is specialized to drawing a simple author-&gt;class
 * mapping.
 *
 * @author Michael E. Locasto
 */
public final class GVSupport
{
    /**
     * Temporary storage for keeping track of various elements like:
     * - packages, classes, fields, constructors, and methods
     * - authors and classes
     * 
     * key: String object containing a unique node identifier
     * value: GVNode containing the node attributes
     */
    private Hashtable<String, GVNode> m_nodes = new Hashtable();

    /**
     * Temporary storage for the author -> classes map / edges.
     */
    private Vector<String> m_edges = new Vector();

    /** A handle to the output file */
    private File m_file = null;

    /** Output utility / writer */
    private PrintWriter m_fout = null;

    /** A flag telling us if setup of the output stream failed. If so,
     * object should not be used. Communicated to clients via the
     * return value of @see GVSupport#setup setup() */
    private boolean m_outputFail = false;

    /** default no-arg constructor */
    public GVSupport(){}

    /**
     * Configure and initialize this GVSupport object to hide all the
     * grungy file-handling details from a client.
     *
     * @return boolean
     * @assumption: clients MUST call this method (XXX is this a lifecycle assumption?)
     */
    public boolean setup(String filename,
			 String graphname)
    {
	try{
	    m_file = new File(filename);
	    m_fout = new PrintWriter(new BufferedWriter(new FileWriter(m_file)));
	    m_fout.print("strict digraph "+graphname+" {\n");
	    m_fout.print("ranksep=3;\n");
	    m_fout.print("ratio=auto;\n");
	    //fout.print("root=\""+GVNode.DEFAULT_ROOT_NODE_NAME+"\";\n");
	}catch(IOException ioex){
	    System.err.println("I/O Exception: error opening .dot output file"+
			       ioex.getMessage());
	    //bail on output
	    m_outputFail = true;
	}

	return m_outputFail;
    }

    public String getOutputFilePath()
    {
	if(null!=m_file)
	    return m_file.getAbsolutePath();
	else
	    return "";
		
    }

    /**
     * Write the final lines of the DOT file, then close the output stream.
     * 
     * @throws IOException if something went wrong with writing to m_fout
     */
    public void finish()
	throws IOException
    {
	m_fout.print("}\n");
	m_fout.close();
    }

    /** 
     * Empty the internal storage by invoking their
     * <code>.clear()</code> (@see java.util.AbstractCollection#clear)
     * method.
     */
    public void flushHash()
    {
	m_nodes.clear();
	m_edges.clear();
    }

    /**
     * Add an edge to our edge table.
     *
     * @param name - the brief name of the parent class
     * @param qualfix - the FQCN of the parent class
     */
    public void addEdge(String src,
			String dst)
    {
	StringBuffer edge = new StringBuffer(src.length()+dst.length());

	edge.append("\"");
	edge.append(src);
	edge.append("\"");
	edge.append("->");
	edge.append("\"");
	edge.append(dst);
	edge.append("\"");

	m_edges.add(edge.toString());
	edge = null;
	return;
    }

    /**
     * Add a node identifier to our internal node storage.
     *
     * Caller should purge the internal storage (write output to a
     * file and then call @see #flushHash flushHash() ) after the
     * method completes.
     *
     * @param name - node identifier; author or classname
     * @param shape - node shape; mapping is done by client
     * @param color - node color; mapping is done by client
     */
    public void addNode(String name,
			String shape,
			String color)
    {
	GVNode n = new GVNode();
	n.name = name;
	n.fillColor = color;
	n.shape = shape;
	m_nodes.put(name, n);
	return;
    }

    /**
     * Take the accumulated internal storage and output their contents
     * as a graphviz DOT file.
     * 
     * @param fout the handle to the .dot file
     * @assumption: assume param fout is already open and available
     * for writing. Caller assumes responsibility for flushing and
     * closing.
     */
    public void writeToDotFile()
	throws IOException
    {
	Enumeration edges = m_edges.elements();
	Enumeration nodes = m_nodes.elements();
	String edge;
	GVNode node;

	//write nodes
	while( nodes.hasMoreElements() )
	{
	    node = (GVNode)nodes.nextElement();
	    m_fout.print("\""+node.name+"\""+" [ label=\"\",shape=\""+node.shape+"\",style=\"filled\",color=\""+node.fillColor+"\" ];\n");
	}	

	//write edges
	while( edges.hasMoreElements() )
	{
	    edge = (String)edges.nextElement();
	    m_fout.print(edge+" [ color=\"black\",arrowhead=\"dot\" ] ;\n");
	}
	return;
    }

}
