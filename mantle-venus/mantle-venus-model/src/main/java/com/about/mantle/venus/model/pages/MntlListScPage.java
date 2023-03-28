package com.about.mantle.venus.model.pages;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.components.lists.MntlListScItemComponent;
import com.about.mantle.venus.model.components.lists.MntlProductBlockCommerceItemComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.utils.Lazy;

import java.util.List;

@SuppressWarnings("rawtypes")
public class MntlListScPage<T extends MntlComponent> extends MntlBasePage {
	
	private final Lazy <List<MntlListScItemComponent>> listScBlocks;
	private final Lazy<List<MntlProductBlockCommerceItemComponent>> productRecordBlocks;

	public MntlListScPage(WebDriverExtended driver, Class componentClass) {
		super(driver, componentClass);
		
		this.listScBlocks = lazy(() -> findComponents(MntlListScItemComponent.class));
		this.productRecordBlocks = lazy(() -> findComponents(MntlProductBlockCommerceItemComponent.class));
			}
	
	public List<MntlListScItemComponent> listScBlocks() {
		return listScBlocks.get();
	}

	public List<MntlProductBlockCommerceItemComponent> ProductRecordBlocks() {
		return productRecordBlocks.get();
	}
}
