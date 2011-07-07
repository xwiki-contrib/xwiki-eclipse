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

import org.junit.Assert;
import org.junit.Test;
import org.xwiki.rest.model.jaxb.PageSummary;
import org.xwiki.rest.model.jaxb.Space;
import org.xwiki.rest.model.jaxb.Wiki;

/**
 * @version $Id$
 */
public class XWikiRESTClientTest
{

    @Test
    public void testGetPages()
    {
        String serverUrl = "http://localhost:8080/xwiki/rest";
        String pagesUrl = "http://localhost:8080/xwiki/rest/wikis/wiki1/spaces/myspace/pages";
        String username = "Admin";
        String password = "admin";
        XWikiRESTClient client = new XWikiRESTClient(serverUrl, username, password);
        try {
            List<PageSummary> pages = client.getPages(pagesUrl);
            Assert.assertNotNull(pages);
            Assert.assertTrue(pages.size() > 0);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Test
    public void testGetSpaces()
    {
        String serverUrl = "http://localhost:8080/xwiki/rest";
        String spacesUrl = "http://localhost:8080/xwiki/rest/wikis/wiki1/spaces";
        String username = "Admin";
        String password = "admin";
        XWikiRESTClient client = new XWikiRESTClient(serverUrl, username, password);
        try {
            List<Space> spaces = client.getSpaces(spacesUrl);
            Assert.assertNotNull(spaces);
            Assert.assertTrue(spaces.size() > 0);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Test
    public void testLogin()
    {
        String serverUrl = "http://localhost:8080/xwiki/rest";
        String username = "Admin";
        String password = "admin";
        XWikiRESTClient client = new XWikiRESTClient(serverUrl, username, password);
        Assert.assertTrue(client.login(username, password));
    }

    @Test
    public void testGetWikis()
    {
        String serverUrl = "http://localhost:8080/xwiki/rest";
        String username = "Admin";
        String password = "admin";
        XWikiRESTClient client = new XWikiRESTClient(serverUrl, username, password);
        try {
            List<Wiki> wikis = client.getWikis(username, password);
            Assert.assertNotNull(wikis);
            Assert.assertTrue(wikis.size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
