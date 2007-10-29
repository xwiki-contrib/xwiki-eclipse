package org.xwiki.xeclipse.model;

import java.util.Collection;

/**
 * This interface provides full access to space information.
 */
public interface IXWikiSpace
{        
    public String getKey();
    public String getName();
    public String getType();
    public String getUrl();        
    public String getDescription();    
    public String getHomePage();
    
    public Collection<IXWikiPage> getPages() throws XWikiConnectionException;       
}
