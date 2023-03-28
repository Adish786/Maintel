package com.about.mantle.utils.selene.document.api.listsc;

import lombok.Builder;
import lombok.Getter;

import javax.ws.rs.core.Link;

@Getter
@Builder
public class UserFeedback {
	private Link link;
	private String annotation;
}
