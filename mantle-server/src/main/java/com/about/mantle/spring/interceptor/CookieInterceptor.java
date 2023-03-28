package com.about.mantle.spring.interceptor;

import static com.about.mantle.model.services.ConsentBannerService.getExpectedTemplate;
import static com.about.mantle.model.services.ConsentBannerService.Template.GDPR;
import static org.eclipse.jetty.http.HttpCookie.SAME_SITE_NONE_COMMENT;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.AccountInfo;
import com.about.globe.core.http.RequestContext;
import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.about.mantle.spring.MantleFilterUtils;
import com.about.mantle.spring.MantleSpringConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CookieInterceptor extends AbstractMantleInterceptor {

	public static final String IDSTAMP = "IDSTAMP";
	/** User ID cookie */
	public static final String USER_ID_COOKIE = "TMog";
	public static final int USER_ID_MAXAGE = Integer.MAX_VALUE;
	/** Legacy Meredith User ID cookie */
	public static final String MUID_COOKIE = "globalTI_SID";
	public static final int MUID_MAXAGE = 60 * 60 * 24 * 365 * 2; // 2 years in seconds
	/** Legacy meredith cookies */
	public static final String MDP_ACCOUNT_ID_COOKIE = "mdpaccount";
	public static final String HASH_ID_COOKIE = "hid";
	public static final String HASH_ID_QUERY_PARAM = HASH_ID_COOKIE;
	/** DDM cookie to replace legacy meredith account cookie */
	public static final String DDM_ACCOUNT_ID_COOKIE = "ddmaccount";
	/** Common Cookie expiration */
	public static final int ONE_YEAR_IN_SECONDS = 60 * 60 * 24 * 365;
	/** Session ID cookie */
	public static final String SESSION_ID_COOKIE = "Mint";
	public static final int SESSION_ID_MAXAGE = 60 * 30; // 30 minutes in seconds (only in use if session cookies are disabled)
	/** Page view count cookie */
	public static final String PAGEVIEW_COUNT_COOKIE = "pc";
	public static final int PAGEVIEW_COUNT_MAXAGE = SESSION_ID_MAXAGE; // Keep in synch with session (only in use if session cookies are disabled)
	/** CSRF token cookie */
	public static final String CSRF_TOKEN_COOKIE = "CSRFToken";

	private static final Pattern NO_PAGEVIEW_PATTERN = Pattern.compile(
			"^/(googleCpcRefresh\\.html|browserconfig\\.xml|(comscore|manifest|(serveModel\\/model))\\.json|comscore-tracking|embed|sponsor-tracking-codes|g00.*|([^-]+-)?sw\\.js|1|"
					+ MantleSpringConfiguration.THUMBOR_PROXY_PREFIX.substring(1) + "/.*)",
			Pattern.CASE_INSENSITIVE);
	
	private static final String ONETRUST_CONSENT_COOKIE_NAME = "OptanonConsent";
	private static final Pattern ONETRUST_CONSENT_COOKIE_PATTERN = Pattern.compile(
			"^.*groups=.*C0002:(0|1).*");

	private final String domain;
	private final boolean sessionCookiesEnabled;
	private final ObjectMapper objectMapper;
	private static final Logger logger = LoggerFactory.getLogger(CookieInterceptor.class);

	public CookieInterceptor(String domain, boolean sessionCookiesEnabled) {
		this.domain = domain;
		this.sessionCookiesEnabled = sessionCookiesEnabled;
		this.objectMapper = new StandardObjectMapperProvider().getContext(null);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		handleUserId(request, response);
		handleLegacyMeredithUserId(request, response);
		handleSessionId(request, response);
		handleAuthRelatedCookies(request, response);
		handlePageCount(request, response);
		return true;
	}

	private void handleUserId(HttpServletRequest request, HttpServletResponse response) {
		RequestContext requestContext = RequestContext.get(request);
		// Add user id cookie if needed
		if (!requestContext.getCookies().containsKey(USER_ID_COOKIE)) {
			Cookie cookie = makePersistentCookie(USER_ID_COOKIE, requestContext.getUserId(), USER_ID_MAXAGE);
			response.addCookie(cookie);
		}
	}

	private void handleLegacyMeredithUserId(HttpServletRequest request, HttpServletResponse response) {
		RequestContext requestContext = RequestContext.get(request);
		// Add muId (legacy meredith user id) cookie if needed
		if (!requestContext.getCookies().containsKey(MUID_COOKIE)) {
			Cookie cookie = makePersistentCookie(MUID_COOKIE, requestContext.getMuid(), MUID_MAXAGE);
			response.addCookie(cookie);
		}
	}

	private void handleSessionId(HttpServletRequest request, HttpServletResponse response) {
		RequestContext requestContext = RequestContext.get(request);
		String sessionId = requestContext.getSessionId();
		Cookie sessionCookie = sessionCookiesEnabled ?
				makeSessionCookie(SESSION_ID_COOKIE, sessionId) : makePersistentCookie(SESSION_ID_COOKIE, sessionId, SESSION_ID_MAXAGE);

		/* Add additional attributes to the session cookie, just for
		  our /tools/ endpoints i.e. embeddable tools. This is required so that session cookie can be saved by
		  tool requesting site. Saved session cookie is used to verify CSRF token which is sent by the requesting site as part of POST request from the submit action on tool.
		  for more info, see this thread - https://dotdash.slack.com/archives/G667ZFFAT/p1611692442012900
		 */
		if (MantleFilterUtils.isRequestForEmbedTools(request)) {
			sessionCookie.setComment(SAME_SITE_NONE_COMMENT);
			sessionCookie.setSecure(true);
		}

		if (!sessionCookiesEnabled || !requestContext.getCookies().containsKey(SESSION_ID_COOKIE)) {
			response.addCookie(sessionCookie);

			// Since the session/csrf cookies are two sides of the same coin, we only ever want to
			// set the csrf token cookie if we're also setting the session id cookie. This will mitigate
			// weird edge cases during the transition period where there is already an existing session id
			// cookie with a set expiration, then we add a csrf token based on that pre-existing session id,
			// but then the duration-based session id eventually expires and we create a new one that is now
			// disassociated with the last csrf token.
			handleCsrfToken(request, response);
		}
	}

	private void handleCsrfToken(HttpServletRequest request, HttpServletResponse response) {
		RequestContext requestContext = RequestContext.get(request);
		if (sessionCookiesEnabled) {
			Cookie cookie = makeSessionCookie(CSRF_TOKEN_COOKIE, requestContext.getCsrfToken());
			cookie.setSecure(true);
			response.addCookie(cookie);
		}
	}

	private void handlePageCount(HttpServletRequest request, HttpServletResponse response) {
		RequestContext requestContext = RequestContext.get(request);
		Map<String, Cookie> cookies = requestContext.getCookies();
		if (isPageView(request, requestContext)) {
			String pageViewCount = "1";
			// Increment page view, resetting it for new sessions
			if (cookies.containsKey(PAGEVIEW_COUNT_COOKIE) && cookies.containsKey(SESSION_ID_COOKIE)) {
				pageViewCount = Integer.toString(NumberUtils.toInt(cookies.get(PAGEVIEW_COUNT_COOKIE).getValue()) + 1);
			}

			//If request is under GDPR add PAGEVIEW_COUNT_COOKIE only if consent is given for Performance Cookies
			boolean isRequestUnderGdpr = GDPR.equals(getExpectedTemplate(requestContext.getGeoData()));
			if (!isRequestUnderGdpr || hasOneTrustConsentForPeformanceCookies(requestContext)) {
				if (isRequestUnderGdpr) {
					boolean isPageLoad = pageViewCount.equals("1");
					// Since first page load is used to get consent & does not set
					// PAGEVIEW_COUNT_COOKIE, set pageViewCount to 2 once consent is provided  
					if (isPageLoad) {
						pageViewCount = "2";
					}
				}
				response.addCookie(sessionCookiesEnabled ?
						makeSessionCookie(PAGEVIEW_COUNT_COOKIE, pageViewCount) :
						makePersistentCookie(PAGEVIEW_COUNT_COOKIE, pageViewCount, PAGEVIEW_COUNT_MAXAGE));
			}
		}
	}

	/**
	 * For handling all auth related cookies
	 */
	private void handleAuthRelatedCookies(HttpServletRequest request, HttpServletResponse response) {
		RequestContext requestContext = RequestContext.get(request);
		addHashIdCookie(requestContext, response);

		Map<String, Cookie> cookies = requestContext.getCookies();
		if (cookies.containsKey(DDM_ACCOUNT_ID_COOKIE)) {
			testDDMAccountCookie(response, cookies.get(DDM_ACCOUNT_ID_COOKIE));
		} else {
			convertMDPAccountCookieToDDMAccountCookie(response, cookies);
		}
	}

	/**
	 * Creates the hid cookie containing the hashId commonly used with legacy meredith services
	 */
	private void addHashIdCookie(RequestContext requestContext, HttpServletResponse response){
		// Add HASH_ID_COOKIE
		String hidQueryParamValue = requestContext.getParameterSingle(HASH_ID_QUERY_PARAM);
		if(StringUtils.isNotEmpty(hidQueryParamValue)) {
			Cookie hidCookie = makePersistentCookie(HASH_ID_COOKIE, StringEscapeUtils.escapeHtml4(hidQueryParamValue), ONE_YEAR_IN_SECONDS);
			response.addCookie(hidCookie);
		}
	}

	/**
	 * Need to convert mdpaccount cookies to ddmaccount cookies because Globe is RFC6265 compliant while the
	 * mdpaccount cookies are not.
	 *
	 * See https://dotdash.atlassian.net/browse/GLBE-9547
	 */
	private void convertMDPAccountCookieToDDMAccountCookie(HttpServletResponse response, Map<String, Cookie> cookies){
		// Need to convert existing MDP Account cookies into DDM Account Cookies
		if (cookies.containsKey(MDP_ACCOUNT_ID_COOKIE) && !cookies.containsKey(DDM_ACCOUNT_ID_COOKIE)){

			try {
				AccountInfo accountInfo = null;
				accountInfo = objectMapper.readValue(cookies.get(MDP_ACCOUNT_ID_COOKIE).getValue(), AccountInfo.class);

				Cookie ddm = makePersistentCookie(DDM_ACCOUNT_ID_COOKIE,
						Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(accountInfo).getBytes()),
						ONE_YEAR_IN_SECONDS);

				ddm.setSecure(true);
				response.addCookie(ddm);
			} catch (JsonProcessingException e) {
				logger.error("Failed to parse {} cookie", MDP_ACCOUNT_ID_COOKIE, e);
			}
		}
	}

	private void testDDMAccountCookie(HttpServletResponse response, Cookie ddmAccountCookie){
		try {
			String jsonString = new String(Base64.getDecoder().decode(ddmAccountCookie.getValue()));
			// check for proper format
			objectMapper.readValue(jsonString, AccountInfo.class);
		}catch(Exception e) {
			//On any exception we should delete the cookie
			Cookie ddm = makeDeletionCookie(DDM_ACCOUNT_ID_COOKIE);
			response.addCookie(ddm);
		}
	}
	
	private boolean hasOneTrustConsentForPeformanceCookies(RequestContext requestContext) {
		boolean hasConsentForPeformanceCookies = false;
		Cookie oneTrustConsentCookie = requestContext.getCookie(ONETRUST_CONSENT_COOKIE_NAME);
		if(oneTrustConsentCookie != null) {
			String oneTrustConsentCookieValue = URLDecoder.decode(oneTrustConsentCookie.getValue(), StandardCharsets.UTF_8);
			Matcher oneTrustConsentCookieMatcher = ONETRUST_CONSENT_COOKIE_PATTERN.matcher(oneTrustConsentCookieValue);
			
			if(oneTrustConsentCookieMatcher.matches()) {
				hasConsentForPeformanceCookies = oneTrustConsentCookieMatcher.group(1).equals("1");
			}
		}
		return hasConsentForPeformanceCookies;
	}

	/**
	 * Constructs and initializes a Cookie with a set of default values.
	 * 
	 * @param name          the name of the Cookie.
	 * @param value         cookie data.
	 * @param maxAgeSeconds maximum age, in seconds, of the Cookie, after which it
	 *                      will expire.
	 * @return Newly-created Cookie, with domain given in the constructor and with
	 *         path <code>/</code>.
	 */
	private Cookie makePersistentCookie(String name, String value, int maxAgeSeconds) {
		Cookie cookie = makeSessionCookie(name, value);
		cookie.setMaxAge(maxAgeSeconds);
		return cookie;
	}

	private Cookie makeDeletionCookie(String name) {
		Cookie cookie = makeSessionCookie(name, "");
		cookie.setMaxAge(0);
		return cookie;
	}

	private Cookie makeSessionCookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setDomain(this.domain);
		cookie.setPath("/");
		return cookie;
	}

	protected boolean isPageView(HttpServletRequest request, RequestContext requestContext) {

		// Don't increment for ajax requests
		if (requestContext.isDeferred())
			return false;

		// Some uri should not update page views
		if (NO_PAGEVIEW_PATTERN.matcher(request.getRequestURI()).matches())
			return false;

		return true;
	}

	@Override
	final public String getIncludePathPatterns() {
		return "/**";
	}

}
