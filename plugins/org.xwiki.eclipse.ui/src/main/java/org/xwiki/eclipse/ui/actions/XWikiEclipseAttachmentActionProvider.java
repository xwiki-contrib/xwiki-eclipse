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
public class XWikiEclipseAttachmentActionProvider extends CommonActionProvider
{
    private CommandContributionItem download;

    private CommandContributionItem update;

    @Override
    public void init(ICommonActionExtensionSite aSite)
    {
        super.init(aSite);
        CommandContributionItemParameter ccip =
            new CommandContributionItemParameter(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                UIConstants.DOWNLOAD_ATTACHMENT_COMMAND, UIConstants.DOWNLOAD_ATTACHMENT_COMMAND, SWT.NONE);

        download = new CommandContributionItem(ccip);

        ccip =
            new CommandContributionItemParameter(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                UIConstants.UPDATE_ATTACHMENT_COMMAND, UIConstants.UPDATE_ATTACHMENT_COMMAND, SWT.NONE);

        update = new CommandContributionItem(ccip);

    }

    @Override
    public void fillContextMenu(IMenuManager menu)
    {
        menu.appendToGroup(ICommonMenuConstants.GROUP_NEW, download);
        menu.appendToGroup(ICommonMenuConstants.GROUP_NEW, update);
        super.fillContextMenu(menu);
    }
}
