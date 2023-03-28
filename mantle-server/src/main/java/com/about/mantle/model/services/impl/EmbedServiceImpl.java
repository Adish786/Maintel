package com.about.mantle.model.services.impl;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.mantle.model.services.EmbedService;
import com.about.mantle.model.services.embeds.EmbedContent;
import com.about.mantle.model.services.embeds.EmbedProvider;

/**
 * Cycles through a list of embed providers until it finds one
 * that can supply the embed content for the given url.
 */
public class EmbedServiceImpl implements EmbedService {
	// Regex to handle multiple youtube url patterns
	// https://stackoverflow.com/questions/3452546/how-do-i-get-the-youtube-video-id-from-a-url#comment11747164_8260383
	private static final Pattern YOUTUBE_ID_PATTERN = Pattern.compile(".*(?:youtu.be\\/|v\\/|u\\/\\w\\/|embed\\/|watch\\?v=)([^#\\&\\?]*).*");

	private static final Logger logger = LoggerFactory.getLogger(EmbedServiceImpl.class);

	private final List<EmbedProvider> providers;

	public EmbedServiceImpl(List<EmbedProvider> providers) {
		this.providers = providers;
	}

	@Override
	public EmbedContent getContent(String url, Map<String, Map<String, String>> options) {
		for (EmbedProvider provider : providers) {
			EmbedContent content = provider.getContent(url, options);
			if (content != null) {
				logger.debug("embed content provided by [{}]", provider.getClass().getSimpleName());
				return content;
			}
		}
		logger.error("failed to identify embed provider for url [{}]", url);
		return null;
	}

	private String getLastPathComponent(String url) {
		try {
			URI uri = new URI(url);
			String path = uri.getPath();
			if (path.endsWith("/")) {
				// Trim trailing slash
				path = path.substring(0, path.length() - 1);
			}
			return path.substring(path.lastIndexOf('/') + 1);
		} catch (Exception e) {
			logger.error("Error parsing url {}", url, e);
		}
		return null;
	}
}