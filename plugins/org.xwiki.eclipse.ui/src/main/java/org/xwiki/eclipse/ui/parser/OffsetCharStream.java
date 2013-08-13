package org.xwiki.eclipse.ui.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.xwiki.eclipse.ui.parser.xwiki21.SimpleCharStream;

/**
 * CharStream used by the XWiki+Velocity parser
 *
 */
public class OffsetCharStream extends SimpleCharStream {
	public int beginOffset;
	public int currentOffset;

	public OffsetCharStream(Reader r) {
		super(r, 1, 1);
	}

	public OffsetCharStream(Reader r, int i, int j) {
		super(r, i, j);
	}

	public OffsetCharStream(InputStream r, String e, int i, int j)
			throws UnsupportedEncodingException {
		super(r, e, i, j);
	}

	public char BeginToken() throws IOException {
		char c = super.BeginToken();
		beginOffset = currentOffset;

		return c;
	}

	public char readChar() throws IOException {
		char c = super.readChar();
		currentOffset++;

		return c;
	}

	public void backup(int amount) {
		super.backup(amount);
		currentOffset -= amount;
	}
}