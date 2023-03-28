package com.about.mantle.htmlslicing;

import static org.apache.commons.lang3.ArrayUtils.indexOf;
import static org.apache.commons.lang3.ArrayUtils.reverse;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.about.globe.core.exception.GlobeException;
import com.carrotsearch.hppc.CharHashSet;
import com.carrotsearch.hppc.CharObjectHashMap;
import com.carrotsearch.hppc.CharObjectMap;
import com.carrotsearch.hppc.CharSet;
import com.google.common.collect.ImmutableSet;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

/**
 * Intentionally package-access because it's intended to be used through an implementation of `HtmlSlicer`.
 */
abstract class AbstractSlicerHelper {

	private static final Set<String> HEADING_TAGS = ImmutableSet.of("b", "h1", "h2", "h3", "h4", "h5", "strong");
	private static final Set<String> CLOSABLE_TAGS = ImmutableSet
			.copyOf(split(HtmlCompressor.BLOCK_TAGS_MAX + ",pre,li", ','));
	private static final CharTail NON_SENTENCE_ENDS = CharTail.of("i.e.", "e.g.", "a.k.a.", "mr.", "mrs.", "dr.",
			"u.s.", "vs.", "a.m.", "p.m.", "f.i.t.t.", "i.d.");
	private static final SpaceSequenceCandidate nbsp = new SpaceSequenceCandidate(true, "&nbsp;");
	private static final SpaceSequenceCandidate br = new SpaceSequenceCandidate(true, "<br/>");
	private static final SpaceSequenceCandidate space = new SpaceSequenceCandidate(true, " ");
	private static final SpaceSequenceCandidate noSpace = new SpaceSequenceCandidate(false, "");
	private static final Set<String> selfClosing = new HashSet<>(Arrays.asList("area", "base", "br", "col", "command", "embed", "hr", "img", "input", "keygen", "link", "menuitem", "meta", "param", "source", "track", "wbr"));

	private final HtmlSlicerConfig config;
	protected final char[] chars;

	protected List<HtmlSlice> result = new ArrayList<>();
	protected int blockIndex = 0;
	protected int charCount = 0;
	protected int wordCount = 0;
	protected boolean nextCharStartsWord = true;
	protected int startIndex = 0;
	protected int endIndex = 0;
	protected Deque<String> elementStack = new LinkedList<>();

	AbstractSlicerHelper(HtmlSlicerConfig config, String content) {
		this.config = config;
		this.chars = content.toCharArray();
	}

	List<HtmlSlice> slice() {

		while (endIndex < chars.length) {

			if (chars[endIndex] == '<') {
				nextCharStartsWord = true;

				if (chars[endIndex + 1] != '/') {
					// Start of tag
					String elementName = getElementName(chars, endIndex + 1);
					if (isSplittable(elementName)) {
						fastForwardToAfterEndOfTag();
					} else {
						// Not splittable so try to close block
						if (charCountFits(true) && allSplittable(elementStack)) {
							/* [@mmanashirov] I have a suspicion that this block is _never_ entered because I can't find any
							 * references that suggest "ranges" from HtmlSlicerConfig are being used. When no range is
							 * specified the range is implicitly 0 which causes `charCountFits(true)` to always return false.
							 * 
							 * I am preserving it as a concept for now to err on the side of caution and limit the potential
							 * risk of this change. Maybe it can be removed in the future?
							 */
							createSlice(true, false);
						} else {
							fastForwardToAfterEndOfTag();
						}
					}

					if (!selfClosing.contains(elementName)) {
						elementStack.push(elementName);
					}
				} else {
					// End of tag
					boolean sentenceEnd = isSentenceEnd(endIndex - 1);
					fastForwardToAfterEndOfTag();
					String tag = elementStack.pop();
					if (allSplittable(elementStack) && charCountFits(false) && !isHeadingTag(tag)
							&& !isPunctuation(endIndex) && (sentenceEnd || CLOSABLE_TAGS.contains(tag))) {
						createSlice(false, false);
					}
				}
			} else if (Character.isWhitespace(chars[endIndex])) {
				// White space counts as character and word separation
				nextCharStartsWord = true;
				charCount++;
				endIndex++;
			} else {
				// Everything else is character
				charCount++;
				if (nextCharStartsWord) {
					wordCount++;
					nextCharStartsWord = false;
				}
				if (isPunctuation(endIndex) && findSpaceCandidate(endIndex + 1).isSpace() && allSplittable(elementStack)
						&& charCountFits(false)) {
					createSlice(true, true);
				} else if (chars[endIndex] == '&') {
					// Entity counts as one char so fast forward to end of entity
					int endOfEscapedChar = indexOf(chars, ';', endIndex);
					if (endOfEscapedChar < endIndex || endOfEscapedChar - endIndex > 11) {
						// 11 is a bit of an arbitrary choice.  The idea is that we don't want to match on a
						// semicolon that isn't part of the escape char.  None of the escape chars I found
						// [here](https://www.freeformatter.com/html-entities.html) are > 11.  Some edge cases
						// could still fall through.  This should probably be revisited as throwing an exception
						// isn't great but is better than the server crashing
						throw new GlobeException("Unescaped ampersand found in HTML content.  Unable to process.");
					}
					endIndex = endOfEscapedChar + 1;
				} else {
					endIndex++;
				}
			}
		}

		// Everything else
		endIndex--;
		if (startIndex < endIndex) {
			createSlice(true, false);
		}

		return result;
	}

	/**
	 * @param inclusive
	 *            - If true then char at endIndex _should_ be included in the _current_ slice
	 * @param skip
	 *            - If true then char at endIndex _should not_ be included in _next_ slice
	 */
	protected abstract void createSlice(boolean inclusive, boolean skip);

	private boolean isHeadingTag(String tag) {
		return HEADING_TAGS.contains(tag);
	}

	private boolean isSentenceEnd(int index) {
		for (; index >= 0; index--) {
			// Skip to beginning of &nbsp;
			if (chars[index] == ';' && index >= 5 && chars[index - 5] == '&') {
				index -= 5;
			}
			// Skip over spaces
			if (findSpaceCandidate(index).isSpace()) continue;
			// Non space must be punctuation, if not then not sentence end
			return isPunctuation(index);
		}
		return false;
	}

	private boolean isPunctuation(int index) {
		if (index >= chars.length) return false;

		switch (chars[index]) {
		case '.':
		case '?':
		case '!':
			return !NON_SENTENCE_ENDS.hasTail(chars, index);
		}

		return false;
	}

	protected SpaceSequenceCandidate findSpaceCandidate(final int index) {
		// note: the or statement looks like it is identical but it is not.
		// One is U+0020 : SPACE [SP] and the other is U+00A0 : NO-BREAK SPACE [NBSP]
		if (index < chars.length && (chars[index] == ' ' || chars[index] == ' ')) {
			return space;
		}

		if (nbsp.found(chars, index)) return nbsp;

		if (br.found(chars, index)) return br;

		return noSpace;
	}

	protected boolean isSplittable(String tag) {
		return config.getTags().contains(tag);
	}

	protected boolean allSplittable(Deque<String> elementStack) {
		for (String element : elementStack) {
			if (!isSplittable(element)) return false;
		}
		return true;
	}

	protected void fastForwardToAfterEndOfTag() {
		int index = indexOf(chars, '>', endIndex);
		
		if (index < 0) {
			throw new GlobeException("Failed to find end of tag");
		}
		
		endIndex = index + 1;
	}

	protected boolean charCountFits(boolean useRange) {

		int[] blockSizes = config.getChars();
		if (blockSizes.length <= blockIndex) return false;

		int desiredChars = blockSizes[blockIndex];

		if (!useRange) return charCount >= desiredChars;

		int[] blockRanges = config.getRange();
		int desiredRange = blockRanges.length <= blockIndex ? 0 : blockRanges[blockIndex];

		return desiredChars - desiredRange < charCount && charCount < desiredChars + desiredRange;
	}

	protected String getElementName(char[] chars, int startIndex) {
		for (int endIndex = startIndex; endIndex < chars.length; endIndex++) {
			if (chars[endIndex] == ' ' || chars[endIndex] == '>' || chars[endIndex] == '/') {
				return new String(chars, startIndex, endIndex - startIndex);
			}
		}
		return null;
	}

	/**
	 * Structure that can be used to create a space and time efficient character array tail matcher.
	 */
	private static class CharTail {

		/**
		 * Characters that denote a word boundary that ends matching. For example:
		 * 
		 * <pre>
		 *  CharTail.of("Mr.").hasTail("Hello Mr.") == true;
		 *  CharTail.of("Mr.").hasTail("HelloMr.") == false;
		 * </pre>
		 */
		private final static CharSet wordBoundaries = CharHashSet.from(" ()[]{};<>\"\n".toCharArray());

		private final CharObjectMap<CharTail> children = new CharObjectHashMap<>();

		/**
		 * Create character array tail matcher that will match for all given strings.
		 * 
		 * @param strings
		 *            Strings to match character array tails for
		 * @return Character array tail matcher that will match for all given strings
		 */
		static CharTail of(String... strings) {

			CharTail charTail = new CharTail();

			for (String string : strings) {
				char[] chars = string.toLowerCase().toCharArray();
				reverse(chars);
				charTail.add(chars);
			}

			return charTail;
		}

		/**
		 * Get existing child for given char or create new one if none exists.
		 * 
		 * @param c
		 *            Char to ensure child for.
		 * @return Existing or newly created child for given char.
		 */
		private CharTail ensureChild(char c) {
			if (children.containsKey(c)) {
				return children.get(c);
			} else {
				CharTail child = new CharTail();
				children.put(c, child);
				return child;
			}
		}

		/**
		 * Add character array to this matching structure.
		 * 
		 * @param chars
		 *            Character array to add to this matching structure
		 */
		private void add(char[] chars) {
			add(chars, 0);
		}

		/**
		 * Add character array, from given index, to this matching structure.
		 * 
		 * @param chars
		 *            Character array to add to this matching structure
		 * @param fromIndex
		 *            Index into character array to add from
		 */
		private void add(char[] chars, int fromIndex) {

			if (chars.length <= fromIndex) return;

			// Ensure child for first char then add rest of the chars to it
			ensureChild(chars[fromIndex]).add(chars, fromIndex + 1);
		}

		/**
		 * Check if given character array, at given index, ends with any words used to construct {@link CharTail}.
		 * 
		 * @param chars
		 *            Character array to match on.
		 * @param fromIndex
		 *            Index in given character array to match at.
		 * @return true if given character array, at given index, ends with any words used to construct this
		 *         {@link CharTail}, false otherwise
		 */
		boolean hasTail(char[] chars, int fromIndex) {

			if (chars == null || chars.length == 0 || fromIndex < 0 || fromIndex >= chars.length) {
				return false;
			}

			if (fromIndex >= 0) {
				char tail = Character.toLowerCase(chars[fromIndex]);

				if (children.containsKey(tail)) {
					// Beginning of chars, so acts like word boundry
					if (fromIndex == 0 && children.get(tail).children.isEmpty()) return true;
					return children.get(tail).hasTail(chars, fromIndex - 1);
				}

				if (children.isEmpty()) {
					return wordBoundaries.contains(tail);
				}
			}

			return false;
		}
	}

}
