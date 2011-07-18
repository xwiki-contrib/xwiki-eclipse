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
 * A class representing an XWiki class.
 * 
 * @version $Id$
 */
public class XWikiEclipseClass extends ModelObject
{
    public static final String XWIKICLASS_ATTRIBUTE = "xwikiclass";

    private String id;

    private String name;

    private String objectsUrl;

    private String propertiesUrl;

    private String url;

    private List<XWikiEclipseObjectProperty> properties;

    public XWikiEclipseClass(DataManager dataManager)
    {
        super(dataManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXWikiEclipseId()
    {
        return String.format("xwikieclipse://%s/class/%s/summary", getDataManager().getName(), getId()); //$NON-NLS-1$
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

    public String getObjectsUrl()
    {
        return objectsUrl;
    }

    public void setObjectsUrl(String objectsUrl)
    {
        this.objectsUrl = objectsUrl;
    }

    public String getPropertiesUrl()
    {
        return propertiesUrl;
    }

    public void setPropertiesUrl(String propertiesUrl)
    {
        this.propertiesUrl = propertiesUrl;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public List<XWikiEclipseObjectProperty> getProperties()
    {
        return properties;
    }

    public void setProperties(List<XWikiEclipseObjectProperty> properties)
    {
        this.properties = properties;
    }
}
