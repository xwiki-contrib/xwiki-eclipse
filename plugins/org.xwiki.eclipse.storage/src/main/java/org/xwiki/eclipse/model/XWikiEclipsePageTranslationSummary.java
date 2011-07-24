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
 * A class representing a page translation summary.
 * 
 * @version $Id$
 */
public class XWikiEclipsePageTranslationSummary extends ModelObject
{
    private String historyUrl;

    private String pageUrl;

    private String defaultLanguage;

    private String language;

    public XWikiEclipsePageTranslationSummary(DataManager dataManager)
    {
        super(dataManager);
    }

    @Override
    public String getXWikiEclipseId()
    {
        return String.format(
            "xwikieclipse://%s/page/%s/translations/%s", getDataManager().getName(), getPageUrl(), getLanguage()); //$NON-NLS-1$
    }

    public String getPageUrl()
    {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl)
    {
        this.pageUrl = pageUrl;
    }

    public String getHistoryUrl()
    {
        return historyUrl;
    }

    public void setHistoryUrl(String historyUrl)
    {
        this.historyUrl = historyUrl;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getDefaultLanguage()
    {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultlanguage)
    {
        this.defaultLanguage = defaultlanguage;
    }
}
