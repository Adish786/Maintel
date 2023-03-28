package com.about.mantle.model.services.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.model.services.BusinessOwnedVerticalDataService;
import com.about.mantle.model.services.PrebidConfigurationService;
import com.about.mantle.model.services.prebid.BidderEntry;
import com.about.mantle.model.services.prebid.PrebidConfiguration;
import com.about.mantle.model.services.prebid.bidders.Bidder;
import com.about.mantle.model.services.prebid.bidders.BidderCreator;
import com.about.mantle.model.services.prebid.csv.BidderSpecificBeanVerifier;
import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.opencsv.bean.BeanVerifier;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvBasedPrebidConfigurationService implements PrebidConfigurationService {

	private static final Logger logger = LoggerFactory.getLogger(CsvBasedPrebidConfigurationService.class);

	/**
	 * This logger is reserved for CSV parsing errors to give stakeholders the ability to isolate errors
	 * in the config file from all other globe errors.
	 */
	private static final Logger prebidConfigLogger = LoggerFactory.getLogger("PrebidConfigurationErrors");

	private final Map<String, BidderCreator> bidderCreatorMap;
	private final BusinessOwnedVerticalDataService bovdService;
	private final String filepath;
	private final BeanVerifier<PrebidCsvLine> beanVerifier;

	public CsvBasedPrebidConfigurationService(Map<String, BidderCreator> bidderCreatorMap,
	                                          BusinessOwnedVerticalDataService bovdService,
	                                          String filepath, BeanVerifier<PrebidCsvLine> beanVerifier) {
		this.bidderCreatorMap = bidderCreatorMap;
		this.bovdService = bovdService;
		this.filepath = filepath;
		this.beanVerifier = beanVerifier;
	}

	@Override
	public PrebidConfiguration getConfiguration() {
		PrebidConfiguration.Builder configBuilder = new PrebidConfiguration.Builder();
		byte[] bytes = bovdService.getResource(filepath);
		try (InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(bytes))) {
			CsvToBean<PrebidCsvLine> csvToBeans = new CsvToBeanBuilder<PrebidCsvLine>(reader)
				.withThrowExceptions(false) // allow exceptions to accumulate and then log them as errors at the end
				.withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
				.withType(PrebidCsvLine.class)
				.withVerifier(beanVerifier)
				.withVerifier(new BidderSpecificBeanVerifier(bidderCreatorMap))
				.build();
			Iterator<PrebidCsvLine> iterator = csvToBeans.iterator();
			while (iterator.hasNext()) {
				try {
					PrebidCsvLine prebidCsvLine = iterator.next();
					BidderCreator bidderCreator = bidderCreatorMap.get(prebidCsvLine.getBidder());
					if (bidderCreator != null) { // Should never be null by virtue of BidderSpecificBeanVerifier
						Bidder bidder = bidderCreator.create(prebidCsvLine);
						BidderEntry bidderEntry = BidderEntry.from(prebidCsvLine, bidder);
						configBuilder.addBidderEntry(bidderEntry);
					}
				} catch (Exception e) {
					logger.error("Failed to add bidder entry: ", e);
				}
			}
			if (!csvToBeans.getCapturedExceptions().isEmpty()) {
				// log one error to general globe logs
				logger.error("Encountered {} error(s) in the prebid configuration file. See '{}' log for details.",
					csvToBeans.getCapturedExceptions().size(), prebidConfigLogger.getName());
			}
			for (CsvException e : csvToBeans.getCapturedExceptions()) {
				// log each error to the dedicated config log
				prebidConfigLogger.error("Prebid CSV Line {} Error: {}", e.getLineNumber(), e.getMessage());
			}
		} catch (Exception e) {
			logger.error("Failed to load the prebid configuration.", e);
		}
		PrebidConfiguration prebidConfiguration = configBuilder.build();
		if (prebidConfiguration.getBidderEntries().isEmpty()) {
			/**
			 * Why throw here instead of returning an empty config?
			 * An empty config implies that something is wrong with the config file.
			 * If we returned an empty config then it could be mistaken as intentional and that is what would get cached in redis.
			 * We would prefer not to cache an empty config in redis because redis can act as a safety net. That is, for cache refreshes during runtime,
			 * the object in redis is being returned and then the cache is being refreshed asynchronously. Since this would throw the refresh would fail
			 * and you'd still be getting the same object from Redis (while it's there for 3ish days). That said, every call would try to refresh the cache
			 * and fail until the config is fixed. On server startup there's no previous good configuration so it would always fail regardless.
			 */
			throw new GlobeException("Prebid configuration is empty.");
		}
		return prebidConfiguration;
	}

}