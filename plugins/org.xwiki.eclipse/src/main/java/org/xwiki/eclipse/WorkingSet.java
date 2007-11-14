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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.xwiki.eclipse.model.IXWikiConnection;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.model.IXWikiSpace;

public class WorkingSet implements Serializable
{
    private static final long serialVersionUID = 2374252363910602198L;

    private String name;

    private Set<String> ids;

    public WorkingSet(String name)
    {
        this.name = name;
        ids = new HashSet<String>();
    }

    public void add(IXWikiConnection xwikiConnection)
    {
        ids.add(getId(xwikiConnection));
    }

    public void add(IXWikiSpace xwikiSpace)
    {
        ids.add(getId(xwikiSpace));
    }

    public void add(IXWikiPage xwikiPage)
    {
        ids.add(getId(xwikiPage));
    }

    public void remove(IXWikiConnection xwikiConnection)
    {
        ids.remove(getId(xwikiConnection));
    }

    public void remove(IXWikiSpace xwikiSpace)
    {
        ids.remove(getId(xwikiSpace));
    }

    public void remove(IXWikiPage xwikiPage)
    {
        ids.remove(getId(xwikiPage));
    }

    public boolean contains(IXWikiConnection xwikiConnection)
    {
        return ids.contains(getId(xwikiConnection));
    }

    public boolean contains(IXWikiSpace xwikiSpace)
    {
        return ids.contains(getId(xwikiSpace));
    }

    public boolean contains(IXWikiPage xwikiPage)
    {
        return ids.contains(getId(xwikiPage));
    }

    private String getId(IXWikiConnection xwikiConnection)
    {
        return xwikiConnection.getId();
    }

    private String getId(IXWikiSpace xwikiSpace)
    {
        return String.format("%s#%s", xwikiSpace.getConnection().getId(), xwikiSpace.getKey());
    }

    private String getId(IXWikiPage xwikiPage)
    {
        return String.format("%s#%s", xwikiPage.getConnection().getId(), xwikiPage.getId());
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
