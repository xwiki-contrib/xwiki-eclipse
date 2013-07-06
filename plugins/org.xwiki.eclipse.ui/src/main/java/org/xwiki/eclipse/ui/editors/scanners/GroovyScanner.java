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
/*
 * Modified from GroovyPartitionScanner from Groovy Eclipse Plugin
 * http://groovy.codehaus.org/Eclipse+Plugin 
 * 
 * Copyright 2003-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xwiki.eclipse.ui.editors.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordPatternRule;
import org.eclipse.jface.text.rules.WordRule;
import org.xwiki.eclipse.ui.editors.Preferences;

public class GroovyScanner extends RuleBasedScanner {

    /**
     * Detector for directives.
     */
    static class DirectiveDetector implements IWordDetector {

        public boolean isWordStart(char aChar) {
            return isWordPart(aChar);
        }

        public boolean isWordPart(char aChar) {
            return ((aChar >= 'a') && (aChar <= 'z')) || ((aChar >= 'A') && (aChar <= 'Z')) || ((aChar >= '0') && (aChar <= '9'))
                || (aChar == '{') || (aChar == '}') || (aChar == '!') || (aChar == '_') || (aChar == '-');
        }
    }

    /**
     * Creates the partitioner and sets up the appropriate rules.
     */
    public GroovyScanner() {
        List<IRule> rules= new ArrayList<IRule>();
        IToken commentToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.COMMENT));
        IToken stringToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.UNDERLINE));
        IToken otherStyleToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.DEFAULT));
        IToken directiveToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.IDENTIFIER));
        IToken macroToken = new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.MACRO));

        // Add rule for JavaDoc
        rules.add(new MultiLineRule("/**", "*/", commentToken, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$

        // Add rules for multi-line comments
        rules.add(new MultiLineRule("/*", "*/", commentToken, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$

        rules.add(new SingleLineRule("{{", "}}", macroToken, '\\'));

        // Add rule for single line comments.
        rules.add(new EndOfLineRule("//", commentToken));

        // Add rule for strings and character constants.
        rules.add(new MultiLineRule("'''", "'''", stringToken));
        rules.add(new MultiLineRule("\"\"\"", "\"\"\"", stringToken));
        // GRECLIPSE-1111 do not eagerly match these kinds of multiline strings
        rules.add(new SingleLineRule("\"", "\"", stringToken, '\\'));
        rules.add(new SingleLineRule("'", "'", stringToken, '\\'));

        // GRECLIPSE-1203 make dollar slashies optionally highlighted
        rules.add(new MultiLineRule("$/", "/$", stringToken, '\0', false));

        DirectiveDetector ddetector = new DirectiveDetector();
        WordRule wordrule = new WordRule(ddetector,otherStyleToken);
        wordrule.addWord("def", directiveToken);
        wordrule.addWord("import", directiveToken);
        wordrule.addWord("if", directiveToken);
        wordrule.addWord("for", directiveToken);
        wordrule.addWord("do", directiveToken);
        wordrule.addWord("while", directiveToken);
        wordrule.addWord("try", directiveToken);
        wordrule.addWord("catch", directiveToken);
        wordrule.addWord("finally", directiveToken);
        wordrule.addWord("public", directiveToken);
        wordrule.addWord("class", directiveToken);
        wordrule.addWord("final", directiveToken);
        wordrule.addWord("static", directiveToken);
        wordrule.addWord("new", directiveToken);
        wordrule.addWord("protected", directiveToken);
        wordrule.addWord("private", directiveToken);
        wordrule.addWord("extends", directiveToken);
        wordrule.addWord("implements", directiveToken);
        rules.add(wordrule);

        setRules(rules.toArray(new IRule[rules.size()]));
        setDefaultReturnToken(otherStyleToken);
    }
}