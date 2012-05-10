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
    public Properties analyze(RootDoc rootDoc) {
	// A counter for the classes, used in the properties list
	int classID = 0;
	DecimalFormat formatter = new DecimalFormat("##0000.000");	
	File f = null;
	PrintWriter fout = null;
	boolean outputFail = false;
	//string filename = "comment-density-"+System.currentTimeMillis()+".dot";
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
	    
	    for (int i = 0; i < classes.length; ++i) 
	    {
		ClassDoc cd = classes[i];
		GVNode classNode = new GVNode();
		String s = cd.getRawCommentText();
		String qualifiedEdge = new String(cd.qualifiedName());

		classNode.name = cd.name();
		classNode.commented = (s!=null && !s.equals("")) ? true : false;
		classNode.shape = "ellipse";
		m_nodes.put(cd.qualifiedName(),
			    classNode);

		qualifiedEdge = qualifiedEdge.replace((CharSequence)".",
						      (CharSequence)"\"->\"");
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
		addEdges(cd.qualifiedName(),
			 cd.name(),
			 cd.constructors(),
			 true);

		addMembers(cd.fields(), "box", false);
		addEdges(cd.qualifiedName(),
			 cd.name(),
			 cd.fields(),
			 false);
				
		addMembers(cd.methods(), "circle", true);
		addEdges(cd.qualifiedName(),
			 cd.name(),
			 cd.methods(),
			 true);
		
		classID++;
	    }
	    
	    try
	    {
		writeDotFile(fout);
		
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

	if(null==members)
	    return;

	for (int i = 0; i<members.length;i++)
	{
	    qualifiedEdge = new String(members[i].qualifiedName());
	    qualifiedEdge = qualifiedEdge.replace((CharSequence)dot,
						  (CharSequence)arrow);
	    if(isRoutine)
	    {
		ExecutableMemberDoc exmdoc = (ExecutableMemberDoc)members[i];

		if(exmdoc.qualifiedName().equals(qualfix))
		{
		    //this is a constructor; it needs a "doubling" of the classname
		    m_edges.put(exmdoc.qualifiedName()+name+exmdoc.signature(),
				qualifiedEdge+"\"->\""+name+exmdoc.signature());
		}else{
		    m_edges.put(exmdoc.qualifiedName()+exmdoc.signature(),
				qualifiedEdge+exmdoc.signature());
		}
	    }else{
		m_edges.put(members[i].qualifiedName(),
			    qualifiedEdge);
	    }
	}

	return;
    }

    /**
     * Accept all elements of the code, and enter a node into the hashtable for it.
     *
     * @param members
     */
    private void addMembers(MemberDoc[] members, 
			    String shape, 
			    boolean isRoutine)
    {
	GVNode node = null;

	for (int i = 0; i < members.length; ++i) 
	{
	    node = new GVNode();
	    node.name = members[i].name();
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
     * Take the accumulated hashtable and output its contents as
     * a graphviz DOT file.
     * 
     * @param f the handle to the .dot file
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

    private class GVNode
    {
	public static final String DEFAULT_ROOT_NODE_NAME = "centre";
	public String name = null;
	public boolean commented = false;
	public String shape = "box";
    }

    //should really have a private GVProgram class whose toString
    //creates the GV program.

}
