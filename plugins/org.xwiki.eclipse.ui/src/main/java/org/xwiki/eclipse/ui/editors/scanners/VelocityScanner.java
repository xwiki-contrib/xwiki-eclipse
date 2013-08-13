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

import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordPatternRule;
import org.eclipse.jface.text.rules.WordRule;
import org.xwiki.eclipse.ui.editors.Preferences;

/**
 * This scanner is used for Syntax Highlighting of Velocity Code
 * It is activated from XWikiSourceViewerConfiguration#getPresentationReconciler
 * 
 * @see XWikiSourceViewerConfiguration#getPresentationReconciler
 * @version $Id$
 */
public class VelocityScanner extends XWikiMarkupScanner
{
    /**
     * Detector for directives.
     */
    static class DirectiveDetector implements IWordDetector {

        /*
         * (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
         */
        public boolean isWordStart(char aChar)
        {
            return ((aChar == '#') || (aChar == '$'));
        }

        /*
         * (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
         */
        public boolean isWordPart(char aChar)
        {
            return ((aChar >= 'a') && (aChar <= 'z')) || ((aChar >= 'A') && (aChar <= 'Z')) || ((aChar >= '0') && (aChar <= '9'))
                || (aChar == '{') || (aChar == '}') || (aChar == '!') || (aChar == '_') || (aChar == '-');
        }
    }

    public VelocityScanner()
    {
        IToken identifierToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.IDENTIFIER));
        IToken htmlToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.HTML));
        IToken commentToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.COMMENT));
        IToken stringToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.UNDERLINE));
        IToken otherStyleToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.DEFAULT));
        IToken directiveToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.DEFINITION_TERM));
 
        List<IRule> rules = super.getRules();

        // Add rule for single line comments
        rules.add(new EndOfLineRule("##", commentToken));
        // Add rules for multi-line comments and doc comments
        rules.add(new MultiLineRule("#**", "*#", commentToken));
        rules.add(new MultiLineRule("#*", "*#", commentToken));
        // Add special empty comment word rules
        IWordDetector ddetector = new DirectiveDetector();

        rules.add(new WordPatternRule(ddetector, "#***#", null, commentToken));
        rules.add(new WordPatternRule(ddetector, "#**#", null, commentToken));

        rules.add(new SingleLineRule("'", "'", stringToken, '\\'));
        rules.add(new SingleLineRule("\"", "\"", stringToken, '\\'));
        // rules.add(new RegExRule("\\$[\\p{Alnum}\\.\\(\\)]*", identifierToken));
        // rules.add(new BalancedParenthesisRule('$', identifierToken));
        rules.add(new WordPatternRule(ddetector, "#set", null, directiveToken));
        rules.add(new WordPatternRule(ddetector, "#if", null, directiveToken));
        rules.add(new WordPatternRule(ddetector, "#else", null, directiveToken));
        rules.add(new WordPatternRule(ddetector, "#elseif", null, directiveToken));
        rules.add(new WordPatternRule(ddetector, "#end", null, directiveToken));
        rules.add(new WordPatternRule(ddetector, "#macro", null, directiveToken));
        rules.add(new WordPatternRule(ddetector, "#foreach", null, directiveToken));
        rules.add(new WordPatternRule(ddetector, "#stop", null, directiveToken));

        rules.add(new WordRule(ddetector, identifierToken));

        rules.add(new MultiLineRule("<![CDATA[", "]]>", htmlToken));
        rules.add(new MultiLineRule("<![", "]>", htmlToken));
        rules.add(new MultiLineRule("<", ">", htmlToken));

        setRules(rules.toArray(new IRule[rules.size()]));

        setDefaultReturnToken(otherStyleToken);
    }
}
