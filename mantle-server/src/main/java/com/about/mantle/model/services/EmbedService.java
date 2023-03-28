package com.about.mantle.model.services;

import java.util.Map;

import com.about.mantle.model.services.embeds.EmbedContent;

/**
 * Supplies embed content associated with the given URL.
 */
public interface EmbedService {

	/**
	 * Gets the embed content for a given url or null if no providers exist to handle this url.
	 * Options parameter contains options for all embed providers, e.g. twitter, giphy, etc.
	 * Expensive if not cached because it may depend on a request to an external server.
	 */
	EmbedContent getContent(String url, Map<String, Map<String, String>> options);
}
