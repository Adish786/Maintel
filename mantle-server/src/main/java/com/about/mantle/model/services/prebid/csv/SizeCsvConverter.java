package com.about.mantle.model.services.prebid.csv;

import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.SIZE;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import org.apache.commons.lang3.StringUtils;

/**
 * Converts string value to List<Integer>, e.g. "300x250" -> [300, 250].
 */
public class SizeCsvConverter extends AbstractBeanField<List<Integer>, Integer> {

	@Override
	protected List<Integer> convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
		List<Integer> answer = null;
		if (StringUtils.isNotBlank(value)) {
			String[] parts = StringUtils.split(value, 'x');
			if (parts == null || parts.length != 2) {
				throw new CsvDataTypeMismatchException("Value '" + value + "' is not valid for '" + SIZE + "' column.");
			}
			try {
				answer = ImmutableList.of(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]));
			} catch (Exception e) {
				throw new CsvDataTypeMismatchException("Value '" + value + "' is not valid for '" + SIZE + "' column.");
			}
		}
		return answer;
	}

}