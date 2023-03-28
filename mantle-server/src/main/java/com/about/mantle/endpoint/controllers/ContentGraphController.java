package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.contentgraph.AssetMetadata;
import com.about.mantle.model.contentgraph.EntityId;
import com.about.mantle.model.contentgraph.PresignedUrl;
import com.about.mantle.model.services.contentgraph.ContentGraphService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Exposes certain content graph services to Globe frontend.
 */
public class ContentGraphController extends AbstractMantleEndpointController {

	private static final Logger logger = LoggerFactory.getLogger(ContentGraphController.class);

	private final ContentGraphService contentGraphService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public ContentGraphController(ContentGraphService contentGraphService) {
		this.contentGraphService = contentGraphService; 
	}

	@Override
	public String getPath() {
		// Keeping it simple for now but in the event that this increases in scope it is
		// upon you to refactor this controller to handle multiple "operations".
		return "/content-graph/generatepresignedurl/public";
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (contentGraphService == null) {
			throw new GlobeException("Content graph service is not configured for the application.");
		}

		RequestContext requestContext = RequestContext.get(request);
		if (requestContext.getAccountInfo() == null || requestContext.getHashId() == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} else {
			AssetMetadata assetMetadata = null;

			try {
				assetMetadata = new AssetMetadata.Builder()
						.entityId(new EntityId.Builder()
								.provider(requestContext.getParameterSingle("provider"))
								.id(requestContext.getParameterSingle("providerId"))
								.build())
						.contentType(requestContext.getParameterSingle("contentType"))
						.fileName(uniqueFilename(requestContext.getParameterSingle("filename")))
						.build();
			} catch (GlobeException e) {
				logger.debug("bad request", e);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}

			if (assetMetadata != null) {
				PresignedUrl presignedUrl = contentGraphService.generatePresignedUrl(assetMetadata);

				if (presignedUrl == null) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				} else {
					response.setStatus(HttpServletResponse.SC_OK);
					response.setContentType(MediaType.APPLICATION_JSON);
					objectMapper.writeValue(response.getOutputStream(), presignedUrl);
				}
			}
		}

		response.flushBuffer();
	}

	// Carry over Element handling of filename to limit the potential for overwriting another user's file
	private String uniqueFilename(String filename) {
		String answer = null;
		if (StringUtils.isNotBlank(filename)) {
			answer = Long.toString(System.currentTimeMillis()) + filename;
		}
		return answer;
	}

}
