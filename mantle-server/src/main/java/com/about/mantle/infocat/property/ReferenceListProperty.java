package com.about.mantle.infocat.property;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.about.mantle.infocat.model.Reference;

public class ReferenceListProperty extends Property<List<Reference>> implements Serializable {

    private static final long serialVersionUID = 1L;

    private ReferenceListPropertyValue value;

    @Override
    public PropertyValue<List<Reference>> getValue() {
        return value;
    }

    public static class ReferenceListPropertyValue extends PropertyValue<List<Reference>> {

        List<Reference> referenceValues = Collections.emptyList();

        public List<Reference> getReferenceValues() {
            return referenceValues;
        }

        public void setReferenceValues(List<Reference> referenceValues) {
            this.referenceValues = referenceValues;
        }

        @Override
        public List<Reference> getPrimaryValue() {
            return referenceValues;
        }
    }
}
