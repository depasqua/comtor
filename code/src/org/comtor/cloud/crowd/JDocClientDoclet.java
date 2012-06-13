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

import com.sun.javadoc.*;

/**
 * This doclet processes a code base (e.g., a single file, a jar file,
 * a directory), extracting the meta-data and data related to major
 * java class components: methods, fields, constructors, classes along
 * with the JavaDoc comment text associated with these components.
 *
 * It then creates a CodeChunk to represent this information and hands
 * the code chunk to its parent JDocClient via a callback. The parent
 * stores these chunks into its chosen data store.
 * 
 * @author Michael E. Locasto
 */
public final class JDocClientDoclet
    extends Doclet
{
    /** A reference to our owner */
    private static JDocClient m_owner = null;

    /** 
     * Construct CodeChunks, push to codestore via m_owner.
     */
    private static void storeMethods(MethodDoc [] m)
    {
	CodeChunk tmp = null;
	for(int i = 0; i<m.length; i++)
	{
	    tmp = JDocTranslator.translateMethod(m[i]);
	    m_owner.takeDelivery(tmp);
	}
	return;
    }

    /**
     * Construct CodeChunks from fields, push to code store via
     * m_owner reference
     *
     * @param f a FieldDoc array
     */
    private static void storeFields(FieldDoc [] f)
    {
	CodeChunk tmp = null;
	for(int i = 0; i<f.length; i++)
	{
	    tmp = JDocTranslator.translateField(f[i]);
	    m_owner.takeDelivery(tmp);
	}
	return;
    }

    /**
     * The entry point for processing provides an
     * object to access the various attributes of all
     * the classes being documented.
     *
     * @param rootDoc  the root of the documentation tree
     * @returns some boolean value used to indicate whatever you want
     */
    public static boolean start(RootDoc rootDoc)
    {
	MethodDoc[] methods = new MethodDoc[0];
	FieldDoc[] fields = new FieldDoc[0];
	ClassDoc[] classes = new ClassDoc[0];

	m_owner = JDocClient.getInstance("");

	if(null==m_owner)
	{
	    System.err.println("[JDocClientDoclet]: start(): m_owner was null");
	    return false;
	}

	classes = rootDoc.classes();
	for(int i=0;i<classes.length;i++)
	{
	    System.out.println(classes[i]);
	    methods = classes[i].methods();
	    storeMethods(methods);
	    fields = classes[i].fields();
	    storeFields(fields);
	}
	
	return true;
    }
}