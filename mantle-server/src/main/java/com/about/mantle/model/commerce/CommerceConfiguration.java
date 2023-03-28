package com.about.mantle.model.commerce;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps around the map passed in to the commerceTask to hold configuration properties for the commerce apis.
 */
public class CommerceConfiguration {

    public static final String CONFIG_IMAGE_SIZE = "imageSize";
    public static final String IMAGE_SIZE_SMALL = "small";
    public static final String IMAGE_SIZE_MEDIUM = "medium";
    public static final String IMAGE_SIZE_LARGE = "large";

    private Map<String, String> configurationMapping;

    public CommerceConfiguration(Map<String, String> configurationMapping) {
        this.configurationMapping = configurationMapping != null ? configurationMapping : new HashMap<>();
    }

  
    public String getImageSize() {
        return configurationMapping.get(CONFIG_IMAGE_SIZE);
    }
}
