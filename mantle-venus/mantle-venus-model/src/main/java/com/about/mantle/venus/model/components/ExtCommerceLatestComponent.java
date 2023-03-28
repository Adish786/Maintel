package com.about.mantle.venus.model.components;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".ext-commerce-latest")
public class ExtCommerceLatestComponent extends MntlComponent {


    private final Lazy<WebElementEx> latestHeading;
    private final Lazy<LatestList> latestComponent;

    public ExtCommerceLatestComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.latestHeading = lazy(() -> findElement(By.cssSelector(".commerce-latest__heading")));
        this.latestComponent = lazy(() -> findElement(By.cssSelector(".commerce-latest__list"), LatestList::new));

    }

    public String latestHeading(){
        return latestHeading.get().getText();
    }

    public boolean isLatestComponentExist(){
        return this.getElement().elementExists(".commerce-latest");
    }

    public LatestList latestList(){
        return latestComponent.get();
    }

    public static class LatestList extends Component {

        private final Lazy<List<WebElementEx>> listItems;
        private final Lazy<List<WebElementEx>> cardMedia;
        private final Lazy<List<WebElementEx>> cardMediaTag;
        private final Lazy<List<WebElementEx>> cardTitle;

        public LatestList(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.listItems = lazy(() -> findElements(By.tagName("a")));
            this.cardMedia = lazy(() -> findElements(By.cssSelector(".card__media img")));
            this.cardTitle = lazy(() -> findElements(By.cssSelector(".card__content .card__title-text")));
            this.cardMediaTag = lazy(() -> findElements(By.cssSelector(".card__media")));

        }

        public String cardTitle(int index){
            return cardTitle.get().get(index).getText();
        }

        public List<WebElementEx> listItems(){
            return listItems.get();
        }

        public List<WebElementEx> cardMedia(){
            return cardMedia.get();
        }

        public String cardMediaTagText(int tag){
            if(cardMediaTag.get().get(tag).getAttribute("data-tag") != null) {
                return cardMediaTag.get().get(tag).getAttribute("data-tag");
            } else{
                return null;
            }
        }
    }
}
