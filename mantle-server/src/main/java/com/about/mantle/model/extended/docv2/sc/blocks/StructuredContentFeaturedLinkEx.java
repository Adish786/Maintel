package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class StructuredContentFeaturedLinkEx extends AbstractStructuredContentContentEx<StructuredContentFeaturedLinkEx.StructuredContentFeaturedLinkDataEx> {
	
	/**
     * Implements the `FeaturedLinkContentData` definition of the Structured Content schema
     */
	
	public static class StructuredContentFeaturedLinkDataEx extends AbstractStructuredContentDataEx {

		private String uri;
		private String text;
		private String description;
		private BaseDocumentEx document;

		@JsonIgnore
		public String getEffectiveAnchorText() {
			return text != null && !text.isEmpty() ? text : (document != null ? document.getBestTitle() : null);
		}

		public String getUri() {
			return uri;
		}
		
		public void setUri(String link) {
			this.uri = link;
		}
		
		public String getText() {
			return text;
		}
		
		public void setText(String text) {
			this.text = text;
		}
		
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public BaseDocumentEx getDocument() {
			return document;
		}

		public void setDocument(BaseDocumentEx document) {
			this.document = document;
		}

		@Override
		public String toString() {
			return "StructuredContentFeaturedLinkDataEx{" +
					"uri='" + uri + '\'' +
					", text='" + text + '\'' +
					", description='" + description + '\'' +
					", document=" + document +
					"} " + super.toString();
		}
	}
}
