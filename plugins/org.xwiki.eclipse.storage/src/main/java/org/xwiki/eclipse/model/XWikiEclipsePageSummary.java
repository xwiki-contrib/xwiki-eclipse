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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xwiki.eclipse.storage.DataManager;

/**
 * A class representing an XWiki page summary.
 * 
 * @version $Id$
 */
public class XWikiEclipsePageSummary extends ModelObject
{
    public enum Data
    {
        OBJECTS,
        ATTACHMENTS,
        TAGS,
        COMMENTS
    }

    private String id;

    private String title;

    private String name;

    private String fullName;

    private String space;

    private String wiki;

    private String parentId;

    private String url;

    private String syntax;

    private List<XWikiEclipsePageTranslationSummary> translations;

    private String language = "";

    private Set<Data> data;

    public XWikiEclipsePageSummary(DataManager dataManager, Data... data)
    {
        super(dataManager);
        this.data = new HashSet<Data>();
        for (Data d : data) {
            this.data.add(d);
        }
    }

    @Override
    public String getXWikiEclipseId()
    {
        return String
            .format(
                "xwikieclipse://%s/%s/%s/%s/summary/%s", getDataManager().getName(), getWiki(), getSpace(), getName(), getLanguage().equals("") ? "default" : getLanguage()); //$NON-NLS-1$
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

    public String getWiki()
    {
        return wiki;
    }

    public void setWiki(String wiki)
    {
        this.wiki = wiki;
    }

    public List<XWikiEclipsePageTranslationSummary> getTranslations()
    {
        if (translations == null) {
            translations = new ArrayList<XWikiEclipsePageTranslationSummary>();
        }
        return translations;
    }

    public void setTranslations(List<XWikiEclipsePageTranslationSummary> translations)
    {
        this.translations = translations;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getSyntax()
    {
        return syntax;
    }

    public void setSyntax(String syntax)
    {
        this.syntax = syntax;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public boolean has(Data d)
    {
        return data.contains(d);
    }

}
