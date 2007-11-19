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

package org.xwiki.plugins.eclipse.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.xwiki.plugins.eclipse.model.IXWikiPage;

/**
 * Manages the installation/deinstallation of global actions for multi-page editors. Responsible for
 * the redirection of global actions to the active editor. Multi-page contributor replaces the
 * contributors for the individual editors in the multi-page editor.
 */
public class XWikiEditorContributor extends MultiPageEditorActionBarContributor
{
    /**
     * Active EditorPart as known to the contributor.
     */
    private IEditorPart activeEditorPart;

    /**
     * Creates a multi-page contributor.
     */
    public XWikiEditorContributor()
    {
        super();
    }

    /**
     * @return The action registed with the given text editor.
     */
    protected IAction getAction(ITextEditor editor, String actionID)
    {
        return (editor == null ? null : editor.getAction(actionID));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.MultiPageEditorActionBarContributor#setActivePage(org.eclipse.ui.IEditorPart)
     */
    public void setActivePage(IEditorPart part)
    {
        if (activeEditorPart == part) {
            return;
        }
        activeEditorPart = part;
        IActionBars actionBars = getActionBars();
        if (actionBars != null) {
            ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;
            actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), getAction(editor,
                ITextEditorActionConstants.DELETE));
            actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), getAction(editor,
                ITextEditorActionConstants.UNDO));
            actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), getAction(editor,
                ITextEditorActionConstants.REDO));
            actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), getAction(editor,
                ITextEditorActionConstants.CUT));
            actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), getAction(editor,
                ITextEditorActionConstants.COPY));
            actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), getAction(editor,
                ITextEditorActionConstants.PASTE));
            actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), getAction(editor,
                ITextEditorActionConstants.SELECT_ALL));
            actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), getAction(editor,
                ITextEditorActionConstants.FIND));
            actionBars.setGlobalActionHandler(IDEActionFactory.BOOKMARK.getId(), getAction(
                editor, IDEActionFactory.BOOKMARK.getId()));
            actionBars.updateActionBars();
        }
        if (part instanceof XWikiBrowserPart) {
            XWikiBrowserPart browser = (XWikiBrowserPart) part;
            IXWikiPage page = (IXWikiPage) browser.getEditorInput();
            String url = page.getUrl();
            browser.getBrowser().setUrl(url);
            browser.getXwikiBrowserUi().getToggleViewButton().setSelection(false);

        }
    }
}
