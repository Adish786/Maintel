package com.about.mantle.utils.selene.document.api;

import com.about.mantle.utils.selene.image.api.Image;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Sponsor {

    @Builder.Default
    private Image sponsorImage = Image.builder().build();

    private String pixelTrackingUrl;

    private String description;

    private String url;

}
