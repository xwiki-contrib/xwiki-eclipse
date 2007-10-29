package org.xwiki.xeclipse.model;

import java.util.Date;

/**
 * This interface provides access to all page data and related information.
 */
public interface IXWikiPage
{
    public String getId();
    public int getLocks();
    public String getParentId();
    public String getSpace();
    public String getTitle();
    public String getUrl();
    public String getCreator();       
    public String getContentStatus();
    public int getVersion();
    public Date getModified();
    public String getModifier();
    
    public String getContent();
    public void setContent(String content);
    
    /**
     * @return true if the page has been modified locally but not yet synchronized with the remote XWiki instance.
     * @throws XWikiConnectionException 
     */
    public boolean isDirty();
    
    /**
     * @return true if the page has been modified both locally and remotely.
     * @throws XWikiConnectionException 
     */
    public boolean isConflict();
    
    /**
     * Save the page content.
     * 
     * @throws XWikiConnectionException
     */
    public void save() throws XWikiConnectionException;
}
