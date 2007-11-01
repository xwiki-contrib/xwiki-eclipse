package org.xwiki.xeclipse;

public enum XWikiEclipseEvent
{
    /* Sender: XWikiConnectionManager, Data: IXWikiConnection */
    CONNECTION_ADDED,
    /* Sender: XWikiConnectionManager, Data: IXWikiConnection */
    CONNECTION_REMOVED,
    /* Sender: IXWikiConnection, Data: null */
    CONNECTION_ESTABLISHED,
    /* Sender: IXWikiConnection, Data: null */
    CONNECTION_CLOSED
}
