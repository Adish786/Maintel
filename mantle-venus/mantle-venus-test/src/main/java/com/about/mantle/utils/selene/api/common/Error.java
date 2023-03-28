package com.about.mantle.utils.selene.api.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Error {
	private String code;
	private String message;
}
