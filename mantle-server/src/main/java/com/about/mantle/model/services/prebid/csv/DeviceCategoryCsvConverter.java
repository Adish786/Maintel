package com.about.mantle.model.services.prebid.csv;

import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.DEVICE;

import com.about.globe.core.definition.resource.DeviceCategoryMapping;
import com.about.globe.core.http.ua.DeviceCategory;
import com.opencsv.bean.AbstractCsvConverter;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

/**
 * Converts string value to enum, e.g. "pc" -> PERSONAL_COMPUTER.
 */
public class DeviceCategoryCsvConverter extends AbstractCsvConverter {

	@Override
	public DeviceCategory convertToRead(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
		DeviceCategory deviceCategory = DeviceCategoryMapping.get(value);
		if (deviceCategory == null) {
			throw new CsvDataTypeMismatchException("Value '" + value + "' is not valid for '" + DEVICE + "' column.");
		}
		return deviceCategory;
	}

}