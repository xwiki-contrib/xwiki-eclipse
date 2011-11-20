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
package org.xwiki.eclipse.ui;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.progress.DeferredTreeContentManager;
import org.xwiki.eclipse.core.CoreLog;
import org.xwiki.eclipse.core.notification.CoreEvent;
import org.xwiki.eclipse.core.notification.ICoreEventListener;
import org.xwiki.eclipse.core.notification.NotificationManager;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.model.XWikiEclipseTag;
import org.xwiki.eclipse.storage.DataManager;
import org.xwiki.eclipse.storage.DataManagerRegistry;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;
import org.xwiki.eclipse.storage.utils.IdProcessor;
import org.xwiki.eclipse.ui.utils.UIUtils;

/**
 * @version $Id$
 */
public class NavigatorContentProvider extends BaseWorkbenchContentProvider implements ICoreEventListener
{
    private static final Object[] NO_OBJECTS = new Object[0];

    private AbstractTreeViewer viewer;

    private IWorkingSet workingSet;

    private DeferredTreeContentManager deferredTreeContentManager;

    public NavigatorContentProvider()
    {
        super();
        NotificationManager.getDefault().addListener(
            this,
            new CoreEvent.Type[] {CoreEvent.Type.DATA_MANAGER_REGISTERED, CoreEvent.Type.DATA_MANAGER_UNREGISTERED,
            CoreEvent.Type.DATA_MANAGER_CONNECTED, CoreEvent.Type.DATA_MANAGER_DISCONNECTED,
            CoreEvent.Type.PAGE_STORED, CoreEvent.Type.OBJECT_STORED, CoreEvent.Type.PAGE_REMOVED,
            CoreEvent.Type.OBJECT_REMOVED, CoreEvent.Type.REFRESH, CoreEvent.Type.PAGE_RENAMED,
            CoreEvent.Type.SPACE_REMOVED, CoreEvent.Type.COMMENT_REMOVED, CoreEvent.Type.COMMENT_STORED,
            CoreEvent.Type.ATTACHMENT_REMOVED, CoreEvent.Type.ATTACHMENT_UPLOADED, CoreEvent.Type.ATTACHMENT_UPDATED,
            CoreEvent.Type.TAG_STORED});

        workingSet = null;
    }

    @Override
    public void dispose()
    {
        NotificationManager.getDefault().removeListener(this);
        super.dispose();
    }

    @Override
    public Object[] getChildren(Object element)
    {
        /* If our parent is a project then return the data manager associated to that project */
        if (element instanceof IProject) {
            IProject project = (IProject) element;
            DataManager dataManager = DataManagerRegistry.getDefault().findDataManagerByProject(project);
            if (dataManager != null) {
                return new Object[] {dataManager};
            } else {
                return NO_OBJECTS;
            }
        }

        return deferredTreeContentManager.getChildren(element);
    }

    @Override
    public boolean hasChildren(Object element)
    {
        return deferredTreeContentManager.mayHaveChildren(element);
    }

    @Override
    public Object[] getElements(Object element)
    {
        Object[] result = DataManagerRegistry.getDefault().getDataManagers().toArray();
        return UIUtils.filterByWorkingSet(result, workingSet);
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        this.viewer = (AbstractTreeViewer) viewer;

        if (newInput instanceof IWorkingSet) {
            workingSet = (IWorkingSet) newInput;
        } else {
            workingSet = null;
        }

        deferredTreeContentManager = new WorkingSetDeferredTreeContentManager(this.viewer, workingSet);

        super.inputChanged(viewer, oldInput, newInput);
    }

    public void handleCoreEvent(final CoreEvent event)
    {
        switch (event.getType()) {
            case DATA_MANAGER_REGISTERED:
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        /*
                         * FIXME: Find a way to add new DataManagers to the viewer to avoid flicker and loss of expanded
                         * state caused by refresh(). Tried: viewer.add(dataManager.getProject().getParent(),
                         * dataManager) but the data manager that was added could not be expanded. No arrow appeared
                         * next to it and isExpandable(dataManager) returns false. The arrow would appear only after
                         * issuing refresh(), but that destroys the expanded state of the viewer.
                         */
                        viewer.refresh();
                    }
                });
                break;

            case DATA_MANAGER_UNREGISTERED:
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        DataManager dataManager = (DataManager) event.getData();
                        viewer.remove(dataManager);
                    }
                });
                break;

            case DATA_MANAGER_CONNECTED:
            case DATA_MANAGER_DISCONNECTED:
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        DataManager dataManager = (DataManager) event.getSource();
                        viewer.refresh(dataManager);
                    }
                });
                break;

            case PAGE_STORED:
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        viewer.update(event.getData(), null);
                    }
                });
                break;

            case PAGE_RENAMED:
                break;

            case PAGE_REMOVED:
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        viewer.remove(event.getData());
                    }
                });
                break;

            case OBJECT_STORED:
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        /*
                         * Build an object summary corresponding to the object received through the event. This is
                         * needed because in the tree we only find object summaries, and to match them we need this kind
                         * of objects.
                         */
                        XWikiEclipseObject object = (XWikiEclipseObject) event.getData();
                        XWikiEclipseObjectSummary objectSummary =
                            new XWikiEclipseObjectSummary(object.getDataManager());
                        objectSummary.setClassName(object.getClassName());
                        objectSummary.setId(object.getId());
                        objectSummary.setNumber(object.getNumber());
                        objectSummary.setPageId(object.getPageId());
                        objectSummary.setPageName(object.getPageName());
                        objectSummary.setSpace(object.getSpace());
                        objectSummary.setWiki(object.getWiki());

                        viewer.update(objectSummary, null);
                    }
                });
                break;
            case TAG_STORED:
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        XWikiEclipseTag tag = (XWikiEclipseTag) event.getData();

                        try {
                            XWikiEclipsePageSummary pageSummary =
                                tag.getDataManager().getPageSummary(tag.getWiki(), tag.getSpace(), tag.getPage(), "");
                            /* refresh the page */
                            viewer.setExpandedState(pageSummary, true);
                            viewer.refresh(pageSummary);
                        } catch (XWikiEclipseStorageException e) {
                            CoreLog.logError("Error getting page summary in navigator content provider", e);
                        }

                    }
                });
                break;
            case ATTACHMENT_UPLOADED:
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) event.getData();

                        /* refresh the page */
                        viewer.setExpandedState(pageSummary, true);
                        viewer.refresh(pageSummary);
                    }
                });
                break;
            case ATTACHMENT_UPDATED:

                Map<XWikiEclipsePageSummary, List<XWikiEclipseAttachment>> pageSummaries =
                    (Map<XWikiEclipsePageSummary, List<XWikiEclipseAttachment>>) event.getData();

                /* refresh the page */
                for (final XWikiEclipsePageSummary pageSummary : pageSummaries.keySet()) {
                    Display.getDefault().asyncExec(new Runnable()
                    {
                        public void run()
                        {
                            viewer.setExpandedState(pageSummary, true);
                            viewer.refresh(pageSummary);
                        }
                    });
                }

                break;
            case ATTACHMENT_REMOVED:

                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        XWikiEclipseAttachment attachment = (XWikiEclipseAttachment) event.getData();
                        try {
                            String pageId = attachment.getPageId();

                            IdProcessor parser = new IdProcessor(pageId);

                            XWikiEclipsePageSummary pageSummary =
                                attachment.getDataManager().getPageSummary(parser.getWiki(), parser.getSpace(),
                                    parser.getPage(), "");
                            viewer.setExpandedState(pageSummary, true);
                            viewer.refresh(pageSummary);
                        } catch (XWikiEclipseStorageException e) {
                            CoreLog.logError("Error getting page summary in navigator content provider", e);
                        }

                    }

                });
                break;
            case COMMENT_REMOVED:

                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        XWikiEclipseComment comment = (XWikiEclipseComment) event.getData();
                        viewer.remove(comment);
                    }

                });
                break;
            case COMMENT_STORED:

                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        try {
                            XWikiEclipseComment comment = (XWikiEclipseComment) event.getData();
                            String pageId = comment.getPageId();
                            IdProcessor parser = new IdProcessor(pageId);

                            XWikiEclipsePageSummary pageSummary =
                                comment.getDataManager().getPageSummary(parser.getWiki(), parser.getSpace(),
                                    parser.getPage(), "");

                            viewer.setExpandedState(pageSummary, true);
                            viewer.refresh(pageSummary, true);

                        } catch (XWikiEclipseStorageException e) {
                            CoreLog.logError("Error getting page summary in navigator content provider", e);
                        }

                    }

                });
                break;
            case OBJECT_REMOVED:
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        XWikiEclipseObjectSummary object = (XWikiEclipseObjectSummary) event.getData();
                        viewer.remove(object);
                    }

                });
                break;

            case SPACE_REMOVED:
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        XWikiEclipseSpaceSummary space = (XWikiEclipseSpaceSummary) event.getData();
                        viewer.remove(space);
                    }

                });
                break;

            case REFRESH:
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        /*
                         * FIXME: This should work but it doesn't. Can't get the viewer's expanded elements to restore
                         * after a refresh. Tried many things, none seem to work. Any attempt at restoring the expanded
                         * state fails, although the viewer's data classes all have equals and hashCode methods
                         * overridden in their superclass.
                         */
                        Object[] expandedElements = viewer.getVisibleExpandedElements();
                        viewer.refresh(event.getData());
                        viewer.setExpandedElements(expandedElements);
                    }
                });
                break;
        }
    }
}
