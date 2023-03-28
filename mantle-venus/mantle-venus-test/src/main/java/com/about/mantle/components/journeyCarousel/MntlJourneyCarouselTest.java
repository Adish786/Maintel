package com.about.mantle.components.journeyCarousel;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.carousel.MntlJourneyCarouselComponent;
import com.about.venus.core.driver.WebDriverExtended;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class MntlJourneyCarouselTest extends MntlVenusTest implements MntlCommonTestMethods {
    public void testJourneyCarousel(WebDriverExtended driver, MntlJourneyCarouselComponent carousel) {
        carousel.scrollIntoViewCentered();
        collector.checkThat("Journey Carousel component doesn't exist", carousel.displayed(), is(true));
        if(!desktop(driver)){
            collector.checkThat("Journey Carousel component scroll doesn't exist", carousel.getElement().hasClass("allow-free-scroll"), is(true));
        }
        else {
            if (carousel.hasRightArrow()){
            carousel.arrowRight().click();
            collector.checkThat("Journey Carousel component doesn't show left arrow to scroll.", carousel.arrowLeft().isDisplayed(), is(true)); }
            if (carousel.hasLeftArrow()) {
            carousel.arrowLeft().click();
            collector.checkThat("Journey Carousel component doesn't show right arrow to scroll.", carousel.hasRightArrow(), is(true)); }
        }

        collector.checkThat("Journey Carousel component header doesn't exist", carousel.headerCTA().isDisplayed(), is(true));
        collector.checkThat("Journey Carousel component header doesn't exist", carousel.headerCTA().text(), notNullValue());
        collector.checkThat("Journey Carousel component header doesn't exist", carousel.headerTitle().isDisplayed(), is(true));
        collector.checkThat("Journey Carousel component header doesn't exist", carousel.headerTitle().text(), notNullValue());
        collector.checkThat("Journey Carousel component wrapper doesn't exist",carousel.carouselWrapper().isDisplayed(), is(true));
        collector.checkThat("Journey Carousel component cards size should not be zero.", carousel.journeyNavSublistItems().size(), notNullValue());
        AtomicInteger itemCount = new AtomicInteger(1);
        for(MntlJourneyCarouselComponent.CarouselItems item : carousel.journeyNavSublistItems())
        {
            item.scrollIntoView();
            carousel.waitFor().exactMoment(1, TimeUnit.SECONDS);
            collector.checkThat("Carousel card item "+itemCount+" doesn't have href.", item.journeyCarouselItemCard().href(), notNullValue());
            itemCount.getAndIncrement();
        }
    }

    public Consumer<Runner> testJourneyCarousel = (runner) -> {
        WebDriverExtended driver = runner.driver();
        MntlJourneyCarouselComponent carousel = (MntlJourneyCarouselComponent) runner.component();
        testJourneyCarousel(driver, carousel);
    };
}

