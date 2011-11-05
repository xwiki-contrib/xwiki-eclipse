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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipseObjectCollection;
import org.xwiki.eclipse.model.XWikiEclipseObjectCollection.Type;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseTag;
import org.xwiki.eclipse.storage.DataManager;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.UIPlugin;
import org.xwiki.eclipse.ui.utils.UIUtils;

/**
 * @version $Id$
 */
public class XWikiEclipseObjectCollectionAdapter extends WorkbenchAdapter implements IDeferredWorkbenchAdapter
{
    @Override
    public Object[] getChildren(Object object)
    {

        if (object instanceof XWikiEclipseObjectCollection) {
            XWikiEclipseObjectCollection collection = (XWikiEclipseObjectCollection) object;
            XWikiEclipsePageSummary pageSummary = collection.getPageSummary();

            DataManager dataManager = collection.getDataManager();

            Map<String, List<XWikiEclipseObjectSummary>> classToObjectsMap =
                new HashMap<String, List<XWikiEclipseObjectSummary>>();

            try {
                switch (collection.getType()) {
                    case OBJECTS:

                        List<XWikiEclipseObjectSummary> objects =
                            dataManager.getObjectSummaries(pageSummary.getWiki(), pageSummary.getSpace(),
                                pageSummary.getName());

                        for (XWikiEclipseObjectSummary objectSummary : objects) {
                            List<XWikiEclipseObjectSummary> l = classToObjectsMap.get(objectSummary.getClassName());
                            if (l == null) {
                                l = new ArrayList<XWikiEclipseObjectSummary>();
                                classToObjectsMap.put(objectSummary.getClassName(), l);
                            }

                            l.add(objectSummary);
                        }

                        if (collection.getArg() == null) {
                            List<XWikiEclipseObjectCollection> result = new ArrayList<XWikiEclipseObjectCollection>();
                            Set<String> classNames = classToObjectsMap.keySet();
                            for (String className : classNames) {
                                result.add(new XWikiEclipseObjectCollection(dataManager, pageSummary, Type.OBJECTS,
                                    className));
                            }
                            return result.toArray();
                        } else {
                            objects = classToObjectsMap.get(collection.getArg());
                            if (objects != null) {
                                return objects.toArray();
                            } else {
                                return NO_CHILDREN;
                            }
                        }

                    case ATTACHMENTS:
                        List<XWikiEclipseAttachment> attachments =
                            dataManager.getAttachments(pageSummary.getWiki(), pageSummary.getSpace(),
                                pageSummary.getName());

                        return attachments.toArray();

                    case COMMENTS:
                        List<XWikiEclipseComment> comments =
                            dataManager.getComments(pageSummary.getWiki(), pageSummary.getSpace(),
                                pageSummary.getName());

                        return comments.toArray();

                    case TAGS:
                        List<XWikiEclipseTag> tags = dataManager.getTags(pageSummary);

                        return tags.toArray();
                }
            } catch (Exception e) {
                UIUtils.showMessageDialog(Display.getDefault().getActiveShell(), SWT.ICON_ERROR,
                    "Error getting objects.", "There was a communication error while getting objects.");
            }
        }

        return super.getChildren(object);
    }

    @Override
    public String getLabel(Object object)
    {
        if (object instanceof XWikiEclipseObjectCollection) {
            XWikiEclipseObjectCollection collection = (XWikiEclipseObjectCollection) object;
            Type type = collection.getType();

            switch (type) {
                case OBJECTS:
                    if (collection.getArg() == null) {
                        return "Objects";
                    } else {
                        return collection.getArg().toString();
                    }
                case ATTACHMENTS:
                    return "Attachments";
                case TAGS:
                    return "Tags";
                case COMMENTS:
                    return "Comments";
                case ANNOTATIONS:
                    return "Annotations";
                default:
                    return type.toString();
            }
        }

        return super.getLabel(object);
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object)
    {
        if (object instanceof XWikiEclipseObjectCollection) {
            XWikiEclipseObjectCollection collection = (XWikiEclipseObjectCollection) object;
            Type type = collection.getType();

            switch (type) {
                case OBJECTS:
                    return UIPlugin.getImageDescriptor(UIConstants.OBJECT_COLLECTION_ICON);
                case ATTACHMENTS:
                    return UIPlugin.getImageDescriptor(UIConstants.PAGE_ATTACHMENTS_ICON);
                case TAGS:
                    return UIPlugin.getImageDescriptor(UIConstants.TAGS_ICON);
                case COMMENTS:
                    return UIPlugin.getImageDescriptor(UIConstants.PAGE_COMMENTS_ICON);
                case ANNOTATIONS:
                    return UIPlugin.getImageDescriptor(UIConstants.PAGE_ANNOTATIONS_ICON);
                default:
                    return UIPlugin.getImageDescriptor(UIConstants.OBJECT_COLLECTION_ICON);
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.progress.IDeferredWorkbenchAdapter#fetchDeferredChildren(java.lang.Object,
     *      org.eclipse.ui.progress.IElementCollector, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void fetchDeferredChildren(Object object, IElementCollector collector, IProgressMonitor monitor)
    {
        collector.add(getChildren(object), monitor);
        collector.done();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.progress.IDeferredWorkbenchAdapter#isContainer()
     */
    @Override
    public boolean isContainer()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.progress.IDeferredWorkbenchAdapter#getRule(java.lang.Object)
     */
    @Override
    public ISchedulingRule getRule(Object object)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
