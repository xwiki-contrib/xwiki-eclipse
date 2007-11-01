package org.xwiki.xeclipse.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

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
    }

}
