package org.xwiki.eclipse.ui.editors.utils;

import org.eclipse.jface.text.rules.ICharacterScanner;

/**
 * This class implements a CharSequence that is backed by an ICharacterScanner. It is used as a bridge for using Java
 * regex pattern matching with IRules that use ICharacterScanners for the evaluation.
 * 
 * @author fmancinelli
 */
public class CharacterScannerCharSequence implements CharSequence
{
    /**
     * The buffer used for buffering characters read from scanner.
     */
    private StringBuffer buffer;

    /**
     * The source scanner.
     */
    private ICharacterScanner scanner;

    /**
     * The column of the first character that will be read from the scanner.
     */
    private int column;

    public CharacterScannerCharSequence(ICharacterScanner scanner)
    {
        this.scanner = scanner;

        buffer = new StringBuffer();

        column = scanner.getColumn();

        /* Buffer all the characters */
        int c;
        while ((c = scanner.read()) != ICharacterScanner.EOF) {
            buffer.append((char) c);
        }
    }

    /**
     * @return The column of the first character of this sequence.
     */
    public int getColumn()
    {
        return column;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.CharSequence#charAt(int)
     */
    public char charAt(int index)
    {
        return buffer.charAt(index);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.CharSequence#length()
     */
    public int length()
    {
        return buffer.length();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.CharSequence#subSequence(int, int)
     */
    public CharSequence subSequence(int start, int end)
    {
        return buffer.subSequence(start, end);
    }

    /**
     * Push back all the buffered characters to the scanner.
     */
    public void unread()
    {
        unread(buffer.length());
    }

    /**
     * Push back characters to the scanner.
     * 
     * @param n The number of characters to push back.
     */
    public void unread(int n)
    {
        for (int i = 0; i <= n; i++) {
            scanner.unread();
        }
    }

    @Override
    public String toString()
    {
        return buffer.toString();
    }

}
