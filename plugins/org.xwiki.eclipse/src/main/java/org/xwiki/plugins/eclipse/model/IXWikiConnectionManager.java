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

import org.codehaus.swizzle.confluence.SwizzleConfluenceException;

/**
 * Responsible for making and managing {@link IXWikiConnection} objects.
 */
public interface IXWikiConnectionManager
{
    /**
     * creates a new IXWikiConnection object.
     * 
     * @param serverUrl XWiki server url (Ex http://somehost/xwiki/xmlrpc/confluence).
     * @param userName User name.
     * @param password Password.
     * @param proxy If a proxy is used (Ex http://somehost:someport).
     * @return An instance of IXWikiConnection upon success.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public IXWikiConnection connect(String serverUrl, String userName, String password,
        String proxy) throws SwizzleConfluenceException;

    /**
     * @return All live connections.
     */
    public Collection<IXWikiConnection> getAllConnections();

    /**
     * <p>
     * Removes the connection from pool (does not disconnect). it is assumed the connection has been
     * closed at this point.
     * </p>
     * 
     * @param connection The connection to be closed.
     */
    public void removeConnection(IXWikiConnection connection);
}
