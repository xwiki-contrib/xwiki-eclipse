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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.StorageDocumentProvider;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.model.XWikiConnectionException;
import org.xwiki.eclipse.utils.XWikiEclipseUtil;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;

public class XWikiPageDocumentProvider extends StorageDocumentProvider
{
    private XWikiPageEditor xwikiPageEditor;

    public XWikiPageDocumentProvider(XWikiPageEditor xwikiPageEditor)
    {
        this.xwikiPageEditor = xwikiPageEditor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createDocument(java.lang.Object)
     */
    @Override
    protected IDocument createDocument(Object element) throws CoreException
    {
        final IDocument document = new Document();
        if (element instanceof XWikiPageEditorInput) {
            final XWikiPageEditorInput input = (XWikiPageEditorInput) element;

            try {
                XWikiEclipseUtil.runOperationWithProgress(new IRunnableWithProgress()
                {
                    public void run(IProgressMonitor monitor) throws InvocationTargetException,
                        InterruptedException
                    {
                        monitor.beginTask("Loading page", IProgressMonitor.UNKNOWN);
                        String content = input.getXWikiPage().getContent();
                        document.set(content);
                        monitor.done();
                    }

                }, Display.getDefault().getActiveShell());
            } catch (Exception e) {
                throw new CoreException(new Status(IStatus.ERROR,
                    XWikiEclipsePlugin.PLUGIN_ID,
                    "Error opening page",
                    e));
            }
        }

        return document;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.editors.text.StorageDocumentProvider#isModifiable(java.lang.Object)
     */
    @Override
    public boolean isModifiable(Object element)
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.editors.text.StorageDocumentProvider#isReadOnly(java.lang.Object)
     */
    @Override
    public boolean isReadOnly(Object element)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.editors.text.StorageDocumentProvider#doSaveDocument(org.eclipse.core.runtime.IProgressMonitor,
     *      java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
     */
    @Override
    protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document,
        boolean overwrite) throws CoreException
    {
        XWikiPageEditorInput input = (XWikiPageEditorInput) element;
        IXWikiPage xwikiPage = input.getXWikiPage();

        xwikiPage.setContent(document.get());

        try {
            xwikiPage.save();

            if (xwikiPage.isConflict()) {
                MessageDialog
                    .openWarning(
                        Display.getDefault().getActiveShell(),
                        "Page out of synch",
                        "The page being saved has been modified remotely, and is not up to date.\nLocal and remote content will be presented in the editor.\n\nMerge the contents and resave the page in order to actualy update the remote version.");
            }

            xwikiPageEditor.updateEditor(input.getXWikiPage());
        } catch (XWikiConnectionException e) {
            throw new CoreException(new Status(IStatus.ERROR,
                XWikiEclipsePlugin.PLUGIN_ID,
                "Unable to save",
                e));
        }
    }

}
