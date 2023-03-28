package com.about.mantle.amazon.productAdvertisingApi.v5.services.impl;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.stream.Collectors;

import com.about.mantle.amazon.productAdvertisingApi.v5.services.AmazonProductAdvertisingApi;
import com.about.mantle.amazon.productAdvertisingApi.v5.services.AmazonProductAdvertisingApiFacade;
import com.about.mantle.model.commerce.AmazonCommerceModelV5;
import com.amazon.paapi5.v1.GetItemsResource;

public class AmazonProductAdvertisingApiFacadeImpl implements AmazonProductAdvertisingApiFacade {

    private AmazonProductAdvertisingApi api;

    public AmazonProductAdvertisingApiFacadeImpl(AmazonProductAdvertisingApi api) {
        this.api = api;
    }

	@Override
	public Collection<AmazonCommerceModelV5> getItems(Collection<String> asins, EnumSet<GetItemsResource> itemResources,
			Locale locale, String imageSize) {
		return api.getItems(asins, itemResources, locale)
                .map(item -> AmazonCommerceModelV5.buildAmazonCommerceModel(item, imageSize, locale))
                .collect(Collectors.toList());
	}
}
