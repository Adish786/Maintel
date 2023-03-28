package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentCheetahEmbedEx extends AbstractStructuredContentContentEx<StructuredContentCheetahEmbedEx.StructuredContentCheetahEmbedDataEx> {

    public static class StructuredContentCheetahEmbedDataEx extends AbstractStructuredContentDataEx {

        private String cheetahId;

        public String getCheetahId() {
            return cheetahId;
        }

        public void setCheetahId(String cheetahId) {
            this.cheetahId = cheetahId;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("StructuredContentCheetahEmbedDataEx{");
            sb.append("cheetahId='").append(cheetahId).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
