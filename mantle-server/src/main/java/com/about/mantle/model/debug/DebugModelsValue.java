package com.about.mantle.model.debug;

/**
 * Represents a model in the response to the DebugModelsController
 */
public class DebugModelsValue {
    private String id;
    private String qualifiedId;
    private Object value;
    private String valueRefId;

    public DebugModelsValue(String id, String qualifiedId, Object value, String valueRefId) {
        this.id = id;
        this.qualifiedId = qualifiedId;
        this.value = value;
        this.valueRefId = valueRefId;
    }

    public String getId() {
        return id;
    }

    public String getQualifiedId() {
        return qualifiedId;
    }

    public Object getValue() {
        return value;
    }

    public String getValueRefId() {
        return valueRefId;
    }
}