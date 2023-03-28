package com.about.mantle.venus.model.components.blocks.masonry;

import java.util.List;

import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.components.blocks.MntlCardComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-masonry-card-list")
public class MntlMasonryCardListComponent  extends MntlMasonryListComponent {

	private final Lazy<List<MntlCardComponent>> mntlCards;

	public MntlMasonryCardListComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.mntlCards = lazy(() -> items(MntlCardComponent.class));
	}

	public List<MntlCardComponent> cardList() {
		return mntlCards.get();
	}

}
