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
package org.xwiki.eclipse.ui.handlers;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.DataManagerRegistry;
import org.xwiki.eclipse.core.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.core.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.core.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.ui.utils.UIUtils;
import org.xwiki.eclipse.ui.utils.XWikiEclipseSafeRunnable;

public class DeleteXWikiElementHandler extends AbstractHandler
{
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);

        Set selectedObjects = UIUtils.getSelectedObjectsFromSelection(selection);
        for (Object selectedObject : selectedObjects) {
            if (selectedObject instanceof XWikiEclipsePageSummary) {
                final XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) selectedObject;

                MessageBox messageBox =
                    new MessageBox(HandlerUtil.getActiveShell(event), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
                messageBox.setMessage(String.format("Do you really want to delete the page '%s'?", pageSummary
                    .getData().getTitle()));
                int result = messageBox.open();
                if (result == SWT.YES) {
                    SafeRunner.run(new XWikiEclipseSafeRunnable()
                    {
                        public void run() throws Exception
                        {
                            pageSummary.getDataManager().removePage(pageSummary.getData().getId());
                        }
                    });
                }

            }

            if (selectedObject instanceof XWikiEclipseObjectSummary) {
                final XWikiEclipseObjectSummary objectSummary = (XWikiEclipseObjectSummary) selectedObject;

                MessageBox messageBox =
                    new MessageBox(HandlerUtil.getActiveShell(event), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
                messageBox.setMessage(String.format("Do you really want to delete object '%s' from page '%s'?",
                    objectSummary.getData().getPrettyName(), objectSummary.getPageSummary().getTitle()));
                int result = messageBox.open();
                if (result == SWT.YES) {
                    SafeRunner.run(new XWikiEclipseSafeRunnable()
                    {
                        public void run() throws Exception
                        {
                            objectSummary.getDataManager().removeObject(objectSummary.getData().getPageId(),
                                objectSummary.getData().getClassName(), objectSummary.getData().getId());
                        }
                    });
                }
            }

            if (selectedObject instanceof DataManager) {
                final DataManager dataManager = (DataManager) selectedObject;

                MessageBox messageBox =
                    new MessageBox(HandlerUtil.getActiveShell(event), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
                messageBox
                    .setMessage(String
                        .format(
                            "Do you really want to delete the connection '%s'?\n\nWarning: Any unsaved opperations will be lost.",
                            dataManager.getName()));
                int result = messageBox.open();
                if (result == SWT.YES) {
                    SafeRunner.run(new XWikiEclipseSafeRunnable()
                    {
                        public void run() throws Exception
                        {
                            DataManagerRegistry.getDefault().unregister(dataManager);
                            dataManager.getProject().delete(true, null);
                            ResourcesPlugin.getWorkspace().save(true, new NullProgressMonitor());
                        }
                    });
                }
            }

            if (selectedObject instanceof XWikiEclipseSpaceSummary) {
                final XWikiEclipseSpaceSummary spaceSummary = (XWikiEclipseSpaceSummary) selectedObject;

                MessageBox messageBox =
                    new MessageBox(HandlerUtil.getActiveShell(event), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
                messageBox.setMessage(String.format("Do you really want to delete ALL the pages under the space '%s'?",
                    spaceSummary.getData().getName()));
                int result = messageBox.open();
                if (result == SWT.YES) {
                    SafeRunner.run(new XWikiEclipseSafeRunnable()
                    {
                        public void run() throws Exception
                        {
                            spaceSummary.getDataManager().removeSpace(spaceSummary.getData().getKey());
                        }
                    });
                }

            }

        }

        return null;
    }

}
