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
package org.xwiki.eclipse.utils;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.xwiki.eclipse.editors.XWikiPageEditor;
import org.xwiki.eclipse.editors.XWikiPageEditorInput;
import org.xwiki.eclipse.model.IXWikiConnection;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.model.XWikiConnectionException;

/**
 * Utility methods for different tasks.
 */
public class XWikiEclipseUtil
{
    /**
     * @param object The object that should represent an IStructuredSelection
     * @return the only selected object in the passed selection or null if the passed object is not
     *         an IStructuredSelection of if it contains more than one selected item.
     */
    public static Object getSingleSelectedObjectInStructuredSelection(Object object)
    {
        if (object instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) object;
            if (selection.size() == 1) {
                return selection.getFirstElement();
            }
        }

        return null;
    }

    public static void runOperationWithProgress(IRunnableWithProgress operation, Shell shell)
        throws InvocationTargetException, InterruptedException
    {
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
        dialog.run(true, false, operation);
    }

    public static void updateEditors(IWorkbenchPage page, IXWikiConnection connection)
    {
        try {

            IEditorReference[] editorReferences = page.getEditorReferences();
            for (IEditorReference editorReference : editorReferences) {
                XWikiPageEditorInput input;

                input = (XWikiPageEditorInput) editorReference.getEditorInput();

                if (input.getXWikiPage().getConnection() == connection) {
                    IXWikiPage xwikiPage = connection.getPage(input.getXWikiPage().getId());
                    IEditorPart editorPart = editorReference.getEditor(false);
                    if (editorPart instanceof XWikiPageEditor) {
                        XWikiPageEditor xwikiPageEditor = (XWikiPageEditor) editorPart;
                        input.setXWikiPage(xwikiPage);
                        xwikiPageEditor.updateEditor(xwikiPage);
                    }
                }
            }
        } catch (PartInitException e) {
            e.printStackTrace();
        } catch (XWikiConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
