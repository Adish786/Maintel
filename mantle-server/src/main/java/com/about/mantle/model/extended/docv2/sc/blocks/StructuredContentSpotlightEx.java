package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.ReferencedDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentSpotlightEx extends AbstractStructuredContentContentEx<StructuredContentSpotlightEx.StructuredContentSpotlightDataEx>{

	public static class StructuredContentSpotlightDataEx extends AbstractStructuredContentDataEx {
		
		private String heading;
		private SliceableListEx<ReferencedDocumentEx> documents;

		public String getHeading() {
			return heading;
		}

		public void setHeading(String heading) {
			this.heading = heading;
		}

		public SliceableListEx<ReferencedDocumentEx> getDocuments() {
			return documents;
		}

		public void setDocuments(SliceableListEx<ReferencedDocumentEx> documents) {
			this.documents = documents;
		}

		@Override
		public String toString() {
			return "StructuredContentSpotlightDataEx{" +
					"heading='" + heading + '\'' +
					", documents=" + documents +
					"} " + super.toString();
		}

	}
	
}
