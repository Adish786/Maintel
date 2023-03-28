package com.about.mantle.model.services.document.preprocessor;

/**
 * Wrapper class to expose parts of a JSoup element for modification by ElementRewriters
 */
public class JSoupElementWrapper implements Element {
	private org.jsoup.nodes.Element jsoupElement;

	public JSoupElementWrapper(org.jsoup.nodes.Element jsoupElement) {
		this.jsoupElement = jsoupElement;
	}

	public String getAttribute(String attributeKey) {
		return jsoupElement.attr(attributeKey);
	}

	public void setAttribute(String attributeKey, String attributeValue) {
		jsoupElement.attr(attributeKey, attributeValue);
	}

	public String getTagName() {
		return jsoupElement.tagName();
	}
}
