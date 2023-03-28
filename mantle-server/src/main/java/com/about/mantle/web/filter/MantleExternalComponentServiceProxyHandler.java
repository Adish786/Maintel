package com.about.mantle.web.filter;

import static org.apache.commons.lang3.StringUtils.stripToNull;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.http.MantleHttpProxy;
import com.about.mantle.http.MantleHttpProxy.UriRewriter;
import com.about.mantle.model.services.ExternalComponentService;

/**
 * Responsible for _proxying_ incoming request to the service (url) identified
 * by `externalComponentService` query param's value. This value is used to
 * lookup service in {@link ExternalComponentServiceRegistry}. Proxyed request
 * will be striped off `externalComponentService` query param, other information
 * (path and queryString) kept as is.
 *
 */
public class MantleExternalComponentServiceProxyHandler implements ExternalServiceProxyHandler {

	public static final String EXTERNAL_COMPONENT_SERVICE_QUERY_PARAM = "externalComponentService";
	private static final String EXTERNAL_COMPONENT_SERVICE_QUERY_PARAM_REGEX = EXTERNAL_COMPONENT_SERVICE_QUERY_PARAM + "=[^&]+&*";
	
	private static final Logger logger = LoggerFactory.getLogger(MantleExternalComponentServiceProxyHandler.class);
	private final Map<String, ExternalComponentService> externalServicesMap;
	private final Map<String, MantleHttpProxy> serviceToProxy;

	public MantleExternalComponentServiceProxyHandler(Map<String, ExternalComponentService> externalServicesMap) {
		this.externalServicesMap = externalServicesMap;
		serviceToProxy = getServiceToProxyMap();
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response) {
		boolean answer = false;
		String parameterServiceName = null;
		try {
			//Use getQueryString() instead of getParameter() in order to preserve the input stream of the initial request
			Map<String, List<String>> queryParams = parseQueryParams(request.getQueryString());
			if(queryParams != null && queryParams.containsKey(EXTERNAL_COMPONENT_SERVICE_QUERY_PARAM)) {
				parameterServiceName = stripToNull(queryParams.get(EXTERNAL_COMPONENT_SERVICE_QUERY_PARAM).get(0));
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Unable to proxy to ExternalComponentService due to an unparseable query string", e);
		}
		
		if (parameterServiceName == null) {
			answer = true; // if param is not present then continue.
		} else {
			// if param is present then try to get external service uri
			URI externalComponentServiceUri = determineExternalComponentServiceUri(parameterServiceName);
			if (externalComponentServiceUri == null) {
				answer = true; // if no uri is present i.e. invalid service name provided , log the error and
								// continue
			} else {
				// by this time, we are sure that we got valid service back from registry
				serviceToProxy.get(parameterServiceName).service(request, response);
			}
		}
		return answer;
	}

	private Map<String, MantleHttpProxy> getServiceToProxyMap() {

		Map<String, MantleHttpProxy> answer = new ConcurrentHashMap<>();

		for (String serviceName : externalServicesMap.keySet()) {
			URI externalComponentServiceUri = externalServicesMap.get(serviceName).getUri();
			ExternalServiceUriRewriter uriRewriter = new ExternalServiceUriRewriter(
					externalComponentServiceUri.toString(), EXTERNAL_COMPONENT_SERVICE_QUERY_PARAM_REGEX);

			String hostHeader = getHostHeader(serviceName);

			MantleHttpProxy proxy = new MantleHttpProxy.MantleHttpProxyBuilder(uriRewriter, new HttpClient(new SslContextFactory(true))).setHostHeader(hostHeader)
					.build();

			answer.put(serviceName, proxy);
		}
		return answer;
	}

	private String getHostHeader(String serviceName) {
		URI externalComponentService = externalServicesMap.get(serviceName).getUri();

		String hostHeader = null;

		if (externalComponentService.getPort() == -1) {
			hostHeader = externalComponentService.getHost();
		} else {
			hostHeader = StringUtils.join(externalComponentService.getHost(), ":",
					Integer.toString(externalComponentService.getPort()));
		}
		return hostHeader;
	}

	private URI determineExternalComponentServiceUri(String paramValue) {
		ExternalComponentService externalComponentService = null;
		if (StringUtils.isBlank(paramValue)) {
			logger.error("Cannot determine name of externalComponentService given paramValue: {}", paramValue);
		} else {
			externalComponentService = externalServicesMap.get(paramValue);
			if (externalComponentService == null) {
				logger.error("ExternalComponentService {} not found in registry", paramValue);
			}
		}
		return externalComponentService != null ? externalComponentService.getUri() : null;
	}
	
	/* This has been copied directly from CommonUrlData and should be revisited after CMRC-208 and CMRC-209 have been finished.
	 * If RequestContextFilter has been moved ahead of this filter the request context can be used directly. Otherwise, that method
	 * should be made public and static or that method should be extracted into a url utility class and 
	 * both CommonUrlData and this class can use that method. This will be done as part of CMRC-210
	 */
	private Map<String, List<String>> parseQueryParams(String query) throws UnsupportedEncodingException {
		if (query == null) {
			return null;
		} else {
			Map<String, List<String>> queryParams = new LinkedHashMap<>();
			if (!query.isEmpty()) {
				query = query.charAt(0) == '?' ? query.substring(1) : query;
				for (String pair : StringUtils.split(query, "&")) {
					String[] nameValue = StringUtils.split(pair, '=');
					if (nameValue.length > 0) {
						String newValue = null;
						if (nameValue.length > 1) {
							newValue = nameValue[1];
						} else if (pair.endsWith("=")) {
							newValue = "";
						}
						if (newValue != null) {
							newValue = URLDecoder.decode(newValue, CharEncoding.UTF_8);
						}
						List<String> values = queryParams.get(nameValue[0]);
						if (values != null) {
							values.add(newValue);
						} else {
							values = new ArrayList<String>();
							values.add(newValue);
							queryParams.put(nameValue[0], values);
						}
					}
				}
			}
			return queryParams;
		}
	}

	/**
	 * UriRewriter for this handler. 
	 * proxyHost is the host of the proxy service
	 * queryParamRegex is used to strip out the `externalComponentService` from final proxy url
	 */
	private static class ExternalServiceUriRewriter implements UriRewriter {

		private final String proxyHost;
		private final Pattern queryParamPattern;

		public ExternalServiceUriRewriter(String proxyHost, String queryParamRegex) {
			this.proxyHost = proxyHost;
			this.queryParamPattern = Pattern.compile(queryParamRegex);
		}

		@Override
		public String rewrite(String req) {
			StringBuilder sb = new StringBuilder();
			try {
				URI requestUri = new URI(req);
				Matcher matcher = queryParamPattern.matcher(requestUri.getQuery());
				sb.append(proxyHost).append(requestUri.getPath());

				String finalQueryString = matcher.replaceAll("");
				if(!StringUtils.isBlank(finalQueryString)) {
					sb.append("?").append(finalQueryString);	
				}
			} catch (Exception e) {
				throw new GlobeException("Couldn't create valid URI from incoming request '" + req + "' while handing it off to proxy", e);
			}
			return sb.toString();
		}
	}

}
