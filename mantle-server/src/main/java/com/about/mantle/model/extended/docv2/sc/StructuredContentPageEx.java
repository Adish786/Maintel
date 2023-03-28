package com.about.mantle.model.extended.docv2.sc;

import com.about.mantle.model.extended.docv2.SliceableListEx;

/**
 * Conforms to the `page` definition of the Structured Content selene schema
 */
public class StructuredContentPageEx {

    private SliceableListEx<AbstractStructuredContentContentEx<?>> contents;

    public SliceableListEx<AbstractStructuredContentContentEx<?>> getContents() {
        return contents;
    }

    public void setContents(SliceableListEx<AbstractStructuredContentContentEx<?>> contents) {
        this.contents = contents;
    }
}
