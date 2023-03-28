package com.about.mantle.venus.model.pages;

import com.about.mantle.venus.model.MntlPage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

public class MntlAffiliateLinkPage extends MntlPage {

    public static final String LINK_DATA_ATTRIBUTE = "data-affiliate-link-rewriter";

    private final Lazy<List<WebElementEx>> links;

    public MntlAffiliateLinkPage(WebDriverExtended driver) {
        super(driver);
        this.links = lazy(() -> findElements(By.xpath("//*[@" + LINK_DATA_ATTRIBUTE + "]")));
    }

    public List<WebElementEx> links() {
        return links.get();
    }

}
