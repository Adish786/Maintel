package com.about.mantle.model.services.embeds;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * [oEmbed](https://oembed.com) provider of embed content.
 */
public class OEmbedProvider implements EmbedProvider {

	private static final Logger logger = LoggerFactory.getLogger(OEmbedProvider.class);

	private final List<OEmbedEndpoint> endpoints;
	private final AntPathMatcher matcher = new AntPathMatcher();

	private static final Client client = ClientBuilder.newClient();

	public OEmbedProvider(List<OEmbedEndpoint> endpoints) {
		this.endpoints = endpoints;
	}

	@Override
	public EmbedContent getContent(String url, Map<String, Map<String, String>> options) {
		OEmbedEndpoint endpoint = getEndpoint(url);
		Map<String, String> relevantOptions = Collections.emptyMap();

		if (endpoint != null) {
			WebTarget webTarget = client.target(endpoint.getUrl())
					.queryParam("format", "json").queryParam("url", url);

			if (options != null) {
				relevantOptions = options.get(endpoint.getName());
				if (relevantOptions != null) {
					for (Entry<String, String> option : relevantOptions.entrySet()) {
						webTarget = webTarget.queryParam(option.getKey(), option.getValue());
					}
				}
			}

			Response response = webTarget.request().get();

			if (response.getStatus() != HttpStatus.OK_200) {
				logger.error("oEmbed endpoint [{}] for url [{}] responded with status code [{}]",
						endpoint, url, response.getStatus());
				response.close();
				return null;
			}

			OEmbedResponse oEmbedResponse = null;
			try {
				oEmbedResponse = response.readEntity(OEmbedResponse.class);
			} catch (Exception e) {
				logger.error("failed to read oEmbed response", e);
				return null;
			} finally {
				response.close();
			}

			EmbedContent embedContent = new EmbedContent();
			embedContent.setProvider(endpoint.getName());
			embedContent.setWidth(oEmbedResponse.getWidth());
			embedContent.setHeight(oEmbedResponse.getHeight());

			if (StringUtils.stripToNull(oEmbedResponse.getHtml()) != null) {
				String html = stripCaptionFromIGIfApplicable(endpoint, relevantOptions, oEmbedResponse.getHtml());
				// handles both types "rich" and "video" which return html
				embedContent.setContent(html);
				embedContent.setType(EmbedContent.Type.HTML);
			} else if (StringUtils.stripToNull(oEmbedResponse.getUrl()) != null && "photo".equals(oEmbedResponse.getType())) {
				embedContent.setContent(oEmbedResponse.getUrl());
				embedContent.setType(EmbedContent.Type.IMG);
			} else {
				// Known type "link" not required at this time.
				logger.error("failed to interpret oEmbed response of type [{}] for url [{}]" , oEmbedResponse.getType(), url);
				return null;
			}

			return embedContent;
		}

		logger.debug("no matching oEmbed endpoint for url [{}]", url);
		return null;
	}

	/**
	 * 
	 * This is a temp hack fix we put in because FB doesn't honor "hidecaption"
	 * attribute and returns HTML with "data-instgrm-captioned" attribute in it. It
	 * leads its embed.js to always render caption. We are stripping that attribute
	 * in this method if vertical wants to hide caption. We should get rid of this
	 * hack once FB fixes it. discussion thread -
	 * https://dotdash.slack.com/archives/DCJBVNY4B/p1605656044001200
	 */
	private String stripCaptionFromIGIfApplicable(OEmbedEndpoint oEmbedEndpoint, Map<String, String> relevantOptions, String html) {
		String hidecaption = relevantOptions != null ? relevantOptions.get("hidecaption") : "false";
		String answer = html;
		if ("instagram".equalsIgnoreCase(oEmbedEndpoint.getName()) && "true".equalsIgnoreCase(hidecaption)) {
			answer = html.replace("data-instgrm-captioned", "");
		}
		return answer;
	}
	
	private OEmbedEndpoint getEndpoint(String url) {
		for (OEmbedEndpoint endpoint : endpoints) {
			for (String pattern : endpoint.getPatterns()) {
				if (matcher.match(pattern, url)) {
					logger.debug("successfully matched oEmbed endpoint [{}] for url [{}]", endpoint, url);
					return endpoint;
				}
			}
		}
		return null;
	}

	/**
	 * Represents a single oEmbed provider.
	 * See https://oembed.com/providers.json
	 */
	public static class OEmbedEndpoint {

		private String name;
		private String url;
		private List<String> patterns;

		public OEmbedEndpoint(String name, String url, List<String> patterns) {
			this.name= name;
			this.url = url;
			this.patterns = patterns;
		}

		/**
		 * Name of this endpoint.
		 */
		public String getName() {
			return name;
		}

		/**
		 * URL of this endpoint.
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * URL patterns supported by this endpoint.
		 */
		public List<String> getPatterns() {
			return patterns;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	/**
	 * See https://oembed.com/#section2.3
	 */
	@JsonIgnoreProperties(ignoreUnknown=true)
	private static class OEmbedResponse {

		private String type;
		private String url;
		private String html;
		// Strings are used for width and height as TikTok provides "100%" as its oembed dimensions
		// Response can not be deserialized correctly when using Integer types here
		private String width;
		private String height;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getHtml() {
			return html;
		}

		public void setHtml(String html) {
			this.html = html;
		}

		public String getWidth() {
			return width;
		}

		public void setWidth(String width) {
			this.width = width;
		}

		public String getHeight() {
			return height;
		}

		public void setHeight(String height) {
			this.height = height;
		}

	}

}
