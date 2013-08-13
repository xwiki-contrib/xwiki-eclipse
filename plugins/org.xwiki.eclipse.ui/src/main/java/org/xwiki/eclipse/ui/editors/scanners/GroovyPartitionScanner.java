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
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.xwiki.eclipse.ui.editors.PageDocumentProvider;

/**
 * This Groovy partition scanner is used to perform partition of pure Groovy code
 * It is used for syntax highlighting of Groovy classes stored in XWiki pages
 * 
 * The partitioner is activated in createDocument in PageDocumentProvider.java
 * @see PageDocumentProvider#createDocument(Object)
 * 
 * Modified from GroovyPartitionScanner from Groovy Eclipse Plugin
 * http://groovy.codehaus.org/Eclipse+Plugin 
 * 
 */
public class GroovyPartitionScanner extends RuleBasedPartitionScanner {

    public final static String GROOVY_MULTILINE_STRINGS= "__groovy_multiline_string"; //$NON-NLS-1$

    /**
     * The identifier of the Java partitioning.
     */
    public final static String JAVA_PARTITIONING= "___java_partitioning";  //$NON-NLS-1$

    /**
     * The identifier of the single-line (JLS2: EndOfLineComment) end comment partition content type.
     */
    public final static String JAVA_SINGLE_LINE_COMMENT= "__java_singleline_comment"; //$NON-NLS-1$

    /**
     * The identifier multi-line (JLS2: TraditionalComment) comment partition content type.
     */
    public final static String JAVA_MULTI_LINE_COMMENT= "__java_multiline_comment"; //$NON-NLS-1$

    /**
     * The identifier of the Javadoc (JLS2: DocumentationComment) partition content type.
     */
    public final static String JAVA_DOC= "__java_javadoc"; //$NON-NLS-1$

    /**
     * The identifier of the Java string partition content type.
     */
    public final static String JAVA_STRING= "__java_string"; //$NON-NLS-1$

    /**
     * The identifier of the Java character partition content type.
     */
    public final static String JAVA_CHARACTER= "__java_character";  //$NON-NLS-1$

    /**
     * The identifier of the Java character partition content type.
     */
    public final static String GROOVY_DEFAULT = "__groovy_default";  //$NON-NLS-1$

    /**
     * Array with legal content types.
     * @since 3.0
     */
    public final static String[] LEGAL_CONTENT_TYPES= new String[] {
        JAVA_DOC,
        JAVA_MULTI_LINE_COMMENT,
        JAVA_SINGLE_LINE_COMMENT,
        JAVA_STRING,
        JAVA_CHARACTER,
        GROOVY_MULTILINE_STRINGS,
        GROOVY_DEFAULT
    };

    /**
     * Creates the partitioner and sets up the appropriate rules.
     */
    public GroovyPartitionScanner() {
        IToken commentToken = new Token(JAVA_MULTI_LINE_COMMENT);
        IToken stringToken = new Token(GROOVY_MULTILINE_STRINGS);
        IToken defaultToken = new Token(GROOVY_DEFAULT);

        List<IRule> rules= new ArrayList<IRule>();
        setDefaultReturnToken(defaultToken);

        // Add rules for the rest
        rules.add(new SingleLineRule("import",  null, defaultToken, '\\'));
        rules.add(new MultiLineRule("Public class", "", defaultToken, (char) 0, true));
        rules.add(new MultiLineRule("public class", "", defaultToken, (char) 0, true));

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

        // Add special case word rule.
        // rules.add(new WordPredicateRule(commentToken));

        // Add rule for JavaDoc
        rules.add(new MultiLineRule("/**", "*/", commentToken, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$

        // Add rules for multi-line comments
        rules.add(new MultiLineRule("/*", "*/", commentToken, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$


        IPredicateRule[] result= new IPredicateRule[rules.size()];
        rules.toArray(result);
        setPredicateRules(result);
    }
}