package com.about.mantle.model.disqus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * What is returned from disqus for thread details
 * JsonIgnoreProperties is used here to avoid having to deserialize all the data we don't
 * need.
 *
 * This is returned in a variety of disqus api calls
 * Note at the time of writing this it is unclear what the app needs, so please see the disqus
 * documentation for more info
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DisqusThreadDetails {
    private String id;
    private int posts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }
}
