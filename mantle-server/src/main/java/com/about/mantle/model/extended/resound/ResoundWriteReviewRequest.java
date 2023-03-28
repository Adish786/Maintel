package com.about.mantle.model.extended.resound;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Used with Resound's POST /review
 * See: https://docs-resound.meredithcorp.io/#/Reviews/post_review
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResoundWriteReviewRequest {

    private String provider;
    private String providerId;
    private String userHash;
    private String brand;
    private String text;
    private String ratingDifficulty;
    private Integer ratingFiveStar;
    private String date;
    private List<String> urls;

    private ResoundWriteReviewRequest(Builder builder){
        this.provider = builder.provider;
        this.providerId = builder.providerId;
        this.userHash = builder.userHash;
        this.brand = builder.brand;
        this.text = builder.text;
        this.ratingDifficulty = builder.ratingDifficulty;
        this.ratingFiveStar = builder.ratingFiveStar;
        this.date = builder.date;
        this.urls = builder.urls;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRatingDifficulty() {
        return ratingDifficulty;
    }

    public void setRatingDifficulty(String ratingDifficulty) {
        this.ratingDifficulty = ratingDifficulty;
    }

    public Integer getRatingFiveStar() {
        return ratingFiveStar;
    }

    public void setRatingFiveStar(Integer ratingFiveStar) {
        this.ratingFiveStar = ratingFiveStar;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public static class Builder {
        private String provider;
        private String providerId;
        private String userHash;
        private String brand;
        private String text;
        private String ratingDifficulty;
        private Integer ratingFiveStar;
        private String date;
        private List<String> urls;

        public Builder provider (String provider){
            this.provider = provider;
            return this;
        }

        public Builder providerId (String providerId){
            this.providerId = providerId;
            return this;
        }

        public Builder userHash (String userHash){
            this.userHash = userHash;
            return this;
        }

        public Builder brand (String brand){
            this.brand = brand;
            return this;
        }

        public Builder text (String text){
            this.text = text;
            return this;
        }

        public Builder ratingDifficulty (String ratingDifficulty){
            this.ratingDifficulty = ratingDifficulty;
            return this;
        }

        public Builder ratingFiveStar (Integer ratingFiveStar){
            this.ratingFiveStar = ratingFiveStar;
            return this;
        }

        public Builder urls(List<String> urls) {
            this.urls = urls;
            return this;
        }

        public Builder url(String url) {
            if (urls == null) {
                urls = new ArrayList<>();
            }
            urls.add(url);
            return this;
        }

        public ResoundWriteReviewRequest build(){
            this.date = DateTime.now().toString();
            return new ResoundWriteReviewRequest(this);
        }
    }
}
