package com.about.mantle.model.extended.docv2.sc.blocks;

/**
 * The various types of groups that are supported by {@link AbstractStructuredContentGroup}
 */
// Tags work for simple cases.
// Special groups may be required for more complicated cases, e.g. gallery blocks.
public enum StructuredContentGroupType {
	OL,
	UL,
	LI,
	DIV,
	GALLERY(false);

	private boolean isTag;

	public boolean isTag() {
		return this.isTag;
	}

	private StructuredContentGroupType() {
		this(true);
	}

	private StructuredContentGroupType(boolean isTag) {
		this.isTag = isTag;
	}
}