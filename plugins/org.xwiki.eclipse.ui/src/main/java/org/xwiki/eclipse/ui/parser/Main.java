package org.xwiki.eclipse.ui.parser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.xwiki.eclipse.ui.parser.xwiki21.ParseException;
import org.xwiki.eclipse.ui.parser.xwiki21.XWikiScanner;

public class Main {

	/**
	 * This is the main method.
	 * 
	 * @param args
	 *            arguments.
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ParseException, IOException {
		InputStream is = Main.class.getResourceAsStream("/test2.xwiki");
		StringBuffer sb = new StringBuffer();
		byte[] buffer = new byte[4096];
		int read;
		while ((read = is.read(buffer)) == 4096) {
			sb.append(new String(buffer));
		}
		sb.append(new String(Arrays.copyOf(buffer, read)));

		String text = sb.toString();

		OffsetCharStream offsetCharStream = new OffsetCharStream(
				new InputStreamReader(new ByteArrayInputStream(text.getBytes())));

		PartitionTokenManager regionTokenManager = new PartitionTokenManager(
				offsetCharStream);

		XWikiScanner xwikiScanner = new XWikiScanner(regionTokenManager);
		PartitionsBuilder partitionsBuilder = new PartitionsBuilder();
		xwikiScanner.parse(partitionsBuilder);

		System.out.format("\nDumping:\n");
		for (Partition region : partitionsBuilder.getPartitions()) {
			System.out.format("%s (%d-%d) '%s' %s\n", region.getType(), region
					.getBeginOffset(), region.getEndOffset(),
					shorten(sb.substring(region.getBeginOffset(),
							region.getEndOffset())),
					region.getAttributes() != null ? region.getAttributes()
							: "");
		}
	}

	public static String shorten(String s) {
		// if (s.length() < 30) {
		return s.replaceAll("\n", "␤");
		// }

		// return String.format("%s...", s.substring(0, 30).replaceAll("\n",
		// "␤"));
	}
}
