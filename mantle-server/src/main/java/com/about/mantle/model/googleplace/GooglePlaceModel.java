package com.about.mantle.model.googleplace;

import java.io.Serializable;

import com.google.maps.model.AddressType;

/**
 * Google Place details model to get comprehensive information about the
 * requested placeId. Such as complete formatted address, adr_address, phone
 * number, directions url etc.
 */
public class GooglePlaceModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Formatted complete human readable address string
	 */
	private String formattedAddress;
	/**
	 * Representation of the place's address in the
	 * <a href="http://microformats.org/wiki/adr">adr microformat</a>
	 */
	private String adrAddress;
	private String phone;
	private String website;
	private String placeId;
	private double lat;
	private double lng;
	/**
	 * Google maps directions url
	 */
	private String url;
	private String name;
	private AddressType[] types;

	public GooglePlaceModel() {
	}

	private GooglePlaceModel(Builder builder) {
		this.formattedAddress = builder.formattedAddress;
		this.adrAddress = builder.adrAddress;
		this.phone = builder.phone;
		this.website = builder.website;
		this.placeId = builder.placeId;
		this.url = builder.url;
		this.lat = builder.lat;
		this.lng = builder.lng;
		this.name = builder.name;
		this.types = builder.types;
	}

	public static class Builder {
		private String formattedAddress;
		private String adrAddress;
		private String phone;
		private String website;
		private String placeId;
		private String url;
		private double lat;
		private double lng;
		private String name;
		private AddressType[] types;

		public Builder() {
		}

		public Builder formattedAddress(String formattedAddress) {
			this.formattedAddress = formattedAddress;
			return this;
		}

		public Builder adrAddress(String adrAddress) {
			this.adrAddress = adrAddress;
			return this;
		}

		public Builder phone(String phone) {
			this.phone = phone;
			return this;
		}

		public Builder website(String website) {
			this.website = website;
			return this;
		}

		public Builder placeId(String placeId) {
			this.placeId = placeId;
			return this;
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}
		
		public Builder lat(double lat) {
			this.lat = lat;
			return this;
		}
		
		public Builder lng(double lng) {
			this.lng = lng;
			return this;
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder types(AddressType[] types) {
			this.types = types;
			return this;
		}

		public GooglePlaceModel build() {
			return new GooglePlaceModel(this);
		}
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public String getAdrAddress() {
		return adrAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public void setAdrAddress(String adrAddress) {
		this.adrAddress = adrAddress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AddressType[] getTypes() {
		return types;
	}

	public void setTypes(AddressType[] types) {
		this.types = types;
	}

	@Override
	public String toString() {
		return "GooglePlaceModel {formattedAddress=" + formattedAddress + ", adrAddress=" + adrAddress + ", phone="
				+ phone + ", website=" + website + ", placeId=" + placeId + ", lat=" + lat + ", lng=" + lng + ", url="
				+ url + ", name=" + name + ", types=" + types + "}";
	}

}
