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
package org.xwiki.eclipse.ui.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.xwiki.eclipse.model.XWikiEclipseObjectCollection;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.storage.DataManager;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.UIPlugin;

/**
 * @version $Id$
 */
public class XWikiEclipsePageSummaryAdapter extends WorkbenchAdapter implements IDeferredWorkbenchAdapter
{
    public Object[] getChildren(Object object)
    {

        if (object instanceof XWikiEclipsePageSummary) {
            XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) object;
            DataManager dataManager = pageSummary.getDataManager();

            List<XWikiEclipseObjectCollection> result = new ArrayList<XWikiEclipseObjectCollection>();

            if (pageSummary.has(XWikiEclipsePageSummary.Data.OBJECTS)) {
                result.add(new XWikiEclipseObjectCollection(dataManager, pageSummary,
                    XWikiEclipseObjectCollection.Type.OBJECTS));
            }
            if (pageSummary.has(XWikiEclipsePageSummary.Data.ATTACHMENTS)) {
                result.add(new XWikiEclipseObjectCollection(dataManager, pageSummary,
                    XWikiEclipseObjectCollection.Type.ATTACHMENTS));
            }
            if (pageSummary.has(XWikiEclipsePageSummary.Data.TAGS)) {
                result.add(new XWikiEclipseObjectCollection(dataManager, pageSummary,
                    XWikiEclipseObjectCollection.Type.TAGS));
            }
            if (pageSummary.has(XWikiEclipsePageSummary.Data.COMMENTS)) {
                result.add(new XWikiEclipseObjectCollection(dataManager, pageSummary,
                    XWikiEclipseObjectCollection.Type.COMMENTS));
            }

            return result.toArray();
        }

        return super.getChildren(object);
    }

    @Override
    public String getLabel(Object object)
    {
        if (object instanceof XWikiEclipsePageSummary) {
            XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) object;

            String title = pageSummary.getTitle();
            if (title == null) {
                title = pageSummary.getId();
            }

            return title + (pageSummary.getLanguage().equals("") ? "" : "[" + pageSummary.getLanguage() + "]");
        }

        return super.getLabel(object);
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object)
    {
        if (object instanceof XWikiEclipsePageSummary) {
            XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) object;

            if (pageSummary.getDataManager().isInConflict(pageSummary.getId())) {
                return UIPlugin.getImageDescriptor(UIConstants.PAGE_CONFLICT_ICON);
            }

            if (pageSummary.getDataManager().isPageLocallyAvailable(pageSummary.getId(), pageSummary.getLanguage())) {
                return UIPlugin.getImageDescriptor(UIConstants.PAGE_LOCALLY_AVAILABLE_ICON);
            } else {
                return UIPlugin.getImageDescriptor(UIConstants.PAGE_ICON);
            }
        }

        return null;
    }

    public void fetchDeferredChildren(Object object, IElementCollector collector, IProgressMonitor monitor)
    {
        collector.add(getChildren(object), monitor);
        collector.done();
    }

    public ISchedulingRule getRule(Object object)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isContainer()
    {
        return true;
    }
}
