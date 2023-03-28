package com.about.mantle.model.extended.docv2.sc.blocks;

import java.math.BigDecimal;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

/**
 * Represents StarRating SC block
 * 
 */
public class StructuredContentStarRatingEx
		extends AbstractStructuredContentContentEx<StructuredContentStarRatingEx.StructuredContentStarRatingDataEx> {

	/**
	 * Represents StarRating SC block's data.
	 */
	public static class StructuredContentStarRatingDataEx extends AbstractStructuredContentDataEx {

		private BigDecimal rating;

		public BigDecimal getRating() {
			return rating;
		}

		public void setRating(BigDecimal rating) {
			this.rating = rating;
		}

	}

}
