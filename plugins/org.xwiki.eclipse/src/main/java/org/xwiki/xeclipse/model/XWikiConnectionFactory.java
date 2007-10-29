package org.xwiki.xeclipse.model;

import java.io.File;

import org.xwiki.xeclipse.model.impl.XWikiCachedConnection;
import org.xwiki.xeclipse.model.impl.XWikiPlainConnection;

/**
 * A factory for creating connections.
 */
public final class XWikiConnectionFactory
{
    /**
     * Creates a cached connection.
     * 
     * @param serverUrl The URL of the XWiki XML RPC server that will be used by this connection manager.
     * @param userName The user name to be used when connecting to the remote XWiki instance.
     * @param cacheDir The directory to be used to store the local cache.
     * @return A connection object.
     * @throws XWikiConnectionException
     */
    public static IXWikiConnection createCachedConnection(String serverUrl, String userName, File cacheDir) throws XWikiConnectionException {
        return new XWikiCachedConnection(serverUrl, userName, cacheDir);
    }
    
    /**
     * Creates a plain connection.
     * 
     * @param serverUrl The URL of the XWiki XML RPC server that will be used by this connection manager.
     * @param userName The user name to be used when connecting to the remote XWiki instance.
     * @return A connection object.
     * @throws XWikiConnectionException
     */
    public static IXWikiConnection createPlainConnection(String serverUrl, String userName) throws XWikiConnectionException {
        return new XWikiPlainConnection(serverUrl, userName);
    }
}
