package com.about.mantle.amazon.productAdvertisingApi.services;

import com.about.mantle.amazon.productAdvertisingApi.model.ResponseGroup;
import com.about.mantle.amazon.productAdvertisingApi.wsdl.Item;

import java.util.*;
import java.util.stream.Stream;

/**
 * Single point of contact for the Amazon Product Advertising API (http://docs.aws.amazon.com/AWSECommerceService/latest/DG/Welcome.html)
 * Note that there should be no View related methods this class, it is intended only as a passthru.  (eg currency formatting, etc).
 * <p>
 * Expected to be a singleton
 */
public interface AmazonProductAdvertisingApi {

    /**
     * Calls `ItemLookup` Operation.  Will only return New items (not used / refurb etc)
     * <p>
     * See http://docs.aws.amazon.com/AWSECommerceService/latest/DG/ItemLookup.html
     *
     * @param asins          Collection of ASIN product IDs
     * @param responseGroups Response Groups to return.  See http://docs.aws.amazon.com/AWSECommerceService/latest/DG/CHAP_ResponseGroupsList.html
     * @param locale         Locale of the caller.  Used to determine which AWS marketplace / endpoint to hit.
     * @return
     */
    public Stream<Item> lookupItems(
            Collection<String> asins, EnumSet<ResponseGroup> responseGroups, Locale locale);
}
