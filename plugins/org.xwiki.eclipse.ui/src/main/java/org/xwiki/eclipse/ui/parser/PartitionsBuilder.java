package org.xwiki.eclipse.ui.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xwiki.rendering.wikimodel.IWemConstants;
import org.xwiki.rendering.wikimodel.WikiStyle;

/**
 * Partition builder tool to calculate partitions in the XWiki Syntax 2.0
 * This code is called from the XWiki+Velocity parser written in jjtree
 */
public class PartitionsBuilder {
	/* This is used to keep track of the spans inside partitions. */
	List<Span> spans = new ArrayList<Span>();

	/* A stack to keep track of nested partitions. */
	private Stack<Partition> stack = new Stack<Partition>();

	/* The list of partitions corresponding to the parsed input. */
	private List<Partition> partitions = new ArrayList<Partition>();

	public void begin(Partition.Type type, Map<String, Object> attributes,
			int offset) {

		/*
		 * Here we are in the situation of a nested partition. A previously
		 * opened partition has not yet been closed, and a new one just starts.
		 * In this case we emit the close the current partition (without
		 * removing it from the stack, so that it will be resumed when the
		 * nested partition will be closed.
		 */
		if (!stack.isEmpty()) {
			Partition currentPartition = stack.peek();

			/*
			 * Emit the partition ending at the current offset, where the nested
			 * partition begins.
			 */
			// This is currently commented to allow partitions inside partitions
			/*
			  emitPartition(currentPartition.getType(),
			 
					currentPartition.getBeginOffset(), offset,
					currentPartition.getAttributes());
			*/
		}

		/* Begin the new partition. */
		Partition partition = new Partition();
		partition.setType(type);
		partition.setBeginOffset(offset);
		partition.setAttributes(attributes);
		stack.push(partition);
	}
	
	public void end(int offset) {
	   Partition currentPartition = stack.peek();
	   end(currentPartition.getType(), offset);
	
	}
	public void end(Partition.Type type, int offset) {
		Partition currentPartition = stack.pop();

		/*
		 * Check that the partition to be ended is the same of the current one
		 * (on the top of the stack). If this is not the case, then there is
		 * some problem in the input (i.e., an opened macro that has not been
		 * closed)
		 */
		if (!currentPartition.getType().equals(type)) {
			/*
			 * Remove from the stack all the partitions that don't match with
			 * the current one.
			 */
			while (!stack.peek().getType().equals(type)) {
				stack.pop();
			}

			/* Also remove the partition that matches with the current one. */
			stack.pop();
		}

		currentPartition.setEndOffset(offset);
		Map<String, Object> attributes = currentPartition.getAttributes();
		if (!spans.isEmpty()) {
			/*
			 * Copy the spans array in order to freeze it, because it is
			 * modified
			 */
			attributes.put(
					"SPANS",
					normalizeSpans(spans, currentPartition.getBeginOffset(),
							currentPartition.getEndOffset()));

		}
		emitPartition(currentPartition.getType(),
				currentPartition.getBeginOffset(),
				currentPartition.getEndOffset(), attributes);

		/*
		 * Resume the enclosing partition, by setting its begin offset to the
		 * current offset.
		 */
		if (!stack.isEmpty()) {
			// This is currently commented to allow partitions inside partitions
			// stack.peek().setBeginOffset(offset);
		}

		/* Clear the spans for the current partition. */
		spans.clear();
	}

	private void emitPartition(Partition.Type type, int beginOffset,
			int endOffset, Map<String, Object> attributes) {

		/* Don't emit empty or degenerate partitions. */
		if (beginOffset >= endOffset) {
			return;
		}

		Partition partition = new Partition();
		partition.setType(type);
		partition.setBeginOffset(beginOffset);
		partition.setEndOffset(endOffset);
		partition.setAttributes(attributes);
		partitions.add(partition);
	}

	public List<Partition> getPartitions() {
		Collections.sort(partitions);
		return partitions;
	}

	public void onFormat(WikiStyle style, int formatTokenStartOffset,
			int formatTokenEndOffset) {			
		if (spans.isEmpty()) {
			Span span = new Span();
			span.setBegin(formatTokenStartOffset);
			span.setEnd(-1);
			span.setStyles(Arrays.asList(style));
			spans.add(span);
		} else {
			Span currentSpan = spans.get(spans.size() - 1);
			currentSpan.setEnd(formatTokenEndOffset);

			List<WikiStyle> currentStyles = new ArrayList<WikiStyle>(
					currentSpan.getStyles());
			if (currentStyles.contains(style)) {
				currentStyles.remove(style);
			} else {
				currentStyles.add(style);
			}

			if (!currentStyles.isEmpty()) {
				Span span = new Span();
				span.setBegin(formatTokenEndOffset);
				span.setEnd(-1);
				span.setStyles(currentStyles);
				spans.add(span);
			}
		}
	}

	private List<Span> normalizeSpans(List<Span> spans,
			int partitionBeginOffset, int partitionEndOffset) {
		List<Span> result = new ArrayList<Span>();

		int partitionLength = partitionEndOffset - partitionBeginOffset;

		for (Span span : spans) {
			Span normalizedSpan = new Span();

			int normalizedSpanBegin = span.getBegin() - partitionBeginOffset;
			/* This should really never happen... */
			if (normalizedSpanBegin < 0) {
				normalizedSpanBegin = 0;
			}

			/*
			 * spanEnd can be < 0 because by convention we set end to -1 for
			 * spans that go to the end of partition.
			 */
			int normalizedSpanEnd = span.getEnd() - partitionBeginOffset;
			if (normalizedSpanEnd < 0 || normalizedSpanEnd >= partitionLength) {
				normalizedSpanEnd = partitionLength - 1;
			}

			normalizedSpan.setBegin(normalizedSpanBegin);
			normalizedSpan.setEnd(normalizedSpanEnd);
			normalizedSpan.setStyles(span.getStyles());

			result.add(normalizedSpan);
		}

		return result;
	}
}
