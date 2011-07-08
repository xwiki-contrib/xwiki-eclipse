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
 */
package org.xwiki.eclipse.ui.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.storage.DataManager;
import org.xwiki.eclipse.ui.dialogs.SelectionDialog;
import org.xwiki.eclipse.ui.utils.UIUtils;

/**
 * @version $Id$
 */
public class DownloadAttachmentHandler extends AbstractHandler
{

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        /* get the shell */
        Shell shell = HandlerUtil.getActiveShellChecked(event);

        ISelection selection = HandlerUtil.getCurrentSelection(event);

        Set selectedObjects = UIUtils.getSelectedObjectsFromSelection(selection);

        SelectionDialog selectionDialog =
            new SelectionDialog(HandlerUtil.getActiveShell(event), "Download attachments",
                "Review the attachments to be downloaded", selectedObjects);
        int result = selectionDialog.open();
        if (result == IDialogConstants.CANCEL_ID) {
            return null;
        }

        Set<Object> attachmentsToBeDownloaded = selectionDialog.getSelectedObjects();
        final List<XWikiEclipseAttachment> attachments = new ArrayList<XWikiEclipseAttachment>();
        final List<String> names = new ArrayList<String>();

        for (Object selectedObject : selectedObjects) {
            if (attachmentsToBeDownloaded.contains(selectedObject)) {
                if (selectedObject instanceof XWikiEclipseAttachment) {
                    XWikiEclipseAttachment attachment = (XWikiEclipseAttachment) selectedObject;
                    attachments.add(attachment);
                    names.add(attachment.getName());
                } else {
                    return null;
                }
            }
        }

        if (attachments.size() > 0) {
            final DataManager dataManager = attachments.get(0).getDataManager();

            /* pop up a directory selection dialog */
            DirectoryDialog dirDialog = new DirectoryDialog(shell);

            /* Set the initial filter path according to user directory */
            dirDialog.setFilterPath(System.getProperty("user.dir"));

            /* Change the title bar text */
            dirDialog.setText("Download the attachments");

            /* Customizable message displayed in the dialog */
            dirDialog.setMessage("Select a directory");

            /*
             * Calling open() will open and run the dialog. It will return the selected directory, or null if user
             * cancels
             */
            /* construct file name = dir + attachment name */
            final String dir = dirDialog.open();
            if (dir != null) {

                Job downloadJob = new Job(String.format("Downloading %s", Arrays.toString(names.toArray())))
                {

                    @Override
                    protected IStatus run(IProgressMonitor monitor)
                    {

                        monitor.beginTask("Downloading", 100);
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                        int work = 100 / attachments.size();

                        for (XWikiEclipseAttachment attachment : attachments) {
                            monitor.setTaskName("Downloading " + attachment.getName());
                            dataManager.download(dir, attachment);
                            monitor.worked(work);
                        }

                        monitor.done();
                        return Status.OK_STATUS;
                    }
                };
                downloadJob.setUser(true);
                downloadJob.schedule();

            }

        }

        return null;
    }
}
