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

package org.xwiki.plugins.eclipse.model.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.IdentityObjectConvertor;
import org.codehaus.swizzle.confluence.Space;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.codehaus.swizzle.confluence.SwizzleXWiki;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.Image;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.model.adapters.TreeAdapter;
import org.xwiki.plugins.eclipse.model.wrappers.XWikiConnectionWrapper;
import org.xwiki.plugins.eclipse.util.CacheUtils;
import org.xwiki.plugins.eclipse.util.GuiUtils;
import org.xwiki.plugins.eclipse.util.XWikiConstants;

/**
 * Default implementation of {@link IXWikiConnection} TODO Offline functionality was added later and
 * this has made the code dirty. Need to fix (refactor) the code base whenever possible.
 */
public class XWikiConnection implements IXWikiConnection, TreeAdapter
{

    /**
     * serial version ID
     */
    private static final long serialVersionUID = 8193505646889459439L;

    /**
     * An XML-RPC proxy
     */
    private transient Confluence rpc;

    /**
     * Username supplied by the user for this connection.
     */
    private String userName;

    /**
     * This field need to be removed.
     */
    private String password;

    /**
     * Server url to which this connection refers to.
     */
    private String serverUrl;

    /**
     * Top-level spaces. Mapped by spaceKey
     */
    private transient HashMap<String, IXWikiSpace> spacesByKey;

    /**
     * Whether spaces have been retrieved or not.
     */
    private boolean spacesReady = false;

    /**
     * Cache path of this connection.
     */
    private transient IPath cachePath;

    /**
     * True when operating in off-line mode.
     */
    private boolean offline = false;
    
    /**
     * A String identifying this connection.
     */
    private String id = Long.toString(new Date().getTime());

    /**
     * Default constructor. A connection should only be acquired by going through ConnectionManager
     */
    protected XWikiConnection()
    {
        spacesByKey = new HashMap<String, IXWikiSpace>();
    }

    /**
     * Used in connection restoration (from cache) process.
     * 
     * @param spacesByKey Spaces indexed by key.
     */
    protected void setSpaces(HashMap<String, IXWikiSpace> spacesByKey)
    {
        this.spacesByKey = spacesByKey;
    }

    /**
     * Used by ConnectionManager to set initial parameters.
     * 
     * @param loginToken Login token as returned by login() rpc call.
     */
    protected void setRpcProxy(Confluence rpc)
    {
        this.rpc = rpc;
    }

    /**
     * used by ConnectionManager to set initial parameters.
     * 
     * @param serverUrl Server URL.
     */
    protected void setServerUrl(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }

    /**
     * used by ConnectionManager to set initial parameters.
     * 
     * @param userName User name.
     */
    protected void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * used by ConnectionManager to set initial parameters.
     * 
     * @param spacesReady Set whether spaces have been retrieved or not.
     */
    protected void setSpacesReady(boolean spacesReady)
    {
        this.spacesReady = spacesReady;
    }

    /**
     * Used by internal code to add a space into local model.
     * 
     * @param space Space to be added into local model.
     */
    protected void addSpace(IXWikiSpace space)
    {
        spacesByKey.put(space.getKey(), space);
    }

    /**
     * @param password The password for this connection.
     */
    protected void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return The password for this connection.
     */
    protected String getPassword()
    {
        return password;
    }

    /**
     * Sets the local cache directory of this connection.
     */
    protected void setCachePath(IPath cachePath)
    {
        this.cachePath = cachePath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getTreeChildren()
     */
    public Object[] getTreeChildren()
    {
        if (!isSpacesReady()) {
            if (!isOffline()) {
                IXWikiConnection xwikiConnection = new XWikiConnectionWrapper(this);
                try {
                    xwikiConnection.init();
                } catch (SwizzleConfluenceException e) {
                    // Will be logged elsewhere.
                }
                if (!isSpacesReady()) {
                    return null;
                }
            } else {
                return null;
            }
        }
        ArrayList<IXWikiSpace> displaySpaces = new ArrayList<IXWikiSpace>();
        for (IXWikiSpace s : spacesByKey.values()) {
            if (!s.isMasked()) {
                displaySpaces.add(s);
            }
        }
        return displaySpaces.toArray();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getImage()
     */
    public Image getImage()
    {
        if (isOffline()) {
            return GuiUtils.loadIconImage(XWikiConstants.NAV_CON_OFFLINE_ICON).createImage();
        }
        return GuiUtils.loadIconImage(XWikiConstants.NAV_CON_ICON).createImage();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getTreeParent()
     */
    public Object getTreeParent()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getText()
     */
    public String getText()
    {
        if (isOffline()) {
            return "[OFFLINE] " + userName + "@" + serverUrl;
        }
        return userName + "@" + serverUrl;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#hasChildren()
     */
    public boolean hasChildren()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        if (isOffline()) {
            return "[OFFLINE] " + userName + "@" + serverUrl;
        }
        return userName + "@" + serverUrl;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#initialize()
     */
    public void init() throws SwizzleConfluenceException
    {
        if (!isSpacesReady()) {
            if (!isOffline()) {
                List spaceSummaries = rpc.getSpaces();
                for (int i = 0; i < spaceSummaries.size(); i++) {
                    SpaceSummary summary = (SpaceSummary) spaceSummaries.get(i);
                    XWikiSpace xwikiSpace = new XWikiSpace(this, summary);
                    xwikiSpace.setCachePath(getCachePath().addTrailingSeparator().append(
                        xwikiSpace.getKey()));
                    CacheUtils.updateCache(xwikiSpace);
                    xwikiSpace.getCachePath().toFile().mkdir();
                    addSpace(xwikiSpace);
                }
                setSpacesReady(true);
                CacheUtils.updateCache(this);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#synchronize()
     */
    public void synchronize() throws SwizzleConfluenceException
    {
        if (isOffline()) {
            // First login.
            SwizzleXWiki newRpc = new SwizzleXWiki(serverUrl);
            newRpc.login(userName, password);
            // Workaround for finding out xwiki version (server)
            // (compatibility with <= XWiki 1.1.m4)
            try {
                newRpc.setNoConversion();
            } catch (SwizzleConfluenceException e) {
                // Assume older version of xwiki and turn-off conversion on client.
                newRpc.setConvertor(new IdentityObjectConvertor());
            }
            this.rpc = newRpc;
            // Logged in,
            offline = false;
            // Get a fresh copy of space summaries from the server.
            List spaces = rpc.getSpaces();
            // Index new space summaries.
            HashMap<String, SpaceSummary> newSpaceSummaries = new HashMap<String, SpaceSummary>();
            for (Object o : spaces) {
                SpaceSummary summary = (SpaceSummary) o;
                newSpaceSummaries.put(summary.getKey(), summary);
            }
            // Key sets of cached and new space summaries.
            Set<String> cacheKeySet = new HashSet<String>(spacesByKey.keySet());
            Set<String> newKeySet = newSpaceSummaries.keySet();
            // Synchronize each space in cache.
            for (String cacheKey : cacheKeySet) {
                if (newKeySet.contains(cacheKey)) {
                    spacesByKey.get(cacheKey).synchronize(newSpaceSummaries.get(cacheKey));
                } else {
                    // For now, we'll just get rid of the cached space.
                    CacheUtils.clearCache(spacesByKey.get(cacheKey));
                    spacesByKey.remove(cacheKey);
                }
            }
            // Check whether there are any new spaces
            for (String newKey : newKeySet) {
                if (!cacheKeySet.contains(newKey)) {
                    XWikiSpace xwikiSpace = new XWikiSpace(this, newSpaceSummaries.get(newKey));
                    // This space should be displayed
                    xwikiSpace.setMasked(false);
                    xwikiSpace.setCachePath(getCachePath().addTrailingSeparator().append(
                        xwikiSpace.getKey()));
                    CacheUtils.updateCache(xwikiSpace);
                    xwikiSpace.getCachePath().toFile().mkdir();
                    addSpace(xwikiSpace);
                }
            }
            // Finally, update the cache
            CacheUtils.updateCache(this);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#addSpace(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void addSpace(String name, String key, String description)
        throws SwizzleConfluenceException
    {
        if (!isOffline()) {
            Space space = new Space();
            space.setKey(key);
            space.setName(name);
            space.setDescription(description);
            Space result = rpc.addSpace(space);
            SpaceSummary summary = new SpaceSummary();
            summary.setKey(result.getKey());
            summary.setName(result.getName());
            summary.setType(result.getType());
            summary.setUrl(result.getUrl());
            XWikiSpace wikiSpace = new XWikiSpace(this, summary, result);
            wikiSpace.setMasked(false);
            wikiSpace.setCachePath(getCachePath().addTrailingSeparator().append(
                wikiSpace.getKey()));
            CacheUtils.updateCache(wikiSpace);
            wikiSpace.getCachePath().toFile().mkdir();
            addSpace(wikiSpace);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#disconnect()
     */
    public void disconnect() throws SwizzleConfluenceException
    {
        if (!isOffline()) {
            this.offline = true;
            Confluence temp = rpc;
            this.rpc = null;
            CacheUtils.updateCache(this);
            for (IXWikiSpace space : spacesByKey.values()) {
                CacheUtils.updateCache(space);
            }
            temp.logout();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#clearCache()
     */
    public void clearCache()
    {
        XWikiConnectionManager.getInstance().removeConnection(this);
        CacheUtils.clearCache(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getRpcProxy()
     */
    public Confluence getRpcProxy()
    {
        return rpc;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getServerUrl()
     */
    public String getServerUrl()
    {
        return serverUrl;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getId()
     */
    public String getId()
    {
        return id;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getSpace(java.lang.String)
     */
    public IXWikiSpace getSpace(String spaceKey)
    {
        return spacesByKey.get(spaceKey);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getSpaces()
     */
    public Collection<IXWikiSpace> getSpaces()
    {
        return this.spacesByKey.values();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getUserName()
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getCachePath()
     */
    public IPath getCachePath()
    {
        return cachePath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#isSpacesReady()
     */
    public boolean isSpacesReady()
    {
        return spacesReady;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#isOffline()
     */
    public boolean isOffline()
    {
        return offline;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#removeSpace(java.lang.String)
     */
    public void removeSpace(String key) throws SwizzleConfluenceException
    {
        if (!isOffline()) {
            rpc.removeSpace(key);
            IXWikiSpace space = spacesByKey.get(key);
            spacesByKey.remove(key);
            CacheUtils.clearCache(space);
        }
    }

    /**
     * Custom serializer method.
     */
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();        
    }

    /**
     * Custom deserializer method.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
    }
}
