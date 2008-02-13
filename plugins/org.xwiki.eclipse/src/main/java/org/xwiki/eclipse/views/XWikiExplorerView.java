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
package org.xwiki.eclipse.views;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionInfo;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.xwiki.eclipse.IXWikiEclipseEventListener;
import org.xwiki.eclipse.WorkingSet;
import org.xwiki.eclipse.WorkingSetFilter;
import org.xwiki.eclipse.WorkingSetManager;
import org.xwiki.eclipse.XWikiConnectionManager;
import org.xwiki.eclipse.XWikiEclipseConstants;
import org.xwiki.eclipse.XWikiEclipseEvent;
import org.xwiki.eclipse.XWikiEclipseNotificationCenter;
import org.xwiki.eclipse.dialogs.ManageWorkingSetsDialog;
import org.xwiki.eclipse.editors.XWikiPageEditor;
import org.xwiki.eclipse.editors.XWikiPageEditorInput;
import org.xwiki.eclipse.handlers.ConnectHandler;
import org.xwiki.eclipse.handlers.DisconnectHandler;
import org.xwiki.eclipse.handlers.GrabSpaceHandler;
import org.xwiki.eclipse.handlers.NewPageHandler;
import org.xwiki.eclipse.handlers.NewSpaceHandler;
import org.xwiki.eclipse.handlers.RemoveConnectionHandler;
import org.xwiki.eclipse.handlers.RemovePageHandler;
import org.xwiki.eclipse.handlers.RemoveSpaceHandler;
import org.xwiki.eclipse.model.IXWikiConnection;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.model.IXWikiSpace;
import org.xwiki.eclipse.utils.XWikiEclipseUtil;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;

public class XWikiExplorerView extends ViewPart implements IXWikiEclipseEventListener,
    ISelectionChangedListener
{
    public static final String ID = "org.xwiki.eclipse.views.XWikiExplorer";

    private TreeViewer treeViewer;

    private IHandlerActivation deleteCommandActivation;

    // private WorkingSet currentWorkingSet;

    private Form form;

    private class RefreshHandler extends AbstractHandler
    {
        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException
        {
            ISelection selection = HandlerUtil.getCurrentSelection(event);

            Object selectedObject =
                XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(selection);

            if (selectedObject instanceof IXWikiConnection) {
                treeViewer.refresh(selectedObject);
            } else if (selectedObject instanceof IXWikiSpace) {
                treeViewer.refresh(selectedObject);
            }

            return null;
        }
    }

    private class SelectWorkingSetAction extends Action
    {
        private WorkingSet workingSet;

        private TreeViewer treeViewer;

        public SelectWorkingSetAction(WorkingSet workingSet, TreeViewer treeViewer)
        {
            super(null, Action.AS_CHECK_BOX);
            setText(workingSet != null ? workingSet.getName() : "No working set");
            setChecked(workingSet == WorkingSetManager.getDefault().getActiveWorkingSet());
            this.workingSet = workingSet;
            this.treeViewer = treeViewer;

            if (workingSet != null) {
                setImageDescriptor(XWikiEclipsePlugin
                    .getImageDescriptor(XWikiEclipseConstants.WORKING_SET_ICON));
            }

        }

        @Override
        public void run()
        {
            WorkingSetManager.getDefault().setActiveWorkingSet(workingSet);
            treeViewer.resetFilters();
            if (workingSet != null) {
                form.setText(workingSet.getName());
                form.setMessage("(Working set visualization)");
                treeViewer.addFilter(new WorkingSetFilter(workingSet));
            } else {
                form.setText(null);
                form.setMessage(null);
            }
        }

    }

    @Override
    public void createPartControl(Composite parent)
    {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createForm(parent);
        toolkit.decorateFormHeading(form);
        GridLayoutFactory.fillDefaults().applyTo(form.getBody());

        treeViewer = new TreeViewer(form.getBody(), SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(
            treeViewer.getControl());
        treeViewer.setComparator(new ViewerComparator());
        treeViewer.setContentProvider(new XWikiExplorerContentProvider(treeViewer));
        treeViewer.setLabelProvider(new WorkbenchLabelProvider());
        getSite().setSelectionProvider(treeViewer);
        treeViewer.setInput(XWikiConnectionManager.getDefault());
        treeViewer.addSelectionChangedListener(this);

        treeViewer.addDoubleClickListener(new IDoubleClickListener()
        {

            public void doubleClick(DoubleClickEvent event)
            {            	            	
                IWorkbenchPage page =
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                Object selectedObject =
                    XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(treeViewer
                        .getSelection());
                
                if(selectedObject != null) {
                	treeViewer.expandToLevel(selectedObject, 1);
                }
                
                if (selectedObject instanceof IXWikiPage) {
                    IXWikiPage xwikiPage = (IXWikiPage) selectedObject;
                    XWikiPageEditorInput editorInput = new XWikiPageEditorInput(xwikiPage);
                    try {
                        page.openEditor(editorInput, XWikiPageEditor.ID);

                        /*
                         * This updates the icon in order to reflect its new state after that it has
                         * been opened in the editor, i.e., cached, conflict, etc.
                         */
                        treeViewer.refresh(xwikiPage);
                    } catch (PartInitException e) {
                        e.printStackTrace();
                    }
                }
                
                if(selectedObject instanceof IXWikiConnection) {  
                	IXWikiConnection xwikiConnection = (IXWikiConnection) selectedObject;                	                
                	
                	if(xwikiConnection.isConnected()) {
                		return;
                	}
                	
                	IHandlerService handlerService = (IHandlerService) getSite()
    				.getService(IHandlerService.class);
                	
                	try
					{
						handlerService.executeCommand(XWikiEclipseConstants.CONNECT_COMMAND, null);
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
                }
            }
        });

        contributeToActionBars();
        hookContextMenu();
    }

    private void contributeToActionBars()
    {
        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager viewMenuManager = actionBars.getMenuManager();
        MenuManager workingSetMenuManager = new MenuManager("Working sets", "workingSet");
        workingSetMenuManager.add(new Action()
        {
        });
        workingSetMenuManager.setRemoveAllWhenShown(true);
        workingSetMenuManager.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                manager.add(new SelectWorkingSetAction(null, treeViewer));

                for (WorkingSet workingSet : WorkingSetManager.getDefault().getWorkingSets()) {
                    manager.add(new SelectWorkingSetAction(workingSet, treeViewer));
                }

                manager.add(new Separator());

                manager.add(new Action("Manage working sets...")
                {
                    @Override
                    public void run()
                    {
                        ManageWorkingSetsDialog dialog =
                            new ManageWorkingSetsDialog(getSite().getShell());
                        dialog.open();

                        /*
                         * If the current working set has been deleted then set the current working
                         * set to null
                         */
                        if (!WorkingSetManager.getDefault().getWorkingSets().contains(
                            WorkingSetManager.getDefault().getActiveWorkingSet())) {
                            WorkingSetManager.getDefault().setActiveWorkingSet(null);
                            treeViewer.resetFilters();
                        }
                    }

                });

            }
        });

        viewMenuManager.add(workingSetMenuManager);

    }

    private void hookContextMenu()
    {
        MenuManager menuManager = new MenuManager("#Popup");

        menuManager.add(new CommandContributionItem(getSite(),
            null,
            XWikiEclipseConstants.CONNECT_COMMAND,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SWT.NONE));

        menuManager.add(new CommandContributionItem(getSite(),
            null,
            XWikiEclipseConstants.DISCONNECT_COMMAND,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SWT.NONE));

        menuManager.add(new Separator());

        menuManager.add(new CommandContributionItem(getSite(),
            null,
            XWikiEclipseConstants.NEW_SPACE_COMMAND,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SWT.NONE));

        menuManager.add(new CommandContributionItem(getSite(),
            null,
            XWikiEclipseConstants.NEW_PAGE_COMMAND,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SWT.NONE));

        menuManager.add(new Separator());

        menuManager.add(new CommandContributionItem(getSite(),
            null,
            XWikiEclipseConstants.GRAB_SPACE_COMMAND,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SWT.NONE));

        menuManager.add(new Separator());

        menuManager.add(new CommandContributionItem(getSite(),
            null,
            "org.eclipse.ui.edit.delete",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SWT.NONE));

        menuManager.add(new Separator());

        menuManager.add(new CommandContributionItem(getSite(),
            null,
            XWikiEclipseConstants.REFRESH_COMMAND,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SWT.NONE));

        menuManager.add(new Separator());

        menuManager.add(new CommandContributionItem(getSite(),
            null,
            XWikiEclipseConstants.NEW_CONNECTION_COMMAND,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SWT.NONE));
        
        menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        Menu menu = menuManager.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);
        
        getSite().registerContextMenu("#XWikiExplorerPopup", menuManager, treeViewer);        
    }

    @Override
    public void setFocus()
    {
        treeViewer.getControl().setFocus();
    }

    @Override
    public void init(IViewSite site) throws PartInitException
    {
        super.init(site);
        activateHandlers(site);

        XWikiEclipseNotificationCenter.getDefault().addListener(
            XWikiEclipseEvent.CONNECTION_ADDED, this);
        XWikiEclipseNotificationCenter.getDefault().addListener(
            XWikiEclipseEvent.CONNECTION_REMOVED, this);
        XWikiEclipseNotificationCenter.getDefault().addListener(
            XWikiEclipseEvent.CONNECTION_ESTABLISHED, this);
        XWikiEclipseNotificationCenter.getDefault().addListener(
            XWikiEclipseEvent.CONNECTION_CLOSED, this);
        XWikiEclipseNotificationCenter.getDefault().addListener(XWikiEclipseEvent.SPACE_CREATED,
            this);
        XWikiEclipseNotificationCenter.getDefault().addListener(XWikiEclipseEvent.PAGE_CREATED,
            this);
        XWikiEclipseNotificationCenter.getDefault().addListener(XWikiEclipseEvent.SPACE_REMOVED,
            this);
        XWikiEclipseNotificationCenter.getDefault().addListener(XWikiEclipseEvent.PAGE_REMOVED,
            this);
        XWikiEclipseNotificationCenter.getDefault().addListener(XWikiEclipseEvent.PAGE_UPDATED,
            this);
        XWikiEclipseNotificationCenter.getDefault().addListener(XWikiEclipseEvent.PAGES_GRABBED,
            this);
    }

    @Override
    public void dispose()
    {
        XWikiEclipseNotificationCenter.getDefault().removeListener(
            XWikiEclipseEvent.CONNECTION_ADDED, this);
        XWikiEclipseNotificationCenter.getDefault().removeListener(
            XWikiEclipseEvent.CONNECTION_REMOVED, this);
        XWikiEclipseNotificationCenter.getDefault().removeListener(
            XWikiEclipseEvent.CONNECTION_ESTABLISHED, this);
        XWikiEclipseNotificationCenter.getDefault().removeListener(
            XWikiEclipseEvent.CONNECTION_CLOSED, this);
        XWikiEclipseNotificationCenter.getDefault().removeListener(
            XWikiEclipseEvent.SPACE_CREATED, this);
        XWikiEclipseNotificationCenter.getDefault().removeListener(
            XWikiEclipseEvent.PAGE_CREATED, this);
        XWikiEclipseNotificationCenter.getDefault().removeListener(
            XWikiEclipseEvent.SPACE_REMOVED, this);
        XWikiEclipseNotificationCenter.getDefault().removeListener(
            XWikiEclipseEvent.PAGE_REMOVED, this);
        XWikiEclipseNotificationCenter.getDefault().removeListener(
            XWikiEclipseEvent.PAGE_UPDATED, this);
        XWikiEclipseNotificationCenter.getDefault().removeListener(
            XWikiEclipseEvent.PAGES_GRABBED, this);

        super.dispose();
    }

    public void activateHandlers(IViewSite site)
    {
        IHandlerService handlerService = (IHandlerService) site.getService(IHandlerService.class);

        handlerService.activateHandler(XWikiEclipseConstants.CONNECT_COMMAND,
            new ConnectHandler(), new Expression()
            {
                @Override
                public void collectExpressionInfo(ExpressionInfo info)
                {
                    info.addVariableNameAccess(ISources.ACTIVE_CURRENT_SELECTION_NAME);
                }

                @Override
                public EvaluationResult evaluate(IEvaluationContext context) throws CoreException
                {
                    Object selection =
                        context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
                    Object selectedObject =
                        XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(selection);

                    if (selectedObject instanceof IXWikiConnection) {
                        IXWikiConnection xwikiConnection = (IXWikiConnection) selectedObject;

                        if (!xwikiConnection.isConnected()) {
                            return EvaluationResult.TRUE;
                        }
                    }

                    return EvaluationResult.FALSE;
                }
            });

        handlerService.activateHandler(XWikiEclipseConstants.DISCONNECT_COMMAND,
            new DisconnectHandler(), new Expression()
            {
                @Override
                public void collectExpressionInfo(ExpressionInfo info)
                {
                    info.addVariableNameAccess(ISources.ACTIVE_CURRENT_SELECTION_NAME);
                }

                @Override
                public EvaluationResult evaluate(IEvaluationContext context) throws CoreException
                {
                    Object selection =
                        context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
                    Object selectedObject =
                        XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(selection);

                    if (selectedObject instanceof IXWikiConnection) {
                        IXWikiConnection xwikiConnection = (IXWikiConnection) selectedObject;

                        if (xwikiConnection.isConnected()) {
                            return EvaluationResult.TRUE;
                        }
                    }

                    return EvaluationResult.FALSE;
                }
            });

        handlerService.activateHandler(XWikiEclipseConstants.GRAB_SPACE_COMMAND,
            new GrabSpaceHandler(), new Expression()
            {
                @Override
                public void collectExpressionInfo(ExpressionInfo info)
                {
                    info.addVariableNameAccess(ISources.ACTIVE_CURRENT_SELECTION_NAME);
                }

                @Override
                public EvaluationResult evaluate(IEvaluationContext context) throws CoreException
                {
                    Object selection =
                        context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
                    Object selectedObject =
                        XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(selection);

                    if (selectedObject instanceof IXWikiSpace) {
                        IXWikiSpace xwikiSpace = (IXWikiSpace) selectedObject;
                        return xwikiSpace.getConnection().isConnected() ? EvaluationResult.TRUE
                            : EvaluationResult.FALSE;
                    }

                    return EvaluationResult.FALSE;
                }
            });

        handlerService.activateHandler(XWikiEclipseConstants.NEW_SPACE_COMMAND,
            new NewSpaceHandler(), new Expression()
            {
                @Override
                public void collectExpressionInfo(ExpressionInfo info)
                {
                    info.addVariableNameAccess(ISources.ACTIVE_CURRENT_SELECTION_NAME);
                }

                @Override
                public EvaluationResult evaluate(IEvaluationContext context) throws CoreException
                {
                    Object selection =
                        context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
                    Object selectedObject =
                        XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(selection);

                    if (selectedObject instanceof IXWikiConnection) {
                        IXWikiConnection xwikiConnection = (IXWikiConnection) selectedObject;
                        return xwikiConnection.isConnected() ? EvaluationResult.TRUE
                            : EvaluationResult.FALSE;
                    }

                    return EvaluationResult.FALSE;
                }
            });

        handlerService.activateHandler(XWikiEclipseConstants.NEW_PAGE_COMMAND,
            new NewPageHandler(), new Expression()
            {
                @Override
                public void collectExpressionInfo(ExpressionInfo info)
                {
                    info.addVariableNameAccess(ISources.ACTIVE_CURRENT_SELECTION_NAME);
                }

                @Override
                public EvaluationResult evaluate(IEvaluationContext context) throws CoreException
                {
                    Object selection =
                        context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
                    Object selectedObject =
                        XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(selection);

                    if (selectedObject instanceof IXWikiSpace) {
                        IXWikiSpace xwikiSpace = (IXWikiSpace) selectedObject;
                        return xwikiSpace.getConnection().isConnected() ? EvaluationResult.TRUE
                            : EvaluationResult.FALSE;
                    }

                    return EvaluationResult.FALSE;
                }
            });

        handlerService.activateHandler(XWikiEclipseConstants.REFRESH_COMMAND,
            new RefreshHandler(), new Expression()
            {
                @Override
                public void collectExpressionInfo(ExpressionInfo info)
                {
                    info.addVariableNameAccess(ISources.ACTIVE_CURRENT_SELECTION_NAME);
                }

                @Override
                public EvaluationResult evaluate(IEvaluationContext context) throws CoreException
                {
                    Object selection =
                        context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);

                    Object selectedObject =
                        XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(selection);

                    if (selectedObject instanceof IXWikiConnection) {
                        return EvaluationResult.TRUE;
                    }

                    if (selectedObject instanceof IXWikiSpace) {
                        return EvaluationResult.TRUE;
                    }

                    return EvaluationResult.FALSE;
                }
            });

    }

    public void handleEvent(final Object sender, final XWikiEclipseEvent event, final Object data)
    {
        /*
         * Things that updates the UI must be run asynchronously otherwise they can conflict with
         * other UI updates
         */
        Display.getDefault().asyncExec(new Runnable()
        {
            @SuppressWarnings("unchecked")
            public void run()
            {
                IXWikiSpace space;
                IXWikiPage page;

                switch (event) {
                    case CONNECTION_ADDED:
                    case CONNECTION_REMOVED:
                        treeViewer.refresh();
                        break;
                    case CONNECTION_ESTABLISHED:
                    case CONNECTION_CLOSED:
                    case SPACE_CREATED:
                    case SPACE_REMOVED:
                        if (XWikiConnectionManager.getDefault().getConnections().contains(data)) {
                            treeViewer.refresh(data);
                        }
                        break;
                    case PAGE_CREATED:
                        space = (IXWikiSpace) ((Object[]) data)[0];
                        page = (IXWikiPage) ((Object[]) data)[1];
                        treeViewer.add(space, page);
                        break;
                    case PAGE_REMOVED:
                        space = (IXWikiSpace) ((Object[]) data)[0];
                        page = (IXWikiPage) ((Object[]) data)[1];
                        treeViewer.remove(page);
                        break;
                    case PAGE_UPDATED:
                        treeViewer.refresh(data);
                        treeViewer.setSelection(new StructuredSelection(data));
                        break;
                    case PAGES_GRABBED:
                        Collection<IXWikiPage> xwikiPages = (Collection<IXWikiPage>) data;
                        for (IXWikiPage xwikiPage : xwikiPages) {
                            treeViewer.update(xwikiPage, null);
                        }
                        break;
                }

            }
        });

    }

    public void selectionChanged(SelectionChangedEvent event)
    {
        IHandlerService handlerService =
            (IHandlerService) getSite().getService(IHandlerService.class);

        if (deleteCommandActivation != null) {
            handlerService.deactivateHandler(deleteCommandActivation);
        }

        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        if (selection.size() == 1) {
            Object selectedObject = selection.getFirstElement();
            if (selectedObject instanceof IXWikiConnection) {
                deleteCommandActivation =
                    handlerService.activateHandler("org.eclipse.ui.edit.delete",
                        new RemoveConnectionHandler());
            } else if (selectedObject instanceof IXWikiSpace) {
                IXWikiSpace xwikiSpace = (IXWikiSpace) selectedObject;
                if (xwikiSpace.getConnection().isConnected()) {
                    deleteCommandActivation =
                        handlerService.activateHandler("org.eclipse.ui.edit.delete",
                            new RemoveSpaceHandler());
                }
            } else if (selectedObject instanceof IXWikiPage) {
                IXWikiPage xwikiPage = (IXWikiPage) selectedObject;
                if (xwikiPage.getConnection().isConnected()) {
                    deleteCommandActivation =
                        handlerService.activateHandler("org.eclipse.ui.edit.delete",
                            new RemovePageHandler());
                }
            }
        }

    }

}
