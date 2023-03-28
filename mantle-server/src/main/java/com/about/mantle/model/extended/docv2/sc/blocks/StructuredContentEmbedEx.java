package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentEmbedEx extends AbstractStructuredContentContentEx<StructuredContentEmbedEx.StructuredContentEmbedDataEx> {

	public static class StructuredContentEmbedDataEx extends AbstractStructuredContentDataEx {

		private String uri;
		private String title;

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		public String toString() {
			return "StructuredContentEmbedDataEx{" +
			       "uri=" + uri + ", " +
			       "title=" + title +
			       '}';
		}

	}

}
