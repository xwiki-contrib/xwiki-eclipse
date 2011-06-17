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
import org.xwiki.eclipse.storage.AbstractDataManager;
import org.xwiki.xmlrpc.model.XWikiPageHistorySummary;

/**
 * A class representing a space summary.
 */
public class XWikiEclipsePageHistorySummaryInXmlrpc extends org.xwiki.eclipse.model.XWikiEclipsePageHistorySummary
{
    private XWikiPageHistorySummary data;

    public XWikiEclipsePageHistorySummaryInXmlrpc(AbstractDataManager dataManager, XWikiPageHistorySummary data)
    {
        super(dataManager);

        Assert.isNotNull(data);
        this.data = data;
    }

    public XWikiPageHistorySummary getData()
    {
        return data;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePageHistorySummary#getMinorVersion()
     */
    @Override
    public int getMinorVersion()
    {
        return data.getMinorVersion();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePageHistorySummary#getVersion()
     */
    @Override
    public int getVersion()
    {
        return data.getVersion();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePageHistorySummary#getId()
     */
    @Override
    public String getId()
    {
        return data.getId();
    }
}
