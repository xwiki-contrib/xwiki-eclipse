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

import java.util.Calendar;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.eclipse.core.CoreLog;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.ui.editors.CommentEditor;
import org.xwiki.eclipse.ui.editors.CommentEditorInput;
import org.xwiki.eclipse.ui.utils.UIUtils;

/**
 * @version $Id$
 */
public class NewCommentHandler extends AbstractHandler
{
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);

        Command command = event.getCommand();

        Set selectedObjects = UIUtils.getSelectedObjectsFromSelection(selection);
        if (selectedObjects.size() == 1) {
            Object selectedObject = selectedObjects.iterator().next();
            if (selectedObject instanceof XWikiEclipsePageSummary) {
                XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) selectedObject;

                try {
                    XWikiEclipseComment comment = new XWikiEclipseComment(pageSummary.getDataManager());
                    comment.setAuthor(pageSummary.getDataManager().getUserName());

                    comment.setPageId(pageSummary.getId());

                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .openEditor(new CommentEditorInput(comment, command), CommentEditor.ID);
                } catch (Exception e) {
                    CoreLog.logError("Error in new comment", e);
                }
            }

            if (selectedObject instanceof XWikiEclipseComment) {
                XWikiEclipseComment comment = (XWikiEclipseComment) selectedObject;

                try {
                    XWikiEclipseComment replyToComment = new XWikiEclipseComment(comment.getDataManager());
                    replyToComment.setAuthor(comment.getDataManager().getUserName());
                    replyToComment.setReplyTo(comment.getId());
                    /*
                     * make sure that multiple editor pages can be opened, by setting different date and time to create
                     * different input
                     */
                    replyToComment.setDate(Calendar.getInstance());
                    replyToComment.setPageId(comment.getPageId());

                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .openEditor(new CommentEditorInput(replyToComment, command), CommentEditor.ID);
                } catch (Exception e) {
                    CoreLog.logError("Error new comment", e);
                }
            }
        }

        return null;
    }
}
