package com.about.mantle.venus.model.components.global;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("header")
public class MntlHeaderComponent extends MntlComponent {

	private final Lazy<MntlLogoComponent> logoComponent;

	public MntlHeaderComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.logoComponent = lazy(() -> findComponent(MntlLogoComponent.class));
	}

	public MntlLogoComponent logo() {
		return logoComponent.get();
	}

}
