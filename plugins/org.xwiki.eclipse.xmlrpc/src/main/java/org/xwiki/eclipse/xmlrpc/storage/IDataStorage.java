/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */
package org.xwiki.eclipse.xmlrpc.storage;

import java.util.List;

import org.codehaus.swizzle.confluence.SpaceSummary;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;
import org.xwiki.xmlrpc.model.XWikiClass;
import org.xwiki.xmlrpc.model.XWikiClassSummary;
import org.xwiki.xmlrpc.model.XWikiObject;
import org.xwiki.xmlrpc.model.XWikiObjectSummary;
import org.xwiki.xmlrpc.model.XWikiPage;
import org.xwiki.xmlrpc.model.XWikiPageHistorySummary;
import org.xwiki.xmlrpc.model.XWikiPageSummary;

/**
 * An interface for an abstract XWiki data storage component.
 */
interface IDataStorage
{
    void dispose();

    List<SpaceSummary> getSpaces() throws XWikiEclipseStorageException;

    SpaceSummary getSpaceSumary(String spaceKey) throws XWikiEclipseStorageException;

    void removeSpace(String spaceKey) throws XWikiEclipseStorageException;

    List<XWikiPageSummary> getPages(String spaceKey) throws XWikiEclipseStorageException;

    XWikiPageSummary getPageSummary(String pageId) throws XWikiEclipseStorageException;

    XWikiPage getPage(String pageId) throws XWikiEclipseStorageException;

    boolean removePage(String pageId) throws XWikiEclipseStorageException;

    List<XWikiObjectSummary> getObjects(String pageId) throws XWikiEclipseStorageException;

    XWikiObject getObject(String pageId, String className, int objectId) throws XWikiEclipseStorageException;

    List<XWikiClassSummary> getClasses() throws XWikiEclipseStorageException;

    XWikiClass getClass(String classId) throws XWikiEclipseStorageException;

    XWikiPage storePage(XWikiPage page) throws XWikiEclipseStorageException;

    XWikiObject storeObject(XWikiObject object) throws XWikiEclipseStorageException;

    boolean removeObject(String pageId, String className, int objectId) throws XWikiEclipseStorageException;

    void storeClass(XWikiClass xwikiClass) throws XWikiEclipseStorageException;

    boolean exists(String pageId);

    boolean exists(String pageId, String className, int objectId);

    List<XWikiPageHistorySummary> getPageHistory(String pageId) throws XWikiEclipseStorageException;

    List<XWikiPageSummary> getAllPageIds() throws XWikiEclipseStorageException;
}
