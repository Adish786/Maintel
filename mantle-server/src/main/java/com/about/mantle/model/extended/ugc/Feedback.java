package com.about.mantle.model.extended.ugc;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;

public class Feedback implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long docId;
	private String legacyId; // Meredith ID for the document
	private String userId;
	private DateTime created;
	private ThumbsSignal thumbsSignal;
	private String review;
	private String displayName;
	private String profileUrl;
	private Short starRating;
	private Short madeIt;
	private List<Photo> photos;
	private Integer helpfulCount;
	private String legacyFeedbackId;

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public String getLegacyId() {
		return legacyId;
	}

	public void setLegacyId(String legacyId) {
		this.legacyId = legacyId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ThumbsSignal getThumbsSignal() {
		return thumbsSignal;
	}

	public void setThumbsSignal(ThumbsSignal thumbs_SIGNAL) {
		this.thumbsSignal = thumbs_SIGNAL;
	}

	public DateTime getCreated() {
		return created;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
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

	public Short getStarRating() {
		return starRating;
	}

	public void setStarRating(Short starRating) {
		this.starRating = starRating;
	}

	public Short getMadeIt() {
		return madeIt;
	}

	public void setMadeIt(Short madeIt) {
		this.madeIt = madeIt;
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

	public Integer getHelpfulCount() {
		return helpfulCount;
	}

	public void setHelpfulCount(Integer helpfulCount) {
		this.helpfulCount = helpfulCount;
	}

	public String getLegacyFeedbackId() {
		return legacyFeedbackId;
	}

	public void setLegacyFeedbackId(String legacyFeedbackId) {
		this.legacyFeedbackId = legacyFeedbackId;
	}
}