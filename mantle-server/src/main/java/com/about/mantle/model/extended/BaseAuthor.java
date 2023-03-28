package com.about.mantle.model.extended;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

//@formatter:off
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", defaultImpl = com.about.mantle.model.extended.AuthorEx.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AuthorEx.class, name = "AUTHOR"),
        @JsonSubTypes.Type(value = UgcUser.class, name = "UGC_USER")
})
//@formatter:on
public abstract class BaseAuthor implements Serializable {
    public static final String AUTHOR = "AUTHOR";
    public static final String UGC_USER = "UGC_USER";
    private static final long serialVersionUID = 1L;

    private String id;
    private String bioUrl;
    private String type;
    private String displayName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBioUrl() {
        return bioUrl;
    }

    public void setBioUrl(String bioUrl) {
        this.bioUrl = bioUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}