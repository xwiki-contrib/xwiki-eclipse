package org.xwiki.xeclipse.model;

public interface IXWikiConnectionListener
{
    public void connectionEstablished(IXWikiConnection connection);
    public void connectionClosed(IXWikiConnection connection);
}
