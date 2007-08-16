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
import java.util.HashMap;

import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiConnectionManager;
import org.xwiki.plugins.eclipse.rpc.exceptions.CommunicationException;
import org.xwiki.plugins.eclipse.rpc.impl.XWikiRPCHandler;

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
    private HashMap<String, IXWikiConnection> connections;

    /**
     * Private constructor. (singleton)
     */
    private XWikiConnectionManager()
    {
        connections = new HashMap<String, IXWikiConnection>();
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
        String proxy) throws CommunicationException
    {
        XWikiConnection con = new XWikiConnection();
        con.setServerUrl(serverUrl);
        con.setUserName(userName);
        con.setLoginToken(XWikiRPCHandler.getInstance().login(serverUrl, userName, password,
            proxy));
        connections.put(con.getLoginToken(), con);
        return con;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnectionManager#getAllConnections()
     */
    public Collection<IXWikiConnection> getAllConnections()
    {
        return connections.values();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnectionManager#removeConnection(java.lang.String)
     */
    public void removeConnection(String loginToken)
    {
        connections.remove(loginToken);
    }

}
