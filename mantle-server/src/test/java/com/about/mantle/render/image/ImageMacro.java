package com.about.mantle.render.image;

public class ImageMacro {
	private final String url;
	private final String img;
	private final String alt;
	private final String fitInStyle;
	private final String cropSetting;
	private final boolean forceSize;
	private final boolean lazyload;
	private final int width;
	private final int height;
	private final String[] filters;

	private ImageMacro(Builder builder) {
		this.url = builder.url;
		this.img = builder.img;
		this.alt = builder.alt;
		this.fitInStyle = builder.fitInStyle;
		this.cropSetting = builder.cropSetting;
		this.forceSize = builder.forceSize;
		this.lazyload = builder.lazyload;
		this.width = builder.width;
		this.height = builder.height;
		this.filters = builder.filters;
	}

	public String getUrl() {
		return url;
	}

	public String getImg() {
		return img;
	}

	public String getAlt() {
		return alt;
	}

	public String getFitInStyle() {
		return fitInStyle;
	}

	public String getCropSetting() {
		return cropSetting;
	}

	public boolean isForceSize() {
		return forceSize;
	}

	public boolean isLazyload() {
		return lazyload;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String[] getFilters() {
		return filters;
	}

	public static class Builder {
		private String url;
		private String img;
		private String alt;
		private String fitInStyle;
		private String cropSetting;
		private boolean forceSize;
		private boolean lazyload;
		private int width;
		private int height;
		private String[] filters;

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public Builder img(String img) {
			this.img = img;
			return this;
		}

		public Builder alt(String alt) {
			this.alt = alt;
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

		public Builder forceSize(boolean forceSize) {
			this.forceSize = forceSize;
			return this;
		}

		public Builder lazyload(boolean lazyload) {
			this.lazyload = lazyload;
			return this;
		}

		public Builder width(int width) {
			this.width = width;
			return this;
		}

		public Builder height(int height) {
			this.height = height;
			return this;
		}

		public Builder filters(String[] filters) {
			this.filters = filters;
			return this;
		}

		public ImageMacro build() {
			return new ImageMacro(this);
		}
	}
}
