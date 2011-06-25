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
package org.xwiki.eclipse.rest;

import java.util.List;

import org.xwiki.rest.model.jaxb.Space;
import org.xwiki.rest.model.jaxb.Wiki;
import org.xwiki.rest.model.jaxb.Xwiki;

/**
 * @version $Id$
 */
public class RestRemoteXWikiDataStorage
{
    private XWikiRESTClient restRemoteClient;

    private String endpoint;

    private String username;

    private String password;

    public RestRemoteXWikiDataStorage(String endpoint, String username, String password)
    {
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
        this.restRemoteClient = new XWikiRESTClient(endpoint, username, password);
    }

    /**
     * @param username
     * @param password
     * @return
     */
    public List<Wiki> getWikis(String username, String password) throws Exception
    {

        try {
            return restRemoteClient.getWikis(username, password);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
    }

    public Xwiki getServerInfo()
    {
        return restRemoteClient.getServerInfo();
    }

    public List<Space> getSpaces(String spacesUrl, String username, String password)
    {
        try {
            return restRemoteClient.getSpaces(spacesUrl, username, password);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
