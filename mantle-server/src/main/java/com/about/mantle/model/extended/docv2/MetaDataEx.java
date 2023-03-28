package com.about.mantle.model.extended.docv2;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.joda.time.DateTime;


public class MetaDataEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final MetaDataEx EMPTY = new MetaDataEx() {

		private static final long serialVersionUID = 1L;

		@Override
		public void setUrl(String url) {
		}

		@Override
		public void setDocId(Long docId) {
		}
		
		@Override
		public void setHasTableOfContents(Boolean hasTableOfContents) {
		}

		@Override
		public void setEntities(List<Entity> entities) {
		}

		@Override
		public void setRecircKeywords(SliceableListEx<RecircKeyword> recircKeywords) {
		}

		@Override
		public void setBadge(String badge) {
		}

		@Override
		public void setmTaxonomyIds(SliceableListEx<String> mTaxonomyIds) {
		}

		@Override
		public void setAdTaxonomyIds(SliceableListEx<String> adTaxonomyIds) {
		}

		@Override
		public void setNlp(NLP nlp) {
		}
	};

	private String url;
	private Long docId;
	private Boolean hasTableOfContents = false;
	private List<Entity> entities = Collections.emptyList();
	private SliceableListEx<RecircKeyword> recircKeywords = SliceableListEx.emptyList();
	private Review review;
	private String appleArticleId;
	private String badge;
	private SliceableListEx<String> mTaxonomyIds;
	private SliceableListEx<String> adTaxonomyIds;
	private NLP nlp;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}
	
	public Boolean getHasTableOfContents() {
		return this.hasTableOfContents;
	}
	
	public void setHasTableOfContents(Boolean hasTableOfContents) {
		this.hasTableOfContents = hasTableOfContents != null ? hasTableOfContents : false;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = ListUtils.emptyIfNull(entities);
	}

	public SliceableListEx<RecircKeyword> getRecircKeywords() {
		return recircKeywords;
	}

	public void setRecircKeywords(SliceableListEx<RecircKeyword> recircKeywords) {
		this.recircKeywords = SliceableListEx.emptyIfNull(recircKeywords);
	}

	public static MetaDataEx empty() {
		return EMPTY;
	}

	public static MetaDataEx emptyIfNull(MetaDataEx metaData) {
		return metaData != null ? metaData : empty();
	}

	public Review getReview() {
		return review;
	}

	public void setReview(Review review) {
		this.review = review;
	}

	public String getAppleArticleId() {
		return appleArticleId;
	}

	public void setAppleArticleId(String appleArticleId) {
		this.appleArticleId = appleArticleId;
	}

	public String getBadge() {
		return badge;
	}

	public void setBadge(String badge) {
		this.badge = badge;
	}

	public SliceableListEx<String> getmTaxonomyIds() {
		return mTaxonomyIds;
	}

	public void setmTaxonomyIds(SliceableListEx<String> mTaxonomyIds) {
		this.mTaxonomyIds = mTaxonomyIds;
	}

	public SliceableListEx<String> getAdTaxonomyIds() {
		return adTaxonomyIds;
	}

	public void setAdTaxonomyIds(SliceableListEx<String> adTaxonomyIds) {
		this.adTaxonomyIds = adTaxonomyIds;
	}

	public NLP getNlp() {
		return nlp;
	}

	public void setNlp(NLP nlp) {
		this.nlp = nlp;
	}

	public static class Entity implements Serializable {
		private static final long serialVersionUID = 1L;

		private String id;
		private String name;
		private BigDecimal score;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public BigDecimal getScore() {
			return score;
		}

		public void setScore(BigDecimal score) {
			this.score = score;
		}
	}
	
	public static class RecircKeyword implements Serializable {
		private static final long serialVersionUID = 1L;

		private String term;
		private String url;

		public String getTerm() {
			return term;
		}

		public void setTerm(String term) {
			this.term = term;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
	
	public static class Review implements Serializable {
		private static final long serialVersionUID = 1L;

		private String authorId;
		private Long userId;
		private String type;
		private DateTime lastReviewed;

		public String getAuthorId() {
			return authorId;
		}

		public void setAuthorId(String authorId) {
			this.authorId = authorId;
		}

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public DateTime getLastReviewed() {
			return lastReviewed;
		}

		public void setLastReviewed(DateTime lastReviewed) {
			this.lastReviewed = lastReviewed;
		}

	}

	public static class NLP implements Serializable {
		private static final long serialVersionUID = 1L;

		private String sentimentLabel;
		private BigDecimal sentimentScore;
		private BigDecimal sentimentMagnitude;
		private SliceableListEx<NLPEntity> entities;
		private String payload;
		private SliceableListEx<NLPCategory> categories;
		private SliceableListEx<String> rollupEntities;
		private SliceableListEx<String> rollupCategories;
		private String language;
		// populated with nlpPreprocessor
		private List<String> concepts;
		private List<String> taxons;

		public String getSentimentLabel() {
			return sentimentLabel;
		}

		public void setSentimentLabel(String sentimentLabel) {
			this.sentimentLabel = sentimentLabel;
		}

		public BigDecimal getSentimentScore() {
			return sentimentScore;
		}

		public void setSentimentScore(BigDecimal sentimentScore) {
			this.sentimentScore = sentimentScore;
		}

		public BigDecimal getSentimentMagnitude() {
			return sentimentMagnitude;
		}

		public void setSentimentMagnitude(BigDecimal sentimentMagnitude) {
			this.sentimentMagnitude = sentimentMagnitude;
		}

		public SliceableListEx<NLPEntity> getEntities() {
			return entities;
		}

		public void setEntities(SliceableListEx<NLPEntity> entities) {
			this.entities = entities;
		}

		public String getPayload() {
			return payload;
		}

		public void setPayload(String payload) {
			this.payload = payload;
		}

		public SliceableListEx<NLPCategory> getCategories() {
			return categories;
		}

		public void setCategories(SliceableListEx<NLPCategory> categories) {
			this.categories = categories;
		}

		public SliceableListEx<String> getRollupEntities() {
			return rollupEntities;
		}

		public void setRollupEntities(SliceableListEx<String> rollupEntities) {
			this.rollupEntities = rollupEntities;
		}

		public SliceableListEx<String> getRollupCategories() {
			return rollupCategories;
		}

		public void setRollupCategories(SliceableListEx<String> rollupCategories) {
			this.rollupCategories = rollupCategories;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public List<String> getConcepts() {
			return concepts;
		}

		public void setConcepts(List<String> concepts) {
			this.concepts = concepts;
		}

		public List<String> getTaxons() {
			return taxons;
		}

		public void setTaxons(List<String> taxons) {
			this.taxons = taxons;
		}

		public static class NLPCategory implements Serializable {
			private static final long serialVersionUID = 1L;

			private String name;
			private BigDecimal confidence;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public BigDecimal getConfidence() {
				return confidence;
			}

			public void setConfidence(BigDecimal confidence) {
				this.confidence = confidence;
			}
		}

		public static class NLPEntity implements Serializable {
			private static final long serialVersionUID = 1L;

			private BigDecimal salience;
			private String name;
			private String type;

			public BigDecimal getSalience() {
				return salience;
			}

			public void setSalience(BigDecimal salience) {
				this.salience = salience;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}
		}
	}
}
