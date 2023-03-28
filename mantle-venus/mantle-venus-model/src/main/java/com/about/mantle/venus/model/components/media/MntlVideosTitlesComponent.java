package com.about.mantle.venus.model.components.media;

import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;
import java.util.List;

@MntlComponentCssSelector(".comp.article")
public class MntlVideosTitlesComponent extends MntlRightRailVideoComponent {
    private Lazy<List<WebElementEx>> inlineVideoTitles;
    public MntlVideosTitlesComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.inlineVideoTitles = lazy(()-> findElements(By.cssSelector(".mntl-sc-block-inlinevideo__title")));
    }
    public List<WebElementEx> inlineVideoTitles() { return this.inlineVideoTitles.get();}
}
