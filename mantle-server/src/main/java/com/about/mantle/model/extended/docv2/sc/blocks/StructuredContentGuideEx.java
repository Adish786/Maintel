package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentGuideEx extends AbstractStructuredContentContentEx<StructuredContentGuideEx.StructuredContentGuideDataEx> {
	public static class StructuredContentGuideDataEx extends AbstractStructuredContentDataEx {
		private String title;
		private String content;
		private ImageEx image;
		private String downloadLink;
		private String sailthruListId;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public ImageEx getImage() {
			return image;
		}

		public void setImage(ImageEx image) {
			this.image = image;
		}

		public String getDownloadLink() {
			return downloadLink;
		}

		public void setDownloadLink(String downloadLink) {
			this.downloadLink = downloadLink;
		}

		public String getSailthruListId() {
			return sailthruListId;
		}

		public void setSailthruListId(String sailthruListId) {
			this.sailthruListId = sailthruListId;
		}
	}
}