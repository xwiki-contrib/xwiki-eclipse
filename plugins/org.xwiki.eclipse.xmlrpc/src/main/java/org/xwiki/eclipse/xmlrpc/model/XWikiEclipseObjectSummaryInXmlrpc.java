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

import org.eclipse.core.runtime.Assert;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.storage.AbstractDataManager;
import org.xwiki.xmlrpc.model.XWikiObjectSummary;
import org.xwiki.xmlrpc.model.XWikiPageSummary;

/**
 * A class representing an object summary.
 */
public class XWikiEclipseObjectSummaryInXmlrpc extends XWikiEclipseObjectSummary
{
    private XWikiObjectSummary data;

    private XWikiPageSummary pageSummary;

    public XWikiEclipseObjectSummaryInXmlrpc(AbstractDataManager dataManager, XWikiObjectSummary data, XWikiPageSummary pageSummary)
    {
        super(dataManager);

        Assert.isNotNull(data);
        this.data = data;

        Assert.isNotNull(pageSummary);
        this.pageSummary = pageSummary;

    }

    public XWikiObjectSummary getData()
    {
        return data;
    }

    public XWikiEclipsePageSummary getPageSummary()
    {
        XWikiEclipsePageSummary result = new XWikiEclipsePageSummaryInXmlrpc(getDataManager(), pageSummary);
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseObjectSummary#getPageId()
     */
    @Override
    public String getPageId()
    {
        return data.getPageId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseObjectSummary#getClassName()
     */
    @Override
    public String getClassName()
    {
        return data.getClassName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseObjectSummary#getId()
     */
    @Override
    public int getId()
    {
        return data.getId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseObjectSummary#getPrettyName()
     */
    @Override
    public String getPrettyName()
    {
        return data.getPrettyName();
    }

}
