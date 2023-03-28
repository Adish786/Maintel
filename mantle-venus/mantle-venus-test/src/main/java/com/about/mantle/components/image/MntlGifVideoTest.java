package com.about.mantle.components.image;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.images.MntlGifVideoComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class MntlGifVideoTest extends MntlVenusTest implements MntlCommonTestMethods {

    public Consumer<Runner> gifVideoTest = runner -> {
        MntlBasePage page = new MntlBasePage(runner.driver(), MntlGifVideoComponent.class);
        List<MntlGifVideoComponent> gifVideos = page.getComponents();
        assertThat("GifVideos list size is null or empty", gifVideos.size(), greaterThan(0));
        for(MntlGifVideoComponent gif : gifVideos) {
            gif.scrollIntoViewCentered();
            collector.checkThat("gif doesn't show", gif.displayed(), is(true));
            gif.waitFor().aMoment(500, TimeUnit.MILLISECONDS);
            Map<String, String> gifAttributes = gif.getElement().getAttributes();
            collector.checkThat("Gif video doesn't have autoplay attribute", gifAttributes.containsKey("autoplay"), is(true));
            collector.checkThat("Gif video doesn't have loop attribute", gifAttributes.containsKey("loop"), is(true));
            collector.checkThat("Gif video doesn't have playsinline attribute", gifAttributes.containsKey("playsinline"), is(true));
            collector.checkThat("Gif video doesn't have muted attribute", gifAttributes.containsKey("muted"), is(true));
            collector.checkThat("Gif video width is null", gifAttributes.get("width"), is(notNullValue()));
            collector.checkThat("Gif video height is null", gifAttributes.get("height"), is(notNullValue()));
            if (gifAttributes.containsKey("data-sizes")) {
                collector.checkThat("Gif video data-sizes is null", gifAttributes.get("data-sizes"), is(notNullValue()));
                if (gifAttributes.containsKey("data-srcset-mp4")) {
                    collector.checkThat("Gif video data-srcset-mp4 is null", gifAttributes.get("data-srcset-mp4"), is(notNullValue()));
                }
                if (gifAttributes.containsKey("data-srcset-webm")) {
                    collector.checkThat("Gif video data-srcset-webm is null", gifAttributes.get("data-srcset-webm"), is(notNullValue()));
                }
            }
            collector.checkThat("mp4 source src is null", gifAttributes.get("src"), is(notNullValue()));
        }
    };
}
