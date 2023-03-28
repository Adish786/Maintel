package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentNestedDataEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentNestedEx;

public class StructuredContentGalleryEx extends AbstractStructuredContentNestedEx<StructuredContentGalleryEx.StructuredContentGalleryDataEx> {

	public static class StructuredContentGalleryDataEx extends AbstractStructuredContentNestedDataEx {
		private String title;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		public StructuredContentGroupType getGroupType() {
			return StructuredContentGroupType.GALLERY;
		}
	}

}
