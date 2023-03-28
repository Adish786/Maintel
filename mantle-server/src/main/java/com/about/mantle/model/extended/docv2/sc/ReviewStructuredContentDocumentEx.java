package com.about.mantle.model.extended.docv2.sc;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReviewStructuredContentDocumentEx extends StructuredContentDocumentEx {

	private static final long serialVersionUID = 1L;
	private ReviewRatingEx rating;
	private ReviewEntityEx entity;
	private SliceableListEx<AbstractStructuredContentContentEx<?>> intro = SliceableListEx.emptyList();

	public ReviewRatingEx getRating() {
		return rating;
	}

	public void setRating(ReviewRatingEx rating) {
		this.rating = rating;
	}

	public ReviewEntityEx getEntity() {
		return entity;
	}

	public void setEntity(ReviewEntityEx entity) {
		this.entity = entity;
	}

	public SliceableListEx<AbstractStructuredContentContentEx<?>> getIntro() {
		return intro;
	}

	public void setIntro(SliceableListEx<AbstractStructuredContentContentEx<?>> intro) {
		this.intro = SliceableListEx.emptyIfNull(intro);
	}

	/**
	 * Convenience method to get {@link #intro} filtered on type
	 * 
	 * @return
	 */
	@JsonIgnore
	public List<AbstractStructuredContentContentEx<?>> getIntroContentsListOfType(String type) {
		return getIntroContentsStreamOfType(type).collect(toList());
	}

	@JsonIgnore
	public Stream<AbstractStructuredContentContentEx<?>> getIntroContentsStreamOfType(String type) {
		return intro.stream().filter(scb -> scb.getType().equalsIgnoreCase(type));
	}

	/**
	 * Returns combined Stream of contents of {@link #intro} and all pages in that
	 * order.
	 */
	@JsonIgnore
	@Override
	public Stream<AbstractStructuredContentContentEx<?>> getContentsStream() {
		return Stream.of(ensureSCStream.apply(intro), super.getContentsStream())
				.flatMap(Function.identity())
				.filter(onNonNullSCData);
	}

}
