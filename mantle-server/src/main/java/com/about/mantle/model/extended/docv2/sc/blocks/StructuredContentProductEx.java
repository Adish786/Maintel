package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.PriceInfo;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentProductEx extends AbstractStructuredContentContentEx<StructuredContentProductEx.StructuredContentProductDataEx> {
	public static class StructuredContentProductDataEx extends AbstractStructuredContentDataEx {
		// you'd expect image and commerceButton to be of type StructuredContentImageEx and StructuredContentCommerceInfoEx respectively
		// but unless the type is AbstractStructuredContentContentEx for both deserialization will fail
		private AbstractStructuredContentContentEx<?> image;
		private AbstractStructuredContentContentEx<?> commerceButton;
		private PriceInfo salePrice;
		private PriceInfo price; // retail (non-sale) price
		private String brand; // e.g. Converse
		private String styleName; // product name, e.g. Chuck Taylor All Star

		public AbstractStructuredContentContentEx<?> getImage() {
			return image;
		}

		public void setImage(AbstractStructuredContentContentEx<?> image) {
			this.image = image;
		}

		public AbstractStructuredContentContentEx<?> getCommerceButton() {
			return commerceButton;
		}

		public void setCommerceButton(AbstractStructuredContentContentEx<?> commerceButton) {
			this.commerceButton = commerceButton;
		}

		public PriceInfo getSalePrice() {
			return salePrice;
		}

		public void setSalePrice(PriceInfo salePrice) {
			this.salePrice = salePrice;
		}

		public PriceInfo getPrice() {
			return price;
		}

		public void setPrice(PriceInfo price) {
			this.price = price;
		}

		public String getBrand() {
			return brand;
		}

		public void setBrand(String brand) {
			this.brand = brand;
		}

		public String getStyleName() {
			return styleName;
		}

		public void setStyleName(String styleName) {
			this.styleName = styleName;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("StructuredContentProductDataEx{");
			sb.append("image=").append(image);
			sb.append(", commerceButton=").append(commerceButton);
			sb.append(", salePrice=").append(salePrice);
			sb.append(", price=").append(price);
			sb.append(", brand=").append(brand);
			sb.append(", styleName=").append(styleName);
			sb.append('}');
			return sb.toString();
		}
	}
}
