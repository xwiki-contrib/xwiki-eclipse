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
package org.xwiki.eclipse.model;

import org.xwiki.eclipse.storage.DataManager;

/**
 * A class representing an XWiki page.
 * 
 * @version $Id$
 */
public abstract class XWikiEclipsePage extends ModelObject
{

    public XWikiEclipsePage(DataManager dataManager)
    {
        super(dataManager);
    }

    public abstract XWikiEclipsePageSummary getSummary();

    @Override
    public String getXWikiEclipseId()
    {
        return String.format("xwikieclipse://%s/%s/page/%s", getDataManager().getName(), getId(), getLanguage()); //$NON-NLS-1$
    }

    public abstract String getLanguage();

    public abstract String getId();

    public abstract int getVersion();

    public abstract String getTitle();

    public abstract String getContent();

    public abstract String getSpace();

    public abstract XWikiEclipseSpaceSummary getSpaceSummary();

    public abstract String getName();

    public abstract void setContent(String content);

    public abstract String getUrl();

    public abstract int getMinorVersion();
}
