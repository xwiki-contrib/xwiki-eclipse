package org.xwiki.xeclipse.model.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.Space;
import org.eclipse.core.runtime.ListenerList;
import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.IXWikiConnectionListener;
import org.xwiki.xeclipse.model.XWikiConnectionException;

/**
 * This is the base class for different type of XWiki connections
 */
public abstract class AbstractXWikiConnection implements IXWikiConnection, Serializable
{
    private String id;

    private String serverUrl;

    private String userName;

    protected transient boolean isDisposed;

    private transient ListenerList connectionListenerList;

    /**
     * Constructor.
     * 
     * @param serverUrl The url where the XML RPC endpoint is located.
     * @param userName The user name to be used when connecting to the remote server.
     * @throws XWikiConnectionException 
     */
    public AbstractXWikiConnection(String serverUrl, String userName) throws XWikiConnectionException
    {
        this.serverUrl = serverUrl;
        this.userName = userName;
        id = UUID.randomUUID().toString();

        init();
    }

    /**
     * Initialize transient fields.
     */
    protected void init() throws XWikiConnectionException
    {
        isDisposed = false;
        connectionListenerList = new ListenerList();
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

    /**
     * @param page The page to be saved.
     * @return The page after that has been saved with all the information updated (version, etc.).
     * @throws XWikiConnectionException
     */
    abstract Page savePage(Page page) throws XWikiConnectionException;

    abstract boolean isPageDirty(String pageId);

    abstract boolean isPageConflict(String pageId);
    
    abstract boolean isPageCached(String pageId);

    abstract Page getRawPage(String pageId) throws XWikiConnectionException;

    abstract Space getRawSpace(String key);

    // /////////////////////////// Event listeners management /////////////////////////////

    public void addConnectionEstablishedListener(IXWikiConnectionListener listener)
    {
        connectionListenerList.add(listener);
    }

    public void removeConnectionEstablishedListener(IXWikiConnectionListener listener)
    {
        connectionListenerList.remove(listener);
    }

    protected void fireConnectionEstablished()
    {
        final Object[] listeners = connectionListenerList.getListeners();
        for (int i = 0; i < listeners.length; i++) {
            final IXWikiConnectionListener listener = (IXWikiConnectionListener) listeners[i];
            listener.connectionEstablished(this);
        }
    }

    protected void fireConnectionClosed()
    {
        final Object[] listeners = connectionListenerList.getListeners();
        for (int i = 0; i < listeners.length; i++) {
            final IXWikiConnectionListener listener = (IXWikiConnectionListener) listeners[i];
            listener.connectionClosed(this);
        }
    }       
}
