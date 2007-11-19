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
package org.xwiki.eclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xwiki.eclipse.model.IXWikiConnection;
import org.xwiki.eclipse.model.XWikiConnectionException;

/**
 * This singleton manages all the active connections
 */
public class XWikiConnectionManager
{
    private static XWikiConnectionManager sharedInstance;

    private List<IXWikiConnection> xwikiConnections;

    private Map<String, String> idToPasswordMapping;

    /**
     * @return The shared instance of the singleton.
     */
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
    }

    /**
     * @return A list of all registered connections.
     */
    public List<IXWikiConnection> getConnections()
    {
        return xwikiConnections;
    }

    /**
     * Add a connection.
     * 
     * @param xwikiConnection The connection to be added.
     * @param password The password to be used for this connection.
     */
    public void addConnection(IXWikiConnection xwikiConnection, String password)
    {
        if (!xwikiConnections.contains(xwikiConnection)) {
            xwikiConnections.add(xwikiConnection);
            idToPasswordMapping.put(xwikiConnection.getId(), password);

            XWikiEclipseNotificationCenter.getDefault().fireEvent(this,
                XWikiEclipseEvent.CONNECTION_ADDED, xwikiConnection);
        }
    }

    /**
     * Remove a connection.
     * 
     * @param xwikiConnection The connection to be removed.
     */
    public void removeConnection(IXWikiConnection xwikiConnection)
    {
        xwikiConnections.remove(xwikiConnection);
        XWikiEclipseNotificationCenter.getDefault().fireEvent(this,
            XWikiEclipseEvent.CONNECTION_REMOVED, xwikiConnection);
    }

    /**
     * Get the password associated to a given connection.
     * 
     * @param xwikiConnection The connection object.
     * @return The password associated to the given connection.
     */
    public String getPasswordForConnection(IXWikiConnection xwikiConnection)
    {
        return idToPasswordMapping.get(xwikiConnection.getId());
    }

    /**
     * Serialize registered connections to persistent storage.
     * 
     * @param output The output file where to write the serialized data.
     * @throws Exception
     */
    public void saveConnections(File output) throws Exception
    {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(output));
        oos.writeObject(xwikiConnections);

        /* This should be written with some encryption mechanism */
        oos.writeObject(idToPasswordMapping);

        oos.close();
    }

    /**
     * Restore previously registered connections.
     * 
     * @param input The file from where reading connection data.
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void restoreConnections(File input) throws Exception
    {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(input));
        xwikiConnections = (List<IXWikiConnection>) ois.readObject();
        idToPasswordMapping = (Map<String, String>) ois.readObject();
        ois.close();
    }

    /**
     * Close all registered connections. This method should be called before quitting the
     * application in order to properly dispose all the resources associated to the connections.
     */
    public void dispose()
    {
        for (IXWikiConnection xwikiConnecton : xwikiConnections) {
            try {
                xwikiConnecton.dispose();
            } catch (XWikiConnectionException e) {
                e.printStackTrace();
            }
        }
    }
}
