package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import org.apache.commons.lang3.StringUtils;

/**
 * Implements the `HeadingContent` definition of the Structured Content schema
 */
public class StructuredContentHeadingEx extends AbstractStructuredContentContentEx<StructuredContentHeadingEx.StructuredContentHeadingDataEx> {

    /**
     * Implements the `HeadingContentData` definition of the Structured Content schema
     */
    public static class StructuredContentHeadingDataEx extends AbstractStructuredContentDataEx {

        private String text;
        private String shortText;
        private String uri;
        private Boolean hideOnTOC;

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

        public Boolean getHideOnTOC() {
            return hideOnTOC;
        }

        public void setHideOnTOC(Boolean hideOnTOC) {
            this.hideOnTOC = hideOnTOC;
        }

        @Override
        public String toString() {
            return "StructuredContentHeadingDataEx{" +
                    "text='" + text + '\'' +
                    "shortText='" + shortText + '\'' +
                    "uri='" + uri + '\'' +
                    "hideOnTOC='" + hideOnTOC + '\'' +
                    '}';
        }
    }
}
