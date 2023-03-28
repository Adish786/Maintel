package com.about.mantle.utils.dateutils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

	private final Long dateEpoch;
	private final String format;

	public DateUtils(Long dateEpoch, String format) {
		this.dateEpoch = dateEpoch;
		this.format = format;
	}
	/*
	change epoch date to GMT timezone in a given format
	 */
	public String getDateInGivenFormat() {
		return getDateInGivenFormatAndTimeZone("GMT");
	}

	/*
	change epoch date to given timezone in a given format
	 */
	public String getDateInGivenFormatAndTimeZone(String timezone) {
		Date date = new Date(dateEpoch);
		SimpleDateFormat jdf = new SimpleDateFormat(format);
		jdf.setTimeZone(TimeZone.getTimeZone(timezone));
		return jdf.format(date);
	}
}
