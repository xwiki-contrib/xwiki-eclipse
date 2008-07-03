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
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.DataManagerRegistry;
import org.xwiki.eclipse.core.model.XWikiEclipseObject;
import org.xwiki.eclipse.core.model.XWikiEclipsePage;
import org.xwiki.eclipse.core.notifications.CoreEvent;
import org.xwiki.eclipse.core.notifications.ICoreEventListener;
import org.xwiki.eclipse.core.notifications.NotificationManager;
import org.xwiki.eclipse.ui.utils.UIUtils;

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
            CoreEvent.Type.OBJECT_REMOVED, CoreEvent.Type.REFRESH});

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
            case DATA_MANAGER_UNREGISTERED:
                Display.getDefault().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        viewer.refresh();
                    }
                });
                break;

            case DATA_MANAGER_CONNECTED:
            case DATA_MANAGER_DISCONNECTED:
                Display.getDefault().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        DataManager dataManager = (DataManager) event.getSource();
                        viewer.refresh(dataManager);
                    }
                });
                break;

            case PAGE_STORED:
                Display.getDefault().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        XWikiEclipsePage page = (XWikiEclipsePage) event.getData();
                        viewer.refresh(page.getSummary());
                    }
                });
                break;

            case OBJECT_STORED:
                Display.getDefault().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        XWikiEclipseObject object = (XWikiEclipseObject) event.getData();
                        viewer.refresh(object.getSummary());
                    }

                });
                break;

            case PAGE_REMOVED:
                Display.getDefault().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        XWikiEclipsePage page = (XWikiEclipsePage) event.getData();
                        viewer.remove(page.getSummary());
                    }
                });
                break;

            case OBJECT_REMOVED:
                Display.getDefault().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        XWikiEclipsePage page = (XWikiEclipsePage) event.getData();
                        viewer.refresh(page.getSummary());
                    }

                });
                break;

            case REFRESH:
                Display.getDefault().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        viewer.refresh(event.getData());
                    }
                });
                break;
        }
    }

}
