package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import com.about.mantle.model.extended.docv2.sc.EntityReferenceInfo;

public class StructuredContentRecordImageGalleryEx extends AbstractStructuredContentContentEx<StructuredContentRecordImageGalleryEx.StructuredContentRecordImageGalleryDataEx> {

	public static class StructuredContentRecordImageGalleryDataEx extends AbstractStructuredContentDataEx {

		private String recordId;
		private SliceableListEx<ImageEx> images;

		public String getRecordId() {
			return recordId;
		}

		public void setRecordId(String recordId) {
			this.recordId = recordId;
		}

		public SliceableListEx<ImageEx> getImages() {
			return images;
		}

		public void setImages(SliceableListEx<ImageEx> images) {
			this.images = images;
		}

		@Override
		public String toString() {
			return "StructuredContentRecordImageGalleryDataEx [recordId=" + recordId
					+ ", images=" + images + "]";
		}

	}

}
