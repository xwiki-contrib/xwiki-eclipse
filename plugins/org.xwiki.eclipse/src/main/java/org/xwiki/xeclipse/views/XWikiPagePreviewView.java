package org.xwiki.xeclipse.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.xwiki.xeclipse.IXWikiEclipseEventListener;
import org.xwiki.xeclipse.XWikiEclipseEvent;
import org.xwiki.xeclipse.XWikiEclipseNotificationCenter;
import org.xwiki.xeclipse.editors.XWikiPageEditor;
import org.xwiki.xeclipse.model.IXWikiPage;
import org.xwiki.xeclipse.utils.XWikiEclipseUtil;
import org.xwiki.xeclipse.viewers.XWikiPagePreviewViewer;

public class XWikiPagePreviewView extends ViewPart implements ISelectionListener,
    IXWikiEclipseEventListener
{
    public static final String ID = "org.xwiki.xeclipse.views.XWikiPagePreview";

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
