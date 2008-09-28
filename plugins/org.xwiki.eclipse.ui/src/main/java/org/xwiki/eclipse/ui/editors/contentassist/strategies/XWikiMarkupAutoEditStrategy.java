package org.xwiki.eclipse.ui.editors.contentassist.strategies;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.xwiki.eclipse.ui.editors.Constants;

/**
 * @author fmancinelli, venkatesh, malaka
 */
public class XWikiMarkupAutoEditStrategy implements IAutoEditStrategy
{
    private Pattern listBulletPattern = Pattern.compile(String.format("^(?:%s)", Constants.LIST_BULLET_PATTERN));

    public void customizeDocumentCommand(IDocument document, DocumentCommand command)
    {
        try {
            if (command.text.equals("*")) {
                if (!isListStarBullet(document, command)) {
                    configureCommand(command, "**", 1);
                }
            } else if (command.text.equals("~")) {
                if (document.getChar(command.offset - 1) == '~') {
                    configureCommand(command, "~~~", 1);
                }
            } else if (command.text.equals("_")) {
                if (document.getChar(command.offset - 1) == '_') {
                    configureCommand(command, "___", 1);
                }
            } else if (command.text.equals("-")) {
                if (document.getChar(command.offset - 1) == '-') {
                    configureCommand(command, "---", 1);
                }
            } else if (command.text.equals("[")) {
                configureCommand(command, "[]", 1);
            } else if (command.text.equals(">")) {
                String tag = getTag(document, '<', command.offset - 1);
                if (tag != null) {
                    String closingTag = String.format("></%s>", tag.substring(1));

                    configureCommand(command, closingTag, 1);
                }
            } else if (command.text.equals("\n")) {
                String bullet = getListBullet(document, command.offset);
                if (bullet != null) {
                    configureCommand(command, bullet, bullet.length());
                }
            }
        } catch (BadLocationException e) {
        }
    }

    private String getListBullet(IDocument document, int offset)
    {
        try {
            IRegion lineRegion = document.getLineInformationOfOffset(offset);
            String line = document.get(lineRegion.getOffset(), lineRegion.getLength());

            Matcher m = listBulletPattern.matcher(line);
            if (m.find()) {
                if (!line.equals(m.group())) {
                    return String.format("\n%s", m.group());
                }
            }
        } catch (BadLocationException e) {
        }

        return null;

    }

    private boolean isListStarBullet(IDocument document, DocumentCommand command)
    {
        try {
            IRegion lineRegion = document.getLineInformationOfOffset(command.offset);
            String line = document.get(lineRegion.getOffset(), lineRegion.getLength());

            return Pattern.matches("^\\**", line);
        } catch (BadLocationException e) {
        }

        return false;
    }

    private String getTag(IDocument document, char openingChar, int endOffset)
    {
        try {
            int startOffset = endOffset;
            int character;

            while (startOffset >= 0) {
                character = document.getChar(startOffset);
                if (character == '\n') {
                    return null;
                }

                if (character == openingChar) {
                    return document.get(startOffset, endOffset - startOffset + 1);
                }

                startOffset--;
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void configureCommand(DocumentCommand command, String text, int caretAdjustement)
    {
        command.text = text;
        command.caretOffset = command.offset + caretAdjustement;
        command.shiftsCaret = false;
    }
}
