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
package org.xwiki.xeclipse.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.DeferredTreeContentManager;
import org.xwiki.xeclipse.XWikiConnectionManager;

public class XWikiExplorerContentProvider implements ITreeContentProvider
{
    private DeferredTreeContentManager manager;
    
    public XWikiExplorerContentProvider(TreeViewer treeViewer)
    {
        manager = new DeferredTreeContentManager(this, treeViewer);
    }

    public Object[] getChildren(Object parentElement)
    {
        return manager.getChildren(parentElement);
    }

    public Object getParent(Object element)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasChildren(Object element)
    {
        return manager.mayHaveChildren(element);
    }

    public Object[] getElements(Object inputElement)
    { 
        return XWikiConnectionManager.getDefault().getConnections().toArray();
    }

    public void dispose()
    {
        // TODO Auto-generated method stub
        
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        // TODO Auto-generated method stub
        
    }


}
