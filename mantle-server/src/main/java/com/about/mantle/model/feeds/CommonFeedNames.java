package com.about.mantle.model.feeds;

/**
 * Deprecated in favor of feed builder classes keeping track of their own feed names
 * since verticals can create their own feed builders
 */
@Deprecated
public enum CommonFeedNames {
    FLIPBOARD("flipboard"),
    GOOGLE_NEWS("google-news"),
    TAXONOMY_NEWS("taxonomy");

    private final String name;

    CommonFeedNames(String name) {
        this.name = name;
    }

    public String getValue() {
        return name;
    }

    public static CommonFeedNames fromValue(String name) {
        for (CommonFeedNames anEnum : CommonFeedNames.values()) {
            if (anEnum.getValue().equals(name)) {
                return anEnum;
            }
        }
        throw new IllegalArgumentException();
    }
}