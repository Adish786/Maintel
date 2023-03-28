package com.about.mantle.amazon.productAdvertisingApi.v5.services;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;

import com.about.mantle.model.commerce.AmazonCommerceModelV5;
import com.amazon.paapi5.v1.GetItemsResource;

/**
 * A wrapper around the core {@link AmazonProductAdvertisingApi}.  Exists so that we can cache only what we need
 * downstream, not everything that we'd get from Amazon via the {@link AmazonProductAdvertisingApi}.
 */
public interface AmazonProductAdvertisingApiFacade {

    /**
     * Calls `GetItems` Operation.  Will only return New items (not used / refurb etc)
     * <p>
     * See https://webservices.amazon.com/paapi5/documentation/get-items.html
     *
     * @param asins          Collection of ASIN product IDs
     * @param itemResources  GetItemsResource to return.  See https://webservices.amazon.com/paapi5/documentation/get-items.html#resources-parameter
     * @param locale         Locale of the caller.  Used to determine which AWS marketplace / endpoint to hit.
     * @param imageSize      Size of the image.  Use constants provided by {@link AmazonCommerceModel}
     * @return
     */
    public Collection<AmazonCommerceModelV5> getItems(
            Collection<String> asins,
            EnumSet<GetItemsResource> itemResources,
            Locale locale,
            String imageSize);
}
