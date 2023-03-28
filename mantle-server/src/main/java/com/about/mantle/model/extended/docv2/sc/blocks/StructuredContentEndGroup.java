package com.about.mantle.model.extended.docv2.sc.blocks;

/**
 * Represents the end of a group of structured content documents.  Should always be paired with a
 * {@link StructuredContentStartGroup}
 *
 * This type is *not* deserialized from Selene documents but is inserted manually as necessary
 * between actual blocks that come from Selene.
 */
public class StructuredContentEndGroup extends AbstractStructuredContentGroup {

    public StructuredContentEndGroup(StructuredContentGroupType groupType) {
        super(groupType);
        if (groupType.isTag()) {
            // Tags can be handled by a single component that simply renders the closing tag.
            this.type = "ENDGROUP";
        } else {
            // Special groups must be separate components because their endings can vary.
            this.type = groupType.name() + "-ENDGROUP";
        }
    }
}