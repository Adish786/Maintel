package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import org.apache.commons.lang3.StringUtils;

/**
 * Implements the `SubheadingContent` definition of the Structured Content schema
 */
public class StructuredContentSubheadingEx extends AbstractStructuredContentContentEx<StructuredContentSubheadingEx.StructuredContentSubheadingDataEx> {

    /**
     * Implements the `SubheadingContentData` definition of the Structured Content schema
     */
    public static class StructuredContentSubheadingDataEx extends AbstractStructuredContentDataEx {

        private String text;
        private String shortText;
        private String uri;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getShortText() {
            return shortText;
        }

        public void setShortText(String shortText) {
            this.shortText = StringUtils.trimToNull(shortText);
        }

        /**
         * Null if not provided
         *
         * @return
         */
        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = StringUtils.trimToNull(uri);
        }

        @Override
        public String toString() {
            return "StructuredContentSubheadingDataEx{" +
                    "text='" + text + '\'' +
                    "shortText='" + shortText + '\'' +
                    "uri='" + uri + '\'' +
                    '}';
        }
    }
}
