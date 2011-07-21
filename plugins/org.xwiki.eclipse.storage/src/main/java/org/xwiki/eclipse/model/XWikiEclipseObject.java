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
import java.util.List;

import org.xwiki.eclipse.storage.DataManager;

/**
 * A class representing an XWiki object.
 * 
 * @version $Id$
 */
public class XWikiEclipseObject extends ModelObject
{
    /**
     * The list of properties available for this object.
     * 
     * @see XWikiEclipseObjectProperty
     */
    private List<XWikiEclipseObjectProperty> properties;

    private String id;

    private String name;

    private String pageId;

    private String space;

    private String wiki;

    private String className;

    public XWikiEclipseObject(DataManager dataManager)
    {
        super(dataManager);
    }

    /**
     * @param propertyName
     * @return The information for a given property.
     */
    public XWikiEclipseObjectProperty getProperty(String propertyName)
    {
        return null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXWikiEclipseId()
    {
        return String.format(
            "xwikieclipse://%s/%s/%s/%s", getDataManager().getName(), getPageId(), getClassName(), getId()); //$NON-NLS-1$
    }

    /**
     * @param propertyName
     * @param value
     */
    public void setProperty(String propertyName, Object value)
    {

    }

    /**
     * @param propertyName
     * @param attributeName
     * @return
     */
    public Object getPropertyAttribute(String propertyName, String attributeName)
    {
        return null;
    }

    public List<XWikiEclipseObjectProperty> getProperties()
    {
        if (properties == null) {
            properties = new ArrayList<XWikiEclipseObjectProperty>();
        }
        return properties;
    }

    public void setProperties(List<XWikiEclipseObjectProperty> properties)
    {
        this.properties = properties;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPageId()
    {
        return pageId;
    }

    public void setPageId(String pageId)
    {
        this.pageId = pageId;
    }

    public String getSpace()
    {
        return space;
    }

    public void setSpace(String space)
    {
        this.space = space;
    }

    public String getWiki()
    {
        return wiki;
    }

    public void setWiki(String wiki)
    {
        this.wiki = wiki;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
