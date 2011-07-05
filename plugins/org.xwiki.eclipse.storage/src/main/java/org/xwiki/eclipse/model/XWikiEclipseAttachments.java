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
 * @version $Id$
 */
public class XWikiEclipseAttachments extends ModelObject
{
    private String id;

    private List<XWikiEclipseAttachment> attachments;

    /**
     * @param dataManager
     */
    public XWikiEclipseAttachments(DataManager dataManager)
    {
        super(dataManager);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.ModelObject#getXWikiEclipseId()
     */
    @Override
    public String getXWikiEclipseId()
    {
        return String.format("xwikieclipse://%s/attachments/%s", getDataManager().getName(), getId()); //$NON-NLS-1$
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public List<XWikiEclipseAttachment> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(List<XWikiEclipseAttachment> attachments)
    {
        this.attachments = attachments;
    }

}
