package org.xwiki.xeclipse.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;
import org.xwiki.xeclipse.XWikiEclipseConstants;
import org.xwiki.xeclipse.XWikiEclipseEvent;
import org.xwiki.xeclipse.XWikiEclipseNotificationCenter;
import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.IXWikiPage;
import org.xwiki.xeclipse.viewers.XWikiPagePreviewViewer;

public class XWikiPageEditor extends AbstractTextEditor
{
    public static final String ID = "org.xwiki.xeclipse.editors.XWikiPage";

    private Form form;

    private SashForm sashForm;

    private XWikiPagePreviewViewer previewViewer;

    private class ShowPreviewAction extends Action
    {
        public ShowPreviewAction()
        {
            super("Show preview", AS_CHECK_BOX);
            setImageDescriptor(XWikiEclipsePlugin
                .getImageDescriptor(XWikiEclipseConstants.SHOW_EDITOR_PREVIEW_ICON));
            setChecked(false);
        }

        @Override
        public void run()
        {
            if (isChecked()) {
                sashForm.setMaximizedControl(null);
            } else {
                sashForm.setMaximizedControl(sashForm.getChildren()[0]);
            }
        }
    }

    public XWikiPageEditor()
    {
        super();
        setDocumentProvider(new XWikiPageDocumentProvider(this));
        setSourceViewerConfiguration(new SourceViewerConfiguration());
    }

    @Override
    public void createPartControl(Composite parent)
    {
        IXWikiPage xwikiPage = ((XWikiPageEditorInput) getEditorInput()).getXWikiPage();

        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createForm(parent);
        toolkit.decorateFormHeading(form);
        form.getToolBarManager().add(new ShowPreviewAction());
        form.updateToolBar();
        GridLayoutFactory.fillDefaults().applyTo(form.getBody());

        sashForm = new SashForm(form.getBody(), SWT.VERTICAL | SWT.BORDER);
        toolkit.adapt(sashForm);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(
            sashForm);

        super.createPartControl(sashForm);

        previewViewer = new XWikiPagePreviewViewer(sashForm);

        sashForm.setWeights(new int[] {50, 50});
        sashForm.setMaximizedControl(sashForm.getChildren()[0]);

        updateEditor(xwikiPage);
    }

    public void updateEditor(IXWikiPage page)
    {
        String id = page.getId();
        IXWikiConnection connection = page.getConnection();
        String userName = connection.getUserName();
        String serverUrl = connection.getServerUrl();
        boolean connected = connection.isConnected();
        int version = page.getVersion();

        form.setText(String.format("%s version %d [%s]", id, version, connected ? "online"
            : "cached", id));
        form.setMessage(String.format("%s@%s", userName, serverUrl));

        previewViewer.showPreview(page);
  
        XWikiEclipseNotificationCenter.getDefault().fireEvent(this,
            XWikiEclipseEvent.PAGE_UPDATED, page);
    }

}
