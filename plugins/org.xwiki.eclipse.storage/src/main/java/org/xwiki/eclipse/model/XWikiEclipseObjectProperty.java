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

import java.util.Map;

import org.xwiki.eclipse.storage.DataManager;

/**
 * This class is an utility class used for encapsulating and accessing the information concerning a property in a more
 * usable way (in this way properties can be compactly passed around using a single reference)
 * 
 * @version $Id$
 */
public class XWikiEclipseObjectProperty extends ModelObject
{
    private String name;

    private String type;

    private String value;

    private Map<String, String> attributes;

    /**
     * @param dataManager
     */
    public XWikiEclipseObjectProperty(DataManager dataManager)
    {
        super(dataManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXWikiEclipseId()
    {
        return String.format("xwikieclipse://%s/properties/%s/%s", getDataManager().getName(), getType(), getName()); //$NON-NLS-1$        
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }
}
