package com.about.mantle.venus.model.components.article;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

public class MntlBylineTaglineItemComponent extends MntlComponent {

    private final Lazy<WebElementEx> bylineItem;
    private final Lazy<WebElementEx> toolTipTrigger;
    private final Lazy<WebElementEx> bylinesItemName;
    private final Lazy<WebElementEx> bylinesItemLink;
    private final Lazy<WebElementEx> bylinesItemDescriptor;
    private Lazy<DataTooltip> dataTooltip;
    private final Lazy<WebElementEx> bylinesItemDate;
    private final Lazy<WebElementEx> bylinesItemIcon;
    private final Lazy<WebElementEx> bylinesAuthorImage;


    public MntlBylineTaglineItemComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.bylineItem = lazy(() -> findElement(By.cssSelector(".mntl-bylines__item")));
        this.toolTipTrigger = lazy(() -> findElement(By.cssSelector(".mntl-dynamic-tooltip--trigger")));
        this.bylinesItemName = lazy(() -> findElement(By.cssSelector(".mntl-attribution__item-name:not(a)")));
        this.bylinesItemLink = lazy(() -> findElement(By.cssSelector(".mntl-attribution__item-name")));
        this.bylinesItemDescriptor = lazy(() -> findElement(By.cssSelector(".mntl-attribution__item-descriptor")));
        this.dataTooltip = null;
        if (this.getElement().elementExists(".mntl-dynamic-tooltip--trigger")) {
            dataTooltip = lazy(() -> findComponent(DataTooltip.class));
        }
        this.bylinesItemDate = lazy(() -> findElement(By.cssSelector(".mntl-attribution__item-date")));
        this.bylinesItemIcon = lazy(() -> findElement(By.cssSelector(".mntl-attribution__item-icon")));
        this.bylinesAuthorImage = lazy(() -> findElement(By.tagName("img")));
    }

    public boolean doesDateExist() { return getElement().elementExists(".mntl-attribution__item-date"); }

    public WebElementEx bylinesItemDate() { return bylinesItemDate.get(); }

    public WebElementEx bylinesItemIcon() { return bylinesItemIcon.get(); }

    public WebElementEx bylinesItemName() { return bylinesItemName.get(); }

    public WebElementEx bylineItem() { return bylineItem.get(); }
    public WebElementEx toolTipTrigger() { return toolTipTrigger.get(); }

    public WebElementEx bylinesItemLink() { return bylinesItemLink.get(); }

    public WebElementEx bylinesItemDescriptor() { return bylinesItemDescriptor.get(); }
    
    public WebElementEx bylinesAuthorImage() { return bylinesAuthorImage.get(); }

    public boolean doesNameExist() { return getElement().elementExists(".mntl-attribution__item-name:not(a)"); }

    public DataTooltip dataTooltip() { return dataTooltip.get(); }

    public boolean hasDataTooltip() { return getElement().elementExists(".mntl-dynamic-tooltip--trigger"); }

    public boolean hasDescriptor() { return getElement().elementExists(".mntl-attribution__item-descriptor"); }

    public boolean hasAuthorImage() { return getElement().elementExists(".mntl-attribution__author-image"); }
    
    @MntlComponentCssSelector(".mntl-dynamic-tooltip--trigger")
    public static class DataTooltip extends MntlComponent {

        private final Lazy<MntlDynamicTooltipComponent> bylinesItemDinamicTooltip;
        private final Lazy<WebElementEx> bioLink;

        public DataTooltip(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.bylinesItemDinamicTooltip = lazy(() -> findComponent(MntlDynamicTooltipComponent.class));
            this.bioLink = lazy(() -> findElement(By.tagName("a")));
        }

        public MntlDynamicTooltipComponent bylinesItemDynamicDataTooltip() { return bylinesItemDinamicTooltip.get(); }

        public WebElementEx bioLink() { return bioLink.get(); }

    }

}

