package com.about.mantle.utils.selene.image.api;

import com.about.mantle.utils.selene.api.common.SliceableList;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Image {
	private String id;

	@Builder.Default
	private String objectId = "selene-automation-global-image-59f2233922fa3a001143e9f8.jpg";

	@Builder.Default
	private String url = "http://0.tqn.com/d/homesecurity/1/0/2/3/-/-/1100549sd23.jpg";

	@Builder.Default
	private String caption = "auto-caption";

	private String owner;

	@Builder.Default
	private String alt = "auto-alt.jpg";

	@Builder.Default
	private String width = "1407";

	@Builder.Default
	private String height = "2111";

	@Builder.Default
	private Boolean sensitive = null;

	@Builder.Default
	private Boolean original = true;

	@Builder.Default
	private String fileName = "auto-filename.jpg";
	private String authorId;
	private String user;
	private String owners;
	private SliceableList<String> tags;

	private String encodedThumbnail;
}
