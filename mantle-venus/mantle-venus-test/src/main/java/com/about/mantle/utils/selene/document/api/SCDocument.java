package com.about.mantle.utils.selene.document.api;

import com.about.mantle.utils.selene.api.common.Audit;
import com.about.mantle.utils.selene.api.common.SliceableList;
import com.about.mantle.utils.selene.image.api.Image;
import com.about.mantle.utils.selene.image.api.TaggedImage;
import com.about.mantle.utils.selene.meta.api.Meta;
import com.about.mantle.utils.selene.ugcrating.api.UGCRating;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class SCDocument extends Document {

	@Builder.Default
	private String key = "4777231_1580318066091";

	private String revisionKey;

	@Builder.Default
	private String authorKey = "34731";

	@Builder.Default
	private String ownerKey = "230233";

	@Builder.Default
	private String templateType = "STRUCTUREDCONTENT";

	@Builder.Default
	private String lastEditingAuthorId = "230233";

	@Builder.Default
	private String lastEditingUserId = "151378030080892";

	@Builder.Default
	private String title = "auto-test-title";

	@Builder.Default
	private String socialTitle = "auto-social-title";

	@Builder.Default
	private String heading = "auto-heading";

	@Builder.Default
	private String description = "auto-description";

	@Builder.Default
	private String promoDescription = "auto-promo-description";

	@Builder.Default
	private SliceableList<Page> pages = SliceableList.<Page>builder()
			.totalSize(4)
			.list(ImmutableList.of(Page.builder()
									   .contents(SliceableList.<ContentBlock>builder()
														 .list(ImmutableList.of(ContentBlock.builder().build()))
														 .totalSize(1)
														 .build())
									   .build(),
								   Page.builder()
									   .contents(SliceableList.<ContentBlock>builder()
														 .list(ImmutableList.of(ContentBlock.builder().build()))
														 .totalSize(1)
														 .build())
									   .build(),
								   Page.builder()
									   .contents(SliceableList.<ContentBlock>builder()
														 .list(ImmutableList.of(ContentBlock.builder().build()))
														 .totalSize(1)
														 .build())
									   .build(),
								   Page.builder()
									   .contents(SliceableList.<ContentBlock>builder()
														 .list(ImmutableList.of(ContentBlock.builder().build()))
														 .totalSize(1)
														 .build())
									   .build()))
			.build();

	private String metaDescription;
	private String url;
	private String rootUrl;
	private String uri;
	private String id;
	private Audit audit;
	private PrimaryVideo primaryVideo;
	private Image primaryImage;
	private String sailthruTemplate;
	private Boolean generated;
	private Boolean edited;
	private Image image;

	@Builder.Default
	private String viewType = "NEWS";

	@Builder.Default
	private String newsType = "news";

	private String target;
	private Boolean hasSummaryList;
	private Boolean hasTableOfContents;
	private Boolean medicalBoardApproved;
	private SliceableList<TaggedImage> taggedImages;
	private String programmingTitle;
	private String name;
	private Summary summary;
	private Source sources;
	private GuestAuthor guestAuthor;
	private SliceableList<Citation> citations;
	private SliceableList<Byline> bylines;
	private Category category;
	private RevenueGroup revenueGroup;
	private UGCRating ugcAggregateRatings;

	public enum RevenueGroup {
		COMMERCE,
		COMMERCENEWSDEALS,
		PERFORMANCEMARKETING,
		FINANCIALSERVICES,
		SPONSOR
	}

	@Builder @Getter
	public static class Category {
		private String socialLink;
	}
	private String methodology;

	@Builder.Default
	private Disclaimer disclaimer = Disclaimer.builder().affiliate(false).build();


	@Builder @Getter
	public static class Source {
		@Builder.Default
		private List<String> list = ImmutableList.of("some text",
													 "more text");
	}

	@Builder @Getter
	public static class Citation {
		private String source;
		private Integer refId;
	}

	@Builder @Getter
	public static class GuestAuthor {
		@Builder.Default
		private String shortBio = "auto-short-bio";
	}

	@Builder.Default
	private Meta metaData = Meta.builder().build();

	@Builder @Getter
	public static class Byline {
		private String id;
		private String type;
		private String authorType;
		private String authorId;
		private String lastModified;
		private String descriptor;
		private String attribution;
	}

}
