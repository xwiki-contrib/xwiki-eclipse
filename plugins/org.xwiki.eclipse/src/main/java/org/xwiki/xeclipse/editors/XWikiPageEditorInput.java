package org.xwiki.xeclipse.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.xwiki.xeclipse.model.IXWikiPage;

public class XWikiPageEditorInput implements IEditorInput
{
    private IXWikiPage xwikiPage;
    
    public XWikiPageEditorInput(IXWikiPage page) {
        this.xwikiPage = page;
    }
    
    public boolean exists()
    { 
        return false;
    }

    public ImageDescriptor getImageDescriptor()
    {      
        return null;
    }

    public String getName()
    {
        return xwikiPage.getId();
    }

    public IPersistableElement getPersistable()
    {
        return null;
    }

    public String getToolTipText()
    {
        return xwikiPage.getTitle();
    }

    public Object getAdapter(Class adapter)
    {
        return null;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(super.equals(obj)) {
            return true;
        }
        
        if(!(obj instanceof XWikiPageEditorInput)) {
            return false;
        }
        
        XWikiPageEditorInput other = (XWikiPageEditorInput) obj;
        
        return xwikiPage.getId().equals(other.xwikiPage.getId());
    }

    @Override
    public int hashCode()
    {
        return xwikiPage.getId().hashCode();        
    }

    public IXWikiPage getXWikiPage()
    {
        return xwikiPage;
    }    
    
    public void setXWikiPage(IXWikiPage xwikiPage) {
        this.xwikiPage = xwikiPage;
    }
}
