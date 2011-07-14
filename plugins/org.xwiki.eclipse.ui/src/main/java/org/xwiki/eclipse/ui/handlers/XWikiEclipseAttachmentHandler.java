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
 */
package org.xwiki.eclipse.ui.handlers;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.utils.UIUtils;
import org.xwiki.eclipse.ui.wizards.XWikiEclipseAttachmentWizard;

/**
 * @version $Id$
 */
public class XWikiEclipseAttachmentHandler extends AbstractHandler
{

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        Command command = event.getCommand();

        Set selectedObjects = UIUtils.getSelectedObjectsFromSelection(selection);
        /* get the shell */
        Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();

        if (command.getId().equals(UIConstants.UPLOAD_ATTACHMENT_COMMAND) && selectedObjects.size() > 1) {
            UIUtils.showMessageDialog(shell, SWT.ICON_ERROR, "Please only select one page to upload attachment",
                "Please only select one page to upload attachment");
            return null;
        }

        if (command.getId().equals(UIConstants.DOWNLOAD_ATTACHMENT_COMMAND)
            || command.getId().equals(UIConstants.UPDATE_ATTACHMENT_COMMAND)
            || command.getId().equals(UIConstants.UPLOAD_ATTACHMENT_COMMAND)) {
            XWikiEclipseAttachmentWizard wizard = new XWikiEclipseAttachmentWizard(selectedObjects, command);
            WizardDialog wizardDialog = new WizardDialog(shell, wizard);
            wizardDialog.create();
            wizardDialog.open();
        }

        return null;
    }
}
