package com.about.mantle.components.media;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.images.MntlInlineImage;
import com.about.mantle.venus.model.components.media.MntlLightBoxComponent;
import com.about.mantle.venus.model.pages.MntlScPage;
import com.about.venus.core.driver.WebElementEx;
import com.google.common.base.Predicate;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;

public abstract class MntlLightBoxTest<T extends MntlInlineImage> extends MntlVenusTest implements MntlCommonTestMethods {

    public abstract Class<T> getScListComp();
    public abstract int getListItemWithMultipleInlineImages();
    public abstract String lightBoxTestNoCarouselFunctionalityScListItemsUrl();
    public abstract String lightBoxTestCarouselFunctionalityUrl();
    public int imagesInCarousel;

    /**
     * The test validates lightbox components on the page. It verifies the image (displayed, src and alt attributes are not null or empty),
     * caption, close button is not empty (aria-label attribute is not null or empty). It also verifies previous and next arrows are not present,
     * the lightbox component contains expected values when open and closed; component not displayed after closing.
     */
    protected Consumer<MntlRunner> lightBoxTestNoCarouselFunctionality = runner -> {
        MntlScPage mntlScPage = new MntlScPage<>(runner.driver(), getScListComp());
        List<T> mntlInlineImagesComponents = (List<T>) mntlScPage
                .getComponents();
        MntlLightBoxComponent mntlLightBoxcomponent = mntlScPage.mntlLightBoxcomponent();
        collector.checkThat("There are no list items present on the page", mntlInlineImagesComponents.size(), greaterThan(0));

        // Looping through each component and validating lightbox image
        AtomicInteger lightboxCount = new AtomicInteger(1);
        for (T inlineImgComponent : mntlInlineImagesComponents) {
            inlineImgComponent.image().scrollIntoViewCentered();
            if (!mntlLightBoxcomponent.getElement().isInViewPort()) mntlLightBoxcomponent.getElement().scrollIntoViewCentered();
            runner.page().waitFor().aMoment();
            if (desktop(runner.driver())) {
                inlineImgComponent.image().getElement().mouseHover();
                runner.page().waitFor().aMoment();
                collector.checkThat("The cursor on lightbox image is not zoom-in", inlineImgComponent.image().cssValue("cursor"), is("zoom-in"));
            }
            runner.driver().waitFor(ExpectedConditions.visibilityOf(inlineImgComponent.image().getElement()), 10);
            inlineImgComponent.image().click();
            runner.driver().waitFor((Predicate<WebDriver>) webdriver -> mntlLightBoxcomponent.lightboxCaption().isDisplayed(), 10);
            collector.checkThat("Lightbox component #" + lightboxCount + " is not displayed", mntlLightBoxcomponent.displayed(), is(true));
            collector.checkThat("Lightbox image #" + lightboxCount + " is not displayed", mntlLightBoxcomponent.mntlLightboxImg().isDisplayed(), is(true));
            collector.checkThat("Lightbox image #" + lightboxCount + " src is null or empty", mntlLightBoxcomponent.mntlLightboxImg().src(), not(emptyOrNullString()));
            collector.checkThat("Lightbox image #" + lightboxCount + " alt is null or empty", mntlLightBoxcomponent.mntlLightboxImg().getAttribute("alt"), not(emptyOrNullString()));
            collector.checkThat("MntlLightbox div contains 'is-closed' class even when lightbox component #" + lightboxCount + " is open", mntlLightBoxcomponent.className().contains("is-closed"), is(false));
            collector.checkThat("Lightbox component #" + lightboxCount + "has incorrect caption", mntlLightBoxcomponent.lightboxCaption().text(), is(inlineImgComponent.caption().text()));
            collector.checkThat("Prev arrow is present on lightbox component #" + lightboxCount + " when it shouldn't", mntlLightBoxcomponent.lightboxPrevArrow().isDisplayed(), is(false));
            collector.checkThat("Next arrow is present on lightbox component #" + lightboxCount + " when it shouldn't", mntlLightBoxcomponent.lightboxNextArrow().isDisplayed(), is(false));
            collector.checkThat("Lightbox close button on lightbox component #" + lightboxCount + " is empty (aria-label attribute is null or empty",
                    mntlLightBoxcomponent.imageLightboxClose().getAttribute("aria-label"), not(emptyOrNullString()));
            mntlLightBoxcomponent.imageLightboxClose().jsClick();
            collector.checkThat("MntlLightbox div does not contain is-closed class when lightbox component #" + lightboxCount + " is closed", mntlLightBoxcomponent.className().contains("is-closed"), is(true));
            collector.checkThat("Lightbox component #" + lightboxCount + "is displayed after closing it", mntlLightBoxcomponent.displayed(), is(false));
            runner.driver().waitFor(1, TimeUnit.SECONDS);
            lightboxCount.getAndIncrement();
        }
    };

    /**
     * The test validates lightbox components on the page. It verifies the image (displayed, src and alt attributes are not null or empty);
     * caption; previous, next and close buttons are not empty (aria-label attribute is not null or empty). It also verifies previous and next arrows,
     * the lightbox component contains expected value when open.
     */
    protected Consumer<MntlRunner> testLightBoxCarouselFunctionality = runner -> {
        MntlScPage<T> mntlScPage = new MntlScPage<>(runner.driver(), getScListComp());
        List<T> mntlInlineImagesComponents = (List<T>) mntlScPage
                .getComponents();
        MntlLightBoxComponent mntlLightBoxcomponent = mntlScPage.mntlLightBoxcomponent();
        T listItemWithMultipleImages = mntlInlineImagesComponents.get(getListItemWithMultipleInlineImages());
        WebElementEx firstInlineImage = listItemWithMultipleImages.inlineImages().get(0);
        firstInlineImage.scrollIntoViewCentered();
        firstInlineImage.waitFor().aMoment();
        runner.driver().waitFor((Predicate<WebDriver>) webdriver -> firstInlineImage.isDisplayed(), 10);
        firstInlineImage.click();

        AtomicInteger lightboxCount = new AtomicInteger(1);
        for (int i = 0; i < imagesInCarousel; i++) {
            if (i == 0) {
                collector.checkThat("Prev arrow is present on lightbox component #" + lightboxCount + " when it shouldn't", mntlLightBoxcomponent.lightboxPrevArrow().isDisplayed(), is(false));
                collector.checkThat("Next arrow is not present on lightbox component #" + lightboxCount, mntlLightBoxcomponent.lightboxNextArrow().isDisplayed(), is(true));
                collector.checkThat("Next arrow button on lightbox component #" + lightboxCount + " is empty (aria-label attribute is null or empty",
                        mntlLightBoxcomponent.lightboxNextArrow().getAttribute("aria-label"), not(emptyOrNullString()));
            } else if (i != imagesInCarousel - 1) {
                collector.checkThat("Prev arrow is not present on lightbox component #" + lightboxCount, mntlLightBoxcomponent.lightboxPrevArrow().isDisplayed(), is(true));
                collector.checkThat("Prev arrow button on lightbox component #" + lightboxCount + " is empty (aria-label attribute is null or empty",
                        mntlLightBoxcomponent.lightboxPrevArrow().getAttribute("aria-label"), not(emptyOrNullString()));
                collector.checkThat("Next arrow is not present on lightbox component #" + lightboxCount, mntlLightBoxcomponent.lightboxNextArrow().isDisplayed(), is(true));
                collector.checkThat("Next arrow button on lightbox component #" + lightboxCount + " is empty (aria-label attribute is null or empty",
                        mntlLightBoxcomponent.lightboxNextArrow().getAttribute("aria-label"), not(emptyOrNullString()));
            } else {
                collector.checkThat("Next arrow is present on lightbox component #" + lightboxCount + " when it shouldn't", mntlLightBoxcomponent.lightboxNextArrow().isDisplayed(), is(false));
                collector.checkThat("Prev arrow is not present on lightbox component #" + lightboxCount, mntlLightBoxcomponent.lightboxPrevArrow().isDisplayed(), is(true));
                collector.checkThat("Prev arrow button on lightbox component #" + lightboxCount + " is empty (aria-label attribute is null or empty",
                        mntlLightBoxcomponent.lightboxPrevArrow().getAttribute("aria-label"), not(emptyOrNullString()));
                mntlLightBoxcomponent.leftKeyPress();
                runner.page().waitFor().aMoment();
                collector.checkThat("Cannot get to previous image by pressing a left key, prev arrow is not working as expected", mntlLightBoxcomponent.lightboxNextArrow().isDisplayed(), is(true));
                break;
            }
			collector.checkThat("Lightbox component #" + lightboxCount + " is not displayed", mntlLightBoxcomponent.displayed(), is(true));
			collector.checkThat("Lightbox image #" + lightboxCount + " is not displayed", mntlLightBoxcomponent.mntlLightboxImg().isDisplayed(), is(true));
			collector.checkThat("Lightbox image #" + lightboxCount + " src is null or empty", mntlLightBoxcomponent.mntlLightboxImg().src(), not(emptyOrNullString()));
			collector.checkThat("Lightbox image #" + lightboxCount + " alt is null or empty", mntlLightBoxcomponent.mntlLightboxImg().getAttribute("alt"), not(emptyOrNullString()));
			collector.checkThat("MntlLightbox div contains 'is-closed' class even when lightbox component #" + lightboxCount + " is open", mntlLightBoxcomponent.className().contains("is-closed"), is(false));
            collector.checkThat("Lightbox close button on lightbox component #" + lightboxCount + " is empty (aria-label attribute is null or empty",
                    mntlLightBoxcomponent.imageLightboxClose().getAttribute("aria-label"), not(emptyOrNullString()));
            if (i % 2 == 0) mntlLightBoxcomponent.rightKeyPress();
            else mntlLightBoxcomponent.lightboxNextArrow().click();
            runner.page().waitFor().aMoment();
            lightboxCount.getAndIncrement();
        }
    };

}
