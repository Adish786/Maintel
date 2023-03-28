package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class BusinessEx implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String website;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
