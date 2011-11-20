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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.eclipse.core.notification.CoreEvent;
import org.xwiki.eclipse.core.notification.NotificationManager;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.model.XWikiEclipseTag;
import org.xwiki.eclipse.storage.DataManager;
import org.xwiki.eclipse.storage.DataManagerRegistry;
import org.xwiki.eclipse.ui.dialogs.SelectionDialog;
import org.xwiki.eclipse.ui.utils.UIUtils;
import org.xwiki.eclipse.ui.utils.XWikiEclipseSafeRunnable;

/**
 * @version $Id$
 */
public class DeleteXWikiElementHandler extends AbstractHandler
{
    /* only fire the remove event once, which is after the last object is deleted */
    static Object toBeRefreshed = null;

    static CoreEvent.Type coreEvent = null;

    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);

        final Set selectedObjects = UIUtils.getSelectedObjectsFromSelection(selection);

        SelectionDialog selectionDialog =
            new SelectionDialog(HandlerUtil.getActiveShell(event), "Delete XWiki elements",
                "Review the elements to be deleted", selectedObjects);
        int result = selectionDialog.open();
        if (result == IDialogConstants.CANCEL_ID) {
            return null;
        }

        final Set<Object> objectsToBeRemoved = selectionDialog.getSelectedObjects();

        Job deleteJob = new Job("Deleting...")
        {

            @Override
            protected IStatus run(final IProgressMonitor monitor)
            {

                monitor.beginTask("Deleting...", objectsToBeRemoved.size());
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                for (Object selectedObject : selectedObjects) {
                    if (objectsToBeRemoved.contains(selectedObject)) {
                        if (selectedObject instanceof XWikiEclipsePageSummary) {
                            final XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) selectedObject;

                            toBeRefreshed = pageSummary;
                            coreEvent = CoreEvent.Type.PAGE_REMOVED;

                            monitor.setTaskName("Deleting " + pageSummary.getId());

                            SafeRunner.run(new XWikiEclipseSafeRunnable()
                            {
                                public void run() throws Exception
                                {
                                    pageSummary.getDataManager().removePage(pageSummary);
                                    monitor.worked(1);
                                    Thread.sleep(2000);
                                }
                            });
                        }

                        if (selectedObject instanceof XWikiEclipseObjectSummary) {
                            final XWikiEclipseObjectSummary objectSummary = (XWikiEclipseObjectSummary) selectedObject;

                            toBeRefreshed = objectSummary;
                            coreEvent = CoreEvent.Type.OBJECT_REMOVED;

                            monitor.setTaskName("Deleting " + objectSummary.getId());

                            SafeRunner.run(new XWikiEclipseSafeRunnable()
                            {
                                public void run() throws Exception
                                {
                                    objectSummary.getDataManager().remove(objectSummary);
                                    monitor.worked(1);
                                    Thread.sleep(2000);
                                }
                            });

                        }

                        if (selectedObject instanceof DataManager) {
                            final DataManager dataManager = (DataManager) selectedObject;

                            monitor.setTaskName("Deleting " + dataManager.getName());

                            SafeRunner.run(new XWikiEclipseSafeRunnable()
                            {
                                public void run() throws Exception
                                {
                                    DataManagerRegistry.getDefault().unregister(dataManager);
                                    dataManager.getProject().delete(true, null);
                                    ResourcesPlugin.getWorkspace().save(true, new NullProgressMonitor());
                                    monitor.worked(1);
                                    Thread.sleep(2000);
                                }
                            });

                        }

                        if (selectedObject instanceof XWikiEclipseSpaceSummary) {
                            final XWikiEclipseSpaceSummary spaceSummary = (XWikiEclipseSpaceSummary) selectedObject;

                            // SafeRunner.run(new XWikiEclipseSafeRunnable()
                            // {
                            // public void run() throws Exception
                            // {
                            // spaceSummary.getDataManager().removeSpace(spaceSummary);
                            // }
                            // });
                        }

                        if (selectedObject instanceof XWikiEclipseComment) {
                            final XWikiEclipseComment comment = (XWikiEclipseComment) selectedObject;

                            toBeRefreshed = comment;
                            coreEvent = CoreEvent.Type.COMMENT_REMOVED;

                            monitor.setTaskName("Deleting " + comment.getId());

                            SafeRunner.run(new XWikiEclipseSafeRunnable()
                            {
                                public void run() throws Exception
                                {
                                    comment.getDataManager().remove(comment);
                                    monitor.worked(1);
                                    Thread.sleep(2000);
                                }
                            });

                        }

                        if (selectedObject instanceof XWikiEclipseTag) {
                            final XWikiEclipseTag tag = (XWikiEclipseTag) selectedObject;

                            toBeRefreshed = tag;
                            coreEvent = CoreEvent.Type.TAG_STORED;

                            monitor.setTaskName("Deleting " + tag.getName());

                            SafeRunner.run(new XWikiEclipseSafeRunnable()
                            {
                                public void run() throws Exception
                                {
                                    tag.getDataManager().remove(tag);
                                    monitor.worked(1);
                                    Thread.sleep(2000);
                                }
                            });

                        }

                        if (selectedObject instanceof XWikiEclipseAttachment) {
                            final XWikiEclipseAttachment attachment = (XWikiEclipseAttachment) selectedObject;
                            toBeRefreshed = attachment;
                            coreEvent = CoreEvent.Type.ATTACHMENT_REMOVED;
                            monitor.setTaskName("Deleting " + attachment.getName());

                            SafeRunner.run(new XWikiEclipseSafeRunnable()
                            {
                                public void run() throws Exception
                                {
                                    attachment.getDataManager().remove(attachment);
                                    monitor.worked(1);
                                    Thread.sleep(2000);
                                }
                            });
                        }

                        NotificationManager.getDefault().fireCoreEvent(coreEvent, this, toBeRefreshed);
                    }
                }

                monitor.done();

                return Status.OK_STATUS;
            }
        };
        deleteJob.setUser(true);
        deleteJob.schedule();

        return null;
    }
}
