/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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
package org.xwiki.xeclipse.model;

import java.io.File;

import org.xwiki.xeclipse.model.impl.XWikiCachedConnection;
import org.xwiki.xeclipse.model.impl.XWikiPlainConnection;

/**
 * A factory for creating connections.
 */
public final class XWikiConnectionFactory
{
    /**
     * Creates a cached connection.
     * 
     * @param serverUrl The URL of the XWiki XML RPC server that will be used by this connection
     *            manager.
     * @param userName The user name to be used when connecting to the remote XWiki instance.
     * @param cacheDir The directory to be used to store the local cache.
     * @return A connection object.
     * @throws XWikiConnectionException
     */
    public static IXWikiConnection createCachedConnection(String serverUrl, String userName,
        File cacheDir) throws XWikiConnectionException
    {
        return new XWikiCachedConnection(serverUrl, userName, cacheDir);
    }

    /**
     * Creates a plain connection.
     * 
     * @param serverUrl The URL of the XWiki XML RPC server that will be used by this connection
     *            manager.
     * @param userName The user name to be used when connecting to the remote XWiki instance.
     * @return A connection object.
     * @throws XWikiConnectionException
     */
    public static IXWikiConnection createPlainConnection(String serverUrl, String userName)
        throws XWikiConnectionException
    {
        return new XWikiPlainConnection(serverUrl, userName);
    }
}
