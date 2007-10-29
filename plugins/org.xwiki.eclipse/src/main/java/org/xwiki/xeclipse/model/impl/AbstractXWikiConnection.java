package org.xwiki.xeclipse.model.impl;

import java.util.UUID;

import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.Space;
import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.XWikiConnectionException;

/**
 * This is the base class for different type of XWiki connections 
 */
public abstract class AbstractXWikiConnection implements IXWikiConnection
{
    private String id;

    private String serverUrl;

    private String userName;

    protected transient boolean isDisposed;

    /**
     * Constructor.
     * 
     * @param serverUrl The url where the XML RPC endpoint is located.
     * @param userName The user name to be used when connecting to the remote server.
     */
    public AbstractXWikiConnection(String serverUrl, String userName)
    {
        this.serverUrl = serverUrl;
        this.userName = userName;
        id = UUID.randomUUID().toString();

        init();
    }

    /**
     * Initialize transient fields.
     */
    private void init()
    {
        isDisposed = false;
    }

    /**
     * {@inheritDoc}
     */
    public String getId()
    {
        return id;
    }

    public String getServerUrl()
    {
        return serverUrl;
    }

    public String getUserName()
    {
        return userName;
    }
    
    protected void assertNotDisposed()
    {
        if (isDisposed) {
            throw new Error("XWiki Connection has been disposed");
        }
    }

    abstract void savePage(Page page) throws XWikiConnectionException;
    
    abstract boolean isPageDirty(String pageId);

    abstract boolean isPageConflict(String pageId);

    abstract Page getRawPage(String pageId) throws XWikiConnectionException;

    abstract Space getRawSpace(String key);
}
