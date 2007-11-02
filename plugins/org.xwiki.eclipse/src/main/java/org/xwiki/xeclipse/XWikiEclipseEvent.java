package org.xwiki.xeclipse;

public enum XWikiEclipseEvent
{
    /* Data: IXWikiConnection */
    CONNECTION_ADDED,
    /* Data: IXWikiConnection */
    CONNECTION_REMOVED,
    /* Data: IXWikiConnection */
    CONNECTION_ESTABLISHED,
    /* Data: IXWikiConnection */
    CONNECTION_CLOSED,
    /* Data: IXWikiPage */
    PAGE_UPDATED,
    /* Data: IXWikiConnection - the connection to be refreshed */
    SPACE_CREATED, 
    /* Data: IXWikiSpace - the space to be refreshed */
    PAGE_CREATED
}
