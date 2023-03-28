package com.about.mantle.spring.interceptor;

import static com.about.mantle.web.filter.MantleExternalComponentServiceProxyHandler.EXTERNAL_COMPONENT_SERVICE_QUERY_PARAM;
import static com.about.mantle.spring.interceptor.CookieInterceptor.HASH_ID_QUERY_PARAM;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;

import com.about.globe.core.render.CoreRenderUtils;
import com.about.hippodrome.url.ExternalUrlData;
import com.about.hippodrome.url.PlatformUrlDataFactory;
import com.about.mantle.model.seo.QueryParamSafelist;
import com.google.common.collect.ImmutableSet;

public class SeoSafelistInterceptor extends AbstractMantleInterceptor {

	private static final Set<String> RESERVED_QUERYPARAMS = ImmutableSet.of("modelId", "itemIds", "state", "et", "cr",
			"loaded_cr", "loaded_css", "loaded_js", "cis", "force_cis", "pv", "facebookInstant", "logLevel", "prodDfp",
			"forceEndComments", "feedName", "taxDocId", "utm", EXTERNAL_COMPONENT_SERVICE_QUERY_PARAM, HASH_ID_QUERY_PARAM);

	private QueryParamSafelist safelist;
	private CoreRenderUtils renderUtils;
	private PlatformUrlDataFactory urlDataFactory;

	public SeoSafelistInterceptor(QueryParamSafelist safelist, CoreRenderUtils renderUtils,
			PlatformUrlDataFactory urlDataFactory) {
		this.safelist = safelist;
		this.renderUtils = renderUtils;
		this.urlDataFactory = urlDataFactory;
	}

	private String getQueryParamSafelistRedirect(ServletRequest request, ServletResponse response) {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		if (!httpRequest.getMethod().equalsIgnoreCase("GET")) {
			return null;
		}

		Map<String, String[]> queryParams = request.getParameterMap();
		if (queryParams.isEmpty()) {
			return null;
		}

		for (Map.Entry<String, String[]> queryParam : queryParams.entrySet()) {
			if (isSafelisted(queryParam.getKey())) {
				return null;
			}
		}

		return buildRedirectUrl((HttpServletRequest) request);
	}

	protected boolean isSafelisted(String queryParam) {
		if (RESERVED_QUERYPARAMS.contains(queryParam)) {
			return true;
		}
		if (safelist.getEqualsSet().contains(queryParam)) {
			return true;
		}

		for (String pattern : safelist.getStartsWithSet()) {
			if (queryParam.startsWith(pattern)) {
				return true;
			}
		}

		if (renderUtils.isPresevedQueryParam(queryParam)) {
			return true;
		}

		return false;
	}

	private String buildRedirectUrl(HttpServletRequest request) {
		ExternalUrlData.Builder urlDataBuilder;

		try {
			urlDataBuilder = ExternalUrlData.builder(urlDataFactory).from(request.getRequestURL().toString());
		} catch (MalformedURLException | UnsupportedEncodingException | URISyntaxException e) {
			throw new RuntimeException(e);
		}

		urlDataBuilder.scheme("https");

		String requestUrl = urlDataBuilder.build().getUrl();

		return urlDataFactory.create(requestUrl).with().build().getUrl();
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		boolean answer = true;
		String redirect = getQueryParamSafelistRedirect(request, response);
		if (redirect != null) {
			response.setHeader(HttpHeader.LOCATION.asString(), redirect);
			response.setStatus(HttpStatus.Code.MOVED_PERMANENTLY.getCode());
			answer = false;
		}
		return answer;
	}

	@Override
	final public String getIncludePathPatterns() {
		return "/**";
	}

}
