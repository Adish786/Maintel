package com.about.mantle.endpoint.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.about.globe.core.http.RequestContext;
import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * For use with the `mntl-csrf` component in order to make contact on a
 * browser-disk-cached page to create a new session.
 */
public class CsrfSessionController extends AbstractMantleEndpointController {
	private static final ObjectMapper objectMapper = new StandardObjectMapperProvider().getContext(null);
	private final boolean sessionCookiesEnabled;

	public CsrfSessionController(boolean sessionCookiesEnabled) {
		this.sessionCookiesEnabled = sessionCookiesEnabled;
	}

	@Override
	public String getPath() {
		return "/csrf-session/refresh";
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (sessionCookiesEnabled) {
			var value = Map.of("csrfToken", RequestContext.get(request).getCsrfToken());
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(MediaType.APPLICATION_JSON);
			response.setHeader("Cache-Control", "no-store");
			objectMapper.writeValue(response.getOutputStream(), value);
		} else {
			render403(request, response);
		}
	}
}