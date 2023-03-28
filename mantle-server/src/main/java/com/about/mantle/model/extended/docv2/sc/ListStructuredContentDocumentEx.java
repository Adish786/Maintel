package com.about.mantle.model.extended.docv2.sc;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import com.about.mantle.model.extended.docv2.ListDocumentEx.ListOptions;
import com.about.mantle.model.extended.docv2.ListDocumentEx.UserFeedback;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.liveblogpost.LiveBlogPost;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * List document having list of sc blocks in each of its item. {@link #intro}
 * and {@link #outro} fields are also list of sc blocks.
 *
 */
public class ListStructuredContentDocumentEx extends StructuredContentBaseDocumentEx {

	private static final long serialVersionUID = 1L;

	private UserFeedback userFeedback;
	private ListOptions listOptions;

	private LiveBlogPost liveBlogPost;
	private Boolean showAttributeRatings;

	private SliceableListEx<AbstractStructuredContentContentEx<?>> intro = SliceableListEx.emptyList();
	private SliceableListEx<StructuredContentItemEx> items = SliceableListEx.emptyList();
	private SliceableListEx<AbstractStructuredContentContentEx<?>> outro = SliceableListEx.emptyList();

	/**
	 * Returns combined Stream of contents of {@link #intro}, all list items and
	 * {@link #outro}, in that order.
	 */
	@JsonIgnore
	@Override
	public Stream<AbstractStructuredContentContentEx<?>> getContentsStream() {

		Stream<AbstractStructuredContentContentEx<?>> itemsContentsStream =
				Objects.nonNull(items) ? items.stream()
				.flatMap(item -> item.getContents().stream())
				: Stream.empty();

		return Stream.of(ensureSCStream.apply(intro), itemsContentsStream, ensureSCStream.apply(outro))
				.flatMap(Function.identity()) // Effectively concats all three streams
				.filter(onNonNullSCData);
	}

	/**
	 * Convenience method to get {@link #intro} filtered on type
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

	@JsonIgnore
	public Stream<AbstractStructuredContentContentEx<?>> getIntroContentsStreamOfTypes(String... types) {
		if (types == null) return Stream.empty();
		return intro.stream().filter(scb -> Arrays.stream(types).anyMatch(scb.getType()::equalsIgnoreCase));
	}

	@JsonIgnore
	public List<AbstractStructuredContentContentEx<?>> getOutroContentsListOfType(String type) {
		return getOutroContentsStreamOfType(type).collect(toList());
	}

	@JsonIgnore
	public Stream<AbstractStructuredContentContentEx<?>> getOutroContentsStreamOfType(String type) {
		return outro.stream().filter(scb -> scb.getType().equalsIgnoreCase(type));
	}

	@JsonIgnore
	public Stream<AbstractStructuredContentContentEx<?>> getOutroContentsStreamOfTypes(String... types) {
		if (types == null) return Stream.empty();
		return outro.stream().filter(scb -> Arrays.stream(types).anyMatch(scb.getType()::equalsIgnoreCase));
	}

	public SliceableListEx<StructuredContentItemEx> getItems() {
		return items;
	}

	public void setItems(SliceableListEx<StructuredContentItemEx> items) {
		this.items = items;
	}

	public SliceableListEx<AbstractStructuredContentContentEx<?>> getIntro() {
		return intro;
	}

	public void setIntro(SliceableListEx<AbstractStructuredContentContentEx<?>> intro) {
		this.intro = intro;
	}

	public SliceableListEx<AbstractStructuredContentContentEx<?>> getOutro() {
		return outro;
	}

	public void setOutro(SliceableListEx<AbstractStructuredContentContentEx<?>> outro) {
		this.outro = outro;
	}

	public UserFeedback getUserFeedback() {
		return userFeedback;
	}

	public void setUserFeedback(UserFeedback userFeedback) {
		this.userFeedback = userFeedback;
	}

	public ListOptions getListOptions() {
		return listOptions;
	}

	public void setListOptions(ListOptions listOptions) {
		this.listOptions = listOptions;
	}

	public LiveBlogPost getLiveBlogPost() {
		return liveBlogPost;
	}

	public void setLiveBlogPost(LiveBlogPost liveBlogPost) {
		this.liveBlogPost = liveBlogPost;
	}

	public Boolean getShowAttributeRatings() {
		return showAttributeRatings;
	}

	public void setShowAttributeRatings(Boolean showAttributeRatings) {
		this.showAttributeRatings = showAttributeRatings;
	}

	@Override
	public String toString() {
		return "ListStructuredContentDocumentEx{parent=" + super.toString() + "}";
	}

}
