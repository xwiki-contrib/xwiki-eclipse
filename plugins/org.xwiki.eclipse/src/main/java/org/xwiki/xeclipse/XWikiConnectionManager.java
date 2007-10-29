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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.XWikiConnectionException;

public class XWikiConnectionManager
{    
    private static XWikiConnectionManager sharedInstance;

    private List<IXWikiConnection> xwikiConnections;
    private Map<String, String> idToPasswordMapping;

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
        idToPasswordMapping = new HashMap<String, String>();
        connectionManagerListenerList = new ListenerList();
    }

    public List<IXWikiConnection> getConnections()
    {
        return xwikiConnections;
    }

    public void addConnection(IXWikiConnection xwikiConnection, String password)
    {
        if (!xwikiConnections.contains(xwikiConnection)) {
            xwikiConnections.add(xwikiConnection);
            idToPasswordMapping.put(xwikiConnection.getId(), password);
            fireConnectionAdded(xwikiConnection);
        }
    }    

    public void removeConnection(IXWikiConnection xwikiConnection)
    {
        xwikiConnections.remove(xwikiConnection);
        fireConnectionRemoved(xwikiConnection);
    }
    
    public String getPasswordForConnection(String id) {
        return idToPasswordMapping.get(id);
    }
    
    public void saveConnections(File output) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(output));
        oos.writeObject(xwikiConnections);
        
        /* This should be written with some encryption mechanism */
        oos.writeObject(idToPasswordMapping);
        
        oos.close();
    }
    
    @SuppressWarnings("unchecked")
    public void restoreConnections(File input) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(input));
        xwikiConnections = (List<IXWikiConnection>) ois.readObject();
        idToPasswordMapping = (Map<String, String>) ois.readObject();
        ois.close();        
    }
    
    public void dispose() {
        for(IXWikiConnection xwikiConnecton : xwikiConnections) {
            try {
                xwikiConnecton.dispose();
            } catch (XWikiConnectionException e) {             
                e.printStackTrace();
            }
        }
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
