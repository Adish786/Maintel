package com.about.mantle.model.extended.resound;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Used with Resound's POST /react
 * See: https://docs-resound.meredithcorp.io/#/Reactions/post_react
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResoundReactionRequest {
    private String provider;
    private String providerId;
    private String userHash;
    private String brand;
    private String type;

    private ResoundReactionRequest(Builder builder) {
        this.provider = builder.provider;
        this.providerId = builder.providerId;
        this.userHash = builder.userHash;
        this.brand = builder.brand;
        this.type = builder.type;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getUserHash() {
        return userHash;
    }

    public void setUserHash(String userHash) {
        this.userHash = userHash;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class Builder {
        private String provider;
        private String providerId;
        private String userHash;
        private String brand;
        private String type;

        public ResoundReactionRequest build() {
            return new ResoundReactionRequest(this);
        }

        public Builder setProvider(String provider) {
            this.provider = provider;
            return this;
        }

        public Builder setProviderId(String providerId) {
            this.providerId = providerId;
            return this;
        }

        public Builder setUserHash(String userHash) {
            this.userHash = userHash;
            return this;
        }

        public Builder setBrand(String brand) {
            this.brand = brand;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }
    }
}
