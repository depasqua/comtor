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

/**
 * The <code>ICodeStore</code> interface defines a series of methods
 * for storing and fetching pieces of source code text from a data
 * store.  The focus of this interface is on defining a narrow set of
 * interactions; clients and data stores on either side of this
 * interface should be free to do many specialized things.
 *
 * For example, an implementing class might provide a data store that
 * utilizes SQL to store the code in a relational database. This class
 * might have two clients: one that reads from SVN or CVS (or Javadoc)
 * and writes to the store and another that queries and fetches from
 * the store.
 *
 * Classes that implement this interface may store code in one of
 * the following code stores (or another of their choice...)
 *
 *  a file<br/>
 *  an in-memory hashtable<br/>
 *  a local or remote SQL database<br/>
 *  an AWS S3 bucket<br/>
 *  an AWS Simple DB instance<br/>
 *  ...
 *
 * The main way clients and stores communicate through this interface
 * is the CodeChunk object.
 *
 * @author Michael E. Locasto
 */
public interface ICodeStore
{
    
    /**
     * Is this <code>ICodeStore</code> properly initialized and ready
     * for communication?
     *
     * @returns boolean indicating whether the Code Store has been
     * properly initialized and successfully connected to its backing
     * storage.
     */
    public boolean isStoreInitialized();

    /**
     * Return the number of code chunks in this code store.
     *
     * @returns an integer indicating the number of code chunks
     */
    public int count();

    /**
     * Initialize this module; clients call this to make
     * sure that the CodeStore is ready for transporting
     * the data store service it should connect to.
     *
     * @assumption: implementing classes read this configuration
     * information from somewhere, like an XML config file,
     * command-line options, or user input.
     *
     * @returns boolean indicating whether the Code Store has been
     * properly initialized and successfully connected to its backing
     * storage.
     *
     * @param CodeStoreConf - configuration object; mainly present so we
     * avoid constantly rewriting this method signature for different
     * forms of authentication and connection.
     */
    public boolean initStore(CodeStoreConf config);

    /**
     * Disconnect from backing store and shut down.
     *
     * @return a boolean value indicating whether or not the shutdown was
     * successful
     */
    public boolean closeStore();

    /**
     * Push a piece of code to the data store
     * @param chunk - a {@link CodeChunk} object representing the piece
     * of code to be stored in the backing store
     */
    //public void storeChunk(CodeChunk chunk);
    public void store(CodeChunk chunk);

    /**
     * Associate the supplied {@link CodeNote} with this code chunk.
     * This is useful for the mode where multiple people in the
     * crowd are writing new comments or giving a rating. A
     * note can either be a comment or a rating.
     *
     * @param CodeNote - a note (i.e., comment) that should be
     * associated with this chunk of code.
     */
    public void annotate(CodeChunk chunk, CodeNote n);

    /**
     * Associate the supplied {@link CodeRating} with this code chunk.
     * This is useful for the mode where multiple people in the crowd
     * are writing new comments or giving a rating. A note can either
     * be a comment or a rating.
     *
     * @param CodeRating - a rating (i.e., score about a certain
     * characteristic) that should be associated with this chunk of
     * code.
     */
    public void rate(CodeChunk chunk, CodeRating r);

    /**
     * Grab a piece of code from the data store.
     *
     * @param name - a String representing the name of the code chunk to
     * fetch
     * @returns CodeChunk - a new {@link CodeChunk} representing the
     * chunk of code with the same name. Only returns first occurrence
     * in the case that multiple elements in the backing store match
     * this name. Order of matching is nondeterministic and depends on
     * the semantics of the backing store.
     */
    public CodeChunk fetch(String name);

    /**
     * Obtain all occurrences of this named item.
     */
    public ArrayList<CodeChunk> fetchMultiple(String name);

    /**
     * Grab a piece of code from the data store.
     */
    public CodeChunk fetch(int id);



}