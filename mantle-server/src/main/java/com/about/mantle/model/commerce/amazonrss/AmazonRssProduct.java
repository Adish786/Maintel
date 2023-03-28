package com.about.mantle.model.commerce.amazonrss;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Used to render Amazon RSS feed template
 */
public class AmazonRssProduct implements Serializable {

	private static final long serialVersionUID = 1L;

	private String url;
	private String headline;
	private String summaryHeading;
	private String summary;
	private Integer rank;
	private String award;
	private Rating rating;

	private AmazonRssProduct(Builder builder) {
		this.setUrl(builder.url);
		this.setHeadline(builder.headline);
		this.setSummaryHeading(builder.summaryHeading);
		this.setSummary(builder.summary);
		this.setRank(builder.rank);
		this.setAward(builder.award);
		this.setRating(builder.rating);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getAward() {
		return award;
	}

	public void setAward(String award) {
		this.award = award;
	}

	public String getSummaryHeading() {
		return summaryHeading;
	}

	public void setSummaryHeading(String summaryHeading) {
		this.summaryHeading = summaryHeading;
	}
	
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public Rating getRating() {
		return rating;
	}
	
	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AmazonRssProduct { ")
		  .append("url: ").append(getUrl())
		  .append(" }");
		return sb.toString();
	}

	public static class Rating {
		private BigDecimal ratingValue;
		private Boolean applyToVariants;
		private int bestRating;
		private int worstRating;

		public BigDecimal getRatingValue() {
			return ratingValue;
		}

		public void setRatingValue(BigDecimal ratingValue) {
			this.ratingValue = ratingValue;
		}

		public boolean getApplyToVariants() {
			return applyToVariants;
		}

		public void setApplyToVariants(Boolean applyToVariants) {
			this.applyToVariants = applyToVariants;
		}

		public int getBestRating() {
			return bestRating;
		}

		public void setBestRating(int bestRating) {
			this.bestRating = bestRating;
		}

		public int getWorstRating() {
			return worstRating;
		}

		public void setWorstRating(int worstRating) {
			this.worstRating = worstRating;
		}
	}
	
	public static class Builder {
		private String url;
		private String headline;
		private String summaryHeading;
		private String summary;
		private Integer rank;
		private String award;
		private Rating rating;

		public Builder() {
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public Builder headline(String headline) {
			this.headline = headline;
			return this;
		}

		public Builder summaryHeading(String summaryHeading) {
			this.summaryHeading = summaryHeading;
			return this;
		}
		
		public Builder summary(String summary) {
			this.summary = summary;
			return this;
		}

		public Builder rank(Integer rank) {
			this.rank = rank;
			return this;
		}

		public Builder award(String award) {
			this.award = award;
			return this;
		}
		
		public Builder rating(Rating rating) {
			this.rating = rating;
			return this;
		}

		public AmazonRssProduct build() {
			if (this.url == null) {
				return null;
			} else {
				return new AmazonRssProduct(this);
			}
		}
	}

}