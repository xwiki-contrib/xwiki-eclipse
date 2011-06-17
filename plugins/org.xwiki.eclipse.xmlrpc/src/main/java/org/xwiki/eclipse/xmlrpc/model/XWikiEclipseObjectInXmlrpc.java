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
package org.xwiki.eclipse.xmlrpc.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.xwiki.eclipse.model.XWikiEclipseClass;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipseObjectProperty;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.storage.AbstractDataManager;
import org.xwiki.xmlrpc.model.XWikiClass;
import org.xwiki.xmlrpc.model.XWikiObject;
import org.xwiki.xmlrpc.model.XWikiObjectSummary;
import org.xwiki.xmlrpc.model.XWikiPageSummary;

/**
 * A class representing an XWiki object.
 */
public class XWikiEclipseObjectInXmlrpc extends XWikiEclipseObject
{
    private XWikiObject data;

    private XWikiClass xwikiClass;

    private XWikiPageSummary pageSummary;

    /**
     * Constructor
     * 
     * @param dataManager The data manager that returned this object.
     * @param data The actual XWiki object description.
     * @param xwikiClass The XWiki class for this object.
     */
    public XWikiEclipseObjectInXmlrpc(AbstractDataManager dataManager, XWikiObject data, XWikiClass xwikiClass,
        XWikiPageSummary pageSummary)
    {
        super(dataManager);

        Assert.isNotNull(data);
        this.data = data;

        Assert.isNotNull(xwikiClass);
        Assert.isLegal(data.getClassName().equals(xwikiClass.getId()));
        this.xwikiClass = xwikiClass;

        this.pageSummary = pageSummary;
    }

    public XWikiObject getData()
    {
        return data;
    }

    public XWikiClass getXWikiClassInXmlrpc()
    {
        return xwikiClass;
    }

    /**
     * @return The list of properties available for this object.
     * @see XWikiEclipseObjectProperty
     */
    public List<XWikiEclipseObjectProperty> getProperties()
    {
        List<XWikiEclipseObjectProperty> result = new ArrayList<XWikiEclipseObjectProperty>();
        for (String propertyName : xwikiClass.getProperties()) {
            XWikiEclipseObjectProperty property = new XWikiEclipseObjectPropertyInXmlrpc(this, propertyName);
            result.add(property);
        }

        return result;
    }

    /**
     * @param propertyName
     * @return The information for a given property.
     */
    public XWikiEclipseObjectProperty getProperty(String propertyName)
    {
        return new XWikiEclipseObjectPropertyInXmlrpc(this, propertyName);
    }

    public String getName()
    {
        String name = data.getPrettyName();
        if (name == null) {
            if (data.getId() == -1) {
                name = String.format("%s[NEW]", data.getClassName());
            } else {
                name = String.format("%s[%d]", data.getClassName(), data.getId());
            }
        }

        return name;
    }

    public XWikiEclipsePageSummary getPageSummary()
    {
        XWikiEclipsePageSummary result = new XWikiEclipsePageSummaryInXmlrpc(getDataManager(), pageSummary);
        return result;
    }

    public XWikiEclipseObjectSummary getSummary()
    {
        XWikiObjectSummary summary = new XWikiObjectSummary(data.toRawMap());
        return new XWikiEclipseObjectSummaryInXmlrpc(getDataManager(), summary, pageSummary);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseObject#getPageId()
     */
    @Override
    public String getPageId()
    {
        return data.getPageId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseObject#getClassName()
     */
    @Override
    public String getClassName()
    {
        return data.getClassName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseObject#getId()
     */
    @Override
    public int getId()
    {
        return data.getId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseObject#setProperty(java.lang.String, java.lang.Object)
     */
    @Override
    public void setProperty(String propertyName, Object value)
    {
        data.setProperty(propertyName, value);
    }

    /**
     * @return
     */
    public XWikiPageSummary getPageSummaryInXmlrpc()
    {
        return pageSummary;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseObject#getPropertyAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public Object getPropertyAttribute(String propertyName, String attributeName)
    {
        return xwikiClass.getPropertyAttribute(propertyName, attributeName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseObject#getXWikiClass()
     */
    @Override
    public XWikiEclipseClass getXWikiClass()
    {
        return new XWikiEclipseClassInXmlrpc(getDataManager(), xwikiClass);
    }

}
