package org.xwiki.xeclipse.model.impl;

import java.util.List;

import org.codehaus.swizzle.confluence.ConfluenceException;
import org.codehaus.swizzle.confluence.IdentityObjectConvertor;
import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.Space;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.codehaus.swizzle.confluence.SwizzleXWiki;

/**
 * A Data Access Object for accessing a remote XWiki instance. 
 */
public class XWikiRemoteDAO implements IXWikiDAO
{
    private SwizzleXWiki swizzleXWiki;

    /**
     * Constructor. 
     * 
     * @param serverUrl The remote URL for the XWiki XML-RPC service.
     * @param username The user name to be used when connecting to the remote server.
     * @param password The password to be used in order to access the remote account.
     * @throws XWikiDAOException
     */
    public XWikiRemoteDAO(String serverUrl, String username, String password)
        throws XWikiDAOException
    {
        try {
            swizzleXWiki = new SwizzleXWiki(serverUrl);
            swizzleXWiki.login(username, password);

            try {
                // Workaround for finding out xwiki version (server)
                // (compatibility with <= XWiki 1.1.m4)
                swizzleXWiki.setNoConversion();
            } catch (SwizzleConfluenceException e) {
                // Assume older version of xwiki and turn-off conversion on client.
                swizzleXWiki.setConvertor(new IdentityObjectConvertor());
            }

        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }

    }

    /**
     * Close the connection to the remote XWiki instance.
     * @throws XWikiDAOException
     */
    public void close() throws XWikiDAOException
    {
        try {
            swizzleXWiki.logout();
        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }
    }

    /**
     * @return A list of the remotely available spaces.
     * @throws XWikiDAOException
     */
    @SuppressWarnings("unchecked")
    public List<SpaceSummary> getSpaces() throws XWikiDAOException
    {
        try {
            return swizzleXWiki.getSpaces();
        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }
    }

    /**
     * @param key The space key.
     * @return The information about the remote space.
     * @throws XWikiDAOException
     */
    public Space getSpace(String key) throws XWikiDAOException
    {
        try {
            return swizzleXWiki.getSpace(key);            
        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }
    }

    /**
     * @param spaceKey The space key.
     * @return The page summaries for all the pages available in the given space. 
     * @throws XWikiDAOException
     */
    @SuppressWarnings("unchecked")
    public List<PageSummary> getPages(String spaceKey) throws XWikiDAOException
    {
        try {            
            return swizzleXWiki.getPages(spaceKey);
        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }
    }

    /**
     * @param id The page id.
     * @return All the page information for the page identified by the given id.
     * @throws XWikiDAOException
     */
    public Page getPage(String id) throws XWikiDAOException
    {
        try {
            return swizzleXWiki.getPage(id);
        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }
    }

    /**
     * Stores a page on the remote server.
     * 
     * @param page The page information to be stored.
     * @throws XWikiDAOException
     */
    public void storePage(Page page) throws XWikiDAOException
    {
        try {            
            swizzleXWiki.storePage(page);            
        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }
    }

    public Space createSpace(String key, String name, String description) throws XWikiDAOException
    {
        Space space = new Space();
        space.setKey(key);
        space.setName(name);
        space.setDescription(description);
                
        try {
            space = swizzleXWiki.addSpace(space);            
        } catch (SwizzleConfluenceException e) {            
            e.printStackTrace();
            throw new XWikiDAOException(e);
        }
        
        return space;
    }

    public Page createPage(String spaceKey, String title, String content) throws XWikiDAOException
    {
        Page page = new Page();
        page.setSpace(spaceKey);
        page.setTitle(title);
        page.setContent(content);
        
        try {
            page = swizzleXWiki.storePage(page);
        } catch (SwizzleConfluenceException e) {            
            e.printStackTrace();
            throw new XWikiDAOException(e);
        }
        
        return page;
    }

    public void removePage(String id) throws XWikiDAOException
    {
        try {
            swizzleXWiki.removePage(id);
        } catch (SwizzleConfluenceException e) {         
            e.printStackTrace();
            throw new XWikiDAOException(e);
        }
        
    }

    public void removeSpace(String key) throws XWikiDAOException
    {
        try {
            swizzleXWiki.removeSpace(key);
        } catch (SwizzleConfluenceException e) {         
            e.printStackTrace();
            throw new XWikiDAOException(e);
        }
    }
    
}
