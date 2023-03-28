package com.about.mantle.utils.selene.taxeneconfig.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WriteRequest {
	private Long docId;

	@Builder.Default
	private String configName = "config";

	private String config;
}
