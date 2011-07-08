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
package org.xwiki.eclipse.model;

import java.util.Calendar;

import org.xwiki.eclipse.storage.DataManager;

/**
 * A class representing an XWiki page.
 * 
 * @version $Id$
 */
public class XWikiEclipsePage extends ModelObject
{
    private String language;

    private String id;

    private String version;

    private int majorVersion;

    private int minorVersion;

    private String title;

    private String content;

    private String space;

    private String wiki;

    private String name;

    private String fullName;

    private String url;

    private String creator;

    private String modifier;

    private Calendar created;

    private Calendar modified;

    private String pageClassUrl;

    private String parentId;

    private String syntax;

    public XWikiEclipsePage(DataManager dataManager)
    {
        super(dataManager);
    }

    @Override
    public String getXWikiEclipseId()
    {
        return String.format("xwikieclipse://%s/%s/page/%s", getDataManager().getName(), getId(), getLanguage()); //$NON-NLS-1$
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public int getMajorVersion()
    {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion)
    {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion()
    {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion)
    {
        this.minorVersion = minorVersion;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getSpace()
    {
        return space;
    }

    public void setSpace(String space)
    {
        this.space = space;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getModifier()
    {
        return modifier;
    }

    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    public Calendar getCreated()
    {
        return created;
    }

    public void setCreated(Calendar created)
    {
        this.created = created;
    }

    public Calendar getModified()
    {
        return modified;
    }

    public void setModified(Calendar modified)
    {
        this.modified = modified;
    }

    public String getPageClassUrl()
    {
        return pageClassUrl;
    }

    public void setPageClassUrl(String pageClassUrl)
    {
        this.pageClassUrl = pageClassUrl;
    }

    public String getWiki()
    {
        return wiki;
    }

    public void setWiki(String wiki)
    {
        this.wiki = wiki;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getParentId()
    {
        return parentId;
    }

    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public String getSyntax()
    {
        return syntax;
    }

    public void setSyntax(String syntax)
    {
        this.syntax = syntax;
    }
}
