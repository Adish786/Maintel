package com.about.mantle.utils.selene.document.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DocumentReadRequest {
	private final String url;
	private final Long docId;
	private final String state;
	private final Long activeDate;
	@Builder.Default
	private final Boolean useCache = false;
	@Builder.Default
	private final Boolean includeSummaries = false;
	@Builder.Default
	private final Boolean followRedirect = false;
	private final String projection;
}
