package com.about.mantle.model.services.ugc.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.hippodrome.models.request.HttpMethod;
import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.ugc.AggregatedFeedbacks;
import com.about.mantle.model.extended.ugc.AggregatedFeedbacksRequestContext;
import com.about.mantle.model.extended.ugc.AggregatedFeedbacksRequestContext.Builder;
import com.about.mantle.model.extended.ugc.Feedback;
import com.about.mantle.model.extended.ugc.PhotoGalleryItem;
import com.about.mantle.model.extended.ugc.PhotoGalleryRequestContext;
import com.about.mantle.model.extended.ugc.SortBy;
import com.about.mantle.model.services.ugc.UGCFeedbackService;

public class UGCFeedbackServiceImpl extends AbstractHttpServiceClient implements UGCFeedbackService {

	private static final Logger logger = LoggerFactory.getLogger(UGCFeedbackServiceImpl.class);

	private static final String UGC_SERVICES_FEEDBACK_PATH = "/feedback";
	private static final List<String> anonymousDisplayNames = List.of("anonymous", "deleteduser", "undefined");
	private static final String defaultAnonymousDisplayName = "Anonymous";
	private static final String anonymousDisplayNameMessageKey = "ugcAnonymousDisplayName";

	// Default sort and limit are strings to emulate how they are represented in message.properties
	// They are parsed to their associated type when assigned to the instance variable denoting the defaults.
	private static final String defaultAggregatedFeedbacksDefaultSort = "DATE_DESC";
	private static final String defaultAggregatedFeedbacksDefaultLimit = "99";
	private static final String aggregatedFeedbacksDefaultSortMessageKey = "ugcAggregatedFeedbacksDefaultSort";
	private static final String aggregatedFeedbacksDefaultLimitMessageKey = "ugcAggregatedFeedbacksDefaultLimit";

	private static class FeedbackResponse extends BaseResponse<Feedback> implements Serializable {
		private static final long serialVersionUID = 1L;
	}

	private static class AggregatedFeedbacksResponse extends BaseResponse<AggregatedFeedbacks> implements Serializable {
		private static final long serialVersionUID = 1L;
	}

	private static class PhotoGalleryResponse extends BaseResponse<SliceableListEx<PhotoGalleryItem>> implements Serializable {
		private static final long serialVersionUID = 1L;
	}

	private final String anonymousDisplayName;
	private final SortBy aggregatedFeedbacksDefaultSort;
	private final Integer aggregatedFeedbacksDefaultOffset;
	private final Integer aggregatedFeedbacksDefaultLimit;

	public UGCFeedbackServiceImpl(HttpServiceClientConfig httpServiceClientConfig, Map<String, String> messageProperties) {
		super(httpServiceClientConfig);
		anonymousDisplayName = StringUtils.defaultIfBlank(messageProperties.get(anonymousDisplayNameMessageKey), defaultAnonymousDisplayName);
		aggregatedFeedbacksDefaultOffset = 0;
		try {
			aggregatedFeedbacksDefaultLimit = Integer.parseInt(
					StringUtils.defaultIfBlank(messageProperties.get(aggregatedFeedbacksDefaultLimitMessageKey),
							defaultAggregatedFeedbacksDefaultLimit));
			aggregatedFeedbacksDefaultSort = SortBy.valueOf(
					StringUtils.defaultIfBlank(messageProperties.get(aggregatedFeedbacksDefaultSortMessageKey),
							defaultAggregatedFeedbacksDefaultSort));
		} catch (Exception e) {
			throw new GlobeException("Failed to parse default configurations in message.properties", e);
		}
	}

	@Override
	public Feedback postFeedback(Feedback feedback) {
		WebTarget webTarget = baseTarget.path(UGC_SERVICES_FEEDBACK_PATH);
		FeedbackResponse response = readResponse(webTarget, FeedbackResponse.class, HttpMethod.POST, Entity.entity(feedback, super.getConfig().getMediaType()));
		if (response.getData() == null) {
			logger.error("Failed to POST feedback: {}", response.getStatus() == null ? "unknown" : response.getStatus().toString());
			throw new GlobeException("Failed to save feedback.");
		}
		return response.getData();
	}

	@Override
	public Feedback patchFeedback(Feedback feedback, String patchOperation) {
		WebTarget webTarget = baseTarget.path(UGC_SERVICES_FEEDBACK_PATH).queryParam("patchOperation", patchOperation);
		FeedbackResponse response = readResponse(webTarget, FeedbackResponse.class, HttpMethod.PATCH, Entity.entity(feedback, super.getConfig().getMediaType()));
		if (response.getData() == null) {
			logger.error("Failed to PATCH {} feedback: {}", patchOperation, response.getStatus() == null ? "unknown" : response.getStatus().toString());
			throw new GlobeException("Failed to save feedback.");
		}
		return response.getData();
	}

	@Override
	public Feedback getFeedback(Long docId, String userId) {
		WebTarget webTarget = baseTarget.path(UGC_SERVICES_FEEDBACK_PATH).path(docId.toString()).path(userId);
		FeedbackResponse response = readResponse(webTarget, FeedbackResponse.class);
		return response.getData();
	}

	@Override
	public AggregatedFeedbacks getAggregatedFeedbacks(AggregatedFeedbacksRequestContext reqCtx) {
		WebTarget webTarget = baseTarget.path(UGC_SERVICES_FEEDBACK_PATH).path("aggregate").path(reqCtx.getDocId().toString());

		webTarget = webTarget
				.queryParam("sort", reqCtx.getSort())
				.queryParam("offset", reqCtx.getOffset())
				.queryParam("limit", reqCtx.getLimit())
				.queryParam("withReview", true);

		AggregatedFeedbacksResponse response = readResponse(webTarget, AggregatedFeedbacksResponse.class, HttpMethod.GET);
		if (response.getData() == null) {
			// this should always return data even if a doc has no reviews/ratings
			// worthy of an error and need to throw so we don't inadvertently cache a bad response
			logger.error("Failed to get document {} feedback: {}", reqCtx.getDocId(),
						response.getStatus() == null ? "unknown" : response.getStatus().toString());
			throw new GlobeException("Aggregated feedbacks response data is null");
		}
		return response.getData();
	}

	@Override
	public Builder getDefaultAggregatedFeedbacksRequestContextBuilder(Long docId) {
		return new AggregatedFeedbacksRequestContext.Builder().docId(docId)
					.sort(aggregatedFeedbacksDefaultSort)
					.offset(aggregatedFeedbacksDefaultOffset)
					.limit(aggregatedFeedbacksDefaultLimit);
	}

	@Override
	public SliceableListEx<PhotoGalleryItem> getPhotoGallery(PhotoGalleryRequestContext reqCtx) {
		WebTarget webTarget = baseTarget.path(UGC_SERVICES_FEEDBACK_PATH).path("photogallery").path(reqCtx.getDocId().toString());

		if (reqCtx.getOffset() != null)
			webTarget = webTarget.queryParam("offset", reqCtx.getOffset());

		if (reqCtx.getLimit() != null)
			webTarget = webTarget.queryParam("limit", reqCtx.getLimit());

		PhotoGalleryResponse response = readResponse(webTarget, PhotoGalleryResponse.class, HttpMethod.GET);
		if (response.getData() == null) {
			// this should always return data even if a doc has no user photos
			// worthy of an error and need to throw so we don't inadvertently cache a bad response
			logger.error("Failed to get document {} photogallery: {}", reqCtx.getDocId(),
						response.getStatus() == null ? "unknown" : response.getStatus().toString());
			throw new GlobeException("UGC photogallery response data is null");
		}
		return response.getData();
	}

	private boolean isAnonymousUser(String displayName) {
		return StringUtils.isBlank(displayName) || anonymousDisplayNames.contains(displayName.trim().toLowerCase());
	}

	private <T extends BaseResponse<?>> T processResponse(T response) {
		if (response instanceof FeedbackResponse) {
			FeedbackResponse feedbackResponse = (FeedbackResponse) response;
			Feedback feedback = feedbackResponse.getData();
			if (feedback != null && isAnonymousUser(feedback.getDisplayName())) {
				feedback.setDisplayName(anonymousDisplayName);
				feedback.setProfileUrl(null);
			}
		} else if (response instanceof AggregatedFeedbacksResponse) {
			AggregatedFeedbacksResponse aggregatedFeedbacksResponse = (AggregatedFeedbacksResponse) response;
			if (aggregatedFeedbacksResponse.getData() != null) {
				aggregatedFeedbacksResponse.getData().getFeedbacks().forEach(feedback -> {
					if (feedback != null && isAnonymousUser(feedback.getDisplayName())) {
						feedback.setDisplayName(anonymousDisplayName);
						feedback.setProfileUrl(null);
					}
				});
			}
		} else if (response instanceof PhotoGalleryResponse) {
			PhotoGalleryResponse photoGalleryResponse = (PhotoGalleryResponse) response;
			if (photoGalleryResponse.getData() != null) {
				photoGalleryResponse.getData().forEach(photoGalleryItem -> {
					if (photoGalleryItem != null && isAnonymousUser(photoGalleryItem.getDisplayName())) {
						photoGalleryItem.setDisplayName(anonymousDisplayName);
						photoGalleryItem.setProfileUrl(null);
					}
				});
			}
		}
		return response;
	}

	@Override
	protected <T extends BaseResponse<?>> T readResponse(WebTarget webTarget, GenericType<T> responseType, String acceptHeaderValue) {
		return processResponse(super.readResponse(webTarget, responseType, acceptHeaderValue));
	}

	@Override
	protected <T extends BaseResponse<?>, E> T readResponse(WebTarget webTarget, Class<T> responseType,
			HttpMethod method, Entity<E> entity, String acceptHeaderValue, Map<String, Object> header) {
		return processResponse(super.readResponse(webTarget, responseType, method, entity, acceptHeaderValue, header));
	}

}
