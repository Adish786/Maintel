package com.about.mantle.utils.selene.document.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
/*
Selene Document delete request builder
 */
public class DocumentDeleteRequest {
	private final String url;
	private final Long docId;
	private final String state;
	private final Long activeDate;
	private final boolean synchSolr;
	private final boolean forceMode;
	private final boolean includeDocumentSummaries;
	private final boolean followDocumentSummaryRedirect;
	private final boolean useCache;
}
