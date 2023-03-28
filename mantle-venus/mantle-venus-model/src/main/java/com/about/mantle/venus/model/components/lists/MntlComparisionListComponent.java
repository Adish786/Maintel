package com.about.mantle.venus.model.components.lists;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import java.util.List;

@MntlComponentCssSelector(".mntl-sc-block-comparisonlist")
public class MntlComparisionListComponent extends MntlComponent {

    private final Lazy<List<ComparisonListColumn>> comparisonListColumns;
    private final Lazy<WebElementEx> listOptionalHeading;
    private final Lazy<WebElementEx> listOptionalSummaryText;

    public MntlComparisionListComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.comparisonListColumns = lazy(() -> findElements(By.className("mntl-sc-block-comparisonlist__wrapper"), ComparisonListColumn::new));
        this.listOptionalHeading = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-comparisonlist__comparison-list-heading")));
        this.listOptionalSummaryText = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-comparisonlist__summary-text")));
    }

    public List<ComparisonListColumn> comparisonListColumns() {
        return comparisonListColumns.get();
    }

    public WebElementEx listOptionalHeading() {
        return listOptionalHeading.get();
    }

    public WebElementEx listOptionalSummaryText() {
        return listOptionalSummaryText.get();
    }

    public static class ComparisonListColumn extends Component {

        private final Lazy<WebElementEx> comparisonListHeading;
        private final Lazy<List<WebElementEx>> comparisonListItems;
        private final Lazy<WebElementEx> list;

        public ComparisonListColumn(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.comparisonListHeading = lazy(() -> findElement(By.className("mntl-sc-block-comparisonlist__heading")));
            this.comparisonListItems = lazy(() -> findElements(By.tagName("li")));
            this.list = lazy(() -> findElement(By.className("mntl-sc-block-comparisonlist__list")));
        }

        public WebElementEx comparisonListHeading() {
            return comparisonListHeading.get();
        }

        public List<WebElementEx> comparisonListItems() {
            return comparisonListItems.get();
        }

        public WebElementEx list() {
            return this.list.get();
        }

        public Dimension elementSize() {
            return getElement().getSize();
        }

        public Point elementLocation() {
            return getElement().getLocation();
        }
    }

}
