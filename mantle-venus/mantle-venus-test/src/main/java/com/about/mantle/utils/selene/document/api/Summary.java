package com.about.mantle.utils.selene.document.api;

import com.about.mantle.utils.selene.image.api.Image;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Summary {
	private String socialTitle;
	private String title;
	private String heading;
	private String description;
	private Image image;
}
