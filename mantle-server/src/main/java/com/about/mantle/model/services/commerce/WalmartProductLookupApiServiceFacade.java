package com.about.mantle.model.services.commerce;

import com.about.mantle.model.commerce.WalmartCommerceModel;

import java.util.Collection;
import java.util.Map;

/**
 * Wraps {@link com.dotdash.walmart.productlookup.ProductLookupApiService}.  Exists so that we can cache only what we
 * need, not everything from the API.
 */
public interface WalmartProductLookupApiServiceFacade {

    /**
     * Calls Walmart API given collection of IDs.  Does not throw any exceptions.  Must log and return an empty
     * map if there's a problem.
     *
     * @param ids
     * @param imageSize
     * @return Map of Item ID to {@link WalmartCommerceModel}
     */
    Map<Integer, WalmartCommerceModel> lookupItems(Collection<Integer> ids, String imageSize);
}
