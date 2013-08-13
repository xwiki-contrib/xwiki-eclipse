package org.xwiki.eclipse.ui.parser;

import org.xwiki.eclipse.ui.parser.xwiki21.Token;
import org.xwiki.eclipse.ui.parser.xwiki21.XWikiScannerTokenManager;


public class PartitionTokenManager extends XWikiScannerTokenManager {
	public PartitionTokenManager(OffsetCharStream stream) {
		super(stream);
	}

	@Override
	protected Token jjFillToken() {
		Token token = super.jjFillToken();

		token.setBeginOffset(((OffsetCharStream) input_stream).beginOffset - 1);
		token.setEndOffset(((OffsetCharStream) input_stream).currentOffset);

		return token;
	}

}
