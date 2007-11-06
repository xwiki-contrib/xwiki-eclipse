package org.xwiki.xeclipse;

import java.util.HashSet;
import java.util.Set;

import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.IXWikiPage;

public class XWikiEclipsePageIndex
{
    private Set<IXWikiPage> pages;

    private static XWikiEclipsePageIndex instance;

    private XWikiEclipsePageIndex()
    {
        pages = new HashSet<IXWikiPage>();
    }

    public static XWikiEclipsePageIndex getDefault()
    {
        if (instance == null) {
            instance = new XWikiEclipsePageIndex();
        }

        return instance;
    }

    public void addPage(IXWikiPage page)
    {
        pages.add(page);
    }

    public void removePage(IXWikiPage page)
    {
        pages.remove(page);
    }

    public void removePagesByConnection(IXWikiConnection connection)
    {
        Set<IXWikiPage> pagesToBeRemoved = new HashSet<IXWikiPage>();
        for (IXWikiPage page : pages) {
            if (page.getConnection().equals(connection)) {
                pagesToBeRemoved.add(page);
            }
        }

        for (IXWikiPage page : pagesToBeRemoved) {
            pages.remove(page);
        }
    }

    public Set<IXWikiPage> getPages()
    {
        return pages;
    }

}
