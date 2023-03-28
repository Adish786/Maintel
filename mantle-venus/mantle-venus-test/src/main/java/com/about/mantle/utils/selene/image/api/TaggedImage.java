package com.about.mantle.utils.selene.image.api;

import com.about.mantle.utils.selene.api.common.SliceableList;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TaggedImage {
	@Builder.Default
	private Image image = Image.builder().build();

	@Builder.Default
	private SliceableList<String> tags = SliceableList.<String>builder()
			.totalSize(5)
			.list(ImmutableList
						  .of("SCHEMA","FACEBOOK","PINTEREST","PRIMARY","TWITTER","RECIRC"))
			.build();
}
