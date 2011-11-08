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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;

public abstract class Rule implements IRule
{
    private static final char[] LINE_SEPARATOR = System.getProperty("line.separator").toCharArray();

    protected StringBuffer readLeadingSpaces(ICharacterScanner scanner)
    {
        StringBuffer spaces = new StringBuffer();

        int c;
        while ((c = scanner.read()) != ICharacterScanner.EOF && isSpace(c)) {
            spaces.append((char) c);
        }

        // Rewind the last character.
        scanner.unread();
        return spaces;
    }

    private boolean isSpace(int c)
    {
        return ((char) c) == ' ' || ((char) c) == '\t';
    }

    protected StringBuffer readWord(ICharacterScanner scanner)
    {
        StringBuffer word = new StringBuffer();

        int c;
        while ((c = scanner.read()) != ICharacterScanner.EOF) {
            word.append((char) c);

            if (Character.isWhitespace(c)) {
                return word;
            }
        }

        // Rewind the EOF.
        scanner.unread();
        return word;
    }

    protected StringBuffer readLine(ICharacterScanner scanner)
    {
        StringBuffer line = new StringBuffer();

        do {
            char[] separator = readLineSeparatorIfPresent(scanner);
            if (separator != null) {
                line.append(separator);
                return line;
            }

            int c = scanner.read();
            if (c == ICharacterScanner.EOF) {
                break;
            }

            line.append((char) c);
        } while (true);

        // Rewind EOF.
        scanner.unread();
        return line;
    }

    private char[] readLineSeparatorIfPresent(ICharacterScanner scanner)
    {
        for (int n = 0; n < LINE_SEPARATOR.length; n++) {
            if (LINE_SEPARATOR[n] != (char) scanner.read()) {
                unread(scanner, n + 1);
                return null;
            }
        }
        return LINE_SEPARATOR;
    }

    protected void unread(ICharacterScanner scanner, int count)
    {
        for (int n = 0; n < count; n++) {
            scanner.unread();
        }
    }
}