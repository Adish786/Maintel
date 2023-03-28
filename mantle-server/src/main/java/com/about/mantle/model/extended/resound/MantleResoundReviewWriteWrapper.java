package com.about.mantle.model.extended.resound;

public class MantleResoundReviewWriteWrapper {
    private Integer status;
    private ResoundWriteReviewResponse review;

    public ResoundWriteReviewResponse getReview() {
        return review;
    }

    public void setReview(ResoundWriteReviewResponse review) {
        this.review = review;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
