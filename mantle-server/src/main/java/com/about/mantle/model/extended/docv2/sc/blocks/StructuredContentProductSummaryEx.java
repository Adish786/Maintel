package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import com.about.mantle.model.product.BaseProductEx;

public class StructuredContentProductSummaryEx extends AbstractStructuredContentContentEx<StructuredContentProductSummaryEx.StructuredContentProductSummaryDataEx>{
	
	public static class StructuredContentProductSummaryDataEx extends AbstractStructuredContentDataEx {
		private SliceableListEx<BaseProductEx> products;

		public SliceableListEx<BaseProductEx> getProducts() {
			return products;
		}

		public void setProducts(SliceableListEx<BaseProductEx> products) {
			this.products = products;
		}
	}

}
