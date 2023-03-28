package com.about.mantle.utils.selene.document.api.listsc;

import com.about.mantle.utils.selene.api.common.SliceableList;
import com.about.mantle.utils.selene.document.api.ContentBlock;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ListSCItem {
	@Builder.Default
	private SliceableList<ContentBlock> contents = SliceableList.<ContentBlock>builder()
			.list(ImmutableList.of(ContentBlock.builder().build(),
								   ContentBlock.builder()
											   .type("COMMERCE")
											   .data(ContentBlock.Data.builder()
																	  .callToAction("commerceDataCallToAction")
																	  .commerceInfo(SliceableList.<CommerceInfo>builder()
																							.list(ImmutableList.of(CommerceInfo.builder()
																															   .id("https://www.amazon.com/")
																															   .type("amazon")
																															   .build()))
																							.totalSize(1)
																							.build())
																	  .build())
											   .build()))
			.totalSize(2)
			.build();
	@Builder.Default
	private String quote =  "auto-quote";

}
