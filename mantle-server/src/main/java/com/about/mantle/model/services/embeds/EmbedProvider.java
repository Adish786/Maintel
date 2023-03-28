package com.about.mantle.model.services.embeds;

import java.util.Map;

/**
 * Used by the `EmbedService` to supply embed content associated with a given url.
 */
public interface EmbedProvider {

	/**
	 * Embed content associated with the given url using the provided options.
	 * Should return null if this provider cannot supply the content for the given url.
	 */
	EmbedContent getContent(String url, Map<String, Map<String, String>> options);

}
