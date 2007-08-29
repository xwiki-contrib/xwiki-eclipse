/*
 * Copyright 2006-2007, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package org.xwiki.plugins.eclipse.model;

import java.util.Collection;

import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.SwizzleConfluenceException;

/**
 * A connection (session...) to a XWiki server.
 * Each connection is uniquely identified by a loginToken. 
 */
public interface IXWikiConnection
{
    /**
     * Initializes this connection by retrieving all spaces.
     * 
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public void init() throws SwizzleConfluenceException;

    /**
     * @return Username.
     */
    public String getUserName();

    /**
     * @return Server url.
     */
    public String getServerUrl();

    /**
     * @return The XML-RPC proxy used by this connection
     */
    public Confluence getRpcProxy();

    /**
     * Retrieves and returns all available spaces.
     * 
     * @return All spaces for this connection as a collection of IXwikiSpaces.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public Collection<IXWikiSpace> getSpaces();

    /**
     * Closes this connection.
     * 
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public void disconnect() throws SwizzleConfluenceException;

    /**
     * @param spaceKey Key of the space.
     * @return The space with corresponding key or null if no such space exists.
     */
    public IXWikiSpace getSpace(String spaceKey);

    /**
     * @return True if spaces have been retrieved, otherwise returns false.
     */
    public boolean isSpacesReady();

    /**
     * Adds a new space to this connection with given parameters (minimal).
     * 
     * @param name Name of the space to be added.
     * @param key Key for the new space.
     * @param description Description of the new space (may contain html stuff).
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public void addSpace(String name, String key, String description)
        throws SwizzleConfluenceException;

    /**
     * Removes the specified space from this connection.
     * 
     * @param key Key of the space which is to be removed.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public void removeSpace(String key) throws SwizzleConfluenceException;
}
