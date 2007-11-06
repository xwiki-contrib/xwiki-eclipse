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

package org.xwiki.plugins.eclipse.model.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.Space;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.eclipse.core.runtime.IPath;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiPage;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.model.adapters.TreeAdapter;
import org.xwiki.plugins.eclipse.model.wrappers.XWikiSpaceWrapper;
import org.xwiki.plugins.eclipse.util.CacheUtils;
import org.xwiki.plugins.eclipse.util.LoggingUtils;

/**
 * Default implementation of {@link IXWikiSpace}. TODO Offline functionality was added later and
 * this has made the code dirty. Need to fix (refactor) the code base whenever possible.
 */
public class XWikiSpace implements IXWikiSpace, TreeAdapter
{

    /**
     * Serial version ID
     */
    private static final long serialVersionUID = 2422606240077511052L;

    /**
     * Parent connection to which this space belongs to.
     */
    private transient IXWikiConnection parent;

    /**
     * Set of pages under this space, mapped by ID.
     */
    private transient HashMap<String, IXWikiPage> pagesByID;

    /**
     * Summary of this space.
     */
    private transient SpaceSummary summary;

    /**
     * Complete data for this space.
     */
    private transient Space space;

    /**
     * Cache path of this space.
     */
    private transient IPath cachePath;

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
     * Sets the cache path for this space.
     * 
     * @param cachePath Cache path to be set.
     */
    protected void setCachePath(IPath cachePath)
    {
        this.cachePath = cachePath;
    }

    /**
     * Used in space restoration (from cache) process.
     * 
     * @param spacesByKey Pages indexed by id.
     */
    protected void setPages(HashMap<String, IXWikiPage> pagesByID)
    {
        this.pagesByID = pagesByID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getTreeChildren()
     */
    public Object[] getTreeChildren()
    {
        if (!isPagesReady()) {
            if (isOffline()) {
                LoggingUtils.error("Empty cache.");
                return null;
            }
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
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getTreeParent()
     */
    public Object getTreeParent()
    {
        return getConnection();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#hasChildren()
     */
    public boolean hasChildren()
    {
        if (isOffline() & !isPagesReady()) {
            return false;
        }
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
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#init()
     */
    public void init() throws SwizzleConfluenceException
    {
        if (!isDataReady()) {
            if (!isOffline()) {
                Confluence rpc = getConnection().getRpcProxy();
                String spaceKey = getKey();
                this.space = rpc.getSpace(spaceKey);
                setDataReady(true);
                CacheUtils.updateCache(this);
            }
        }
        if (!isPagesReady()) {
            if (!isOffline()) {
                Confluence rpc = getConnection().getRpcProxy();
                String spaceKey = getKey();
                List<Object> pages = rpc.getPages(spaceKey);
                for (int i = 0; i < pages.size(); i++) {
                    PageSummary pageSummary = (PageSummary) pages.get(i);
                    XWikiPage xwikiPage = new XWikiPage(this, pageSummary);
                    xwikiPage.setCachePath(getCachePath().addTrailingSeparator().append(
                        xwikiPage.getId()));
                    CacheUtils.updateCache(xwikiPage);
                    addPage(xwikiPage);
                }
                setPagesReady(true);
                CacheUtils.updateCache(this);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#grab()
     */    
    public void grab() throws SwizzleConfluenceException
    {
        init();
        for (IXWikiPage page : getPages()) {
            page.init();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#synchronize(SpaceSummary)
     */
    public boolean synchronize(SpaceSummary newSummary) throws SwizzleConfluenceException
    {
    	boolean success = false;
        if (!isOffline()) {
        	success = true;
            this.summary = newSummary;
            if (isDataReady()) {
                this.space = getConnection().getRpcProxy().getSpace(getKey());
                // Get a fresh copy of page summaries from the server.
                List pages = getConnection().getRpcProxy().getPages(getKey());
                // Index new pages summaries
                HashMap<String, PageSummary> newPageSummaries =
                    new HashMap<String, PageSummary>();
                for (Object o : pages) {
                    PageSummary summary = (PageSummary) o;
                    newPageSummaries.put(summary.getId(), summary);
                }
                // Key sets of cached and new page summaries.
                Set<String> cacheKeySet = new HashSet<String>(pagesByID.keySet());
                Set<String> newKeySet = newPageSummaries.keySet();
                // Synchronize each cached page.
                for (String cacheKey : cacheKeySet) {
                    if (newKeySet.contains(cacheKey)) {
                        success = pagesByID.get(cacheKey).synchronize(newPageSummaries.get(cacheKey)) ? success : false;
                    } else {
                        // For now, we'll simply get rid of the missing page.
                        CacheUtils.clearCache(pagesByID.get(cacheKey));
                        pagesByID.remove(cacheKey);
                    }
                }
                // Check if any new page have been added.
                for (String newKey : newKeySet) {
                    if (!cacheKeySet.contains(newKey)) {
                        XWikiPage xwikiPage = new XWikiPage(this, newPageSummaries.get(newKey));
                        xwikiPage.setCachePath(getCachePath().addTrailingSeparator().append(
                            xwikiPage.getId()));
                        CacheUtils.updateCache(xwikiPage);
                        addPage(xwikiPage);
                    }
                }
                // Finally, update the cache
                CacheUtils.updateCache(this);
            }
        }
        return success;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#addPage(java.lang.String, java.lang.String)
     */
    public void addPage(String title, String content) throws SwizzleConfluenceException
    {
        if (!isOffline()) {
            Page page = new Page();
            page.setSpace(getKey());
            page.setTitle(title);
            page.setContent(content);
            Page result = getConnection().getRpcProxy().storePage(page);
            XWikiPage xwikiPage = new XWikiPage(this, result);
            xwikiPage.setCachePath(getCachePath().addTrailingSeparator()
                .append(xwikiPage.getId()));
            CacheUtils.updateCache(xwikiPage);
            addPage(xwikiPage);
        }
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
        if (!isDataReady()) {
            return "Not Available.";
        }
        return (String) this.space.getDescription();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getHomePageId()
     */
    public String getHomePageId()
    {
        if (!isDataReady()) {
            return "Not Available.";
        }
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
        if (!isPagesReady()) {
            LoggingUtils.error("Pages not available in cache.");
        }
        return pagesByID.get(pageID);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getPageByTitle(java.lang.String)
     */
    public IXWikiPage searchPage(String pageTitle)
    {
        if (!isPagesReady()) {
            LoggingUtils.error("Pages not available in cache.");
        }
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
        if (!isPagesReady()) {
            LoggingUtils.error("Pages not available in cache.");
        }
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
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#isOffline()
     */
    public boolean isOffline()
    {
        return getConnection().isOffline();
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
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getCachePath()
     */
    public IPath getCachePath()
    {
        return this.cachePath;
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
        if (!isOffline()) {
            try {
                getConnection().getRpcProxy().removePage(pageId);
                IXWikiPage pageToRemove = getPageByID(pageId);
                pagesByID.remove(pageToRemove.getId());
                CacheUtils.clearCache(pageToRemove);
            } catch (SwizzleConfluenceException e) {
                throw e;
            }
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

    /**
     * Custom serializer method.
     */
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.writeObject(summary.toMap());
        out.writeObject(space.toMap());
        out.defaultWriteObject();
    }

    /**
     * Custom deserializer method.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        Map summaryMap = (Map) in.readObject();
        Map spaceMap = (Map) in.readObject();
        in.defaultReadObject();
        this.summary = new SpaceSummary(summaryMap);
        this.space = new Space(spaceMap);
    }
}
