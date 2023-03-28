package com.about.mantle.htmlslicing;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.Arrays;
import java.util.Set;

import com.about.globe.core.cache.hash.CollectionHasher;
import com.google.common.collect.ImmutableSet;

/**
 * This is a cut and paste and rename of Selene's ContentBlockConfig class
 */
public class HtmlSlicerConfig {

	private static final String DEFAULT_CONFIG = "chars:500;tags:p,ul,ol;";

	private int[] chars;
	private Set<String> tags;
	private int[] range;
	private boolean nonclosing = false;

	public HtmlSlicerConfig(String configString) {
		if (configString != null) splitBlockConfigs(split(configString, ';'));
		ensureValues();
	}

	public HtmlSlicerConfig(int[] chars, Set<String> tags, int[] range) {
		this(chars, tags, range, false);
	}

	/**
	 * @param chars       each element in the array specifies the minimum number of characters
	 *                    to include in the slice corresponding to the index of the element;
	 *                    when there are more slices than elements the final slice will contain
	 *                    the rest of the content
	 *
	 * @param tags        tags that can be split at the slice boundary
	 *
	 * @param range       each element in the array specifies the amount of "give" (+/-) for
	 *                    the corresponding element in chars
	 *
	 * @param nonclosing  determines whether to use `TagClosingSlicer` or `NonclosingSlicer`;
	 *                    `TagClosingSlicer` will open/close splittable tags at the slice boundaries;
	 *                    `NonclosingSlicer` will leave the content unchanged at the slice boundaries.
	 */
	public HtmlSlicerConfig(int[] chars, Set<String> tags, int[] range, boolean nonclosing) {
		this.chars = chars;
		this.tags = tags;
		this.range = range;
		this.nonclosing = nonclosing;
		ensureValues();
	}

	private void ensureValues() {
		if (chars == null) chars = new int[0];
		if (tags == null) tags = ImmutableSet.of();
		if (range == null) range = new int[0];
	}

	private void splitBlockConfigs(String[] configParams) {
		for (String param : configParams) {
			String[] keyValuePair = split(param, ':');
			if (keyValuePair.length == 2) {
				String key = keyValuePair[0];
				String value = keyValuePair[1];
				if ("chars".equals(key)) {
					this.chars = Arrays.asList(split(value, ',')).stream().mapToInt(Integer::parseInt).toArray();
				} else if ("tags".equals(key)) {
					this.tags = ImmutableSet.copyOf(split(value, ','));
				} else if ("range".equals(key)) {
					this.range = Arrays.asList(split(value, ',')).stream().mapToInt(Integer::parseInt).toArray();
				}
			} else if (keyValuePair.length == 1) {
				String key = keyValuePair[0];
				if ("nonclosing".equals(key)) {
					this.nonclosing = true;
				}
			}
		}
	}

	public int[] getChars() {
		return chars;
	}

	public Set<String> getTags() {
		return tags;
	}

	public int[] getRange() {
		return range;
	}

	public boolean isNonclosing() {
		return nonclosing;
	}

	public void setNonclosing(boolean nonclosing) {
		this.nonclosing = nonclosing;
	}

	public static HtmlSlicerConfig getDefaultConfig() {
		return new HtmlSlicerConfig(DEFAULT_CONFIG);
	}

	public static HtmlSlicerConfig emptyIfNull(HtmlSlicerConfig htmlSlicerConfig) {
    	if(htmlSlicerConfig == null) return new HtmlSlicerConfig(new int[0], ImmutableSet.of(), new int[0]);
    	return htmlSlicerConfig;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(chars);
		result = prime * result + Arrays.hashCode(range);
        result = prime * result + ((tags == null) ? 0 : new CollectionHasher().hash(tags));
		result = prime * result + (nonclosing ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		HtmlSlicerConfig other = (HtmlSlicerConfig) obj;
		if (!Arrays.equals(chars, other.chars)) return false;
		if (!Arrays.equals(range, other.range)) return false;
		if (tags == null) {
			if (other.tags != null) return false;
		} else if (!tags.equals(other.tags)) return false;
		if (nonclosing != other.nonclosing) return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (chars.length > 0) sb.append("chars:").append(join(chars, ',')).append(";");
		if (!tags.isEmpty()) sb.append("tags:").append(join(tags, ',')).append(";");
		if (range.length > 0) sb.append("range:").append(join(range, ',')).append(";");
		sb.append("nonclosing:").append(nonclosing).append(";");
		return sb.toString();
	}

}
