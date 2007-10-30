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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.xwiki.xeclipse.IXWikiConnectionManagerListener;
import org.xwiki.xeclipse.XWikiConnectionManager;
import org.xwiki.xeclipse.XWikiEclipseConstants;
import org.xwiki.xeclipse.handlers.ConnectHandler;
import org.xwiki.xeclipse.handlers.DisconnectHandler;
import org.xwiki.xeclipse.handlers.RemoveConnectionHandler;
import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.IXWikiConnectionListener;
import org.xwiki.xeclipse.utils.XWikiEclipseUtil;

public class XWikiExplorerView extends ViewPart implements IXWikiConnectionManagerListener,
    IXWikiConnectionListener
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
        XWikiConnectionManager.getDefault().addConnectionManagerListener(this);

        /* Monitors the connections that are already registered in the connection manager */
        for (IXWikiConnection xwikiConnection : XWikiConnectionManager.getDefault()
            .getConnections()) {
            xwikiConnection.addConnectionEstablishedListener(this);
        }
    }

    @Override
    public void dispose()
    {
        /*
         * Removes the connection listening on all connections still registered in the connection
         * manager
         */
        for (IXWikiConnection xwikiConnection : XWikiConnectionManager.getDefault()
            .getConnections()) {
            xwikiConnection.removeConnectionEstablishedListener(this);
        }

        XWikiConnectionManager.getDefault().removeConnectionManagerListener(this);

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
    }

    public void connectionAdded(IXWikiConnection xwikiConnection)
    {
        xwikiConnection.addConnectionEstablishedListener(this);
        treeViewer.refresh();
    }

    public void connectionRemoved(IXWikiConnection xwikiConnection)
    {
        xwikiConnection.removeConnectionEstablishedListener(this);
        treeViewer.refresh();
    }

    public void connectionEstablished(IXWikiConnection connection)
    {
        treeViewer.refresh(connection);
    }

    public void connectionClosed(IXWikiConnection connection)
    {
        treeViewer.refresh(connection);
    }

}
