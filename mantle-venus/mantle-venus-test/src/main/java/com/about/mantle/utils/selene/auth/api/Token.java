package com.about.mantle.utils.selene.auth.api;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Token {
	private String username;
	private String secret;
	private String resetPageUrl;
}
