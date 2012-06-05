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
 * The <code>CodeStoreClient</code> class represents clients
 * of {@link ICodeStore} objects. The job of clients is to
 * read source code from some data source, parse it in a meaningful
 * way to understand its structure or semantics, and then store it
 * into
 * 
 * Usually this is accomplished by reading from some flat
 * representation (such as a flat source file), but may alternatively
 * involve reading the file from a source code management or revision
 * control system like cvs, svn, or git.
 * 
 * The abstract client (i.e., this class) provides some default
 * plumbing, but really requires a subclass to know how to read from a
 * specific data source.
 *
 * @author Michael E. Locasto
 */
public abstract class CodeStoreClient
{

}