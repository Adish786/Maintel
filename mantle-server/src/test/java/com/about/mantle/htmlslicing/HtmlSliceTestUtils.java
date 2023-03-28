package com.about.mantle.htmlslicing;

import java.util.List;

import org.junit.Assert;

final class HtmlSliceTestUtils {

	static void assertEquals(String message, List<HtmlSlice> expected, List<HtmlSlice> actual) {
		Assert.assertEquals(message + " - number of slices do not match", expected.size(), actual.size());
		for (int i = 0; i != expected.size(); ++i) {
			Assert.assertEquals(message + " - slice #" + (i+1) + " does not match", expected.get(i), actual.get(i));
		}
	}

}
