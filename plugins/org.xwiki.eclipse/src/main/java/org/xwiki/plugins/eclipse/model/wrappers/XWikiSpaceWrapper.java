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

package org.xwiki.plugins.eclipse.model.wrappers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiPage;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.rpc.exceptions.CommunicationException;
import org.xwiki.plugins.eclipse.util.GuiUtils;
import org.xwiki.plugins.eclipse.util.XWikiProgressRunner;

/**
 * Implentation of Decorator Pattern for adding GUI icing for
 * underlying {@link IXWikiSpace}.
 */
public class XWikiSpaceWrapper implements IXWikiSpace
{
    /**
     * Actual {@link IXWikiSpace} being wrapped.
     */
    private IXWikiSpace space;

    /**
     * Constructs a wrapper.
     * 
     * @param space Actual {@link IXWikiSpace} instance.
     */
    public XWikiSpaceWrapper(IXWikiSpace space)
    {
        this.space = space;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#addPage(java.lang.String, java.lang.String)
     */
    public void addPage(final String title, final String content) throws CommunicationException
    {
        // It is assumed that at this point it has been verified that the
        // given title is unique. (i.e. no page with same title exists)
        XWikiProgressRunner operation = new XWikiProgressRunner()
        {
            public void run(IProgressMonitor monitor) throws InvocationTargetException,
                InterruptedException
            {
                monitor.beginTask("Storing page...", IProgressMonitor.UNKNOWN);
                try {
                    space.addPage(title, content);
                    monitor.done();
                } catch (CommunicationException e) {
                    monitor.done();
                    setComEx(e);
                    throw new InvocationTargetException(e);
                }
            }
        };
        GuiUtils.runOperationWithProgress(operation, null);
        if (operation.getComEx() != null) {
            throw operation.getComEx();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getConnection()
     */
    public IXWikiConnection getConnection()
    {
        return space.getConnection();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getDescriptionAsHtml()
     */
    public String getDescriptionAsHtml()
    {
        try {
            init();
            return space.getDescriptionAsHtml();
        } catch (CommunicationException e) {
            // TODO log this exception
            return "";
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getHomePageId()
     */
    public String getHomePageId()
    {
        try {
            init();
            return space.getHomePageId();
        } catch (CommunicationException e) {
            // TODO log this exception
            return "";
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getKey()
     */
    public String getKey()
    {
        return space.getKey();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getName()
     */
    public String getName()
    {
        return space.getName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getPageByID(java.lang.String)
     */
    public IXWikiPage getPageByID(String pageID)
    {
        return space.getPageByID(pageID);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getPageByTitle(java.lang.String)
     */
    public IXWikiPage searchPage(String pageTitle)
    {
        return space.searchPage(pageTitle);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getPages()
     */
    public Collection<IXWikiPage> getPages()
    {
        try {
            init();
            return space.getPages();
        } catch (CommunicationException e) {
            // TODO log this exception
            return new ArrayList<IXWikiPage>();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getType()
     */
    public String getType()
    {
        return space.getType();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#getUrl()
     */
    public String getUrl()
    {
        return space.getUrl();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#init()
     */
    public void init() throws CommunicationException
    {
        if (!isPagesReady()) {
            XWikiProgressRunner operation = new XWikiProgressRunner()
            {
                public void run(IProgressMonitor monitor) throws InvocationTargetException,
                    InterruptedException
                {
                    monitor.beginTask("Initializing space...", IProgressMonitor.UNKNOWN);
                    try {
                        space.init();
                        monitor.done();
                    } catch (CommunicationException e) {
                        monitor.done();
                        setComEx(e);
                        throw new InvocationTargetException(e);
                    }
                }
            };
            GuiUtils.runOperationWithProgress(operation, null);
            if (operation.getComEx() != null) {
                throw operation.getComEx();
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#isDataReady()
     */
    public boolean isDataReady()
    {
        return space.isDataReady();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#isMasked()
     */
    public boolean isMasked()
    {
        return space.isMasked();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#setMasked()
     */
    public void setMasked(boolean masked)
    {
        space.setMasked(masked);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#isPagesReady()
     */
    public boolean isPagesReady()
    {
        return space.isPagesReady();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#isSummaryReady()
     */
    public boolean isSummaryReady()
    {
        return space.isSummaryReady();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#removeChildPage(java.lang.String)
     */
    public void removeChildPage(final String pageId) throws CommunicationException
    {
        if (space.getPageByID(pageId) != null) {
            XWikiProgressRunner operation = new XWikiProgressRunner()
            {
                public void run(IProgressMonitor monitor) throws InvocationTargetException,
                    InterruptedException
                {
                    monitor.beginTask("Deleting page...", IProgressMonitor.UNKNOWN);
                    try {
                        space.removeChildPage(pageId);
                        monitor.done();
                    } catch (CommunicationException e) {
                        monitor.done();
                        setComEx(e);
                        throw new InvocationTargetException(e);
                    }
                }
            };
            GuiUtils.runOperationWithProgress(operation, null);
            if (operation.getComEx() != null) {
                throw operation.getComEx();
            }
        } else {
            // This is almost impossible.
            GuiUtils.reportError(true, "Internal Error", "Could not find the specified page");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#save()
     */
    public IXWikiSpace save() throws CommunicationException
    {
        XWikiProgressRunner operation = new XWikiProgressRunner()
        {
            public void run(IProgressMonitor monitor) throws InvocationTargetException,
                InterruptedException
            {
                monitor.beginTask("Saving space...", IProgressMonitor.UNKNOWN);
                try {
                    space.save();
                    monitor.done();
                } catch (CommunicationException e) {
                    monitor.done();
                    setComEx(e);
                    throw new InvocationTargetException(e);
                }
            }
        };
        GuiUtils.runOperationWithProgress(operation, null);
        if (operation.getComEx() != null) {
            throw operation.getComEx();
        } else {
            return space;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#setName(java.lang.String)
     */
    public void setName(String newName)
    {
        space.setName(newName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiSpace#updateChildPage(java.lang.String,
     *      java.lang.String, java.lang.String, int)
     */
    public void updateChildPage(String pageId, String title, String content, int version)
        throws CommunicationException
    {
        // TODO Implement this.

    }

}
