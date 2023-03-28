package com.about.mantle.model.disqus;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DisqusThreadPopularPostsResponse {

    private int code;
    private List<DisqusPost> response;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DisqusPost> getResponse() {
        return response;
    }

    public void setResponse(List<DisqusPost> response) {
        this.response = response;
    }

}
