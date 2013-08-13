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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.xwiki.eclipse.ui.editors.scanners.rules.BalancedParenthesisRule;
import org.xwiki.eclipse.ui.editors.scanners.rules.RegExRule;
import org.xwiki.eclipse.ui.parser.OffsetCharStream;
import org.xwiki.eclipse.ui.parser.Partition;
import org.xwiki.eclipse.ui.parser.PartitionTokenManager;
import org.xwiki.eclipse.ui.parser.PartitionsBuilder;
import org.xwiki.eclipse.ui.parser.xwiki21.ParseException;
import org.xwiki.eclipse.ui.parser.xwiki21.XWikiScanner;

/**
 * This partition scanner is an advanced XWiki partition scanner which detects XWiki Syntax
 * and additionally parses Velocity (and in the future Groovy) macro blocks to partition
 * velocity and groovy code in order to perform code folding and indentation
 * 
 * This partitionner is not used for syntax highlighting
 * 
 * @version $Id$
 */
public class XWikiAdvancedPartitionScanner implements IPartitionTokenScanner
{
    public static final String XWIKI_HTML = "__xwiki_html";

    public static final String XWIKI_CODE = "__xwiki_code";

    public static final String XWIKI_PRE = "__xwiki_pre";

    public static final String XWIKI_DL = "__xwiki_dl";

    public static final String XWIKI_TABLE = "__xwiki_table";

    public static final String XWIKI_STYLE = "__xwiki_style";

    public static final String VELOCITY = "__velocity";

    public static final String VELOCITY_IF = "__velocityif";
    
    public static final String VELOCITY_FOREACH = "__velocityforeach";

    public static final String VELOCITY_MACRO = "__velocitymacro";

    public static final String GROOVY = "__groovy";

    public static final String XWIKI_DEFAULT = "__xwikidefault";

    public static final String[] ALL_PARTITIONS = {XWIKI_HTML, XWIKI_CODE, XWIKI_PRE, XWIKI_DL, XWIKI_TABLE,
    XWIKI_STYLE, VELOCITY, VELOCITY_IF, VELOCITY_FOREACH, VELOCITY_MACRO, GROOVY, XWIKI_DEFAULT};
    
    protected List<Partition> partitions;
    protected int offset;
    protected int position;
    protected int length;
    protected Partition currentPartition;
    
    protected Token defaultToken;
    protected Token groovyToken;
    protected Token velocityToken;
    protected Token velocityIfToken;
    protected Token velocityForeachToken;
    protected Token velocityMacroToken;
    protected Token eofToken;
    
    public XWikiAdvancedPartitionScanner()
    {
    	defaultToken = new Token(XWIKI_DEFAULT);
    	groovyToken = new Token(GROOVY);
    	velocityToken = new Token(VELOCITY);
    	velocityIfToken = new Token(VELOCITY_IF);
    	velocityForeachToken = new Token(VELOCITY_FOREACH);
    	velocityMacroToken = new Token(VELOCITY_MACRO);
    }
    
    public List<Partition> getPartitions() {
    	return partitions;
    }

	@Override
	public void setRange(IDocument document, int offset, int length) {
		// TODO Auto-generated method stub
		String content = document.get();
		parse(content, offset);
	}
	
	public void parse(String content, int offset) {
		OffsetCharStream offsetCharStream = new OffsetCharStream(
				new InputStreamReader(new ByteArrayInputStream(content.getBytes())));

		PartitionTokenManager regionTokenManager = new PartitionTokenManager(
				offsetCharStream);

		XWikiScanner xwikiScanner = new XWikiScanner(regionTokenManager);
		PartitionsBuilder partitionsBuilder = new PartitionsBuilder();
		try {
			xwikiScanner.parse(partitionsBuilder);
			partitions = partitionsBuilder.getPartitions();
			this.offset = offset;
			this.position = 0;
			this.length = offset + length;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void dumpPartitions(String content) {
		System.out.format("\nDumping:\n");
		for (Partition region : partitions) {
			try {
			System.out.format("%s (%d-%d) '%s' %s\n", region.getType(), region
					.getBeginOffset(), region.getEndOffset(),
					shorten(content.substring(region.getBeginOffset(),
							(region.getEndOffset()>=content.length()) ? content.length() - 1 : region.getEndOffset())),
					region.getAttributes() != null ? region.getAttributes()
							: "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
		
	private String shorten(String s) {
		// if (s.length() < 30) {
		return s.replaceAll("\n", "␤");
		// }

		// return String.format("%s...", s.substring(0, 30).replaceAll("\n",
		// "␤"));
	}

	@Override
	public IToken nextToken() {
	    for (int i=position;i<partitions.size();i++) {
	       Partition partition = partitions.get(i);
	       if (partition.getBeginOffset()>=offset) {
	    	   offset = (i<partitions.size()-1) ? partitions.get(i+1).getBeginOffset() : partition.getEndOffset();
	    	   if (offset>partition.getBeginOffset()) {
	    		   currentPartition = partition;
	    		   position = i+1;
	    		   // System.out.println("Sending next token from partition " + partition.toString() + " with position " + partition.getBeginOffset() + " " + offset);
	    		   if (partition.getType()==Partition.Type.MACRO) {
	    			   String macroName = (String) partition.getAttribute("NAME");
	    			   if (macroName!=null && macroName.equalsIgnoreCase("groovy"))
	    				   return groovyToken;
	    			   else if (macroName!=null && macroName.equalsIgnoreCase("velocity"))
	    				   return velocityToken;
	    			   else
	    				   return defaultToken;
	    		   } else if (partition.getType()==Partition.Type.VELOCITY_FOREACH) {
	    			   return velocityForeachToken;     	 	    		
	    		   } else if (partition.getType()==Partition.Type.VELOCITY_IF) {
	    			   return velocityIfToken;
	    		   } else if (partition.getType()==Partition.Type.VELOCITY_MACRO) {
	    			   return velocityMacroToken;
	    		   } else {
	    			   return defaultToken;
	    		   }
	    	   }
	    	}
	    }
	    return Token.EOF;
	}

	@Override
	public int getTokenOffset() {
		// TODO Auto-generated method stub
		// System.out.println("Current token offset is " + currentPartition.getBeginOffset());
		return currentPartition.getBeginOffset();
	}

	@Override
	public int getTokenLength() {
		// System.out.println("Current token length is " + (offset - currentPartition.getBeginOffset()));
		return offset - currentPartition.getBeginOffset();
	}

	@Override
	public void setPartialRange(IDocument document, int offset, int length,
			String contentType, int partitionOffset) {
	    setRange(document, offset, length);
	}
}
