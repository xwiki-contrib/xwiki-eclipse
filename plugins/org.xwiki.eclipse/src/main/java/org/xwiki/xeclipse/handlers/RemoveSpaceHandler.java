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
package org.xwiki.xeclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.xeclipse.model.IXWikiSpace;
import org.xwiki.xeclipse.model.XWikiConnectionException;
import org.xwiki.xeclipse.utils.XWikiEclipseUtil;

public class RemoveSpaceHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);

        Object selectedObject =
            XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(selection);

        if (selectedObject instanceof IXWikiSpace) {
            IXWikiSpace xwikiSpace = (IXWikiSpace) selectedObject;

            try {
                MessageBox messageBox =
                    new MessageBox(HandlerUtil.getActiveShell(event), SWT.YES | SWT.NO
                        | SWT.ICON_QUESTION);
                messageBox
                    .setMessage(String.format(
                        "Do you really want to delete '%s' and all its pages?", xwikiSpace
                            .getName()));
                int result = messageBox.open();
                if (result == SWT.YES) {
                    xwikiSpace.remove();
                }

            } catch (XWikiConnectionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return null;
    }
}
