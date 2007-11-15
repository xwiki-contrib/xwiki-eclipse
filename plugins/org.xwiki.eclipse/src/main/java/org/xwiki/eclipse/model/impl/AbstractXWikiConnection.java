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

import java.io.Serializable;
import java.util.UUID;

import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.Space;
import org.xwiki.eclipse.model.IXWikiConnection;
import org.xwiki.eclipse.model.XWikiConnectionException;

/**
 * This is the base class for different type of XWiki connections
 */
public abstract class AbstractXWikiConnection implements IXWikiConnection, Serializable
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
     * @throws XWikiConnectionException
     */
    public AbstractXWikiConnection(String serverUrl, String userName)
        throws XWikiConnectionException
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

    abstract Space getRawSpace(String key) throws XWikiConnectionException;

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((serverUrl == null) ? 0 : serverUrl.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AbstractXWikiConnection other = (AbstractXWikiConnection) obj;
        if (serverUrl == null) {
            if (other.serverUrl != null)
                return false;
        } else if (!serverUrl.equals(other.serverUrl))
            return false;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }

    

}
