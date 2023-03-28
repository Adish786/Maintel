package com.about.mantle.htmlslicing;

import java.util.Deque;

/**
 * This Slicer will close/open "splittable" tags along the slice boundary. "Splittable" tags are specified
 * in `HtmlSlicerConfig`. For example, suppose 'p' and 'ol' are configured as "splittable" tags. Then the
 * behavior of this slicer operates as follows:
 *
 * <p>Here's a list of things to do:<br /><ol><li>thing 1.</li><li>thing 2.</li></ol></p>
 *                                                            ^
 *                                                     [slice boundary]
 *
 * slice 1: <p>Here's a list of things to do:<br /><ol><li>thing 1.</li></ol></p>
 * slice 2: <p class="html-slice"><ol><li>thing 2.</li></ol></p>
 *
 * Rejoining the slices will _NOT_ give you back the original input.
 *
 * Intentionally package-access because it's intended to be used through an implementation of `HtmlSlicer`.
 */
class TagClosingSlicerHelper extends AbstractSlicerHelper {
	private static final String SPLIT_ATTRIBUTES = "class=\"html-slice\"";

	TagClosingSlicerHelper(HtmlSlicerConfig config, String content) {
		super(config, content);
	}

	@Override
	protected void createSlice(boolean inclusive, boolean skip) {
		String content = splitOutBlock(chars, startIndex, inclusive ? endIndex : endIndex - 1, elementStack);
		result.add(new HtmlSlice(content, charCount, wordCount, null));
		// end index one before prepended elements
		startIndex = endIndex = prependElements(chars, skip ? endIndex + 1 : endIndex, elementStack);
		charCount = 0;
		wordCount = 0;
		elementStack.clear();
		blockIndex++;
	}

	/**
	 * @param chars
	 * @param index
	 *            - index to prepend element stack before
	 * @param elementStack
	 * @return index for start of prepended elements
	 */
	private int prependElements(char[] chars, int index, Deque<String> elementStack) {
		int stackLevel = elementStack.size();
		for (String element : elementStack) {
			stackLevel--;
			chars[--index] = '>';
			if (stackLevel == 0) {
				index -= SPLIT_ATTRIBUTES.length();
				System.arraycopy(SPLIT_ATTRIBUTES.toCharArray(), 0, chars, index, SPLIT_ATTRIBUTES.length());
				chars[--index] = ' ';
			}
			index -= element.length();
			System.arraycopy(element.toCharArray(), 0, chars, index, element.length());
			chars[--index] = '<';
		}

		return index;
	}

	/**
	 * @param chars
	 * @param startIndex
	 * @param endIndex
	 *            - end index inclusive
	 * @param elementStack
	 * @return
	 */
	private String splitOutBlock(char[] chars, int startIndex, int endIndex, Deque<String> elementStack) {
		// If no tags to reconstruct just return as is
		if (elementStack.size() == 0) return new String(chars, startIndex, endIndex - startIndex + 1);

		StringBuilder sb = new StringBuilder(endIndex - startIndex + (5 * elementStack.size()));

		sb.append(chars, startIndex, endIndex - startIndex + 1);

		for (String element : elementStack) {
			sb.append("</").append(element).append('>');
		}

		return sb.toString();
	}

}
