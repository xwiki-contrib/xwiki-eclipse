package org.xwiki.xeclipse.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.Space;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.eclipse.core.runtime.ListenerList;
import org.xwiki.xeclipse.model.IXWikiConnectionListener;
import org.xwiki.xeclipse.model.IXWikiPage;
import org.xwiki.xeclipse.model.IXWikiSpace;
import org.xwiki.xeclipse.model.XWikiConnectionException;

public class XWikiPlainConnection extends AbstractXWikiConnection
{
    private transient IXWikiDAO remoteDAO;

    transient private ListenerList connectionListenerList;

    /**
     * Constructor.
     * 
     * @param serverUrl The remote URL for the XWiki XML-RPC service.
     * @param username The user name to be used when connecting to the remote server.
     * @throws XWikiConnectionException
     */
    public XWikiPlainConnection(String serverUrl, String username)
        throws XWikiConnectionException
    {
        super(serverUrl, username);

        init();
    }

    /**
     * Initialization of transient fields.
     * 
     * @throws XWikiConnectionException
     */
    private void init() throws XWikiConnectionException
    {
        connectionListenerList = new ListenerList();
    }

 
    /**
     * {@inheritDoc}
     */
    public void connect(String password) throws XWikiConnectionException
    {
        assertNotDisposed();

        if (isConnected()) {
            return;
        }

        try {
            remoteDAO = new XWikiRemoteDAO(getServerUrl(), getUserName(), password);         
        } catch (XWikiDAOException e) {
            if (remoteDAO != null) {
                try {
                    remoteDAO.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            remoteDAO = null;            

            throw new XWikiConnectionException(e);
        }

        fireConnectionEstablished();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws XWikiConnectionException
     */
    public void disconnect() throws XWikiConnectionException
    {
        assertNotDisposed();

        if (!isConnected()) {
            return;
        }

        try {
            remoteDAO.close();
            remoteDAO = null;
        } catch (XWikiDAOException e) {
            e.printStackTrace();
        }

        fireConnectionClosed();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws XWikiConnectionException
     */
    public void dispose() throws XWikiConnectionException
    {
        disconnect();
      
        isDisposed = true;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<IXWikiSpace> getSpaces() throws XWikiConnectionException
    {
        assertNotDisposed();

        Collection<IXWikiSpace> result = new ArrayList<IXWikiSpace>();
        try {
            List<SpaceSummary> spaceSummaries;
            if (isConnected()) {
                spaceSummaries = remoteDAO.getSpaces();
                for (SpaceSummary spaceSummary : spaceSummaries) {
                    result.add(new XWikiSpace(this, spaceSummary.getKey(), spaceSummary.toMap()));                    
                }
            } 
        } catch (Exception e) {
            throw new XWikiConnectionException(e);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws XWikiConnectionException
     */
    public boolean isConnected()
    {
        assertNotDisposed();

        return remoteDAO != null;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<IXWikiPage> getPages(String spaceKey) throws XWikiConnectionException
    {
        assertNotDisposed();

        Collection<IXWikiPage> result = new ArrayList<IXWikiPage>();
        try {
            List<PageSummary> pageSummaries = null;
            if (isConnected()) {
                pageSummaries = remoteDAO.getPages(spaceKey);
            } 

            if (pageSummaries != null) {
                for (PageSummary pageSummary : pageSummaries) {
                    result.add(new XWikiPage(this, pageSummary.getId(), pageSummary.toMap()));
                }
            }
        } catch (Exception e) {
            throw new XWikiConnectionException(e);
        }

        return result;
    }

    public IXWikiPage getPage(String pageId) throws XWikiConnectionException
    {
        assertNotDisposed();

        Page page = getRawPage(pageId);
        
        return page != null ? new XWikiPage(this, pageId, page.toMap()) : null;
    }

    /**
     * {@inheritDoc}
     */
    Page getRawPage(String pageId) throws XWikiConnectionException
    {
        assertNotDisposed();

        try {            
            if (isConnected()) {
                return remoteDAO.getPage(pageId);             
            }
        } catch (Exception e) {
            throw new XWikiConnectionException(e);
        }
        
        return null;
    }

    /*
     * For the moment we don't retrieve full space information. Since we store only page summary
     * typically all the information is already provided. However this will be implemented in the
     * same way as it is implemented for pages.
     */
    Space getRawSpace(String key)
    {
        return null;
    }

    /**
     * Save the page locally, propagating the changes to the remote XWiki instance if working in
     * "online" mode.
     * 
     * @param page The page to be saved.
     * @throws XWikiConnectionException
     */
    void savePage(Page page) throws XWikiConnectionException
    {
        assertNotDisposed();

        try {
            remoteDAO.storePage(page);            
        } catch (Exception e) {
            throw new XWikiConnectionException(e);
        }
    }

    /**
     * @param pageId
     * @return true if the page with the given id is marked as dirty (i.e., modified locally)
     * @throws XWikiConnectionException
     */
    boolean isPageDirty(String pageId)
    {
        assertNotDisposed();

        return false;
    }

    /**
     * @param pageId
     * @return true if the page with the given id is marked as conflict (i.e., modified locally and
     *         remotely)
     * @throws XWikiConnectionException
     */
    boolean isPageConflict(String pageId)
    {
        assertNotDisposed();

        return false;
    }

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

    /**
     * USED ONLY FOR UNIT TESTING
     * 
     * @return
     */
    IXWikiDAO getRemoteDAO()
    {
        return remoteDAO;
    }
}
