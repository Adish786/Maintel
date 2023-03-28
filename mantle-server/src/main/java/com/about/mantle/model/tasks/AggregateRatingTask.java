package com.about.mantle.model.tasks;

import java.util.HashMap;
import java.util.Map;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.extended.ratings.AggregateRating;
import com.about.mantle.model.extended.ratings.DisqusAggregateRating;
import com.about.mantle.model.extended.ratings.UGCAggregateRating;
import com.about.mantle.model.extended.ugc.AggregatedFeedbacks;
import com.about.mantle.model.services.DisqusRatingService;
import com.about.mantle.model.services.ugc.UGCFeedbackService;

@Tasks
public class AggregateRatingTask {

	private final UGCFeedbackService ugcFeedbackService;
	private final DisqusRatingService disqusRatingService;
	private final boolean isDisqusAggregateRatingEnabled;
	public static final String STAR_RATING = "starRating";

	public AggregateRatingTask(UGCFeedbackService ugcFeedbackService, DisqusRatingService disqusRatingService, boolean isDisqusAggregateRatingEnabled) {
		this.ugcFeedbackService = ugcFeedbackService;
		this.disqusRatingService = disqusRatingService;
		this.isDisqusAggregateRatingEnabled = isDisqusAggregateRatingEnabled;
	}

	@Task(name = "getAggregateRating")
	@TimedComponent(category = "task")
	public Map<String, AggregateRating> fetchAggregateRating(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "documentId") Long documentId) {
		return fetchAggregateRating(requestContext, documentId, null);
	}

	@Task(name = "getAggregateRating")
	@TimedComponent(category = "task")
	public Map<String, AggregateRating> fetchAggregateRating(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "documentId") Long documentId, @TaskParameter(name = "type") String type) {

		Map<String, AggregateRating> answer = new HashMap<String, AggregateRating>();
		if(isDisqusAggregateRatingEnabled) {
			DisqusAggregateRating disqusAggregateRating = disqusRatingService.getAggregateRating(documentId);

			if (disqusAggregateRating == null) return null;

			answer.put(STAR_RATING, disqusAggregateRating);
		}else {
			AggregatedFeedbacks aggregatedFeedbacks = ugcFeedbackService.getAggregatedFeedbacks(
						ugcFeedbackService.getDefaultAggregatedFeedbacksRequestContextBuilder(documentId).build());

			if (aggregatedFeedbacks == null) return null;

			UGCAggregateRating ugcAggregateStarRating = new UGCAggregateRating();
			ugcAggregateStarRating.setCount(aggregatedFeedbacks.getAverageRating().getCount());
			ugcAggregateStarRating.setRating(aggregatedFeedbacks.getAverageRating().getAverage());

			answer.put(STAR_RATING, ugcAggregateStarRating);
		}

		return answer;
	}
}
