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

import java.util.Set;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.xwiki.eclipse.core.model.XWikiEclipseObject;
import org.xwiki.eclipse.core.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.core.model.XWikiEclipsePage;
import org.xwiki.eclipse.core.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.ui.editors.ObjectEditor;
import org.xwiki.eclipse.ui.editors.ObjectEditorInput;
import org.xwiki.eclipse.ui.editors.PageEditor;
import org.xwiki.eclipse.ui.editors.PageEditorInput;
import org.xwiki.eclipse.ui.utils.UIUtils;
import org.xwiki.eclipse.ui.utils.XWikiEclipseSafeRunnable;

/*
 * This is defined as a standard action and not with the command framework because the common
 * navigator does not export a command with the ICommonActionConstants.OPEN id. So in order to make
 * double click work we need to do things in this way.
 */
public class OpenXWikiModelObjectAction extends Action
{
    private ISelectionProvider selectionProvider;

    public OpenXWikiModelObjectAction(ISelectionProvider selectionProvider)
    {
        super("Open...");
        this.selectionProvider = selectionProvider;
    }

    @Override
    public void run()
    {
        Set selectedObjects = UIUtils.getSelectedObjectsFromSelection(selectionProvider.getSelection());
        for (Object object : selectedObjects) {
            if (object instanceof XWikiEclipsePageSummary) {
                final XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) object;

                SafeRunner.run(new XWikiEclipseSafeRunnable()
                {
                    public void run() throws Exception
                    {
                        XWikiEclipsePage page = pageSummary.getDataManager().getPage(pageSummary.getData().getId());

                        if (page == null) {
                            MessageBox mb = new MessageBox(Display.getDefault().getActiveShell());
                            mb.setText("Page not avaliable");
                            mb
                                .setMessage("The page is not currently available. This might happen if the page has been removed remotely or if the page is not locally available.");
                            mb.open();
                            return;
                        }

                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                            new PageEditorInput(page), PageEditor.ID);
                    }
                });
            }

            if (object instanceof XWikiEclipseObjectSummary) {
                final XWikiEclipseObjectSummary objectSummary = (XWikiEclipseObjectSummary) object;

                SafeRunner.run(new XWikiEclipseSafeRunnable()
                {
                    public void run() throws Exception
                    {
                        XWikiEclipseObject xwikiObject =
                            objectSummary.getDataManager().getObject(objectSummary.getData().getPageId(),
                                objectSummary.getData().getClassName(), objectSummary.getData().getId());

                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                            new ObjectEditorInput(xwikiObject), ObjectEditor.ID);
                    }
                });

            }
        }
    }
}
