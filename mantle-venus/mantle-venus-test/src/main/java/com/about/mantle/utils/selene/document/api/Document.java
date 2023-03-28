package com.about.mantle.utils.selene.document.api;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Setter
public class Document {
	private Long docId;

	@Builder.Default
	private DocumentState documentState = DocumentState.builder().build();

	@Builder.Default
	private Dates dates = Dates.builder().build();

	@Builder.Default
	private String slug = "auto-test-slug";

	private String vertical;

	@Builder.Default
	private Sponsor sponsor = Sponsor.builder().build();

}
