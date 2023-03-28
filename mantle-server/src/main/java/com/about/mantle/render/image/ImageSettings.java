package com.about.mantle.render.image;

import java.io.Serializable;
import java.util.Arrays;

public class ImageSettings implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Integer width;
	private final Integer height;
	private final String fitInStyle;
	private final String cropSetting;
	private final String[] filters;

	private ImageSettings(Builder builder) {
		this.width = builder.width;
		this.height = builder.height;
		this.fitInStyle = builder.fitInStyle;
		this.cropSetting = builder.cropSetting;
		this.filters = builder.filters;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}

	public String getFitInStyle() {
		return fitInStyle;
	}

	public String getCropSetting() {
		return cropSetting;
	}

	public String[] getFilters() {
		return filters;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cropSetting == null) ? 0 : cropSetting.hashCode());
		result = prime * result + Arrays.hashCode(filters);
		result = prime * result + ((fitInStyle == null) ? 0 : fitInStyle.hashCode());
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageSettings other = (ImageSettings) obj;
		if (cropSetting == null) {
			if (other.cropSetting != null)
				return false;
		} else if (!cropSetting.equals(other.cropSetting))
			return false;
		if (!Arrays.equals(filters, other.filters))
			return false;
		if (fitInStyle == null) {
			if (other.fitInStyle != null)
				return false;
		} else if (!fitInStyle.equals(other.fitInStyle))
			return false;
		if (height == null) {
			if (other.height != null)
				return false;
		} else if (!height.equals(other.height))
			return false;
		if (width == null) {
			if (other.width != null)
				return false;
		} else if (!width.equals(other.width))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ImageSettings [cropSetting=" + cropSetting + ", filters=" + Arrays.toString(filters) + ", fitInStyle="
				+ fitInStyle + ", height=" + height + ", width=" + width + "]";
	}

	public static class Builder {

		private Integer width;
		private Integer height;
		private String fitInStyle;
		private String cropSetting;
		private String[] filters;

		public Builder width(Integer width) {
			this.width = width;
			return this;
		}

		public Builder height(Integer height) {
			this.height = height;
			return this;
		}

		public Builder fitInStyle(String fitInStyle) {
			this.fitInStyle = fitInStyle;
			return this;
		}

		public Builder cropSetting(String cropSetting) {
			this.cropSetting = cropSetting;
			return this;
		}

		public Builder filters(String[] filters) {
			this.filters = filters;
			return this;
		}

		public ImageSettings build() {
			return new ImageSettings(this);
		}

	}

}
