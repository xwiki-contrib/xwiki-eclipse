/*
 * Copyright 2006-2007, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
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

package org.xwiki.plugins.eclipse.views.navigator;

import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiPage;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.model.wrappers.XWikiConnectionWrapper;
import org.xwiki.plugins.eclipse.model.wrappers.XWikiSpaceWrapper;
import org.xwiki.plugins.eclipse.util.GuiUtils;
import org.xwiki.plugins.eclipse.util.XWikiConstants;
import org.xwiki.plugins.eclipse.wizards.XWikiWizardDialog;
import org.xwiki.plugins.eclipse.wizards.connect.ConnectWizard;
import org.xwiki.plugins.eclipse.wizards.newpage.NewPageWizard;
import org.xwiki.plugins.eclipse.wizards.newspace.NewSpaceWizard;

/**
 * Document navigator (Tree Navigator) of the plug-in.
 */
public class XWikiNavigator extends ViewPart
{
    /**
     * built-in TreeViewer UI widget.
     */
    private TreeViewer viewer;

    /**
     * @see org.eclipse.ui.part.DrillDownAdapter.
     */
    private DrillDownAdapter drillDownAdapter;

    /**
     * Action for adding a new XWikiConnection.
     */
    private Action addConnectionAction;

    /**
     * Action for removing an existing XWikiConnection.
     */
    private Action removeConnectionAction;

    /**
     * Action for adding a new page.
     */
    private Action addPageAction;

    /**
     * Action for removing an existing page.
     */
    private Action removePageAction;

    /**
     * Action for adding a new Space.
     */
    private Action addSpaceAction;

    /**
     * Action for removing a Space.
     */
    private Action removeSpaceAction;

    /**
     * Action executed when the user double clicks on a tree item (edit document).
     */
    private Action editAction;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent)
    {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider(new XWikiNavigatorContentProvider());
        viewer.setLabelProvider(new XWikiNavigatorLabelProvider());
        viewer.setInput(getViewSite());
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
    }

    /**
     * Eclipse auto-generated code. hack these if you need to add items to different places in
     * workbench.
     */
    private void hookContextMenu()
    {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                XWikiNavigator.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    /**
     * Eclipse auto-generated code. hack these if you need to add items to different places in
     * workbench.
     */
    private void contributeToActionBars()
    {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    /**
     * Eclipse auto-generated code. hack these if you need to add items to different places in
     * workbench.
     */
    private void fillLocalPullDown(IMenuManager manager)
    {
        manager.add(addConnectionAction);
        manager.add(removeConnectionAction);
    }

    /**
     * Eclipse auto-generated code. hack these if you need to add items to different places in
     * workbench.
     */
    private void fillContextMenu(IMenuManager manager)
    {
        manager.add(addPageAction);
        manager.add(removePageAction);
        manager.add(new Separator());
        manager.add(addSpaceAction);
        manager.add(removeSpaceAction);
        manager.add(new Separator());
        manager.add(removeConnectionAction);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /**
     * Eclipse auto-generated code. hack these if you need to add items to different places in
     * workbench.
     */
    private void fillLocalToolBar(IToolBarManager manager)
    {
        manager.add(addConnectionAction);
        manager.add(removeConnectionAction);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
    }

    /**
     * Make actions alive.
     */
    private void makeActions()
    {
        /**
         * Add new connection action.
         */
        addConnectionAction = new Action()
        {
            public void run()
            {
                openConnectWizard();
            }
        };
        addConnectionAction.setText("New Login");
        addConnectionAction.setToolTipText("New Login");
        addConnectionAction.setImageDescriptor(GuiUtils
            .loadIconImage(XWikiConstants.ADD_CONNECTION_ICON));

        /**
         * Remove connection action.
         */
        removeConnectionAction = new Action()
        {
            public void run()
            {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                if (obj instanceof IXWikiConnection) {
                    IXWikiConnection connection =
                        new XWikiConnectionWrapper((IXWikiConnection) obj);
                    try {
                        connection.disconnect();
                    } catch (SwizzleConfluenceException e) {
                        // Will be logged else where.
                    }
                    viewer.refresh();
                }
            }
        };
        removeConnectionAction.setText("Logout");
        removeConnectionAction.setToolTipText("Logout");
        removeConnectionAction.setImageDescriptor(GuiUtils
            .loadIconImage(XWikiConstants.REMOVE_CONNECTION_ICON));
        removeConnectionAction.setEnabled(false);

        /**
         * Add page action.
         */
        addPageAction = new Action()
        {
            public void run()
            {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                IXWikiSpace space = null;
                if (obj instanceof IXWikiPage) {
                    space = new XWikiSpaceWrapper(((IXWikiPage) obj).getParentSpace());
                } else if (obj instanceof IXWikiSpace) {
                    space = new XWikiSpaceWrapper((IXWikiSpace) obj);
                }
                if (space != null) {
                    openAddNewPageWizard(space);
                }
            }
        };
        addPageAction.setText("New Page");
        addPageAction.setToolTipText("New Page");
        addPageAction.setImageDescriptor(GuiUtils.loadIconImage(XWikiConstants.ADD_PAGE_ICON));
        addPageAction.setEnabled(false);

        /**
         * Remove page action.
         */
        removePageAction = new Action()
        {
            public void run()
            {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                if (obj instanceof IXWikiPage) {
                    IXWikiPage wikipage = (IXWikiPage) obj;
                    IXWikiSpace space = new XWikiSpaceWrapper(wikipage.getParentSpace());
                    try {
                        space.removeChildPage(wikipage.getId());
                    } catch (SwizzleConfluenceException e) {
                        // Will be logged else where.
                    }
                    viewer.refresh();
                }
            }
        };
        removePageAction.setText("Remove Page");
        removePageAction.setToolTipText("Remove Page");
        removePageAction.setImageDescriptor(GuiUtils
            .loadIconImage(XWikiConstants.REMOVE_PAGE_ICON));
        removePageAction.setEnabled(false);

        /**
         * Add space action.
         */
        addSpaceAction = new Action()
        {
            public void run()
            {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                IXWikiConnection connection = null;
                if (obj instanceof IXWikiConnection) {
                    connection = new XWikiConnectionWrapper((IXWikiConnection) obj);
                } else if (obj instanceof IXWikiSpace) {
                    connection = new XWikiConnectionWrapper(((IXWikiSpace) obj).getConnection());
                }
                if (connection != null) {
                    openAddSpaceWizard(connection);
                }
            }
        };
        addSpaceAction.setText("New Space");
        addSpaceAction.setToolTipText("New Space");
        addSpaceAction.setImageDescriptor(GuiUtils.loadIconImage(XWikiConstants.ADD_SPACE_ICON));
        addSpaceAction.setEnabled(false);

        /**
         * Remove space action.
         */
        removeSpaceAction = new Action()
        {
            public void run()
            {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                if (obj instanceof IXWikiSpace) {
                    IXWikiSpace space = (IXWikiSpace) obj;
                    IXWikiConnection connection =
                        new XWikiConnectionWrapper(space.getConnection());
                    try {
                        connection.removeSpace(space.getKey());
                    } catch (SwizzleConfluenceException e) {
                        // Will be logged elsewhere.
                    }
                    viewer.refresh();
                }
            }
        };
        removeSpaceAction.setText("Remove Space");
        removeSpaceAction.setToolTipText("Remove Space");
        removeSpaceAction.setImageDescriptor(GuiUtils
            .loadIconImage(XWikiConstants.REMOVE_SPACE_ICON));
        removeSpaceAction.setEnabled(false);

        /**
         * Edit action.
         */
        editAction = new Action()
        {
            public void run()
            {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                if (obj instanceof IXWikiPage) {
                    IXWikiPage page = (IXWikiPage) obj;
                    openEditor((IEditorInput) page);
                }
            }
        };

        /**
         * Viewer selection listener
         */
        viewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {
                Object selection =
                    ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (selection instanceof IXWikiConnection) {
                    removeConnectionAction.setEnabled(true);
                } else {
                    removeConnectionAction.setEnabled(false);
                }
                if (selection instanceof IXWikiConnection || selection instanceof IXWikiSpace) {
                    addSpaceAction.setEnabled(true);
                } else {
                    addSpaceAction.setEnabled(false);
                }
                if (selection instanceof IXWikiSpace) {
                    removeSpaceAction.setEnabled(true);
                } else {
                    removeSpaceAction.setEnabled(false);
                }
                if (selection instanceof IXWikiSpace || selection instanceof IXWikiPage) {
                    addPageAction.setEnabled(true);
                } else {
                    addPageAction.setEnabled(false);
                }
                if (selection instanceof IXWikiPage) {
                    removePageAction.setEnabled(true);
                } else {
                    removePageAction.setEnabled(false);
                }
            }
        });
    }

    /**
     * Eclipse auto-generated code. hack these if you need to add items to different places in
     * workbench.
     */
    private void hookDoubleClickAction()
    {
        viewer.addDoubleClickListener(new IDoubleClickListener()
        {
            public void doubleClick(DoubleClickEvent event)
            {
                editAction.run();
            }
        });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus()
    {
        viewer.getControl().setFocus();
    }

    /**
     * Opens up the new connection wizard.
     */
    private void openConnectWizard()
    {
        ConnectWizard connectWizard = new ConnectWizard();
        WizardDialog wizardDialog =
            new XWikiWizardDialog(Display.getCurrent().getActiveShell(), connectWizard);
        wizardDialog.setTitle("Connect...");
        wizardDialog.setBlockOnOpen(true);
        wizardDialog.open();
        viewer.refresh();
    }

    /**
     * Opens up the add new space wizard.
     * 
     * @param connection Connection into which the space should be added.
     */
    private void openAddSpaceWizard(IXWikiConnection connection)
    {
        NewSpaceWizard newSpaceWizard = new NewSpaceWizard(connection);
        WizardDialog wizardDialog =
            new XWikiWizardDialog(Display.getCurrent().getActiveShell(), newSpaceWizard);
        wizardDialog.setTitle("New Space...");
        wizardDialog.setBlockOnOpen(true);
        wizardDialog.open();
        viewer.refresh();
    }

    /**
     * Opens up the add new page wizard.
     * 
     * @param space Space into which the page should be added.
     */
    private void openAddNewPageWizard(IXWikiSpace space)
    {
        NewPageWizard newPageWizard = new NewPageWizard(space);
        WizardDialog wizardDialog =
            new XWikiWizardDialog(Display.getCurrent().getActiveShell(), newPageWizard);
        wizardDialog.setTitle("New Page...");
        wizardDialog.setBlockOnOpen(true);
        wizardDialog.open();
        viewer.refresh();
    }

    /**
     * Opens up the document for editing.
     * 
     * @param editorInput The XWikiPage to be edited.
     */
    private void openEditor(IEditorInput editorInput)
    {
        IWorkbenchPage page =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (page != null) {
            try {
                page.openEditor(editorInput, "org.xwiki.plugins.eclipse.editors.XWikiEditor");
            } catch (PartInitException e) {
                GuiUtils.reportError(true, "Error", e.getMessage());
            }
        } else {
            GuiUtils.reportError(true, "Error", "Internal Error : Could not create page.");
        }
    }
}
