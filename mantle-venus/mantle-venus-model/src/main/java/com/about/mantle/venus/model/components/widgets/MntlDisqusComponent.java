package com.about.mantle.venus.model.components.widgets;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-disqus")
public class MntlDisqusComponent extends MntlComponent {
	
	private final Lazy<WebElementEx> disqusIframe;

	public MntlDisqusComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.disqusIframe = lazy(() -> findElement(By.cssSelector("#disqus_thread iframe")));
	}
	
	public WebElementEx disqusIframe() {
		return disqusIframe.get();
	}

}
