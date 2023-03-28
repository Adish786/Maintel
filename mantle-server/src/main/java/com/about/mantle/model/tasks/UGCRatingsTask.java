package com.about.mantle.model.tasks;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.extended.ratings.UGCUserRating;
import com.about.mantle.model.extended.ugc.Feedback;
import com.about.mantle.model.services.ugc.UGCFeedbackService;

@Tasks
public class UGCRatingsTask {
	private final UGCFeedbackService ugcFeedbackService;

	public static final String STAR_RATING_TYPE = "starRating";
	public static final String MADE_IT_TYPE = "madeIt";

	public UGCRatingsTask(UGCFeedbackService ugcFeedbackService) {
		this.ugcFeedbackService = ugcFeedbackService;
	}

	private String getUserIdFromRequestContext(RequestContext requestContext) {
		return requestContext.getUserId();
	}

	@Task(name = "getUserRating")
	@TimedComponent(category = "task")
	public Map<String, UGCUserRating> fetchUserRatingByDocId(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "documentId") Long documentId) {
		return fetchUserRatingByUserId(requestContext, documentId, null);
	}

	@Task(name = "getUserRating")
	@TimedComponent(category = "task")
	public Map<String, UGCUserRating> fetchUserRatingByUserId(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "documentId") Long documentId, @TaskParameter(name = "userId") String userId) {
		return fetchUserRating(requestContext, documentId, userId, null);
	}

	@Task(name = "getUserRating")
	@TimedComponent(category = "task")
	public Map<String, UGCUserRating> fetchUserRatingByDocIdAndType(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "documentId") Long documentId, @TaskParameter(name = "type") String type) {
		return fetchUserRating(requestContext, documentId, null, type);
	}

	@Task(name = "getUserRating")
	@TimedComponent(category = "task")
	public Map<String, UGCUserRating> fetchUserRating(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "documentId") Long documentId, @TaskParameter(name = "userId") String userId,
			@TaskParameter(name = "type") String type) {

		if (userId == null) userId = getUserIdFromRequestContext(requestContext);

		Map<String, UGCUserRating> answer = new HashMap<>();
		Feedback feedback = ugcFeedbackService.getFeedback(documentId, userId);
		if (feedback != null) {
			if (StringUtils.isBlank(type) || STAR_RATING_TYPE.equalsIgnoreCase(type)) {
				answer.put(STAR_RATING_TYPE, UGCUserRating.fromScore(feedback.getStarRating()));
			}
			if (StringUtils.isBlank(type) || MADE_IT_TYPE.equalsIgnoreCase(type)) {
				answer.put(MADE_IT_TYPE, UGCUserRating.fromScore(feedback.getMadeIt()));
			}
		}
		return answer;
	}

	public void patchUserRating(@RequestContextTaskParameter RequestContext requestContext) {
		String[] types = requestContext.getParameters().get("type");
		String[] ratingScores = requestContext.getParameters().get("rating");
		String[] docIds = requestContext.getParameters().get("docId");

		if (types == null || ratingScores == null || docIds == null) return;

		String type = types[0];
		String userId = getUserIdFromRequestContext(requestContext);
		Long docId;
		Short ratingScore;

		try {
			docId = Long.parseLong(docIds[0]);
			ratingScore = Short.parseShort(ratingScores[0]);
		} catch (NumberFormatException e) {
			throw new GlobeException("Not a valid docId", e);
		}

		Feedback feedback = new Feedback();
		feedback.setDocId(docId);
		feedback.setUserId(userId);
		if (STAR_RATING_TYPE.equalsIgnoreCase(type)) {
			feedback.setStarRating(ratingScore);
		} else if (MADE_IT_TYPE.equalsIgnoreCase(type)) {
			feedback.setMadeIt(ratingScore);
		} else {
			throw new GlobeException("unknown type: " + type);
		}

		ugcFeedbackService.patchFeedback(feedback, ratingScore > 0 ? "SET" : "REMOVE");
	}

}
