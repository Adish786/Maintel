package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.CommerceInfoEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

/**
 * Represents commerce SC block.
 */
public class StructuredContentCommerceInfoEx extends
		AbstractStructuredContentContentEx<StructuredContentCommerceInfoEx.StructuredContentCommerceInfoDataEx> {

	public static class StructuredContentCommerceInfoDataEx extends AbstractStructuredContentDataEx {

		private SliceableListEx<CommerceInfoEx> commerceInfo;
		private String callToAction;

		public SliceableListEx<CommerceInfoEx> getCommerceInfo() {
			return commerceInfo;
		}

		public void setCommerceInfo(SliceableListEx<CommerceInfoEx> commerceInfo) {
			this.commerceInfo = commerceInfo;
		}

		@Override
		public String toString() {
			return "StructuredContentCommerceInfoDataEx [commerceInfo=" + commerceInfo + "]";
		}

		public String getCallToAction() {
			return callToAction;
		}

		public void setCallToAction(String callToAction) {
			this.callToAction = callToAction;
		}

	}

}
