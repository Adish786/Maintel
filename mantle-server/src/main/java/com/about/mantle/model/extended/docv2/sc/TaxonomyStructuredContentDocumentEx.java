package com.about.mantle.model.extended.docv2.sc;

import java.util.function.Function;
import java.util.stream.Stream;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class TaxonomyStructuredContentDocumentEx extends StructuredContentBaseDocumentEx {

	private static final long serialVersionUID = 1L;

	private SliceableListEx<AbstractStructuredContentContentEx<?>> intro = SliceableListEx.emptyList();
	private SliceableListEx<AbstractStructuredContentContentEx<?>> contents = SliceableListEx.emptyList();

	public TaxonomyStructuredContentDocumentEx() {
		super();
	}

	public TaxonomyStructuredContentDocumentEx(TaxonomyStructuredContentDocumentEx document) {
		super(document);
		this.intro = document.getIntro();
		this.contents = document.getContents();
	}

	public SliceableListEx<AbstractStructuredContentContentEx<?>> getIntro() {
		return intro;
	}

	public void setIntro(SliceableListEx<AbstractStructuredContentContentEx<?>> intro) {
		this.intro = intro;
	}

	public SliceableListEx<AbstractStructuredContentContentEx<?>> getContents() {
		return contents;
	}

	public void setContents(SliceableListEx<AbstractStructuredContentContentEx<?>> contents) {
		this.contents = contents;
	}

	/**
	 * Returns combined stream of {@link #intro} and {@link #contents}, in that
	 * order.
	 */
	@JsonIgnore
	@Override
	public Stream<AbstractStructuredContentContentEx<?>> getContentsStream() {

		return Stream.of(ensureSCStream.apply(intro),ensureSCStream.apply(contents)).flatMap(Function.identity())
				.filter(onNonNullSCData);
	}

	@Override
	public String toString() {
		return "TaxonomyStructuredContentDocumentEx{parent=" + super.toString() + "}";
	}

}
