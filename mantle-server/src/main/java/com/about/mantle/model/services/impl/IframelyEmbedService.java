package com.about.mantle.model.services.impl;

import com.about.mantle.model.services.EmbedService;
import com.about.mantle.model.services.embeds.EmbedContent;
import com.about.mantle.model.services.embeds.IframelyEmbedProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilizes the Iframely oembed calls for providing embed functionality
 *
 * See https://iframely.com/docs
 */
public class IframelyEmbedService implements EmbedService {

    private static final Logger logger = LoggerFactory.getLogger(IframelyEmbedService.class);

    private final IframelyEmbedProvider iframelyEmbedProvider;

    public IframelyEmbedService(IframelyEmbedProvider iframelyEmbedProvider) {
        this.iframelyEmbedProvider = iframelyEmbedProvider;
    }

    /**
     * getContent
     *
     * @param url - url you want to embed
     * @param options - a map of embed type to options (both Iframely parameters and per embed options)
     *                See https://iframely.com/docs/parameters and https://iframely.com/docs/options
     * @return html from iframely
     */
    @Override
    public EmbedContent getContent(String url, Map<String, Map<String, String>> options) {
        return iframelyEmbedProvider.getIframelyResult(url, buildOptionMap (url, options));
    }

    /**
     * gets the map of options for a specific url type from a map of options
     * @param url
     * @param options
     * @return
     */
    private Map<String, String> buildOptionMap(String url, Map<String, Map<String, String>> options) {
        Map<String, String> optionMap = new HashMap<>();

        if(options != null) {
            String provider = null;
            for (String key : options.keySet()) {
                if (url.contains(key)) provider = key;
            }

            if (provider != null) {
                optionMap = options.get(provider);
            }

            //Remove oembed option as it has changed
            //TODO Remove after verticals update, left because otherwise this becomes a breaking change
            //Change as part of GLBE-8380
            if("instagram".equalsIgnoreCase(provider) && optionMap.containsKey("hidecaption")){

                if("true".equalsIgnoreCase(optionMap.get("hidecaption"))){
                    optionMap.put("_showcaption", "false");
                }else{
                    optionMap.put("_showcaption", "true");
                }
                optionMap.remove("hidecaption");
            }
        }
        return optionMap;
    }
}