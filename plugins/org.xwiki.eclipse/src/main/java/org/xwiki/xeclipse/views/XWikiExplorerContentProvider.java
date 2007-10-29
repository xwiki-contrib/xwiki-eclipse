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
