/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */
package org.xwiki.eclipse.model.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.Space;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.xwiki.eclipse.XWikiEclipseEvent;
import org.xwiki.eclipse.XWikiEclipseNotificationCenter;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.model.IXWikiSpace;
import org.xwiki.eclipse.model.XWikiConnectionException;

public class XWikiCachedConnection extends AbstractXWikiConnection implements Serializable
{
    private static final long serialVersionUID = -3186885585670670854L;

    private File cacheDir;

    private transient IXWikiDAO remoteDAO;

    private transient IXWikiCacheDAO cacheDAO;

    /**
     * Constructor.
     * 
     * @param serverUrl The remote URL for the XWiki XML-RPC service.
     * @param username The user name to be used when connecting to the remote server.
     * @throws XWikiConnectionException
     */
    public XWikiCachedConnection(String serverUrl, String username, File cacheDir)
        throws XWikiConnectionException
    {
        super(serverUrl, username);
        this.cacheDir = cacheDir;

        init();
    }

    /**
     * Initialization of transient fields.
     * 
     * @throws XWikiConnectionException
     */
    protected void init() throws XWikiConnectionException
    {
        super.init();

        try {
            cacheDAO = new DiskCacheDAO(new File(cacheDir, getId()));
        } catch (Exception e) {
            throw new XWikiConnectionException(e);
        }
    }

    /**
     * Syncrhonize a page. Synchronization works in the following way:
     * 
     * Let remote page be (v, c) where v is version and c is content.
     * 
     * Let cached page be (v, c, d) where v is version, c is content and d is dirty flag.
     * 
     * Case 1) Remote page = null; Cached page = (v, c, _). This is a page that has been created
     * while being offline, so it doesn't yet exist remotely. Just store the page as it is remotely,
     * update the cached version and clear the dirty and conflict flags.
     * 
     * Case 2.1) Remote page = (v, c); Cached Page = (v, c1, true). The page exists in the local
     * cache and has been modified locally. Since both the cached and remote page have the same
     * version v, this means that remotely nobody has modified the page so we can safely store the
     * page remotely and update the local cache with this new version.
     * 
     * Case 2.2) Remote page = (v, c); Cached Page = (v, c, false). Remote and cached pages are
     * already synchronized (i.e., they are already at version v with no local changes) so there is
     * nothing to do.
     * 
     * Case 3.1) Remote page = (v+n, c); Cached Page = (v, c1, false). The page has been remotely
     * changed but not locally. So simply store the remote page in the local cache.
     * 
     * Case 3.2) Remote page = (v+n, c); Cached Page = (v, c1, true). A page previously cached at
     * version v has been remotely modified. What happens now is that the local cached page is
     * updated to version v+n and its content is set to the diff between the local and remote page.
     * No remote update is performed, the dirty flag is kept to true and the conflict flag is set to
     * true as well. Remote update It will be done to the next synchronization, provided that nobody
     * version number v+n matches (i.e., we are in case 2.x)
     * 
     * This method is written defensively. Actually cases where the dirty flag for a cached page is
     * false should never happen since synchronize is (or should be) called only on dirty pages.
     * 
     * @param pageId The id of the page to be synchronized.
     * @throws XWikiDAOException
     * @throws XWikiConnectionException
     */
    Page synchronize(String pageId) throws XWikiDAOException, XWikiConnectionException
    {
        assertNotDisposed();

        // Get the cached and remote version of the page. If there is no cached version then there
        // is nothing to synchronize so just return.
        Page cachedPage = cacheDAO.getPage(pageId);
        if (cachedPage == null) {
            return null;
        }

        // If we are not connected then do nothing.
        if (!isConnected()) {
            return cachedPage;
        }

        // Get the remote page
        Page remotePage = remoteDAO.getPage(pageId);

        // This is case 1
        if (remotePage == null) {
            remoteDAO.storePage(cachedPage);

            // Retrieve again the stored remote page in order to have all the attributes correctly
            // set by the server (i.e., version etc.), and cache it again.
            remotePage = remoteDAO.getPage(pageId);
            cacheDAO.storePage(remotePage);
            cacheDAO.setDirty(pageId, false);
            cacheDAO.setConflict(pageId, false);

            return remotePage;
        }

        // This is case 2.x
        if (remotePage.getVersion() == cachedPage.getVersion()) {
            // This is case 2.1
            if (cacheDAO.isDirty(pageId)) {
                remoteDAO.storePage(cachedPage);

                // Retrieve again the stored remote page in order to have all the attributes
                // correctly set by the server (i.e., version etc.), and cache it again.
                remotePage = remoteDAO.getPage(pageId);
                cacheDAO.storePage(remotePage);
                cacheDAO.setDirty(pageId, false);
                cacheDAO.setConflict(pageId, false);
            }
        } else {
            // This is case 3.2
            if (cacheDAO.isDirty(pageId)) {
                // TODO: Do a more intelligent and helpful diff
                String diff =
                    String.format(">>> LOCAL >>>\n%s\n>>> REMOTE >>>\n%s", cachedPage
                        .getContent(), remotePage.getContent());
                remotePage.setContent(diff);

                cacheDAO.storePage(remotePage);
                cacheDAO.setDirty(pageId, true);
                cacheDAO.setConflict(pageId, true);
            } else {
                // This is case 3.1
                cacheDAO.storePage(remotePage);
                cacheDAO.setDirty(pageId, false);
                cacheDAO.setConflict(pageId, false);
            }
        }

        return remotePage;
    }

    /**
     * Synchronize all the cached pages that have local modifications.
     * 
     * @throws XWikiDAOException
     * @throws XWikiConnectionException
     */
    void synchronizeAll() throws XWikiDAOException, XWikiConnectionException
    {
        /*
         * This is necessary in order to avoid concurrent modification to the dirtyPages set when
         * synchronizing. In fact, in synchronize we remove elements from cacheDAO.getDirtyPages()
         * and we cannot use it directly for iterating.
         */
        Set<String> dirtyPages = new HashSet<String>(cacheDAO.getDirtyPages());
        for (String pageId : dirtyPages) {
            synchronize(pageId);
        }

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

            // Synchronize all dirty pages
            synchronizeAll();
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

        XWikiEclipseNotificationCenter.getDefault().fireEvent(this,
            XWikiEclipseEvent.CONNECTION_ESTABLISHED, this);
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
        } catch (XWikiDAOException e) {
            e.printStackTrace();
        }
        finally {
        	remoteDAO = null;
        }

        XWikiEclipseNotificationCenter.getDefault().fireEvent(this,
            XWikiEclipseEvent.CONNECTION_CLOSED, this);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws XWikiConnectionException
     */
    public void dispose() throws XWikiConnectionException
    {
        disconnect();

        try {
            cacheDAO.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cacheDAO = null;
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
                    cacheDAO.storeSpaceSummary(spaceSummary);
                }
            } else {
                spaceSummaries = cacheDAO.getSpaces();
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
    public Collection<IXWikiPage> getPages(IXWikiSpace space) throws XWikiConnectionException
    {
        assertNotDisposed();

        Collection<IXWikiPage> result = new ArrayList<IXWikiPage>();
        try {
            List<PageSummary> pageSummaries;
            if (isConnected()) {
                pageSummaries = remoteDAO.getPages(space.getKey());
            } else {
                pageSummaries = cacheDAO.getPages(space.getKey());
            }

            if (pageSummaries != null) {
                for (PageSummary pageSummary : pageSummaries) {
                    result.add(new XWikiPage(this, pageSummary.getId(), space, pageSummary
                        .toMap()));
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
        Space space = getRawSpace(page.getSpace());
        return page != null ? new XWikiPage(this, pageId, new XWikiSpace(this,
            space.getKey(),
            space.toMap()), page.toMap()) : null;
    }

    /**
     * {@inheritDoc}
     */
    Page getRawPage(String pageId) throws XWikiConnectionException
    {
        assertNotDisposed();

        try {
            Page page = cacheDAO.getPage(pageId);
            if (page != null) {
                /* If our cached page is dirty then return it */
                if (cacheDAO.isDirty(pageId)) {
                    return page;
                }
            }

            /*
             * If we are here either there is no cached page, or the cached page is not dirty, so we
             * can grab the latest version of the page and store it in the cache
             */
            if (isConnected()) {
                Page remotePage = remoteDAO.getPage(pageId);
                if (remotePage != null) {
                    cacheDAO.storePage(remotePage);
                    page = remotePage;
                }
            }

            return page;
        } catch (Exception e) {
            throw new XWikiConnectionException(e);
        }
    }

    Space getRawSpace(String key) throws XWikiConnectionException
    {
        assertNotDisposed();

        try {
            Space space = cacheDAO.getSpace(key);
            if (space != null) {
                return space;
            }

            if (isConnected()) {
                space = remoteDAO.getSpace(key);
            }

            return space;
        } catch (Exception e) {
            throw new XWikiConnectionException(e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * Save the page locally, propagating the changes to the remote XWiki instance if working in
     * "online" mode.
     * 
     */
    Page savePage(Page page) throws XWikiConnectionException
    {
        assertNotDisposed();

        try {
            cacheDAO.storePage(page);
            cacheDAO.setDirty(page.getId(), true);

            // Synchronize the page immediately.
            return synchronize(page.getId());
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

        return cacheDAO.isDirty(pageId);
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

        return cacheDAO.isInConflict(pageId);
    }

    private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException
    {
        s.defaultWriteObject();
    }

    private synchronized void readObject(java.io.ObjectInputStream s) throws IOException,
        ClassNotFoundException
    {
        s.defaultReadObject();
        try {
            init();
        } catch (XWikiConnectionException e) {
            e.printStackTrace();
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

    /**
     * USER ONLY FOR UNIT TESTING
     * 
     * @return
     */
    IXWikiCacheDAO getCacheDAO()
    {
        return cacheDAO;
    }

    @Override
    boolean isPageCached(String pageId)
    {
        return cacheDAO.isCached(pageId);
    }

    public IXWikiSpace createSpace(String key, String name, String description)
        throws XWikiConnectionException
    {
        if (isConnected()) {
            try {
                Space space = remoteDAO.createSpace(key, name, description);
                XWikiEclipseNotificationCenter.getDefault().fireEvent(this,
                    XWikiEclipseEvent.SPACE_CREATED, this);
                return new XWikiSpace(this, space.getKey(), space.toMap());
            } catch (XWikiDAOException e) {
                e.printStackTrace();
                throw new XWikiConnectionException(e);
            }
        } else {
            throw new XWikiConnectionException("Cannot create a space if not connected");
        }

    }

    public IXWikiPage createPage(IXWikiSpace space, String name, String content)
        throws XWikiConnectionException
    {
        if (isConnected()) {
            try {
                Page page = remoteDAO.createPage(space.getKey(), name, content);
                XWikiEclipseNotificationCenter.getDefault().fireEvent(this,
                    XWikiEclipseEvent.PAGE_CREATED,
                    new Object[] {space, new XWikiPage(this, page.getId(), space, page.toMap())});
                return new XWikiPage(this, page.getId(), space, page.toMap());
            } catch (XWikiDAOException e) {
                e.printStackTrace();
                throw new XWikiConnectionException(e);
            }
        } else {
            throw new XWikiConnectionException("Cannot create a page if not connected");
        }
    }

    public void removePage(IXWikiPage page) throws XWikiConnectionException
    {
        try {
            if (isConnected()) {
                remoteDAO.removePage(page.getId());
            }

            cacheDAO.removePage(page.getId());

            XWikiEclipseNotificationCenter.getDefault().fireEvent(this,
                XWikiEclipseEvent.PAGE_REMOVED, new Object[] {page.getSpace(), page});
        } catch (XWikiDAOException e) {
            e.printStackTrace();
            throw new XWikiConnectionException(e);
        }

    }

    public void removeSpace(IXWikiSpace space) throws XWikiConnectionException
    {
        try {
            if (isConnected()) {
                remoteDAO.removeSpace(space.getKey());
            }

            cacheDAO.removeSpace(space.getKey());

            XWikiEclipseNotificationCenter.getDefault().fireEvent(this,
                XWikiEclipseEvent.SPACE_REMOVED, this);
        } catch (XWikiDAOException e) {
            e.printStackTrace();
            throw new XWikiConnectionException(e);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * Does nothing for now.
     */
    public Collection<IXWikiPage> getKnownPages()
    {
        Set<IXWikiPage> result = new HashSet<IXWikiPage>();
        return result;
    }
    
    
}
