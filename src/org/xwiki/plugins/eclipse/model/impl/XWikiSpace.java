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

import org.eclipse.swt.graphics.Image;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiPage;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.model.adapters.TreeAdapter;
import org.xwiki.plugins.eclipse.model.wrappers.XWikiSpaceWrapper;
import org.xwiki.plugins.eclipse.rpc.exceptions.CommunicationException;
import org.xwiki.plugins.eclipse.rpc.impl.XWikiRPCHandler;
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
     * Set of pages under this space, mapped by Title.
     */
    private HashMap<String, IXWikiPage> pagesByTitle;

    /**
     * Summary of this space.
     */
    private HashMap<String, Object> summary;

    /**
     * Complete data for this space.
     */
    private HashMap<String, Object> data;

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
    protected XWikiSpace(IXWikiConnection parent, HashMap<String, Object> summary)
    {
        pagesByID = new HashMap<String, IXWikiPage>();
        pagesByTitle = new HashMap<String, IXWikiPage>();
        data = new HashMap<String, Object>();
        setSummary(summary);
        setConnection(parent);
    }

    /**
     * Creates a new space. Clients should use {@link IXWikiConnection#getSpaces()} and similar
     * methods.
     * 
     * @param parent Parent connection of this space.
     * @param summary Summary of this space.
     * @param data Data of this space.
     */
    protected XWikiSpace(IXWikiConnection parent, HashMap<String, Object> summary,
        HashMap<String, Object> data)
    {
        this(parent, summary);
        this.data = data;
    }

    /**
     * Used by internal code to initialize this space.
     * 
     * @param data Data (core) of this space.
     */
    protected void setData(HashMap<String, Object> data)
    {
        this.data = data;
    }

    /**
     * Used by internal code to initialize this space.
     * 
     * @param page Page to be added to localModel.
     */
    protected void addPage(IXWikiPage page)
    {
        pagesByID.put(page.getId(), page);
        pagesByTitle.put(page.getTitle(), page);
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
    protected void setSummary(HashMap<String, Object> summary)
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
            } catch (CommunicationException e) {
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
    public void init() throws CommunicationException
    {
        if (!isDataReady()) {
            String loginToken = getConnection().getLoginToken();
            String spaceKey = getKey();
            this.data =
                (HashMap<String, Object>) XWikiRPCHandler.getInstance().getSpace(loginToken,
                    spaceKey);
            setDataReady(true);
        }
        if (!isPagesReady()) {
            Object[] pages =
                XWikiRPCHandler.getInstance().getPageSummaries(getConnection().getLoginToken(),
                    getKey());
            for (Object page : pages) {
                HashMap<String, Object> pageSummary = (HashMap<String, Object>) page;
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
    public void addPage(String title, String content) throws CommunicationException
    {
        HashMap<String, Object> tempPage = new HashMap<String, Object>();
        HashMap<String, Object> summary = new HashMap<String, Object>();
        HashMap<String, Object> result;
        tempPage.put(XWikiConstants.PAGE_SPACE, getKey());
        tempPage.put(XWikiConstants.PAGE_TITLE, title);
        tempPage.put(XWikiConstants.PAGE_CONTENT, content);
        result =
            (HashMap<String, Object>) XWikiRPCHandler.getInstance().storePage(
                getConnection().getLoginToken(), tempPage);
        // Now we need to construct the summary too...
        summary.put(XWikiConstants.PAGE_SUMMARY_ID, result.get(XWikiConstants.PAGE_ID));
        summary.put(XWikiConstants.PAGE_SUMMARY_LOCKS, result.get(XWikiConstants.PAGE_LOCKS));
        summary.put(XWikiConstants.PAGE_SUMMARY_PARENT_ID, result
            .get(XWikiConstants.PAGE_PARENT_ID));
        summary.put(XWikiConstants.PAGE_SUMMARY_SPACE, result.get(XWikiConstants.PAGE_SPACE));
        summary.put(XWikiConstants.PAGE_SUMMARY_TITLE, result.get(XWikiConstants.PAGE_TITLE));
        summary.put(XWikiConstants.PAGE_SUMMARY_URL, result.get(XWikiConstants.PAGE_URL));
        // Finally, we add the page into local model...
        IXWikiPage wikiPage = new XWikiPage(this, summary, result);
        addPage(wikiPage);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getConnection()
     */
    public IXWikiConnection getConnection()
    {
        return parent;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getDescriptionAsHtml()
     */
    public String getDescriptionAsHtml()
    {
        return (String) this.data.get(XWikiConstants.SPACE_DESCRIPTION);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getHomePageId()
     */
    public String getHomePageId()
    {
        return (String) this.data.get(XWikiConstants.SPACE_HOMEPAGE);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getKey()
     */
    public String getKey()
    {
        return (String) this.summary.get(XWikiConstants.SPACE_SUMMARY_KEY);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getName()
     */
    public String getName()
    {
        return (String) this.summary.get(XWikiConstants.SPACE_SUMMARY_NAME);
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
    public IXWikiPage getPageByTitle(String pageTitle)
    {
        return pagesByTitle.get(pageTitle);
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
        return (String) this.summary.get(XWikiConstants.SPACE_SUMMARY_TYPE);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getUrl()
     */
    public String getUrl()
    {
        return (String) this.summary.get(XWikiConstants.SPACE_SUMMARY_URL);
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
    public void removeChildPage(String pageId) throws CommunicationException
    {
        try {
            XWikiRPCHandler.getInstance().removePage(getConnection().getLoginToken(), pageId);
            IXWikiPage pageToRemove = getPageByID(pageId);
            pagesByID.remove(pageToRemove.getId());
            pagesByTitle.remove(pageToRemove.getTitle());
        } catch (CommunicationException e) {
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
    public IXWikiSpace save() throws CommunicationException
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
        throws CommunicationException
    {
        // TODO implement this.
    }

}
