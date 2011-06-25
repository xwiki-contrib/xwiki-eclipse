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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.swizzle.confluence.ServerInfo;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.xwiki.eclipse.model.ModelObject;
import org.xwiki.eclipse.model.XWikiEclipseServerInfo;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.model.XWikiEclipseWikiSummary;
import org.xwiki.eclipse.xmlrpc.XWikiEclipseXmlrpcException;
import org.xwiki.eclipse.xmlrpc.XmlrpcRemoteXWikiDataStorage;

/**
 * @version $Id$
 */
public class XmlrpcRemoteXWikiDataStorageAdapter implements IRemoteXWikiDataStorage
{
    private DataManager dataManager;

    private XmlrpcRemoteXWikiDataStorage xmlrpcRemoteDataStorage;

    /**
     * @param dataManager
     * @param endpoint
     * @param userName
     * @param password
     */
    public XmlrpcRemoteXWikiDataStorageAdapter(DataManager dataManager, String endpoint, String userName,
        String password)
    {
        this.dataManager = dataManager;

        try {
            xmlrpcRemoteDataStorage = new XmlrpcRemoteXWikiDataStorage(endpoint, userName, password);
        } catch (XWikiEclipseXmlrpcException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getRootResources()
     */
    @Override
    public List<ModelObject> getRootResources()
    {
        List<ModelObject> result = new ArrayList<ModelObject>();
        List<SpaceSummary> spaces;
        try {
            spaces = this.xmlrpcRemoteDataStorage.getSpaces();
            for (SpaceSummary spaceSummary : spaces) {
                XWikiEclipseSpaceSummary space = new XWikiEclipseSpaceSummary(dataManager);
                space.setKey(spaceSummary.getKey());
                space.setName(spaceSummary.getName());
                space.setUrl(spaceSummary.getUrl());

                result.add(space);
            }
            return result;

        } catch (XWikiEclipseXmlrpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getWikis()
     */
    @Override
    public List<XWikiEclipseWikiSummary> getWikis()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getSpaces()
     */
    @Override
    public List<XWikiEclipseSpaceSummary> getSpaces(XWikiEclipseWikiSummary wiki)
    {
        List<XWikiEclipseSpaceSummary> result = new ArrayList<XWikiEclipseSpaceSummary>();

        try {
            List<SpaceSummary> spaces = this.xmlrpcRemoteDataStorage.getSpaces();
            for (SpaceSummary spaceSummary : spaces) {
                XWikiEclipseSpaceSummary space = new XWikiEclipseSpaceSummary(dataManager);
                space.setKey(spaceSummary.getKey());
                space.setName(spaceSummary.getName());
                space.setUrl(spaceSummary.getUrl());

                result.add(space);
            }
            return result;
        } catch (XWikiEclipseXmlrpcException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#dispose()
     */
    @Override
    public void dispose()
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getServerInfo()
     */
    @Override
    public XWikiEclipseServerInfo getServerInfo()
    {
        XWikiEclipseServerInfo serverInfo = new XWikiEclipseServerInfo();

        try {
            ServerInfo info = xmlrpcRemoteDataStorage.getServerInfo();
            serverInfo.setBaseUrl(info.getBaseUrl());
            serverInfo.setMajorVersion(info.getMajorVersion());
            serverInfo.setMinorVersion(info.getMinorVersion());

            return serverInfo;
        } catch (XWikiEclipseXmlrpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
