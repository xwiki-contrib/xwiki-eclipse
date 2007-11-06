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

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.IXWikiPage;
import org.xwiki.xeclipse.model.IXWikiSpace;

/**
 * Adapter factory for adapting XWiki Eclipse model object to different workbench interfaces.
 */
public class AdapterFactory implements IAdapterFactory
{
    /**
     * The adapter for XWiki connections.
     */
    private IDeferredWorkbenchAdapter xwikiConnectionAdapter = new XWikiConnectionAdapter();

    /**
     * The adapter for XWiki spaces.
     */
    private IDeferredWorkbenchAdapter xwikiSpaceAdapter =
        new XWikiSpaceAdapter();

    /**
     * The adapter for XWiki pages.
     */
    private IWorkbenchAdapter xwikiPageAdapter = new XWikiPageAdapter();

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(Object, Class)
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter(Object adaptableObject, Class adapterType)
    {
        if (adapterType == IDeferredWorkbenchAdapter.class
            && adaptableObject instanceof IXWikiConnection) {
            return xwikiConnectionAdapter;
        }

        if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof IXWikiConnection) {
            return xwikiConnectionAdapter;
        }
        
        if (adapterType == IWorkbenchAdapter2.class && adaptableObject instanceof IXWikiConnection) {
            return xwikiConnectionAdapter;
        }
        
        if (adapterType == IDeferredWorkbenchAdapter.class
            && adaptableObject instanceof IXWikiSpace) {
            return xwikiSpaceAdapter;
        }

        if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof IXWikiSpace) {
            return xwikiSpaceAdapter;
        }
        
        if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof IXWikiPage) {
            return xwikiPageAdapter;
        }
                
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    @SuppressWarnings("unchecked")
    public Class[] getAdapterList()
    {
        return new Class[] {IDeferredWorkbenchAdapter.class, IWorkbenchAdapter.class, IWorkbenchAdapter2.class};
    }

}
