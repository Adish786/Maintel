package com.about.mantle.infocat.model;

import java.io.Serializable;

public class Reference implements Serializable {

    private static final long serialVersionUID = 1L;

    private String recordId;
    private String type;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReferenceListPropertyValue{");
        sb.append("recordId='").append(recordId).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
