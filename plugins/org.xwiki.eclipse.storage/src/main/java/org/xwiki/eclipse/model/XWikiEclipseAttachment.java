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
package org.xwiki.eclipse.model;

import java.util.Calendar;

import org.xwiki.eclipse.storage.DataManager;

/**
 * @version $Id$
 */
public class XWikiEclipseAttachment extends ModelObject
{
    private String pageUrl;

    private String attachmentUrl;

    private String id;

    private String name;

    private String version;

    private String pageId;

    private String pageVersion;

    private String mimeType;

    private String author;

    private Calendar date;

    private String absoluteUrl;

    /**
     * @param dataManager
     */
    public XWikiEclipseAttachment(DataManager dataManager)
    {
        super(dataManager);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.ModelObject#getXWikiEclipseId()
     */
    @Override
    public String getXWikiEclipseId()
    {
        return String.format("xwikieclipse://%s/attachment/%s", getDataManager().getName(), getId()); //$NON-NLS-1$
    }

    public String getPageUrl()
    {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl)
    {
        this.pageUrl = pageUrl;
    }

    public String getAttachmentUrl()
    {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl)
    {
        this.attachmentUrl = attachmentUrl;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getPageId()
    {
        return pageId;
    }

    public void setPageId(String pageId)
    {
        this.pageId = pageId;
    }

    public String getPageVersion()
    {
        return pageVersion;
    }

    public void setPageVersion(String pageVersion)
    {
        this.pageVersion = pageVersion;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public Calendar getDate()
    {
        return date;
    }

    public void setDate(Calendar date)
    {
        this.date = date;
    }

    public String getAbsoluteUrl()
    {
        return absoluteUrl;
    }

    public void setAbsoluteUrl(String absoluteUrl)
    {
        this.absoluteUrl = absoluteUrl;
    }
}
