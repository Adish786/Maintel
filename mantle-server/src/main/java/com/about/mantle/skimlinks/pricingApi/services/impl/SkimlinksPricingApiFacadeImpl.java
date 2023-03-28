package com.about.mantle.skimlinks.pricingApi.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.mantle.model.commerce.SkimlinksCommerceModel;
import com.about.mantle.skimlinks.pricingApi.services.SkimlinksPricingApi;
import com.about.mantle.skimlinks.pricingApi.services.SkimlinksPricingApiFacade;

public class SkimlinksPricingApiFacadeImpl implements SkimlinksPricingApiFacade {

	private static Logger logger = LoggerFactory.getLogger(SkimlinksPricingApiFacadeImpl.class);

    private final SkimlinksPricingApi api;

    public SkimlinksPricingApiFacadeImpl(SkimlinksPricingApi api) {
        this.api = api;
    }
    
	@Override
	public Map<String, SkimlinksCommerceModel> lookupItems(Collection<String> urls) {
		Map<String, SkimlinksCommerceModel> answer = Collections.EMPTY_MAP;
        try {
        	answer = api.lookupItems(urls).entrySet().stream()
                    .map((entry) ->  new SkimlinksCommerceModel(entry.getValue(), entry.getKey()))
                    .collect(Collectors.toMap(SkimlinksCommerceModel::getUrl, Function.identity()));
        } catch (Exception e) {
            logger.error("Could not complete Skimlinks product lookup", e);
        }
        return answer;
	}

}
