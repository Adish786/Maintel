package com.about.mantle.utils.selene.document.api;

import com.about.mantle.utils.selene.api.common.SliceableList;
import com.about.mantle.utils.selene.document.api.listsc.CommerceInfo;
import com.about.mantle.utils.selene.image.api.Image;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.RandomUtils;

import java.util.UUID;

@Builder
@Getter
public class ContentBlock {
	@Builder.Default
	private int id = RandomUtils.nextInt(0, 1000);

	@Builder.Default
	private String uuid = UUID.randomUUID().toString();

	@Builder.Default
	private String type = "HTML";
	@Builder.Default
	private Data data = Data.builder().build();
	private String theme;

	@Getter @Builder
	public static class Data {
		private String title;
		private Long docId;
		private String uri;
		private String heading;
		private String text;
		private SliceableList<CommerceInfo> commerceInfo;
		@Builder.Default
		private String html = "<p>Few experiences capture the essence of summer as strongly as a day at the beach. When the hot, humid weather hits, the beach is the only place I want to be, lounging in the sand, cooling off at the shoreline, and relaxing under the sun. And given the perfection of a summer day at the beach, the only way it can be made better is by <a href=\"https://www.thespruce.com/how-to-throw-a-beach-party-4170086\" data-component=\"link\" data-source=\"inlineLink\" data-type=\"internalLink\" data-ordinal=\"1\">hosting a party</a> and inviting your favorite beach bum friends to enjoy the salty, sea air with you. Keep your party casual, transportable, and by all means as relaxed as a day at the beach should be.</p>";
		private String shortText;
		private String theme;
		private String toolId;
		private String stepType;
		private Boolean isLastStep;
		private String placeId;
		private String programmingTitle;
		private Boolean showTitle;
		private String downloadLink;
		private Boolean showLink;
		private String sailthruListId;
		private String content;
		private Image image;
		private Object columnAttributes;
		private Object data;
		private Double rating;
		private SCDocument SCDocument;
		private Boolean hideOnTOC;
		private String callToAction;
		private String productId;
		private String superlative;
		private String quote;
		private SliceableList<Products> products;
		private SliceableList<CallOuts> callOuts;
		private SliceableList<ContentBlock> contents;
		private String cheetahId;
		private String question;
		private String answer;
		private String caption;
		private ComparisonList listA;
		private ComparisonList listB;

		@Getter @Builder
		public static class Products {

			private String id;

			@Builder.Default
			private String type = "CREDITCARD";
		}

		@Getter @Builder
		public static class CallOuts {
			@Builder.Default
			private String title = "auto-title";

			@Builder.Default
			private Image image = Image.builder().build();

			@Builder.Default
			private String caption = "auto-caption";

			private String url;

			@Builder.Default
			private Long date = 1572287146000L;
		}

		@Getter @Builder
		public static class ComparisonList {
			private String heading;
			private SliceableList<String> items;
		}
	}

}
