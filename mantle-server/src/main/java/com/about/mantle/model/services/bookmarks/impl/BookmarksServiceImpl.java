package com.about.mantle.model.services.bookmarks.impl;

import java.io.Serializable;
import java.util.Map;
import java.util.function.BooleanSupplier;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.hippodrome.models.request.HttpMethod;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.bookmarks.BookmarksRequestContext;
import com.about.mantle.model.services.bookmarks.BookmarksService;

public class BookmarksServiceImpl extends AbstractHttpServiceClient implements BookmarksService {

	private static final Logger logger = LoggerFactory.getLogger(BookmarksServiceImpl.class);
	private static final String BOOKMARK_PATH_TEMPLATE = "/v3/user/{hashId}/document/{seleneDocId}/bookmark";
	private static final String SAVE_BOOKMARK_PATH = "/v3/bookmarks";
	private final String brand;

	public BookmarksServiceImpl(HttpServiceClientConfig httpServiceClientConfig, String brand) {
		super(httpServiceClientConfig);
		this.brand = brand;

		if (StringUtils.isBlank(brand)) {
			throw new GlobeException("Bookmarks service requires 'brand' configuration.");
		}
	}

	@Override
	public boolean isBookmarked(BookmarksRequestContext reqCtx) {
		WebTarget webTarget = webTargetForExistingBookmark(reqCtx);
		Response response = makeRequest(reqCtx, webTarget, HttpMethod.GET);
		return handleResponse(response, () -> {
			boolean answer = false;
			if (response.getStatus() == 200) {
				answer = true;
			} else if (response.getStatus() != 404) {
				logUnexpectedResponse(reqCtx.getDocId(), webTarget, HttpMethod.GET, response);
			}
			return answer;
		});
	}

	@Override
	public boolean deleteBookmark(BookmarksRequestContext reqCtx) {
		WebTarget webTarget = webTargetForExistingBookmark(reqCtx);
		Response response = makeRequest(reqCtx, webTarget, HttpMethod.DELETE);
		return handleResponse(response, () -> {
			boolean answer = false;
			if (response.getStatus() == 204) {
				answer = true;
			} else if (response.getStatus() != 404) {
				logUnexpectedResponse(reqCtx.getDocId(), webTarget, HttpMethod.DELETE, response);
			}
			return answer;
		});
	}

	@Override
	public boolean saveBookmark(BookmarksRequestContext reqCtx) {
		WebTarget webTarget = baseTarget.path(SAVE_BOOKMARK_PATH);
		Response response = makeRequest(reqCtx, webTarget, HttpMethod.POST);
		return handleResponse(response, () -> {
			boolean answer = false;
			if (response.getStatus() == 200) {
				answer = true;
			} else {
				logUnexpectedResponse(reqCtx.getDocId(), webTarget, HttpMethod.POST, response);
			}
			return answer;
		});
	}

	private WebTarget webTargetForExistingBookmark(BookmarksRequestContext reqCtx) {
		WebTarget webTarget = baseTarget.path(BOOKMARK_PATH_TEMPLATE)
					.resolveTemplate("hashId", reqCtx.getHashId(), true)
					.resolveTemplate("seleneDocId", reqCtx.getDocId());
		return webTarget;
	}

	private Response makeRequest(BookmarksRequestContext reqCtx, WebTarget webTarget, HttpMethod httpMethod) {
		try {
			Entity<Map<String, Serializable>> entity = HttpMethod.POST.equals(httpMethod) ? postPayload(reqCtx) : null;
			return sendRequest(webTarget, httpMethod, entity, getConfig().getMediaType(), headers(reqCtx));
		} catch (Exception e) {
			logger.error("Request failed: seleneDocId {}, URI {}, HttpMethod {}, Reason {}",
						reqCtx.getDocId(), webTarget.getUri(), httpMethod, e.getMessage(), e);
			throw e;
		}
	}

	private Entity<Map<String, Serializable>> postPayload(BookmarksRequestContext reqCtx) {
		return Entity.json(Map.of(
				"brand", brand,
				"hashId", reqCtx.getHashId(),
				"seleneDocId", reqCtx.getDocId()
			));
	}

	private static Map<String, Object> headers(BookmarksRequestContext reqCtx) {
		return Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + reqCtx.getAccessToken());
	}

	private static void logUnexpectedResponse(Long seleneDocId, WebTarget webTarget, HttpMethod httpMethod, Response response) {
		logger.error("Unexpected response from Bookmarks service: seleneDocId {}, URI {}, HttpMethod {}, Status {}, Response {}",
					seleneDocId, webTarget.getUri(), httpMethod, response.getStatus(), response.readEntity(Map.class));
	}

	private static boolean handleResponse(Response response, BooleanSupplier handler) {
		try {
			return handler.getAsBoolean();
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

}
