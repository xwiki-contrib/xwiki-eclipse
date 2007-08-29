/*
 * Copyright 2006-2007, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
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

package org.xwiki.plugins.eclipse.model.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.Space;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.eclipse.swt.graphics.Image;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiPage;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.model.adapters.TreeAdapter;
import org.xwiki.plugins.eclipse.model.wrappers.XWikiSpaceWrapper;
import org.xwiki.plugins.eclipse.util.GuiUtils;
import org.xwiki.plugins.eclipse.util.XWikiConstants;

/**
 * Default implementation of {@link IXWikiSpace}.
 */
public class XWikiSpace implements IXWikiSpace, TreeAdapter
{

    /**
     * Parent connection to which this space belongs to.
     */
    private IXWikiConnection parent;

    /**
     * Set of pages under this space, mapped by ID.
     */
    private HashMap<String, IXWikiPage> pagesByID;

    /**
     * Summary of this space. 
     */
    private SpaceSummary summary;
    // TODO This is (almost) a subset of the space. Why do we need both ?
    // -- in general summaries are exact subsets ... here it's an anomaly and should be treated as such
    // -- especially since xwiki does not have a notion of space type

    /**
     * Complete data for this space.
     */
    private Space space;

    /**
     * Whether this space should be displayed or not.
     */
    private boolean masked = true;

    /**
     * Whether data has been retrieved or not.
     */
    private boolean dataReady = false;

    /**
     * Whether child pages have been retrieved or not.
     */
    private boolean pagesReady = false;

    /**
     * Creates a new space. Clients should use {@link IXWikiConnection#getSpaces()} and similar
     * methods.
     * 
     * @param parent Parent connection of this space.
     * @param summary Summary of this space.
     */
    protected XWikiSpace(IXWikiConnection wikiConnection, SpaceSummary summary)
    {
        pagesByID = new HashMap<String, IXWikiPage>();
        space = new Space();
        setSummary(summary);
        setConnection(wikiConnection);
    }

    /**
     * Creates a new space. Clients should use {@link IXWikiConnection#getSpaces()} and similar
     * methods.
     * 
     * @param connection Parent connection of this space.
     * @param summary Summary of this space.
     * @param space Data of this space.
     */
    protected XWikiSpace(IXWikiConnection connection, SpaceSummary summary, Space space)
    {
        this(connection, summary);
        this.space = space;
    }



    /**
     * Used by internal code to initialize this space.
     * 
     * @param space Data (core) of this space.
     */
    protected void setSpace(Space space)
    {
        this.space = space;
    }

    /**
     * Used by internal code to initialize this space.
     * 
     * @param page Page to be added to localModel.
     */
    protected void addPage(IXWikiPage page)
    {
        pagesByID.put(page.getId(), page);
      //  pagesByTitle.put(page.getTitle(), page);
    }

    /**
     * Used by internal code to initialize this space.
     * 
     * @param parent Parent connection of this space.
     */
    protected void setConnection(IXWikiConnection parent)
    {    	
        this.parent = parent;
    }

    /**
     * Used by internal code to initialize this space.
     * 
     * @param summary Summary of this space.
     */
    protected void setSummary(SpaceSummary summary)
    {
        this.summary = summary;
    }

    /**
     * Used by internal code to initialize this space.
     * 
     * @param dataReady Sets whether data for this space has been retrieved or not.
     */
    protected void setDataReady(boolean dataReady)
    {
        this.dataReady = dataReady;
    }

    /**
     * Used by internal code to initialize this space.
     * 
     * @param pagesReady Sets whether child pages have been retreived or not.
     */
    protected void setPagesReady(boolean pagesReady)
    {
        this.pagesReady = pagesReady;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getTreeChildren()
     */
    public Object[] getTreeChildren()
    {
        if (!isPagesReady()) {
            IXWikiSpace space = new XWikiSpaceWrapper(this);
            try {
                space.init();
            } catch (SwizzleConfluenceException e) {
                // Will be logged elsewhere
            }
            if (!isPagesReady()) {
                return null;
            }
        }
        return pagesByID.values().toArray();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getImage()
     */
    public Image getImage()
    {
        return GuiUtils.loadIconImage(XWikiConstants.NAV_SPACE_ICON).createImage();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getTreeParent()
     */
    public Object getTreeParent()
    {
        return getConnection();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getText()
     */
    public String getText()
    {
        return getName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#hasChildren()
     */
    public boolean hasChildren()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getUrl();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#initialize()
     */
    public void init() throws SwizzleConfluenceException
    {
        if (!isDataReady()) {        	
            Confluence rpc = getConnection().getRpcProxy();
            String spaceKey = getKey();
            this.space = rpc.getSpace(spaceKey);            
            setDataReady(true);
        }
        if (!isPagesReady()) {
            Confluence rpc = getConnection().getRpcProxy();
            String spaceKey = getKey();
            List<Object> pages = rpc.getPages(spaceKey);
            for (int i = 0; i<pages.size(); i++) {
                PageSummary pageSummary = (PageSummary)pages.get(i);
                IXWikiPage xwikiPage = new XWikiPage(this, pageSummary);
                addPage(xwikiPage);
            }
            setPagesReady(true);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#addPage(java.lang.String, java.lang.String)
     */
    public void addPage(String title, String content) throws SwizzleConfluenceException
    {
        Page page = new Page();
        page.setSpace(getKey());
        page.setTitle(title);
        page.setContent(content);
        Page result = getConnection().getRpcProxy().storePage(page);
        IXWikiPage wikiPage = new XWikiPage(this, result);
        addPage(wikiPage);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getConnection()
     */
    public IXWikiConnection getConnection()
    {    	    	
    	return this.parent;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getDescriptionAsHtml()
     */
    public String getDescriptionAsHtml()
    {
        return (String) this.space.getDescription();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getHomePageId()
     */
    public String getHomePageId()
    {
        return (String) this.space.getHomepage();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getKey()
     */
    public String getKey()
    {
        return (String) this.summary.getKey();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getName()
     */
    public String getName()
    {
        // Only space keys are referred inside XEclipse
        return (String) this.summary.getKey();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getPageByID(java.lang.String)
     */
    public IXWikiPage getPageByID(String pageID)
    {
        return pagesByID.get(pageID);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getPageByTitle(java.lang.String)
     */
    public IXWikiPage searchPage(String pageTitle)
    {
        for (IXWikiPage page : pagesByID.values()) {
        	if (page.getTitle().equals(pageTitle)) {
        		return page;
        	}
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getPages()
     */
    public Collection<IXWikiPage> getPages()
    {
        return pagesByID.values();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getType()
     */
    public String getType()
    {
        return (String) this.summary.getType();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getUrl()
     */
    public String getUrl()
    {
        return (String) this.summary.getUrl();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#isDataReady()
     */
    public boolean isDataReady()
    {
        return dataReady;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#isMasked()
     */
    public boolean isMasked()
    {
        return masked;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#setMasked(boolean)
     */
    public void setMasked(boolean masked)
    {
        this.masked = masked;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#isPagesReady()
     */
    public boolean isPagesReady()
    {
        return pagesReady;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#isSummaryReady()
     */
    public boolean isSummaryReady()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#removePage(java.lang.String)
     */
    public void removeChildPage(String pageId) throws SwizzleConfluenceException
    {
        try {
            getConnection().getRpcProxy().removePage(pageId);
            IXWikiPage pageToRemove = getPageByID(pageId);
            pagesByID.remove(pageToRemove.getId());
        } catch (SwizzleConfluenceException e) {
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#setName(java.lang.String)
     */
    public void setName(String newName)
    {
        // TODO implement this.
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#update()
     */
    public IXWikiSpace save() throws SwizzleConfluenceException
    {
        // TODO implement this.
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#updatePage(java.lang.String,
     *      java.lang.String, java.lang.String, int)
     */
    public void updateChildPage(String pageId, String title, String content, int version)
        throws SwizzleConfluenceException
    {
        // TODO implement this.
    }

}
