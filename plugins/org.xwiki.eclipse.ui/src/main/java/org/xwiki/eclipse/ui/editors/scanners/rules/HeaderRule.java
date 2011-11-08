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
package org.xwiki.eclipse.ui.editors.scanners.rules;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class HeaderRule extends Rule
{
    private Map<Pattern, IToken> tokenLookup = new LinkedHashMap<Pattern, IToken>();

    public IToken evaluate(ICharacterScanner scanner)
    {
        if (scanner.getColumn() != 0) {
            return Token.UNDEFINED;
        }

        // Consume all leading spaces.
        StringBuffer spaces = readLeadingSpaces(scanner);

        // Consume the first two words.
        StringBuffer text = readWord(scanner);
        text.append(readWord(scanner));

        Iterator<Pattern> patterns = tokenLookup.keySet().iterator();
        while (patterns.hasNext()) {
            Pattern pattern = patterns.next();
            if (pattern == null) {
                continue;
            }

            // Is there a headline?
            Matcher matcher = pattern.matcher(text);
            if (matcher.lookingAt()) {
                // Yes. Consume the rest of the line.
                readLine(scanner);
                return tokenLookup.get(pattern);
            }
        }

        // There is no headline. Rewind the scanner.
        unread(scanner, spaces.length() + text.length());
        return Token.UNDEFINED;
    }

    public void add(String regex, IToken token)
    {
        tokenLookup.put(Pattern.compile(regex), token);
    }
}
