package com.about.mantle.model.services.prebid.csv;

import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.AB_TESTS;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

/**
 * Converts AB test specification, e.g. testName1=bucketName1,bucketName2,...;testName2=bucketName3,...
 */
public class ABTestCsvConverter extends AbstractBeanField<Map<String, Set<String>>, String> {

	@Override
	protected Map<String, Set<String>> convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
		Map<String, Set<String>> answer = null;
		if (isNotBlank(value)) {
			answer = new HashMap<>();
			for (String test : split(value, ';')) {
				loadTest(test, answer);
			}
			if (answer.isEmpty()) {
				throw new CsvDataTypeMismatchException("Value '" + value + "' is not valid for '" + AB_TESTS + "' column.");
			}
		}
		return answer;
	}

	private static void loadTest(String test, Map<String, Set<String>> accumulator) throws CsvDataTypeMismatchException {
		if (isNotBlank(test)) {
			String[] testParts = split(test, '=');
			if (testParts.length != 2 || isBlank(testParts[0]) || isBlank(testParts[1])) {
				throw new CsvDataTypeMismatchException("Value '" + test + "' is not valid for '" + AB_TESTS + "' column.");
			}
			String testName = testParts[0];
			Set<String> buckets = accumulator.getOrDefault(testName, new HashSet<>());
			for (String bucket : split(testParts[1], ',')) {
				if (isNotBlank(bucket)) {
					buckets.add(bucket);
				}
			}
			if (buckets.isEmpty()) {
				throw new CsvDataTypeMismatchException("Value '" + test + "' is not valid for '" + AB_TESTS + "' column.");
			}
			accumulator.put(testName, buckets);
		}
	}

}