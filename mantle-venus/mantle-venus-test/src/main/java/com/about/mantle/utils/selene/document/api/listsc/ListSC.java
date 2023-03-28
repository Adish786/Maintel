package com.about.mantle.utils.selene.document.api.listsc;

import com.about.mantle.utils.selene.api.common.SliceableList;
import com.about.mantle.utils.selene.document.api.ContentBlock;
import com.about.mantle.utils.selene.document.api.Disclaimer;
import com.about.mantle.utils.selene.document.api.Document;
import com.about.mantle.utils.selene.document.api.SCDocument;
import com.about.mantle.utils.selene.image.api.TaggedImage;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class ListSC extends Document {
	@Builder.Default
	private String key = "3a51a5e5f4000086be0035f3";

	@Builder.Default
	private String authorKey = "41033";

	@Builder.Default
	private String templateType = "LISTSC";

	@Builder.Default
	private String lastEditingAuthorId = "41033";

	@Builder.Default
	private String lastEditingUserId = "157531902117689";

	@Builder.Default
	private String title = "auto-title";

	@Builder.Default
	private String socialTitle = "auto-social-title";

	@Builder.Default
	private String heading = "auto-heading";

	@Builder.Default
	private String description = "auto-description";

	@Builder.Default
	private String metaDescription = "auto-meta-description";

	private String url;
	private String rootUrl;

	private SliceableList<TaggedImage> taggedImages;

	@Builder.Default
	private String newsType = "news";

	@Builder.Default
	private String webPageSchemaType = "auto-schema";

	@Builder.Default
	private SliceableList<ListSCItem> items = SliceableList.<ListSCItem>builder()
			.list(ImmutableList.of(ListSCItem.builder().build(), ListSCItem.builder().build(), ListSCItem.builder().build(), ListSCItem.builder().build(), ListSCItem.builder().build(), ListSCItem.builder().build()))
			.totalSize(6)
			.build();

	private HiddenFields hiddenFields;

	private Disclaimer disclaimer;

	private String subheading;

	private ListOptions listOptions;
	private UserFeedback userFeedback;
	private SliceableList<ContentBlock> intro;
	private SliceableList<ContentBlock> outro;

	@Builder @Getter
	public static class HiddenFields {

		private SliceableList<ListSCItem> items;

		private SliceableList<SCDocument.Citation> citations;

	}

}
