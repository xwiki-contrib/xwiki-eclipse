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

package org.xwiki.plugins.eclipse.model.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.IdentityObjectConvertor;
import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.codehaus.swizzle.confluence.SwizzleXWiki;
import org.eclipse.core.runtime.IPath;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiConnectionManager;
import org.xwiki.plugins.eclipse.model.IXWikiPage;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.util.CacheUtils;
import org.xwiki.plugins.eclipse.util.ICacheable;

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
        XWikiConnection conection = new XWikiConnection();
        conection.setServerUrl(serverUrl);
        conection.setUserName(userName);
        conection.setPassword(password);
        conection.setRpcProxy(rpc);
        IPath masterCacheDir = CacheUtils.getMasterCacheDirectory();
        IPath cachePath =
            masterCacheDir.addTrailingSeparator().append(String.valueOf(new Date().getTime()));
        conection.setCachePath(cachePath);
        CacheUtils.updateCache(conection);
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
        CacheUtils.clearCache(connection);
    }

    /**
     * Used by internal code to restore connections.
     * 
     * @param connection Connection to be added into store.
     */
    protected void addConnection(IXWikiConnection connection)
    {
        connections.add(connection);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnectionManager#restoreAllConnections()
     */
    public void restoreAllConnections() throws IOException, ClassNotFoundException
    {
        Map<IPath, ICacheable> connections =
            CacheUtils.readCache(CacheUtils.getMasterCacheDirectory().toFile());
        Set<IPath> connectionsKeySet = connections.keySet();
        for (IPath connectionCachePath : connectionsKeySet) {
            XWikiConnection connection = (XWikiConnection) (connections.get(connectionCachePath));
            connection.setCachePath(connectionCachePath);
            Map<IPath, ICacheable> spaces =
                CacheUtils.readCache(connection.getCachePath().toFile());
            HashMap<String, IXWikiSpace> spacesByKey = new HashMap<String, IXWikiSpace>();
            Set<IPath> spacesKeySet = spaces.keySet();
            for (IPath spaceCachepath : spacesKeySet) {
                XWikiSpace space = (XWikiSpace) (spaces.get(spaceCachepath));
                space.setCachePath(spaceCachepath);
                spacesByKey.put(space.getKey(), space);
                Map<IPath, ICacheable> pages =
                    CacheUtils.readCache(space.getCachePath().toFile());
                HashMap<String, IXWikiPage> pagesByID = new HashMap<String, IXWikiPage>();
                Set<IPath> pagesKeySet = pages.keySet();
                for (IPath pageCachePath : pagesKeySet) {
                    XWikiPage page = (XWikiPage) (pages.get(pageCachePath));
                    page.setCachePath(pageCachePath);
                    page.setSpace(space);
                    pagesByID.put(page.getId(), page);
                }
                space.setConnection(connection);
                space.setPages(pagesByID);
            }
            connection.setSpaces(spacesByKey);
            addConnection(connection);
        }
    }
}
