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
package org.xwiki.eclipse.xmlrpc.storage;

import java.net.MalformedURLException;

import org.apache.xmlrpc.XmlRpcException;
import org.xwiki.eclipse.storage.AbstractXWikiClient;
import org.xwiki.xmlrpc.XWikiXmlRpcClient;

/**
 * 
 * @version $Id$
 */
public class XWikiXmlrpcClient extends AbstractXWikiClient
{
    private XWikiXmlRpcClient client;

    /**
     * @param serverUrl
     */
    public XWikiXmlrpcClient(String serverUrl)
    {
        super(serverUrl);
        try {
            this.client = new XWikiXmlRpcClient(serverUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractXWikiClient#login(java.lang.String, java.lang.String)
     */
    @Override
    public boolean login(String username, String password)
    {
        boolean result = false;
        
        try {
            this.client.login(username, password);
            result = true;
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractXWikiClient#logout()
     */
    @Override
    public boolean logout()
    {        
        try {
            return this.client.logout();
        } catch (XmlRpcException e) {
            e.printStackTrace();
            return false;
        }
    }

}
