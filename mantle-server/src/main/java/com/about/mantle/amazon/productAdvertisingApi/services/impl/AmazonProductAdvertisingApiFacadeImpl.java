package com.about.mantle.amazon.productAdvertisingApi.services.impl;

import com.about.mantle.amazon.productAdvertisingApi.model.ResponseGroup;
import com.about.mantle.amazon.productAdvertisingApi.services.AmazonProductAdvertisingApi;
import com.about.mantle.amazon.productAdvertisingApi.services.AmazonProductAdvertisingApiFacade;
import com.about.mantle.model.commerce.AmazonCommerceModel;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.stream.Collectors;

public class AmazonProductAdvertisingApiFacadeImpl implements AmazonProductAdvertisingApiFacade {

    private AmazonProductAdvertisingApi api;

    public AmazonProductAdvertisingApiFacadeImpl(AmazonProductAdvertisingApi api) {
        this.api = api;
    }

    @Override
    public Collection<AmazonCommerceModel> lookupItems(Collection<String> asins, EnumSet<ResponseGroup> responseGroups,
                                              Locale locale, String imageSize) {

        return api.lookupItems(asins, responseGroups, locale)
                .map(item -> AmazonCommerceModel.buildAmazonCommerceModel(item, imageSize, locale))
                .collect(Collectors.toList());
    }
}
