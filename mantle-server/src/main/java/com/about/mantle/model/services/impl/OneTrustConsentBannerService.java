package com.about.mantle.model.services.impl;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.GeoData;
import com.about.mantle.model.services.ConsentBannerService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneTrustConsentBannerService implements ConsentBannerService {

	private static final Logger logger = LoggerFactory.getLogger(OneTrustConsentBannerService.class);

	private final boolean isSsr;
	private final String sdkVersion;
	private final String domainId;
	private final String lang;
	private final Set<String> templates;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final Client client = ClientBuilder.newClient();

	// ISO 3166-2:US state codes for 50 states + DC + territories (https://en.wikipedia.org/wiki/ISO_3166-2:US)
	private static final List<String> STATE_CODES = ImmutableList.of("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "DC", "AS", "GU", "MP", "PR", "UM", "VI");

	public OneTrustConsentBannerService(String domainId, String lang, Set<String> templates) {
		this(domainId, lang, templates, false, null);
	}

	public OneTrustConsentBannerService(String domainId, String lang, Set<String> templates, boolean isSsr, String sdkVersion) {
		this.domainId = domainId;
		if (domainId == null) {
			logger.error("No domain id specified. CMP banner disabled.");
		}
		this.lang = lang;
		this.templates = templates;
		this.isSsr = isSsr;
		this.sdkVersion = sdkVersion;
	}

	@Override
	public Map<String, Object> getDomainData(GeoData geoData) {
		final Template expectedTemplate = ConsentBannerService.getExpectedTemplate(geoData);
		if (domainId == null || expectedTemplate == null || templates == null || !templates.contains(expectedTemplate.name())) {
			return null;
		}

		// Get the state code from the geoData
		String stateCode = getStateCodeFromGeoData(geoData);

		logger.debug("Getting domain data for expected template {} from {}", expectedTemplate, geoData);
		Invocation.Builder requestBuilder = client
			.target("https://cookies-data.onetrust.io/bannersdk/domaindata")
			.request()
			.header("domain", domainId)
			.header("lang", lang)
			.header("location", "cdn.cookielaw.org")
			.header("OT-Country-Code", geoData.getIsoCode())
			// OT only supports region/state-level granularity inside the US
			.header("OT-Region-Code", stateCode);

		if (isSsr) {
			requestBuilder = requestBuilder.header("fetchHtmlAndCss", "true")
					.header("sdkVersion", sdkVersion);
		}

		Response response = requestBuilder.get();

		if (response.getStatus() != HttpStatus.OK_200) {
			response.close();
			throw new GlobeException("Failed to get OneTrust domain data for " + geoData.toString() +
			                         "; status code: " + response.getStatus() + "; body: " + response.readEntity(String.class));
		}

		try {
			final String responseBody = response.readEntity(String.class);
			Map<String, Object> answer = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
			if (isGenericPlaceholder(answer)) {
				logger.error("Expected {} template but got the generic placeholder template instead for {}", expectedTemplate, geoData);
				answer = null; // Disable the CMP!
			} else {
				List<String> errors = validatePayload(expectedTemplate, answer);
				if (!errors.isEmpty()) {
					for (String error : errors) {
						logger.error("OneTrust domain data payload error for {}: {}", geoData.toString(), error);
					}
					// throwing an exception forces a re-attempt as opposed to returning null which shuts off the CMP
					throw new GlobeException("Received invalid payload for " + geoData.toString() + "; body: " + responseBody);
				}
			}
			return answer;
		} catch (GlobeException e) {
			throw e;
		} catch (Exception e) {
			throw new GlobeException("Failed to process the domain data response", e);
		} finally {
			response.close();
		}
	}

	/**
	 * Based on the geoData, determine the state code to use for the OT-Region-Code header.
	 * If the isoCode is not in the US, return null.
	 * If the isoCode is in the US but does not match a valid state code, default to CA.
	 * Otherwise, return the subdivisionCode (e.g. "CA", "NY", etc.).
	 *
	 * @param geoData the geoData
	 * @return {String} the state code if the isoCode is in the US, null otherwise
	 */
	public static String getStateCodeFromGeoData(GeoData geoData) {
		String stateCode = "US".equals(geoData.getIsoCode()) ? geoData.getSubdivisionCode() : null;
		// if isoCode is in the US but does not match a valid state code, default to CA
		if (stateCode != null && !STATE_CODES.contains(stateCode)) {
			stateCode = "CA";
		}
		return stateCode;
	}

	private static final Map<Template, List<String>> ssrRequiredPathsMap = ImmutableMap.of(
			Template.GDPR, ImmutableList.of(
					//---onetrust-banner.ftl---
					"bannerLayout.html",
					//---onetrust-banner.evaluated.css---
					"bannerLayout.css"
			));

	private static final Map<Template, List<String>> requiredPathsMap = ImmutableMap.of(
		Template.GDPR, ImmutableList.of(
			//---onetrust-banner.ftl---
			"culture.DomainData.BannerTitle",
			"culture.DomainData.AlertNoticeText",
			"culture.DomainData.BannerIABPartnersLink",
			"culture.DomainData.AlertAllowCookiesText",
			//---onetrust-banner.evaluated.css---
			"culture.CommonData.BannerLinksTextColor",
			"culture.CommonData.TextColor",
			"culture.CommonData.ButtonColor",
			"culture.CommonData.ButtonTextColor",
			"culture.CommonData.BackgroundColor",
			"culture.CommonData.BannerCustomCSS"
		));

	/**
	 * Inspect payload for required data points.
	 * @param expectedTemplate
	 * @param payload
	 * @return list of detected errors
	 */
	private List<String> validatePayload(Template expectedTemplate, Map<String, Object> payload) {
		ArrayList<String> errors = new ArrayList<>();
		List<String> requiredPaths = (isSsr ? ssrRequiredPathsMap : requiredPathsMap).get(expectedTemplate);
		if (requiredPaths != null) {
			for (String requiredPath : requiredPaths) {
				if (isBlank((String) drill(payload, requiredPath))) {
					errors.add("template " + expectedTemplate + " is missing required path " + requiredPath);
				}
			}
		}
		return errors;
	}

	/**
	 * Needed to detect whether the template is the generic placeholder.
	 * We are only doing this because the name of the template is not included in the payload.
	 * OT may fix this in the future in which case we can check the name of the template
	 * explicitly as opposed to resorting to a convention of identifying the placeholder.
	 * The only reason we need the generic placeholder is because of constraints set by OT
	 * where the GDPR template must be displayed globally when it is the only template configured
	 * for the domain. Since we only want to display the GDPR template in the EU we have to
	 * configure a placeholder template as the global template.
	 */
	private static boolean isGenericPlaceholder(Map<String, Object> answer) {
		final String bannerTitle = (String) drill(answer, new ArrayList<>(Arrays.asList("culture", "DomainData", "BannerTitle")));
		/**
		 * TODO: check the template name as opposed to the title when it becomes available.
		 * https://dotdash.atlassian.net/browse/AXIS-306
		 */
		return "DO NOT EDIT THE GENERIC PLACEHOLDER BANNER OR ELSE!".equals(bannerTitle);
	}

	/**
	 * Helper method to get a piece of information from the payload.
	 * We are supressing warnings about type safety because we are working with raw objects here.
	 * We normally don't care about inspecting parts of the payload here and generally just pass it
	 * unaltered to the freemarker side for generating the banner component.
	 */
	@SuppressWarnings("unchecked")
	private static Object drill(Map<String, Object> payload, ArrayList<String> path) {
		if (payload == null || path == null || path.isEmpty()) {
			return null;
		}
		Object next = payload.get(path.remove(0));
		return path.isEmpty() ? next : drill((Map<String, Object>) next, path);
	}

	/**
	 * Convenience method for handling json paths, e.g. culture.DomainData.BannerTitle
	 * @param payload
	 * @param path
	 * @return
	 */
	private static Object drill(Map<String, Object> payload, String path) {
		return drill(payload, new ArrayList<>(Arrays.asList(split(path, '.'))));
	}

}
