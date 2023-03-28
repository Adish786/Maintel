package com.about.mantle.model.extended.attribution;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;

import java.io.Serializable;
import java.util.List;

import static com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;

public class AttributionType  implements Serializable {
    private static final long serialVersionUID = 1L;

    private String value;
    private TagValue tags;

    public AttributionType(){}

    public AttributionType(String value, TagValue tags) {
        this.value = value;
        this.tags = tags;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TagValue getTags() {
        return tags;
    }

    public void setTags(TagValue tags) {
        this.tags = tags;
    }

    public static class TagValue implements Serializable {

        private static final long serialVersionUID = 1L;

        private Placement limitedTo;
        private List<BaseDocumentEx.Vertical> verticals;
        private String category;

        public Placement getLimitedTo() {
            return limitedTo;
        }

        public void setLimitedTo(Placement limitedTo) {
            this.limitedTo = limitedTo;
        }

        public List<Vertical> getVerticals() {
            return verticals;
        }

        public void setVerticals(List<Vertical> verticals) {
            this.verticals = verticals;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        private enum Placement {
            BYLINES,
            TAGLINES
        }
    }

}
