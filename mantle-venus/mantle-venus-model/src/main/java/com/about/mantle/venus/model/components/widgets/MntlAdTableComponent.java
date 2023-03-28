package com.about.mantle.venus.model.components.widgets;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentId("mntl-ad-table")
public class MntlAdTableComponent extends MntlComponent {
    private Lazy<MtnlAdTableHeadingComponent> heading;
    private Lazy<MtnlAdTableContentComponent> content;
    private Lazy<MtnlAdTableDislosureComponent> disclosure;

    public MntlAdTableComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.heading = lazy(() -> findComponent(MtnlAdTableHeadingComponent.class));
        this.content = lazy(() -> findComponent(MtnlAdTableContentComponent.class));
        this.disclosure = lazy(() -> findComponent(MtnlAdTableDislosureComponent.class));
    }

    public MtnlAdTableHeadingComponent heading() {
        return heading.get();
    }

    public MtnlAdTableContentComponent content() {
        return content.get();
    }

    public MtnlAdTableDislosureComponent disclosure() {
        return disclosure.get();
    }

    @MntlComponentId("mntl-ad-table__heading")
    public static class MtnlAdTableHeadingComponent extends MntlComponent {
        public MtnlAdTableHeadingComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
        }
    }

    @MntlComponentId("mntl-ad-table__content")
    public static class MtnlAdTableContentComponent extends MntlComponent {
        private Lazy<List<MtnlAdTableHeadingComponent>> headings;
        private Lazy<List<MtnlAdTableColumnComponent>> columns;

        public MtnlAdTableContentComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.headings = lazy(() -> findElements(By.cssSelector(".mntl-ad-table__headers .mntl-text-block"), MtnlAdTableHeadingComponent::new));
            this.columns = lazy(() -> findComponents(MtnlAdTableColumnComponent.class));
        }

        public List<MtnlAdTableHeadingComponent> headings() {
            return headings.get();
        }

        public List<MtnlAdTableColumnComponent> columns() {
            return columns.get();
        }
    }

    @MntlComponentId("mntl-ad-table-column")
    public static class MtnlAdTableColumnComponent extends MntlComponent {
        private Lazy<WebElementEx> wrapper;

        public MtnlAdTableColumnComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.wrapper = lazy(() -> findElement(By.className("wrapper")));
        }

        public WebElementEx wrapper() {
            return wrapper.get();
        }
    }

    @MntlComponentId("mntl-ad-table__disclosure")
    public static class MtnlAdTableDislosureComponent extends MntlComponent {
        private Lazy<MtnlAdTableDisclosureButtonComponent> disclosureButton;
        private Lazy<MtnlAdTableDisclosureDescriptionComponent> description;
        private Lazy<MtnlAdTableDisclosureDescriptionCloseButtonComponent> descriptionCloseButton;

        public MtnlAdTableDislosureComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.disclosureButton = lazy(() -> findComponent(MtnlAdTableDisclosureButtonComponent.class));
            this.description = lazy(() -> findComponent(MtnlAdTableDisclosureDescriptionComponent.class));
            this.descriptionCloseButton = lazy(() -> findComponent(MtnlAdTableDisclosureDescriptionCloseButtonComponent.class));
        }

        public MtnlAdTableDisclosureButtonComponent disclosureButton() {
            return disclosureButton.get();
        }

        public MtnlAdTableDisclosureDescriptionComponent description() {
            return description.get();
        }

        public MtnlAdTableDisclosureDescriptionCloseButtonComponent descriptionCloseButton() {
            return descriptionCloseButton.get();
        }
    }

    @MntlComponentId("mntl-ad-table__disclosure-label")
    public static class MtnlAdTableDisclosureButtonComponent extends MntlComponent {
        public MtnlAdTableDisclosureButtonComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
        }
    }

    @MntlComponentId("mntl-ad-table__disclosure-description")
    public static class MtnlAdTableDisclosureDescriptionComponent extends MntlComponent {
        public MtnlAdTableDisclosureDescriptionComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
        }

        public String getDescriptionText() {
            return findElement(By.cssSelector(".mntl-text-block:not(.mntl-ad-table__disclosure-close)")).getText();
        }
    }

    @MntlComponentId("mntl-ad-table__disclosure-close")
    public static class MtnlAdTableDisclosureDescriptionCloseButtonComponent extends MntlComponent {
        public MtnlAdTableDisclosureDescriptionCloseButtonComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
        }
    }
}
