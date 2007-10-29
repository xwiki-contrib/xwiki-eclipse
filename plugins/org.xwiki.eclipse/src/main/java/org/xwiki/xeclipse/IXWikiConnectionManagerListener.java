package org.xwiki.xeclipse;

import org.xwiki.xeclipse.model.IXWikiConnection;

public interface IXWikiConnectionManagerListener
{
    void connectionAdded(IXWikiConnection xwikiConnection);

    void connectionRemoved(IXWikiConnection xwikiConnection);

}
