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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.eclipse.core.DataManagerRegistry;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.storage.DataManager;
import org.xwiki.eclipse.ui.dialogs.SelectionDialog;
import org.xwiki.eclipse.ui.utils.UIUtils;
import org.xwiki.eclipse.ui.utils.XWikiEclipseSafeRunnable;

/**
 * @version $Id$
 */
public class DeleteXWikiElementHandler extends AbstractHandler
{
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);

        Set selectedObjects = UIUtils.getSelectedObjectsFromSelection(selection);

        SelectionDialog selectionDialog =
            new SelectionDialog(HandlerUtil.getActiveShell(event), "Delete objects",
                "Review the objects to be deleted", selectedObjects);
        int result = selectionDialog.open();
        if (result == IDialogConstants.CANCEL_ID) {
            return null;
        }

        Set<Object> objectsToBeRemoved = selectionDialog.getSelectedObjects();

        for (Object selectedObject : selectedObjects) {
            if (objectsToBeRemoved.contains(selectedObject)) {
                if (selectedObject instanceof XWikiEclipsePageSummary) {
                    final XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) selectedObject;

                    SafeRunner.run(new XWikiEclipseSafeRunnable()
                    {
                        public void run() throws Exception
                        {
                            pageSummary.getDataManager().removePage(pageSummary);
                        }
                    });

                }

                if (selectedObject instanceof XWikiEclipseObjectSummary) {
                    final XWikiEclipseObjectSummary objectSummary = (XWikiEclipseObjectSummary) selectedObject;

                    SafeRunner.run(new XWikiEclipseSafeRunnable()
                    {
                        public void run() throws Exception
                        {
                            objectSummary.getDataManager().removeObject(objectSummary);
                        }
                    });

                }

                if (selectedObject instanceof DataManager) {
                    final DataManager dataManager = (DataManager) selectedObject;

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

                if (selectedObject instanceof XWikiEclipseSpaceSummary) {
                    final XWikiEclipseSpaceSummary spaceSummary = (XWikiEclipseSpaceSummary) selectedObject;

                    SafeRunner.run(new XWikiEclipseSafeRunnable()
                    {
                        public void run() throws Exception
                        {
                            spaceSummary.getDataManager().removeSpace(spaceSummary);
                        }
                    });
                }

                if (selectedObject instanceof XWikiEclipseComment) {
                    final XWikiEclipseComment comment = (XWikiEclipseComment) selectedObject;

                    /* FIXME: comment delete is not supported in REST API yet */
                    SafeRunner.run(new XWikiEclipseSafeRunnable()
                    {
                        public void run() throws Exception
                        {
                            comment.getDataManager().removeComment(comment);
                        }
                    });

                }

                if (selectedObject instanceof XWikiEclipseAttachment) {
                    final XWikiEclipseAttachment attachment = (XWikiEclipseAttachment) selectedObject;

                    SafeRunner.run(new XWikiEclipseSafeRunnable()
                    {
                        public void run() throws Exception
                        {
                            attachment.getDataManager().removeAttachment(attachment);
                        }
                    });

                }
            }
        }

        return null;
    }
}
