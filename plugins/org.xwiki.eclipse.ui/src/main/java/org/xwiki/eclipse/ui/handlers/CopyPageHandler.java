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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.ui.dialogs.RenamePageDialog;
import org.xwiki.eclipse.ui.utils.UIUtils;
import org.xwiki.eclipse.ui.utils.XWikiEclipseSafeRunnable;

/**
 * @version $Id$
 */
public class CopyPageHandler extends AbstractHandler
{
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);

        Set selectedObjects = UIUtils.getSelectedObjectsFromSelection(selection);
        if (selectedObjects.size() == 1) {
            Object selectedObject = selectedObjects.iterator().next();
            if (selectedObject instanceof XWikiEclipsePageSummary) {
                final XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) selectedObject;               

                final RenamePageDialog dialog =
                    new RenamePageDialog(HandlerUtil.getActiveShell(event), pageSummary, "copyFrom");
                dialog.open();

                if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
                    Job renameJob = new Job("Copying...")
                    {
                        @Override
                        protected IStatus run(IProgressMonitor monitor)
                        {
                            monitor.beginTask("Copying " + pageSummary.getId(), IProgressMonitor.UNKNOWN);
                            if (monitor.isCanceled()) {
                                return Status.CANCEL_STATUS;
                            }

                            if (dialog.getAction().equalsIgnoreCase("copyFrom")) {
                                SafeRunner.run(new XWikiEclipseSafeRunnable()
                                {
                                    public void run() throws Exception
                                    {
                                        pageSummary.getDataManager().copyPage(pageSummary, dialog.getNewWiki(),
                                            dialog.getNewSpace(), dialog.getNewPageName());
                                    }
                                });
                            }

                            monitor.done();
                            return Status.OK_STATUS;
                        }
                    };

                    renameJob.setUser(true);
                    renameJob.schedule();
                }
            }
        }

        return null;
    }
}
