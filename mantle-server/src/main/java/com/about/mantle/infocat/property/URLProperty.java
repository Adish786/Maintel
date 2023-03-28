package com.about.mantle.infocat.property;

import java.io.Serializable;

import org.apache.commons.lang3.tuple.Pair;

public class URLProperty extends Property<Pair<String, String>> implements Serializable {

    private static final long serialVersionUID = 1L;

    private UrlPropertyValue value;

    @Override
    public PropertyValue<Pair<String, String>> getValue() {
        return value;
    }

    public static class UrlPropertyValue extends PropertyValue<Pair<String, String>> {

        private String href;
        private String text;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public Pair<String, String> getPrimaryValue() {
            return Pair.of(href, text);
        }
    }
}
