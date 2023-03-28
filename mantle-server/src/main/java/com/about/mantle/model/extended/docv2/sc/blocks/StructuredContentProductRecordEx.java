package com.about.mantle.model.extended.docv2.sc.blocks;

import java.math.BigDecimal;
import org.apache.commons.lang3.BooleanUtils;

import com.about.mantle.infocat.model.product.Product;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import com.about.mantle.model.tasks.TableOfContentsTask;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;

public class StructuredContentProductRecordEx extends
		AbstractStructuredContentContentEx<StructuredContentProductRecordEx.StructuredContentProductRecordDataEx> {

	public static class StructuredContentProductRecordDataEx extends AbstractStructuredContentDataEx {
		private String productId;
		private String superlative;
		private String quote;
		private Boolean hideOnTOC;
		private String headline;
		private ImageEx	overrideImage;

		/*
		 * Even though the product information already exists as part of
		 * infoCatProductRecords on the document,
		 * the product field is being included for ease of block rendering on the
		 * frontend.
		 */
		private Product product;

		private BigDecimal rating;
		private Boolean showRating;
		private SliceableListEx<String> attributeIds;

		@JsonIgnore
		public String generateAnchorId() {
			return TableOfContentsTask.generateAnchorId(this);
		}

		public String getProductId() {
			return productId;
		}

		public void setProductId(String productId) {
			this.productId = productId;
		}

		public String getSuperlative() {
			return superlative;
		}

		public void setSuperlative(String superlative) {
			this.superlative = superlative;
		}

		public String getQuote() {
			return quote;
		}

		public void setQuote(String quote) {
			this.quote = quote;
		}

		public Boolean getHideOnTOC() {
			return hideOnTOC;
		}

		public void setHideOnTOC(Boolean hideOnTOC) {
			this.hideOnTOC = hideOnTOC;
		}

		public String getHeadline() {
			return headline;
		}

		public void setHeadline(String headline) {
			this.headline = headline;
		}

		public Product getProduct() {
			return product;
		}

		public void setProduct(Product product) {
			this.product = product;
		}

		public BigDecimal getRating() {
			return rating;
		}

		public void setRating(BigDecimal rating) {
			this.rating = rating;
		}

		public SliceableListEx<String> getAttributeIds() {
			return attributeIds;
		}

		public void setAttributeIds(SliceableListEx<String> attributeIds) {
			this.attributeIds = attributeIds;
		}

		@JsonIgnore
		public BigDecimal getProductRecordRating() {
			BigDecimal rating = BigDecimal.ZERO;
			Boolean showRating = this.getShowRating();

			if (BooleanUtils.isNotTrue(showRating)) {
				rating = this.getProduct().getRating();
			} else {
				rating = this.getRating();
			}

			if (rating.compareTo(BigDecimal.ZERO) == 0) {
				rating = null;
			}

			return rating;
		}

		public Boolean getShowRating() {
			return showRating;
		}

		public void setShowRating(Boolean showRating) {
			this.showRating = showRating;
		}

		public ImageEx getOverrideImage() {
			return overrideImage;
		}

		public void setOverrideImage(ImageEx overrideImage) {
			this.overrideImage = overrideImage;
		}

		@JsonIgnore
		public ImageEx getProductRecordImage() {
			return getOverrideImage() == null ? getProduct().getImageForUsage("PRIMARY") : getOverrideImage();
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("StructuredContentProductRecordDataEx [productId=");
			builder.append(productId);
			builder.append(", superlative=");
			builder.append(superlative);
			builder.append(", quote=");
			builder.append(quote);
			builder.append(", headline=");
			builder.append(headline);
			builder.append(", rating=");
			builder.append(rating);
			builder.append("]");
			return builder.toString();
		}

	}
}