package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

/**
 * Parent class for the start / end marker blocks for a group of Structured Content.
 */
public class AbstractStructuredContentGroup extends AbstractStructuredContentContentEx<AbstractStructuredContentGroup.StructuredContentStartGroupDataEx> {

    public AbstractStructuredContentGroup(StructuredContentGroupType groupType) {
        data = new StructuredContentStartGroupDataEx(groupType);
    }

    @Override
    public StructuredContentStartGroupDataEx getData() {
        return data;
    }

    /**
     * Exists solely to keep in convention with the rest of the subclasses of {@link AbstractStructuredContentGroup}
     */
    public static class StructuredContentStartGroupDataEx extends AbstractStructuredContentDataEx {

        private StructuredContentGroupType groupType;

        public StructuredContentStartGroupDataEx(StructuredContentGroupType groupType) {
            this.groupType = groupType;
        }

        public StructuredContentGroupType getGroupType() {
            return groupType;
        }
    }

}
