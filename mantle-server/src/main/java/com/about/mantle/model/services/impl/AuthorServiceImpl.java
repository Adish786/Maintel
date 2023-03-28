package com.about.mantle.model.services.impl;

import javax.ws.rs.client.WebTarget;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.AuthorEx;
import com.about.mantle.model.extended.responses.AuthorExResponse;
import com.about.mantle.model.services.AuthorService;

public class AuthorServiceImpl extends AbstractHttpServiceClient implements AuthorService {

	public static final String SELENE_AUTHOR_PATH = "/author";

	public AuthorServiceImpl(HttpServiceClientConfig httpServiceClientConfig) {
		super(httpServiceClientConfig);
	}

	@Override
	public AuthorEx getAuthorById(String id) {
		AuthorExResponse response = getAuthorById(id, AuthorExResponse.class);

		return response.getData();
	}

	private <T extends BaseResponse<?>> T getAuthorById(String id, Class<T> bindToTarget) {
		WebTarget webTarget = baseTarget.path(SELENE_AUTHOR_PATH).path("id").path(id);

		T response = readResponse(webTarget, bindToTarget);
		return response;
	}

}
