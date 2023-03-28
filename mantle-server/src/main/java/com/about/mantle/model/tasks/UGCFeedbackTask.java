package com.about.mantle.model.tasks;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.ugc.AggregatedFeedbacks;
import com.about.mantle.model.extended.ugc.AggregatedFeedbacksRequestContext;
import com.about.mantle.model.extended.ugc.Feedback;
import com.about.mantle.model.extended.ugc.PhotoGalleryItem;
import com.about.mantle.model.extended.ugc.PhotoGalleryRequestContext;
import com.about.mantle.model.extended.ugc.SortBy;
import com.about.mantle.model.extended.ugc.ThumbsSignal;
import com.about.mantle.model.extended.ugc.ThumbsSignalHistogramEntry;
import com.about.mantle.model.services.ugc.UGCFeedbackService;

//TODO: This class includes action task and template task both. Split them accordingly.
@Tasks
public class UGCFeedbackTask {
	
	private static final Logger logger = LoggerFactory.getLogger(UGCFeedbackTask.class);
	protected final UGCFeedbackService feedbackService;

	public UGCFeedbackTask(UGCFeedbackService feedbackService) {
		this.feedbackService = feedbackService;
	}

	/**
	 * Not for use as a task. Used by the feedback submission controller.
	 */
	public void submitFeedback(HttpServletRequest request,
			HttpServletResponse response, @RequestContextTaskParameter RequestContext requestContext) {
		String thumbSignalParam = requestContext.getParameterSingle("thumbsSignal");
		String commentParam = requestContext.getParameterSingle("comment");

		ThumbsSignal thumbsSignal = ThumbsSignal.valueOf(thumbSignalParam);
		String comment = commentParam == null ? null : commentParam.substring(0, Math.min(commentParam.length(), 1500)).trim();
		Long docId = Long.valueOf(requestContext.getParameters().get("docId")[0]);

		Feedback feedback = new Feedback();
		feedback.setUserId(requestContext.getUserId());
		feedback.setDocId(docId);
		feedback.setThumbsSignal(thumbsSignal);
		feedback.setReview(comment);

		feedbackService.postFeedback(feedback);
	}

	/**
	 * This method calls selene feedback service to get thumbsSignals of feedbacks and group them by their values
	 * @param document Selene Document
	 * @return A map of {Key: All {@link ThumbsSignal} values, Value: Count}
	 */
	@Task(name = "getFeedbackThumbsSignals")
	@TimedComponent(category = "task")
	public Map<String, Integer> getFeedbackThumbsSignals(@TaskParameter(name = "document") BaseDocumentEx document) {
		Map<String, Integer> answer = null;
		try {
			AggregatedFeedbacks aggregatedFeedbacks = getAggregatedFeedbacks(document.getDocumentId());
			answer = aggregatedFeedbacks.getThumbsSignalHistogram().stream().collect(
						Collectors.toMap(k -> k.getThumbsSignal().toString(), ThumbsSignalHistogramEntry::getCount));
		} catch (Exception e) {
			// Not a critical task, thus logging below error level and returning null
			logger.info("Failed to get thumbs signal.", e);
		}
		return answer;
	}

	/**
	 * Task for getting aggregated UGC metrics and the first N reviews for a document.
	 * Intended for use wherever this information is needed for page load, e.g. schema, star ratings, etc.
	 * Preferred for page load because it is associated with a single cache key.
	 * @param docId
	 * @return
	 */
	@Task(name = "aggregatedFeedbacks")
	@TimedComponent(category = "task")
	public AggregatedFeedbacks getAggregatedFeedbacks(@TaskParameter(name = "docId") Long docId) {
		return feedbackService.getAggregatedFeedbacks(
					feedbackService.getDefaultAggregatedFeedbacksRequestContextBuilder(docId).build());
	}

	/**
	 * Task for getting paged feedback from the services-ugc API.
	 * Useful for paging through N reviews at a time in response to user interaction.
	 * @param docId
	 * @param sort
	 * @param offset
	 * @param limit
	 * @return
	 */
	@Task(name = "feedbacks")
	@TimedComponent(category = "task")
	public SliceableListEx<Feedback> getFeedbacks(@TaskParameter(name = "docId") Long docId,
				@TaskParameter(name = "sort") SortBy sort, @TaskParameter(name = "offset") Integer offset,
				@TaskParameter(name = "limit") Integer limit) {
		SliceableListEx<Feedback> answer = null;

		AggregatedFeedbacksRequestContext reqCtx =
			feedbackService.getDefaultAggregatedFeedbacksRequestContextBuilder(docId)
				.sort(sort)
				.offset(offset)
				.limit(limit)
				.build();

		if (!isContainedInDefaultRequestForPageLoad(reqCtx)) {
			// outside the page-load/json-ld-schema range so need to reach out to the service
			AggregatedFeedbacks aggregatedFeedbacks = feedbackService.getAggregatedFeedbacks(reqCtx);
			answer = aggregatedFeedbacks.getFeedbacks();
		} else {
			// inside the page-load/json-ld-schema range
			// handle wasteful use of the task when the data was already provided for page load purposes
			// repackage the data using the hopefully already cached page load data
			AggregatedFeedbacks pageLoadFeedbacks = getAggregatedFeedbacks(docId);
			List<Feedback> partialFeedbacks = pageLoadFeedbacks.getFeedbacks().getList().stream()
							.skip(reqCtx.getOffset()).limit(reqCtx.getLimit()).collect(Collectors.toList());
			answer = new SliceableListEx<>();
			answer.setList(partialFeedbacks);
			answer.setTotalSize(pageLoadFeedbacks.getFeedbacks().getTotalSize());
		}

		return answer;
	}

	/**
	 * The hope is that this method will map arbitrary requests back to the default request if it's
	 * possible for efficiency reasons.
	 * @param reqCtx
	 * @return
	 */
	private boolean isContainedInDefaultRequestForPageLoad(AggregatedFeedbacksRequestContext reqCtx) {
		AggregatedFeedbacksRequestContext defaultReqCtx = feedbackService.getDefaultAggregatedFeedbacksRequestContextBuilder(
					reqCtx.getDocId()).build();
		// boxed types are guaranteed to be non-null thanks to the constructor
		return reqCtx.getSort().equals(defaultReqCtx.getSort()) &&
				reqCtx.getOffset() >= defaultReqCtx.getOffset() &&
				reqCtx.getOffset() + reqCtx.getLimit() <= defaultReqCtx.getLimit();
	}

	/**
	 * Returns up to the first 10 photos available
	 * @param docId
	 * @return
	 */
	@Task(name = "ugcPhotoGallery")
	@TimedComponent(category = "task")
	public SliceableListEx<PhotoGalleryItem> getUGCPhotoGallery(@TaskParameter(name = "docId") Long docId) {
		return getUGCPhotoGallery(docId, null, null);
	}

	@Task(name = "ugcPhotoGallery")
	@TimedComponent(category = "task")
	public SliceableListEx<PhotoGalleryItem> getUGCPhotoGallery(@TaskParameter(name = "docId") Long docId,
				@TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit) {
		return feedbackService.getPhotoGallery(
			new PhotoGalleryRequestContext.Builder()
				.docId(docId)
				.offset(offset)
				.limit(limit)
				.build());
	}

}
