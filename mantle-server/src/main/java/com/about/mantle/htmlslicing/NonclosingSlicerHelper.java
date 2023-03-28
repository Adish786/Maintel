package com.about.mantle.htmlslicing;

/**
 * This Slicer will _NOT_ close/open "splittable" tags along the slice boundary. "Splittable" tags are
 * specified in `HtmlSlicerConfig`. For example, suppose 'p' and 'ol' are configured as "splittable" tags.
 * Then the behavior of this slicer operates as follows:
 *
 * <p>Here's a list of things to do:<br /><ol><li>thing 1.</li><li>thing 2.</li></ol></p>
 *                                                            ^
 *                                                     [slice boundary]
 *
 * slice 1: <p>Here's a list of things to do:<br /><ol><li>thing 1.</li>
 * slice 2: <li>thing 2.</li></ol></p>
 *
 * Rejoining the slices will give you back the original input.
 *
 * Intentionally package-access because it's intended to be used through an implementation of `HtmlSlicer`.
 */
class NonclosingSlicerHelper extends AbstractSlicerHelper {

	NonclosingSlicerHelper(HtmlSlicerConfig config, String content) {
		super(config, content);
	}

	@Override
	protected void createSlice(boolean inclusive, boolean skip) {
		int offset = 0; // will be used to decide the "end" of the current slice;
		                // also used for going beyond the current index for traversing the content.
		if (inclusive && skip) {
			// This is typically the case when we're mid-copy outside any html tags. For example,
			// endIndex is positioned on some punctuation and the character following is a space.
			// Thus, +1 to include the punctuation, and +X to include the following space characters
			// (whether that is a space, break or nbsp sequence).
			SpaceSequenceCandidate spaceSequence = findSpaceCandidate(endIndex + 1);
			offset = 1 + spaceSequence.getLength();
		} else if (inclusive && !skip) {
			// This is typically the case when we're creating the final slice.
			// Thus, endIndex is probably positioned on the last character in the content.
			// +1 to include the last character.
			offset = 1;
		} else if (!inclusive && skip) {
			// This should never happen.
		} else /* !inclusive && !skip */ {
			// This is typically the case when we're just beyond the end of a splittable tag.
			// Thus, the character immediately to the left of endIndex is '>' (the last character
			// of the closing tag) and endIndex is positioned on a character that should probably
			// go into the next slice.
			// +0 to remain on endIndex for the next slice and exclude it from this one.
		}
		String content = new String(chars, startIndex, endIndex - startIndex + offset);
		result.add(new HtmlSlice(content, charCount, wordCount, null));
		startIndex = endIndex = (endIndex + offset);
		charCount = 0;
		wordCount = 0;
		blockIndex++;
	}

}
