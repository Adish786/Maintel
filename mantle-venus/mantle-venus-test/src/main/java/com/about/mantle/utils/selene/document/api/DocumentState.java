package com.about.mantle.utils.selene.document.api;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class DocumentState {
	@Builder.Default
	private String state = "ACTIVE";

	@Builder.Default
	private long activeDate = Instant.now().toEpochMilli() - 24600;
}
