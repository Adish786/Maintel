package com.about.mantle.model.shared_services.registration.request.actions;

public class OptInAction extends Action {
    private Boolean optin;
    private String objectId;

    public OptInAction(String actionType, Boolean optin, String objectId) {
        super(actionType);
        this.optin = optin;
        this.objectId = objectId;
    }

    public Boolean getOptin() {
        return optin;
    }

    public void setOptin(Boolean optin) {
        this.optin = optin;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Action{");
        sb.append(", optin=").append(optin);
        sb.append(", objectId='").append(objectId).append('\'');
        sb.append('}');
        sb.append(super.toString());
        return sb.toString();
    }
}
