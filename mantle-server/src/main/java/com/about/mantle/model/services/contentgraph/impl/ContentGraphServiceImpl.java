package com.about.mantle.model.services.contentgraph.impl;

import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.hippodrome.models.request.HttpMethod;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.contentgraph.AssetMetadata;
import com.about.mantle.model.contentgraph.PresignedUrl;
import com.about.mantle.model.services.contentgraph.ContentGraphService;

public class ContentGraphServiceImpl extends AbstractHttpServiceClient implements ContentGraphService {

	private static final Logger logger = LoggerFactory.getLogger(ContentGraphServiceImpl.class);
	private static final String GENERATE_PRESIGNED_URL_PATH = "/v4/generatepresignedurl/public";

	private final String apiKey;

	public ContentGraphServiceImpl(HttpServiceClientConfig httpServiceClientConfig, String apiKey) {
		super(httpServiceClientConfig);
		this.apiKey = apiKey;
	}

	@Override
	public PresignedUrl generatePresignedUrl(AssetMetadata assetMetadata) {
		PresignedUrl answer = null;
		Entity<AssetMetadata> payload = Entity.json(assetMetadata);
		WebTarget webTarget = baseTarget.path(GENERATE_PRESIGNED_URL_PATH);
		Response response;
		try {
			response = sendRequest(webTarget, HttpMethod.POST, payload, getConfig().getMediaType(), headers());
		} catch (Exception e) {
			logger.error("Request failed: URI {}, Payload {}, Reason {}",
						webTarget.getUri(), assetMetadata, e.getMessage(), e);
			throw e;
		}
		if (response.getStatus() == 200) {
			answer = response.readEntity(PresignedUrl.class);
			logger.debug("payload {}, answer: {}", assetMetadata, answer);
		} else {
			logger.error("Failed to generate presigned url: http status {}, payload {}, response {}",
					response.getStatus(), assetMetadata, response.readEntity(Map.class));
		}
		return answer;
	}

	private Map<String, Object> headers() {
		return Map.of("x-api-key", apiKey);
	}

}
