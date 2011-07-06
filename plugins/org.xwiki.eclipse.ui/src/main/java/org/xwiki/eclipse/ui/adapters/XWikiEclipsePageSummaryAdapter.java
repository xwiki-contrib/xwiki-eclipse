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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.xwiki.eclipse.core.CoreLog;
import org.xwiki.eclipse.model.ModelObject;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.model.XWikiEclipseClassSummary;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipseObjectCollection;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseTag;
import org.xwiki.eclipse.storage.DataManager;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.UIPlugin;
import org.xwiki.eclipse.ui.utils.UIUtils;

/**
 * @version $Id$
 */
public class XWikiEclipsePageSummaryAdapter extends WorkbenchAdapter implements IDeferredWorkbenchAdapter
{
    @Override
    public Object[] getChildren(Object object)
    {
        if (object instanceof XWikiEclipsePageSummary) {
            final XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) object;

            try {
                /* fetch attachments, pageClass, tags, comments, and annotations */
                DataManager dataManager = pageSummary.getDataManager();

                List<XWikiEclipseObjectSummary> objects = dataManager.getObjects(pageSummary);
                List<XWikiEclipseAttachment> attachments = dataManager.getAttachments(pageSummary);
                XWikiEclipseClassSummary pageClass = dataManager.getPageClass(pageSummary);
                List<XWikiEclipseTag> tags = dataManager.getTags(pageSummary);
                List<XWikiEclipseComment> comments = dataManager.getComments(pageSummary);

                List<ModelObject> result = new ArrayList<ModelObject>();

                /* add pageClass */
                result.add(pageClass);

                /* add attachments */
                List<ModelObject> list = null;
                if (attachments != null && attachments.size() > 0) {
                    XWikiEclipseObjectCollection a = new XWikiEclipseObjectCollection(dataManager);
                    a.setClassName("Attachments");

                    list = new ArrayList<ModelObject>();
                    for (XWikiEclipseAttachment attach : attachments) {
                        list.add(attach);
                    }

                    a.setObjects(list);
                    result.add(a);
                }

                /* add tags */
                if (tags != null && tags.size() > 0) {
                    XWikiEclipseObjectCollection t = new XWikiEclipseObjectCollection(dataManager);
                    t.setClassName("Tags");

                    list = new ArrayList<ModelObject>();
                    for (XWikiEclipseTag tag : tags) {
                        list.add(tag);
                    }

                    t.setObjects(list);
                    result.add(t);
                }

                /* add comments */
                if (comments != null && comments.size() > 0) {
                    XWikiEclipseObjectCollection t = new XWikiEclipseObjectCollection(dataManager);
                    t.setClassName("Comments");

                    list = new ArrayList<ModelObject>();
                    for (XWikiEclipseComment comment : comments) {
                        list.add(comment);
                    }

                    t.setObjects(list);
                    result.add(t);
                }

                /* add annotation, all the annotations are included in the object list */
                if (objects != null && objects.size() > 0) {
                    XWikiEclipseObjectCollection t = new XWikiEclipseObjectCollection(dataManager);
                    t.setClassName("Annotations");

                    list = new ArrayList<ModelObject>();
                    for (XWikiEclipseObjectSummary objectSummary : objects) {
                        if (objectSummary.getClassName().equals("AnnotationCode.AnnotationClass")) {
                            list.add(objectSummary);
                        }

                    }

                    t.setObjects(list);
                    result.add(t);
                }

                return result.toArray();
            } catch (XWikiEclipseStorageException e) {
                UIUtils
                    .showMessageDialog(
                        Display.getDefault().getActiveShell(),
                        SWT.ICON_ERROR,
                        "Error getting objects.",
                        "There was a communication error while getting objects. XWiki Eclipse is taking the connection offline in order to prevent further errors. Please check your remote XWiki status and then try to reconnect.");
                pageSummary.getDataManager().disconnect();

                CoreLog.logError("Error getting objects.", e);

                return NO_CHILDREN;
            }
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

            return title;
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

            if (pageSummary.getDataManager().isLocallyAvailable(pageSummary)) {
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
