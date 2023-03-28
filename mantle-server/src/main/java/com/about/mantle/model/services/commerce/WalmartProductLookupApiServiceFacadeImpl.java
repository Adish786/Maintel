package com.about.mantle.model.services.commerce;

import com.about.mantle.model.commerce.WalmartCommerceModel;
import com.dotdash.walmart.productlookup.ProductLookupApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WalmartProductLookupApiServiceFacadeImpl implements WalmartProductLookupApiServiceFacade {

    private static Logger logger = LoggerFactory.getLogger(WalmartProductLookupApiServiceFacadeImpl.class);

    private final ProductLookupApiService delegate;

    public WalmartProductLookupApiServiceFacadeImpl(ProductLookupApiService delegate) {
        this.delegate = delegate;
    }

    @Override
    public Map<Integer, WalmartCommerceModel> lookupItems(Collection<Integer> ids, String imageSize) {

        Map<Integer, WalmartCommerceModel> answer = Collections.EMPTY_MAP;
        try {
            answer = this._safeLookupItems(ids, imageSize);
        } catch (Exception e) {
            logger.error("Could not complete Walmart product lookup", e);
        }
        return answer;
    }

    private Map<Integer, WalmartCommerceModel> _safeLookupItems(Collection<Integer> ids, String imageSize) {

        Map<Integer, WalmartCommerceModel> answer = Collections.EMPTY_MAP;

        answer = delegate.requestItemsById(new ArrayList<>(ids))
                .map((item) -> new WalmartCommerceModel(item, imageSize))
                .collect(Collectors.toMap(WalmartCommerceModel::getId, Function.identity()));

        return answer;
    }
}
