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
package org.xwiki.eclipse;

import java.util.HashSet;
import java.util.Set;

import org.xwiki.eclipse.model.IXWikiConnection;
import org.xwiki.eclipse.model.IXWikiPage;

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

    public synchronized void addPage(IXWikiPage page)
    {
        pages.add(page);
    }

    public synchronized  void removePage(IXWikiPage page)
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
