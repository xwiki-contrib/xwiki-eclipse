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
import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.xeclipse.XWikiEclipseEvent;
import org.xwiki.xeclipse.XWikiEclipseNotificationCenter;
import org.xwiki.xeclipse.model.IXWikiPage;
import org.xwiki.xeclipse.model.IXWikiSpace;
import org.xwiki.xeclipse.model.XWikiConnectionException;
import org.xwiki.xeclipse.utils.XWikiEclipseUtil;

public class GrabSpaceHandler extends AbstractHandler
{
    /*
     * This variable is used to pass data about grabbed pages from the anonymous class performing
     * the downloading to the outer class
     */
    private Collection<IXWikiPage> grabbedXwikiPages;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);

        Object selectedObject =
            XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(selection);

        if (selectedObject instanceof IXWikiSpace) {
            final IXWikiSpace xwikiSpace = (IXWikiSpace) selectedObject;

            try {
                XWikiEclipseUtil.runOperationWithProgress(new IRunnableWithProgress()
                {
                    public void run(IProgressMonitor monitor) throws InvocationTargetException,
                        InterruptedException
                    {
                        monitor.beginTask("Getting pages...", IProgressMonitor.UNKNOWN);
                        try {
                            grabbedXwikiPages = xwikiSpace.getPages();
                            for (IXWikiPage xwikiPage : grabbedXwikiPages) {
                                xwikiPage.getContent();
                            }
                        } catch (XWikiConnectionException e) {
                            e.printStackTrace();
                            throw new InvocationTargetException(e,
                                "Error while downloading pages");
                        }
                        monitor.done();
                    }
                }, HandlerUtil.getActiveShell(event));

                XWikiEclipseNotificationCenter.getDefault().fireEvent(this,
                    XWikiEclipseEvent.PAGES_GRABBED, grabbedXwikiPages);

            } catch (InvocationTargetException e) {

                e.printStackTrace();
                MessageDialog.openError(HandlerUtil.getActiveShell(event), "Error", e
                    .getMessage());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return null;
    }
}
