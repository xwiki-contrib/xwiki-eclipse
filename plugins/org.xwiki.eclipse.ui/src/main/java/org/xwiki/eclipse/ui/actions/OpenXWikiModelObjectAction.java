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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.xwiki.eclipse.core.CoreLog;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;
import org.xwiki.eclipse.ui.editors.CommentEditor;
import org.xwiki.eclipse.ui.editors.CommentEditorInput;
import org.xwiki.eclipse.ui.editors.ObjectEditor;
import org.xwiki.eclipse.ui.editors.ObjectEditorInput;
import org.xwiki.eclipse.ui.editors.PageEditor;
import org.xwiki.eclipse.ui.editors.PageEditorInput;
import org.xwiki.eclipse.ui.utils.UIUtils;

/**
 * This is defined as a standard action and not with the command framework because the common navigator does not export
 * a command with the ICommonActionConstants.OPEN id. So in order to make double click work we need to do things in this
 * way.
 * 
 * @version $Id$
 */
public class OpenXWikiModelObjectAction extends Action
{
    private ISelectionProvider selectionProvider;

    private final static String text = "Open...";

    public OpenXWikiModelObjectAction(ISelectionProvider selectionProvider)
    {
        super(text);
        this.selectionProvider = selectionProvider;
    }

    @Override
    public void run()
    {
        Set selectedObjects = UIUtils.getSelectedObjectsFromSelection(selectionProvider.getSelection());
        for (Object object : selectedObjects) {
            if (object instanceof XWikiEclipsePageSummary) {
                final XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) object;

                try {
                    XWikiEclipsePage page =
                        pageSummary.getDataManager().getPage(pageSummary.getWiki(), pageSummary.getSpace(),
                            pageSummary.getName(), pageSummary.getLanguage());

                    if (page == null) {
                        UIUtils
                            .showMessageDialog(
                                Display.getDefault().getActiveShell(),
                                "Page not avaliable",
                                "The page is not currently available. This might happen if the page has been removed remotely or if the page is not locally available.");

                        return;
                    }

                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .openEditor(new PageEditorInput(page, false), PageEditor.ID);
                } catch (XWikiEclipseStorageException e) {
                    UIUtils
                        .showMessageDialog(
                            Display.getDefault().getActiveShell(),
                            SWT.ICON_ERROR,
                            "Error opening page.",
                            "There was a communication error while opening the page. XWiki Eclipse is taking the connection offline in order to prevent further errors. Please check your remote XWiki status and then try to reconnect.");

                    CoreLog.logError("Error opening page", e);

                    pageSummary.getDataManager().disconnect();
                } catch (PartInitException e) {
                    UIUtils.showMessageDialog(Display.getDefault().getActiveShell(), "Error opening editor",
                        "There was an error while opening the editor.");
                }
            }
            if (object instanceof XWikiEclipseObjectSummary) {
                final XWikiEclipseObjectSummary objectSummary = (XWikiEclipseObjectSummary) object;

                try {
                    XWikiEclipseObject xwikiObject =
                        objectSummary.getDataManager().getObject(objectSummary.getWiki(), objectSummary.getSpace(),
                            objectSummary.getPageName(), objectSummary.getClassName(), objectSummary.getNumber());

                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .openEditor(new ObjectEditorInput(xwikiObject), ObjectEditor.ID);
                } catch (XWikiEclipseStorageException e) {
                    UIUtils
                        .showMessageDialog(
                            Display.getDefault().getActiveShell(),
                            SWT.ICON_ERROR,
                            "Error getting the object.",
                            "There was a communication error while getting the object. XWiki Eclipse is taking the connection offline in order to prevent further errors. Please check your remote XWiki status and then try to reconnect.");

                    CoreLog.logError("Error getting object", e);

                    objectSummary.getDataManager().disconnect();
                } catch (PartInitException e) {
                    UIUtils.showMessageDialog(Display.getDefault().getActiveShell(), "Error opening editor",
                        "There was an error while opening the editor.");
                }

            }

            if (object instanceof XWikiEclipseComment) {
                final XWikiEclipseComment comment = (XWikiEclipseComment) object;

                try {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .openEditor(new CommentEditorInput(comment, this), CommentEditor.ID);
                } catch (PartInitException e) {
                    UIUtils.showMessageDialog(Display.getDefault().getActiveShell(), "Error opening editor",
                        "There was an error while opening the editor.");
                }

            }
        }
    }
}
