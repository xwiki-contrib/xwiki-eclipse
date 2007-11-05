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
package org.xwiki.xeclipse.views;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionInfo;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.xwiki.xeclipse.IXWikiEclipseEventListener;
import org.xwiki.xeclipse.XWikiConnectionManager;
import org.xwiki.xeclipse.XWikiEclipseConstants;
import org.xwiki.xeclipse.XWikiEclipseEvent;
import org.xwiki.xeclipse.XWikiEclipseNotificationCenter;
import org.xwiki.xeclipse.editors.XWikiPageEditor;
import org.xwiki.xeclipse.editors.XWikiPageEditorInput;
import org.xwiki.xeclipse.handlers.ConnectHandler;
import org.xwiki.xeclipse.handlers.DisconnectHandler;
import org.xwiki.xeclipse.handlers.NewPageHandler;
import org.xwiki.xeclipse.handlers.NewSpaceHandler;
import org.xwiki.xeclipse.handlers.RemoveConnectionHandler;
import org.xwiki.xeclipse.handlers.RemovePageHandler;
import org.xwiki.xeclipse.handlers.RemoveSpaceHandler;
import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.IXWikiPage;
import org.xwiki.xeclipse.model.IXWikiSpace;
import org.xwiki.xeclipse.utils.XWikiEclipseUtil;

public class XWikiExplorerView extends ViewPart implements IXWikiEclipseEventListener
{
    public static final String ID = "org.xwiki.xeclipse.views.XWikiExplorer";

    private TreeViewer treeViewer;

    @Override
    public void createPartControl(Composite parent)
    {
        treeViewer = new TreeViewer(parent, SWT.NONE);
        treeViewer.setComparator(new ViewerComparator());
        treeViewer.setContentProvider(new XWikiExplorerContentProvider(treeViewer));
        treeViewer.setLabelProvider(new WorkbenchLabelProvider());
        getSite().setSelectionProvider(treeViewer);
        treeViewer.setInput(XWikiConnectionManager.getDefault());

        treeViewer.addDoubleClickListener(new IDoubleClickListener()
        {

            public void doubleClick(DoubleClickEvent event)
            {
                IWorkbenchPage page =
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                Object selectedObject =
                    XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(treeViewer
                        .getSelection());
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
            }
        });

        hookContextMenu();
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
            XWikiEclipseConstants.REMOVE_CONNECTION_COMMAND,
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
            XWikiEclipseConstants.REMOVE_SPACE_COMMAND,
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
            XWikiEclipseConstants.REMOVE_PAGE_COMMAND,
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

        Menu menu = menuManager.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);
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

        handlerService.activateHandler(XWikiEclipseConstants.REMOVE_CONNECTION_COMMAND,
            new RemoveConnectionHandler(), new Expression()
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

        handlerService.activateHandler(XWikiEclipseConstants.REMOVE_SPACE_COMMAND,
            new RemoveSpaceHandler(), new Expression()
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
                        IXWikiSpace xwikiPage = (IXWikiSpace) selectedObject;
                        return xwikiPage.getConnection().isConnected() ? EvaluationResult.TRUE
                            : EvaluationResult.FALSE;
                    }

                    return EvaluationResult.FALSE;
                }
            });

        handlerService.activateHandler(XWikiEclipseConstants.REMOVE_PAGE_COMMAND,
            new RemovePageHandler(), new Expression()
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

                    if (selectedObject instanceof IXWikiPage) {
                        IXWikiPage xwikiPage = (IXWikiPage) selectedObject;
                        return xwikiPage.getConnection().isConnected() ? EvaluationResult.TRUE
                            : EvaluationResult.FALSE;
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
                        break;

                }

            }
        });

    }

}
