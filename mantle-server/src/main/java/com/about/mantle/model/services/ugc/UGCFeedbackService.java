package com.about.mantle.model.services.ugc;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.ugc.AggregatedFeedbacks;
import com.about.mantle.model.extended.ugc.AggregatedFeedbacksRequestContext;
import com.about.mantle.model.extended.ugc.Feedback;
import com.about.mantle.model.extended.ugc.PhotoGalleryItem;
import com.about.mantle.model.extended.ugc.PhotoGalleryRequestContext;

/**
 * This Service is for User Generated Content feedback, e.g. reviews, ratings, thumbs, etc.
 * Modeled after services-ugc service in Squadron.
 */
public interface UGCFeedbackService {

	/**
	 * Wholesale (over-)write potentially existing feedback.
	 * @param feedback
	 * @return
	 */
	Feedback postFeedback(Feedback feedback);

	/**
	 * Add/remove to a potentially existing feedback.
	 * @param feedback
	 * @param patchOperation
	 * @return
	 */
	Feedback patchFeedback(Feedback feedback, String patchOperation);

	/**
	 * Get an existing feedback.
	 * @param docId
	 * @param userId
	 * @return
	 */
	Feedback getFeedback(Long docId, String userId);

	/**
	 * Get a list of feedbacks with aggregated information for ratings, counts, etc.
	 *
	 * It is strongly advised that the provided request context be built using
	 * {@link #getDefaultAggregatedFeedbacksRequestContextBuilder(Long)} for automatic
	 * programming of the page-load defaults.
	 *
	 * @param reqCtx
	 * @return
	 */
	AggregatedFeedbacks getAggregatedFeedbacks(AggregatedFeedbacksRequestContext reqCtx);

	/**
	 * A pre-programmed Builder for fetching the feedbacks required on page load.
	 * @param docId
	 * @return
	 */
	AggregatedFeedbacksRequestContext.Builder getDefaultAggregatedFeedbacksRequestContextBuilder(Long docId);

	/**
	 * Get a list of user-submitted photos associated with the document/recipe.
	 * @param reqCtx
	 * @return
	 */
	SliceableListEx<PhotoGalleryItem> getPhotoGallery(PhotoGalleryRequestContext reqCtx);

}
