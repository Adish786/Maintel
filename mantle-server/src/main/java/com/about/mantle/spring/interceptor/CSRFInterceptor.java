package com.about.mantle.spring.interceptor;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.stripToNull;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.RequestContext;
import com.about.hippodrome.models.media.VersionedMediaTypes;
import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSRFInterceptor extends AbstractMantleInterceptor {

	private static Logger logger = LoggerFactory.getLogger(CSRFInterceptor.class);

	public boolean preHandle(HttpServletRequest httpRequest, HttpServletResponse response, Object handler) {
		boolean answer = true;
		// we only want to check CSRF requests for POSTs, and NOT for deferred-component
		// ajax requests (recognizable via "cr") param per GLBE-5189
		if (httpRequest.getMethod().equalsIgnoreCase("POST") && httpRequest.getParameter("cr") == null) {
			// Check CSRF Token
			RequestContext requestContext = RequestContext.get(httpRequest);
			String[] csrfTokens = requestContext.getParameters().get("CSRFToken");
			String csrfToken = csrfTokens == null ? null : stripToNull(csrfTokens[0]);

			if (isBlank(csrfToken) && VersionedMediaTypes.DEFAULT_APPLICATION_JSON.equals(httpRequest.getContentType())) {

				try {
					csrfToken = getCsrfTokenFromPostBody(httpRequest);
				} catch (Exception e) {
					
					//Don't log on null, invalid token is the right answer for a null
					if(requestContext.getHeaders().getReferer() != null) {
						logger.error(
							"Could not parse CSRF token from body of post request for url [{}] and referrer [{}].  Will fail check",
							requestContext.getRequestUrl(), requestContext.getHeaders().getReferer(), e);
					}
					
					csrfToken = "INVALID";
				}
			}
			String secret = requestContext.getCsrfToken();
			if (secret == null || !secret.equals(csrfToken)) {
				response.setStatus(HttpStatus.SC_FORBIDDEN);
				answer = false;
			}
		}

		return answer;
	}

	private String getCsrfTokenFromPostBody(HttpServletRequest httpRequest) throws IOException {
		String csrfToken;
		ObjectMapper mapper = new StandardObjectMapperProvider().getContext(null);
		CSRFToken token = mapper.readValue(httpRequest.getInputStream(), CSRFToken.class);
		csrfToken = token.getCsrfToken();
		return csrfToken;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class CSRFToken {
		@JsonProperty("CSRFToken")
		String csrfToken;

		public String getCsrfToken() {
			return csrfToken;
		}

		public void setCsrfToken(String csrfToken) {
			this.csrfToken = csrfToken;
		}

	}

	@Override
	final public String getIncludePathPatterns() {
		return "/**";
	}

}
