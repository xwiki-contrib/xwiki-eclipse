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

    /* Data: {IXWikiSpace, IXWikiPage} - the space to be refreshed */
    PAGE_CREATED,

    /* Data: IXWikiConnection */
    SPACE_REMOVED,

    /* Data: {IXWikiSpace, IXWikiPage} */
    PAGE_REMOVED,

    /* Data: Collection<IXWikiPage> */
    PAGES_GRABBED
}
