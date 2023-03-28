package com.about.mantle.utils.selene.document.api;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
@Builder
@Getter
public class Dates {
	@Builder.Default
	private Long created = 1380188826000L;

	@Builder.Default
	private Long updated = 1400201326000L;

	@Builder.Default
	private Long firstPublished = 1380188839000L;

	@Builder.Default
	private Long lastPublished = 1400201400000L;

	@Builder.Default
	private Long scheduledStart = Instant.now().toEpochMilli();

	private Long firstPending;

	@Builder.Default
	private Long displayed = 1400201400000L;

}

