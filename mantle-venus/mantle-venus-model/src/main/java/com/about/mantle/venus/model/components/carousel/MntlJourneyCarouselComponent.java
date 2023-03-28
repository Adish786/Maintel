package com.about.mantle.venus.model.components.carousel;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-journey-carousel")
public class MntlJourneyCarouselComponent extends MntlComponent {

    private Lazy<WebElementEx> headerCTA;
    private Lazy<WebElementEx> headerTitle;
    private Lazy<WebElementEx> carouselWrapper;
    private Lazy<List<CarouselItems>> journeyCarouselItems;
    private Lazy<WebElementEx> arrowLeft;
    private Lazy<WebElementEx> arrowRight;

    public MntlJourneyCarouselComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.headerCTA = lazy(() -> findElement(By.cssSelector(".mntl-journey-carousel__header-cta")));
        this.headerTitle = lazy(() -> findElement(By.cssSelector(".mntl-journey-carousel__header-title")));
        this.carouselWrapper = lazy(() -> findElement(By.cssSelector(".mntl-journey-carousel__wrapper")));
        this.arrowLeft = lazy(() -> findElement(By.cssSelector(".mntl-journey-carousel__arrow--left")));
        this.arrowRight = lazy(() -> findElement(By.cssSelector(".mntl-journey-carousel__arrow--right")));
        this.journeyCarouselItems = lazy(() -> findComponents(CarouselItems.class));
    }

    public WebElementEx headerCTA() { return headerCTA.get(); }

    public WebElementEx headerTitle() { return headerTitle.get(); }

    public WebElementEx carouselWrapper() { return carouselWrapper.get(); }

    public WebElementEx arrowLeft() { return arrowLeft.get(); }

    public WebElementEx arrowRight() { return arrowRight.get(); }

    public List<CarouselItems> journeyNavSublistItems() { return journeyCarouselItems.get(); }

    public Boolean hasRightArrow() { return this.getElement().elementExists(".mntl-journey-carousel__arrow--right.is-active"); }

    public Boolean hasLeftArrow() { return this.getElement().elementExists(".mntl-journey-carousel__arrow--left.is-active"); }

    @MntlComponentCssSelector(".mntl-journey-carousel__item")
    public static class CarouselItems extends MntlComponent {

        private Lazy<WebElementEx> journeyCarouselItemCard;
        private Lazy<WebElementEx> carouselCardContent;
        private Lazy<WebElementEx> carouselCardFooter;

        public CarouselItems(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.journeyCarouselItemCard = lazy(() -> findElement(By.cssSelector(".mntl-journey-carousel__item-card")));
            this.carouselCardContent = lazy(() -> findElement(By.cssSelector(".card__content")));
            this.carouselCardFooter = lazy(() -> findElement(By.cssSelector(".card__footer")));
        }

        public WebElementEx journeyCarouselItemCard() { return journeyCarouselItemCard.get(); }

        public WebElementEx carouselCardContent() { return carouselCardContent.get(); }

        public WebElementEx carouselCardFooter() { return carouselCardFooter.get(); }
    }
}
