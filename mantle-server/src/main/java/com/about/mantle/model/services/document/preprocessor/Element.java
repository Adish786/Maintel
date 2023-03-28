package com.about.mantle.model.services.document.preprocessor;

/**
 * Interface for exposing content to be modified by ElementRewriter beans
 */
public interface Element {
	String getAttribute(String name);
	void setAttribute(String name, String value);
	String getTagName();
}
