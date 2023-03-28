package com.about.mantle.venus.model.components.structuredContent;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-sc-block-html")
public class MntlScBlockHtml extends MntlComponent {
    private final Lazy<List<WebElementEx>> pTags;
    private final Lazy<List<WebElementEx>> pTagsHtmlSlice;

    public MntlScBlockHtml(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.pTags = lazy(() -> findElements(By.tagName("p")));
        this.pTagsHtmlSlice = lazy(() -> findElements(By.cssSelector("p.html-slice")));
    }

    @Validate()
    public List<WebElementEx> pTags() {
        return pTags.get();
    }

    public List<WebElementEx> htmlSlice() {
        return pTagsHtmlSlice.get();
    }
}
