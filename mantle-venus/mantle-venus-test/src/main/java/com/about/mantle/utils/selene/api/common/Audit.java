package com.about.mantle.utils.selene.api.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Audit {
	private Long lastModified;
	private String clientId;
	private String userId;
}
