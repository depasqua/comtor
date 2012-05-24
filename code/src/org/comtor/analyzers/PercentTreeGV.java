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
package org.comtor.analyzers;

import org.comtor.drivers.*;
import com.sun.javadoc.*;
import java.io.*;
import java.util.*;
import java.text.*;

/**
 * The <code>PercentTreeGV</code> module produces a graphviz program
 * that is a visual representation of the "comment density": the
 * codebase represented as a graph, where each leaf is a method,
 * class, or data member.  Each leaf is shaded green if it has a
 * Javadoc comment associated with it and red if not.
 *
 * This module periodically flushes its in-memory hashtables to the
 * file so that it doesn't run out of memory by trying to keep all 
 * the elements of a large code base in memory at once.
 *
 * RUNNING NOTES: The output is saved to "comments.dot". You can
 * create a visual rendering of this data via:
 * 
 *  twopi -Tpdf -ograph.pdf comments.dot
 *
 * @see PercentageMethods
 * @author Michael E. Locasto
 */
public class PercentTreeGV
    implements ComtorDoclet
{
    private Properties prop = new Properties();
    private HashMap<String, Float> gradeBreakdown = new HashMap<String, Float>();

    /**
     * Temporary storage for keeping track of all encountered entities
     * in the RootDoc (classes, methods, fields).
     * 
     * key: String object containing the FQCN of the code entity
     * value: GVNode object - records basename of entity and whether it is commented
     */
    private Hashtable<String, GVNode> m_nodes = new Hashtable();

    /**
     * Temporary storage for the package->class->method | ->field relationships.
     */
    private Hashtable<String, String> m_edges = new Hashtable();

    /**
     * Constructor for this doclet. By default, it sets the total
     * number of possible "points" to 5.  This values is used when
     * "scoring" student submissions in the web-based client. The
     * "points" value can be modified via the web interface which
     * in turn calls the setGradingBreakdown method.
     */
    public PercentTreeGV() {
	// Set default values for grade breakdown
	gradeBreakdown.put("TotalPoints", new Float(5.0));
    }
    
    /**
     * Examine each class, method, and field (i.e., data member)
     * and produce a graphviz output file. The name of the file is
     * recorded in the COMTOR report.
     *
     * @param rootDoc  the root of the documentation tree
     * @return Properties list containing the result set
     */
    public Properties analyze(RootDoc rootDoc) 
    {
	// A counter for the classes, used in the properties list
	int classID = 0;
	DecimalFormat formatter = new DecimalFormat("##0000.000");	
	File f = null;
	PrintWriter fout = null;
	boolean outputFail = false;
	//string filename = "comment-density-"+System.currentTimeMillis()+".dot";
	//purposefully induce namespace collision on output file; many output
	//files are the equivalent of digital pollution.
	String filename = "comments.dot";
	ClassDoc [] classes = null;
	
	prop.setProperty("title", "Percent Tree (Density) via GraphViz output");
	prop.setProperty(formatter.format(-1), "Preamble notes go here.");
	
	// Capture the starting time, just prior to the start of the analysis
	long startTime = new Date().getTime();
	
	try{
	    f = new File(filename);
	    fout = new PrintWriter(new BufferedWriter(new FileWriter(f)));
	    fout.print("strict digraph COMTOR {\n");
	    fout.print("ranksep=3;\n");
	    fout.print("ratio=auto;\n");
	    //fout.print("bgcolor=black;\n");
	    fout.print("root=\""+GVNode.DEFAULT_ROOT_NODE_NAME+"\";\n");
	}catch(IOException ioex){
	    prop.setProperty("I/O Exception: error opening .dot output file",
			     ioex.getMessage());
	    //bail on output
	    outputFail = true;
	}
	
	if(!outputFail)
	{
	    GVNode rootNode = new GVNode();
	    rootNode.name = GVNode.DEFAULT_ROOT_NODE_NAME;
	    rootNode.commented = false;
	    rootNode.shape = "oval";
	    m_nodes.put(rootNode.name,
			rootNode);		    
	    classes = rootDoc.classes();
	    try
	    {
		for (int i = 0; i < classes.length; ++i) 
		{
		    int classNamePos = 0;
		    ClassDoc cd = classes[i];
		    GVNode classNode = new GVNode();
		    String s = cd.getRawCommentText();
		    String qualifiedEdge = new String(cd.qualifiedName());
		    
		    //classNode.name = cd.name();
		    classNode.name = cd.qualifiedName();
		    classNode.commented = (s!=null && !s.equals("")) ? true : false;
		    classNode.shape = "ellipse";
		    m_nodes.put(cd.qualifiedName(),
				classNode);
		    
		    //strip off last component of qualifiedEdge
		    classNamePos = qualifiedEdge.lastIndexOf("."+cd.name());
		    if(-1==classNamePos)
		    {
			//this is an error; cd.qualifiedName does not contain cd.name()
			System.err.println("analyze(): error: cd.qualifiedName does not contain cd.name");
		    }
		    
		    //this is "inclusive", so we go one _before_ the period char
		    qualifiedEdge = qualifiedEdge.substring(0, classNamePos);
		    
		    //replace periods with arrows
		    qualifiedEdge = qualifiedEdge.replace((CharSequence)".",
							  (CharSequence)"\"->\"");
		    
		    //need to replace last element (i.e., classname) with FQCN
		    //append cd.qualifiedName to qualified edge (with an arrow)
		    
		    qualifiedEdge += "\"->\"";
		    qualifiedEdge += cd.qualifiedName();
		    
		    m_edges.put(cd.qualifiedName(),
				qualifiedEdge);
		    
		    /* "manually" put in an edge from the rootNode to this FQCN */
		    m_edges.put(rootNode.name
				+"->"
				+cd.qualifiedName(),
				/* no leading quote needed here, as output routine puts it in */
				rootNode.name
				+"\""
				+"->"
				+"\""
				+qualifiedEdge);
		    
		    //if this is a constructor, then we need to duplicate the name within the edge
		    addMembers(cd.constructors(), "hexagon", true);
		    writeDotFile(fout);

		    addEdges(cd.qualifiedName(),
			     cd.name(),
			     cd.constructors(),
			     true);
		    writeDotFile(fout);
		    flushHash();
		    
		    //need to replace parent class name element with FQCN
		    addMembers(cd.fields(), "box", false);
		    writeDotFile(fout);
		    addEdges(cd.qualifiedName(),
			     cd.name(),
			     cd.fields(),
			     false);
		    writeDotFile(fout);
		    flushHash();
		    
		    addMembers(cd.methods(), "circle", true);
		    writeDotFile(fout);
		    addEdges(cd.qualifiedName(),
			     cd.name(),
			     cd.methods(),
			     true);
		    writeDotFile(fout);
		    flushHash();		    

		    classID++;
		}
	      
		fout.print("}\n");
		fout.close();
	    }catch(IOException ioex1){
		prop.setProperty("I/O Exception: error writing hashtables to .dot output file",
				 ioex1.getMessage());
		//bail on output
		outputFail = true;
	    }
	    
	}
	
	// Capture the ending time, just after the termination of the analysis
	long endTime = new Date().getTime();
	
	prop.setProperty("output filename", f.getAbsolutePath());
	prop.setProperty("metric1", "A total of " + classID + " class(es) were processed.");
	prop.setProperty("score", "" + getGrade());
	prop.setProperty("start time", Long.toString(startTime));
	prop.setProperty("end time", Long.toString(endTime));
	prop.setProperty("execution time", Long.toString(endTime - startTime));
	
	// Return the property list (report)
	return prop;
    }
    
    /** Empty m_nodes and m_edges */
    private void flushHash()
    {
	//nodex
	m_nodes.clear();
	m_edges.clear();
    }

    /**
     * Add edges to m_edges of the form:
     * &lt;\""package1.package2.class1" -&gt; "package1.package2.class1.field1";\"&gt;, "class1 -> field1"
     *
     * @param name - the brief name of the parent class
     * @param qualfix - the FQCN of the parent class
     */
    private void addEdges(String qualfix, 
			  String name, 
			  MemberDoc[] members,
			  boolean isRoutine)
    {
	//FALSE START 1:
	//tokenize the prefix down by '.'
	//see if any new path components are included
	//if so, insert into m_edges before handling the
	//actual fields, etc.
	//so if we've seen package 'a.b', and we now seen 'a.b.c.d', we
	//don't add "a" -> "b";, but only "b" -> "c" and "c" -> "d"
	//2. also add new nodes to m_nodes (uncommented) as needed
	//3. for new "firsts", add edge "." -> "<newfirst>";

	String dot = ".";
	String arrow = "\"->\"";
	String qualifiedEdge = "";
	int classNamePos = -1;

	if(null==members)
	    return;

	for (int i = 0; i<members.length;i++)
	{
	    //don't do this: qualifiedEdge = new String(members[i].qualifiedName());
	    //approach: need to chop off last component of qualifiedName from qualifiedEdge
	    //before appending members[i].qualifiedName()
	    qualifiedEdge = new String(qualfix);
	    classNamePos = qualifiedEdge.lastIndexOf("."+name);

	    if (-1==classNamePos )
	    {
		System.err.println("addEdges(): error: class name not included in FQCN");
	    }
	    //chop off classname
	    qualifiedEdge = qualifiedEdge.substring(0, classNamePos); 

	    //replace dots with arrows
	    qualifiedEdge = qualifiedEdge.replace((CharSequence)dot,
						  (CharSequence)arrow);
	    qualifiedEdge += "\"->\"";
	    qualifiedEdge += qualfix; //"full" name of class

	    if(isRoutine)
	    {
		ExecutableMemberDoc exmdoc = (ExecutableMemberDoc)members[i];

		if(exmdoc.qualifiedName().equals(qualfix))
		{
		    //this is a constructor; it needs a "doubling" of the classname
		    //fix: need to insert Nodes to m_nodes that match these generated names [DONE];
		    //also need to pay attention to the qualfix+qualfix+qualfix style
		    m_edges.put(exmdoc.qualifiedName()+qualfix+exmdoc.signature(),
				//qualifiedEdge+"\"->\""+qualfix+"\"->\""+qualfix+exmdoc.signature());
				qualifiedEdge+"\"->\""+qualfix+exmdoc.signature());
		}else{
		    m_edges.put(exmdoc.qualifiedName()+exmdoc.signature(),
				qualifiedEdge+"\"->\""+exmdoc.qualifiedName()+exmdoc.signature());
		}
	    }else{
		m_edges.put(members[i].qualifiedName(),
			    qualifiedEdge
			    +"\"->\""
			    +members[i].qualifiedName());
	    }
	}
	return;
    }

    /**
     * Accept all elements of the code, and enter a node into the hashtable for it.
     *
     * Caller should purge the hashtable (write output to a file and
     * then call <code>m_nodes.clear()</code> ) after the method completes.
     *
     * @param members - an array of javadoc-produced class members to iterate over
     *                  an have their names added to <code>m_nodes</code>
     * @param shape - a string that is eventually passed to graphviz
     * @param isRoutine - true if this member is a method or constructor
     */
    private void addMembers(MemberDoc[] members, 
			    String shape, 
			    boolean isRoutine)
    {
	GVNode node = null;

	for (int i = 0; i < members.length; ++i) 
	{
	    node = new GVNode();
	    //node.name = members[i].name();
	    node.name = members[i].qualifiedName();
	    node.shape = shape;
	    if( (null!=members[i].getRawCommentText()) &&
		!members[i].getRawCommentText().equals("") )
	    {
		node.commented = true;
	    }else{
		node.commented = false;
	    }
	    if(isRoutine)
	    {
		node.name+=((ExecutableMemberDoc)members[i]).signature();
		m_nodes.put(members[i].qualifiedName()
			    +((ExecutableMemberDoc)members[i]).signature(),
			    node);
	    }else{
		m_nodes.put(members[i].qualifiedName(),
			    node);
	    }
	}
	return;
    }

    /**
     * Take the accumulated hashtables and output their contents as
     * a graphviz DOT file.
     * 
     * @param fout the handle to the .dot file; assume this is already
     * open and available for writing. Caller assumes responsibility for
     * flushing and closing.
     */
    private void writeDotFile(PrintWriter fout)
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
	    if(node.commented)
		fout.print("\""+node.name+"\""+" [ label=\"\",shape=\""+node.shape+"\",style=\"filled\",color=\"green\" ];\n");
	    else
		fout.print("\""+node.name+"\""+" [ label=\"\",shape=\""+node.shape+"\",style=\"filled\",color=\"grey\" ];\n");
	}	

	//write edges
	while( edges.hasMoreElements() )
	{
	    edge = (String)edges.nextElement();
	    fout.print("\""+edge+"\""+" [ color=\"black\",arrowhead=\"dot\" ] ;\n");
	}
	return;
    }

	/*
	 * Sets the grading breakdown for the doclet.
	 *
	 * @param section Name of the section to set the max grade for
	 * @param maxGrade Maximum grade for the section
	 */
	public void setGradingBreakdown(String section, float maxGrade) {
		gradeBreakdown.put(section, new Float(maxGrade));
	}

	/**
	 * Returns the grade for the doclet.
	 *
	 * @return the overall grade for the doclet, as a float
	 */
	public float getGrade() {
		return 1.0f;
	}

	/**
	 * Sets a parameter used for doclet grading.
	 *
	 * @param param Name of the grading parameter
	 * @param value Value of the parameter
	 */
	public void setGradingParameter(String param, String value) {
		// Not needed for this analyzer
	}

	/**
	 * Sets the configuration properties loaded from the config file
	 *
	 * @param props Properties list
	 */
	public void setConfigProperties(Properties props) {
		// Not needed for this analyzer
	}

    /**
     * Returns a string representation of this object
     *
     * @return the string name of this analyzer
     */
    public String toString() {
	return "PercentTreeGV uses graphviz format to record the density of comments in a codebase.";
    }

    /**
     * Represents a node; holds meta-data for writing to the .dot
     * file, such as the shape that graphviz should draw for this
     * node. Each node represents a program element like a class,
     * constructor, data member (i.e., field), or method.
     *
     * In a perfect world, we should really employ a GVProgram library
     * whose toString creates the GV program, or has facilities for
     * gradually constructing a .dot file via a programmatic interface
     * (i.e., hiearchy of objects) to an edgemap or adjacency matrix.
     */
    private class GVNode
    {
	public static final String DEFAULT_ROOT_NODE_NAME = "centre";
	public String name = null;
	public boolean commented = false;
	public String shape = "box";
    }


}
