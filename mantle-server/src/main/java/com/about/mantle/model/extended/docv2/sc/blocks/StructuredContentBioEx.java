package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentBioEx extends AbstractStructuredContentContentEx<StructuredContentBioEx.StructuredContentBioDataEx> {

	public static class StructuredContentBioDataEx extends AbstractStructuredContentDataEx {

		private Long docId;
		private BaseDocumentEx document;
		private Boolean showLink;

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

		public Boolean getShowLink() {
			return showLink;
		}

		public void setShowLink(Boolean showLink) {
			this.showLink = showLink;
		}

	}
}
