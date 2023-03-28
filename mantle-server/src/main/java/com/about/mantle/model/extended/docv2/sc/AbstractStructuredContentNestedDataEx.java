package com.about.mantle.model.extended.docv2.sc;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentGroupType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractStructuredContentNestedDataEx extends AbstractStructuredContentDataEx{
    @JsonIgnore
    public StructuredContentGroupType getGroupType() {return StructuredContentGroupType.DIV;}

    private SliceableListEx<AbstractStructuredContentContentEx> contents;

    public SliceableListEx<AbstractStructuredContentContentEx> getContents() {
        return contents;
    }

    public void setContents(SliceableListEx<AbstractStructuredContentContentEx> contents) {
        this.contents = contents;
    }
}
