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

import org.xwiki.eclipse.storage.AbstractDataManager;

/**
 * A class representing an object summary.
 * 
 * @version $Id$
 */
public abstract class XWikiEclipseObjectSummary extends ModelObject
{
	
    public XWikiEclipseObjectSummary(AbstractDataManager dataManager) {
		super(dataManager);
	}

    public abstract String getPageId();
    
    public abstract String getClassName();
    
    public abstract int getId();
    
	@Override
    public String getXWikiEclipseId()
    {
        return String
            .format(
                "xwikieclipse://%s/%s/%s/%d/summary", getDataManager().getName(), getPageId(), getClassName(), getId()); //$NON-NLS-1$        
    }

    public abstract XWikiEclipsePageSummary getPageSummary();

    public abstract String getPrettyName();
}
