package com.about.mantle.model.debug;

import java.util.List;

/**
 * Represents a template component in the response to the DebugModelsController
 */
public class DebugModelsComponent {
    private String id;
    private List<DebugModelsValue> models;
    private List<DebugModelsComponent> components;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DebugModelsValue> getModels() {
        return models;
    }

    public void setModels(List<DebugModelsValue> models) {
        this.models = models;
    }

    public List<DebugModelsComponent> getComponents() {
        return components;
    }

    public void setChildren(List<DebugModelsComponent> components) {
        this.components = components;
    }
}