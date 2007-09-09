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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.xwiki.plugins.eclipse.model.IXWikiPage;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.model.adapters.TreeAdapter;
import org.xwiki.plugins.eclipse.model.wrappers.XWikiPageWrapper;
import org.xwiki.plugins.eclipse.util.GuiUtils;
import org.xwiki.plugins.eclipse.util.XWikiConstants;

/**
 * Default implementation of {@link IXWikiPage}.
 */
public class XWikiPage implements IXWikiPage, TreeAdapter, IStorage, IStorageEditorInput
{

    /**
     * Parent XWikiSpace.
     */
    private IXWikiSpace parent;

    /**
     * Summary of this page.
     */
    private PageSummary summary;

    // TODO Again, like for spaces, why keep a summary when it's all included in the page ?

    /**
     * Data (opposite of summary ?) of this page.
     */
    private Page page;

    /**
     * Whether data has been retrieved or not.
     */
    private boolean dataReady = false;

    /**
     * Creates a new intance of an XWikiPage. Clients should use {@link IXWikiSpace#getPages()} and
     * similar methods to retrieve / create pages.
     * 
     * @param parent Parent space.
     * @param summary Page summary.
     */
    protected XWikiPage(IXWikiSpace parent, PageSummary pageSummary)
    {
        page = new Page();
        setSpace(parent);
        setSummary(pageSummary);
    }

    /**
     * Creates a new intance of an XWikiPage. Clients should use {@link IXWikiSpace#getPages()} and
     * similar methods to retrieve / create pages.
     * 
     * @param parent Parent space.
     * @param summary Page summary
     * @param data Page data.
     */
    protected XWikiPage(IXWikiSpace parent, Page page)
    {
        setSpace(parent);
        setSummary(page);
        this.page = page;
        setDataReady(true);
    }

    /**
     * Used by internal code to set data for this page.
     * 
     * @param page Page data.
     */
    protected void setPage(Page page)
    {
        this.page = page;
    }

    /**
     * Used by internal code to set availability of data.
     * 
     * @param dataReady Whether data is ready or not.
     */
    protected void setDataReady(boolean dataReady)
    {
        this.dataReady = dataReady;
    }

    /**
     * Used by internal code to set the parent of this page.
     * 
     * @param parent Parent space of this page.
     */
    protected void setSpace(IXWikiSpace parent)
    {
        this.parent = parent;
    }

    /**
     * Used by internal code to set the summary of this page.
     * 
     * @param summary Summary of this page.
     */
    protected void setSummary(PageSummary summary)
    {
        this.summary = summary;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getTreeChildren()
     */
    public Object[] getTreeChildren()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getImage()
     */
    public Image getImage()
    {
        return GuiUtils.loadIconImage(XWikiConstants.NAV_PAGE_ICON).createImage();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getTreeParent()
     */
    public Object getTreeParent()
    {
        return getParentSpace();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getText()
     */
    public String getText()
    {
        return getTitle();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#hasChildren()
     */
    public boolean hasChildren()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IStorage#getContents()
     */
    public InputStream getContents() throws CoreException
    {
        IXWikiPage wikiPage = new XWikiPageWrapper(this);
        return new ByteArrayInputStream(wikiPage.getContent().getBytes());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IStorage#getFullPath()
     */
    public IPath getFullPath()
    {
        return new Path(getUrl());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IStorage#getName()
     */
    public String getName()
    {
        return getTitle();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IStorage#isReadOnly()
     */
    public boolean isReadOnly()
    {
        // TODO for now...
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor()
    {
        return GuiUtils.loadIconImage(XWikiConstants.NAV_PAGE_ICON);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText()
    {
        return getUrl();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IStorageEditorInput#getStorage()
     */
    public IStorage getStorage() throws CoreException
    {
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter)
    {
        return null;
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
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (o instanceof IXWikiPage) {
            IXWikiPage other = (IXWikiPage) o;
            if (other.getId().equals(this.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#initialize()
     */
    public void init() throws SwizzleConfluenceException
    {
        if (!isDataReady()) {
            Confluence rpc = getParentSpace().getConnection().getRpcProxy();
            String pageId = getId();
            this.page = rpc.getPage(pageId);
            setDataReady(true);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getContent()
     */
    public String getContent()
    {
        return (String) this.page.getContent();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getContentStatus()
     */
    public String getContentStatus()
    {
        return (String) this.page.getContentStatus();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getCreated()
     */
    public Date getCreated()
    {
        return (Date) this.page.getCreated();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getCreator()
     */
    public String getCreator()
    {
        return (String) this.page.getCreator();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getId()
     */
    public String getId()
    {
        return (String) this.summary.getId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getLastModifier()
     */
    public String getLastModifier()
    {
        return (String) this.page.getModifier();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getLocks()
     */
    public int getLocks()
    {
        return (Integer) this.summary.getLocks();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getModified()
     */
    public Date getModified()
    {
        return (Date) this.page.getModified();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getParentId()
     */
    public String getParentId()
    {
        return (String) this.summary.getParentId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getSpace()
     */
    public IXWikiSpace getParentSpace()
    {
        return parent;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getSpaceKey()
     */
    public String getParentSpaceKey()
    {
        return (String) this.summary.getSpace();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getTitle()
     */
    public String getTitle()
    {
        return (String) this.summary.getTitle();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getUrl()
     */
    public String getUrl()
    {
        return (String) this.summary.getUrl();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getVersion()
     */
    public int getVersion()
    {
        return (Integer) this.page.getVersion();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#isCurrent()
     */
    public boolean isCurrent()
    {
        return (Boolean) this.page.isCurrent();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#isDataReady()
     */
    public boolean isDataReady()
    {
        return dataReady;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#isHomePage()
     */
    public boolean isHomePage()
    {
        return (Boolean) this.page.isHomePage();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#isSummaryReady()
     */
    public boolean isSummaryReady()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#setContent(java.lang.String)
     */
    public void setContent(String newContent)
    {
        this.page.setContent(newContent);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#setParentId(java.lang.String)
     */
    public void setParentId(String newParentId)
    {
        this.summary.setParentId(newParentId);
        this.page.setParentId(newParentId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#setTitle(java.lang.String)
     */
    public void setTitle(String newTitle)
    {
        this.summary.setTitle(newTitle);
        this.page.setTitle(newTitle);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#update()
     */
    public IXWikiPage save() throws SwizzleConfluenceException
    {
        Confluence rpc = getParentSpace().getConnection().getRpcProxy();
        this.page = rpc.storePage(page);
        setDataReady(true);
        return this;
    }

}
