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
package org.xwiki.eclipse.ui.editors.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.xwiki.eclipse.ui.editors.Constants;
import org.xwiki.eclipse.ui.editors.Preferences;
import org.xwiki.eclipse.ui.editors.scanners.rules.BalancedParenthesisRule;
import org.xwiki.eclipse.ui.editors.scanners.rules.DefinitionListRule;
import org.xwiki.eclipse.ui.editors.scanners.rules.HeaderRule;
import org.xwiki.eclipse.ui.editors.scanners.rules.ListRule;
import org.xwiki.eclipse.ui.editors.scanners.rules.RegExRule;

/**
 * This scanner is used for Syntax Highlighting of XWiki Syntax (1.0 and 2.0)
 * It is activated from XWikiSourceViewerConfiguration#getPresentationReconciler
 * 
 * @see XWikiSourceViewerConfiguration#getPresentationReconciler
 * @version $Id$
 */
public class XWikiMarkupScanner extends RuleBasedScanner
{
    public XWikiMarkupScanner()
    {
        List<IRule> rules = getRules();
        setRules(rules.toArray(new IRule[rules.size()]));
    }

    protected List<IRule> getRules()
    {
        IToken boldToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.BOLD));

        IToken italicToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.ITALIC));

        IToken linkToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.LINK));

        IToken listBulletToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.LIST_BULLET));

        IToken definitionTermToken =
            new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.DEFINITION_TERM));

        IToken heading1Token = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.HEADING1));
        IToken heading2Token = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.HEADING2));
        IToken heading3Token = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.HEADING3));
        IToken heading4Token = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.HEADING4));
        IToken heading5Token = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.HEADING5));
        IToken heading6Token = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.HEADING6));

        IToken imageToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.IMAGE));

        IToken identifierToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.IDENTIFIER));
        IToken macroToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.MACRO));
        IToken otherStyleToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.UNDERLINE));

        List<IRule> rules = new ArrayList<IRule>();

        HeaderRule headers = new HeaderRule();
        headers.add("^= .", heading1Token);
        headers.add("^== .", heading2Token);
        headers.add("^=== .", heading3Token);
        headers.add("^==== .", heading4Token);
        headers.add("^===== .", heading5Token);
        headers.add("^====== .", heading6Token);
        headers.add("^1 .", heading1Token);
        headers.add("^1\\.1 .", heading2Token);
        headers.add("^1\\.1\\.1 .", heading3Token);
        headers.add("^1\\.1\\.1\\.1 .", heading4Token);
        headers.add("^1\\.1\\.1\\.1\\.1 .", heading5Token);
        headers.add("^1\\.1\\.1\\.1\\.1\\.1 .", heading6Token);
        rules.add(headers);

        rules.add(new ListRule(Constants.LIST_BULLET_PATTERN, listBulletToken));
        rules.add(new DefinitionListRule(Constants.DEFINITION_TERM_PATTERN, definitionTermToken));
        rules.add(new SingleLineRule("**", "**", boldToken, '\\'));
        rules.add(new SingleLineRule("*", "*", boldToken, '\\'));
        rules.add(new SingleLineRule("~~", "~~", italicToken, '\\'));
        rules.add(new SingleLineRule("//", "//", italicToken, '\\'));
        rules.add(new SingleLineRule("[", "]", linkToken, '\\'));
        rules.add(new SingleLineRule("__", "__", otherStyleToken, '\\'));
        rules.add(new SingleLineRule("--", "--", otherStyleToken, '\\'));
        rules.add(new SingleLineRule("<tt>", "</tt>", otherStyleToken, '\\'));
        rules.add(new SingleLineRule("##", "##", otherStyleToken, '\\'));
        rules.add(new SingleLineRule("<sub>", "</sub>", otherStyleToken, '\\'));
        rules.add(new SingleLineRule(",,", ",,", otherStyleToken, '\\'));
        rules.add(new SingleLineRule("<sup>", "</sup>", otherStyleToken, '\\'));
        rules.add(new SingleLineRule("^^", "^^", otherStyleToken, '\\'));
        rules.add(new SingleLineRule("(% style", "%)", otherStyleToken, '\\'));
        rules.add(new SingleLineRule("{image:", "}", imageToken, '\\'));
        rules.add(new SingleLineRule("image:", " ", imageToken, '\\'));

        rules.add(new SingleLineRule("{{{", "}}}", otherStyleToken));
        rules.add(new SingleLineRule("{{", "}}", macroToken));

        return rules;
    }
}
