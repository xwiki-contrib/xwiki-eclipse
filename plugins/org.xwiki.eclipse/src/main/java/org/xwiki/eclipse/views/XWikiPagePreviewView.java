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
package org.xwiki.eclipse.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.xwiki.eclipse.IXWikiEclipseEventListener;
import org.xwiki.eclipse.XWikiEclipseEvent;
import org.xwiki.eclipse.XWikiEclipseNotificationCenter;
import org.xwiki.eclipse.editors.XWikiPageEditor;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.utils.XWikiEclipseUtil;
import org.xwiki.eclipse.viewers.XWikiPagePreviewViewer;

public class XWikiPagePreviewView extends ViewPart implements ISelectionListener,
    IXWikiEclipseEventListener
{
    public static final String ID = "org.xwiki.eclipse.views.XWikiPagePreview";

    private XWikiPagePreviewViewer previewViewer;

    @Override
    public void createPartControl(Composite parent)
    {
        previewViewer = new XWikiPagePreviewViewer(parent);

        getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(this);
        XWikiEclipseNotificationCenter.getDefault().addListener(XWikiEclipseEvent.PAGE_UPDATED,
            this);
    }

    @Override
    public void setFocus()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose()
    {
        getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(this);
        XWikiEclipseNotificationCenter.getDefault().removeListener(
            XWikiEclipseEvent.PAGE_UPDATED, this);
        super.dispose();
    }

    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {
        if (part instanceof XWikiPageEditor) {
            return;
        }

        Object selectedObject =
            XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(selection);
        if (selectedObject instanceof IXWikiPage) {
            IXWikiPage xwikiPage = (IXWikiPage) selectedObject;
            previewViewer.showPreview(xwikiPage);
        } else {
            previewViewer.showPreview(null);
        }
    }

    public void handleEvent(Object sender, XWikiEclipseEvent event, Object data)
    {
        switch (event) {
            case PAGE_UPDATED:
                IXWikiPage xwikiPage = (IXWikiPage) data;
                previewViewer.showPreview(xwikiPage);
                break;
        }

    }
}
