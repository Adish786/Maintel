package com.about.mantle.components.image;

import com.about.mantle.venus.model.components.MntlScBlockComponent;
import com.about.mantle.venus.model.components.images.MntlInlineImage;
import com.about.mantle.venus.model.components.images.MntlPrimaryImageComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.greaterThan;

public class MntlImageDefaultsTest extends MntlImageTest {

    protected String primaryImageSrc = "1500x0";
    protected String primaryImageSrcSet = "750x0";
    protected String inlineImageSrc = "1500x0";
    protected String inlineImageSrcSet = "750x0";

    /**
     * The test is applicable for primary and inline images.
     * It verifies primary and inline images defaults (1500x0 for src and 750x0 for srcset).
     * If there are no primary and inline images found on the page, the test will fail.
     * */
    protected Consumer<Runner> imageDefaultsTest = runner -> {
        int testedImages = 0;

        if (runner.page().componentExists(MntlPrimaryImageComponent.class)) {
            MntlBasePage page = new MntlBasePage(runner.driver(), MntlPrimaryImageComponent.class);
            MntlPrimaryImageComponent primaryImage = (MntlPrimaryImageComponent) page.getComponent();
            primaryImage.scrollIntoView();
            runner.driver().waitFor(1, TimeUnit.SECONDS);
            verifyImageDefaults(primaryImage, "Primary", primaryImageSrc, primaryImageSrcSet);
            testedImages++;
        }

        if (runner.page().componentExists(MntlScBlockComponent.class)) {
            MntlBasePage page = new MntlBasePage(runner.driver(), MntlInlineImage.class);
            List<MntlInlineImage> inlineImages = (List<MntlInlineImage>) page.getComponents();
            readMore(runner);
            runner.driver().waitFor(1, TimeUnit.SECONDS);
            for (MntlInlineImage image : inlineImages) {
                image.scrollIntoView();
                runner.driver().waitFor(250, TimeUnit.MILLISECONDS);
                verifyImageDefaults(image.image(), "SC Block Inline", inlineImageSrc, inlineImageSrcSet);
                testedImages++;
            }
        }

        collector.checkThat("There were no primary or inline images to test on the page", testedImages, greaterThan(0));
    };
}
