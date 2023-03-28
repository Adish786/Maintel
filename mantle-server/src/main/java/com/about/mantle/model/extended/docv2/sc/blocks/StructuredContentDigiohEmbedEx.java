package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentDigiohEmbedEx extends AbstractStructuredContentContentEx<StructuredContentDigiohEmbedEx.StructuredContentDigiohEmbedDataEx> {
    public static class StructuredContentDigiohEmbedDataEx extends AbstractStructuredContentDataEx {
        private String digiohGUID;
        private String digiohTitle;

        public String getDigiohGUID() {
            return digiohGUID;
        }

        public void setDigiohGUID(String digiohGUID) {
            this.digiohGUID = digiohGUID;
        }

        public String getDigiohTitle() {
            return digiohTitle;
        }

        public void setDigiohTitle(String digiohTitle) {
            this.digiohTitle = digiohTitle;
        }

        @Override
        public String toString() {
            return "StructuredContentDigiohEmbedDataEx{" +
                    "digiohGUID='" + digiohGUID + '\'' +
                    ", digiohTitle='" + digiohTitle + '\'' +
                    "} " + super.toString();
        }
    }
}
