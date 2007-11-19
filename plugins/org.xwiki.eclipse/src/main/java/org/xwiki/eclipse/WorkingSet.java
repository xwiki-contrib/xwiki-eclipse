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

/**
 * A class representing a working set, i.e., a set of resources (connections, spaces and pages)
 */
public class WorkingSet implements Serializable
{
    private static final long serialVersionUID = 2374252363910602198L;

    private String name;

    /**
     * The ids of the resources belonging to this working set.
     */
    private Set<String> ids;

    /**
     * Constructor. 
     *
     * @param name The working set symbolic name.
     */
    public WorkingSet(String name)
    {
        this.name = name;
        ids = new HashSet<String>();
    }

    /**
     * Add a connection to the working set.
     * 
     * @param xwikiConnection The connection to be added.
     */
    public void add(IXWikiConnection xwikiConnection)
    {
        ids.add(getId(xwikiConnection));
    }

    /**
     * Add a space to the working set.
     * 
     * @param xwikiSpace The space to be added.
     */
    public void add(IXWikiSpace xwikiSpace)
    {
        ids.add(getId(xwikiSpace));
    }

    /**
     * Add a page to the working set.
     * 
     * @param xwikiPage The page to be added.
     */
    public void add(IXWikiPage xwikiPage)
    {
        ids.add(getId(xwikiPage));
    }

    /**
     * Remove a connection from the working set.
     * 
     * @param xwikiConnection The connection to be removed.
     */
    public void remove(IXWikiConnection xwikiConnection)
    {
        ids.remove(getId(xwikiConnection));
    }

    /**
     * Remove a space from the working set.
     * 
     * @param xwikiSpace The space to be removed.
     */
    public void remove(IXWikiSpace xwikiSpace)
    {
        ids.remove(getId(xwikiSpace));
    }

    /**
     * Remove a page from the working set.
     * 
     * @param xwikiPage The page to be removed.
     */
    public void remove(IXWikiPage xwikiPage)
    {
        ids.remove(getId(xwikiPage));
    }

    /**
     * Check for membership.
     * 
     * @param xwikiConnection An xwiki connection.
     * @return true if the connection belongs to the working set, false otherwise.
     */
    public boolean contains(IXWikiConnection xwikiConnection)
    {
        return ids.contains(getId(xwikiConnection));
    }

    /**
     * Check for membership.
     * 
     * @param xwikiSpace An xwiki space.
     * @return true if the space belongs to the working set, false otherwise.
     */    
    public boolean contains(IXWikiSpace xwikiSpace)
    {
        return ids.contains(getId(xwikiSpace));
    }

    /**
     * Check for membership.
     * 
     * @param xwikiPage An xwiki page.
     * @return true if the page belongs to the working set, false otherwise.
     */
    public boolean contains(IXWikiPage xwikiPage)
    {
        return ids.contains(getId(xwikiPage));
    }

    /**
     * @param xwikiConnection An xwiki connection.
     * @return A synthesized id for the connection.
     */
    private String getId(IXWikiConnection xwikiConnection)
    {
        return xwikiConnection.getId();
    }

    /**
     * @param xwikiSpace An xwiki space.
     * @return A synthesized id for the space.
     */    
    private String getId(IXWikiSpace xwikiSpace)
    {
        return String.format("%s#%s", xwikiSpace.getConnection().getId(), xwikiSpace.getKey());
    }

    /**
     * @param xwikiPage An xwiki page.
     * @return A synthesized id for the page.
     */    
    private String getId(IXWikiPage xwikiPage)
    {
        return String.format("%s#%s", xwikiPage.getConnection().getId(), xwikiPage.getId());
    }

    /**
     * @return The working set symbolic name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the working set symbolic name.
     * 
     * @param name The working set symbolic name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

}
