package com.about.mantle.model.services.prebid.csv;

import java.util.Map;

import com.about.mantle.model.services.prebid.bidders.BidderCreator;
import com.opencsv.bean.BeanVerifier;
import com.opencsv.exceptions.CsvConstraintViolationException;

/**
 * Used to validate that the line has all the required columns for the specified bidder.
 * 
 * The reason to have this as a BeanVerifier is because exceptions thrown from a BeanVerifier
 * are captured by opencsv and can then be iterated through nicely in a way that preserves the line number.
 * Otherwise there is no easy way to know which line number corresponds to a CsvBean when iterating through the beans.
 * So, for example, if a bean is unusable for reasons downstream it's not obvious how to associate it with a
 * particular line number.
 */
public class BidderSpecificBeanVerifier implements BeanVerifier<PrebidCsvLine> {

	private final Map<String, BidderCreator> bidderCreatorMap;

	public BidderSpecificBeanVerifier(Map<String, BidderCreator> bidderCreatorMap) {
		this.bidderCreatorMap = bidderCreatorMap;
	}

	@Override
	public boolean verifyBean(PrebidCsvLine bean) throws CsvConstraintViolationException {
		String bidder = bean.getBidder();
		BidderCreator bidderCreator = bidderCreatorMap.get(bidder);
		if (bidderCreator == null) {
			throw new CsvConstraintViolationException("Unsupported bidder: " + bidder);
		}
		try {
			bidderCreator.validate(bean); // validate will throw if there are errors
		} catch (Exception e) {
			throw new CsvConstraintViolationException(e.getMessage());
		}
		return true;
	}

}