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

import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.eclipse.core.CoreLog;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.ui.utils.UIUtils;

/**
 * @version $Id$
 */
public class GrabSpaceHandler extends AbstractHandler
{
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);

        final Set selectedObjects = UIUtils.getSelectedObjectsFromSelection(selection);

        Job grabJob = new Job("Grab Space...")
        {

            @Override
            protected IStatus run(final IProgressMonitor monitor)
            {
                try {
                    for (Object object : selectedObjects) {
                        if (object instanceof XWikiEclipseSpaceSummary) {
                            final XWikiEclipseSpaceSummary spaceSummary = (XWikiEclipseSpaceSummary) object;

                            List<XWikiEclipsePageSummary> pageSummaries =
                                spaceSummary.getDataManager().getPageSummaries(spaceSummary.getWiki(),
                                    spaceSummary.getName());

                            monitor.beginTask("Fetching pages", pageSummaries.size());

                            if (monitor.isCanceled()) {
                                return Status.CANCEL_STATUS;
                            }

                            for (XWikiEclipsePageSummary pageSummary : pageSummaries) {
                                monitor.setTaskName(String.format("Fetching %s", pageSummary.getId()));

                                pageSummary.getDataManager().getPage(pageSummary.getWiki(), pageSummary.getSpace(),
                                    pageSummary.getName(), pageSummary.getLanguage());

                                if (monitor.isCanceled()) {
                                    return Status.CANCEL_STATUS;
                                }

                                monitor.worked(1);
                            }

                        }
                    }
                } catch (Exception e) {
                    CoreLog.logError("Error in grabbing space", e);
                }

                monitor.done();
                return Status.OK_STATUS;
            }
        };

        grabJob.setUser(true);
        grabJob.schedule();

        return null;
    }
}
