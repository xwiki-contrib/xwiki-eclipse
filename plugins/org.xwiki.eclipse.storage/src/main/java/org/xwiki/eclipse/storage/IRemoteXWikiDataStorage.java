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

import java.net.URL;
import java.util.List;

import org.xwiki.eclipse.model.ModelObject;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.model.XWikiEclipseClass;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipseObjectProperty;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.model.XWikiEclipsePageHistorySummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseServerInfo;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.model.XWikiEclipseTag;
import org.xwiki.eclipse.model.XWikiEclipseWikiSummary;

/**
 * remote data storage interface for different backend implementation, e.g., xmlrpc or rest
 * 
 * @version $Id$
 */
public interface IRemoteXWikiDataStorage
{
    void dispose();

    /* retrieval */
    XWikiEclipseServerInfo getServerInfo() throws XWikiEclipseStorageException;

    List<XWikiEclipseWikiSummary> getWikiSummaries() throws XWikiEclipseStorageException;

    List<XWikiEclipseSpaceSummary> getSpaceSummaries(String wikiId) throws XWikiEclipseStorageException;

    List<XWikiEclipsePageSummary> getPageSummaries(String wiki, String space) throws XWikiEclipseStorageException;

    List<XWikiEclipseObjectSummary> getObjectSummaries(String wiki, String space, String pageName)
        throws XWikiEclipseStorageException;

    List<XWikiEclipseAttachment> getAttachments(String wiki, String space, String pageName) throws XWikiEclipseStorageException;

    List<XWikiEclipsePageHistorySummary> getPageHistorySummaries(String wiki, String space, String page, String language)
        throws XWikiEclipseStorageException;

    XWikiEclipseClass getClass(String wiki, String space, String pageName) throws XWikiEclipseStorageException;

    List<XWikiEclipseTag> getTags(String wiki, String space, String page) throws XWikiEclipseStorageException;

    List<XWikiEclipseComment> getComments(String wiki, String space, String pageName) throws XWikiEclipseStorageException;

    List<XWikiEclipseObjectProperty> getObjectProperties(String wiki, String space, String pageName, String className,
        int number) throws XWikiEclipseStorageException;

    XWikiEclipsePage getPage(String wiki, String space, String pageName, String language)
        throws XWikiEclipseStorageException;

    XWikiEclipseObject getObject(String wiki, String space, String pageName, String className, int number) throws XWikiEclipseStorageException;

    XWikiEclipsePageSummary getPageSummary(String wiki, String space, String pageName, String language) throws XWikiEclipseStorageException;

    XWikiEclipseSpaceSummary getSpace(String wiki, String space) throws XWikiEclipseStorageException;

    List<XWikiEclipseTag> getAllTagsInWiki(String wiki) throws XWikiEclipseStorageException;

    List<XWikiEclipseClass> getClasses(String wiki) throws XWikiEclipseStorageException;

    XWikiEclipseClass getClass(String wiki, String className) throws XWikiEclipseStorageException;

    /* store */
    XWikiEclipseTag addTag(String wiki, String space, String pageName, String tagName) throws XWikiEclipseStorageException;

    void download(String directory, XWikiEclipseAttachment attachment) throws XWikiEclipseStorageException;

    XWikiEclipseComment storeComment(XWikiEclipseComment c) throws XWikiEclipseStorageException;

    void uploadAttachment(String wiki, String space, String pageName, URL fileUrl) throws XWikiEclipseStorageException;

    void updateAttachment(String wiki, String space, String pageName, String attachmentName, URL fileUrl) throws XWikiEclipseStorageException;

    /* delete */
    //FIXME: REFACTORING: Check this... Delete a model object?
    void remove(ModelObject o) throws XWikiEclipseStorageException;

    boolean pageExists(String wiki, String space, String pageName, String language) throws XWikiEclipseStorageException;

    XWikiEclipsePage storePage(XWikiEclipsePage page) throws XWikiEclipseStorageException;

    XWikiEclipsePage getPageHistory(String wiki, String space, String name, String language, int majorVersion,
        int minorVersion) throws XWikiEclipseStorageException;

    XWikiEclipseObject storeObject(XWikiEclipseObject object) throws XWikiEclipseStorageException;

    /**
     * @param sourcePage
     * @param newWiki
     * @param newSpace
     * @param newPageName
     * @return
     */
    XWikiEclipsePage copyPage(XWikiEclipsePage sourcePage, String newWiki, String newSpace, String newPageName)
        throws XWikiEclipseStorageException;

    /**
     * @param sourcePage
     * @param newWiki
     * @param newSpace
     * @param newPageName
     * @return
     */
    XWikiEclipsePage movePage(XWikiEclipsePage sourcePage, String newWiki, String newSpace, String newPageName)
        throws XWikiEclipseStorageException;
}
