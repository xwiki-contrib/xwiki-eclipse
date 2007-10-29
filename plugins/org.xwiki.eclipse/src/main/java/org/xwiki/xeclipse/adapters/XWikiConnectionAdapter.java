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
package org.xwiki.xeclipse.adapters;

import java.util.Collection;

import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.xwiki.xeclipse.XWikiEclipseConstants;
import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.IXWikiSpace;
import org.xwiki.xeclipse.model.XWikiConnectionException;

/**
 * The adapter for XWiki connections
 */
public class XWikiConnectionAdapter implements IDeferredWorkbenchAdapter
{
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(Object)
     */
    public Object[] getChildren(Object o)
    {
        if (o instanceof IXWikiConnection) {
            IXWikiConnection xwikiConnection = (IXWikiConnection) o;
            Collection<IXWikiSpace> result = null;

            try {
                result = xwikiConnection.getSpaces();
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
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(Object)
     */
    public String getLabel(Object object)
    {
        if (object instanceof IXWikiConnection) {
            IXWikiConnection xwikiConnection = (IXWikiConnection) object;

            return String
                .format("%s@%s %s", xwikiConnection.getUserName(),
                    xwikiConnection.getServerUrl(), xwikiConnection.isConnected() ? "[Connected]"
                        : "[Disconnected]");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.progress.IWorkbenchAdapter#getP
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
     * {@inheritDoc} Always returns true because an XWikiConnection is supposed to contain spaces.
     * 
     * @see org.eclipse.ui.progress.IDeferredWorkbenchAdapter#isContainer()
     */
    public boolean isContainer()
    {
        return true;
    }
}
