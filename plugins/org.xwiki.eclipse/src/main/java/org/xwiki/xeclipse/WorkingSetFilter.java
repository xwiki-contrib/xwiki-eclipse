package org.xwiki.xeclipse;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.IXWikiPage;
import org.xwiki.xeclipse.model.IXWikiSpace;

public class WorkingSetFilter extends ViewerFilter {
    private WorkingSet workingSet;
    
    public WorkingSetFilter(WorkingSet workingSet) {
        this.workingSet = workingSet;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
        if(element instanceof IXWikiConnection) {
            IXWikiConnection connection = (IXWikiConnection) element;
            return workingSet.contains(connection);
        }
        
        if(element instanceof IXWikiSpace) {
            IXWikiSpace space = (IXWikiSpace) element;
            return workingSet.contains(space);
        }
        
        if(element instanceof IXWikiPage) {
            IXWikiPage page = (IXWikiPage) element;
            return workingSet.contains(page);
        }
        
        return false;
    }
    
}