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

package org.xwiki.plugins.eclipse.model.impl;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.IdentityObjectConvertor;
import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.codehaus.swizzle.confluence.SwizzleXWiki;
import org.eclipse.core.runtime.IPath;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiConnectionManager;
import org.xwiki.plugins.eclipse.util.CacheUtils;

/**
 * Default implementation of {@link IXWikiConnectionManager}.
 */
public class XWikiConnectionManager implements IXWikiConnectionManager
{

    /**
     * Shared instance of ConnectionManager.
     */
    private static XWikiConnectionManager privateInstance;

    /**
     * Currently established connections.
     */
    private List<IXWikiConnection> connections;

    /**
     * Private constructor. (singleton)
     */
    private XWikiConnectionManager()
    {
        connections = new LinkedList<IXWikiConnection>();
    }

    /**
     * @return Shared instance as a {@link IXWikiConnectionManager}
     */
    public static IXWikiConnectionManager getInstance()
    {
        if (privateInstance == null) {
            privateInstance = new XWikiConnectionManager();
        }
        return privateInstance;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnectionManager#connect(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public IXWikiConnection connect(String serverUrl, String userName, String password,
        String proxy) throws SwizzleConfluenceException
    {
        // TODO the proxy part needs to be done here!
        SwizzleXWiki rpc = new SwizzleXWiki(serverUrl);
        rpc.login(userName, password);
        // Workaround for finding out xwiki version (server)
        // (compatibility with <= XWiki 1.1.m4)
        try {
            rpc.setNoConversion();
        } catch (SwizzleConfluenceException e) {
            // Assume older version of xwiki and turn-off conversion on client.
            rpc.setConvertor(new IdentityObjectConvertor());
        }
        // Continue as usual.
        XWikiConnection conection = new XWikiConnection();
        conection.setServerUrl(serverUrl);
        conection.setUserName(userName);
        conection.setRpcProxy(rpc);
        // Initialize and update cache.
        Date timeStamp = new Date();
        IPath masterCacheDir = CacheUtils.getMasterCacheDirectory();
        IPath cachePath =
            masterCacheDir.addTrailingSeparator().append(String.valueOf(timeStamp.getTime()));
        conection.setCachePath(cachePath);
        CacheUtils.saveConnection(conection);
        conection.getCachePath().toFile().mkdir();
        connections.add(conection);
        return conection;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnectionManager#getAllConnections()
     */
    public Collection<IXWikiConnection> getAllConnections()
    {
        return connections;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnectionManager#removeConnection(Confluence)
     */
    public void removeConnection(IXWikiConnection connection)
    {
        connections.remove(connection);
    }

}
