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

import java.util.List;

import org.xwiki.eclipse.model.ModelObject;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.model.XWikiEclipseClassSummary;
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
    List<XWikiEclipseWikiSummary> getWikiSummaries() throws XWikiEclipseStorageException;

    List<XWikiEclipseSpaceSummary> getSpaceSummaries(XWikiEclipseWikiSummary wiki) throws XWikiEclipseStorageException;

    void dispose();

    XWikiEclipseServerInfo getServerInfo();

    /**
     * @param spaceSummary
     * @return
     */
    List<XWikiEclipsePageSummary> getPageSummaries(XWikiEclipseSpaceSummary spaceSummary);

    /**
     * @param pageSummary
     * @return
     */
    List<XWikiEclipseObjectSummary> getObjectSummaries(XWikiEclipsePageSummary pageSummary);

    /**
     * @param pageSummary
     * @return
     */
    List<XWikiEclipseAttachment> getAttachments(XWikiEclipsePageSummary pageSummary);

    /**
     * @param pageSummary
     * @return
     */
    List<XWikiEclipsePageHistorySummary> getPageHistorySummaries(XWikiEclipsePageSummary pageSummary)
        throws XWikiEclipseStorageException;

    /**
     * @param pageSummary
     * @return
     */
    XWikiEclipseClassSummary getPageClassSummary(XWikiEclipsePageSummary pageSummary);

    /**
     * @param pageSummary
     * @return
     */
    List<XWikiEclipseTag> getTags(XWikiEclipsePageSummary pageSummary);

    /**
     * @param pageSummary
     * @return
     */
    List<XWikiEclipseComment> getComments(XWikiEclipsePageSummary pageSummary);

    /**
     * @param objectSummary
     * @return
     */
    List<XWikiEclipseObjectProperty> getObjectProperties(XWikiEclipseObjectSummary objectSummary);

    /**
     * @param directory
     * @param attachment
     */
    void download(String directory, XWikiEclipseAttachment attachment);

    XWikiEclipsePage getPage(ModelObject o);

    /**
     * @param objectSummary
     * @return
     */
    XWikiEclipseObject getObject(XWikiEclipseObjectSummary objectSummary);

    /**
     * @param comment
     */
    void removeComment(XWikiEclipseComment comment);

    /**
     * @param c
     * @return
     */
    XWikiEclipseComment storeComment(XWikiEclipseComment c);

    /**
     * @param m
     * @return
     */
    XWikiEclipsePageSummary getPageSummary(ModelObject m);
}
