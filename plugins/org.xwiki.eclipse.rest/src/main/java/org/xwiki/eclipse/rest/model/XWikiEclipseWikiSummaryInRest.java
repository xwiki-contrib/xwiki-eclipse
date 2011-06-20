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
package org.xwiki.eclipse.rest.model;

import org.eclipse.core.runtime.CoreException;
import org.xwiki.eclipse.model.XWikiEclipseWikiSummary;
import org.xwiki.eclipse.rest.storage.Relations;
import org.xwiki.eclipse.storage.AbstractDataManager;
import org.xwiki.rest.model.jaxb.Wiki;

/**
 * 
 * @version $Id$
 */
public class XWikiEclipseWikiSummaryInRest extends XWikiEclipseWikiSummary
{
    private Wiki data;

    /**
     * @param dataManager
     */
    public XWikiEclipseWikiSummaryInRest(AbstractDataManager dataManager, Wiki data)
    {
        super(dataManager);
        
        this.data = data;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseWikiSummary#getName()
     */
    @Override
    public String getName()
    {
        return data.getName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseWikiSummary#getUrl()
     */
    @Override
    public String getUrl()
    {
        String endpoint;
        try {
            endpoint = getDataManager().getEndpoint();
            String result = null;
            if (endpoint.endsWith("/")) {
                result = endpoint + Relations.WIKIS_PREFIX + "/" + getName();
            } else {
                result = endpoint + "/" + Relations.WIKIS_PREFIX + "/" + getName();
            }
            return  result;
        } catch (CoreException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseWikiSummary#getWikiId()
     */
    @Override
    public String getWikiId()
    {
        return data.getId();
    }

}
