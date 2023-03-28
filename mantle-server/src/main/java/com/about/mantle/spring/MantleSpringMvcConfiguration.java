package com.about.mantle.spring;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;

import com.about.globe.core.app.GlobeExternalConfigKeys;
import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContextSource;
import com.about.globe.core.render.CoreRenderUtils;
import com.about.globe.core.web.filter.AccessLogInjector;
import com.about.globe.core.web.filter.MdcPopulator;
import com.about.hippodrome.config.CommonPropertyFactory;
import com.about.hippodrome.config.HippodromePropertyFactory;
import com.about.hippodrome.url.PlatformUrlDataFactory;
import com.about.mantle.app.LegacyUrlMap;
import com.about.mantle.app.MantleExternalConfigKeys;
import com.about.mantle.cache.clearance.CacheClearanceCandidateRepo;
import com.about.mantle.endpoint.controllers.AbstractMantleEndpointController;
import com.about.mantle.endpoint.controllers.AdsTxtController;
import com.about.mantle.endpoint.controllers.AmazonRssController;
import com.about.mantle.endpoint.controllers.AppConfigsController;
import com.about.mantle.endpoint.controllers.AppTasksController;
import com.about.mantle.endpoint.controllers.AppleFaviconHandlerController;
import com.about.mantle.endpoint.controllers.ApplicationManifestController;
import com.about.mantle.endpoint.controllers.Auth0LoginController;
import com.about.mantle.endpoint.controllers.Auth0LogoutController;
import com.about.mantle.endpoint.controllers.BookmarksController;
import com.about.mantle.endpoint.controllers.ContentGraphController;
import com.about.mantle.endpoint.controllers.CsrfSessionController;
import com.about.mantle.endpoint.controllers.DebugBlockingOnSafelistController;
import com.about.mantle.endpoint.controllers.DebugCacheClearanceController;
import com.about.mantle.endpoint.controllers.DebugEffectiveTemplateController;
import com.about.mantle.endpoint.controllers.DebugLegacyUrlMapRefreshController;
import com.about.mantle.endpoint.controllers.DebugModelTimingsController;
import com.about.mantle.endpoint.controllers.DebugModelsController;
import com.about.mantle.endpoint.controllers.DebugResolvedEffectiveTemplateController;
import com.about.mantle.endpoint.controllers.DoSomethingMagicalController;
import com.about.mantle.endpoint.controllers.DocumentController;
import com.about.mantle.endpoint.controllers.EmbedController;
import com.about.mantle.endpoint.controllers.FacebookShareRedirectController;
import com.about.mantle.endpoint.controllers.FaviconHandlerController;
import com.about.mantle.endpoint.controllers.FlamegraphController;
import com.about.mantle.endpoint.controllers.FlamegraphSvgController;
import com.about.mantle.endpoint.controllers.GoogleNewsSitemapController;
import com.about.mantle.endpoint.controllers.HomePageController;
import com.about.mantle.endpoint.controllers.MantleHandlerExceptionResolver;
import com.about.mantle.endpoint.controllers.NewsletterSubmitController;
import com.about.mantle.endpoint.controllers.PageGoneController;
import com.about.mantle.endpoint.controllers.PageNotFoundController;
import com.about.mantle.endpoint.controllers.PinterestTaxonomyController;
import com.about.mantle.endpoint.controllers.PrivacyRequestController;
import com.about.mantle.endpoint.controllers.PrivacyRequestSubmissionController;
import com.about.mantle.endpoint.controllers.PushlyServiceWorkerController;
import com.about.mantle.endpoint.controllers.RenderEditPlComponentController;
import com.about.mantle.endpoint.controllers.RenderPlComponentController;
import com.about.mantle.endpoint.controllers.RenderPlComponentJsonController;
import com.about.mantle.endpoint.controllers.RenderPlController;
import com.about.mantle.endpoint.controllers.ResoundReactController;
import com.about.mantle.endpoint.controllers.ResoundReviewStatusController;
import com.about.mantle.endpoint.controllers.ResoundSubmitReviewController;
import com.about.mantle.endpoint.controllers.ResourceHandlerController;
import com.about.mantle.endpoint.controllers.RobotsTxtController;
import com.about.mantle.endpoint.controllers.RssFeedController;
import com.about.mantle.endpoint.controllers.RssFeedUsingAddedDatesController;
import com.about.mantle.endpoint.controllers.SearchPageController;
import com.about.mantle.endpoint.controllers.ServeModelController;
import com.about.mantle.endpoint.controllers.ServerStatusController;
import com.about.mantle.endpoint.controllers.ServiceWorkerController;
import com.about.mantle.endpoint.controllers.SitemapIndexController;
import com.about.mantle.endpoint.controllers.SitemapPageController;
import com.about.mantle.endpoint.controllers.SmartNewsRssFeedController;
import com.about.mantle.endpoint.controllers.SponsoredTrackingCodesController;
import com.about.mantle.endpoint.controllers.TestLoggerController;
import com.about.mantle.endpoint.controllers.UgcFeedbackController;
import com.about.mantle.endpoint.controllers.UgcRatingController;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;
import com.about.mantle.handlers.methods.MantleResourceHandlerMethods;
import com.about.mantle.logging.SafeListParamaterFailedLogger;
import com.about.mantle.model.seo.QueryParamSafelist;
import com.about.mantle.model.services.Mantle410Service;
import com.about.mantle.model.services.bookmarks.BookmarksService;
import com.about.mantle.model.services.contentgraph.ContentGraphService;
import com.about.mantle.spring.interceptor.AbstractMantleInterceptor;
import com.about.mantle.spring.interceptor.CSRFInterceptor;
import com.about.mantle.spring.interceptor.CacheClearanceInterceptor;
import com.about.mantle.spring.interceptor.CookieInterceptor;
import com.about.mantle.spring.interceptor.GlobalRedirectInterceptor;
import com.about.mantle.spring.interceptor.MdcPopulatorInterceptor;
import com.about.mantle.spring.interceptor.PageGoneHandler;
import com.about.mantle.spring.interceptor.PageNotFoundHandler;
import com.about.mantle.spring.interceptor.RedirectHandler;
import com.about.mantle.spring.interceptor.RequestContextContributorInterceptor;
import com.about.mantle.spring.interceptor.RequestHandlerRedirectInterceptor;
import com.about.mantle.spring.interceptor.SeoSafelistInterceptor;
import com.about.mantle.spring.interceptor.SeoSafelistLoggingInterceptor;
import com.about.mantle.web.filter.Mantle410Handler;
import com.google.common.collect.Sets;

@Configuration
public class MantleSpringMvcConfiguration extends WebMvcConfigurationSupport {

	private final int MANTLE_INTERCEPTORS_ORDER_OFFSET = 0;
	protected RequestContextSource requestContextSource;
	protected AccessLogInjector accessLogInjector;
	protected CacheClearanceCandidateRepo cacheClearanceCandidateRepo;
	protected PageNotFoundHandler pageNotFoundHandler;
	protected RedirectHandler redirectHandler;
	
	protected QueryParamSafelist safelist;
	
	protected CoreRenderUtils renderUtils;
	protected PlatformUrlDataFactory urlDataFactory;
	protected MantleRequestHandlerMethods requestHandlerMethods;
	protected MantleResourceHandlerMethods resourceHandlerMethods;
	protected MdcPopulator mdcPopulator;

	@Autowired(required = false)
	private BookmarksService bookmarksService;

	@Autowired(required = false)
	private ContentGraphService contentGraphService;

	@Autowired
	private MantleCorsConfigs mantleCorsConfigs;

	@Autowired
	private Mantle410Service mantle410Service;

	@Autowired
	private SafeListParamaterFailedLogger safeListParamaterFailedLogger;

	@Autowired
	private LegacyUrlMap legacyUrlMap;

	protected final HippodromePropertyFactory propertyFactory = CommonPropertyFactory.INSTANCE.get();

	@Autowired
	public MantleSpringMvcConfiguration(RequestContextSource requestContextSource, AccessLogInjector accessLogInjector,
			CacheClearanceCandidateRepo cacheClearanceCandidateRepo, PageNotFoundHandler pageNotFoundHandler,
			RedirectHandler redirectHandler, QueryParamSafelist safelist, CoreRenderUtils renderUtils,
			PlatformUrlDataFactory urlDataFactory, MdcPopulator mdcPopulator,
			MantleRequestHandlerMethods handlerMethods, MantleResourceHandlerMethods resourceHandlerMethods) {
		this.requestContextSource = requestContextSource;
		this.accessLogInjector = accessLogInjector;
		this.cacheClearanceCandidateRepo = cacheClearanceCandidateRepo;
		this.pageNotFoundHandler = pageNotFoundHandler;
		this.redirectHandler = redirectHandler;
		this.safelist = safelist;
		this.renderUtils = renderUtils;
		this.urlDataFactory = urlDataFactory;
		this.requestHandlerMethods = handlerMethods;
		this.resourceHandlerMethods = resourceHandlerMethods;
		this.mdcPopulator = mdcPopulator;
	}

	//TODO refactor in 3.13 to fit inline with how pageNotFoundHandler is passed in
	@Bean
	public PageGoneHandler pageGoneHandler(){
		return new Mantle410Handler(mantle410Service);
	}

	@Bean
	@Override
	public HandlerExceptionResolver handlerExceptionResolver(@Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager) {
		return new MantleHandlerExceptionResolver(pageNotFoundHandler, pageGoneHandler());
	}

	@Bean
	public CacheClearanceInterceptor cacheClearanceInterceptor() {
		return new CacheClearanceInterceptor(cacheClearanceCandidateRepo);
	}

	@Bean
	public GlobalRedirectInterceptor globalRedirectInterceptor() {
		return new GlobalRedirectInterceptor(redirectHandler, pageNotFoundHandler);
	}

	@Bean
	public RequestHandlerRedirectInterceptor requestHandlerRedirectInterceptor() {
		return new RequestHandlerRedirectInterceptor(redirectHandler);
	}

	@Bean
	public RequestContextContributorInterceptor requestContextContributorInterceptor() {
		return new RequestContextContributorInterceptor(requestContextSource, accessLogInjector);
	}

	@Bean
	public CSRFInterceptor CSRFInterceptor() {
		return new CSRFInterceptor();
	}

	@Bean
	public MdcPopulatorInterceptor mdcPopulatorInterceptor() {
		return new MdcPopulatorInterceptor(mdcPopulator);
	}

	@Bean
	public SeoSafelistInterceptor seoSafelistInterceptor() {
		return new SeoSafelistInterceptor(safelist, renderUtils, urlDataFactory);
	}

	@Bean
	public SeoSafelistLoggingInterceptor seoSafelistLoggingInterceptor() {
		return new SeoSafelistLoggingInterceptor(safelist, renderUtils, urlDataFactory, safeListParamaterFailedLogger, seoSafelistInterceptor());
	}

	@Bean
	public CookieInterceptor cookieInterceptor() {
		String domain = "." + renderUtils.getDomain("/");
		boolean sessionCookiesEnabled = propertyFactory.getProperty(MantleExternalConfigKeys.SESSION_COOKIES_ENABLED).asBoolean(false).get();
		return new CookieInterceptor(domain, sessionCookiesEnabled);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// cache clearance is applied to both static and non-static requests
		registry.addInterceptor(cacheClearanceInterceptor())
				.addPathPatterns(getIncludedPathPatterns(cacheClearanceInterceptor()))
				.excludePathPatterns(getExcludedPathPatterns(cacheClearanceInterceptor()))
				.order(MANTLE_INTERCEPTORS_ORDER_OFFSET + 1);

		registry.addInterceptor(seoSafelistLoggingInterceptor())
				.addPathPatterns(getIncludedPathPatterns(seoSafelistLoggingInterceptor()))
				.excludePathPatterns(getExcludedPathPatterns(seoSafelistLoggingInterceptor()))
				.order(MANTLE_INTERCEPTORS_ORDER_OFFSET + 2);

		registry.addInterceptor(globalRedirectInterceptor())
				.addPathPatterns(getIncludedPathPatterns(globalRedirectInterceptor()))
				.excludePathPatterns(getExcludedPathPatterns(globalRedirectInterceptor()))
				.order(MANTLE_INTERCEPTORS_ORDER_OFFSET + 3);

		registry.addInterceptor(requestHandlerRedirectInterceptor())
				.addPathPatterns(getIncludedPathPatterns(requestHandlerRedirectInterceptor()))
				.excludePathPatterns(getExcludedPathPatterns(requestHandlerRedirectInterceptor()))
				.order(MANTLE_INTERCEPTORS_ORDER_OFFSET + 4);

		// requestContextContributorInterceptor is applied to both static and non-static
		// requests
		// TODO we _really_ don't want to put RequestContextContrb for static resources.
		// Needed because we need to
		// log static resources to the access log, but we should be able to do that
		// w/out the Req Ctx
		registry.addInterceptor(requestContextContributorInterceptor())
				.addPathPatterns(getIncludedPathPatterns(requestContextContributorInterceptor()))
				.excludePathPatterns(getExcludedPathPatterns(requestContextContributorInterceptor()))
				.order(MANTLE_INTERCEPTORS_ORDER_OFFSET + 5);

		// mdc populator is applied to both static and non-static requests.
		registry.addInterceptor(mdcPopulatorInterceptor())
				.addPathPatterns(getIncludedPathPatterns(mdcPopulatorInterceptor()))
				.excludePathPatterns(getExcludedPathPatterns(mdcPopulatorInterceptor()))
				.order(MANTLE_INTERCEPTORS_ORDER_OFFSET + 6);

		registry.addInterceptor(CSRFInterceptor()).addPathPatterns(getIncludedPathPatterns(CSRFInterceptor()))
				.excludePathPatterns(getExcludedPathPatterns(CSRFInterceptor()))
				.order(MANTLE_INTERCEPTORS_ORDER_OFFSET + 7);

		registry.addInterceptor(seoSafelistInterceptor())
			    .addPathPatterns(getIncludedPathPatterns(seoSafelistInterceptor()))
			    .excludePathPatterns(getExcludedPathPatterns(seoSafelistInterceptor()))
			    .order(MANTLE_INTERCEPTORS_ORDER_OFFSET + 8);

		registry.addInterceptor(cookieInterceptor()).addPathPatterns(getIncludedPathPatterns(cookieInterceptor()))
				.excludePathPatterns(getExcludedPathPatterns(cookieInterceptor()))
				.order(MANTLE_INTERCEPTORS_ORDER_OFFSET + 9);

	}

	@Bean
	public Controller adsTxtController() {
		String adsTxtBovdPath = propertyFactory.getProperty(MantleExternalConfigKeys.ADS_TXT_BOVD_PATH).asString("mantle/ads.txt").get();
		return new AdsTxtController(requestHandlerMethods, adsTxtBovdPath);
	}

	@Bean
	public Controller sponsoredTrackingCodesController() {
		return new SponsoredTrackingCodesController(requestHandlerMethods);
	}

	@Bean
	public Controller embedController() {
		String primaryDomain = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN).asString(null).get();

		return new EmbedController(requestHandlerMethods, primaryDomain);
	}

	@Bean
	public Controller amazonRssController() {
		return new AmazonRssController(requestHandlerMethods);
	}

	@Bean
	public Controller smartNewsRssController() {
		return new SmartNewsRssFeedController(requestHandlerMethods);
	}

	@Bean
	public Controller applicationManifestController() {
		return new ApplicationManifestController(requestHandlerMethods);
	}

	@Bean
	public Controller debugCacheClearanceController() {
		return new DebugCacheClearanceController(requestHandlerMethods, cacheClearanceCandidateRepo);
	}

	@Bean
	public Controller debugEffectiveTemplateController() {
		return new DebugEffectiveTemplateController(requestHandlerMethods);
	}

	@Bean
	public Controller debugLegacyUrlMapRefreshController() {
		return new DebugLegacyUrlMapRefreshController(requestHandlerMethods);
	}

	@Bean
	public Controller debugResolvedEffectiveTemplateController() {
		return new DebugResolvedEffectiveTemplateController(requestHandlerMethods);
	}

	@Bean
	public Controller debugModelsController() {
		return new DebugModelsController(requestHandlerMethods);
	}

	@Bean
	public Controller debugModelTimingsController() {
		return new DebugModelTimingsController(requestHandlerMethods);
	}

	@Bean
	public Controller debugBlockingOnSafelistController() {
		return new DebugBlockingOnSafelistController(requestHandlerMethods);
	}

	@Bean
	public Controller doSomethingMagicalController() {
		return new DoSomethingMagicalController(requestHandlerMethods);
	}

	@Bean
	public Controller documentController() {
		return new DocumentController(requestHandlerMethods);
	}

	private String getPrimaryDomain() {
		String primaryDomain = propertyFactory.getProperty(GlobeExternalConfigKeys.DOMAIN).asString(null).get();
		if(StringUtils.isBlank(primaryDomain)) {
			throw new GlobeException("GlobeExternalConfigKeys.DOMAIN value not set, please set so that CORS works as expected.");
		}
		return primaryDomain;
	}
	
	@Bean
	public Controller facebookShareRedirectController() {
		return new FacebookShareRedirectController(requestHandlerMethods);
	}

	@Bean
	public Controller flamegraphController() {
		return new FlamegraphController(requestHandlerMethods);
	}

	@Bean
	public Controller flamegraphSvgController() {
		return new FlamegraphSvgController(requestHandlerMethods);
	}

	@Bean
	public Controller googleNewsSitemapController() {
		return new GoogleNewsSitemapController(requestHandlerMethods);
	}

	@Bean
	public Controller homePageController() {
		return new HomePageController(requestHandlerMethods);
	}

	@Bean
	public Controller newsletterSubmitController() {
		return new NewsletterSubmitController(requestHandlerMethods);
	}

	@Bean
	public Controller pageNotFoundController() {
		return new PageNotFoundController(requestHandlerMethods);
	}

	@Bean
	public Controller pageGoneController() {
		return new PageGoneController(requestHandlerMethods);
	}

	@Bean
	public Controller pinterestTaxonomyController() {
		return new PinterestTaxonomyController(requestHandlerMethods);
	}


	@Bean
	public Controller privacyRequestController() {
		return new PrivacyRequestController(requestHandlerMethods);
	}

	@Bean
	public Controller privacyRequestSubmissionController() {
		return new PrivacyRequestSubmissionController(requestHandlerMethods);
	}

	@Bean
	public Controller renderEditPlComponentController() {
		return new RenderEditPlComponentController(requestHandlerMethods);
	}

	@Bean
	public Controller renderPlComponentController() {
		return new RenderPlComponentController(requestHandlerMethods);
	}

	@Bean
	public Controller renderPlComponentJsonController() {
		return new RenderPlComponentJsonController(requestHandlerMethods);
	}

	@Bean
	public Controller renderPlController() {
		return new RenderPlController(requestHandlerMethods);
	}

	@Bean
	public Controller robotsTxtController() {
		return new RobotsTxtController(requestHandlerMethods);
	}

	@Bean
	public Controller rssFeedController() {
		return new RssFeedController(requestHandlerMethods);
	}

	@Bean
	public Controller rssFeedUsingAddedDatesController() {
		return new RssFeedUsingAddedDatesController(requestHandlerMethods);
	}

	@Bean
	public Controller searchPageController() {
		return new SearchPageController(requestHandlerMethods);
	}

	@Bean
	public Controller serverStatusController() {
		return new ServerStatusController(requestHandlerMethods);
	}

	@Bean
	public Controller serviceWorkerController() {
		return new ServiceWorkerController(requestHandlerMethods);
	}

	@Bean
	public Controller serveModelController() {
		return new ServeModelController(requestHandlerMethods);
	}

	@Bean
	public Controller sitemapIndexController() {
		return new SitemapIndexController(requestHandlerMethods);
	}

	@Bean
	public Controller sitemapPageController() {
		return new SitemapPageController(requestHandlerMethods);
	}

	@Bean
	public Controller testLoggerController() {
		return new TestLoggerController(requestHandlerMethods);
	}

	@Bean
	public Controller ugcFeedbackController() {
		return new UgcFeedbackController(requestHandlerMethods);
	}

	@Bean
	public Controller ugcRatingController() {
		return new UgcRatingController(requestHandlerMethods);
	}

	@Bean
	public Controller resourceHandlerController() {
		return new ResourceHandlerController(resourceHandlerMethods);
	}

	@Bean
	public Controller faviconHandlerController() {
		return new FaviconHandlerController(renderUtils, resourceHandlerMethods);
	}

	@Bean
	public Controller appleFaviconHandlerController() {
		return new AppleFaviconHandlerController(renderUtils, resourceHandlerMethods);
	}
	
	@Bean
	public Controller appTasksController() {
		return new AppTasksController(requestHandlerMethods);
	}
	
	@Bean
	public Controller appConfigsController() {
		return new AppConfigsController(requestHandlerMethods);
	}

	@Bean
	public Controller auth0LoginController(){
		return new Auth0LoginController(requestHandlerMethods);
	}

	@Bean
	public Controller auth0LogoutController(){
		return new Auth0LogoutController(requestHandlerMethods);
	}

	@Bean
	public Controller resoundReviewStatusController() {
		return new ResoundReviewStatusController(requestHandlerMethods);
	}

	@Bean
	public Controller resoundSubmitReviewController() {
		return new ResoundSubmitReviewController(requestHandlerMethods);
	}

	@Bean
	public Controller resoundReactController() {
		return new ResoundReactController(requestHandlerMethods);
	}
	@Bean
	public Controller bookmarksController() {
		return new BookmarksController(bookmarksService);
	}

	@Bean
	public Controller contentGraphController() {
		return new ContentGraphController(contentGraphService);
	}

	@Bean
	public Controller pushlyServiceWorkerController() {
		String pushlyDomainKey = propertyFactory.getProperty(MantleExternalConfigKeys.PUSHLY_DOMAIN_KEY).asString(null).get();
		return new PushlyServiceWorkerController(requestHandlerMethods, pushlyDomainKey);
	}

	@Bean
	public Controller csrfSessionController() {
		boolean sessionCookiesEnabled = propertyFactory.getProperty(MantleExternalConfigKeys.SESSION_COOKIES_ENABLED).asBoolean(false).get();
		return new CsrfSessionController(sessionCookiesEnabled);
	}

	@Bean
	public SimpleUrlHandlerMapping simpleUrlHandlerMapping(List<Controller> endpointControllers) {

		SimpleUrlHandlerMapping simpleUrlMapping = new SimpleUrlHandlerMapping();
	    
		Map<String, Controller> urlMap = endpointControllers.stream()
				.filter(controller -> controller instanceof AbstractMantleEndpointController).collect(Collectors.toMap(
						controller -> ((AbstractMantleEndpointController) controller).getPath(), Function.identity()));
		simpleUrlMapping.setUrlMap(urlMap);
		simpleUrlMapping.setCorsConfigurationSource(buildCorsMapping(endpointControllers));
		simpleUrlMapping.setCorsProcessor(new MantleCorsProcessor(mantleCorsConfigs.getPrimaryDomain()));
		simpleUrlMapping.setInterceptors(this.getInterceptors(mvcConversionService(),mvcResourceUrlProvider()));
		simpleUrlMapping.setOrder(Ordered.LOWEST_PRECEDENCE);

		setPathsToExcludeForLegacyUrlMap( endpointControllers);
		return simpleUrlMapping;
	}


	private void setPathsToExcludeForLegacyUrlMap(List<Controller> endpointControllers){
		List<String> pathsToExclude = endpointControllers.stream().filter(controller -> controller instanceof AbstractMantleEndpointController
																						&&!(controller instanceof DocumentController) )
				.map(controller ->  ((AbstractMantleEndpointController) controller).getPath())
				.collect(Collectors.toList());
		legacyUrlMap.addPathsToExclude(pathsToExclude);
	}

	private static List<String> getPathPatterns(String pathPatterns) {
		List<String> answer = Collections.emptyList();
		if (StringUtils.isNotBlank(pathPatterns)) {
			answer = Arrays.asList(pathPatterns.split(",")).stream().map(pattern -> pattern.trim()).distinct()
					.collect(Collectors.toList());
		}
		return answer;
	}

	private static List<String> getIncludedPathPatterns(AbstractMantleInterceptor interceptor) {
		return getPathPatterns(interceptor.getIncludePathPatterns());
	}

	private static List<String> getExcludedPathPatterns(AbstractMantleInterceptor interceptor) {
		return getPathPatterns(interceptor.getExcludePathPatterns());
	}

	private static CorsConfiguration getCorsConfig() {
		//Note you must go from specific to less specific paths with CORS registrations for them to work properly
		CorsConfiguration staticCorsConfiguration = new CorsConfiguration();
		staticCorsConfiguration.addAllowedOrigin("*");
		staticCorsConfiguration.addAllowedMethod("*");
		staticCorsConfiguration.addAllowedHeader("*");
		return staticCorsConfiguration;
	}

	private CorsConfiguration getDocumentCorsConfig() {
		String primaryDomain = getPrimaryDomain();
		/** sproutsocial.com is a social media management vendor that we use to manage to our Social media.
		 * they scrape our site to show preview of our articles in their tools. Unless we add them here, they won't be able to
		 * scrape. More details - https://dotdash.atlassian.net/browse/GLBE-8254
		 */
		Set<String> allowedOrigins = Sets.newHashSet("sproutsocial.com");
		MantleCorsConfigs mantleCorsConfigs = new MantleCorsConfigs(primaryDomain, allowedOrigins);
		return MantleDomainNameCorsConfiguration.buildCorsConfigsFromMantleConfigs(mantleCorsConfigs);
	}
	
	private UrlBasedCorsConfigurationSource buildCorsMapping(List<Controller> endpointControllers) {
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration staticCorsConfiguration = getCorsConfig();

		//Excludes paths from CORS to enable embedding
		//Note you must go from specific to less specific paths with CORS registrations for them to work properly
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/static/**", staticCorsConfiguration);
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/favicon.ico", staticCorsConfiguration);
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/apple-touch-icon*.png", staticCorsConfiguration);
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/tools/**", staticCorsConfiguration);
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/*", getDocumentCorsConfig());
		//Setup Global CORS Configuration 
		CorsConfiguration globalCorsConfiguration = MantleDomainNameCorsConfiguration.buildCorsConfigsFromMantleConfigs(mantleCorsConfigs);
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", globalCorsConfiguration);
		
		return urlBasedCorsConfigurationSource;
	}
}
