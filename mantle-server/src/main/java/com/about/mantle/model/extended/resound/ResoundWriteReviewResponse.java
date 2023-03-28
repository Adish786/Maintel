package com.about.mantle.model.extended.resound;

import com.fasterxml.jackson.annotation.JsonAlias;

public class ResoundWriteReviewResponse {
    @JsonAlias("Message")
    private String message;
    private Providers providers;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Providers getProviders() {
        return providers;
    }

    public void setProviders(Providers providers) {
        this.providers = providers;
    }

    public static class Providers{
        private String resound;
        public String getResound() {
            return resound;
        }

        public void setResound(String resound) {
            this.resound = resound;
        }
    }
}
