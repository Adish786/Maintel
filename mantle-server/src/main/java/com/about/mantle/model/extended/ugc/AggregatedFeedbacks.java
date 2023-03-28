package com.about.mantle.model.extended.ugc;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.SliceableListEx;

public class AggregatedFeedbacks implements Serializable {
	private static final long serialVersionUID = 1L;

	private AverageRating averageRating;
	private AverageRating averageMadeIt;
	private SliceableListEx<ScoreHistogramEntry> scoreHistogram;
	private SliceableListEx<ThumbsSignalHistogramEntry> thumbsSignalHistogram;
	private Integer reviewCount;
	private MostHelpfulFeedbacks mostHelpful;
	private SliceableListEx<Feedback> feedbacks;

	public AverageRating getAverageRating() {
		return averageRating;
	}
	public void setAverageRating(AverageRating averageRating) {
		this.averageRating = averageRating;
	}
	public AverageRating getAverageMadeIt() {
		return averageMadeIt;
	}
	public void setAverageMadeIt(AverageRating averageMadeIt) {
		this.averageMadeIt = averageMadeIt;
	}
	public Integer getReviewCount() {
		return reviewCount;
	}
	public void setReviewCount(Integer reviewCount) {
		this.reviewCount = reviewCount;
	}
	public SliceableListEx<ScoreHistogramEntry> getScoreHistogram() {
		return scoreHistogram;
	}
	public void setScoreHistogram(SliceableListEx<ScoreHistogramEntry> scoreHistogram) {
		this.scoreHistogram = scoreHistogram;
	}
	public SliceableListEx<ThumbsSignalHistogramEntry> getThumbsSignalHistogram() {
		return thumbsSignalHistogram;
	}
	public void setThumbsSignalHistogram(SliceableListEx<ThumbsSignalHistogramEntry> thumbsSignalHistogram) {
		this.thumbsSignalHistogram = thumbsSignalHistogram;
	}
	public MostHelpfulFeedbacks getMostHelpful() {
		return mostHelpful;
	}
	public void setMostHelpful(MostHelpfulFeedbacks mostHelpful) {
		this.mostHelpful = mostHelpful;
	}
	public SliceableListEx<Feedback> getFeedbacks() {
		return feedbacks;
	}
	public void setFeedbacks(SliceableListEx<Feedback> feedbacks) {
		this.feedbacks = feedbacks;
	}

}