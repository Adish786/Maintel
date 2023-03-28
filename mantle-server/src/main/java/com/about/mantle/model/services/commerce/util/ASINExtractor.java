package com.about.mantle.model.services.commerce.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ASINExtractor {

	private static final String ASIN_REGEX = "amazon.com(?:%2F|/)(?:[\\w\\p{IsLatin}\\p{InGreek}-%’®]+(?:%2F|/))?(?:dp|gp(?:%2F|/)product)(?:%2F|/)(?:\\w+(?:%2F|/))?(\\w{10}).*";
	private static final Pattern regexForAsin = Pattern.compile(ASIN_REGEX);
	
	public static String get(String url) {
		Matcher asinMatcher = regexForAsin.matcher(url);
		return asinMatcher.find() ? asinMatcher.group(1) : null;
	}

}
