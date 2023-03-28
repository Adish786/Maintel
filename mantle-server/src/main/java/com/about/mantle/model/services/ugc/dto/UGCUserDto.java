package com.about.mantle.model.services.ugc.dto;

import com.about.mantle.model.extended.UgcUser;

import java.io.Serializable;

public class UGCUserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String displayName;
    private String profileUrl;

    public UgcUser toUgcUser() {
        UgcUser ugcUser = new UgcUser();
        ugcUser.setId(userId);
        ugcUser.setDisplayName(displayName);
        ugcUser.setBioUrl(profileUrl);
        return ugcUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
