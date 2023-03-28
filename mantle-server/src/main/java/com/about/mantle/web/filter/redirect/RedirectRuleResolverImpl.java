package com.about.mantle.web.filter.redirect;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.about.globe.core.exception.GlobeException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.hippodrome.url.UrlData;
import com.about.hippodrome.url.UrlDataFactory;
import com.about.mantle.http.util.RequestSourceUtils;

public class RedirectRuleResolverImpl implements RedirectRuleResolver {
	private static Logger logger = LoggerFactory.getLogger(RedirectRuleResolverImpl.class);

	private final Map<String, List<Triple<Map<String, String>, Pattern, String>>> regExpUrls = new HashMap<>();
	private final Map<String, Pair<Map<String, String>, String>> absoluteUrls = new HashMap<>();
	private final UrlDataFactory urlDataFactory;

	public RedirectRuleResolverImpl(InputStream file, UrlDataFactory urlDataFactory) {

		this.urlDataFactory = urlDataFactory;

		try {
			initialize(file);
		} catch (IOException e) {
		    throw new GlobeException("Could not init redirect rules", e);
		}
	}

	private void initialize(InputStream file) throws IOException {
		if (file != null) {
			IOUtils.readLines(file).stream().forEach(line -> {
				String[] parts = StringUtils.split(line, ',');
				if (parts.length < 2)
					return;
				RedirectRuleType ruleType = RedirectRuleType.valueOf(parts[0]);
				processRedirects(ruleType, parts);
			});
		}
	}

	private Map<String, String> parseHeaders(String headers) {
		String[] headerList = StringUtils.split(headers, ';');
		if (headerList.length > 0) {
			Map<String, String> headerMapping = new HashMap<>();
			for (String header : headerList) {
				String[] headerKeyValue = StringUtils.split(header, ':');
				if (headerKeyValue.length == 2)
					headerMapping.put(headerKeyValue[0], headerKeyValue[1]);
			}
			return headerMapping;
		}
		return null;
	}

	@Override
	public String resolve(HttpServletRequest request) {
		if (regExpUrls.isEmpty() && absoluteUrls.isEmpty())
			return null;

		UrlData requestUrl;
		String hostName;
		String canonicalUrl;

		try {
			boolean isRemoteSecure = RequestSourceUtils.isRemoteSecure(request);
			

			@SuppressWarnings("rawtypes")
			UrlData.Builder requestUrlBuilder = urlDataFactory.create(request.getRequestURL().toString()).with()
					.query(request.getQueryString());
			if(isRemoteSecure) requestUrlBuilder.scheme("https");
			requestUrl = requestUrlBuilder.build();
			hostName = requestUrl.with().environment("").port(null).build().getHost();
			canonicalUrl = requestUrl.getCanonicalUrl();
		} catch (UnsupportedEncodingException | URISyntaxException e1) {
			logger.error("Failed to get canonical request URL.", e1);
			return null;
		}

		String resolvedUrl = resolveAbsoluteRedirects(request, requestUrl, canonicalUrl);
		return resolvedUrl != null ? resolvedUrl : resolveRegularExpressionRedirects(request, requestUrl, hostName);
	}

	private String resolveRegularExpressionRedirects(HttpServletRequest httpRequest, UrlData requestUrl,
			String hostName) {
		List<Triple<Map<String, String>, Pattern, String>> hostNameMappings = regExpUrls.get(hostName);

		if (hostNameMappings != null) {
			String urlPath = requestUrl.getPath();
			if (StringUtils.isNotEmpty(requestUrl.getQuery())) {
				urlPath = urlPath + "?" + requestUrl.getQuery();
			}
			for (Triple<Map<String, String>, Pattern, String> t : hostNameMappings) {
				if (!hasRequiredHeaders(httpRequest, t.getLeft()))
					continue;

				if (t.getMiddle().matcher(urlPath).matches() && StringUtils.isNotEmpty(t.getRight())) {
					return createEnviromentUrl(requestUrl, t.getRight());
				}
			}
		}
		return null;
	}

	private boolean hasRequiredHeaders(HttpServletRequest httpRequest, Map<String, String> headers) {
		if (MapUtils.isEmpty(headers))
			return true;

		for (Entry<String, String> e : headers.entrySet()) {
			if (!StringUtils.equals(httpRequest.getHeader(e.getKey()), e.getValue())) {
				return false;
			}
		}
		return true;
	}

	private String resolveAbsoluteRedirects(HttpServletRequest httpRequest, UrlData requestUrl, String url) {
		Pair<Map<String, String>, String> hostNameMapping = absoluteUrls.get(url);
		if (hostNameMapping == null) hostNameMapping = absoluteUrls.get(url + "?" + requestUrl.getQuery());
		
		if (hostNameMapping != null) {
			if (!hasRequiredHeaders(httpRequest, hostNameMapping.getLeft()))
				return null;
			return createEnviromentUrl(requestUrl, hostNameMapping.getRight());
		}
		return null;
	}

	private String createEnviromentUrl(UrlData requestUrl, String host) {
		if (!requestUrl.isDefaultPort() || StringUtils.isNotEmpty(requestUrl.getEnvironment())) {
			try {
				return urlDataFactory.create(host).with().environment(requestUrl.getEnvironment()).build().toString();
			} catch (URISyntaxException e) {
				logger.error("Failed to create environment URL for environment [{}] and port [{}] for target [{}]",
						requestUrl.getEnvironment(), requestUrl.getPort(), host, e);
			}
		}
		return host;
	}

	private void processRedirects(RedirectRuleType ruleType, String[] parts) throws IllegalArgumentException {
		switch (ruleType) {
		case REGEX:
			processRegularExpressionRedirects(parts);
			break;
		case ABSOLUTE:
			processAbsoluteRedirects(parts);
			break;
		}
	}

	private void processRegularExpressionRedirects(String[] parts) throws IllegalArgumentException {
		if (parts.length == 5) {
			Map<String, String> headerMappings = parseHeaders(parts[1]);
			String host = parts[2];
			String path = parts[3];

			// check if host already added to resultsMap
			if (!regExpUrls.containsKey(host)) {
				List<Triple<Map<String, String>, Pattern, String>> redirectList = new ArrayList<>();
				regExpUrls.put(host, redirectList);
			}

			Pattern pattern = Pattern.compile(path);
			regExpUrls.get(host).add(Triple.of(headerMappings, pattern, parts[4]));
		}
	}

	private void processAbsoluteRedirects(String[] parts) throws IllegalArgumentException {
		if (parts.length == 4) {
			Map<String, String> headerMappings = parseHeaders(parts[1]);
			String requestUrl = parts[2];
			String targetUrl = parts[3];
			// check if host already added to resultsMap
			if (!absoluteUrls.containsKey(requestUrl)) {
				absoluteUrls.put(requestUrl, Pair.of(headerMappings, targetUrl));
			}
		}
	}

}
