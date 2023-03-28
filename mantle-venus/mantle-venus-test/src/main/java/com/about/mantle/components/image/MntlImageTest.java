package com.about.mantle.components.image;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.MntlComponent;

import java.util.Map;

import static org.hamcrest.CoreMatchers.*;

public class MntlImageTest extends MntlVenusTest implements MntlCommonTestMethods {
    /**
     * Verify src, srcset, sizes, width and height attributes are not null. alt attribute could be null sometimes.
     * @param image
     */
    public void verifyImageAttributes(MntlComponent image) {
        collector.checkThat(image.attributeValue("src") + " src attribute is null", image.attributeValue("src"), is(notNullValue()));
        if(image.getElement().getAttributes().containsKey("srcset")) {
            collector.checkThat(image.attributeValue("src") + " srcset attribute is null", image.attributeValue("srcset"), is(notNullValue()));
            collector.checkThat(image.attributeValue("src") + " sizes attribute is null", image.attributeValue("sizes"), is(notNullValue()));
        }
        collector.checkThat(image.attributeValue("src") + " width attribute is null", image.attributeValue("width"), is(notNullValue()));
        collector.checkThat(image.attributeValue("src") + " height attribute is null", image.attributeValue("height"), is(notNullValue()));
    }

    public void verifyVideoAttributes(MntlComponent video) {
        Map<String, String> videoAttributes = video.getElement().getAttributes();
        collector.checkThat("Gif video doesn't have autoplay attribute", videoAttributes.containsKey("autoplay"), is(true));
        collector.checkThat("Gif video doesn't have loop attribute", videoAttributes.containsKey("loop"), is(true));
        collector.checkThat("Gif video doesn't have playsinline attribute", videoAttributes.containsKey("playsinline"), is(true));
        collector.checkThat("Gif video doesn't have muted attribute", videoAttributes.containsKey("muted"), is(true));
        collector.checkThat( "Gif video src attribute is null", video.attributeValue("src"), is(notNullValue()));
        if(video.getElement().getAttributes().containsKey("data-srcset")) {
            collector.checkThat("Gif video data-srcset attribute is null", video.attributeValue("data-srcset"), is(notNullValue()));
            collector.checkThat("Gif video data-sizes attribute is null", video.attributeValue("data-sizes"), is(notNullValue()));
        } else {
            collector.checkThat("Gif video data-src attribute is null", video.attributeValue("data-src"), is(notNullValue()));
        }
        collector.checkThat("Gif video width attribute is null", video.attributeValue("width"), is(notNullValue()));
        collector.checkThat("Gif video height attribute is null", video.attributeValue("height"), is(notNullValue()));
    }

    /**
     * Verify data-srcset, data-sizes, data-src and data-src attribute are not null, class is lazyload.
     * @param image
     */
    public void verifyLazyLoadImage(MntlComponent image) {
        if(image.getElement().getAttributes().containsKey("data-srcset")) {
            collector.checkThat("Image data-srcset attribute is null", image.attributeValue("data-srcset"), is(notNullValue()));
            collector.checkThat("Image data-sizes attribute is null", image.attributeValue("data-sizes"), is(notNullValue()));
        }
        collector.checkThat("Image data-src attribute is null", image.attributeValue("data-src"), is(notNullValue()));
        collector.checkThat("Image class is not lazyload", image.attributeValue("class"), containsStringIgnoringCase("lazyload"));
        verifyImageAttributes(image);
    }

    /**
     * Verify image defaults (1500x0 for src and 750x0 for srcset)
     * */
    public void verifyImageDefaults(MntlComponent image, String imageType, String expectedSrc, String expectedSrcSet) {
        collector.checkThat(imageType + " image src does not have 1500x0 in the url", image.src(), containsString(expectedSrc));
        collector.checkThat(imageType + " image srcset does not have 750x0 in the url", image.attributeValue("srcset"), containsString(expectedSrcSet));
    }
}