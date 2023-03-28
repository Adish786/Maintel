package com.about.mantle.model.extended.resound;

public class MantleResoundReviewStatusWrapper {
    private Integer status;
    private ResoundReviewResponse review;

    public ResoundReviewResponse getReview() {
        return review;
    }

    public void setReview(ResoundReviewResponse review) {
        this.review = review;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
