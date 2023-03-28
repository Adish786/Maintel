package com.about.mantle.utils.selene.api.common;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Status {
	private String code;
	private List<FailedRules> failedRules;
	private List<Error> errors;
	private String message;
}
