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

import java.util.List;

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

    private XmlrpcRemoteXWikiDataStorage xmlrpcRemoteDataStorage;

    /**
     * @param endpoint
     * @param userName
     * @param password
     */
    public XmlrpcRemoteXWikiDataStorageAdapter(String endpoint, String userName, String password)
    {
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
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getSpaces()
     */
    @Override
    public List<XWikiEclipseSpaceSummary> getSpaces()
    {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return null;
    }

}
