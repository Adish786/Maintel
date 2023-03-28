package com.about.mantle.model.extended.resound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ResoundImageEntity {
	private Udf udf;
	
	public Udf getUdf() {
		return udf;
	}

	public void setUdf(Udf udf) {
		this.udf = udf;
	}

	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class Udf {
		private Image original;

		public Image getOriginal() {
			return original;
		}

		public void setOriginal(Image original) {
			this.original = original;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class Image {
		private String src;
		private String mime_type;
		private Integer width;
		private Integer height;

		public String getSrc() {
			return src;
		}

		public void setSrc(String src) {
			this.src = src;
		}

		public String getMime_type() {
			return mime_type;
		}

		public void setMime_type(String mime_type) {
			this.mime_type = mime_type;
		}

		public Integer getWidth() {
			return width;
		}

		public void setWidth(Integer width) {
			this.width = width;
		}

		public Integer getHeight() {
			return height;
		}

		public void setHeight(Integer height) {
			this.height = height;
		}
	}
}
