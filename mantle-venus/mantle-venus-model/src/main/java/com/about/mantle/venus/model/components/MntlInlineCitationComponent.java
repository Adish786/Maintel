package com.about.mantle.venus.model.components;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.components.article.MntlDynamicTooltipComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector(".mntl-inline-citation")
public class MntlInlineCitationComponent extends MntlComponent {

	private final Lazy<MntlDynamicTooltipComponent> citationTooltip;

	public MntlInlineCitationComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);

		this.citationTooltip = lazy(() -> findComponent(MntlDynamicTooltipComponent.class));

	}

	public MntlDynamicTooltipComponent citationTooltip() {
		return citationTooltip.get();
	}

}