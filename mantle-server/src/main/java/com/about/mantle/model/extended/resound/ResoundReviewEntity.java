package com.about.mantle.model.extended.resound;

import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ResoundReviewEntity {
    private Udf udf;

    public Udf getUdf() {
        return udf;
    }

    public void setUdf(Udf udf) {
        this.udf = udf;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Udf {
        private DateTime last_updated;
        private Integer rating_five_star;
        private String _type;
        private DateTime publish_date;
        private String brand;
        private String user_text;
        private List<ResoundEntityContainer<ResoundImageEntity>> media;

        public DateTime getLast_updated() {
            return last_updated;
        }

        public void setLast_updated(DateTime last_updated) {
            this.last_updated = last_updated;
        }

        public Integer getRating_five_star() {
            return rating_five_star;
        }

        public void setRating_five_star(Integer rating_five_star) {
            this.rating_five_star = rating_five_star;
        }

        public String get_type() {
            return _type;
        }

        public void set_type(String _type) {
            this._type = _type;
        }

        public DateTime getPublish_date() {
            return publish_date;
        }

        public void setPublish_date(DateTime publish_date) {
            this.publish_date = publish_date;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getUser_text() {
            return user_text;
        }

        public void setUser_text(String user_text) {
            this.user_text = user_text;
        }

        public List<ResoundEntityContainer<ResoundImageEntity>> getMedia() {
            return media;
        }

        public void setMedia(List<ResoundEntityContainer<ResoundImageEntity>> media) {
            this.media = media;
        }
    }
}
