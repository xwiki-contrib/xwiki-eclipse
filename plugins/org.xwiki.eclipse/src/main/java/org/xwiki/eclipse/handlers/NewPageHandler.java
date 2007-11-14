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
package org.xwiki.eclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.eclipse.model.IXWikiSpace;
import org.xwiki.eclipse.utils.XWikiEclipseUtil;
import org.xwiki.eclipse.wizards.NewPageWizard;

public class NewPageHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);

        Object selectedObject =
            XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(selection);

        if (selectedObject instanceof IXWikiSpace) {
            IXWikiSpace xwikiSpace = (IXWikiSpace) selectedObject;

            NewPageWizard wizard =
                new NewPageWizard(xwikiSpace, HandlerUtil.getActiveWorkbenchWindow(event)
                    .getActivePage());

            WizardDialog dialog =
                new WizardDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), wizard);
            dialog.create();
            dialog.open();
        }

        return null;
    }
}
