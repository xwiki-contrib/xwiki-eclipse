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

import java.net.MalformedURLException;

import org.apache.xmlrpc.XmlRpcException;
import org.xwiki.eclipse.rest.XWikiRESTClient;
import org.xwiki.eclipse.storage.utils.StorageUtils;
import org.xwiki.xmlrpc.XWikiXmlRpcClient;

/**
 * @version $Id$
 */
public class XWikiClient
{
    private XWikiXmlRpcClient xmlrpcClient = null;

    private XWikiRESTClient restClient = null;

    public XWikiClient(String serverUrl, String username, String password)
    {

        BackendType backend;
        try {
            backend = StorageUtils.getBackend(serverUrl);
            switch (backend) {
                case XMLRPC:
                    this.xmlrpcClient = new XWikiXmlRpcClient(serverUrl);
                    break;
                case REST:
                    this.restClient = new XWikiRESTClient(serverUrl, username, password);
                    break;
                default:
                    break;
            }
        } catch (XWikiEclipseStorageException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String username, String password) throws XWikiEclipseStorageException
    {
        boolean result = false;
        if (this.xmlrpcClient != null) {
            try {
                this.xmlrpcClient.login(username, password);
                result = true;
            } catch (Exception e) {
                throw new XWikiEclipseStorageException(e);
            }

            return result;

        }

        if (this.restClient != null) {
            result = this.restClient.login(username, password);
            return result;
        }

        return false;
    }

    public boolean logout() throws XWikiEclipseStorageException
    {
        if (this.xmlrpcClient != null) {
            try {
                return this.xmlrpcClient.logout();
            } catch (XmlRpcException e) {
                e.printStackTrace();
                throw new XWikiEclipseStorageException(e);
            }
        }

        if (this.restClient != null) {
            return this.restClient.logout();
        }

        return false;
    }
}
