package com.about.mantle.venus.model.components.images;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-universal-image")
public class MntlUniversalImageComponent extends MntlComponent{

	private Lazy<MntlImageComponent> image;

	public MntlUniversalImageComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.image = lazy(() -> findComponent(MntlImageComponent.class));
	}

	public MntlImageComponent image() {
		return image.get();
	}
}
