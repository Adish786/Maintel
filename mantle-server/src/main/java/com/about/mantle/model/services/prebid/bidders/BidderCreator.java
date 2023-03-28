package com.about.mantle.model.services.prebid.bidders;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;

/**
 * Abstract factory pattern for creating and validating {@link Bidder} objects from a {@link PrebidCsvLine}.
 *
 * Introduced because there was a need to run the validation _separately_ from the creation inside
 * {@link com.about.mantle.model.services.prebid.csv.BidderSpecificBeanVerifier}
 * where the line in the CSV is verified against the {@link Bidder} that will eventually be created
 * from the {@link PrebidCsvLine}. The benefit of running the validation inside the verifier is that
 * validation errors are bound to the exact line number in the CSV which is important for logging purposes.
 */
public interface BidderCreator {

	/**
	 * The name of the {@link Bidder}, e.g. 'Index Exchange', 'Rubicon Project', etc.
	 * 
	 * This is how a {@link PrebidCsvLine} is associated with a {@link BidderCreator}.
	 */
	String name();

	/**
	 * The code of the {@link Bidder}, e.g. 'ix', 'rubicon', etc.
	 * http://prebid.org/dev-docs/bidders.html
	 */
	String code();

	/**
	 * Creates the {@link Bidder} object corresponding to a line in the config.
	 * @param prebidCsvLine   CsvBean object corresponding to a line in the config.
	 */
	Bidder create(PrebidCsvLine prebidCsvLine);

	/**
	 * Validates the {@link PrebidCsvLine} against the {@link Bidder} object.
	 *
	 * Validation errors are raised as {@link GlobeException}. We throw an exception instead of
	 * returning a boolean because that is how we signal to OpenCSV that there is an error on the line.
	 * A {@link com.opencsv.bean.BeanVerifier} that returns false silently filters out a line whereas
	 * throwing an exception prompts the library to treat the line as an error.
	 *
	 * @param prebidCsvLine   CsvBean object corresponding to a line in the config.
	 */
	void validate(PrebidCsvLine prebidCsvLine);

}