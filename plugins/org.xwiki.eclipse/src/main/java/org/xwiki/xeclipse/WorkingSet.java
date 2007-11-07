package org.xwiki.xeclipse;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.IXWikiPage;
import org.xwiki.xeclipse.model.IXWikiSpace;

public class WorkingSet implements Serializable
{
    private static final long serialVersionUID = 2374252363910602198L;
    private String name;
    private Set<String> ids;
    
    public WorkingSet(String name)
    {
        this.name = name;
        ids = new HashSet<String>();
    }
    
    public void add(IXWikiConnection xwikiConnection) {
        ids.add(getId(xwikiConnection));       
    }
    
    public void add(IXWikiSpace xwikiSpace) {
        ids.add(getId(xwikiSpace));
    }
    
    public void add(IXWikiPage xwikiPage) {
        ids.add(getId(xwikiPage));
    }
    
    public void remove(IXWikiConnection xwikiConnection) {
        ids.remove(getId(xwikiConnection));       
    }
    
    public void remove(IXWikiSpace xwikiSpace) {
        ids.remove(getId(xwikiSpace));
    }
    
    public void remove(IXWikiPage xwikiPage) {
        ids.remove(getId(xwikiPage));
    }
    
    public boolean contains(IXWikiConnection xwikiConnection) {
        return ids.contains(getId(xwikiConnection));       
    }
    
    public boolean contains(IXWikiSpace xwikiSpace) {
        return ids.contains(getId(xwikiSpace));
    }
    
    public boolean contains(IXWikiPage xwikiPage) {
        return ids.contains(getId(xwikiPage));
    }
    
    
    private String getId(IXWikiConnection xwikiConnection) {
        return xwikiConnection.getId();
    }
    
    private String getId(IXWikiSpace xwikiSpace) {
        return String.format("%s#%s", xwikiSpace.getConnection().getId(), xwikiSpace.getKey());
    }
    
    private String getId(IXWikiPage xwikiPage) {
        return String.format("%s#%s", xwikiPage.getConnection().getId(), xwikiPage.getId());
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
}   
