package com.about.mantle.venus.model.components.sections.search;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.components.ads.MntlBillboard2Component;
import com.about.mantle.venus.model.components.ads.MntlBillboardComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

public class MntlSearchLayoutComponent extends MntlComponent {

	private Lazy<? extends MntlBillboardComponent> mntlBillboard;
	private Lazy<? extends MntlBillboard2Component> mntlBillboard2;

	public MntlSearchLayoutComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}

	public void billboardComponent(Class<? extends MntlBillboardComponent> billboardComponentClass) {
		this.mntlBillboard = lazy(() -> findComponent(billboardComponentClass));
	}

	public MntlBillboardComponent billboard() {
		return mntlBillboard.get();
	}

	public void billboard2Component(Class<? extends MntlBillboard2Component> billboard2ComponentClass) {
		this.mntlBillboard2 = lazy(() -> findComponent(billboard2ComponentClass));
	}

	public MntlBillboard2Component billboard2() {
		return mntlBillboard2.get();
	}

}
