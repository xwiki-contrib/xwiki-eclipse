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
package org.xwiki.eclipse.ui.handlers;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.model.ModelObject;
import org.xwiki.eclipse.ui.dialogs.OpenPageDialog;
import org.xwiki.eclipse.ui.utils.UIUtils;

public class OpenPageHandler extends AbstractHandler
{
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        DataManager dataManager = null;

        Set selectedObjects = UIUtils.getSelectedObjectsFromSelection(selection);
        if (selectedObjects.size() == 1) {
            Object selectedObject = selectedObjects.iterator().next();

            if (selectedObject instanceof DataManager) {
                dataManager = (DataManager) selectedObject;
            } else if (selectedObject instanceof ModelObject) {
                ModelObject modelObject = (ModelObject) selectedObject;

                dataManager = modelObject.getDataManager();
            }
        }

        OpenPageDialog dialog = new OpenPageDialog(HandlerUtil.getActiveShell(event), dataManager);
        dialog.open();

        return null;
    }
}
