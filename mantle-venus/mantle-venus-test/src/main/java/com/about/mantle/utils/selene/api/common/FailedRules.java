package com.about.mantle.utils.selene.api.common;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FailedRules {
	private Error error;
	private String message;
}
