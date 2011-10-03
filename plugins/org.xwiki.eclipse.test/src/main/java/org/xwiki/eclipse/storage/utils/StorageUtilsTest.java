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
package org.xwiki.eclipse.storage.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.storage.DataManager;

/**
 * @version $Id$
 */
public class StorageUtilsTest
{

    /**
     * Test method for
     * {@link org.xwiki.eclipse.storage.utils.StorageUtils#writeToJson(org.eclipse.core.resources.IFile, java.lang.Object)}
     * .
     */
    @Test
    public void testWriteToJson()
    {
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

        IProject project = workspaceRoot.getProject("testProject");

        try {
            if (!project.exists()) {
                project.create(null);
            }
            project.open(null);

            IFile file = project.getFile(new Path(".").append("test.xeos"));

            DataManager manager = new DataManager(project);

            XWikiEclipseObjectSummary objectSummary = new XWikiEclipseObjectSummary(manager);
            objectSummary.setClassName("testClass");
            objectSummary.setNumber(1);
            objectSummary.setPageName("test");

            StorageUtils.writeToJson(file, objectSummary);
            Assert.assertTrue(file.exists());

            file.delete(true, null);
            Assert.assertTrue(!file.exists());
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReadFromJson()
    {
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

        IProject project = workspaceRoot.getProject("testProject");

        try {
            if (!project.exists()) {
                project.create(null);
            }

            project.open(null);

            IFile file = project.getFile(new Path(".").append("test.xeos"));

            DataManager manager = new DataManager(project);

            XWikiEclipseObjectSummary objectSummary = new XWikiEclipseObjectSummary(manager);
            objectSummary.setClassName("testClass");
            objectSummary.setNumber(1);
            objectSummary.setPageName("test");

            StorageUtils.writeToJson(file, objectSummary);
            Assert.assertTrue(file.exists());

            /* read json string */
            XWikiEclipseObjectSummary os =
                (XWikiEclipseObjectSummary) StorageUtils.readFromJSON(file,
                    XWikiEclipseObjectSummary.class.getCanonicalName());
            Assert.assertEquals("testClass", os.getClassName());
            Assert.assertEquals("test", os.getPageName());
            Assert.assertEquals(1, os.getNumber());

            file.delete(true, null);
            Assert.assertTrue(!file.exists());
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
