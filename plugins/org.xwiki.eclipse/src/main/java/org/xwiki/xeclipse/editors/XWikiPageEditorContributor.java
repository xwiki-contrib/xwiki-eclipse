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
package org.xwiki.xeclipse.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.xwiki.xeclipse.XWikiEclipseEvent;
import org.xwiki.xeclipse.XWikiEclipseNotificationCenter;

public class XWikiPageEditorContributor extends BasicTextEditorActionContributor
{
    public XWikiPageEditorContributor()
    {
        super();
    }

    @Override
    public void setActiveEditor(IEditorPart part)
    {
        super.setActiveEditor(part);
                        
        if (!(part instanceof ITextEditor)) {
            return;
        }

        IActionBars actionBars = getActionBars();

        if (actionBars == null) {
            return;
        }

        ITextEditor editor = (ITextEditor) part;

        actionBars.setGlobalActionHandler(ActionFactory.SAVE.getId(), getAction(editor,
            ITextEditorActionConstants.SAVE));

        IAction action = getAction(editor, ITextEditorActionConstants.CUT);
        actionBars.setGlobalActionHandler(action.getActionDefinitionId(), action);

        action = getAction(editor, ITextEditorActionConstants.COPY);
        actionBars.setGlobalActionHandler(action.getActionDefinitionId(), action);

        action = getAction(editor, ITextEditorActionConstants.PASTE);
        actionBars.setGlobalActionHandler(action.getActionDefinitionId(), action);

        actionBars.updateActionBars();     
        
        /*
         * Send a notification that the edited page has been updated.
         */
        if(part instanceof XWikiPageEditor) {
            XWikiPageEditor xwikiPageEditor = (XWikiPageEditor) part;
            XWikiPageEditorInput xwikiPageEditorInput = (XWikiPageEditorInput) xwikiPageEditor.getEditorInput();
            XWikiEclipseNotificationCenter.getDefault().fireEvent(xwikiPageEditor, XWikiEclipseEvent.PAGE_UPDATED, xwikiPageEditorInput.getXWikiPage());           
        }
        
    }

}
