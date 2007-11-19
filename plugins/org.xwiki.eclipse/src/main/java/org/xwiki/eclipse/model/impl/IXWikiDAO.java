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
package org.xwiki.eclipse.model.impl;

import java.util.List;

import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.Space;
import org.codehaus.swizzle.confluence.SpaceSummary;

public interface IXWikiDAO
{
    public List<SpaceSummary> getSpaces() throws XWikiDAOException;

    public Space getSpace(String key) throws XWikiDAOException;

    public List<PageSummary> getPages(String spaceKey) throws XWikiDAOException;

    public Page getPage(String id) throws XWikiDAOException;

    public void storePage(Page page) throws XWikiDAOException;

    public void close() throws XWikiDAOException;

    public Space createSpace(String key, String name, String description)
        throws XWikiDAOException;

    public Page createPage(String spaceKey, String title, String content)
        throws XWikiDAOException;

    public void removeSpace(String key) throws XWikiDAOException;

    public void removePage(String id) throws XWikiDAOException;
}
