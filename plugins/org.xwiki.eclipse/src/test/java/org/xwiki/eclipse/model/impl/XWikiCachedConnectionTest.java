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
package org.xwiki.eclipse.model.impl;

import java.io.File;
import java.util.Collection;
import java.util.Date;

import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.PageSummary;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.model.IXWikiSpace;
import org.xwiki.eclipse.model.XWikiConnectionException;
import org.xwiki.eclipse.model.impl.IXWikiCacheDAO;
import org.xwiki.eclipse.model.impl.IXWikiDAO;
import org.xwiki.eclipse.model.impl.XWikiCachedConnection;
import org.xwiki.eclipse.model.impl.XWikiDAOException;
import org.xwiki.eclipse.model.impl.XWikiRemoteDAO;

public class XWikiCachedConnectionTest
{
    private XWikiCachedConnection connection;

    @Before
    public void setup() throws XWikiConnectionException
    {
        connection =
            new XWikiCachedConnection(Constants.SERVER_URL,
                Constants.USERNAME,
                new File(Constants.CACHE_DIR));
        connection.connect(Constants.PASSWORD);
    }

    /*
     * This test simply check if the connection flag is well set.
     */
    @Test
    public void connectionTest() throws XWikiDAOException, XWikiConnectionException
    {
        Assert.assertTrue(connection.isConnected());
    }

    /*
     * This test retrieves all the pages of the first remote space and checks that they are
     * correctly cached.
     */
    @Test
    public void getPagesTest() throws XWikiConnectionException, XWikiDAOException
    {
        Collection<IXWikiSpace> spaces = connection.getSpaces();
        IXWikiSpace space = spaces.iterator().next();
        Collection<IXWikiPage> pages = space.getPages();
        for (IXWikiPage page : pages) {
            page.getContent();
        }

        IXWikiDAO remoteDAO = connection.getRemoteDAO();
        IXWikiCacheDAO cacheDAO = connection.getCacheDAO();

        /*
         * This doesn't work anymore because cacheDAO.getSpaces() return only spaces that have some
         * cached pages inside
         */
        /* Check that all the remote space summaries are cached */
        // List<SpaceSummary> remoteSpaceSummaries = remoteDAO.getSpaces();
        // List<SpaceSummary> cachedSpaceSummaries = cacheDAO.getSpaces();
        // Assert.assertEquals(remoteSpaceSummaries.size(), cachedSpaceSummaries.size());
        //        
        // for(SpaceSummary remoteSpaceSummary: remoteSpaceSummaries) {
        // boolean found = false;
        // for(SpaceSummary cachedSpaceSummary: cachedSpaceSummaries) {
        // if(remoteSpaceSummary.toMap().equals(cachedSpaceSummary.toMap())) {
        // found = true;
        // break;
        // }
        // }
        // Assert.assertTrue(found);
        // }
        /* Check that all the pages that have been got have been cached as well */
        for (PageSummary pageSummary : remoteDAO.getPages(space.getKey())) {
            Page remotePage = remoteDAO.getPage(pageSummary.getId());
            Page cachedPage = cacheDAO.getPage(pageSummary.getId());
            Assert.assertEquals(remotePage.toMap(), cachedPage.toMap());
        }
    }

    /*
     * This test retrieve a page, modify it locally, synchronize with the server and checks that the
     * page contents are the same.
     */
    @Test
    public void synchronizationTest() throws XWikiConnectionException
    {
        Collection<IXWikiSpace> spaces = connection.getSpaces();
        IXWikiSpace space = spaces.iterator().next();
        Collection<IXWikiPage> pages = space.getPages();
        IXWikiPage page = connection.getPage(pages.iterator().next().getId());

        String workingPageId = page.getId();

        connection.disconnect();

        String newContent = "Test content " + new Date();
        page.setContent(newContent);
        page.save();

        Assert.assertTrue(page.isDirty());

        connection.connect("test");

        IXWikiPage remotePage = connection.getPage(workingPageId);
        Assert.assertEquals(remotePage.getContent(), newContent);
    }

    /*
     * This test simulates a concurrent local/remote modification that generates a conflict, and its
     * resolution.
     */
    @Test
    public void conflictTest() throws XWikiConnectionException, XWikiDAOException
    {
        // Create a "parallel" connection.
        XWikiRemoteDAO remoteDAO =
            new XWikiRemoteDAO("http://localhost:8080/xwiki/xmlrpc/confluence", "test", "test");

        Collection<IXWikiSpace> spaces = connection.getSpaces();
        IXWikiSpace space = spaces.iterator().next();
        Collection<IXWikiPage> pages = space.getPages();
        IXWikiPage page = connection.getPage(pages.iterator().next().getId());

        String workingPageId = page.getId();

        connection.disconnect();

        String newLocalContent = "Locally modified content" + new Date();
        page.setContent(newLocalContent);
        page.save();

        // Modify the page remotely on the "parallel" connection.
        Page remotePage = remoteDAO.getPage(workingPageId);
        remotePage.setContent("Remotely modified content" + new Date());
        remoteDAO.storePage(remotePage);
        remotePage = remoteDAO.getPage(workingPageId);

        // Reopen the connection and synchronize.
        connection.connect("test");

        page = connection.getPage(workingPageId);
        Assert.assertTrue(page.isDirty());
        Assert.assertTrue(page.isConflict());
        Assert.assertEquals(remotePage.getVersion(), page.getVersion());

        // Modify the page and save it again.
        String conflictResolutionContent = "Conflict resolved" + new Date();
        page.setContent(conflictResolutionContent);
        page.save();

        // Retrieve again the page both via the connection manager and the "parallel" connection
        page = connection.getPage(workingPageId);
        remotePage = remoteDAO.getPage(workingPageId);

        Assert.assertFalse(page.isDirty());
        Assert.assertFalse(page.isConflict());
        Assert.assertEquals(page.getVersion(), remotePage.getVersion());
        Assert.assertEquals(page.getContent(), remotePage.getContent());
    }

    @After
    public void tearDown() throws XWikiConnectionException
    {
        connection.disconnect();
        connection.dispose();
        connection = null;
    }
}
