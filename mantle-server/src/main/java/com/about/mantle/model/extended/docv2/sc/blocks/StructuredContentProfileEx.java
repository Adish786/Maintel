package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.infocat.model.product.Product;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentProfileEx extends AbstractStructuredContentContentEx<StructuredContentProfileEx.StructuredContentProfileDataEx> {
	public static class StructuredContentProfileDataEx extends AbstractStructuredContentDataEx {
		private String profileId;

		/*
		 * Product data stored on document root has implications on schema structure.
		 * Profile data is currently separate. This may be rolled up to a separate
		 * field on the document but for now is only stored on its respective blocks.
		 * Even then, it will continue to be stored here for ease of use on the
		 * frontend.
		 */
		private Product profile;

		public String getProfileId() {
			return profileId;
		}

		public void setProfileId(String profileId) {
			this.profileId = profileId;
		}

		public Product getProfile() {
			return profile;
		}

		public void setProfile(Product profile) {
			this.profile = profile;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("StructuredContentProfileDataEx [profileId=");
			builder.append(profileId);
			builder.append("]");
			return builder.toString();
		}

	}
}
