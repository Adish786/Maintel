package com.about.mantle.model.extended.docv2.sc;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStructuredContentNestedEx<T extends AbstractStructuredContentNestedDataEx> extends AbstractStructuredContentContentEx<T> {

    @JsonIgnore
    public List<AbstractStructuredContentContentEx> getNestingContents() {
        List<AbstractStructuredContentContentEx> contents = new ArrayList<>();

        if (!SliceableListEx.isEmpty(getData().getContents())) {
            contents.addAll(getData().getContents().getList());
        }

        return contents;
    }
}
