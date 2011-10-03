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
package org.xwiki.eclipse.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.UIPlugin;

/**
 * @version $Id$
 */
public class PageEditorInput implements IEditorInput
{
    private XWikiEclipsePage page;

    private boolean readOnly;

    public PageEditorInput(XWikiEclipsePage page, boolean readOnly)
    {
        setPage(page, readOnly);
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public boolean exists()
    {
        return false;
    }

    public ImageDescriptor getImageDescriptor()
    {
        return UIPlugin.getImageDescriptor(UIConstants.PAGE_ICON);
    }

    public String getName()
    {
        String name =
            page.getName() + "[" + page.getVersion() + "]"
                + (page.getLanguage().equals("") ? "" : "[" + page.getLanguage() + "]");
        return name;
    }

    public IPersistableElement getPersistable()
    {
        return null;
    }

    public String getToolTipText()
    {
        return getName();
    }

    public Object getAdapter(Class adapter)
    {
        return null;
    }

    public XWikiEclipsePage getPage()
    {
        return page;
    }

    public void setPage(XWikiEclipsePage page, boolean readOnly)
    {
        Assert.isNotNull(page);
        this.page = page;
        this.readOnly = readOnly;
    }

    @Override
    public int hashCode()
    {
        return page.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PageEditorInput other = (PageEditorInput) obj;
        if (page == null) {
            if (other.page != null)
                return false;
        } else if (!page.getXWikiEclipseId().equals(other.page.getXWikiEclipseId()))
            return false;
        return true;
    }
}
