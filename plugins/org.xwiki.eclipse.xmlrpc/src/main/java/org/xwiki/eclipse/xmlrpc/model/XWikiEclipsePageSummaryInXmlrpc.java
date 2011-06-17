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

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.storage.AbstractDataManager;
import org.xwiki.xmlrpc.model.XWikiPageSummary;

/**
 * A class representing an XWiki page summary.
 */
public class XWikiEclipsePageSummaryInXmlrpc extends XWikiEclipsePageSummary
{
    private XWikiPageSummary data;

    public XWikiEclipsePageSummaryInXmlrpc(AbstractDataManager dataManager, XWikiPageSummary data)
    {
        super(dataManager);

        Assert.isNotNull(data);
        this.data = data;
    }

    public XWikiPageSummary getData()
    {
        return data;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePageSummary#getId()
     */
    @Override
    public String getId()
    {
        return data.getId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePageSummary#getTranslations()
     */
    @Override
    public List<String> getTranslations()
    {
        return data.getTranslations();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePageSummary#getTitle()
     */
    @Override
    public String getTitle()
    {
        return data.getTitle();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePageSummary#getSpace()
     */
    @Override
    public String getSpace()
    {
        return data.getSpace();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePageSummary#getParentId()
     */
    @Override
    public String getParentId()
    {
        return data.getParentId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePageSummary#getUrl()
     */
    @Override
    public String getUrl()
    {
        return data.getUrl();
    }
}
