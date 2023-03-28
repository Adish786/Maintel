package com.about.mantle.utils.selene.auth.api;

import lombok.Builder;
import lombok.Getter;

import java.util.Base64;

@Builder
@Getter
public class User {
	@Builder.Default
	private final String username = "customdata.qa@dotdash.com";

	@Builder.Default
	private final String secret = new String(Base64.getUrlDecoder().decode("QGJvdXRNYWMhMQ"));
}
