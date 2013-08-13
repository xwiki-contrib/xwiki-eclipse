package org.xwiki.eclipse.ui.parser;

import java.util.ArrayList;
import java.util.List;

import org.xwiki.rendering.wikimodel.WikiStyle;

public class Span {
	public int begin;

	public int end;

	
	public List<WikiStyle> styles;

	public Span() {
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public List<WikiStyle> getStyles() {
		return styles;
	}

	public void setStyles(List<WikiStyle> styles) {
		this.styles = new ArrayList<WikiStyle>(styles);
	}

	@Override
	public String toString() {
		return String.format("Span [begin=%s, end=%s, styles=%s]", begin, end,
				styles);
	}

}