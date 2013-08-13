package org.xwiki.eclipse.ui.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a partition calculated using the XWiki+Velocity parser
 * 
 */
public class Partition implements Comparable {
	public enum Type {
		DOCUMENT, HEADER, PARAGRAPH, LIST, LIST_ITEM, TABLE, TABLE_ROW, QUOT, QUOT_LINE, MACRO, STYLE, PARAMETERS, VELOCITY_FOREACH, VELOCITY_IF, VELOCITY_MACRO
	}

	private int beginOffset;

	private int endOffset;

	private Type type;

	private Map<String, Object> attributes = new HashMap<String, Object>();

	public Partition() {
		// TODO Auto-generated constructor stub
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getBeginOffset() {
		return beginOffset;
	}

	public void setBeginOffset(int beginOffset) {
		this.beginOffset = beginOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		if (attributes != null) {
			for (String k : attributes.keySet()) {
				this.attributes.put(k, attributes.get(k));
			}
		}
	}

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	@Override
	public String toString() {
		return String.format("Region [type=%s, beginOffset=%s, endOffset=%s]",
				type, beginOffset, endOffset);
	}

	/**
	 * Allows to reorder partitions to make sure a parition contain into another ones comes after the one containing it
	 */
	@Override
	public int compareTo(Object arg0) {
		int i = getBeginOffset() - ((Partition)arg0).getBeginOffset();
		if (i==0) 
			return ((Partition)arg0).getEndOffset()-getEndOffset();
		else
			return i;
	}

}
