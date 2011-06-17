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
 * A class representing a space summary.
 * 
 * @version $Id$
 */
public abstract class XWikiEclipsePageHistorySummary extends ModelObject
{

    public XWikiEclipsePageHistorySummary(AbstractDataManager dataManager) {
		super(dataManager);
	}

	@Override
    public String getXWikiEclipseId()
    {
        return String
            .format(
                "xwikieclipse://%s/%s/%d/%d", getDataManager().getName(), getId(), getVersion(), getMinorVersion()); //$NON-NLS-1$
    }

	public abstract int getMinorVersion();

	public abstract int getVersion();

	public abstract String getId();
}
