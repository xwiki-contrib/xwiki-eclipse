package org.xwiki.eclipse.ui.parser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.xwiki.eclipse.ui.parser.xwiki21.ParseException;
import org.xwiki.eclipse.ui.parser.xwiki21.XWikiScanner;
import org.xwiki.velocity.internal.util.VelocityBlock.VelocityType;
import org.xwiki.velocity.internal.util.VelocityParser;
import org.xwiki.velocity.internal.util.VelocityParserContext;

public class Main2 {

	/**
	 * This is the main method.
	 * 
	 * @param args
	 *            arguments.
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ParseException, IOException {
		System.out.println("Hello");
		InputStream is = Main2.class.getResourceAsStream("/test2.xwiki");
		StringBuffer sb = new StringBuffer();
		byte[] buffer = new byte[4096];
		int read;
		while ((read = is.read(buffer)) == 4096) {
			sb.append(new String(buffer));
		}
		sb.append(new String(Arrays.copyOf(buffer, read)));

		String text = sb.toString();

    	
    	PartitionsBuilder partitionsBuilder = new PartitionsBuilder();
    	char[] array = text.toCharArray();
        
		StringBuffer velocityBlock = new StringBuffer();
        VelocityParserContext context = new VelocityParserContext();
        VelocityParser velocityParser = new VelocityParser();
        int i = 0;
        
        for (; i < array.length;) {
            char c = array[i];

            context.setType(null);

            velocityBlock.setLength(0);

        	// System.out.println("Char: " + c);
        	
            try {
                if (c == '#') {
                	int startOffset = i;
                    i = velocityParser.getKeyWord(array, i, velocityBlock, context);
                     // velocityParser.
                    System.out.println("Found keyword" + i);
                    System.out.println(velocityBlock);

                    if (velocityBlock.toString().startsWith("#if")) { 
                    	partitionsBuilder.begin(Partition.Type.VELOCITY_IF, null, startOffset);
                    }
                    else if(velocityBlock.toString().startsWith("#foreach")) {
                    	partitionsBuilder.begin(Partition.Type.VELOCITY_FOREACH, null, startOffset);
                    }
                    else if(velocityBlock.toString().startsWith("#foreach")) {
                    	partitionsBuilder.begin(Partition.Type.VELOCITY_MACRO, null, startOffset);
                    }
                    else if(velocityBlock.toString().startsWith("#end")) {
                    	partitionsBuilder.end(startOffset + 4);
                    }
                    
                } else if (c == '$') {
                    i = velocityParser.getVar(array, i, velocityBlock, context);
                    System.out.println("Found variable" + i);
                    System.out.println(velocityBlock);
                } else if (c == '\\') {
                    System.out.println("Found line break" + i);
  
                    if (array.length > i + 1) {
                        char escapedChar = array[i + 1];

                        if (escapedChar == '\\') {
                            c = escapedChar;
                            i++;
                        } else {
                            int newI = i + 1;
                            if (escapedChar == '#') {
                                newI = velocityParser.getKeyWord(array, newI, velocityBlock, context);
                                
                                System.out.println("Found keyword2" + newI);
                                System.out.println(velocityBlock);
                                  
                                
                            } else if (escapedChar == '$') {
                                newI = velocityParser.getVar(array, newI, velocityBlock, context);
                            }

                            if (context.getType() != VelocityType.COMMENT) {
                                c = escapedChar;
                                i++;
                            }

                            context.setType(null);
                        }
                    }
                } else {
                	i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                i++;
            }
            
            System.out.println("next");
            // i++;
        }

        System.out.format("\nDumping:\n");
		for (Partition region : partitionsBuilder.getPartitions()) {
			System.out.format("%s (%d-%d) '%s' %s\n", region.getType(), region
					.getBeginOffset(), region.getEndOffset(),
					shorten(sb.substring(region.getBeginOffset(),
							region.getEndOffset())),
					region.getAttributes() != null ? region.getAttributes()
							: "");
		}

		
		/*
		OffsetCharStream offsetCharStream = new OffsetCharStream(
				new InputStreamReader(new ByteArrayInputStream(text.getBytes())));

		PartitionTokenManager regionTokenManager = new PartitionTokenManager(
				offsetCharStream);

		Parser velScanner = new Parser(regionTokenManager);
		PartitionsBuilder partitionsBuilder = new PartitionsBuilder();
		velScanner.parse(partitionsBuilder);

		System.out.format("\nDumping:\n");
		for (Partition region : partitionsBuilder.getPartitions()) {
			System.out.format("%s (%d-%d) '%s' %s\n", region.getType(), region
					.getBeginOffset(), region.getEndOffset(),
					shorten(sb.substring(region.getBeginOffset(),
							region.getEndOffset())),
					region.getAttributes() != null ? region.getAttributes()
							: "");
		}
		*/
	}

	public static String shorten(String s) {
		// if (s.length() < 30) {
		return s.replaceAll("\n", "␤");
		// }

		// return String.format("%s...", s.substring(0, 30).replaceAll("\n",
		// "␤"));
	}
}
