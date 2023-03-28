package com.about.mantle.utils.selene.document.api;

import com.about.mantle.utils.selene.api.common.SliceableList;
import com.about.mantle.utils.selene.image.api.Image;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Page {
	private SliceableList<ContentBlock> contents;
	private SliceableList<Image> images;
}
