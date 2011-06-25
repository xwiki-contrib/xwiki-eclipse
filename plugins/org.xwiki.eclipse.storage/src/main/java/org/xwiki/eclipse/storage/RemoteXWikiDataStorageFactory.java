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
 */
package org.xwiki.eclipse.storage;

import org.xwiki.eclipse.storage.utils.StorageUtils;

/**
 * @version $Id$
 */
public class RemoteXWikiDataStorageFactory
{

    /**
     * @param dataManager
     * @param endpoint
     * @param userName
     * @param password
     * @return
     */
    public static IRemoteXWikiDataStorage getRemoteXWikiDataStorage(DataManager dataManager, String endpoint,
        String userName, String password)
    {
        BackendType backend;
        try {
            backend = StorageUtils.getBackend(endpoint);
            switch (backend) {
                case XMLRPC:
                    IRemoteXWikiDataStorage xmlrpcRemoteStorage =
                        new XmlrpcRemoteXWikiDataStorageAdapter(dataManager, endpoint, userName, password);
                    return xmlrpcRemoteStorage;
                case REST:
                    IRemoteXWikiDataStorage restRemoteStorage =
                        new RestRemoteXWikiDataStorageAdapter(dataManager, endpoint, userName, password);
                    return restRemoteStorage;
                default:
                    break;
            }
        } catch (XWikiEclipseStorageException e) {
            e.printStackTrace();
        }

        return null;
    }
}
