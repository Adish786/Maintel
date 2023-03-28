package com.about.mantle.skimlinks.pricingApi.services;

import java.util.Collection;
import java.util.Map;

import com.about.mantle.model.commerce.SkimlinksCommerceModel;

public interface SkimlinksPricingApiFacade {
	
	/**
     * Calls Skimlinks API given collection of urls.  Does not throw any exceptions.  Must log and return an empty
     * map if there's a problem.
     *
     * @param urls
     * @return Map of Item ID to {@link SkimlinksCommerceModel}
     */
	Map<String, SkimlinksCommerceModel> lookupItems(Collection<String> urls);
}

