package com.about.mantle.components.image;

import com.about.mantle.venus.model.components.images.MntlScBlockImageComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class MntlScBlockImagesTest extends MntlImageTest {

    /**
     * This test will verify lazy load image attributes before scrolling and after scrolling.
     * It will also check if the element is video and check its attributes.
     */
    protected Consumer<Runner> scBlockImagesTest = runner -> {
        MntlBasePage page = new MntlBasePage(runner.driver(), MntlScBlockImageComponent.class);
        List<MntlScBlockImageComponent> scBlockImagesComponent = (List<MntlScBlockImageComponent>) page.getComponents();
        readMore(runner);
        assertThat("There are no SC block images on the page", scBlockImagesComponent.size(), greaterThan(0));
        for (MntlScBlockImageComponent scBlockImagesComponentItem : scBlockImagesComponent) {
            scBlockImagesComponentItem.scrollIntoViewCentered();
            scBlockImagesComponentItem.waitFor().aMoment(1, TimeUnit.SECONDS);
            collector.checkThat("SC block image doesn't show as expected", scBlockImagesComponentItem.displayed(), is(true));
            if (scBlockImagesComponentItem.isVideo()) {
                verifyVideoAttributes(scBlockImagesComponentItem.video());
            } else if (scBlockImagesComponentItem.image().attributeValue("class").contains("lazyload")) {
                verifyLazyLoadImage(scBlockImagesComponentItem.image());
                collector.checkThat("noscript tag doesn't exist", scBlockImagesComponentItem.getElement().elementExists("noscript"), is(true));
            } else {
                verifyImageAttributes(scBlockImagesComponentItem.image());
            }
        }
    };
}