package com.about.mantle.model;

import java.io.Serializable;

/**
 * Represents a component that was rendered on another server.  Provided by
 * {@link com.about.mantle.model.services.ExternalComponentService}
 */
public class ExternalComponent implements Serializable {

	private static final long serialVersionUID = 1L;

	private String css;
	private String html;
	private String javascript;
	private String svg;

	public ExternalComponent(Builder builder) {
		this.setCss(builder.css);
		this.setHtml(builder.html);
		this.setJavascript(builder.javascript);
		this.setSvg(builder.svg);
	}

	public String getCss() {
		return this.css;
	}

	public String getHtml() {
		return this.html;
	}

	public String getJavascript() {
		return this.javascript;
	}

	public String getSvg() {
		return this.svg;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public void setJavascript(String javascript) {
		this.javascript = javascript;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ExternalComponent {\n")
		  .append("html: ").append(this.html).append(",\n")
		  .append("css: ").append(this.css).append(",\n")
		  .append("js: ").append(this.javascript).append("\n")
		  .append("svg: ").append(this.svg).append("\n")
		  .append("}");
		return sb.toString();
	}

	public static class Builder {
		private String css;
		private String html;
		private String javascript;
		private String svg;

		public Builder css(String css) {
			this.css = css;
			return this;
		}

		public Builder html(String html) {
			this.html = html;
			return this;
		}

		public Builder javascript (String javascript) {
			this.javascript = javascript;
			return this;
		}

		public Builder svg(String svg) {
			this.svg = svg;
			return this;
		}

		public ExternalComponent build() {
			return new ExternalComponent(this);
		}
	}

}
