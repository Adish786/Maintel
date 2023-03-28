package com.about.mantle.expression.spring;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.about.globe.core.expression.spring.ContextRootObject;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.model.extended.Configs;
import com.about.globe.core.render.CoreRenderUtils;
import com.about.globe.core.testing.GlobeBucket;
import com.about.hippodrome.util.projectinfo.ProjectInfo;
import com.about.mantle.model.extended.docv2.CuratedDocumentEx;
import com.about.mantle.render.image.filter.ImageFilterBuilder;

public class MantleContextRootObject extends ContextRootObject {

	private final Class<ImageFilterBuilder> imageFilter;
	private final Utils utils;

	public MantleContextRootObject(RequestContext requestContext, Map<String, GlobeBucket> tests, Configs configs,
			Map<String, String> messages, CoreRenderUtils renderUtils, ProjectInfo projectInfo) {
		super(requestContext, tests, configs, messages, renderUtils, projectInfo);
		imageFilter = ImageFilterBuilder.class;
		this.utils = new Utils();
	}

	public Class<ImageFilterBuilder> getImageFilter() {
		return imageFilter;
	}

	@Override
	public Utils getUtils() {
		return utils;
	}

	protected static class Utils extends ContextRootObject.Utils {

		/**
		 * Checks whether the provided document is a curated document. This prevents the need to
		 * hardcode the full class package into spel expressions.
		 * @param doc
		 * @return
		 */
		public boolean isCuratedDocument(Object doc) {
			return doc instanceof CuratedDocumentEx;
		}

		/**
		 * A utility method that flattens a list of lists
		 *
		 * @param input
		 *            a list of lists of a generic object
		 * @param <T>
		 *            parameter data type of the given and returned list
		 * @return single list containing all elements from the input lists
		 */
		public <T> List<T> flatMap(List<List<T>> input) {
			return input.stream().flatMap(Collection::stream).collect(Collectors.toList());
		}

		/**
		 * A utility method that sorts the input list by modulus
		 *
		 * @param input
		 *            a list of generic object
		 * @param mod
		 *            an integer that need to be sorted with
		 * @param <T>
		 *            parameter data type of the given and returned list
		 * @return sorted list
		 */
		public <T> List<T> modSort(List<T> input, Integer mod) {

			if (mod == null || input == null)
				return input;

			int size = input.size();
			List<T> result = new ArrayList<>(size);

			int row = -1;
			int rows = (int) Math.ceil(size * 1.0 / mod);
			int i = 0;

			while (result.size() < size) {
				int remainder = i % rows;
				if (remainder == 0) {
					row++;
				}
				int pos = remainder * mod + row;
				if (pos < size) {
					result.add(input.get(pos));
				}
				i++;
			}
			return result;

		}

		/**
		 * A method that partition a list given the start, and end positions of the sublist
		 *
		 * @param input
		 *            the input list
		 * @param start
		 *            the start position of the list (should be less than the input list's size)
		 * @param end
		 *            the end position of the list (should be after the start position)
		 * @param <T>
		 *            data type of the given and returned list
		 * @return a sublist of the given input list. Otherwise it will return <b>null</b> if any of the given
		 *         parameters is null or the start index is greater than or equal the input's size
		 */
		public <T> List<T> subList(List<T> input, Integer start, Integer end) {
			/* Null checks */
			if (input == null || start == null || end == null)
				return null;

			/* Logical checks */
			if (start >= input.size() || start < 0 || end < 0 || end < start)
				return null;

			/* If the end index is greater than list's size, then set it to the end of list */
			end = end > input.size() ? input.size() : end;

			return new ArrayList<>(input.subList(start, end));
		}

		/**
		 * Returns a range of numbers as an iterable.  Useful for creating a `component-list` that repeats
		 * @param start beginning of the range, inclusive
		 * @param end end of the range, inclusive
		 * @return the range as an iterable
		 */
		public Iterable<Integer> range(int start, int end) {

			ArrayList<Integer> answer = new ArrayList<>(end - start + 1);

			for (int c = 0; end - c >= start; c++) {
				answer.add(c, start + c);
			}

			return answer;
		}
		
		/**
		 * Formats the seconds duration to specified format.
		 * @param seconds duration in seconds
		 * @param format the way in which to format the duration
		 * @return time as a string
		 */
		public String formatDurationSeconds(int seconds, String format) {
			return formatDuration(TimeUnit.SECONDS.toMillis(seconds), format);
		}
	}

}
