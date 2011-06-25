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

import java.util.List;

import org.xwiki.eclipse.storage.DataManager;

/**
 * A class representing an XWiki page summary.
 * 
 * @version $Id$
 */
public class XWikiEclipsePageSummary extends ModelObject
{
    private String id;

    private String title;

    private String space;

    private String parentId;

    private String url;

    private List<String> translations;

    public XWikiEclipsePageSummary(DataManager dataManager, String id, String title, String space, String parentId,
        String url, List<String> translations)
    {
        super(dataManager);
        this.id = id;
        this.title = title;
        this.space = space;
        this.parentId = parentId;
        this.url = url;
        this.translations = translations;
    }

    @Override
    public String getXWikiEclipseId()
    {
        return String.format("xwikieclipse://%s/%s/summary", getDataManager().getName(), getId()); //$NON-NLS-1$
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSpace()
    {
        return space;
    }

    public void setSpace(String space)
    {
        this.space = space;
    }

    public String getParentId()
    {
        return parentId;
    }

    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public List<String> getTranslations()
    {
        return translations;
    }

    public void setTranslations(List<String> translations)
    {
        this.translations = translations;
    }
}
