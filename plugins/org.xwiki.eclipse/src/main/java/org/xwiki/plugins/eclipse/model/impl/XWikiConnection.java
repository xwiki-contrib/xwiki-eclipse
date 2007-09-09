/*
 * Copyright 2006-2007, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
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
import java.util.HashMap;
import java.util.List;

import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.Space;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
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
 * Default implementation of {@link IXWikiConnection}
 */
public class XWikiConnection implements IXWikiConnection, TreeAdapter
{
    /**
     * An XML-RPC proxy
     */
    private transient Confluence rpc;

    /**
     * Username supplied by the user for this connection.
     */
    private String userName;

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
     * Default constructor. A connection should only be acquired by going through ConnectionManager
     */
    protected XWikiConnection()
    {
        spacesByKey = new HashMap<String, IXWikiSpace>();
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
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.adapters.TreeAdapter#getTreeChildren()
     */
    public Object[] getTreeChildren()
    {
        IXWikiConnection xwikiConnection = new XWikiConnectionWrapper(this);
        try {
            xwikiConnection.init();
        } catch (SwizzleConfluenceException e) {
            // Will be logged elsewhere.
        }
        if (!spacesReady) {
            return null;
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

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#addSpace(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void addSpace(String name, String key, String description)
        throws SwizzleConfluenceException
    {
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

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#disconnect()
     */
    public void disconnect() throws SwizzleConfluenceException
    {
        rpc.logout();
        XWikiConnectionManager.getInstance().removeConnection(this);
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
     * Sets the local cache directory of this connection.
     */
    protected void setCachePath(IPath cachePath)
    {
        this.cachePath = cachePath;
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
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#removeSpace(java.lang.String)
     */
    public void removeSpace(String key) throws SwizzleConfluenceException
    {
        rpc.removeSpace(key);
        IXWikiSpace space = spacesByKey.get(key);        
        spacesByKey.remove(key);
        CacheUtils.clearCache(space);
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
        // We'll worry about this later.
    }
}
