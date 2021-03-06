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

/**
 * The <code>GVNode</code> class provides storage for the attributes
 * of a node that gets represented in a .dot source file.
 *
 * The main challenge here is in mapping the attributes of various
 * comments as derived by modules to the actual properties of graphviz
 * nodes.
 * 
 * @author Michael E. Locasto
 */
public class GVNode
{
    public String name = "";
    public String type = "";
    public String shape = "box";
    public String fillColor = "gray";
}