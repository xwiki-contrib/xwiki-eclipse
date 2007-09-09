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

package org.xwiki.plugins.eclipse.model;

import java.util.Collection;

import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.eclipse.core.runtime.IPath;
import org.xwiki.plugins.eclipse.util.ICacheable;

/**
 * Represents an XWiki Space.
 */
public interface IXWikiSpace extends ICacheable
{
    /**
     * Initializes this space if it has not been initialized. This method simply retrieves all the
     * data required from the server.
     * 
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public void init() throws SwizzleConfluenceException;

    /**
     * @return Key of this space.
     */
    public String getKey();

    /**
     * @return Name of this space.
     */
    public String getName();

    /**
     * Renames this space, space must be saved to take effect.
     * 
     * @param newName Name to be given.
     */
    public void setName(String newName);

    /**
     * @return Type of this space.
     */
    public String getType();

    /**
     * @return Url of this space (if it is to be viewed on browser).
     */
    public String getUrl();

    /**
     * @return ID of the page which is the home page of this space.
     */
    public String getHomePageId();

    /**
     * @return Description of this space as a html String.
     */
    public String getDescriptionAsHtml();

    /**
     * @return The cache path for this space.
     */
    public IPath getCachePath();

    /**
     * @return Whether this space should be displyed or not.
     */
    public boolean isMasked();

    /**
     * Set's or unset's the mask of this space
     * 
     * @param masked Masked or not.
     */
    public void setMasked(boolean masked);

    /**
     * @return Whether the space summary has been retrieved or not.
     */
    public boolean isSummaryReady();

    /**
     * @return Whether space data (core) has been retrieved or not.
     */
    public boolean isDataReady();

    /**
     * @return Whether pages of this space has been retrieved or not.
     */
    public boolean isPagesReady();

    /**
     * @return Parent IXWikiConnection which retrieved this space.
     */
    public IXWikiConnection getConnection();

    /**
     * @return All children pages as a collection of IXWikiPages.
     */
    public Collection<IXWikiPage> getPages();

    /**
     * @param pageID ID of the page.
     * @return Specific page with given id or null if no such page exists.
     */
    public IXWikiPage getPageByID(String pageID);

    /**
     * @param pageTitle Title of the Page.
     * @return Specific page with given id or null if no such page exists.
     */
    public IXWikiPage searchPage(String pageTitle);

    /**
     * Adds a new page to this space with given parameters.
     * 
     * @param title Title of the new page.
     * @param content Initial content of the page (XWikiMarkup).
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public void addPage(String title, String content) throws SwizzleConfluenceException;

    /**
     * Updates an existing page with given information.
     * 
     * @param pageId Id of the page which is to be updated.
     * @param title Title (may be new).
     * @param content Content (may be new).
     * @param version Version (may be new).
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public void updateChildPage(String pageId, String title, String content, int version)
        throws SwizzleConfluenceException;

    /**
     * Removes the page with given ID from this space.
     * 
     * @param pageId Id of the page which is to be removed.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public void removeChildPage(String pageId) throws SwizzleConfluenceException;

    /**
     * Saves this space. This method should be invoked in order to make local changes to the space
     * permenent.
     * 
     * @return Updated space as returned from the server.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public IXWikiSpace save() throws SwizzleConfluenceException;
}
