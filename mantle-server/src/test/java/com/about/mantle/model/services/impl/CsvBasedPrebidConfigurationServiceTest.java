package com.about.mantle.model.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.about.mantle.model.services.BusinessOwnedVerticalDataService;
import com.about.mantle.model.services.prebid.PrebidConfiguration;
import com.about.mantle.model.services.prebid.bidders.BidderCreator;
import com.about.mantle.model.services.prebid.bidders.CriteoCreator;
import com.about.mantle.model.services.prebid.bidders.CriteoPGCreator;
import com.about.mantle.model.services.prebid.bidders.IndexExchangeCreator;
import com.about.mantle.model.services.prebid.bidders.RubiconProjectCreator;
import com.about.mantle.model.services.prebid.csv.DomainSpecificBeanVerifier;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class CsvBasedPrebidConfigurationServiceTest {

	private static final Map<String, BidderCreator> bidderCreatorMap = initBidderCreatorMap();

	private static Map<String, BidderCreator> initBidderCreatorMap() {
		CriteoCreator criteo = new CriteoCreator();
		CriteoPGCreator criteopg = new CriteoPGCreator();
		IndexExchangeCreator ix = new IndexExchangeCreator();
		RubiconProjectCreator rubicon = new RubiconProjectCreator();
		return ImmutableMap.of(
			criteo.name(), criteo,
			criteopg.name(), criteopg,
			ix.name(), ix,
			rubicon.name(), rubicon
		);
	}

	private static final BusinessOwnedVerticalDataService testDataService = new BusinessOwnedVerticalDataService() {
		@Override
		public byte[] getResource(String path) {
			String filepath = "/ServicesTest/CsvBasedPrebidConfigurationServiceTest/" + path;
			try (InputStream inputStream = getClass().getResourceAsStream(filepath)) {
				if (inputStream == null) {
					throw new RuntimeException("failed to load file: " + filepath);
				}
				return IOUtils.toByteArray(inputStream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void addPropertyChangeLister(PropertyChangeListener propertyChangeListener) {
		}
	};

	private static final DomainSpecificBeanVerifier beanVerifier = new DomainSpecificBeanVerifier("aboutespanol");

	@Test
	public void testEmpty() {
		CsvBasedPrebidConfigurationService configService = new CsvBasedPrebidConfigurationService(bidderCreatorMap, testDataService, "empty.csv", beanVerifier);
		try {
			configService.getConfiguration();
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testNoHeader() {
		CsvBasedPrebidConfigurationService configService = new CsvBasedPrebidConfigurationService(bidderCreatorMap, testDataService, "no-header.csv", beanVerifier);
		try {
			configService.getConfiguration();
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testAllInvalid() {
		CsvBasedPrebidConfigurationService configService = new CsvBasedPrebidConfigurationService(bidderCreatorMap, testDataService, "all-invalid.csv", beanVerifier);
		try {
			configService.getConfiguration();
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testSomeInvalid() {
		CsvBasedPrebidConfigurationService configService = new CsvBasedPrebidConfigurationService(bidderCreatorMap, testDataService, "some-invalid.csv", beanVerifier);
		PrebidConfiguration config = configService.getConfiguration();
		assertEquals(2, config.getBidderEntries().size());
	}

	@Test
	public void testAllValid() {
		CsvBasedPrebidConfigurationService configService = new CsvBasedPrebidConfigurationService(bidderCreatorMap, testDataService, "all-valid.csv", beanVerifier);
		PrebidConfiguration config = configService.getConfiguration();
		assertEquals(49, config.getBidderEntries().size());
	}

	@Test
	public void testMixedDomain() {
		CsvBasedPrebidConfigurationService configService = new CsvBasedPrebidConfigurationService(bidderCreatorMap, testDataService, "mixed-domain.csv", beanVerifier);
		PrebidConfiguration config = configService.getConfiguration();
		assertEquals(49, config.getBidderEntries().size());
	}

	@Test
	public void testDeduping() {
		CsvBasedPrebidConfigurationService configService = new CsvBasedPrebidConfigurationService(bidderCreatorMap, testDataService, "dupes.csv", beanVerifier);
		PrebidConfiguration config = configService.getConfiguration();
		assertEquals(1, config.getBidderEntries().size());
	}

}