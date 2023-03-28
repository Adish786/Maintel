package com.about.mantle.web.filter;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.RequestContextSource;
import com.about.globe.core.exception.GlobeNotFoundException;
import com.about.globe.core.exception.GlobeTaskHaltException;
import com.about.hippodrome.url.CommonUrlData;
import com.about.hippodrome.url.ExternalUrlData;
import com.about.hippodrome.url.PlatformUrlDataFactory;
import com.about.hippodrome.url.UrlData;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.app.LegacyUrlMap;
import com.about.mantle.http.util.RequestSourceUtils;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.RedirectDocumentEx;
import com.about.mantle.model.services.DocumentService;
import com.about.mantle.model.tasks.TaxeneConfigurationTask;
import com.about.mantle.model.tasks.TemplateNameResolveTask;
import com.about.mantle.spring.interceptor.RedirectHandler;
import com.about.mantle.web.filter.redirect.RedirectRuleResolver;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class MantleRedirectHandler implements RedirectHandler {
	private static Logger logger = LoggerFactory.getLogger(MantleRedirectHandler.class);

	private static final Set<String> INTERNAL_PATHS = Sets.newHashSet("/serverStatus","/404");
	private static final Pattern MULTIPLE_SLASH_PATTERN = Pattern.compile("//+");

	private PlatformUrlDataFactory urlDataFactory;
	private DocumentService documentService;
	private RedirectRuleResolver redirectRuleResolver;
	private TemplateNameResolveTask templateNameResolveTask;
	private RequestContextSource requestContextSource;
	private TaxeneConfigurationTask taxeneConfigurationTask;
	private LegacyUrlMap legacyUrlMap;
	private final boolean seleneDocUrlsWithoutWww;

	@Deprecated
	public MantleRedirectHandler(
			PlatformUrlDataFactory urlDataFactory,
			DocumentService documentService,
			RedirectRuleResolver redirectRuleResolver,
			TemplateNameResolveTask templateNameResolveTask,
			RequestContextSource requestContextSource) {
		this(urlDataFactory, documentService, redirectRuleResolver, templateNameResolveTask, requestContextSource, null);
	}

	@Deprecated
	public MantleRedirectHandler(
			PlatformUrlDataFactory urlDataFactory,
			DocumentService documentService,
			RedirectRuleResolver redirectRuleResolver,
			TemplateNameResolveTask templateNameResolveTask,
			RequestContextSource requestContextSource,
			TaxeneConfigurationTask taxeneConfigurationTask) {
		this(urlDataFactory, documentService, redirectRuleResolver, templateNameResolveTask, requestContextSource, taxeneConfigurationTask, null);
	}

	@Deprecated
	public MantleRedirectHandler(
			PlatformUrlDataFactory urlDataFactory,
			DocumentService documentService,
			RedirectRuleResolver redirectRuleResolver,
			TemplateNameResolveTask templateNameResolveTask,
			RequestContextSource requestContextSource,
			TaxeneConfigurationTask taxeneConfigurationTask,
			LegacyUrlMap legacyUrlMap) {
		this(urlDataFactory, documentService, redirectRuleResolver, templateNameResolveTask, requestContextSource, taxeneConfigurationTask, legacyUrlMap, false);
	}

	@Deprecated
	public MantleRedirectHandler(
			PlatformUrlDataFactory urlDataFactory,
			DocumentService documentService,
			RedirectRuleResolver redirectRuleResolver,
			TemplateNameResolveTask templateNameResolveTask,
			RequestContextSource requestContextSource,
			TaxeneConfigurationTask taxeneConfigurationTask,
			LegacyUrlMap legacyUrlMap,
			boolean seleneDocUrlsWithoutWww) {
		this.urlDataFactory = urlDataFactory;
		this.documentService = documentService;
		this.redirectRuleResolver = redirectRuleResolver;
		this.templateNameResolveTask = templateNameResolveTask;
		this.requestContextSource = requestContextSource;
		this.taxeneConfigurationTask = taxeneConfigurationTask;
		this.legacyUrlMap = legacyUrlMap;
		this.seleneDocUrlsWithoutWww = seleneDocUrlsWithoutWww;
	}

	public MantleRedirectHandler(
			PlatformUrlDataFactory urlDataFactory,
			DocumentService documentService,
			RedirectRuleResolver redirectRuleResolver,
			TaxeneConfigurationTask taxeneConfigurationTask,
			LegacyUrlMap legacyUrlMap,
			boolean seleneDocUrlsWithoutWww) {
		this.urlDataFactory = urlDataFactory;
		this.documentService = documentService;
		this.redirectRuleResolver = redirectRuleResolver;
		this.taxeneConfigurationTask = taxeneConfigurationTask;
		this.legacyUrlMap = legacyUrlMap;
		this.seleneDocUrlsWithoutWww = seleneDocUrlsWithoutWww;
	}

	@Override
	public Redirect shouldRedirectForAllRequests(HttpServletRequest httpServletRequest) {
		Redirect answer = null;
		if (!INTERNAL_PATHS.contains(httpServletRequest.getRequestURI())) {
			if (answer == null && redirectRuleResolver != null) {
				answer = shouldRedirectBasedOnRules(httpServletRequest);
			}
			if (answer == null && documentService != null) {
				answer = shouldRedirectBasedOnDocument(httpServletRequest);
			}
		}
		return answer;
	}

	@Override
	public Redirect shouldRedirectForNonResourceRequests(HttpServletRequest httpServletRequest) {
		Redirect answer = null;
		if (!INTERNAL_PATHS.contains(httpServletRequest.getRequestURI())) {
			answer = shouldRedirectBasedOnCanonicalForm(httpServletRequest);
		}
		return answer;
	}

	// NOTE: these rules cannot be applied to resources because hashed resources may contain uppercase letters
	protected Redirect shouldRedirectBasedOnCanonicalForm(HttpServletRequest httpServletRequest) {
		String uri = httpServletRequest.getRequestURI();
		String domain = httpServletRequest.getServerName();

		boolean redirect = ensureSubdomain(domain) || ensureDomain(domain);

		//alter uri
		uri = collapseSlashes(uri);
		String tempUri = removeTrailingSlash(uri);

		//If the URI is the same it means one of two cases for legacy document
		//Option 1, it has a / and worked so it wasn't changed
		//Option 2, it doesn't have a /
		//We then check to see if it doesn't have a / if it needs it
		if(tempUri.equals(uri)) {
			tempUri = addMissingEndingSlashForLegacyDocument(uri);
		}

		uri = ensureUrl(tempUri);
		uri = additionalRedirectCheck(uri);

		return redirect || (!uri.equals(httpServletRequest.getRequestURI())) ? performRedirect (httpServletRequest,uri) : null ;
	}

	//A hook for an additional uri check for use by extending classes
	protected String additionalRedirectCheck(String uri){
		return uri;
	}

	protected Redirect performRedirect (HttpServletRequest httpServletRequest, String uri){

		ExternalUrlData.Builder urlDataBuilder;
		String requestUrl = httpServletRequest.getRequestURL().toString().toLowerCase();
		try {
			urlDataBuilder = ExternalUrlData.builder(urlDataFactory).from(requestUrl).subdomain(seleneDocUrlsWithoutWww ? null : "www").path(uri);
		} catch (MalformedURLException | UnsupportedEncodingException | URISyntaxException e) {
			logger.error("Failed to construct canonical form redirect URL from request {} and path {}", requestUrl, uri, e);
			throw new RuntimeException(e);
		}
		return buildRedirect(httpServletRequest, urlDataBuilder);
	}

	protected String collapseSlashes (String uri){

		// Collapse consecutive slashes
		if (uri.contains("//")) {
			uri = MULTIPLE_SLASH_PATTERN.matcher(uri).replaceAll("/");
		}

		return uri;
	}

	protected String removeTrailingSlash(String uri){

		// Do not remove trailing slash that is part of legacy url
		if (legacyUrlMap != null && uri.endsWith("/") && legacyUrlMap.getDocId(uri) != null) {
			return uri;
		}

		// Remove trailing slash
		if (!"/".equals(uri) && uri.endsWith("/")) {
			uri = uri.substring(0, uri.length() - 1);
		}

		return uri;
	}

	protected String addMissingEndingSlashForLegacyDocument(String uri){

		// Add trailing slash if this completes a legacy url, only if current url is not already a valid legacy url
		if (legacyUrlMap != null && !uri.endsWith("/") && legacyUrlMap.getDocId(uri+"/") != null && legacyUrlMap.getDocId(uri) == null) {
			uri = uri+"/";
		}

		return uri;
	}

	// Ensure www subdomain for verticals that contains www in selene document urls
	protected boolean ensureSubdomain(String domain) {
		boolean redirect = false;

		if(seleneDocUrlsWithoutWww) {
			redirect = domain.startsWith("www.") || domain.startsWith("www-");
		}else {
			redirect = !domain.startsWith("www.") && !domain.startsWith("www-");
		}

		return redirect;
	}

	protected boolean ensureDomain(String domain){
		boolean redirect = false;

		// Ensure domain is lowercase
		if (!domain.equals(domain.toLowerCase())) {
			redirect = true;
		}
		return redirect;
	}

	protected String ensureUrl(String uri){

		// Ensure url is lowercase
		String uriLowerCase = uri.toLowerCase();
		if (!uri.equals(uriLowerCase)) {
			uri = uriLowerCase;
		}

		return uri;
	}

	private Redirect shouldRedirectBasedOnRules(HttpServletRequest httpServletRequest) {
		String redirectUrl = redirectRuleResolver.resolve(httpServletRequest);
		if (redirectUrl != null) {
			UrlData urlData = urlDataFactory.create(httpServletRequest.getRequestURL().toString());
			UrlData redirectUrlData = getRedirectUrlData(urlData, redirectUrl);
			if(redirectUrlData != null) {
			    return new Redirect(redirectUrlData.toString(), HttpStatus.Code.MOVED_PERMANENTLY);
			}
		}

		return null;
	}

	private Redirect shouldRedirectBasedOnDocument(HttpServletRequest httpServletRequest) {
		Redirect answer = null;
		String requestUrl = httpServletRequest.getRequestURL().toString();
		UrlData urlData = urlDataFactory.create(requestUrl);

		if (RequestSourceUtils.isRemoteSecure(httpServletRequest)) {
			urlData = urlData.with().scheme("https").build();
		}

		if (urlData instanceof VerticalUrlData) {
			VerticalUrlData verticalUrlData = (VerticalUrlData) urlData;

			if (verticalUrlData.getDocId() != null) {
				String key = verticalUrlData.getDocId().toString();
				BaseDocumentEx doc = documentService.getDocument(key, httpServletRequest.getParameterMap());
				if (doc != null) {
					String redirectUrl = shouldRedirectDocument(verticalUrlData, doc);

					answer = buildRedirectFromRedirectUrl(httpServletRequest, redirectUrl);
				}
			}
		}
		return answer;
	}

	private String shouldRedirectDocument(VerticalUrlData requestUrlData, BaseDocumentEx document) {
		UrlData answer = null;
		String redirectUrl = null;

		if (document instanceof RedirectDocumentEx) {
			redirectUrl = ((RedirectDocumentEx) document).getTarget();
		} else if (!document.getUrl().equals(requestUrlData.getCanonicalUrl())) {
			// if selene document url is different then redirect to that url
			redirectUrl = document.getUrl();
		}
		answer = getRedirectUrlData(requestUrlData, redirectUrl);
		return answer!= null? answer.toString(): null;
	}

	private UrlData getRedirectUrlData(UrlData requestUrlData, String redirectUrl) {
		UrlData answer = null;
		if (redirectUrl != null) {
			UrlData redirectUrlData = urlDataFactory.create(redirectUrl);

			if (!(redirectUrlData instanceof VerticalUrlData)) {
				throw new GlobeNotFoundException("Document URL not from a dotdash domain.  URL host: " + redirectUrl);
			}
			if (!requestUrlData.isDefaultPort() || StringUtils.isNotEmpty(requestUrlData.getEnvironment())) {
				try {
					answer = redirectUrlData.with().environment(requestUrlData.getEnvironment()).port(requestUrlData.getPort()).build();
				} catch (URISyntaxException e) {
					throw new GlobeTaskHaltException("failed to build redirect url from url [" + redirectUrl + "] and request url " + requestUrlData.toString(), e);
				}
			} else {
				answer = redirectUrlData;
			}

		}
		return answer;
	}
	
	/**
	 * This method builds {@link RedirectHandler.Redirect} from String redirect url
	 * This handles errors if any and logs them.
	 * Internally uses helper method {@link #buildRedirect} to build actual {@link RedirectHandler.Redirect}
	 * @param httpServletRequest
	 * @param redirectUrl
	 * @return
	 */
	private Redirect buildRedirectFromRedirectUrl(HttpServletRequest httpServletRequest, String redirectUrl) {
		Redirect answer = null;
		if (redirectUrl != null) {
			try {
				answer = buildRedirect(httpServletRequest, ExternalUrlData.builder(urlDataFactory).from(redirectUrl));
			} catch (MalformedURLException | UnsupportedEncodingException | URISyntaxException e) {
				logger.error("Failed to construct redirect {}", redirectUrl, e);
				throw new RuntimeException(e);
			}
		}
		return answer;
	}
	
	/**
	 * Helper method for building Redirect used by {@link #buildRedirectFromRedirectUrl}
	 */
	private <B extends CommonUrlData.Builder<B>> Redirect buildRedirect(HttpServletRequest httpServletRequest, B urlDataBuilder) {
		if (!httpServletRequest.getParameterMap().isEmpty()) {
			ImmutableMap.Builder<String, List<String>> queryParams = new ImmutableMap.Builder<>();
			if (!httpServletRequest.getParameterMap().isEmpty()) {
				for (Map.Entry<String, String[]> entry : httpServletRequest.getParameterMap().entrySet()) {
					queryParams.put(entry.getKey(), Arrays.asList(entry.getValue()));
				}
			}
			urlDataBuilder.queryParams(queryParams.build());
		}
		if (RequestSourceUtils.isRemoteSecure(httpServletRequest)) {
			urlDataBuilder.scheme("https");
		}
		return new Redirect(urlDataBuilder.build().getUrl(), HttpStatus.Code.MOVED_PERMANENTLY);
	}

}
