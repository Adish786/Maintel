package com.about.mantle.utils.selene.meta.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReadRequest {
	private final String url;
	private final Long docId;
}
