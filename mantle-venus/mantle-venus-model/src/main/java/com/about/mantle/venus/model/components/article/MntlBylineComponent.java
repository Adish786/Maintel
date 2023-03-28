package com.about.mantle.venus.model.components.article;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@Deprecated // Due to https://dotdash.atlassian.net/browse/AXIS-1766, will be replaced with `MntlBylinesComponent` once all the verticals are on the new `bylines` component.
@MntlComponentCssSelector(".mntl-byline")
public class MntlBylineComponent extends MntlComponent {
	
    private final Lazy<WebElementEx> byline;
    private final Lazy<WebElementEx> bioLink;
    private final Lazy<WebElementEx> bylineLink;

	public MntlBylineComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
        this.byline = lazy(() -> findElement(By.className("mntl-byline__name")));
        this.bioLink = lazy(() -> findElement(By.cssSelector(".mntl-byline__name a")));
        this.bylineLink = lazy(() -> findElement(By.cssSelector(".mntl-byline__link")));
	}
	
    public WebElementEx byline() {
        return this.byline.get();
    }

    public WebElementEx bioLink() {
        return this.bioLink.get();
    }
    
    public WebElementEx bylineLink() {
        return this.bylineLink.get();
    }

}
