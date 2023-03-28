package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

/**
 * Used as a placeholder in the DOM to insert ads.  This type is *not* deserialized from Selene documents but is
 * inserted manually as necessary between actual blocks that come from Selene.
 */
public class StructuredContentAdSlotEx extends AbstractStructuredContentContentEx<StructuredContentAdSlotEx.StructuredContentEmptyDataEx> {

    @Override
    public String getType() {
        return "ADSLOT";
    }

    /**
     * Exists to provide empty data since data is required.
     */
    @Override
    public StructuredContentEmptyDataEx getData() {
        return new StructuredContentEmptyDataEx();
    }

    /**
     * Exists only to follow existing conventions that require that all ContextEx types have a corresponding
     * data type
     */
    public static class StructuredContentEmptyDataEx extends AbstractStructuredContentDataEx {

        @Override
        public String toString() {
            return "StructuredContentEmptyDataEx{}";
        }
    }
}
