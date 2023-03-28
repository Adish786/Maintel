package com.about.mantle.model.shared_services.regsources.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * Registration Source Target. There are three sub types: "newsletter", "objector" & "sponsor-campaign".
 * Currently setup to serialize/deserialize "newsletter" & "objector" sub types.
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", defaultImpl = Void.class)
@JsonSubTypes({ @JsonSubTypes.Type(value = NewsletterRegistrationSourceTarget.class, name = "newsletter"),
                @JsonSubTypes.Type(value = ObjectorRegistrationSourceTarget.class, name = "objector") })
public abstract class RegistrationSourceTarget implements Serializable {

    private String type;
    private String uid;
    private String description;
    private Boolean precheck;
    private String title;
    private Integer sequenceNum;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPrecheck() {
        return precheck;
    }

    public void setPrecheck(Boolean precheck) {
        this.precheck = precheck;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(Integer sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistrationSourceTarget{");
        sb.append("type='").append(type).append('\'');
        sb.append(", uid='").append(uid).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", precheck=").append(precheck);
        sb.append(", title='").append(title).append('\'');
        sb.append(", sequenceNum='").append(sequenceNum).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
