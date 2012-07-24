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
package org.xwiki.eclipse.ui.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.xwiki.eclipse.ui.UIConstants;

/**
 * @version $Id$
 */
public class DataManagerActionProvider
{
    private CommandContributionItem connect;

    private CommandContributionItem disconnect;

    private CommandContributionItem newPage;

    public DataManagerActionProvider()
    {
        CommandContributionItemParameter ccip =
            new CommandContributionItemParameter(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                UIConstants.DATA_MANAGER_CONNECT_COMMAND, UIConstants.DATA_MANAGER_CONNECT_COMMAND, SWT.NONE);
        connect = new CommandContributionItem(ccip);

        ccip =
            new CommandContributionItemParameter(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                UIConstants.DATA_MANAGER_DISCONNECT_COMMAND, UIConstants.DATA_MANAGER_DISCONNECT_COMMAND, SWT.NONE);
        disconnect = new CommandContributionItem(ccip);

        ccip =
            new CommandContributionItemParameter(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                UIConstants.NEW_PAGE_COMMAND, UIConstants.NEW_PAGE_COMMAND, SWT.NONE);
        newPage = new CommandContributionItem(ccip);
    }

    public void fillContextMenu(IMenuManager menu)
    {
        menu.add(connect);
        menu.add(disconnect);
        menu.add(new Separator());
        menu.add(newPage);
    }
}
