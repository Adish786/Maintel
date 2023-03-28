package com.about.mantle.skimlinks.pricingApi.services;

import java.util.Collection;
import java.util.Map;

import com.about.mantle.skimlinks.pricingApi.model.SkimlinksItem;

public interface SkimlinksPricingApi {
	
	/**
     * Calls the Monetizer101 `sale-prices` operation.  Will asynchronously scrape retailer site and return sale pricing information
     * as well as the Skimlinks Link wrapped url for the product page
     * <p>
     * See http://docs.monetizer101.com/api/skimlinks/v1.0/api.html#!/default/post_scraper_sale_prices
     * See https://developers.skimlinks.com/link.html
     *
     * @param urls          Collection of product urls
     * @return
     */
    public Map<String, SkimlinksItem> lookupItems(Collection<String> urls);

}
