package com.about.mantle.model.extended.docv2.sc.blocks;

/**
 * Represents the start of a group of structured content documents.  Should always be paired with a
 * {@link StructuredContentEndGroup}
 *
 * This type is *not* deserialized from Selene documents but is inserted manually as necessary
 * between actual blocks that come from Selene.
 */
public class StructuredContentStartGroup extends AbstractStructuredContentGroup {

    public StructuredContentStartGroup(StructuredContentGroupType groupType) {
        super(groupType);
        this.setType("STARTGROUP");
    }
}