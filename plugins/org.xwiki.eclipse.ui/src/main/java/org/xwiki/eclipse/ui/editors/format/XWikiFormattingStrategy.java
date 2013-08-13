package org.xwiki.eclipse.ui.editors.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xwiki.eclipse.ui.editors.scanners.XWikiAdvancedPartitionScanner;
import org.xwiki.eclipse.ui.parser.Partition;

/**
 * This class manages XWiki Velocity Indentation
 * 
 * Only velocity code is handled with only #macro, #foreach, #if, #else, #end tags 
 * making indentation happen
 * 
 * @author ludovic
 *
 */
public class XWikiFormattingStrategy extends DefaultFormattingStrategy
{
    private static final String lineSeparator = System.getProperty("line.separator");
    private static final String indentationString = "  ";
    private static final String BEGIN_VELOCITY = "{{velocity}}" + lineSeparator;
    private static final String END_VELOCITY = lineSeparator + "{{/velocity}}";
    
    private String initialIndentation = "";
	private boolean velocityOnly = false;
    
    public XWikiFormattingStrategy(boolean velocityOnly) {
    	this.velocityOnly  = velocityOnly;
    }
    public void formatterStarts(String initialIndentation)
    {
    	this.initialIndentation = initialIndentation;
    }
  
    public String format(String content, 
        boolean isLineStart, 
        String indentation, 
        int[] positions)
    {	
    	ArrayList<Integer> indents = new ArrayList<Integer>();
    	
    	// we start formatting by trimming white spaces at the beginning of lines
    	content = trimContent(content, initialIndentation);
        
    	// if we are handling some velocity content we need to wrap it with velocity macros
    	// before passing it to the parser
    	if (velocityOnly) {
        	content =  BEGIN_VELOCITY + content + END_VELOCITY;
        }

    	XWikiAdvancedPartitionScanner scanner = new XWikiAdvancedPartitionScanner();
        scanner.parse(content, 0);
        // debugging
        // scanner.dumpPartitions(content);
    	List<Partition> partitions = scanner.getPartitions(); 
          	
    
        for (int i=0;i<partitions.size();i++) {
        	Partition partition = partitions.get(i);
        	// System.out.println("Handling partition " + i + " " + partition.getType() + " begin: " + partition.getBeginOffset() + " end: " + partition.getEndOffset());
        	if (partition.getType().equals(Partition.Type.VELOCITY_FOREACH) 
            		|| partition.getType().equals(Partition.Type.VELOCITY_IF) 
            		|| partition.getType().equals(Partition.Type.VELOCITY_MACRO)) {
        		// System.out.println("In Handling partition " + i + " " + partition.getType());	
            	addIndentedContent(indents, content, partition.getBeginOffset(), partition.getEndOffset());
            }
        }
        
        // we sort indent otherwise we would loose track of positions when inserting indentations
        Collections.sort(indents);
        
        // Now insert all the indents
        // we need to keep track of indentations we have already inserted
        int totalOffset = 0;
        for (int indent : indents) {
        	String content2 = content.substring(0, indent + totalOffset)
        			   + indentationString + content.substring(indent + totalOffset);
        	content = content2;
        	totalOffset += indentationString.length();
        }
         
        if (velocityOnly) {
            // we need to cleanup the velocity tag
        	content =  content.substring(BEGIN_VELOCITY.length());
        	int i = content.lastIndexOf(END_VELOCITY);
        	if (i!=-1)
          	 content =  content.substring(0, i);
        }
        
        return content;    
    }

    /**
     * Add a marker where to indent content. We cannot indent right away because otherwise, 
     * for the next partitions we would have changed positions
     * 
     * @param substring
     * @param indentation
     */
	private void addIndentedContent(ArrayList<Integer> indents, String content, int begin, int end) {
		int pos = begin;
		
		// pass the first line separator as we don't want to indent the first line
		pos = content.indexOf(lineSeparator, pos + 1);
		if (pos!=-1 && pos<end) {
			while (true) {
				int prevPos = pos + lineSeparator.length();
				pos = content.indexOf(lineSeparator, pos + 1);
				if (pos!=-1 && pos<end) {
					indents.add(prevPos);
				} else {
					// if the last line is not starting with # it means the #end section is not on it's own line
					// in this case we still want to indent
					if (content.charAt(prevPos) != '#')
						indents.add(prevPos);					
					break;
				}
			}
		}
		
		// dumpIndents(indents, content);
    }
	
	private void dumpIndents(ArrayList<Integer> indents, String content) {
		System.out.println("Dumping indents");
		for (int indent : indents) {
			System.out.println("Indent: " + indent + " " + content.charAt(indent));
		}
	}
	
	  /**
     * Add indented content
     * 
     * @param content
     * @param initialIndentation
     */
	private String trimContent(String content, String initialIndentation) {
		StringBuffer newContent = new StringBuffer();
		
		String[] lines = content.split(lineSeparator);
		for (int i=0;i<lines.length;i++) {
			newContent.append(initialIndentation);	
			newContent.append(lines[i].trim());
			if (i!=lines.length-1) 
   			   newContent.append(lineSeparator);			
		}
		return newContent.toString();
	}
}
