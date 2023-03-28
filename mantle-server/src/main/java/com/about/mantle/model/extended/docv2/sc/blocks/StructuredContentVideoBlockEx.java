package com.about.mantle.model.extended.docv2.sc.blocks;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Inline Video SC block
 *
 */
public class StructuredContentVideoBlockEx extends AbstractStructuredContentContentEx<StructuredContentVideoBlockEx.StructuredContentVideoBlockDataEx>{

	/**
	 * Inline Video SC block data
	 */
	public static class StructuredContentVideoBlockDataEx extends AbstractStructuredContentDataEx {
		
		private String programmingTitle;
		private Boolean showTitle;
		private Long docId;
		private BaseDocumentEx document;
		private String caption;
		private String featureTitle;
		private String featureLink;
		
		public String getProgrammingTitle() {
			return programmingTitle;
		}
		public void setProgrammingTitle(String programmingTitle) {
			this.programmingTitle = programmingTitle;
		}
		public Boolean getShowTitle() {
			return showTitle;
		}
		public void setShowTitle(Boolean showTitle) {
			this.showTitle = showTitle;
		}
		public Long getDocId() {
			return docId;
		}
		public void setDocId(Long docId) {
			this.docId = docId;
		}
		public BaseDocumentEx getDocument() {
			return document;
		}
		public void setDocument(BaseDocumentEx document) {
			this.document = document;
		}
		public String getCaption() {
			return caption;
		}
		public void setCaption(String caption) {
			this.caption = caption;
		}
		public String getFeatureTitle() {
			return featureTitle;
		}
		public void setFeatureTitle(String featureTitle) {
			this.featureTitle = featureTitle;
		}
		public String getFeatureLink() {
			return featureLink;
		}
		public void setFeatureLink(String featureLink) {
			this.featureLink = featureLink;
		}
		
		@JsonIgnore
		public String getBestTitle() {
			if (showTitle != null && showTitle && isNotEmpty(getProgrammingTitle())) {
				return getProgrammingTitle();        
			}
			return document != null ? document.getTitle() : null;
		}

		@Override
		public String toString() {
			return "StructuredContentVideoBlockDataEx{" +
					"programmingTitle='" + programmingTitle + '\'' +
					", showTitle=" + showTitle +
					", docId=" + docId +
					", document=" + document +
					", bestTitle=" + getBestTitle() +
					", caption='" + caption + '\'' +
					", featureTitle='" + featureTitle + '\'' +
					", featureLink='" + featureLink + '\'' +
					"} " + super.toString();
		}

	}
}
