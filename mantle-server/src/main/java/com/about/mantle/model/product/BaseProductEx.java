package com.about.mantle.model.product;

import java.io.Serializable;

import com.about.mantle.model.creditcard.CreditCardEx;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;


@JsonTypeInfo(use = Id.NAME, visible = true, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = GenericProductEx.class, name = "PRODUCT"),
		@JsonSubTypes.Type(value = GenericProductEx.class, name = "PRODUCTRECORD"),
		@JsonSubTypes.Type(value = FinanceProductEx.class, name = "FINANCE"),
		@JsonSubTypes.Type(value = CreditCardEx.class, name = "CREDITCARD")})
public abstract class BaseProductEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	protected ProductType type;
	private String name;
	private String productTitle;
	private String url;
	private ImageEx image = ImageEx.EMPTY;
	private String description;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ImageEx getImage() {
		return image;
	}

	public void setImage(ImageEx image) {
		this.image = ImageEx.emptyIfNull(image);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public ProductType getType() {
		return type;
	}

	public void setType(ProductType type) {
		this.type = type;
	}

	public enum ProductType {
		FINANCE,
		PRODUCT,
		CREDITCARD,
		PRODUCTRECORD
	}
	
}
