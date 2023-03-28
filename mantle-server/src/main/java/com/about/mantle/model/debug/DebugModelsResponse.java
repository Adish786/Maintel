package com.about.mantle.model.debug;

/**
 * Used for serializing the JSON response to the DebugModelsController
 */
public class DebugModelsResponse {
    private String status;
    private String message;
    private DebugModelsComponent data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DebugModelsComponent getData() {
        return data;
    }

    public void setData(DebugModelsComponent data) {
        this.data = data;
    }
}