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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.progress.DeferredTreeContentManager;
import org.xwiki.eclipse.core.DataManagerRegistry;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.storage.DataManager;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;
import org.xwiki.eclipse.storage.notification.CoreEvent;
import org.xwiki.eclipse.storage.notification.ICoreEventListener;
import org.xwiki.eclipse.storage.notification.NotificationManager;
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
            CoreEvent.Type.ATTACHMENT_REMOVED});

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
                // Display.getDefault().syncExec(new Runnable()
                // {
                // public void run()
                // {
                // XWikiEclipsePage page = (XWikiEclipsePage) event.getData();
                //
                // // Check if this is a newly created page.
                // if (page.getMajorVersion() == 1) {
                // // Make sure the new page/space get drawn.
                // XWikiEclipseSpaceSummary space = page.getDataManager().getSpaceSummary(page);
                //
                // // SpaceSummary spaceSummary = new SpaceSummary();
                // // spaceSummary.setKey(page.getData().getSpace());
                // // spaceSummary.setName(page.getData().getSpace());
                // // XWikiEclipseSpaceSummary space =
                // // new XWikiEclipseSpaceSummary(page.getDataManager(), spaceSummary);
                //
                // // If the space did not previously exist, draw it.
                // if (viewer.testFindItem(space) == null)
                // viewer.add(page.getDataManager(), space);
                //
                // viewer.add(space, page.getSummary());
                // viewer.expandToLevel(page.getSummary(), 0);
                // } else {
                // viewer.refresh(page.getSummary());
                // }
                // }
                // });
                break;

            case PAGE_RENAMED:
                // Display.getDefault().syncExec(new Runnable()
                // {
                // public void run()
                // {
                // XWikiEclipsePage oldPage = ((XWikiEclipsePage[]) event.getData())[0];
                // XWikiEclipsePage newPage = ((XWikiEclipsePage[]) event.getData())[1];
                //
                // XWikiEclipseSpaceSummary space = oldPage.getSpaceSummary();
                //
                // // SpaceSummary spaceSummary = new SpaceSummary();
                // // spaceSummary.setKey(newPage.getData().getSpace());
                // // spaceSummary.setName(newPage.getData().getSpace());
                // // XWikiEclipseSpaceSummary space =
                // // new XWikiEclipseSpaceSummary(newPage.getDataManager(), spaceSummary);
                //
                // viewer.add(newPage.getDataManager(), space);
                // viewer.add(space, newPage.getSummary());
                // viewer.remove(oldPage.getSummary());
                // }
                // });
                break;

            case PAGE_REMOVED:
                // Display.getDefault().syncExec(new Runnable()
                // {
                // public void run()
                // {
                // XWikiEclipsePage page = (XWikiEclipsePage) event.getData();
                //
                // String spaceKey = page.getSpace();
                //
                // List<XWikiEclipsePageSummary> pages = null;
                // try {
                // pages = page.getDataManager().getPages(spaceKey);
                // } catch (XWikiEclipseStorageException e) {
                // CoreLog.logError("Unable to get space pages: " + e.getMessage());
                // }
                //
                // if (pages != null && pages.size() == 0) {
                // // The space is left with no pages so it has to be removed.
                // XWikiEclipseSpaceSummary space = page.getSpaceSummary();
                // // SpaceSummary spaceSummary = new SpaceSummary();
                // // spaceSummary.setKey(spaceKey);
                // // spaceSummary.setName(spaceKey);
                // //
                // // XWikiEclipseSpaceSummary space =
                // // new XWikiEclipseSpaceSummary(page.getDataManager(), spaceSummary);
                // viewer.remove(space);
                // } else {
                // viewer.remove(page.getSummary());
                // }
                // }
                // });
                break;

            case OBJECT_STORED:
                // Display.getDefault().syncExec(new Runnable()
                // {
                // public void run()
                // {
                // XWikiEclipseObject object = (XWikiEclipseObject) event.getData();
                // XWikiEclipsePageSummary pageSumary = object.getPageSummary();
                // // XWikiEclipsePageSummary pageSumary =
                // // new XWikiEclipsePageSummary(object.getDataManager(), object.getPageSummary());
                // /*
                // * FIXME: For lack of a way of knowing whether the object has just been created or modified, I
                // * chose to refresh all the objects in the page. Best way: like the PAGE_STORED event handling,
                // * only that, in that case, there was a way of knowing if the page was just created and that
                // * there were visual inconsistencies. Maybe a new OBJECT_CREATED event? This could be an elegant
                // * solution for the PAGE_STORED too, by introducing a PAGE_CREATED event.
                // */
                // viewer.refresh(pageSumary);
                // }
                //
                // });
                break;
            case ATTACHMENT_REMOVED:

                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        XWikiEclipseAttachment attachment = (XWikiEclipseAttachment) event.getData();
                        try {
                            XWikiEclipsePageSummary pageSummary =
                                attachment.getDataManager().getPageSummary(attachment);
                            viewer.setExpandedState(pageSummary, true);
                            viewer.refresh(pageSummary);

                        } catch (XWikiEclipseStorageException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
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

                        XWikiEclipseComment comment = (XWikiEclipseComment) event.getData();
                        XWikiEclipsePageSummary pageSummary;
                        try {
                            pageSummary = comment.getDataManager().getPageSummary(comment);

                            viewer.setExpandedState(pageSummary, true);

                            viewer.refresh(pageSummary, false);

                        } catch (XWikiEclipseStorageException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                });
                break;
            case OBJECT_REMOVED:
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        XWikiEclipseObject object = (XWikiEclipseObject) event.getData();
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
