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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.swt.graphics.Image;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.model.adapters.TreeAdapter;
import org.xwiki.plugins.eclipse.model.wrappers.XWikiConnectionWrapper;
import org.xwiki.plugins.eclipse.rpc.exceptions.CommunicationException;
import org.xwiki.plugins.eclipse.rpc.impl.XWikiRPCHandler;
import org.xwiki.plugins.eclipse.util.GuiUtils;
import org.xwiki.plugins.eclipse.util.XWikiConstants;

/**
 * Default implementation of {@link IXWikiConnection}
 */
public class XWikiConnection implements IXWikiConnection, TreeAdapter
{

    /**
     * Login token as returned by the login rpc call.
     */
    private String loginToken;

    /**
     * Username supplied by the user for this connection.
     */
    private String userName;

    /**
     * Server url to which this connection refers to.
     */
    private String serverUrl;

    /**
     * Top-level spaces. Mapped by spaceName
     */
    private HashMap<String, IXWikiSpace> spacesByName;

    /**
     * Top-level spaces. Mapped by spaceKey
     */
    private HashMap<String, IXWikiSpace> spacesByKey;

    /**
     * Whether spaces have been retrieved or not.
     */
    private boolean spacesReady = false;

    /**
     * Default constructor. A connection should only be aquired by going through ConnectionManager
     */
    protected XWikiConnection()
    {
        spacesByName = new HashMap<String, IXWikiSpace>();
        spacesByKey = new HashMap<String, IXWikiSpace>();
    }

    /**
     * Used by ConnectionManager to set initial parameters.
     * 
     * @param loginToken Login token as returned by login() rpc call.
     */
    protected void setLoginToken(String loginToken)
    {
        this.loginToken = loginToken;
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
        spacesByName.put(space.getName(), space);
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
        } catch (CommunicationException e) {
            // Will be logged elsewhere.
        }
        if (!spacesReady) {
            return null;
        }
        ArrayList<IXWikiSpace> displaySpaces = new ArrayList<IXWikiSpace>();
        for (IXWikiSpace s : spacesByName.values()) {
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
    public void init() throws CommunicationException
    {
        if (!isSpacesReady()) {
            Object[] spaces = XWikiRPCHandler.getInstance().getSpaceSummaries(loginToken);
            for (Object space : spaces) {
                HashMap<String, Object> summary = (HashMap<String, Object>) space;
                IXWikiSpace xwikiSpace = new XWikiSpace(this, summary);
                addSpace(xwikiSpace);
            }
            setSpacesReady(true);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#addSpace(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void addSpace(String name, String key, String description)
        throws CommunicationException
    {
        HashMap<String, Object> tempSapce = new HashMap<String, Object>();
        HashMap<String, Object> summary = new HashMap<String, Object>();
        HashMap<String, Object> result;
        tempSapce.put(XWikiConstants.SPACE_KEY, key);
        tempSapce.put(XWikiConstants.SPACE_NAME, name);
        tempSapce.put(XWikiConstants.SPACE_DESCRIPTION, description);
        result =
            (HashMap<String, Object>) XWikiRPCHandler.getInstance().addSpace(loginToken,
                tempSapce);
        summary.put(XWikiConstants.SPACE_SUMMARY_KEY, result.get(XWikiConstants.SPACE_KEY));
        summary.put(XWikiConstants.SPACE_SUMMARY_NAME, result.get(XWikiConstants.SPACE_NAME));
        // This is a hack to avoid complexity.
        summary.put(XWikiConstants.SPACE_SUMMARY_TYPE, "Not Available");
        summary.put(XWikiConstants.SPACE_SUMMARY_URL, result.get(XWikiConstants.SPACE_URL));
        IXWikiSpace wikiSpace = new XWikiSpace(this, summary, result);
        // We need this space to be displayed.
        wikiSpace.setMasked(false);
        // Finally, add the space to local model.
        addSpace(wikiSpace);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#disconnect()
     */
    public void disconnect() throws CommunicationException
    {
        XWikiRPCHandler.getInstance().logout(loginToken);
        XWikiConnectionManager.getInstance().removeConnection(loginToken);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getLoginToken()
     */
    public String getLoginToken()
    {
        return loginToken;
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
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getSpaceByName(java.lang.String)
     */
    public IXWikiSpace getSpaceByName(String spaceName)
    {
        return spacesByName.get(spaceName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getSpaceByKey(java.lang.String)
     */
    public IXWikiSpace getSpaceByKey(String spaceKey)
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
        return this.spacesByName.values();
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
    public void removeSpace(String key) throws CommunicationException
    {
        IXWikiSpace spaceToBeRemoved = getSpaceByKey(key);
        XWikiRPCHandler.getInstance().removeSpace(getLoginToken(), key);
        spacesByName.remove(spaceToBeRemoved.getName());
        spacesByKey.remove(key);
    }
}
