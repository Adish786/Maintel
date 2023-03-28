package com.about.mantle.venus.model.pages;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.components.article.MntlBylineTaglineItemComponent;
import com.about.mantle.venus.model.components.article.MntlUpdatedStampBylinesComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

public class BylinesAndTaglinesPage extends MntlBasePage {

    private Lazy<List<BylineTaglineGroup>> bylineGroups;
    private Lazy<List<BylineTaglineGroup>> taglineGroups;
    private Lazy<List<MntlUpdatedStampBylinesComponent>> updatedStamp;

    public BylinesAndTaglinesPage(WebDriverExtended driver, Class componentClass) {
        super(driver, componentClass);
        this.bylineGroups = lazy(() -> findElements(By.cssSelector(".mntl-bylines .mntl-bylines__group"), BylineTaglineGroup::new));
        this.taglineGroups = lazy(() -> findElements(By.cssSelector(".mntl-taglines .mntl-taglines__group"), BylineTaglineGroup::new));
        this.updatedStamp = lazy(() -> findComponents(MntlUpdatedStampBylinesComponent.class));
    }

    public List<BylineTaglineGroup> bylineGroups() { return bylineGroups.get(); }

    public List<BylineTaglineGroup> taglineGroups() { return taglineGroups.get(); }

    public List<MntlUpdatedStampBylinesComponent> updatedStamps() { return updatedStamp.get(); }

    public class BylineTaglineGroup extends MntlComponent {

        private Lazy<List<MntlBylineTaglineItemComponent>> bylineItem;
        private Lazy<List<MntlBylineTaglineItemComponent>> taglineItem;

        public BylineTaglineGroup(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.bylineItem = lazy(() -> findElements(By.cssSelector(".mntl-bylines__item:not(.mntl-attribution__item-name)"), MntlBylineTaglineItemComponent::new));
            this.taglineItem = lazy(() -> findElements(By.cssSelector(".mntl-taglines__item:not(.mntl-attribution__item-name)"), MntlBylineTaglineItemComponent::new));
        }

        public List<MntlBylineTaglineItemComponent> bylineItems() { return bylineItem.get(); }

        public List<MntlBylineTaglineItemComponent> taglineItems() { return taglineItem.get(); }

    }

}
