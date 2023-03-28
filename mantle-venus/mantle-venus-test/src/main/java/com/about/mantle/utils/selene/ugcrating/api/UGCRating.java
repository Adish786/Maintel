package com.about.mantle.utils.selene.ugcrating.api;

import com.about.mantle.utils.selene.api.common.Audit;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UGCRating {
	@Builder.Default
	private RatingScore starRating = RatingScore.builder().ratingScore(1).build();
	@Builder.Default
	private RatingScore madeIt = RatingScore.builder().ratingScore(1).build();

	private String userId;

	private Long docId;

	@Builder
	@Getter
	public static class RatingScore<T> {
		@Builder.Default
		private T ratingScore = null;

		private T average;

		private Integer count;

		private Audit audit;
	}
}
