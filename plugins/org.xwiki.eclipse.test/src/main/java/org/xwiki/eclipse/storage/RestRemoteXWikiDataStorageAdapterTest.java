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

import java.util.Calendar;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.xwiki.eclipse.model.XWikiEclipsePage;

/**
 * @version $Id$
 */
public class RestRemoteXWikiDataStorageAdapterTest
{

    /**
     * Test method for
     * {@link org.xwiki.eclipse.storage.RestRemoteXWikiDataStorageAdapter#storePage(org.xwiki.eclipse.model.XWikiEclipsePage)}
     * .
     */
    @Test
    public void testStorePage()
    {
        String serverUrl = "http://localhost:8080/xwiki/rest";

        String username = "XWiki.Admin";
        String password = "admin";

        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

        IProject project = workspaceRoot.getProject("testProject");

        try {
            if (!project.exists()) {
                project.create(null);
            }

            project.open(null);

            DataManager dataManager = new DataManager(project);

            IRemoteXWikiDataStorage remoteXWikiDataStorage =
                RemoteXWikiDataStorageFactory.getRemoteXWikiDataStorage(dataManager, serverUrl, username, password);

            XWikiEclipsePage page = new XWikiEclipsePage(dataManager);
            page.setFullName("test.test");
            page.setId("myspace:test.test");
            page.setName("test");
            page.setSpace("myspace");
            page.setTitle("test title");
            page.setWiki("xwiki");
            page.setContent("test content");
            page.setCreated(Calendar.getInstance());
            page.setCreator(username);
            page.setLanguage("");
            page.setMajorVersion(1);
            page.setMinorVersion(1);
            page.setModified(Calendar.getInstance());
            page.setModifier(username);
            page.setParentId("");
            page.setVersion("1.1");

            XWikiEclipsePage storedPage = remoteXWikiDataStorage.storePage(page);
            Assert.assertNotNull(storedPage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown()
    {
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

        IProject project = workspaceRoot.getProject("testProject");
        try {
            project.delete(true, null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(!project.exists());
    }
}
