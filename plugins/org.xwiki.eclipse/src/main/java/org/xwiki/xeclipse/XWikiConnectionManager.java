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
package org.xwiki.xeclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.xwiki.xeclipse.model.IXWikiConnection;

public class XWikiConnectionManager
{    
    private static XWikiConnectionManager sharedInstance;

    private List<IXWikiConnection> xwikiConnections;

    private ListenerList connectionManagerListenerList;

    public static XWikiConnectionManager getDefault()
    {
        if (sharedInstance == null) {
            sharedInstance = new XWikiConnectionManager();
        }

        return sharedInstance;
    }

    private XWikiConnectionManager()
    {
        xwikiConnections = new ArrayList<IXWikiConnection>();
        connectionManagerListenerList = new ListenerList();
    }

    public List<IXWikiConnection> getConnections()
    {
        return xwikiConnections;
    }

    public void addConnection(IXWikiConnection xwikiConnection)
    {
        if (!xwikiConnections.contains(xwikiConnection)) {
            xwikiConnections.add(xwikiConnection);
            fireConnectionAdded(xwikiConnection);
        }
    }

    public void removeConnection(IXWikiConnection xwikiConnection)
    {
        xwikiConnections.remove(xwikiConnection);
        fireConnectionRemoved(xwikiConnection);
    }

    // /////////////////////////// Event listeners management /////////////////////////////

    public void addConnectionManagerListener(IXWikiConnectionManagerListener listener)
    {
        connectionManagerListenerList.add(listener);
    }

    public void removeConnectionManagerListener(IXWikiConnectionManagerListener listener)
    {
        connectionManagerListenerList.remove(listener);
    }

    protected void fireConnectionAdded(final IXWikiConnection xwikiConnection)
    {
        if (xwikiConnection == null) {
            throw new NullPointerException();
        }

        final Object[] listeners = connectionManagerListenerList.getListeners();
        for (int i = 0; i < listeners.length; i++) {
            final IXWikiConnectionManagerListener listener =
                (IXWikiConnectionManagerListener) listeners[i];
            listener.connectionAdded(xwikiConnection);
        }
    }

    protected void fireConnectionRemoved(final IXWikiConnection xwikiConnection)
    {
        if (xwikiConnection == null) {
            throw new NullPointerException();
        }

        final Object[] listeners = connectionManagerListenerList.getListeners();
        for (int i = 0; i < listeners.length; i++) {
            final IXWikiConnectionManagerListener listener =
                (IXWikiConnectionManagerListener) listeners[i];
            listener.connectionRemoved(xwikiConnection);
        }
    }

}
