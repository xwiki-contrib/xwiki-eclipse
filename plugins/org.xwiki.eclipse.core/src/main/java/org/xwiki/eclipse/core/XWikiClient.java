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
package org.xwiki.eclipse.core;

import org.xwiki.eclipse.storage.AbstractXWikiClient;
import org.xwiki.eclipse.storage.BackendType;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;
import org.xwiki.eclipse.xmlrpc.storage.XWikiXmlrpcClient;

/**
 * 
 * @version $Id$
 */
public class  XWikiClient
{
    private AbstractXWikiClient client;
    
    public XWikiClient(String serverUrl, String backendType) {
        BackendType backend = BackendType.valueOf(backendType);
        switch (backend) {
            case xmlrpc:
                this.client = new XWikiXmlrpcClient(serverUrl);
                break;
            case rest:
                throw new UnsupportedOperationException();
//                break;
            default:
                break;
        }        
    }
    
    public void login(String username, String password) throws XWikiEclipseException {        
        try {
            this.client.login(username, password);
        } catch (XWikiEclipseStorageException e) {
            e.printStackTrace();
            throw new XWikiEclipseException(e);
        }
    }
    
    public boolean logout() {
        return this.client.logout();
    }
}
