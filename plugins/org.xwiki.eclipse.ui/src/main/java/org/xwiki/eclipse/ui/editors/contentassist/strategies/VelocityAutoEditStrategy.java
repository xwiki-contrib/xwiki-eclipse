package org.xwiki.eclipse.ui.editors.contentassist.strategies;

import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;

/**
 * @author fmancinelli, venkatesh, malaka
 */
public class VelocityAutoEditStrategy implements IAutoEditStrategy
{
    public void customizeDocumentCommand(IDocument document, DocumentCommand command)
    {
        if (command.text.equals("\"")) {
            configureCommand(command, "\"\"", 1);
        } else if (command.text.equals("'")) {
            configureCommand(command, "''", 1);
        }
    }

    private void configureCommand(DocumentCommand command, String text, int caretAdjustement)
    {
        command.text = text;
        command.caretOffset = command.offset + caretAdjustement;
        command.shiftsCaret = false;
    }
}
