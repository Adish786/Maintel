package com.about.mantle.model.services.prebid.bidders;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;

/**
 * All {@link BidderCreator} subtypes should extend this to ensure that `create` implicitly calls `validate`
 * for safety reasons. A method is also provided for ensuring that all error messages have a consistent format.
 */
abstract class AbstractBidderCreator implements BidderCreator {

	private final String name;
	private final String code;

	AbstractBidderCreator(String name, String code) {
		this.name = name;
		this.code = code;
	}

	protected abstract Bidder prevalidatedCreate(PrebidCsvLine prebidCsvLine);

	@Override
	public final Bidder create(PrebidCsvLine prebidCsvLine) {
		validate(prebidCsvLine);
		return prevalidatedCreate(prebidCsvLine);
	}

	@Override
	public final String name() {
		return name;
	}

	@Override
	public final String code() {
		return code;
	}

	/**
	 * Validates that a string column value is not missing.
	 * @param prebidCsvLine
	 * @param columnName
	 * @param columnValue
	 */
	protected static void validateNotMissing(PrebidCsvLine prebidCsvLine, String columnName, String columnValue) {
		if (isBlank(columnValue)) {
			throw new GlobeException(errorMessage(prebidCsvLine, columnName, "value missing"));
		}
	}

	/**
	 * Validates that a collection column value is not missing.
	 * @param prebidCsvLine
	 * @param columnName
	 * @param columnValue
	 */
	protected static void validateNotMissing(PrebidCsvLine prebidCsvLine, String columnName, Collection<?> columnValue) {
		if (isEmpty(columnValue)) {
			throw new GlobeException(errorMessage(prebidCsvLine, columnName, "value missing"));
		}
	}

	/**
	 * Validates that a string column value is a number.
	 * @param prebidCsvLine
	 * @param columnName
	 * @param columnValue
	 */
	protected static void validateIsNumber(PrebidCsvLine prebidCsvLine, String columnName, String columnValue) {
		validateNotMissing(prebidCsvLine, columnName, columnValue);
		try {
			Integer.valueOf(columnValue);
		} catch (Exception e) {
			throw new GlobeException(errorMessage(prebidCsvLine, columnName, "value '" + columnValue + "' is not a number"), e);
		}
	}

	/**
	 * Generate a consistently formatted error message.
	 * @param prebidCsvLine    the line associated with the error
	 * @param columnName       the column associated with the error
	 * @param reason           the reason for the error
	 */
	protected static String errorMessage(PrebidCsvLine prebidCsvLine, String columnName, String reason) {
		String deviceString = prebidCsvLine.getDeviceCategories().stream().map(Objects::toString).collect(Collectors.joining(", "));
		StringBuilder sb = new StringBuilder()
			.append("Bidder [").append(prebidCsvLine.getBidder()).append("], ")
			.append("Device [").append(deviceString).append("], ")
			.append("Slot [").append(prebidCsvLine.getSlot()).append("], ")
			.append("Column [").append(columnName).append("], ")
			.append("Reason [").append(reason).append("]");
		return sb.toString();
	}

}