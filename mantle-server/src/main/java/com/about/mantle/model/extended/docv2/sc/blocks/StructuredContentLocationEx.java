package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

/**
 * Implements the `LocationContent` definition of the Structured Content schema
 */
public class StructuredContentLocationEx extends AbstractStructuredContentContentEx<StructuredContentLocationEx.StructuredContentLocationDataEx> {

    /**
     * Implements the `LocationContentData` definition of the Structured Content schema
     */
    public static class StructuredContentLocationDataEx extends AbstractStructuredContentDataEx {

        private String placeId;

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        @Override
        public String toString() {
            return "StructuredContentLocationDataEx{" +
                    "placeId=" + placeId +
                    '}';
        }
    }
}
