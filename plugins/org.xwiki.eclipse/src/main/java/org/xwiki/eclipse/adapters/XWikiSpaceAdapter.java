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
package org.xwiki.eclipse.adapters;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.xwiki.eclipse.XWikiEclipseConstants;
import org.xwiki.eclipse.XWikiEclipsePageIndex;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.model.IXWikiSpace;
import org.xwiki.eclipse.model.XWikiConnectionException;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;

/**
 * The adapter for XWiki spaces
 */
public class XWikiSpaceAdapter implements IDeferredWorkbenchAdapter
{
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(Object)
     */
    public Object[] getChildren(Object o)
    {
        if (o instanceof IXWikiSpace) {
            IXWikiSpace space = (IXWikiSpace) o;
            Collection<IXWikiPage> result = null;

            try {
                result = space.getPages();

                /* Add pages to the local index */
                for (IXWikiPage page : result) {
                    XWikiEclipsePageIndex.getDefault().addPage(page);
                }
            } catch (XWikiConnectionException e) {
                e.printStackTrace();
            }

            return result != null ? result.toArray() : XWikiEclipseConstants.NO_OBJECTS;
        }

        return XWikiEclipseConstants.NO_OBJECTS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(Object)
     */
    public ImageDescriptor getImageDescriptor(Object object)
    {
        return XWikiEclipsePlugin.getImageDescriptor(XWikiEclipseConstants.XWIKI_SPACE_ICON);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(Object)
     */
    public String getLabel(Object object)
    {
        if (object instanceof IXWikiSpace) {
            IXWikiSpace space = (IXWikiSpace) object;

            return space.getKey();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(Object)
     */
    public Object getParent(Object o)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.progress.IDeferredWorkbenchAdapter#fetchDeferredChildren(Object,
     *      IElementCollector, IProgressMonitor)
     */
    public void fetchDeferredChildren(Object object, IElementCollector collector,
        IProgressMonitor monitor)
    {
        collector.add(getChildren(object), monitor);
        collector.done();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.progress.IDeferredWorkbenchAdapter#getRule(Object)
     */
    public ISchedulingRule getRule(Object object)
    {
        return null;
    }

    /**
     * {@inheritDoc} Always returns true because an XWiki space is supposed to contain pages.
     * 
     * @see org.eclipse.ui.progress.IDeferredWorkbenchAdapter#isContainer()
     */
    public boolean isContainer()
    {
        return true;
    }
}
