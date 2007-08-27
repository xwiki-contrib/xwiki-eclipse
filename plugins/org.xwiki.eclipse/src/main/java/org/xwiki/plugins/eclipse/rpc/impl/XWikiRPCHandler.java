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

package org.xwiki.plugins.eclipse.rpc.impl;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.xwiki.plugins.eclipse.rpc.IXWikiRPCHandler;
import org.xwiki.plugins.eclipse.rpc.exceptions.CommunicationException;

/**
 * Default implementation of {@link IXWikiRPCHandler}.
 */
public class XWikiRPCHandler implements IXWikiRPCHandler
{

    /**
     * Shared instance of the RPCHandler.
     */
    private static XWikiRPCHandler privateInstance;

    /**
     * Currently connected clients. (loginToken -> RpcClient)
     */
    private HashMap<String, XmlRpcClient> clients;

    /**
     * Private constructor.
     */
    private XWikiRPCHandler()
    {
        clients = new HashMap<String, XmlRpcClient>();
    }

    /**
     * @return Shared instance as a {@link IXWikiRPCHandler} object.
     */
    public static IXWikiRPCHandler getInstance()
    {
        if (privateInstance == null) {
            privateInstance = new XWikiRPCHandler();
        }
        return privateInstance;
    }

    /**
     * @param loginToken The login token as returned by login() rpc call.
     * @return Corresponding {@link XmlRpcClient} object.
     */
    private XmlRpcClient findClient(String loginToken)
    {
        XmlRpcClient client = clients.get(loginToken);
        if (client == null) {
            // TODO might need to deal with this, but for now, leaving it out
        }
        return client;
    }

    /**
     * This is where all the rpc calls pass through.
     * 
     * @param rpc Method name which is to be invoked.
     * @param params All the parameters packed into a vector.
     * @param client XmlRpcClient on which the call should be made upon.
     * @return Value returned from server as an Object.
     * @throws CommunicationException - If some exception occurs.
     */
    private Object rpcCall(String rpc, Vector<Object> params, XmlRpcClient client)
        throws CommunicationException
    {
        try {
            return client.execute("confluence1." + rpc, params);
        } catch (XmlRpcException e) {
            throw new CommunicationException(e.getMessage());
        } catch (Exception e) {
            throw new CommunicationException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.rpc.IXWikiRPCHandler#getPage(java.lang.String,
     *      java.lang.String)
     */
    public Object getPage(String loginToken, String pageId) throws CommunicationException
    {
        XmlRpcClient client = findClient(loginToken);
        Vector<Object> params = new Vector<Object>();
        params.add(loginToken);
        params.add(pageId);
        return rpcCall("getPage", params, client);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.rpc.IXWikiRPCHandler#storePage(java.lang.String,
     *      java.lang.Object)
     */
    public Object storePage(String loginToken, Object page) throws CommunicationException
    {
        XmlRpcClient client = findClient(loginToken);
        Vector<Object> params = new Vector<Object>();
        params.add(loginToken);
        params.add(page);
        return (Object) rpcCall("storePage", params, client);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.rpc.IXWikiRPCHandler#getPageSummaries(java.lang.String,
     *      java.lang.String)
     */
    public Object[] getPageSummaries(String loginToken, String spaceKey)
        throws CommunicationException
    {
        XmlRpcClient client = findClient(loginToken);
        Vector<Object> params = new Vector<Object>();
        params.add(loginToken);
        params.add(spaceKey);
        return (Object[]) rpcCall("getPages", params, client);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.rpc.IXWikiRPCHandler#removePage(java.lang.String,
     *      java.lang.String)
     */
    public void removePage(String loginToken, String pageId) throws CommunicationException
    {
        XmlRpcClient client = findClient(loginToken);
        Vector<Object> params = new Vector<Object>();
        params.add(loginToken);
        params.add(pageId);
        rpcCall("removePage", params, client);
        return;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.rpc.IXWikiRPCHandler#getSpace(java.lang.String,
     *      java.lang.String)
     */
    public Object getSpace(String loginToken, String spacekey) throws CommunicationException
    {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.rpc.IXWikiRPCHandler#getSpaceSummaries(java.lang.String)
     */
    public Object[] getSpaceSummaries(String loginToken) throws CommunicationException
    {
        XmlRpcClient client = findClient(loginToken);
        Vector<Object> params = new Vector<Object>();
        params.add(loginToken);
        return (Object[]) rpcCall("getSpaces", params, client);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.rpc.IXWikiRPCHandler#addSpace(java.lang.String,
     *      java.lang.Object)
     */
    public Object addSpace(String loginToken, Object space) throws CommunicationException
    {
        XmlRpcClient client = findClient(loginToken);
        Vector<Object> params = new Vector<Object>();
        params.add(loginToken);
        params.add(space);
        return rpcCall("addSpace", params, client);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.rpc.IXWikiRPCHandler#removeSpace(java.lang.String,
     *      java.lang.String)
     */
    public void removeSpace(String loginToken, String spaceKey) throws CommunicationException
    {
        XmlRpcClient client = findClient(loginToken);
        Vector<Object> params = new Vector<Object>();
        params.add(loginToken);
        params.add(spaceKey);
        rpcCall("removeSpace", params, client);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.rpc.IXWikiRPCHandler#logout(java.lang.String)
     */
    public void logout(String loginToken) throws CommunicationException
    {
        XmlRpcClient client = findClient(loginToken);
        Vector<Object> params = new Vector<Object>();
        params.add(loginToken);
        rpcCall("logout", params, client);
        clients.remove(loginToken);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.rpc.IXWikiRPCHandler#login(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public String login(String serverUrl, String username, String password, String proxy)
        throws CommunicationException
    {
        if (proxy != null) {
            String[] proxyDetails = proxy.split(":");
            System.getProperties().put("http.proxyHost", proxyDetails[0]);
            System.getProperties().put("http.proxyPort", proxyDetails[0]);
            // System.getProperties().put("proxySet","true");
        }
        XmlRpcClient client = new XmlRpcClient();
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        String loginToken = null;
        try {
            config.setServerURL(new URL(serverUrl));
        } catch (IOException e) {
            throw new CommunicationException(e.getMessage());
        }
        client.setConfig(config);
        Vector<Object> params = new Vector<Object>();
        params.add(username);
        params.add(password);
        loginToken = (String) rpcCall("login", params, client);
        if (loginToken == null) {
            throw new CommunicationException("Unable to obtain a login token from server.");
        }
        clients.put(loginToken, client);
        return loginToken;
    }
}
