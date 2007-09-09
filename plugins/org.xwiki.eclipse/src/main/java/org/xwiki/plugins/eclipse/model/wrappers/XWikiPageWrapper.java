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
import java.util.Date;

import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xwiki.plugins.eclipse.model.IXWikiPage;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.util.GuiUtils;
import org.xwiki.plugins.eclipse.util.XWikiProgressRunner;

/**
 * Implentation of Decorator Pattern for adding GUI icing for underlying {@link IXWikiPage}.
 */
public class XWikiPageWrapper implements IXWikiPage
{
    /**
     * Actual {@link IXWikiPage} being wrapped.
     */
    private IXWikiPage page;

    /**
     * Constructs a wrapper.
     * 
     * @param page Actual {@link IXWikiPage} instance.
     */
    public XWikiPageWrapper(IXWikiPage page)
    {
        this.page = page;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getContent()
     */
    public String getContent()
    {
        try {
            init();
            return page.getContent();
        } catch (SwizzleConfluenceException e) {
            // TODO log this exception.
            return "";
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getContentStatus()
     */
    public String getContentStatus()
    {
        try {
            init();
            return page.getContentStatus();
        } catch (SwizzleConfluenceException e) {
            // TODO log this exception.
            return "";
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getCreated()
     */
    public Date getCreated()
    {
        try {
            init();
            return page.getCreated();
        } catch (SwizzleConfluenceException e) {
            // TODO log this exception.
            return new Date();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getCreator()
     */
    public String getCreator()
    {
        try {
            init();
            return page.getCreator();
        } catch (SwizzleConfluenceException e) {
            // TODO log this exception.
            return "";
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getId()
     */
    public String getId()
    {
        return page.getId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getLastModifier()
     */
    public String getLastModifier()
    {
        try {
            init();
            return page.getLastModifier();
        } catch (SwizzleConfluenceException e) {
            // TODO log this exception.
            return "";
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getLocks()
     */
    public int getLocks()
    {
        try {
            init();
            return page.getLocks();
        } catch (SwizzleConfluenceException e) {
            // TODO log this exception.
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getModified()
     */
    public Date getModified()
    {
        try {
            init();
            return page.getModified();
        } catch (SwizzleConfluenceException e) {
            // TODO log this exception.
            return new Date();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getParentId()
     */
    public String getParentId()
    {
        try {
            init();
            return page.getParentId();
        } catch (SwizzleConfluenceException e) {
            // TODO log this exception.
            return "";
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getParentSpace()
     */
    public IXWikiSpace getParentSpace()
    {
        return page.getParentSpace();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getParentSpaceKey()
     */
    public String getParentSpaceKey()
    {
        return page.getParentSpaceKey();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getTitle()
     */
    public String getTitle()
    {
        return page.getTitle();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getUrl()
     */
    public String getUrl()
    {
        return page.getUrl();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#getVersion()
     */
    public int getVersion()
    {
        try {
            init();
            return page.getVersion();
        } catch (SwizzleConfluenceException e) {
            // TODO log this exception.
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#init()
     */
    public void init() throws SwizzleConfluenceException
    {
        if (!isDataReady()) {
            XWikiProgressRunner operation = new XWikiProgressRunner()
            {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
                 */
                public void run(IProgressMonitor monitor) throws InvocationTargetException,
                    InterruptedException
                {
                    monitor.beginTask("Retrieving Page...", IProgressMonitor.UNKNOWN);
                    try {
                        page.init();
                        monitor.done();
                    } catch (SwizzleConfluenceException e) {
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
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#isCurrent()
     */
    public boolean isCurrent()
    {
        try {
            init();
            return page.isCurrent();
        } catch (SwizzleConfluenceException e) {
            // TODO log this exception.
            return true;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#isDataReady()
     */
    public boolean isDataReady()
    {
        return page.isDataReady();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#isHomePage()
     */
    public boolean isHomePage()
    {
        try {
            init();
            return page.isHomePage();
        } catch (SwizzleConfluenceException e) {
            // TODO log this exception.
            return false;
        }
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
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#save()
     */
    public IXWikiPage save() throws SwizzleConfluenceException
    {
        XWikiProgressRunner operation = new XWikiProgressRunner()
        {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
             */
            public void run(IProgressMonitor monitor) throws InvocationTargetException,
                InterruptedException
            {
                monitor.beginTask("Saving Page...", IProgressMonitor.UNKNOWN);
                try {
                    page.save();
                    monitor.done();
                } catch (SwizzleConfluenceException e) {
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
            return page;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#setContent(java.lang.String)
     */
    public void setContent(String newContent)
    {
        page.setContent(newContent);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#setParentId(java.lang.String)
     */
    public void setParentId(String newParentId)
    {
        page.setParentId(newParentId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiPage#setTitle(java.lang.String)
     */
    public void setTitle(String newTitle)
    {
        page.setTitle(newTitle);
    }
}
