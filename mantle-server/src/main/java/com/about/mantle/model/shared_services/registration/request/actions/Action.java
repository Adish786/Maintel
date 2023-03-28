package com.about.mantle.model.shared_services.registration.request.actions;

public abstract class Action {
    private String actionType;

    public Action(String actionType) {
        this.actionType = actionType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Action{");
        sb.append("actionType='").append(actionType);
        sb.append('}');
        return sb.toString();
    }
}
