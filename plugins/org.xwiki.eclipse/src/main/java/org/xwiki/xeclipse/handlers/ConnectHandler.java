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
package org.xwiki.xeclipse.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.xeclipse.XWikiConnectionManager;
import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.XWikiConnectionException;
import org.xwiki.xeclipse.utils.XWikiEclipseUtil;

public class ConnectHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);

        Object selectedObject =
            XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(selection);

        if (selectedObject instanceof IXWikiConnection) {
            final IXWikiConnection xwikiConnection = (IXWikiConnection) selectedObject;

            try {

                String password =
                    XWikiConnectionManager.getDefault().getPasswordForConnection(
                        xwikiConnection.getId());
                if (password == null) {
                    InputDialog inputDialog =
                        new InputDialog(HandlerUtil.getActiveShell(event), "Password", String
                            .format("Password for %s@%s:", xwikiConnection.getUserName(),
                                xwikiConnection.getServerUrl()), null, null);
                    inputDialog.open();
                    password = inputDialog.getValue();
                }

                final String actualPassword = password;

                XWikiEclipseUtil.runOperationWithProgress(new IRunnableWithProgress()
                {
                    public void run(IProgressMonitor monitor) throws InvocationTargetException,
                        InterruptedException
                    {
                        monitor.beginTask("Connecting...", IProgressMonitor.UNKNOWN);
                        try {
                            xwikiConnection.connect(actualPassword);
                        } catch (XWikiConnectionException e) {
                            e.printStackTrace();
                            throw new InvocationTargetException(e, String.format(
                                "Cannot connect to %s\n%s", xwikiConnection.getServerUrl(), e
                                    .getMessage()));
                        }
                        monitor.done();
                    }

                }, HandlerUtil.getActiveShell(event));

                // XWikiEclipseUtil.closeReopenEditorsForConnection(HandlerUtil.getActiveWorkbenchWindow(event).getActivePage(),
                // xwikiConnection);

                XWikiEclipseUtil.updateEditors(HandlerUtil.getActiveWorkbenchWindow(event)
                    .getActivePage(), xwikiConnection);

            } catch (InvocationTargetException e) {
                e.printStackTrace();
                MessageDialog.openError(HandlerUtil.getActiveShell(event), "Error", e
                    .getMessage());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /*
             * Re-set the current selection in order to force the re-evaluation of the expressions
             * associated to the other handlers that might depend on the changed state of the
             * selected object
             */
            HandlerUtil.getActiveSite(event).getSelectionProvider().setSelection(selection);
        }

        return null;
    }
}
