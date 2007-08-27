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
import java.util.HashMap;

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
import org.xwiki.plugins.eclipse.rpc.exceptions.CommunicationException;
import org.xwiki.plugins.eclipse.rpc.impl.XWikiRPCHandler;
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
    private HashMap<String, Object> summary;

    /**
     * Data (opposite of summary ?) of this page.
     */
    private HashMap<String, Object> data;

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
    protected XWikiPage(IXWikiSpace parent, HashMap<String, Object> summary)
    {
        data = new HashMap<String, Object>();
        setSpace(parent);
        setSummary(summary);
    }

    /**
     * Creates a new intance of an XWikiPage. Clients should use {@link IXWikiSpace#getPages()} and
     * similar methods to retrieve / create pages.
     * 
     * @param parent Parent space.
     * @param summary Page summary
     * @param data Page data.
     */
    protected XWikiPage(IXWikiSpace parent, HashMap<String, Object> summary,
        HashMap<String, Object> data)
    {
        setSpace(parent);
        setSummary(summary);
        this.data = data;
        setDataReady(true);
    }

    /**
     * Used by internal code to set data for this page.
     * 
     * @param data Page data.
     */
    protected void setData(HashMap<String, Object> data)
    {
        this.data = data;
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
    protected void setSummary(HashMap<String, Object> summary)
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
    public void init() throws CommunicationException
    {
        if (!isDataReady()) {
            String loginToken = getParentSpace().getConnection().getLoginToken();
            String pageId = getId();
            Object pageData = XWikiRPCHandler.getInstance().getPage(loginToken, pageId);
            this.data = (HashMap<String, Object>) pageData;
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
        return (String) this.data.get(XWikiConstants.PAGE_CONTENT);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getContentStatus()
     */
    public String getContentStatus()
    {
        return (String) this.data.get(XWikiConstants.PAGE_CONTENT_STATUS);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getCreated()
     */
    public Date getCreated()
    {
        return (Date) this.data.get(XWikiConstants.PAGE_CREATED);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getCreator()
     */
    public String getCreator()
    {
        return (String) this.data.get(XWikiConstants.PAGE_CREATOR);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getId()
     */
    public String getId()
    {
        return (String) this.summary.get(XWikiConstants.PAGE_SUMMARY_ID);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getLastModifier()
     */
    public String getLastModifier()
    {
        return (String) this.data.get(XWikiConstants.PAGE_MODIFIER);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getLocks()
     */
    public int getLocks()
    {
        return (Integer) this.summary.get(XWikiConstants.PAGE_SUMMARY_LOCKS);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getModified()
     */
    public Date getModified()
    {
        return (Date) this.data.get(XWikiConstants.PAGE_MODIFIED);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getParentId()
     */
    public String getParentId()
    {
        return (String) this.summary.get(XWikiConstants.PAGE_SUMMARY_PARENT_ID);
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
        return (String) this.summary.get(XWikiConstants.PAGE_SUMMARY_SPACE);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getTitle()
     */
    public String getTitle()
    {
        return (String) this.summary.get(XWikiConstants.PAGE_SUMMARY_TITLE);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getUrl()
     */
    public String getUrl()
    {
        return (String) this.summary.get(XWikiConstants.PAGE_SUMMARY_URL);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getVersion()
     */
    public int getVersion()
    {
        return (Integer) this.data.get(XWikiConstants.PAGE_VERSION);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#isCurrent()
     */
    public boolean isCurrent()
    {
        return (Boolean) this.data.get(XWikiConstants.PAGE_CURRENT);
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
        return (Boolean) this.data.get(XWikiConstants.PAGE_HOMEPAGE);
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
        this.data.put(XWikiConstants.PAGE_CONTENT, newContent);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#setParentId(java.lang.String)
     */
    public void setParentId(String newParentId)
    {
        this.summary.put(XWikiConstants.PAGE_SUMMARY_ID, newParentId);
        this.data.put(XWikiConstants.PAGE_PARENT_ID, newParentId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#setTitle(java.lang.String)
     */
    public void setTitle(String newTitle)
    {
        this.summary.put(XWikiConstants.PAGE_SUMMARY_TITLE, newTitle);
        this.data.put(XWikiConstants.PAGE_TITLE, newTitle);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#update()
     */
    public IXWikiPage save() throws CommunicationException
    {
        String loginToken = getParentSpace().getConnection().getLoginToken();
        HashMap<String, Object> newData =
            (HashMap<String, Object>) XWikiRPCHandler.getInstance().storePage(loginToken,
                this.data);
        this.data = newData;
        setDataReady(true);
        return this;
    }

}
