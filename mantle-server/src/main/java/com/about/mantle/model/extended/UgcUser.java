package com.about.mantle.model.extended;

import java.io.Serializable;

public class UgcUser extends BaseAuthor implements Serializable {
    private static final long serialVersionUID = 1L;

    public UgcUser() {
        this.setType(BaseAuthor.UGC_USER);
    }

    @Override
    public String getType() {
        return BaseAuthor.UGC_USER;
    }
}