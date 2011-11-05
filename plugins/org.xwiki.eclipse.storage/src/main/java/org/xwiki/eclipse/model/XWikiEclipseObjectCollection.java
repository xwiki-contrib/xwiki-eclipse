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
 */
package org.xwiki.eclipse.model;

import java.util.List;

import org.xwiki.eclipse.storage.DataManager;

/**
 * a container for the page's objects that have the same className, e.g., annotation, tag, comments
 * 
 * @version $Id$
 */
public class XWikiEclipseObjectCollection extends ModelObject
{
    public enum Type
    {
        ATTACHMENTS,
        OBJECTS,
        TAGS,
        COMMENTS,
        ANNOTATIONS
    }

    private Type type;

    private Object arg;

    private XWikiEclipsePageSummary pageSummary;

    public XWikiEclipseObjectCollection(DataManager dataManager, XWikiEclipsePageSummary pageSummary, Type type)
    {
        this(dataManager, pageSummary, type, null);
    }

    public XWikiEclipseObjectCollection(DataManager dataManager, XWikiEclipsePageSummary pageSummary, Type type,
        Object arg)
    {
        super(dataManager);
        this.pageSummary = pageSummary;
        this.type = type;
        this.arg = arg;
    }

    @Override
    public String getXWikiEclipseId()
    {
        return String
            .format(
                "xwikieclipse://%s/%s/%s/%s", getDataManager().getName(), pageSummary.getId(), type, arg != null ? arg : "all"); //$NON-NLS-1$
    }

    public Type getType()
    {
        return type;
    }

    public XWikiEclipsePageSummary getPageSummary()
    {
        return pageSummary;
    }

    public Object getArg()
    {
        return arg;
    }
}
