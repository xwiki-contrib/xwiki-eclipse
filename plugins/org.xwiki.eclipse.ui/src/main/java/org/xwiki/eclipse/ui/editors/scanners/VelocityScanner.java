package org.xwiki.eclipse.ui.editors.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.xwiki.eclipse.ui.editors.Preferences;
import org.xwiki.eclipse.ui.editors.scanners.rules.BalancedParenthesisRule;

/**
 * @author fmancinelli, venkatesh, malaka
 */
public class VelocityScanner extends RuleBasedScanner
{
    public VelocityScanner()
    {
        IToken identifierToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.IDENTIFIER));
        IToken otherStyleToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.UNDERLINE));

        List<IRule> rules = new ArrayList<IRule>();

        rules.add(new SingleLineRule("'", "'", otherStyleToken, '\\'));
        rules.add(new SingleLineRule("\"", "\"", otherStyleToken, '\\'));
        // rules.add(new RegExRule("\\$[\\p{Alnum}\\.\\(\\)]*", identifierToken));
        rules.add(new BalancedParenthesisRule('$', identifierToken));

        setRules(rules.toArray(new IRule[rules.size()]));

        setDefaultReturnToken(new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.MACRO)));
    }
}
