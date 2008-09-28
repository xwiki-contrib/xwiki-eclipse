package org.xwiki.eclipse.ui.editors.scanners.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * @author fmancinelli, venkatesh, malaka
 */
public class BalancedParenthesisRule implements IPredicateRule
{
    private IToken token;

    private char startingChar;

    public BalancedParenthesisRule(char startingChar, IToken token)
    {
        this.startingChar = startingChar;
        this.token = token;
    }

    public IToken evaluate(ICharacterScanner scanner, boolean resume)
    {
        /*
         * Here the logic is the following: isolate the partition between # and a white space provided that all
         * parenthesis are balanced. If there are open parenthesis, keep scanning until the last parenthesis is closed.
         */
        int parenthesis = 0;

        int c = scanner.read();
        if (c == startingChar) {
            while ((c = scanner.read()) != ICharacterScanner.EOF) {
                if (Character.isWhitespace(c) && parenthesis == 0) {
                    scanner.unread();
                    return token;
                } else if (c == '(') {
                    parenthesis++;
                } else if (c == ')') {
                    parenthesis--;
                    /* If this is the last parenthesis, then end scanning */
                    if (parenthesis == 0) {
                        return token;
                    }
                }
            }

            return token;
        }

        scanner.unread();
        return Token.UNDEFINED;
    }

    public IToken getSuccessToken()
    {
        return token;
    }

    public IToken evaluate(ICharacterScanner scanner)
    {
        return evaluate(scanner, false);
    }

}
