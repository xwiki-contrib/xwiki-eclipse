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
package org.xwiki.eclipse.ui.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.progress.DeferredTreeContentManager;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.xwiki.eclipse.model.XWikiEclipseClass;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.UIPlugin;

/**
 * @version $Id$
 */
public class ObjectSettingsPage extends WizardPage
{
    private NewObjectWizardState newObjectWizardState;

    private XWikiEclipsePageSummary pageSummary;

    private TreeViewer viewer;

    private Map<String, List<String>> clazzMap = null;

    public ObjectSettingsPage(String wizardPageName, XWikiEclipsePageSummary pageSummary)
    {
        super(wizardPageName);
        setTitle("New object");
        setImageDescriptor(UIPlugin.getImageDescriptor(UIConstants.OBJECT_SETTINGS_BANNER));
        this.pageSummary = pageSummary;
    }

    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(composite);

        newObjectWizardState = ((NewObjectWizard) getWizard()).getNewObjectWizardState();

        Group group = new Group(composite, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(group);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(group);
        group.setText("Object settings");

        /* Page, space, wiki */
        Label label = new Label(group, SWT.NONE);
        label.setText("Page:");
        label = new Label(group, SWT.NONE);
        label.setText(newObjectWizardState.getPageId());

        label = new Label(group, SWT.NONE);
        label.setText("Space:");
        label = new Label(group, SWT.NONE);
        label.setText(newObjectWizardState.getSpace());

        label = new Label(group, SWT.NONE);
        label.setText("Wiki:");
        label = new Label(group, SWT.NONE);
        label.setText(newObjectWizardState.getWiki());

        label = new Label(group, SWT.NONE);
        label.setText("Class:");

        viewer = new TreeViewer(group, SWT.VIRTUAL | SWT.BORDER | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getControl());

        viewer.setContentProvider(new ClassSelectionTreeContentProvider());
        viewer.setLabelProvider(new WorkbenchLabelProvider());
        viewer.setUseHashlookup(true);
        viewer.setInput(new DeferredTreeItem("All Classes"));

        viewer.addSelectionChangedListener(new ISelectionChangedListener()
        {

            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                if (event.getSelection().isEmpty()) {
                    return;
                }

                if (event.getSelection() instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                    Object o = selection.iterator().next();
                    /* make sure it is the tree item */
                    if (o instanceof DeferredTreeItem) {
                        DeferredTreeItem item = (DeferredTreeItem) o;
                        newObjectWizardState.setClassName(item.getName());
                    }
                }
                getContainer().updateButtons();
            }
        });

        setControl(composite);
    }

    @Override
    public boolean isPageComplete()
    {
        if (viewer.getSelection().isEmpty()) {
            setErrorMessage("A class must be selected");
            return false;
        }

        if (viewer.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
            Object o = selection.iterator().next();
            if (o instanceof DeferredTreeItem) {
                DeferredTreeItem item = (DeferredTreeItem) o;
                if (clazzMap.containsKey(item.getName())) {
                    setErrorMessage("Please select a class instead of a collection");
                    return false;
                }
            } else {
                return false;
            }

        }

        setErrorMessage(null);
        return true;
    }

    class ClassSelectionTreeContentProvider implements ITreeContentProvider
    {
        private DeferredTreeContentManager manager;

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        @Override
        public void dispose()
        {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
         *      java.lang.Object, java.lang.Object)
         */
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
            if (viewer instanceof AbstractTreeViewer) {
                manager = new DeferredTreeContentManager((AbstractTreeViewer) viewer);
            }

        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
         */
        @Override
        public Object[] getElements(Object inputElement)
        {
            if (inputElement instanceof DeferredTreeItem) {
                return manager.getChildren(inputElement);
            }

            return new Object[0];
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         */
        @Override
        public Object[] getChildren(Object parentElement)
        {
            return manager.getChildren(parentElement);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         */
        @Override
        public Object getParent(Object element)
        {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         */
        @Override
        public boolean hasChildren(Object element)
        {
            return manager.mayHaveChildren(element);
        }
    }

    class DeferredTreeItem implements IDeferredWorkbenchAdapter
    {

        private String name;

        public DeferredTreeItem(String name)
        {
            this.name = name;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
         */
        @Override
        public Object[] getChildren(Object o)
        {
            DeferredTreeItem item = (DeferredTreeItem) o;
            if (item.getName().equals("All Classes")) {
                List<DeferredTreeItem> children = new ArrayList<ObjectSettingsPage.DeferredTreeItem>();
                for (String s : clazzMap.keySet()) {
                    children.add(new DeferredTreeItem(s));
                }
                return children.toArray();
            }

            if (clazzMap.containsKey(item.getName())) {
                List<DeferredTreeItem> children = new ArrayList<ObjectSettingsPage.DeferredTreeItem>();
                for (String s : clazzMap.get(item.getName())) {
                    children.add(new DeferredTreeItem(s));
                }
                return children.toArray();
            }

            return new Object[0];
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
         */
        @Override
        public ImageDescriptor getImageDescriptor(Object object)
        {
            DeferredTreeItem item = (DeferredTreeItem) object;
            if (item.getName().equals("All Classes") || clazzMap.containsKey(item.getName())) {

                return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT);
            }

            return UIPlugin.getImageDescriptor(UIConstants.CLASS_ICON);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
         */
        @Override
        public String getLabel(Object o)
        {
            if (o instanceof DeferredTreeItem) {
                return ((DeferredTreeItem) o).getName();
            }
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
         */
        @Override
        public Object getParent(Object o)
        {
            // TODO Auto-generated method stub
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
            DeferredTreeItem item = (DeferredTreeItem) object;

            if (item.getName().equals("All Classes")) {
                List<XWikiEclipseClass> clazzs = pageSummary.getDataManager().getClasses(pageSummary.getWiki());
                for (XWikiEclipseClass c : clazzs) {
                    String id = c.getId();

                    String key = null;
                    if (id.contains(".")) {
                        key = id.split("\\.")[0];
                    } else {
                        key = id;
                    }

                    if (clazzMap == null) {
                        clazzMap = new HashMap<String, List<String>>();
                    }

                    if (clazzMap.containsKey(key)) {
                        clazzMap.get(key).add(id);
                    } else {
                        List<String> list = new ArrayList<String>();
                        list.add(id);
                        clazzMap.put(key, list);
                    }
                }

                collector.add(getChildren(object), monitor);
                collector.done();

            } else {
                collector.add(getChildren(object), monitor);
                collector.done();
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.ui.progress.IDeferredWorkbenchAdapter#isContainer()
         */
        @Override
        public boolean isContainer()
        {
            if (name.equals("All Classes") || clazzMap.containsKey(name)) {
                return true;
            }

            return false;
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

        public String getName()
        {
            return name;
        }
    }
}
