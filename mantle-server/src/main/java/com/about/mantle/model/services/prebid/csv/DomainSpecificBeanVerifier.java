package com.about.mantle.model.services.prebid.csv;

import com.opencsv.bean.BeanVerifier;
import com.opencsv.exceptions.CsvConstraintViolationException;

/**
 * Used to filter for the lines that pertain to a particular domain.
 * 
 * Needed because there's a single master CSV that contains lines for _all_ domains.
 */
public class DomainSpecificBeanVerifier implements BeanVerifier<PrebidCsvLine> {

	private final String domain;

	public DomainSpecificBeanVerifier(String domain) {
		this.domain = domain;
	}

	@Override
	public boolean verifyBean(PrebidCsvLine bean) throws CsvConstraintViolationException {
		return domain.equalsIgnoreCase(bean.getDomain());
	}

}