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
 * A class representing an XWiki object.
 * 
 * @version $Id$
 */
public abstract class XWikiEclipseObject extends ModelObject
{

    public XWikiEclipseObject(DataManager dataManager)
    {
        super(dataManager);
    }

    /**
     * @return The list of properties available for this object.
     * @see XWikiEclipseObjectProperty
     */
    public abstract List<XWikiEclipseObjectProperty> getProperties();

    /**
     * @param propertyName
     * @return The information for a given property.
     */
    public abstract XWikiEclipseObjectProperty getProperty(String propertyName);

    public abstract String getName();

    public abstract XWikiEclipsePageSummary getPageSummary();

    public abstract XWikiEclipseObjectSummary getSummary();

    public abstract String getPageId();

    public abstract String getClassName();

    public abstract int getId();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXWikiEclipseId()
    {
        return String.format(
            "xwikieclipse://%s/%s/%s/%d", getDataManager().getName(), getPageId(), getClassName(), getId()); //$NON-NLS-1$
    }

    /**
     * @param propertyName
     * @param value
     */
    public abstract void setProperty(String propertyName, Object value);

    /**
     * @param propertyName
     * @param attributeName
     * @return
     */
    public abstract Object getPropertyAttribute(String propertyName, String attributeName);

    public abstract XWikiEclipseClass getXWikiClass();
}
