package com.about.mantle.utils.selene.meta.api;

import com.about.mantle.utils.selene.api.common.SliceableList;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder(toBuilder=true)
@Getter
@Setter
public class Meta {
	@Builder.Default
	private String url = "";

	private Long docId;

	@Builder.Default
	private SliceableList<RecircKeyword> recircKeywords = SliceableList.<RecircKeyword>builder()
			.totalSize(2)
			.list(ImmutableList
						  .of(RecircKeyword.builder().term("test1").url("http://site.about.com/od/category/a/test1.htm").build(),
							  RecircKeyword.builder().term("test4").url("http://site.about.com/od/category/a/test2.htm").build()))
			.build();;

	@Builder.Default
	private List<Entity> entities = ImmutableList.of(Entity.builder().id("id1").name("entity1").score(0.12434357).build(),
													 Entity.builder().id("id2").name("entity2").score(0.1).build(),
													 Entity.builder().id("id3").name("entity3").score(0.0).build(),
													 Entity.builder().id("id4").name("entity4").build());

	@Builder.Default
	private Review review = Review.builder().build();

	@Builder @Getter
	public static class Entity {
		private String id;
		private String name;
		private Double score;
	}

	@Builder @Getter @Setter
	public static class Review {
		@Builder.Default
		private String authorId = "214340";
		@Builder.Default
		private Long userId = 153028347708638L;
		@Builder.Default
		private String type = "|recipe_approved|";
		@Builder.Default
		private Long lastReviewed = 1588231325000L;
	}

	@Builder @Getter
	public static class RecircKeyword {
		private String term;
		private String url;
	}
}
