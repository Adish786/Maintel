package com.about.mantle.http;

import static org.apache.commons.lang3.StringUtils.isAlphanumeric;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trim;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jetty.http.HttpHeader;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.AccountInfo;
import com.about.globe.core.http.GeoData;
import com.about.globe.core.http.RequestContext.Builder;
import com.about.globe.core.http.RequestContextContributor;
import com.about.globe.core.http.RequestHeaders;
import com.about.globe.core.http.RequestScheme;
import com.about.globe.core.http.ua.DeviceCategory;
import com.about.globe.core.http.ua.UserAgent;
import com.about.globe.core.http.ua.UserAgentImpl;
import com.about.globe.core.http.ua.UserAgentParser;
import com.about.globe.core.util.StringUtil;
import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.about.hippodrome.models.request.HttpMethod;
import com.about.hippodrome.url.ExternalUrlData;
import com.about.hippodrome.url.PlatformUrlData;
import com.about.hippodrome.url.PlatformUrlDataFactory;
import com.about.hippodrome.url.UrlData;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.http.util.RequestSourceUtils;
import com.about.mantle.spring.interceptor.CookieInterceptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;

public class HttpRequestContextContributor implements RequestContextContributor {
	private static final Logger logger = LoggerFactory.getLogger(HttpRequestContextContributor.class);

	// `n` is a completely arbitrary choice for a prefix, no convention here
	private static final String IDSTAMP_PREFIX = "n";
	private static final String ORIG_URL = "ORIG_URL";

	private final UserAgentParser userAgentParser;
	private final PlatformUrlDataFactory urlDataFactory;
	private final Locale locale;

	private static final String MUID_FORMAT = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx";
	private static final Pattern MUID_PATTERN = Pattern.compile("x|y");

	private final ObjectMapper objectMapper;

	public HttpRequestContextContributor(UserAgentParser userAgentParser, PlatformUrlDataFactory urlDataFactory,
										 Locale locale) {

		this.userAgentParser = userAgentParser;
		this.urlDataFactory = urlDataFactory;
		this.locale = locale;
		this.objectMapper = new StandardObjectMapperProvider().getContext(null);
	}

	@Override
	public void contribute(HttpServletRequest request, Builder builder) {

		String serverName = request.getServerName();

		// we need to do this first in order to determine whether or not HTTPS
		// header was sent to rewrite urls
		RequestHeaders headers = extractHeaders(request);
		builder.setHeaders(headers);
		
		// Set the OriginalRequestApplication for proxied requests which return the X-Forwarded-Host and X-Forwarded-Proto headers
		if(headers.getXForwardedHost() != null && headers.getXForwardedProto() != null) {
			//Split the X_Forwareded_Host header for fastly which returns a csv of proxied hosts
			String[] hosts = split(headers.getXForwardedHost(), ',');
			if(hosts.length > 0) { 
				String host = trim(hosts[hosts.length-1]);
				String proxiedUrl = headers.getXForwardedProto() + "://" + host;
				try {
					PlatformUrlData proxiedRequestUrlData = urlDataFactory.create(proxiedUrl);
					if(proxiedRequestUrlData instanceof VerticalUrlData) {
						builder.setOriginalRequestApplication(((VerticalUrlData)proxiedRequestUrlData).getApplicationName());
					}
				}
				catch (Exception e) {
					logger.error("failed to parse proxied url {}", proxiedUrl, e);
				}
			}
		}
    

		builder.setRequestUri(request.getRequestURI());
		builder.setQueryString(request.getQueryString());
		String queryString = request.getQueryString() == null ? null : "?" + request.getQueryString();
		
		try {
			ExternalUrlData.Builder externalUrlDataBuilder = ExternalUrlData.builder(urlDataFactory)
					.from(request.getRequestURL().toString());
			if (headers.isRemoteSecure()) {
				externalUrlDataBuilder.scheme("https");
			}
			String requestUrl = externalUrlDataBuilder.build().getUrl();
			builder.setRequestUrl(requestUrl);
			
			PlatformUrlData requestUrlData = urlDataFactory.create(requestUrl);
			UrlData.Builder requestContextUrlDataBuilder =  requestUrlData.with().query(queryString);
			//If origin doc header is set, set docId in the urlData.
			//This must happen in all cases as only the origin application can be sure what the docId is in the case of legacy urls.
			//This will allow a user to override the docId in the requestContext but would only affect their own page
			//and should not pose a security risk. In practice this header should only be set during
			//external component requests and when testing a legacy document via an external service template
			if(headers.getXOriginDoc() != null && requestUrlData instanceof VerticalUrlData) {
				try {
					((VerticalUrlData.Builder)requestContextUrlDataBuilder).docId(Long.parseLong(headers.getXOriginDoc()));
				}
				catch (NumberFormatException e) {
					logger.error("failed to parse X-Origin-Document header " + headers.getXOriginDoc(), e);
				}
			}
			UrlData requestContextUrlData = requestContextUrlDataBuilder.build();
			builder.setUrlData(requestContextUrlData);
			builder.setCanonicalUrl(requestContextUrlData.getCanonicalUrl());
		} catch (UnsupportedEncodingException | MalformedURLException | URISyntaxException e) {
			logger.error("failed to parse requestUrl " + builder.getRequestUrl(), e);
		}

		builder.setLocale(locale);
		builder.setServerName(serverName);
		builder.setServerPort(request.getServerPort());
		builder.setHttpMethod(determineHttpMethod(request));
		builder.setRequestScheme(determineRequestScheme(request));
		builder.setUserAgent(determineUserAgent(request));

		// Cookies
		Map<String, Cookie> cookies = readCookies(request);
		builder.setCookies(cookies);

		builder.setRequestTimestamp(System.currentTimeMillis());

		// Ids
		String xOriginRequestId =  headers.getXOriginRequestId();
		String requestId = StringUtils.isNotBlank(xOriginRequestId)? xOriginRequestId: determineRequestId();

		builder.setRequestId(requestId);
		builder.setSessionId(determineSessionId(requestId, cookies));
		builder.setUserId(determineUserId(requestId, cookies));
		builder.setMuid(determineMuid(cookies));

		AccountInfo accountInfo = determineAccountInfo(cookies);
		builder.setAccountInfo(accountInfo);
		builder.setHashId(determineHashId(request, accountInfo, cookies));

		builder.setGeoData(new GeoData(headers.getCountry(), headers.getRegion(), headers.isEuropeanUnion()));

		Map<String, String[]> parameterMap = request.getParameterMap();
		builder.setParameters(parameterMap);

		boolean is404 = isNotEmpty((String) request.getAttribute(ORIG_URL));
		builder.set404(is404);
	}

	private RequestHeaders extractHeaders(HttpServletRequest request) {
		RequestHeaders.Builder builder = new RequestHeaders.Builder();

		builder.setReferer(request.getHeader(HttpHeader.REFERER.asString()));
		builder.setAccept(request.getHeader(HttpHeader.ACCEPT.asString()));
		builder.setAcceptLanguage(request.getHeader(HttpHeader.ACCEPT_LANGUAGE.asString()));
		builder.setUserAgent(request.getHeader(HttpHeader.USER_AGENT.asString()));
		builder.setDebugJsCheck(request.getHeader("X-Abt-Debug-JS-Errors") != null);
		builder.setAuthorization(request.getHeader(HttpHeader.AUTHORIZATION.asString()));
		builder.setCountry(request.getHeader("country_code_iso2"));
		// Obviously 'EU' is not a country code, but we get EU for EU countries when using this Fastly header.
		// see https://dotdash.slack.com/archives/C2MN20337/p1540323492000100
		builder.setIsEuropeanUnion("EU".equals(request.getHeader("country_code")));
		builder.setIsGoogleBot(RequestSourceUtils.isGoogleBot(request));
		builder.setRegion(processRegionHeader(request.getHeader("geo_region")));
		builder.setRemoteIp(RequestSourceUtils.getRemoteIp(request));
		builder.setRemoteSecure(RequestSourceUtils.isRemoteSecure(request));
		builder.setHost(request.getHeader(HttpHeader.HOST.asString()));
		builder.setXForwardedHost(request.getHeader(HttpHeader.X_FORWARDED_HOST.asString()));
		builder.setXForwardedProto(request.getHeader(HttpHeader.X_FORWARDED_PROTO.asString()));
		builder.setXFastlyDevice(request.getHeader("X-Fastly-Device"));
		// this will be mostly for external services
		builder.setXOriginDoc(request.getHeader("X-Origin-Document"));
	 	builder.setXOriginRequestId(request.getHeader("X-Origin-RequestId"));

		return builder.build();
	}

	/**
	 * Sometimes the format of region is COUNTRYCODE-REGIONCODE eg US-NY, FR-L, etc.  We're only interested in what's after
	 * the `-`
	 * @param geo_region
	 * @return
	 */
	private String processRegionHeader(String geo_region) {

		String answer;
		if (geo_region == null) {
			answer = "";
		} else if (geo_region.indexOf('-') == -1) {
			answer = geo_region;
		} else {
			answer = StringUtils.substringAfterLast(geo_region, "-");
		}

		return answer;

	}

	private Map<String, Cookie> readCookies(HttpServletRequest request) {
		if (request.getCookies() == null) return ImmutableMap.of();

		Map<String, Cookie> cookieMap = new HashMap<>();

		for (Cookie cookie : request.getCookies()) {
			if (!cookieMap.containsKey(cookie.getName())) {
				cookieMap.put(cookie.getName(), cookie);
			}
		}

		return Collections.unmodifiableMap(cookieMap);
	}

	private RequestScheme determineRequestScheme(HttpServletRequest request) {
		return RequestScheme.fromPrefix(request.getScheme());
	}

	private UserAgent determineUserAgent(HttpServletRequest request) {
		String userAgentString = request.getHeader(HttpHeaders.USER_AGENT);
		UserAgent userAgent = userAgentParser.parse(userAgentString);
		String deviceTypeHeader = request.getHeader("X-Fastly-Device");
		if (isNotBlank(deviceTypeHeader)) {
			return new UserAgentImpl(DeviceCategory.valueOf(deviceTypeHeader), userAgent.getFamilyName(),
					userAgent.getVersionMajor(), userAgent.getVersionMinor(), userAgent.getOsName(),
					userAgent.getOsVersion(), userAgent.getRaw());
		}
		return userAgent;
	}

	private HttpMethod determineHttpMethod(HttpServletRequest request) {
		return HttpMethod.valueOf(request.getMethod().toUpperCase());
	}

	private String determineUserId(String requestId, Map<String, Cookie> cookies) {

		Cookie userIdCookie = cookies.get(CookieInterceptor.USER_ID_COOKIE);
		if (userIdCookie == null || isBlank(userIdCookie.getValue())) return requestId;

		return userIdCookie.getValue();
	}

	private AccountInfo determineAccountInfo(Map<String, Cookie> cookies) {
		Cookie mdpAccountCookie = cookies.get(CookieInterceptor.MDP_ACCOUNT_ID_COOKIE);
		Cookie ddmAccountCookie = cookies.get(CookieInterceptor.DDM_ACCOUNT_ID_COOKIE);
		AccountInfo accountInfo = null;

		if(ddmAccountCookie != null && !isBlank(ddmAccountCookie.getValue())){

            try {
                String jsonString = new String(Base64.getDecoder().decode(ddmAccountCookie.getValue()));
                accountInfo = determineAccountInfoFromJsonString(jsonString, CookieInterceptor.DDM_ACCOUNT_ID_COOKIE);
            }catch(IllegalArgumentException e){
                logger.error("Failed to Base64 decode {} cookie", CookieInterceptor.DDM_ACCOUNT_ID_COOKIE, e);
            }

		}else if (mdpAccountCookie != null && !isBlank(mdpAccountCookie.getValue())) {
			accountInfo = determineAccountInfoFromJsonString(mdpAccountCookie.getValue(), CookieInterceptor.MDP_ACCOUNT_ID_COOKIE);
		}

		return accountInfo;
	}

	private AccountInfo determineAccountInfoFromJsonString(String jsonString, String cookieName){
		AccountInfo accountInfo = null;
		try {
			accountInfo = objectMapper.readValue(jsonString, AccountInfo.class);
		} catch (JsonProcessingException e) {
			logger.error("Failed to parse {} cookie", cookieName, e);
		}

		return accountInfo;
	}

	private String determineHashId(HttpServletRequest request, AccountInfo accountInfo, Map<String, Cookie> cookies) {
		//hid query param should always take precedence irrespective of presence of hashId in other places (hid cookie and accountInfo.hashId)
		String hidQueryParamValue = request.getParameter(CookieInterceptor.HASH_ID_QUERY_PARAM);
		if(StringUtils.isNotEmpty(hidQueryParamValue)) {
			return StringEscapeUtils.escapeHtml4(hidQueryParamValue);
		}

		Cookie hidCookie = cookies.get(CookieInterceptor.HASH_ID_COOKIE);

		//There is a possibility that CookieInterceptor.MDP_ACCOUNT_ID_COOKIE that populates accountInfo exists but hid doesn't. Fallback to hashId from accountInfo in that case.
		if(hidCookie == null || isBlank(hidCookie.getValue())) {
			return accountInfo != null ? accountInfo.getHashId() : null;
		}

		return hidCookie.getValue();
	}

	private String determineSessionId(String requestId, Map<String, Cookie> cookies) {

		Cookie sessionIdCookie = cookies.get(CookieInterceptor.SESSION_ID_COOKIE);
		if (sessionIdCookie == null || isBlank(sessionIdCookie.getValue()) || !isAlphanumeric(sessionIdCookie.getValue())) {
			return requestId;
		}

		return sessionIdCookie.getValue();
	}

	//total length of return value is 35 chars
	private String determineRequestId() {
		return IDSTAMP_PREFIX + UUID.randomUUID().toString().replace("-", "") + String.format("%02d", new LocalDateTime().getHourOfDay());
	}

	private String determineMuid(Map<String, Cookie> cookies) {

		Cookie muidCookie = cookies.get(CookieInterceptor.MUID_COOKIE);

		if (muidCookie == null || isBlank(muidCookie.getValue())) {
			return generateMuid();
		}

		return muidCookie.getValue();
	}

	/**
	 * Generation logic is lifted from legacy Meredith's muid-pixel
	 * @return muid as formatted in legacy Merdith to be compatible with downstream systems
	 */
	private String generateMuid() {
		long currentTime = new Date().getTime();
		Function <Matcher, String> replacerFunction = m -> {
			int r = (int)((currentTime + Math.random() * 16) % 16);
			return m.group().equals("x") ? Integer.toString(r, 16) : Integer.toString(r & 0x3 | 0x8, 16);
		};

		return StringUtil.replace(MUID_FORMAT, MUID_PATTERN, replacerFunction);
	}

}
