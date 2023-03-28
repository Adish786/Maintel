package com.about.mantle.utils.selene.auth.api;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignedToken {
	private String signedToken;
	private Token token;
}
