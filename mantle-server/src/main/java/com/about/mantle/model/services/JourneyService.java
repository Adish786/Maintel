package com.about.mantle.model.services;

import com.about.mantle.model.journey.JourneyRelationshipType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.about.mantle.model.journey.JourneyRoot;
import org.apache.commons.lang3.tuple.Pair;

public interface JourneyService {

	/* Needed because clients of this service already expect a non-null return value even for non-journey documents.
	 * Might be worth changing this in the next breaking change.  See GLBE-6411
	 */
	JourneyRoot NULL_JOURNEY = new JourneyRoot(null);

    /**
     * Returns the JourneyRoot as well as the relationship between the document specified in the reqCtx and the journey
     *
     * @param reqCtx
     * @return
     */
    Pair<JourneyRoot, JourneyRelationshipType> getJourneyRootAndRelationship(JourneyRequestContext reqCtx);

	class JourneyRequestContext {
		private final Long docId;
		private final boolean includeDocumentSummaries;
		private final String projection;

		private JourneyRequestContext(Builder builder) {
			if (builder.docId == null) {
				throw new IllegalArgumentException("docId not provided.");
			} else {
				docId = builder.docId;
			}

			includeDocumentSummaries = (builder.includeDocumentSummaries == null) ? false
					: builder.includeDocumentSummaries;
			projection = builder.projection;
		}

		public Long getDocId() {
			return docId;
		}

		public Boolean getIncludeDocumentSummaries() {
			return includeDocumentSummaries;
		}

		public String getProjection() {
			return projection;
		}

		@Override
		public String toString() {
			// @formatter:off
			return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
					.append("docId", docId)
					.append("includeDocumentSummaries", includeDocumentSummaries)
					.append("projection", projection)
					.appendSuper(super.toString())
					.build();
			// @formatter:on
		}

		public static class Builder {
			private Long docId;
			private Boolean includeDocumentSummaries;
			private String projection;

			public Builder setDocId(Long docId) {
				this.docId = docId;
				return this;
			}

			public Builder setIncludeDocumentSummaries(Boolean includeDocumentSummaries) {
				this.includeDocumentSummaries = includeDocumentSummaries;
				return this;
			}

			public Builder setProjection(String projection) {
				this.projection = projection;
				return this;
			}

			public JourneyRequestContext build() {
				return new JourneyRequestContext(this);
			}
		}
	}

}
