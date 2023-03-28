package com.about.mantle.amazon.productAdvertisingApi.services;

import com.about.mantle.amazon.productAdvertisingApi.model.ResponseGroup;
import com.about.mantle.model.commerce.AmazonCommerceModel;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;

/**
 * A wrapper around the core {@link AmazonProductAdvertisingApi}.  Exists so that we can cache only what we need
 * downstream, not everything that we'd get from Amazon via the {@link AmazonProductAdvertisingApi}.
 */
public interface AmazonProductAdvertisingApiFacade {

    /**
     * Calls `ItemLookup` Operation.  Will only return New items (not used / refurb etc)
     * <p>
     * See http://docs.aws.amazon.com/AWSECommerceService/latest/DG/ItemLookup.html
     *
     * @param asins          Collection of ASIN product IDs
     * @param responseGroups Response Groups to return.  See http://docs.aws.amazon.com/AWSECommerceService/latest/DG/CHAP_ResponseGroupsList.html
     * @param locale         Locale of the caller.  Used to determine which AWS marketplace / endpoint to hit.
     * @param imageSize      Size of the image.  Use constants provided by {@link AmazonCommerceModel}
     * @return
     */
    public Collection<AmazonCommerceModel> lookupItems(
            Collection<String> asins,
            EnumSet<ResponseGroup> responseGroups,
            Locale locale,
            String imageSize);
}
