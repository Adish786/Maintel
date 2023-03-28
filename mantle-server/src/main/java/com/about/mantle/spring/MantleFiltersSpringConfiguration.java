package com.about.mantle.spring;

import static com.about.mantle.spring.MantleFilterUtils.getConfiguredFilter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.DispatcherType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.http.HttpMethod;

import com.about.hippodrome.config.CommonPropertyFactory;
import com.about.hippodrome.config.HippodromePropertyFactory;
import com.about.mantle.app.MantleExternalConfigKeys;
import com.about.mantle.http.util.RequestSourceUtils;
import com.about.mantle.web.filter.ContentCachingRequestFilter;
import com.about.mantle.web.filter.EnsureHttpsFilter;
import com.about.mantle.web.filter.ExtendedIPDoSFilter;
import com.about.mantle.web.filter.ExternalServiceProxyFilter;
import com.about.mantle.web.filter.ExternalServiceProxyHandler;
import com.about.mantle.web.filter.MalformedURLFilter;
import com.about.mantle.web.filter.ResponseHeadersFilter;
import com.about.mantle.web.filter.SafelistHttpMethodFilter;
import com.about.mantle.web.filter.SandboxedContentDomainFilter;
import com.about.mantle.web.filter.TraceFilter;
import com.about.mantle.web.filter.XContentTypeOptionsFilter;

@Configuration
public class MantleFiltersSpringConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MantleFiltersSpringConfiguration.class);

	protected static final HippodromePropertyFactory propertyFactory = CommonPropertyFactory.INSTANCE.get();

	@Bean
	public FilterRegistrationBean<TraceFilter> traceFilter() {

		FilterRegistrationBean<TraceFilter> traceFilter = new FilterRegistrationBean<>();
		TraceFilter filter = new TraceFilter();
		traceFilter.setFilter(filter);
		traceFilter.addUrlPatterns("/*");
		traceFilter
				.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ERROR));
		traceFilter.setOrder(1);
		return traceFilter;
	}
	
	@Bean
	public FilterRegistrationBean<EnsureHttpsFilter> ensureHttpsFilter() {
		FilterRegistrationBean<EnsureHttpsFilter> ensureHttpsFilter = new FilterRegistrationBean<>();
		EnsureHttpsFilter filter = new EnsureHttpsFilter();
		ensureHttpsFilter.setFilter(filter);
		ensureHttpsFilter.addUrlPatterns("/*");
		ensureHttpsFilter.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
		ensureHttpsFilter.setOrder(2);
		return ensureHttpsFilter;
	}

	@Bean
	public FilterRegistrationBean<SafelistHttpMethodFilter> safelistHttpMethodFilter() {
		FilterRegistrationBean<SafelistHttpMethodFilter> safeMethodFilter = new FilterRegistrationBean<>();
		SafelistHttpMethodFilter filter = new SafelistHttpMethodFilter(getSafelistedHttpMethods());

		safeMethodFilter.setFilter(filter);
		safeMethodFilter.addUrlPatterns("/*");
		safeMethodFilter.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
		safeMethodFilter.setOrder(3);
		return safeMethodFilter;
	}

	@Bean
	public FilterRegistrationBean<ResponseHeadersFilter> responseHeadersFilter() {
		return getConfiguredFilter(new ResponseHeadersFilter(), EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), 4,
				"/*");
	}

	@Bean
	@Conditional(DdosFilterCreationCondition.class)
	public FilterRegistrationBean<ExtendedIPDoSFilter> dDosFilterForPost() {
		FilterRegistrationBean<ExtendedIPDoSFilter> dDosFilterForPost = getConfiguredFilter(new ExtendedIPDoSFilter(request -> {
					// google crawler exempt from more stringent POST method filtering
					return HttpMethod.POST.matches(request.getMethod()) && !RequestSourceUtils.isGoogleBot(request);
				}),
				EnumSet.of(DispatcherType.REQUEST), 5, "/*");

		Map<String, String> initParameters = new HashMap<>();
		initParameters.put("maxRequestsPerSec",
				propertyFactory.getProperty(MantleExternalConfigKeys.POST_DOS_REQ_SEC).asString("3").get());
		initParameters.put("delayMs", "-1");

		dDosFilterForPost.setInitParameters(initParameters);
		dDosFilterForPost.setName("DDosFilterForPost");
		return dDosFilterForPost;
	}

	@Bean
	@Conditional(DdosFilterCreationCondition.class)
	public FilterRegistrationBean<ExtendedIPDoSFilter> dDosFilterForAll() {
		Set<String> safelistIPs = new HashSet<>();
		String safelistStr = propertyFactory.getProperty(MantleExternalConfigKeys.DOS_REQ_SAFELIST).asString(null).get();
		if (safelistStr != null) {
			safelistIPs.addAll(Arrays.asList(safelistStr.trim().split(",")));
		}

		FilterRegistrationBean<ExtendedIPDoSFilter> dDosFilter = getConfiguredFilter(new ExtendedIPDoSFilter(request -> true),
				EnumSet.of(DispatcherType.REQUEST), 6, "/*");

		Map<String, String> initParameters = new HashMap<>();
		initParameters.put("ipWhitelist", String.join(",", safelistIPs));
		initParameters.put("maxRequestsPerSec",
				propertyFactory.getProperty(MantleExternalConfigKeys.DOS_REQ_SEC).asString("25").get());

		boolean rejectRequests = propertyFactory.getProperty(MantleExternalConfigKeys.DOS_REQ_REJECT).asBoolean(true).get();
		if (rejectRequests) {
			initParameters.put("delayMs", "-1");
		}

		dDosFilter.setInitParameters(initParameters);
		dDosFilter.setName("DDosFilterForAll");
		return dDosFilter;
	}

	@Bean
	@Autowired
	public FilterRegistrationBean<ExternalServiceProxyFilter> externalServiceProxyFilter(
			ExternalServiceProxyHandler proxyHandler) {
		return getConfiguredFilter(new ExternalServiceProxyFilter(proxyHandler),
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), 7, "/*");
	}

	@Bean
	public FilterRegistrationBean<MalformedURLFilter> malformedURLFilter() {
		return getConfiguredFilter(new MalformedURLFilter(), EnumSet.of(DispatcherType.REQUEST), 8, "/*");
	}

	@Bean
	public FilterRegistrationBean<XContentTypeOptionsFilter> xContentTypeOptionsFilter() {
		return getConfiguredFilter(new XContentTypeOptionsFilter(), EnumSet.allOf(DispatcherType.class), 9, "/*");
	}

	@Bean
	@Conditional(SandboxedContentDomainFilterCreationCondition.class)
	public FilterRegistrationBean<SandboxedContentDomainFilter> sandboxedContentDomainFilter() {
		return getConfiguredFilter(new SandboxedContentDomainFilter(propertyFactory), EnumSet.of(DispatcherType.REQUEST), 10, "/*");
	}

	@Bean
	public FilterRegistrationBean<ContentCachingRequestFilter> contentCachingFilter() {
		FilterRegistrationBean<ContentCachingRequestFilter> contentCachingFilter = getConfiguredFilter(new ContentCachingRequestFilter(),
				EnumSet.of(DispatcherType.REQUEST), 11, "/*");
		return contentCachingFilter;
	}

	/**
	 * Returns HTTP methods allowed for this app. Methods not in this set will
	 * return a 405 to the user. Default includes: HEAD, GET, POST, and OPTIONS.
	 * Note that PUT, DELETE, TRACE, and CONNECT pose possible security risks; only
	 * enable these if necessary. See GLBE-4984
	 * 
	 * @return Set of allowed HTTP methods
	 */
	// TODO: this is copy-pasted from hippodrom jetty, also made private. Revisit
	// this if it absolutely
	// need to be protected.
	private Set<String> getSafelistedHttpMethods() {

		Set<String> answer = new HashSet<>();

		answer.add("HEAD");
		answer.add("GET");
		answer.add("POST");
		answer.add("OPTIONS");

		return answer;

	}

	/**
	 * Conditional logic for creation of Ddods filters
	 */
	private static class DdosFilterCreationCondition implements Condition {
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			boolean condition = propertyFactory.getProperty(MantleExternalConfigKeys.DOS_ENABLED).asBoolean(true).get();

			if (!condition) {
				logger.warn("Denial of Service filter is not enabled.  Property `{}` is set to false",
						MantleExternalConfigKeys.DOS_ENABLED);
			}
			return condition;
		}
	}

	/**
	 * Conditional logic for creation of SandboxedContentDomainFilter filters
	 */
	private static class SandboxedContentDomainFilterCreationCondition implements Condition {
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			boolean condition = propertyFactory.getProperty(MantleExternalConfigKeys.SANDBOXED_CONTENT_ENABLED).asBoolean(true).get();

			if (!condition) {
				logger.warn("Sponsored Content Domain filter is not enabled.  Property `{}` is set to false",
						MantleExternalConfigKeys.SANDBOXED_CONTENT_ENABLED);
			}
			return condition;
		}

	}
	
}