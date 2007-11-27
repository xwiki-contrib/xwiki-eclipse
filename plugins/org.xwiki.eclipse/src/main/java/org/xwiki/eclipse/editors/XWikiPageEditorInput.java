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
package org.xwiki.eclipse.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.xwiki.eclipse.model.IXWikiPage;

public class XWikiPageEditorInput implements IEditorInput
{
    private IXWikiPage xwikiPage;

    public XWikiPageEditorInput(IXWikiPage page)
    {
        this.xwikiPage = page;
    }

    public boolean exists()
    {
        return false;
    }

    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }

    public String getName()
    {
        return xwikiPage.getId();
    }

    public IPersistableElement getPersistable()
    {
        return null;
    }

    public String getToolTipText()
    {
        return xwikiPage.getTitle();
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter)
    {
        return null;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj)) {
            return true;
        }

        if (!(obj instanceof XWikiPageEditorInput)) {
            return false;
        }

        XWikiPageEditorInput other = (XWikiPageEditorInput) obj;

        return xwikiPage.getExtendedId().equals(other.xwikiPage.getExtendedId());
    }

    @Override
    public int hashCode()
    {
        return xwikiPage.getExtendedId().hashCode();
    }

    public IXWikiPage getXWikiPage()
    {
        return xwikiPage;
    }

    public void setXWikiPage(IXWikiPage xwikiPage)
    {
        this.xwikiPage = xwikiPage;
    }
}
