package com.about.mantle.model.extended.docv2.sc;

import java.util.stream.Stream;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This document type is intended to be used for presenting drugs and supplements content.
 *
 * One thing that sets this document apart from others is that its content is partially sourced
 * from a reference "database", e.g. IBM's DDIC. The association to the external data source is
 * provided by the {@link EntityReferenceInfo}.
 */
public class EntityReferenceDocumentEx extends StructuredContentBaseDocumentEx {

	private static final long serialVersionUID = 1L;

	private EntityReferenceInfo entityReferenceInfo;
	private SliceableListEx<AbstractStructuredContentContentEx<?>> contents;
	private String warning;

	public EntityReferenceInfo getEntityReferenceInfo() {
		return entityReferenceInfo;
	}

	public void setEntityReferenceInfo(EntityReferenceInfo entityReferenceInfo) {
		this.entityReferenceInfo = entityReferenceInfo;
	}

	public SliceableListEx<AbstractStructuredContentContentEx<?>> getContents() {
		return contents;
	}

	public void setContents(SliceableListEx<AbstractStructuredContentContentEx<?>> contents) {
		this.contents = contents;
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	@JsonIgnore
	@Override
	public Stream<AbstractStructuredContentContentEx<?>> getContentsStream() {
		return ensureSCStream.apply(contents).filter(onNonNullSCData);
	}

	@Override
	public String toString() {
		return "EntityReferenceDocumentEx{parent=" + super.toString() + "}";
	}

}
