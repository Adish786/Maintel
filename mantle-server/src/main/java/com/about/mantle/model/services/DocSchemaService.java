package com.about.mantle.model.services;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Service for retrieving /docschema data that is used to populate the about array with things like
 * Medical condition schema and FAQ schema.
 */
public interface DocSchemaService {

	String getDocSchemaByDocId(Long docId);

	public static class DocSchemaRequest {
		private Long docId;

		private DocSchemaRequest(Builder builder) {
			docId = builder.docId;
		}

		public static Builder builder() {
			return new DocSchemaRequest.Builder();
		}

		public Long getDocId() {
			return docId;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder()
				.append(docId)
				.build();
		}

		@Override
		public String toString() {
			return Long.toString(getDocId());
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (!(obj instanceof DocSchemaRequest)) {
				return false;
			}

			DocSchemaRequest other = (DocSchemaRequest) obj;

			return new EqualsBuilder()
					.append(docId, other.docId)
					.build();
		}

		/**
		 *
		 * {@link DocSchemaRequest} Builder
		 *
		 */
		public static class Builder {
			private Long docId;

			public Builder setDocId(Long docId) {
				this.docId = docId;
				return this;
			}

			public DocSchemaRequest build() {
				return new DocSchemaRequest(this);
			}
		}
	}
	
}
