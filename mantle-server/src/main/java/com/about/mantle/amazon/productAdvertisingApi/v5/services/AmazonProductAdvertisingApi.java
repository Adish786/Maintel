package com.about.mantle.amazon.productAdvertisingApi.v5.services;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.stream.Stream;

import com.amazon.paapi5.v1.GetItemsResource;
import com.amazon.paapi5.v1.Item;

/**
 * Single point of contact for the Amazon Product Advertising API (http://docs.aws.amazon.com/AWSECommerceService/latest/DG/Welcome.html)
 * Note that there should be no View related methods this class, it is intended only as a passthru.  (eg currency formatting, etc).
 * <p>
 * Expected to be a singleton
 */
public interface AmazonProductAdvertisingApi {

    /**
     * Calls `GetItems` Operation.  Will only return New items (not used / refurb etc)
     * <p>
     * See https://webservices.amazon.com/paapi5/documentation/get-items.html#getitems
     *
     * @param asins          Collection of ASIN product IDs
     * @param itemsResources Item Resources to return.  See https://webservices.amazon.com/paapi5/documentation/get-items.html#resources-parameter
     * @param locale         Locale of the caller.  Used to determine which AWS marketplace / endpoint to hit.
     * @return
     */
    public Stream<Item> getItems(
            Collection<String> asins, EnumSet<GetItemsResource> itemsResources, Locale locale);
}
