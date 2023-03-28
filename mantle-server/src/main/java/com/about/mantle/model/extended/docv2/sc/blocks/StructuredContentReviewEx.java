package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class StructuredContentReviewEx extends AbstractStructuredContentContentEx<StructuredContentReviewEx.StructuredContentReviewDataEx> {
	public static class StructuredContentReviewDataEx extends AbstractStructuredContentDataEx {
		private Long docId;
		private ReviewStructuredContentDocumentEx document;

		public Long getDocId() {
			return docId;
		}

		public void setDocId(Long docId) {
			this.docId = docId;
		}

		public ReviewStructuredContentDocumentEx getDocument() {
			return document;
		}

		public void setDocument(ReviewStructuredContentDocumentEx document) {
			this.document = document;
		}

		@JsonIgnore
		public ReviewRatingEx getRating() {
			return document.getRating();
		}

		@JsonIgnore
		public ReviewEntityEx getEntity() {
			return document.getEntity();
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("StructuredContentReviewEx [docId=");
			builder.append(docId);
			builder.append(", document=");
			builder.append(document);
			builder.append("]");
			return builder.toString();
		}

	}
}
