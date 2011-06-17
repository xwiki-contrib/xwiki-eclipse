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

import org.codehaus.swizzle.confluence.SpaceSummary;
import org.eclipse.core.runtime.Assert;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.storage.AbstractDataManager;
import org.xwiki.xmlrpc.model.XWikiExtendedId;
import org.xwiki.xmlrpc.model.XWikiPage;
import org.xwiki.xmlrpc.model.XWikiPageSummary;

/**
 * A class representing an XWiki page.
 */
public class XWikiEclipsePageInXmlrpc extends XWikiEclipsePage
{
    private XWikiPage data;

    public XWikiEclipsePageInXmlrpc(AbstractDataManager dataManager, XWikiPage data)
    {
        super(dataManager);

        Assert.isNotNull(data);
        this.data = data;
    }

    public XWikiPage getData()
    {
        return data;
    }

    public XWikiEclipsePageSummary getSummary()
    {
        XWikiPageSummary summary = new XWikiPageSummary(data.toRawMap());
        return new XWikiEclipsePageSummaryInXmlrpc(getDataManager(), summary);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePage#getLanguage()
     */
    @Override
    public String getLanguage()
    {
        return data.getLanguage();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePage#getId()
     */
    @Override
    public String getId()
    {
        return data.getId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePage#getVersion()
     */
    @Override
    public int getVersion()
    {
        return data.getVersion();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePage#getTitle()
     */
    @Override
    public String getTitle()
    {
        return data.getTitle();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePage#getContent()
     */
    @Override
    public String getContent()
    {
        return data.getContent();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePage#getSpace()
     */
    @Override
    public String getSpace()
    {
        return data.getSpace();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePage#getSpaceSummary()
     */
    @Override
    public XWikiEclipseSpaceSummary getSpaceSummary()
    {

        SpaceSummary spaceSummary = new SpaceSummary();
        String spaceKey = getSpace();
        spaceSummary.setKey(spaceKey);
        spaceSummary.setName(spaceKey);

        XWikiEclipseSpaceSummary space = new XWikiEclipseSpaceSummaryInXmlrpc(getDataManager(), spaceSummary);
        return space;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePage#getName()
     */
    @Override
    public String getName()
    {
        String name = null;
        
        /* If the page id does not have a '.' then we are dealing with confluence ids */
        if (getId().indexOf(".") != -1) {
            XWikiExtendedId extendedId = new XWikiExtendedId(getId());

            if (getLanguage() != null) {
                if (getLanguage().equals("")) {
                    name = String.format("%s", extendedId.getBasePageId());
                } else {
                    name = String.format("%s [%s]", extendedId.getBasePageId(), getLanguage());
                }
            } else {
                name = String.format("%s", extendedId.getBasePageId());
            }
        } else {
            /* This is a confuence page */
            name = getTitle();
        }
        
        return name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePage#setContent(java.lang.String)
     */
    @Override
    public void setContent(String content)
    {
        data.setContent(content);        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipsePage#getUrl()
     */
    @Override
    public String getUrl()
    {
        return data.getUrl();
    }
}
