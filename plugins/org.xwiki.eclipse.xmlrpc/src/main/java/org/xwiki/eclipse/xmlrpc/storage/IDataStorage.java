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
package org.xwiki.eclipse.xmlrpc.storage;

import java.util.List;

import org.codehaus.swizzle.confluence.SpaceSummary;
import org.xwiki.eclipse.xmlrpc.XWikiEclipseXmlrpcException;
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
public interface IDataStorage
{
    public void dispose();

    public List<SpaceSummary> getSpaces() throws XWikiEclipseXmlrpcException;

    public SpaceSummary getSpaceSumary(String spaceKey) throws XWikiEclipseXmlrpcException;

    public void removeSpace(String spaceKey) throws XWikiEclipseXmlrpcException;

    public List<XWikiPageSummary> getPages(String spaceKey) throws XWikiEclipseXmlrpcException;

    public XWikiPageSummary getPageSummary(String pageId) throws XWikiEclipseXmlrpcException;

    public XWikiPage getPage(String pageId) throws XWikiEclipseXmlrpcException;

    public boolean removePage(String pageId) throws XWikiEclipseXmlrpcException;

    public List<XWikiObjectSummary> getObjects(String pageId) throws XWikiEclipseXmlrpcException;

    public XWikiObject getObject(String pageId, String className, int objectId) throws XWikiEclipseXmlrpcException;

    public List<XWikiClassSummary> getClasses() throws XWikiEclipseXmlrpcException;

    public XWikiClass getClass(String classId) throws XWikiEclipseXmlrpcException;

    public XWikiPage storePage(XWikiPage page) throws XWikiEclipseXmlrpcException;

    public XWikiObject storeObject(XWikiObject object) throws XWikiEclipseXmlrpcException;

    public boolean removeObject(String pageId, String className, int objectId) throws XWikiEclipseXmlrpcException;

    public void storeClass(XWikiClass xwikiClass) throws XWikiEclipseXmlrpcException;

    public boolean exists(String pageId);

    public boolean exists(String pageId, String className, int objectId);

    public List<XWikiPageHistorySummary> getPageHistory(String pageId) throws XWikiEclipseXmlrpcException;

    public List<XWikiPageSummary> getAllPageIds() throws XWikiEclipseXmlrpcException;
}
