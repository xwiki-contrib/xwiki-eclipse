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

package org.xwiki.plugins.eclipse.rpc;

import org.xwiki.plugins.eclipse.rpc.exceptions.CommunicationException;

/**
 * Interface for defining RPC handlers.
 */
public interface IXWikiRPCHandler
{
    /**
     * Creates a session with the given user credentials
     * 
     * @param serverUrl XWiki server url (Ex http://somehost/xwiki/xmlrpc/confluence).
     * @param username User name.
     * @param password Password.
     * @param proxy If a proxy is used (Ex http://somehost:someport).
     * @return Login token as a String
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public String login(String serverUrl, String username, String password, String proxy)
        throws CommunicationException;

    /**
     * Retrieves all the space summaries for this session.
     * 
     * @param loginToken Session identifier.
     * @return An array of objects which are of HashMap<String, Object> type.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public Object[] getSpaceSummaries(String loginToken) throws CommunicationException;

    /**
     * Retreives the specific space with the given key.
     * 
     * @param loginToken Session identifier.
     * @param spacekey Space identifier.
     * @return Space which is of HashMap<String, Object> type.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public Object getSpace(String loginToken, String spacekey) throws CommunicationException;

    /**
     * Adds a new space into user's workspace.
     * 
     * @param loginToken Session identifier.
     * @param space Space to be created.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public Object addSpace(String loginToken, Object space) throws CommunicationException;

    /**
     * Removes the specified space permanently.
     * 
     * @param loginToken Session identifier.
     * @param spaceKey Key of the space which is to be removed.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public void removeSpace(String loginToken, String spaceKey) throws CommunicationException;

    /**
     * Retrieves summaries of all the pages that belongs to the specified space.
     * 
     * @param loginToken Session identifier.
     * @param spaceKey Space identifier.
     * @return An Array of objects which are of HashMap<String, Object> type.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public Object[] getPageSummaries(String loginToken, String spaceKey)
        throws CommunicationException;

    /**
     * Returns the specific page with the given Id.
     * 
     * @param loginToken Session identifier
     * @param pageId Page identifier.
     * @return Page object which is of HashMap<String, Object> type.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public Object getPage(String loginToken, String pageId) throws CommunicationException;

    /**
     * Saves a page on the server. It may be an already existing page (an update) or a completely
     * new one (creation). There are restrictions on which parameters should be provided along with
     * pages. Those restrictions shall be enforced by upper layers.
     * 
     * @param loginToken Session identifier.
     * @param page Page object which is of HashMap<String, Object> type.
     * @return New Page which was saved on the server.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public Object storePage(String loginToken, Object page) throws CommunicationException;

    /**
     * Removes a page from server (should be removed from the local model as well).
     * 
     * @param loginToken Session identifier.
     * @param pageId Id of the page to be removed.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public void removePage(String loginToken, String pageId) throws CommunicationException;

    /**
     * Logs out the user and destroys all the data structures associated with the session.
     * 
     * @param loginToken Session identifier.
     * @throws CommunicationException - If XMLRPC call fails.
     */
    public void logout(String loginToken) throws CommunicationException;
}
