package com.about.mantle.model.services.client.selene;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;

import com.about.globe.core.exception.GlobeException;
import com.about.hippodrome.models.request.HttpMethod;
import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.hippodrome.restclient.jwt.JwtAuthTokenProvider;
import com.about.mantle.exception.SeleneUnavailableException;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;

/**
 * Http client which calls Selene Auth API for a token.
 * This is used in {@link JwtAuthClientImpl} which is used by {@link JwtAuthTokenProvider}}
 */
public class SeleneAuthServiceHttpClient extends AbstractHttpServiceClient {

	public SeleneAuthServiceHttpClient(HttpServiceClientConfig config) {
		super(config);
		baseTarget = baseTarget.path("auth");
	}

	public <T extends BaseResponse<?>> T authenticate(UserCredentials userCredentials, Class<T> bindToTarget) {
		WebTarget webTarget = baseTarget.path("token");
		T response;
		try {
			response = readResponse(webTarget, bindToTarget, HttpMethod.POST,
					Entity.entity(userCredentials, getConfig().getMediaType()), getConfig().getMediaType());
		} catch (MessageBodyProviderNotFoundException e) {
			throw new SeleneUnavailableException(e);
		}

		return response;
	}

	public <T extends BaseResponse<?>> T validate(String signedToken, Class<T> bindToTarget) {
		WebTarget webTarget = baseTarget.path("validate");
		Map<String, Object> header = new HashMap<>();
		header.put(HttpHeaders.AUTHORIZATION, "Token " + signedToken);

		return readResponse(webTarget, bindToTarget, HttpMethod.GET, null, getConfig().getMediaType(), header);
	}

	public <T extends BaseResponse<?>> T validateWithTokenProvider(Class<T> bindToTarget) {
		return readResponse(baseTarget.path("validate"), bindToTarget);
	}
}