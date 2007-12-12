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
import org.xwiki.eclipse.XWikiEclipseConstants;
import org.xwiki.eclipse.XWikiEclipseEvent;
import org.xwiki.eclipse.XWikiEclipseNotificationCenter;
import org.xwiki.eclipse.model.IXWikiConnection;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.viewers.XWikiPagePreviewViewer;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;

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
                
        form.setText(String.format("%s version %d.%d [%s]", id, (version >> 16) + 1, version & 0xFFFF, connected ? "online"
            : "cached", id));
        form.setMessage(String.format("%s@%s", userName, serverUrl));

        previewViewer.showPreview(page);

        if (!getDocumentProvider().getDocument(getEditorInput()).get().equals(page.getContent())) {
            XWikiPageEditor.CaretState caretState = getCaretState();
            getDocumentProvider().getDocument(getEditorInput()).set(page.getContent());
            setCaretOffset(caretState);
        }

        XWikiEclipseNotificationCenter.getDefault().fireEvent(this,
            XWikiEclipseEvent.PAGE_UPDATED, page);
    }

    class CaretState
    {
        private int topPixel;

        private int caretOffset;

        public CaretState(int caretOffset, int topPixel)
        {
            this.caretOffset = caretOffset;
            this.topPixel = topPixel;
        }

        public int getTopPixel()
        {
            return topPixel;
        }

        public int getCaretOffset()
        {
            return caretOffset;
        }
    }

    CaretState getCaretState()
    {
        CaretState caretState =
            new CaretState(getSourceViewer().getTextWidget().getCaretOffset(), getSourceViewer()
                .getTextWidget().getTopPixel());

        return caretState;
    }

    void setCaretOffset(CaretState caretState)
    {
        getSourceViewer().getTextWidget().setCaretOffset(caretState.getCaretOffset());
        getSourceViewer().getTextWidget().setTopPixel(caretState.getTopPixel());
    }
}
